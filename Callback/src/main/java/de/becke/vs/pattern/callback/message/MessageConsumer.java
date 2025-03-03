package de.becke.vs.pattern.callback.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * Ein Konsument für Message-basierte Callbacks, der Nachrichten von einer
 * RabbitMQ-Warteschlange empfängt und verarbeitet.
 */
public class MessageConsumer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);
    
    // RabbitMQ-Verbindung und Kanal
    private final Connection connection;
    private final Channel channel;
    
    // Namen der Warteschlange und des Exchanges
    private final String queueName;
    private final String exchangeName;
    private final String routingKey;
    
    // JSON-Mapper für die Deserialisierung
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // Liste der empfangenen Callbacks
    private final List<Map<String, Object>> receivedCallbacks = new CopyOnWriteArrayList<>();
    
    // Listener für empfangene Callbacks
    private final List<Consumer<Map<String, Object>>> listeners = new ArrayList<>();
    
    /**
     * Erstellt einen neuen MessageConsumer mit den angegebenen Parametern.
     * 
     * @param host Der Hostname des RabbitMQ-Servers.
     * @param port Der Port des RabbitMQ-Servers.
     * @param username Der Benutzername für die Authentifizierung.
     * @param password Das Passwort für die Authentifizierung.
     * @param exchangeName Der Name des Exchanges.
     * @param queueName Der Name der Warteschlange.
     * @param routingKey Der Routing-Key für die Bindung.
     * @throws IOException Wenn ein I/O-Fehler auftritt.
     * @throws TimeoutException Wenn eine Zeitüberschreitung beim Verbindungsaufbau auftritt.
     */
    public MessageConsumer(String host, int port, String username, String password,
                          String exchangeName, String queueName, String routingKey)
            throws IOException, TimeoutException {
        
        LOGGER.info("Initialisiere MessageConsumer für Queue '{}' mit Routing-Key '{}'", 
                queueName, routingKey);
        
        // RabbitMQ-Verbindungsfabrik erstellen
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        
        // Verbindung und Kanal erstellen
        connection = factory.newConnection();
        channel = connection.createChannel();
        
        // Warteschlange, Exchange und Binding erstellen
        this.exchangeName = exchangeName;
        this.queueName = queueName;
        this.routingKey = routingKey;
        
        channel.exchangeDeclare(exchangeName, "direct", true);
        channel.queueDeclare(queueName, true, false, false, null);
        channel.queueBind(queueName, exchangeName, routingKey);
        
        // Konsumenten einrichten
        setupConsumer();
        
        LOGGER.info("MessageConsumer erfolgreich initialisiert");
    }
    
    /**
     * Richtet den Konsumenten für die Warteschlange ein.
     * 
     * @throws IOException Wenn ein I/O-Fehler auftritt.
     */
    private void setupConsumer() throws IOException {
        LOGGER.info("Richte Konsumenten für Warteschlange '{}' ein", queueName);
        
        // Callback für eingehende Nachrichten
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            
            LOGGER.info("Nachricht empfangen: {}", message);
            
            try {
                // Nachricht deserialisieren
                @SuppressWarnings("unchecked")
                Map<String, Object> callbackData = objectMapper.readValue(message, Map.class);
                
                // Füge die Nachricht zur Liste der empfangenen Callbacks hinzu
                receivedCallbacks.add(callbackData);
                
                // Benachrichtige alle Listener
                for (Consumer<Map<String, Object>> listener : listeners) {
                    try {
                        listener.accept(callbackData);
                    } catch (Exception e) {
                        LOGGER.error("Fehler beim Benachrichtigen eines Listeners: {}", 
                                e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Fehler beim Verarbeiten der Nachricht: {}", e.getMessage(), e);
            }
        };
        
        // Konsumenten starten
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {});
    }
    
    /**
     * Fügt einen Listener hinzu, der bei jedem empfangenen Callback aufgerufen wird.
     * 
     * @param listener Der hinzuzufügende Listener.
     */
    public void addCallbackListener(Consumer<Map<String, Object>> listener) {
        listeners.add(listener);
    }
    
    /**
     * Gibt alle bisher empfangenen Callbacks zurück.
     * 
     * @return Eine Liste der empfangenen Callbacks.
     */
    public List<Map<String, Object>> getReceivedCallbacks() {
        return new ArrayList<>(receivedCallbacks);
    }
    
    /**
     * Gibt den letzten empfangenen Callback zurück oder null, wenn noch kein Callback empfangen wurde.
     * 
     * @return Der letzte empfangene Callback oder null.
     */
    public Map<String, Object> getLastCallback() {
        return receivedCallbacks.isEmpty() ? null : receivedCallbacks.get(receivedCallbacks.size() - 1);
    }
    
    /**
     * Löscht alle bisher empfangenen Callbacks.
     */
    public void clearCallbacks() {
        receivedCallbacks.clear();
    }
    
    /**
     * Stoppt den Konsumenten und gibt alle Ressourcen frei.
     * 
     * @throws IOException Wenn ein I/O-Fehler auftritt.
     * @throws TimeoutException Wenn eine Zeitüberschreitung beim Schließen auftritt.
     */
    public void close() throws IOException, TimeoutException {
        LOGGER.info("Schließe MessageConsumer");
        
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }
}