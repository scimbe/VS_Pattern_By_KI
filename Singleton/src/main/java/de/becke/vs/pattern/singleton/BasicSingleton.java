package de.becke.vs.pattern.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Eine verbesserte Implementierung des Singleton-Patterns.
 * 
 * Diese Klasse demonstriert eine thread-sichere und serialisierbare Implementierung
 * des Singleton-Patterns, die besser für den Einsatz in Microservices geeignet ist.
 */
public class BasicSingleton implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicSingleton.class);
    
    // Die einzige Instanz dieser Klasse mit volatile für bessere Thread-Sicherheit
    private static volatile BasicSingleton instance;
    
    // Die Singleton-Klasse könnte einen internen Zustand speichern
    private String data;
    
    /**
     * Private Konstruktor, der die Erstellung von Instanzen von außen verhindert.
     */
    private BasicSingleton() {
        LOGGER.info("BasicSingleton wird initialisiert");
        data = "Initialisierte Daten";
    }
    
    /**
     * Gibt die einzige Instanz der Klasse zurück.
     * Diese Implementierung ist thread-sicher durch Double-Checked Locking.
     *
     * @return die einzige Instanz von BasicSingleton
     */
    public static BasicSingleton getInstance() {
        if (instance == null) {
            synchronized (BasicSingleton.class) {
                if (instance == null) {
                    LOGGER.info("Erzeuge neue BasicSingleton-Instanz");
                    instance = new BasicSingleton();
                }
            }
        }
        return instance;
    }
    
    /**
     * Gibt die gespeicherten Daten zurück.
     *
     * @return die im Singleton gespeicherten Daten
     */
    public String getData() {
        return data;
    }
    
    /**
     * Setzt die gespeicherten Daten.
     *
     * @param data die zu speichernden Daten
     */
    public void setData(String data) {
        this.data = data;
        LOGGER.info("Daten wurden geändert: {}", data);
    }
    
    /**
     * Demonstriert die Verwendung in verteilten Systemen (als Mock-Implementation).
     * In realen Anwendungen könnte dies eine Remote-Verbindung oder einen Cache darstellen.
     */
    public String getRemoteData() {
        LOGGER.info("Hole Daten von entferntem System (simuliert)");
        return "Remote-Daten: " + data;
    }
    
    /**
     * Schützt vor Singleton-Verletzung durch Deserialisierung.
     * 
     * @return Die bestehende Singleton-Instanz
     */
    protected Object readResolve() {
        // Gibt die bestehende Instanz zurück und verwirft die deserialisierte Instanz
        return getInstance();
    }
}