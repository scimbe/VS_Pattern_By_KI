package de.becke.vs.pattern.callback.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import de.becke.vs.pattern.callback.common.OperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Ein Dienst, der asynchrone Operationen mit Message-basierten Callbacks implementiert.
 * Bei diesem Ansatz wird die asynchrone Operation gestartet und das Ergebnis über eine
 * Nachrichtenwarteschlange (z.B. RabbitMQ) an den Client zurückgesendet.
 */
public class MessageBasedAsyncService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBasedAsyncService.class);
    
    // Ein Thread-Pool für die asynchrone Ausführung
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    // RabbitMQ-Verbindung und -Kanal
    private final Connection connection;
    private final Channel channel;
    
    // Namen der Warteschlangen
    private final String requestQueueName;
    private final String responseQueueName;
    
    // JSON-Mapper für die Serialisierung von Objekten
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Speichert registrierte Callback-Handler
    private final Map<String, Consumer<OperationResult<?>>> callbackHandlers = new ConcurrentHashMap<>();
    
    /**
     * Erstellt einen neuen MessageBasedAsyncService mit Standardwarteschlangen.
     * 
     * @throws IOException Wenn ein I/O-Fehler auftritt.
     * @throws TimeoutException Wenn bei der Verbindungsherstellung ein Timeout auftritt.
     */
    public MessageBasedAsyncService() throws IOException, TimeoutException {
        this("async_requests", "async_responses");
    }
    
    /**
     * Erstellt einen neuen MessageBasedAsyncService mit den angegebenen Warteschlangen.
     * 
     * @param requestQueueName Der Name der Anfragewarteschlange.
     * @param responseQueueName Der Name der Antwortwarteschlange.
     * @throws IOException Wenn ein I/O-Fehler auftritt.
     * @throws TimeoutException Wenn bei der Verbindungsherstellung ein Timeout auftritt.
     */
    public MessageBasedAsyncService(String requestQueueName, String responseQueueName) 
            throws IOException, TimeoutException {
        
        LOGGER.info("Initialisiere MessageBasedAsyncService mit Warteschlangen {} und {}", 
                requestQueueName, responseQueueName);
        
        this.requestQueueName = requestQueueName;
        this.responseQueueName = responseQueueName;
        
        // Verbindung zu RabbitMQ herstellen
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        
        // Warteschlangen deklarieren
        channel.queueDeclare(requestQueueName, false, false, false, null);
        channel.queueDeclare(responseQueueName, false, false, false, null);
        
        // Consumer für die Antwortwarteschlange einrichten
        setupResponseConsumer();
        
        LOGGER.info("MessageBasedAsyncService initialisiert");
    }
    
    /**
     * Richtet einen Consumer für die Antwortwarteschlange ein.
     * 
     * @throws IOException Wenn ein I/O-Fehler auftritt.
     */
    private void setupResponseConsumer() throws IOException {
        LOGGER.info("Richte Consumer für Antwortwarteschlange {} ein", responseQueueName);
        
        channel.basicConsume(responseQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, 
                                      AMQP.BasicProperties properties, byte[] body) 
                    throws IOException {
                
                String correlationId = properties.getCorrelationId();
                LOGGER.info("Antwort für Operation {} empfangen", correlationId);
                
                try {
                    // Deserialisiere die Antwort
                    String message = new String(body, StandardCharsets.UTF_8);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> responseData = objectMapper.readValue(message, Map.class);
                    
                    // Erstelle ein OperationResult-Objekt
                    OperationResult<?> result = createOperationResult(responseData);
                    
                    // Rufe den registrierten Callback-Handler auf
                    Consumer<OperationResult<?>> handler = callbackHandlers.remove(correlationId);
                    if (handler != null) {
                        LOGGER.info("Rufe Callback-Handler für Operation {} auf", correlationId);
                        handler.accept(result);
                    } else {
                        LOGGER.warn("Kein Callback-Handler für Operation {} registriert", correlationId);
                    }
                } catch (Exception e) {
                    LOGGER.error("Fehler beim Verarbeiten der Antwort für Operation {}: {}", 
                            correlationId, e.getMessage(), e);
                }
            }
        });
    }
    
    /**
     * Erstellt ein OperationResult-Objekt aus den empfangenen Antwortdaten.
     * 
     * @param responseData Die empfangenen Antwortdaten.
     * @return Ein OperationResult-Objekt.
     */
    @SuppressWarnings("unchecked")
    private OperationResult<?> createOperationResult(Map<String, Object> responseData) {
        String operationId = (String) responseData.get("operationId");
        String statusStr = (String) responseData.get("status");
        OperationStatus status = OperationStatus.valueOf(statusStr);
        
        if (status == OperationStatus.COMPLETED) {
            Object result = responseData.get("result");
            return new OperationResult<>(operationId, status, result, null);
        } else if (status == OperationStatus.FAILED) {
            String errorMessage = (String) responseData.get("error");
            String errorType = (String) responseData.get("errorType");
            Exception error = new Exception(errorMessage); // Vereinfachte Fehlerbehandlung
            return new OperationResult<>(operationId, status, null, error);
        } else {
            return new OperationResult<>(operationId, status, null, null);
        }
    }
    
    /**
     * Sendet eine asynchrone Anfrage und registriert einen Callback-Handler für die Antwort.
     * 
     * @param input Die Eingabedaten für die Operation.
     * @param operationType Der Typ der auszuführenden Operation.
     * @param callbackHandler Der Handler, der bei Erhalt der Antwort aufgerufen werden soll.
     * @param <T> Der Typ der Eingabedaten.
     * @param <R> Der Typ des Ergebnisses.
     * @return Eine ID zur Identifizierung der Operation.
     * @throws IOException Wenn ein I/O-Fehler auftritt.
     */
    public <T, R> String sendRequest(T input, String operationType, 
                                   Consumer<OperationResult<R>> callbackHandler) 
            throws IOException {
        
        String operationId = UUID.randomUUID().toString();
        LOGGER.info("Sende Anfrage für Operation {} vom Typ {}", operationId, operationType);
        
        // Erstelle die Anfragedaten
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("operationId", operationId);
        requestData.put("operationType", operationType);
        requestData.put("input", input);
        
        // Serialisiere die Anfragedaten
        String message = objectMapper.writeValueAsString(requestData);
        
        // Eigenschaften für die Nachricht erstellen
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(operationId)
                .replyTo(responseQueueName)
                .build();
        
        // Registriere den Callback-Handler
        @SuppressWarnings("unchecked")
        Consumer<OperationResult<?>> genericHandler = (Consumer<OperationResult<?>>) (Consumer<?>) callbackHandler;
        callbackHandlers.put(operationId, genericHandler);
        
        // Sende die Nachricht
        channel.basicPublish("", requestQueueName, props, message.getBytes(StandardCharsets.UTF_8));
        LOGGER.info("Anfrage für Operation {} gesendet", operationId);
        
        return operationId;
    }
    
    /**
     * Startet einen Worker-Prozess, der asynchrone Anfragen verarbeitet.
     * 
     * @param operationProcessors Eine Map mit Prozessoren für verschiedene Operationstypen.
     * @throws IOException Wenn ein I/O-Fehler auftritt.
     */
    public void startWorker(Map<String, Function<Object, Object>> operationProcessors) 
            throws IOException {
        
        LOGGER.info("Starte Worker-Prozess für Warteschlange {}", requestQueueName);
        
        channel.basicConsume(requestQueueName, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, 
                                      AMQP.BasicProperties properties, byte[] body) 
                    throws IOException {
                
                String correlationId = properties.getCorrelationId();
                String replyTo = properties.getReplyTo();
                
                LOGGER.info("Anfrage für Operation {} empfangen", correlationId);
                
                executor.submit(() -> {
                    try {
                        // Deserialisiere die Anfrage
                        String message = new String(body, StandardCharsets.UTF_8);
                        @SuppressWarnings("unchecked")
                        Map<String, Object> requestData = objectMapper.readValue(message, Map.class);
                        
                        String operationId = (String) requestData.get("operationId");
                        String operationType = (String) requestData.get("operationType");
                        Object input = requestData.get("input");
                        
                        // Finde den passenden Prozessor
                        Function<Object, Object> processor = operationProcessors.get(operationType);
                        if (processor == null) {
                            LOGGER.error("Kein Prozessor für Operationstyp {} gefunden", operationType);
                            sendErrorResponse(operationId, replyTo, 
                                    "Unbekannter Operationstyp: " + operationType);
                            return;
                        }
                        
                        LOGGER.info("Verarbeite Operation {} vom Typ {}", operationId, operationType);
                        
                        try {
                            // Führe die Operation aus
                            Object result = processor.apply(input);
                            
                            // Sende die Antwort
                            sendSuccessResponse(operationId, replyTo, result);
                            
                        } catch (Exception e) {
                            LOGGER.error("Fehler bei der Ausführung von Operation {}: {}", 
                                    operationId, e.getMessage(), e);
                            
                            // Sende die Fehlerantwort
                            sendErrorResponse(operationId, replyTo, e.getMessage());
                        }
                        
                        // Bestätige die Verarbeitung der Nachricht
                        channel.basicAck(envelope.getDeliveryTag(), false);
                        
                    } catch (Exception e) {
                        LOGGER.error("Fehler beim Verarbeiten der Anfrage: {}", e.getMessage(), e);
                        try {
                            // Negativ-Quittung für die Nachricht
                            channel.basicNack(envelope.getDeliveryTag(), false, true);
                        } catch (IOException ex) {
                            LOGGER.error("Fehler beim Senden der Negativ-Quittung: {}", 
                                    ex.getMessage(), ex);
                        }
                    }
                });
            }
        });
    }
    
    /**
     * Sendet eine Erfolgsantwort an die angegebene Antwort-Warteschlange.
     * 
     * @param operationId Die ID der Operation.
     * @param replyTo Die Antwort-Warteschlange.
     * @param result Das Ergebnis der Operation.
     * @throws IOException Wenn ein I/O-Fehler auftritt.
     */
    private void sendSuccessResponse(String operationId, String replyTo, Object result) 
            throws IOException {
        
        LOGGER.info("Sende Erfolgsantwort für Operation {} an Warteschlange {}", 
                operationId, replyTo);
        
        // Erstelle die Antwortdaten
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("operationId", operationId);
        responseData.put("status", OperationStatus.COMPLETED.name());
        responseData.put("result", result);
        
        // Serialisiere die Antwortdaten
        String message = objectMapper.writeValueAsString(responseData);
        
        // Eigenschaften für die Nachricht erstellen
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(operationId)
                .build();
        
        // Sende die Nachricht
        channel.basicPublish("", replyTo, props, message.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Sendet eine Fehlerantwort an die angegebene Antwort-Warteschlange.
     * 
     * @param operationId Die ID der Operation.
     * @param replyTo Die Antwort-Warteschlange.
     * @param errorMessage Die Fehlermeldung.
     * @throws IOException Wenn ein I/O-Fehler auftritt.
     */
    private void sendErrorResponse(String operationId, String replyTo, String errorMessage) 
            throws IOException {
        
        LOGGER.info("Sende Fehlerantwort für Operation {} an Warteschlange {}: {}", 
                operationId, replyTo, errorMessage);
        
        // Erstelle die Antwortdaten
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("operationId", operationId);
        responseData.put("status", OperationStatus.FAILED.name());
        responseData.put("error", errorMessage);
        responseData.put("errorType", "java.lang.Exception");
        
        // Serialisiere die Antwortdaten
        String message = objectMapper.writeValueAsString(responseData);
        
        // Eigenschaften für die Nachricht erstellen
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                .correlationId(operationId)
                .build();
        
        // Sende die Nachricht
        channel.basicPublish("", replyTo, props, message.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Stoppt den Dienst und gibt alle Ressourcen frei.
     */
    public void shutdown() {
        LOGGER.info("Stoppe MessageBasedAsyncService");
        
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (InterruptedException | IOException | TimeoutException e) {
            LOGGER.error("Fehler beim Herunterfahren: {}", e.getMessage(), e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}