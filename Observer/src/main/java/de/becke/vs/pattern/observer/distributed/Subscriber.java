package de.becke.vs.pattern.observer.distributed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Eine Subscriber-Klasse für ein verteiltes Observer-Pattern.
 * 
 * Diese Klasse entspricht dem Observer in einem verteilten Kontext und verwendet
 * einen MessageBroker, um Nachrichten von einem oder mehreren Publishern zu empfangen.
 */
public class Subscriber {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Subscriber.class);
    
    private final String subscriberId;
    private final MessageBroker broker;
    
    // Speichert die Anzahl der pro Topic empfangenen Nachrichten
    private final Map<String, AtomicInteger> messageCounters = new HashMap<>();
    
    /**
     * Konstruktor mit Subscriber-ID und MessageBroker.
     * 
     * @param subscriberId Die ID des Subscribers
     * @param broker Der MessageBroker, der für den Empfang verwendet wird
     */
    public Subscriber(String subscriberId, MessageBroker broker) {
        this.subscriberId = subscriberId;
        this.broker = broker;
        LOGGER.info("Subscriber '{}' erstellt", subscriberId);
    }
    
    /**
     * Abonniert ein Topic mit einem benutzerdefinierten EventHandler.
     * 
     * @param topic Das zu abonnierende Topic
     * @param handler Der EventHandler für empfangene Ereignisse
     */
    public void subscribe(String topic, EventHandler handler) {
        try {
            LOGGER.info("Subscriber '{}' abonniert Topic '{}'", subscriberId, topic);
            
            // Initialisiere den Zähler für dieses Topic
            messageCounters.put(topic, new AtomicInteger(0));
            
            // Erstelle einen Wrapper-Handler, der Zähler aktualisiert und dann den benutzerdefinierten Handler aufruft
            EventHandler wrapperHandler = (t, content) -> {
                // Erhöhe den Zähler
                AtomicInteger counter = messageCounters.get(t);
                int count = counter.incrementAndGet();
                
                LOGGER.info("Subscriber '{}' hat {} Nachrichten auf Topic '{}' empfangen", 
                        subscriberId, count, t);
                
                // Rufe den benutzerdefinierten Handler auf
                handler.onEvent(t, content);
            };
            
            // Abonniere das Topic mit dem Wrapper-Handler
            broker.subscribe(topic, subscriberId, wrapperHandler);
            
            LOGGER.info("Subscriber '{}' hat Topic '{}' erfolgreich abonniert", subscriberId, topic);
        } catch (JMSException e) {
            LOGGER.error("Fehler beim Abonnieren des Topics '{}': {}", topic, e.getMessage(), e);
        }
    }
    
    /**
     * Abonniert ein Topic mit einem Standardhandler, der Nachrichten protokolliert.
     * 
     * @param topic Das zu abonnierende Topic
     */
    public void subscribe(String topic) {
        LOGGER.info("Subscriber '{}' abonniert Topic '{}' mit Standardhandler", subscriberId, topic);
        
        // Abonniere mit einem Standardhandler, der die Nachrichten protokolliert
        subscribe(topic, (t, content) -> {
            LOGGER.info("Subscriber '{}' verarbeitet Nachricht auf Topic '{}': {}", 
                    subscriberId, t, content);
            
            // Simuliere eine Verarbeitung
            try {
                Thread.sleep(20);
                LOGGER.info("Subscriber '{}' hat Nachrichtenverarbeitung abgeschlossen", subscriberId);
            } catch (InterruptedException e) {
                LOGGER.error("Verarbeitung unterbrochen", e);
                Thread.currentThread().interrupt();
            }
        });
    }
    
    /**
     * Hebt das Abonnement eines Topics auf.
     * 
     * @param topic Das Topic, dessen Abonnement aufgehoben werden soll
     */
    public void unsubscribe(String topic) {
        try {
            LOGGER.info("Subscriber '{}' hebt Abonnement für Topic '{}' auf", subscriberId, topic);
            broker.unsubscribe(topic, subscriberId);
            LOGGER.info("Abonnement erfolgreich aufgehoben");
        } catch (JMSException e) {
            LOGGER.error("Fehler beim Aufheben des Abonnements für Topic '{}': {}", 
                    topic, e.getMessage(), e);
        }
    }
    
    /**
     * Gibt die Anzahl der für ein bestimmtes Topic empfangenen Nachrichten zurück.
     * 
     * @param topic Das Topic, für das die Anzahl abgerufen werden soll
     * @return Die Anzahl der empfangenen Nachrichten oder 0, wenn das Topic nicht abonniert ist
     */
    public int getMessageCount(String topic) {
        AtomicInteger counter = messageCounters.get(topic);
        return counter != null ? counter.get() : 0;
    }
    
    /**
     * Gibt die ID des Subscribers zurück.
     * 
     * @return Die ID des Subscribers
     */
    public String getSubscriberId() {
        return subscriberId;
    }
}