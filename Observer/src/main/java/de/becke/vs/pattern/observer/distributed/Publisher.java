package de.becke.vs.pattern.observer.distributed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;

/**
 * Eine Publisher-Klasse für ein verteiltes Observer-Pattern.
 * 
 * Diese Klasse entspricht dem Subject in einem verteilten Kontext und verwendet
 * einen MessageBroker, um Nachrichten an Abonnenten zu veröffentlichen.
 */
public class Publisher {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Publisher.class);
    
    private final String publisherId;
    private final MessageBroker broker;
    
    /**
     * Konstruktor mit Publisher-ID und MessageBroker.
     * 
     * @param publisherId Die ID des Publishers
     * @param broker Der MessageBroker, der für die Veröffentlichung verwendet wird
     */
    public Publisher(String publisherId, MessageBroker broker) {
        this.publisherId = publisherId;
        this.broker = broker;
        LOGGER.info("Publisher '{}' erstellt", publisherId);
    }
    
    /**
     * Veröffentlicht ein Ereignis zu einem bestimmten Topic.
     * 
     * @param topic Das Topic, zu dem veröffentlicht werden soll
     * @param eventData Die Daten des Ereignisses
     */
    public void publishEvent(String topic, String eventData) {
        try {
            LOGGER.info("Publisher '{}' veröffentlicht Ereignis zu Topic '{}': {}", 
                    publisherId, topic, eventData);
            
            // Füge Publisher-ID zu den Ereignisdaten hinzu
            String enrichedData = String.format("{'publisher':'%s', 'data':'%s'}", 
                    publisherId, eventData);
            
            // Veröffentliche über den MessageBroker
            broker.publish(topic, enrichedData);
            
            LOGGER.info("Ereignis erfolgreich veröffentlicht");
        } catch (JMSException e) {
            LOGGER.error("Fehler beim Veröffentlichen des Ereignisses: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Simuliert die Generierung und Veröffentlichung von Ereignissen.
     * 
     * @param topic Das Topic, zu dem veröffentlicht werden soll
     * @param count Die Anzahl der zu generierenden Ereignisse
     * @param delayMs Die Verzögerung zwischen den Ereignissen in Millisekunden
     */
    public void simulateEvents(String topic, int count, long delayMs) {
        LOGGER.info("Simuliere {} Ereignisse auf Topic '{}' mit {}ms Verzögerung", 
                count, topic, delayMs);
        
        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                String eventData = String.format("Ereignis Nr. %d vom Publisher %s", 
                        i + 1, publisherId);
                publishEvent(topic, eventData);
                
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    LOGGER.error("Ereignissimulation unterbrochen", e);
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            LOGGER.info("Ereignissimulation abgeschlossen");
        }).start();
    }
    
    /**
     * Gibt die ID des Publishers zurück.
     * 
     * @return Die ID des Publishers
     */
    public String getPublisherId() {
        return publisherId;
    }
}