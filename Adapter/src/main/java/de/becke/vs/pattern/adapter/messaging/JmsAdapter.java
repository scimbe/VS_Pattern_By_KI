package de.becke.vs.pattern.adapter.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter, der eine JMS-Implementierung in die MessagingService-Schnittstelle integriert.
 */
public class JmsAdapter implements MessagingService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsAdapter.class);
    
    private final JmsMessageService jmsService;
    private final String senderIdentity;
    
    /**
     * Erstellt einen neuen JMS-Adapter.
     * 
     * @param jmsService Der zu adaptierende JMS-Service
     * @param senderIdentity Die Identität des Absenders
     */
    public JmsAdapter(JmsMessageService jmsService, String senderIdentity) {
        this.jmsService = jmsService;
        this.senderIdentity = senderIdentity;
        LOGGER.info("JMS-Adapter initialisiert für Absender: {}", senderIdentity);
    }
    
    @Override
    public boolean sendMessage(String recipient, String message) {
        LOGGER.info("Adapter: Sende Nachricht an {}", recipient);
        
        try {
            // Erstelle Eigenschaften für die JMS-Nachricht
            Map<String, Object> properties = new HashMap<>();
            properties.put("priority", "normal");
            properties.put("contentType", "text/plain");
            properties.put("sentBy", "JmsAdapter");
            
            // Sende die Nachricht über den JMS-Service
            String messageId = jmsService.sendJmsMessage(senderIdentity, recipient, message, properties);
            
            LOGGER.info("Nachricht erfolgreich gesendet, ID: {}", messageId);
            return true;
        } catch (Exception e) {
            LOGGER.error("Fehler beim Senden der Nachricht: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public Message[] receiveMessages(String recipient) {
        LOGGER.info("Adapter: Empfange Nachrichten für {}", recipient);
        
        try {
            // Empfange Nachrichten vom JMS-Service
            JmsMessageService.JmsMessage[] jmsMessages = jmsService.receiveJmsMessages(
                    recipient, 
                    JmsMessageService.JmsAcknowledgeMode.AUTO_ACKNOWLEDGE);
            
            // Konvertiere JMS-Nachrichten in MessagingService-Nachrichten
            List<Message> convertedMessages = new ArrayList<>();
            
            for (JmsMessageService.JmsMessage jmsMessage : jmsMessages) {
                Message message = convertJmsMessage(jmsMessage);
                convertedMessages.add(message);
            }
            
            LOGGER.info("{} Nachrichten empfangen", convertedMessages.size());
            return convertedMessages.toArray(new Message[0]);
        } catch (Exception e) {
            LOGGER.error("Fehler beim Empfangen von Nachrichten: {}", e.getMessage());
            return new Message[0];
        }
    }
    
    @Override
    public String getServiceType() {
        return "JMS-Messaging";
    }
    
    /**
     * Konvertiert eine JMS-Nachricht in eine MessagingService-Nachricht.
     * 
     * @param jmsMessage Die zu konvertierende JMS-Nachricht
     * @return Die konvertierte MessagingService-Nachricht
     */
    private Message convertJmsMessage(JmsMessageService.JmsMessage jmsMessage) {
        Message message = new Message();
        
        message.setMessageId(jmsMessage.getMessageId());
        message.setSender(jmsMessage.getSender());
        message.setRecipient(jmsMessage.getDestination());
        message.setContent(jmsMessage.getMessageText());
        message.setTimestamp(new Date(jmsMessage.getTimestamp()));
        
        return message;
    }
}