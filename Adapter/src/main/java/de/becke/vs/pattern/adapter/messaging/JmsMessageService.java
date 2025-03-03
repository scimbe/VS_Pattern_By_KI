package de.becke.vs.pattern.adapter.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Eine simulierte JMS-Implementierung (Java Message Service).
 * Dies ist das ältere System, das adaptiert werden soll.
 */
public class JmsMessageService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsMessageService.class);
    
    // Nachrichtenspeicher, simuliert eine JMS-Queue, organisiert nach Empfängern
    private final Map<String, List<JmsMessage>> messageStore = new HashMap<>();
    
    /**
     * Erstellt einen neuen JMS-Message-Service.
     */
    public JmsMessageService() {
        LOGGER.info("JMS Message Service initialisiert");
    }
    
    /**
     * Sendet eine JMS-Nachricht.
     * 
     * @param sender Der Absender
     * @param destination Der Zielort (Empfänger oder Queue)
     * @param messageText Der Nachrichtentext
     * @param properties Zusätzliche JMS-Eigenschaften
     * @return Die ID der gesendeten Nachricht
     */
    public String sendJmsMessage(String sender, String destination, String messageText, Map<String, Object> properties) {
        LOGGER.info("JMS: Sende Nachricht von {} an {}", sender, destination);
        
        // Erstelle eine neue JMS-Nachricht
        String messageId = "JMS-" + UUID.randomUUID().toString();
        JmsMessage message = new JmsMessage(messageId, sender, destination, messageText, properties);
        
        // Füge die Nachricht zur Queue hinzu
        messageStore.computeIfAbsent(destination, k -> new ArrayList<>()).add(message);
        
        return messageId;
    }
    
    /**
     * Empfängt JMS-Nachrichten für einen bestimmten Empfänger oder aus einer Queue.
     * 
     * @param destination Der Zielort (Empfänger oder Queue)
     * @param acknowledgeMode Der Bestätigungsmodus (AUTO_ACKNOWLEDGE, CLIENT_ACKNOWLEDGE, DUPS_OK_ACKNOWLEDGE)
     * @return Die empfangenen Nachrichten
     */
    public JmsMessage[] receiveJmsMessages(String destination, int acknowledgeMode) {
        LOGGER.info("JMS: Empfange Nachrichten für {}", destination);
        
        List<JmsMessage> messages = messageStore.getOrDefault(destination, new ArrayList<>());
        
        // Bei AUTO_ACKNOWLEDGE oder CLIENT_ACKNOWLEDGE, Nachrichten aus der Queue entfernen
        if (acknowledgeMode == JmsAcknowledgeMode.AUTO_ACKNOWLEDGE) {
            LOGGER.info("JMS: Entferne {} Nachrichten aus der Queue (AUTO_ACKNOWLEDGE)", messages.size());
            messageStore.remove(destination);
        }
        
        return messages.toArray(new JmsMessage[0]);
    }
    
    /**
     * Bestätigt den Empfang einer JMS-Nachricht (nur für CLIENT_ACKNOWLEDGE).
     * 
     * @param messageId Die ID der zu bestätigenden Nachricht
     */
    public void acknowledgeMessage(String messageId) {
        LOGGER.info("JMS: Bestätige Empfang der Nachricht {}", messageId);
        
        // Suche die Nachricht in allen Queues
        for (Map.Entry<String, List<JmsMessage>> entry : messageStore.entrySet()) {
            List<JmsMessage> messages = entry.getValue();
            
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).getMessageId().equals(messageId)) {
                    LOGGER.info("JMS: Entferne bestätigte Nachricht {} aus Queue {}", messageId, entry.getKey());
                    messages.remove(i);
                    return;
                }
            }
        }
        
        LOGGER.warn("JMS: Nachricht {} nicht gefunden", messageId);
    }
    
    /**
     * Innere Klasse, die eine JMS-Acknowledge-Modi definiert.
     */
    public static class JmsAcknowledgeMode {
        public static final int AUTO_ACKNOWLEDGE = 1;
        public static final int CLIENT_ACKNOWLEDGE = 2;
        public static final int DUPS_OK_ACKNOWLEDGE = 3;
    }
    
    /**
     * Innere Klasse, die eine JMS-Nachricht repräsentiert.
     */
    public static class JmsMessage {
        
        private final String messageId;
        private final String sender;
        private final String destination;
        private final String messageText;
        private final long timestamp;
        private final Map<String, Object> properties;
        
        /**
         * Erstellt eine neue JMS-Nachricht.
         * 
         * @param messageId Die Nachrichten-ID
         * @param sender Der Absender
         * @param destination Der Zielort
         * @param messageText Der Nachrichtentext
         * @param properties Zusätzliche Eigenschaften
         */
        public JmsMessage(String messageId, String sender, String destination, String messageText, Map<String, Object> properties) {
            this.messageId = messageId;
            this.sender = sender;
            this.destination = destination;
            this.messageText = messageText;
            this.timestamp = System.currentTimeMillis();
            this.properties = properties != null ? new HashMap<>(properties) : new HashMap<>();
        }
        
        /**
         * Gibt die Nachrichten-ID zurück.
         * 
         * @return Die Nachrichten-ID
         */
        public String getMessageId() {
            return messageId;
        }
        
        /**
         * Gibt den Absender zurück.
         * 
         * @return Der Absender
         */
        public String getSender() {
            return sender;
        }
        
        /**
         * Gibt den Zielort zurück.
         * 
         * @return Der Zielort
         */
        public String getDestination() {
            return destination;
        }
        
        /**
         * Gibt den Nachrichtentext zurück.
         * 
         * @return Der Nachrichtentext
         */
        public String getMessageText() {
            return messageText;
        }
        
        /**
         * Gibt den Zeitstempel zurück.
         * 
         * @return Der Zeitstempel
         */
        public long getTimestamp() {
            return timestamp;
        }
        
        /**
         * Gibt eine Eigenschaft zurück.
         * 
         * @param name Der Name der Eigenschaft
         * @return Der Wert der Eigenschaft
         */
        public Object getProperty(String name) {
            return properties.get(name);
        }
        
        /**
         * Gibt alle Eigenschaften zurück.
         * 
         * @return Die Eigenschaften
         */
        public Map<String, Object> getProperties() {
            return new HashMap<>(properties);
        }
        
        @Override
        public String toString() {
            return String.format("JmsMessage [id=%s, from=%s, to=%s, time=%d, text=%s]", 
                    messageId, sender, destination, timestamp, messageText);
        }
    }
}