package de.becke.vs.pattern.adapter.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstriert die Verwendung verschiedener Messaging-Adapter.
 */
public class MessagingAdapterDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessagingAdapterDemo.class);
    
    /**
     * Führt die Messaging-Adapter-Demonstration durch.
     */
    public void runDemo() {
        LOGGER.info("Starte Messaging-Adapter-Demonstration");
        
        // 1. JMS-Adapter
        demonstrateJmsAdapter();
        
        LOGGER.info("Messaging-Adapter-Demonstration abgeschlossen");
    }
    
    /**
     * Demonstriert den JMS-Adapter.
     */
    private void demonstrateJmsAdapter() {
        LOGGER.info("\n1. JMS-Adapter Demonstration:");
        
        // Erstelle einen JMS-Service
        JmsMessageService jmsService = new JmsMessageService();
        
        // Erstelle zwei Adapter für verschiedene Benutzer
        MessagingService aliceAdapter = new JmsAdapter(jmsService, "alice");
        MessagingService bobAdapter = new JmsAdapter(jmsService, "bob");
        
        // Alice sendet eine Nachricht an Bob
        LOGGER.info("Alice sendet eine Nachricht an Bob");
        aliceAdapter.sendMessage("bob", "Hallo Bob, hier ist Alice!");
        
        // Bob empfängt die Nachricht
        LOGGER.info("Bob empfängt seine Nachrichten");
        Message[] bobMessages = bobAdapter.receiveMessages("bob");
        
        for (Message message : bobMessages) {
            LOGGER.info("Bob hat eine Nachricht erhalten: {}", message);
        }
        
        // Bob antwortet Alice
        LOGGER.info("Bob antwortet Alice");
        bobAdapter.sendMessage("alice", "Hallo Alice, hier ist Bob! Wie geht es dir?");
        
        // Alice empfängt die Antwort
        LOGGER.info("Alice empfängt ihre Nachrichten");
        Message[] aliceMessages = aliceAdapter.receiveMessages("alice");
        
        for (Message message : aliceMessages) {
            LOGGER.info("Alice hat eine Nachricht erhalten: {}", message);
        }
        
        // Mehrere Nachrichten senden
        LOGGER.info("Alice sendet mehrere Nachrichten an Bob");
        aliceAdapter.sendMessage("bob", "Nachricht 1");
        aliceAdapter.sendMessage("bob", "Nachricht 2");
        aliceAdapter.sendMessage("bob", "Nachricht 3");
        
        // Bob empfängt die Nachrichten
        LOGGER.info("Bob empfängt seine Nachrichten");
        Message[] bobMultipleMessages = bobAdapter.receiveMessages("bob");
        
        LOGGER.info("Bob hat {} Nachrichten erhalten", bobMultipleMessages.length);
        for (Message message : bobMultipleMessages) {
            LOGGER.info("- {}", message);
        }
        
        // Direkte Verwendung des JMS-Service zum Vergleich
        LOGGER.info("Direkter Zugriff auf JMS-Service zum Vergleich");
        
        // Charlie sendet eine Nachricht an Dave (ohne Adapter)
        String messageId = jmsService.sendJmsMessage("charlie", "dave", "Hallo Dave, hier ist Charlie!", null);
        LOGGER.info("Charlie hat eine Nachricht direkt über JMS gesendet, ID: {}", messageId);
        
        // Dave empfängt die Nachricht (ohne Adapter)
        JmsMessageService.JmsMessage[] daveMessages = jmsService.receiveJmsMessages("dave", 
                JmsMessageService.JmsAcknowledgeMode.AUTO_ACKNOWLEDGE);
        
        LOGGER.info("Dave hat {} Nachrichten direkt über JMS erhalten", daveMessages.length);
        for (JmsMessageService.JmsMessage jmsMessage : daveMessages) {
            LOGGER.info("- {}", jmsMessage);
        }
    }
}