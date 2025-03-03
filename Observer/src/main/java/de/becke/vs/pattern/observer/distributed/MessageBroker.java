package de.becke.vs.pattern.observer.distributed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Eine vereinfachte Message-Broker-Implementierung für das Observer-Pattern in verteilten Umgebungen.
 * 
 * Diese Klasse dient als Wrapper um ein JMS-System und stellt eine einfachere API
 * für das Publish-Subscribe-Muster bereit. In einer realen Anwendung würde diese Klasse
 * mit einem echten Message Broker wie ActiveMQ, RabbitMQ oder Kafka verbunden sein.
 */
public class MessageBroker {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBroker.class);
    
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private boolean connected = false;
    
    // Speichert aktive MessageProducer für verschiedene Topics
    private final Map<String, MessageProducer> producers = new HashMap<>();
    
    // Speichert aktive MessageConsumer für verschiedene Topics
    private final Map<String, Map<String, MessageConsumer>> consumers = new HashMap<>();
    
    // ThreadPool für asynchrone Verarbeitung von Nachrichten
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    /**
     * Konstruktor, der eine Verbindung zum Message Broker herstellt.
     * 
     * @param connectionFactory Die JMS ConnectionFactory für die Verbindung
     */
    public MessageBroker(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        LOGGER.info("MessageBroker erstellt");
    }
    
    /**
     * Startet die Verbindung zum Message Broker.
     * 
     * @throws JMSException Wenn ein Fehler bei der Verbindung auftritt
     */
    public void start() throws JMSException {
        LOGGER.info("Starte MessageBroker-Verbindung");
        
        if (!connected) {
            connection = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            connected = true;
            
            LOGGER.info("MessageBroker-Verbindung erfolgreich gestartet");
        }
    }
    
    /**
     * Veröffentlicht eine Nachricht zu einem bestimmten Topic.
     * 
     * @param topic Das Topic, zu dem veröffentlicht werden soll
     * @param content Der Inhalt der Nachricht
     * @throws JMSException Wenn ein Fehler beim Veröffentlichen auftritt
     */
    public void publish(String topic, String content) throws JMSException {
        LOGGER.info("Veröffentliche Nachricht zum Topic '{}': {}", topic, content);
        
        if (!connected) {
            throw new IllegalStateException("MessageBroker ist nicht verbunden");
        }
        
        MessageProducer producer = getOrCreateProducer(topic);
        TextMessage message = session.createTextMessage(content);
        producer.send(message);
        
        LOGGER.info("Nachricht erfolgreich veröffentlicht");
    }
    
    /**
     * Abonniert ein Topic mit einer EventHandler-Funktion.
     * 
     * @param topic Das zu abonnierende Topic
     * @param subscriberId Die ID des Abonnenten
     * @param handler Der EventHandler, der bei Nachrichten aufgerufen wird
     * @throws JMSException Wenn ein Fehler beim Abonnieren auftritt
     */
    public void subscribe(String topic, String subscriberId, EventHandler handler) throws JMSException {
        LOGGER.info("Abonniere Topic '{}' für Subscriber '{}'", topic, subscriberId);
        
        if (!connected) {
            throw new IllegalStateException("MessageBroker ist nicht verbunden");
        }
        
        // Erstelle eine Destination für das Topic
        Destination destination = session.createTopic(topic);
        
        // Erstelle einen MessageConsumer
        MessageConsumer consumer = session.createConsumer(destination);
        
        // Registriere einen MessageListener
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                executorService.submit(() -> {
                    try {
                        if (message instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message;
                            String content = textMessage.getText();
                            
                            LOGGER.info("Subscriber '{}' erhält Nachricht auf Topic '{}': {}", 
                                    subscriberId, topic, content);
                            
                            // Rufe den EventHandler auf
                            handler.onEvent(topic, content);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Fehler bei der Verarbeitung einer Nachricht: {}", 
                                e.getMessage(), e);
                    }
                });
            }
        });
        
        // Speichere den Consumer für späteres Löschen
        consumers.computeIfAbsent(topic, k -> new HashMap<>())
                .put(subscriberId, consumer);
        
        LOGGER.info("Subscriber '{}' erfolgreich für Topic '{}' registriert", subscriberId, topic);
    }
    
    /**
     * Hebt das Abonnement eines Topics für einen bestimmten Abonnenten auf.
     * 
     * @param topic Das Topic, dessen Abonnement aufgehoben werden soll
     * @param subscriberId Die ID des Abonnenten
     * @throws JMSException Wenn ein Fehler beim Aufheben des Abonnements auftritt
     */
    public void unsubscribe(String topic, String subscriberId) throws JMSException {
        LOGGER.info("Hebe Abonnement für Topic '{}' für Subscriber '{}' auf", topic, subscriberId);
        
        if (!connected) {
            throw new IllegalStateException("MessageBroker ist nicht verbunden");
        }
        
        Map<String, MessageConsumer> topicConsumers = consumers.get(topic);
        
        if (topicConsumers != null) {
            MessageConsumer consumer = topicConsumers.remove(subscriberId);
            
            if (consumer != null) {
                consumer.close();
                LOGGER.info("Abonnement erfolgreich aufgehoben");
            }
        }
    }
    
    /**
     * Stoppt den MessageBroker und gibt alle Ressourcen frei.
     */
    public void stop() {
        LOGGER.info("Stoppe MessageBroker");
        
        try {
            // Schließe alle Producer
            for (MessageProducer producer : producers.values()) {
                producer.close();
            }
            producers.clear();
            
            // Schließe alle Consumer
            for (Map<String, MessageConsumer> topicConsumers : consumers.values()) {
                for (MessageConsumer consumer : topicConsumers.values()) {
                    consumer.close();
                }
            }
            consumers.clear();
            
            // Schließe Session und Connection
            if (session != null) {
                session.close();
            }
            
            if (connection != null) {
                connection.close();
            }
            
            // Beende das ExecutorService
            executorService.shutdown();
            
            connected = false;
            LOGGER.info("MessageBroker erfolgreich gestoppt");
        } catch (JMSException e) {
            LOGGER.error("Fehler beim Stoppen des MessageBrokers: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Ruft einen vorhandenen MessageProducer für ein Topic ab oder erstellt einen neuen.
     * 
     * @param topic Das Topic, für das ein Producer benötigt wird
     * @return Der MessageProducer für das angegebene Topic
     * @throws JMSException Wenn ein Fehler beim Erstellen des Producers auftritt
     */
    private MessageProducer getOrCreateProducer(String topic) throws JMSException {
        return producers.computeIfAbsent(topic, t -> {
            try {
                Destination destination = session.createTopic(t);
                return session.createProducer(destination);
            } catch (JMSException e) {
                throw new RuntimeException("Fehler beim Erstellen des MessageProducers für Topic " + t, e);
            }
        });
    }
}