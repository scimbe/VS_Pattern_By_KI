package de.becke.vs.pattern.observer;

import de.becke.vs.pattern.observer.basic.DataObserver;
import de.becke.vs.pattern.observer.basic.DataSource;
import de.becke.vs.pattern.observer.distributed.MessageBroker;
import de.becke.vs.pattern.observer.distributed.Publisher;
import de.becke.vs.pattern.observer.distributed.Subscriber;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.util.Scanner;

/**
 * Hauptklasse zur Demonstration des Observer-Patterns in verschiedenen Varianten.
 */
public class Main {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        LOGGER.info("Starte Demonstration des Observer-Patterns");
        
        // Demonstration des grundlegenden Observer-Patterns
        demonstrateBasicObserver();
        
        // Demonstration des verteilten Observer-Patterns (Publish-Subscribe)
        // Hinweis: Dies erfordert einen laufenden ActiveMQ-Broker
        // demonstrateDistributedObserver();
        
        LOGGER.info("Demonstration abgeschlossen");
    }
    
    /**
     * Demonstriert das grundlegende Observer-Pattern.
     */
    private static void demonstrateBasicObserver() {
        LOGGER.info("\n--- Grundlegendes Observer-Pattern ---");
        
        // Erstelle ein DataSource-Objekt als Subject
        DataSource dataSource = new DataSource("Initialer Datenwert");
        
        // Erstelle mehrere Observer
        DataObserver observer1 = new DataObserver("Observer-1");
        DataObserver observer2 = new DataObserver("Observer-2");
        DataObserver observer3 = new DataObserver("Observer-3");
        
        // Registriere Observer beim Subject
        LOGGER.info("Registriere Observer beim DataSource-Objekt");
        dataSource.addObserver(observer1);
        dataSource.addObserver(observer2);
        
        // Ändere Daten und beobachte die Benachrichtigungen
        LOGGER.info("Ändere Daten im DataSource-Objekt");
        dataSource.setData("Neuer Datenwert");
        
        // Füge einen weiteren Observer hinzu
        LOGGER.info("Füge einen weiteren Observer hinzu");
        dataSource.addObserver(observer3);
        
        // Ändere erneut die Daten
        LOGGER.info("Ändere Daten erneut");
        dataSource.setData("Aktualisierter Datenwert");
        
        // Entferne einen Observer
        LOGGER.info("Entferne Observer-1");
        dataSource.removeObserver(observer1);
        
        // Simuliere eine externe Datenaktualisierung
        LOGGER.info("Simuliere externe Datenaktualisierung");
        dataSource.simulateDataUpdate();
        
        // Überprüfe, ob jeder Observer die korrekten Daten erhalten hat
        LOGGER.info("Letzte empfangene Daten von Observer-1: {}", observer1.getLastData());
        LOGGER.info("Letzte empfangene Daten von Observer-2: {}", observer2.getLastData());
        LOGGER.info("Letzte empfangene Daten von Observer-3: {}", observer3.getLastData());
    }
    
    /**
     * Demonstriert das verteilte Observer-Pattern mit JMS.
     * 
     * Hinweis: Dies erfordert einen laufenden ActiveMQ-Broker.
     * Diese Methode ist auskommentiert, da sie eine externe Abhängigkeit hat.
     */
    private static void demonstrateDistributedObserver() {
        LOGGER.info("\n--- Verteiltes Observer-Pattern (Publish-Subscribe) ---");
        
        // Erstelle einen MessageBroker
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        MessageBroker broker = new MessageBroker(connectionFactory);
        
        try {
            // Starte den Broker
            broker.start();
            
            // Erstelle Publisher
            Publisher publisher1 = new Publisher("Publisher-1", broker);
            Publisher publisher2 = new Publisher("Publisher-2", broker);
            
            // Erstelle Subscriber
            Subscriber subscriber1 = new Subscriber("Subscriber-1", broker);
            Subscriber subscriber2 = new Subscriber("Subscriber-2", broker);
            Subscriber subscriber3 = new Subscriber("Subscriber-3", broker);
            
            // Definiere Topics
            final String SENSOR_TOPIC = "sensors.data";
            final String SYSTEM_TOPIC = "system.status";
            
            // Abonniere Topics
            subscriber1.subscribe(SENSOR_TOPIC);
            subscriber1.subscribe(SYSTEM_TOPIC);
            subscriber2.subscribe(SENSOR_TOPIC);
            subscriber3.subscribe(SYSTEM_TOPIC);
            
            // Veröffentliche Ereignisse
            publisher1.publishEvent(SENSOR_TOPIC, "Temperatur: 23.5°C");
            publisher2.publishEvent(SYSTEM_TOPIC, "System gestartet");
            
            // Simuliere eine Reihe von Ereignissen
            publisher1.simulateEvents(SENSOR_TOPIC, 5, 500);
            publisher2.simulateEvents(SYSTEM_TOPIC, 3, 800);
            
            // Warte auf Abschluss der Simulation
            Thread.sleep(5000);
            
            // Zeige Statistiken
            LOGGER.info("Subscriber-1 hat {} Nachrichten auf Topic '{}' empfangen", 
                    subscriber1.getMessageCount(SENSOR_TOPIC), SENSOR_TOPIC);
            LOGGER.info("Subscriber-1 hat {} Nachrichten auf Topic '{}' empfangen", 
                    subscriber1.getMessageCount(SYSTEM_TOPIC), SYSTEM_TOPIC);
            LOGGER.info("Subscriber-2 hat {} Nachrichten auf Topic '{}' empfangen", 
                    subscriber2.getMessageCount(SENSOR_TOPIC), SENSOR_TOPIC);
            LOGGER.info("Subscriber-3 hat {} Nachrichten auf Topic '{}' empfangen", 
                    subscriber3.getMessageCount(SYSTEM_TOPIC), SYSTEM_TOPIC);
            
            // Hebe Abonnements auf
            subscriber1.unsubscribe(SENSOR_TOPIC);
            subscriber2.unsubscribe(SENSOR_TOPIC);
            subscriber3.unsubscribe(SYSTEM_TOPIC);
            
            // Bereinige Ressourcen
            broker.stop();
            
        } catch (JMSException | InterruptedException e) {
            LOGGER.error("Fehler bei der Demonstration des verteilten Observer-Patterns: {}", 
                    e.getMessage(), e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Wartet auf die Eingabe des Benutzers, um die Demonstration fortzusetzen.
     */
    private static void waitForUserInput() {
        LOGGER.info("Drücke Enter, um fortzufahren...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}