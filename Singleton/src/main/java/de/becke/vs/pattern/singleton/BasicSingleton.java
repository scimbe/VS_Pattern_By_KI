package de.becke.vs.pattern.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eine einfache Implementierung des Singleton-Patterns.
 * 
 * Diese Klasse demonstriert die grundlegende Implementierung des Singleton-Patterns,
 * bei der eine einzige Instanz der Klasse erstellt und über eine statische Methode
 * darauf zugegriffen wird.
 * 
 * Hinweis: Diese Implementierung ist nicht thread-sicher. Für eine thread-sichere 
 * Implementierung siehe {@link ThreadSafeSingleton}.
 */
public class BasicSingleton {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicSingleton.class);
    
    // Die einzige Instanz dieser Klasse
    private static BasicSingleton instance;
    
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
     * Wenn die Instanz noch nicht existiert, wird sie erstellt.
     *
     * @return die einzige Instanz von BasicSingleton
     */
    public static BasicSingleton getInstance() {
        if (instance == null) {
            LOGGER.info("Erzeuge neue BasicSingleton-Instanz");
            instance = new BasicSingleton();
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
}