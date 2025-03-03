package de.becke.vs.pattern.callback.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import de.becke.vs.pattern.callback.common.OperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * Ein Dienst, der asynchrone Operationen mit Message-basierten Callbacks implementiert.
 * Bei diesem Ansatz wird die asynchrone Operation gestartet und das Ergebnis über eine
 * Nachrichtenwarteschlange an den Client zurückgesendet.
 */
public class MessageBasedAsyncService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBasedAsyncService.class);
    
    // RabbitMQ-Verbindung und Kanal
    private final Connection connection;
    private final Channel channel;
    
    // Warteschlangen
    private final String requestQueue;
    private final String responseQueue;
    private final String callbackExchange;
    
    // Thread-Pool für asynchrone Ausführung
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    // JSON-Mapper für die Serialisierung/Deserialisierung
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Map für die Speicherung von CompletableFutures für Operationen
    private final Map<String, CompletableFuture<Object>> pendingRequests = new ConcurrentHashMap<>();
    
    /**
     * Erstellt einen neuen MessageBasedAsyncService mit den angegebenen Parametern.
     * 
     * @param host Der Hostname des RabbitMQ-Servers.
     * @param port Der Port des RabbitMQ-Servers.
     * @param username Der Benutzername für die Authentifizierung.
     * @param password Das Passwort für die Authentifizierung.
     * @param requestQueue Der Name der Anfragewarteschlange.
     * @param responseQueue Der Name der Antwortwarteschlange.
     * @param callbackExchange Der Name des Callback-Exchange.
     * @throws IOException Wenn ein I/O-Fehler auftritt.
     * @throws TimeoutException Wenn eine Zeitüberschreitung beim Verbindungsaufbau auftritt.
     */
    public MessageBasedAsyncService(String host, int port, String username, String password,
                                   String requestQueue, String responseQueue, String callbackExchange)
            throws IOException, TimeoutException {
        
        LOGGER.info("Initialisiere MessageBasedAsyncService");
        
        // RabbitMQ-Verbindungsfabrik erstellen
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        
        // Verbindung und Kanal erstellen
        connection = factory.newConnection();
        channel = connection.createChannel();
        
        // Warteschlangen und Exchange deklarieren
        this.requestQueue = requestQueue;
        this.responseQueue = responseQueue;
        this.callbackExchange = callbackExchange;
        
        channel.queueDeclare(requestQueue, true, false, false, null);
        channel.queueDeclare(responseQueue, true, false, false, null);
        channel.exchangeDeclare(callbackExchange, "direct", true);
        
        // Konsumenten für Antworten erstellen
        setupResponseConsumer();
        
        LOGGER.info("MessageBasedAsyncService erfolgreich initialisiert");
    }
    
    /**
     * Führt eine asynchrone Operation aus und gibt eine CompletableFuture zurück, die
     * das Ergebnis liefert, wenn es verfügbar ist.
     * 
     * @param input Die Eingabedaten für die Operation.
     * @param processor Die Funktion zur Verarbeitung der Daten.
     * @param callbackRoutingKey Der Routing-Key für den Callback.
     * @param <T> Der Typ der Eingabedaten.
     * @param <R> Der Typ des Ergebnisses.
     * @return Eine CompletableFuture, die das Ergebnis liefert.
     */
    public <T, R> CompletableFuture<R> executeAsync(T input, Function<T, R> processor, 
                                                  String callbackRoutingKey) {
        
        String correlationId = UUID.randomUUID().toString();
        LOGGER.info("Starte asynchrone Operation mit ID: {}", correlationId);
        
        // Erstelle eine CompletableFuture für diese Operation
        CompletableFuture<Object> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);
        
        // Führe die Operation asynchron aus
        executor.submit(() -> {
            try {
                LOGGER.info("Verarbeite Daten für Operation: {}", correlationId);
                R result = processor.apply(input);
                
                // Sende das Ergebnis über die Nachrichtenwarteschlange
                sendCallback(correlationId, callbackRoutingKey, result, null);
                
                LOGGER.info("Operation {} abgeschlossen", correlationId);
            } catch (Exception e) {
                LOGGER.error("Fehler bei der Ausführung von Operation {}: {}", 
                        correlationId, e.getMessage(), e);
                
                // Sende den Fehler über die Nachrichtenwarteschlange
                sendCallback(correlationId, callbackRoutingKey, null, e);
            }
        });
        
        // Typ-Konvertierung ist hier sicher, da wir wissen, dass das Ergebnis vom Typ R ist
        @SuppressWarnings("unchecked")
        CompletableFuture<R> typedFuture = (CompletableFuture<R>) future;
        return typedFuture;
    }
    
    /**
     * Richtet den Konsumenten für die Antwortwarteschlange ein.
     * 
     * @throws IOException Wenn ein I/O-Fehler auftritt.
     */
    private void setupResponseConsumer() throws IOException {
        LOGGER.info("Richte Konsumenten für Antwortwarteschlange ein: {}", responseQueue);
        
        // Callback für eingehende Nachrichten
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String correlationId = delivery.getProperties().getCorrelationId();
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            
            LOGGER.info("Antwort für Operation {} empfangen: {}", correlationId, message);
            
            try {
                // Nachricht deserialisieren
                @SuppressWarnings("unchecked")
                Map<String, Object> callbackData = objectMapper.readValue(message, Map.class);
                
                // CompletableFuture abrufen
                CompletableFuture<Object> future = pendingRequests.remove(correlationId);
                if (future != null) {
                    // Status überprüfen
                    String status = (String) callbackData.get("status");
                    if (OperationStatus.COMPLETED.name().equals(status)) {
                        // Erfolg
                        future.complete(callbackData.get("result"));
                    } else if (OperationStatus.FAILED.name().equals(status)) {
                        // Fehler
                        String errorMessage = (String) callbackData.get("error");
                        future.completeExceptionally(new RuntimeException(errorMessage));
                    } else {
                        // Unbekannter Status
                        future.completeExceptionally(
                                new IllegalStateException("Unbekannter Status: " + status));
                    }
                } else {
                    LOGGER.warn("Keine ausstehende Anfrage für correlationId: {}", correlationId);
                }
            } catch (Exception e) {
                LOGGER.error("Fehler beim Verarbeiten der Antwort: {}", e.getMessage(), e);
            }
        };
        
        // Konsumenten starten
        channel.basicConsume(responseQueue, true, deliverCallback, consumerTag -> {});
    }
    
    /**
     * Sendet einen Callback über den RabbitMQ-Exchange.
     * 
     * @param correlationId Die Korrelations-ID der Operation.
     * @param routingKey Der Routing-Key für den Callback.
     * @param result Das Ergebnis der Operation oder null, wenn ein Fehler aufgetreten ist.
     * @param error Der aufgetretene Fehler oder null, wenn die Operation erfolgreich war.
     */
    private void sendCallback(String correlationId, String routingKey, Object result, Throwable error) {
        try {
            LOGGER.info("Sende Callback für Operation {} mit Routing-Key: {}", 
                    correlationId, routingKey);
            
            // Erstelle eine Map mit den Callback-Daten
            Map<String, Object> callbackData = new HashMap<>();
            callbackData.put("correlationId", correlationId);
            
            if (error == null) {
                // Erfolg
                callbackData.put("status", OperationStatus.COMPLETED.name());
                callbackData.put("result", result);
            } else {
                // Fehler
                callbackData.put("status", OperationStatus.FAILED.name());
                callbackData.put("error", error.getMessage());
                callbackData.put("errorType", error.getClass().getName());
            }
            
            // Konvertiere die Daten in JSON
            String jsonData = objectMapper.writeValueAsString(callbackData);
            
            // Nachrichteneigenschaften erstellen
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .correlationId(correlationId)
                    .contentType("application/json")
                    .build();
            
            // Nachricht über den Exchange senden
            channel.basicPublish(callbackExchange, routingKey, properties, 
                    jsonData.getBytes(StandardCharsets.UTF_8));
            
            LOGGER.info("Callback für Operation {} erfolgreich gesendet", correlationId);
        } catch (Exception e) {
            LOGGER.error("Fehler beim Senden des Callbacks für Operation {}: {}", 
                    correlationId, e.getMessage(), e);
        }
    }
    
    /**
     * Stoppt den Dienst und gibt alle Ressourcen frei.
     */
    public void shutdown() {
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
        } catch (Exception e) {
            LOGGER.error("Fehler beim Herunterfahren des MessageBasedAsyncService: {}", 
                    e.getMessage(), e);
            executor.shutdownNow();
        }
    }
}