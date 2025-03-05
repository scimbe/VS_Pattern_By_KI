package de.becke.vs.pattern.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Eine Enum-basierte Implementierung des Singleton-Patterns.
 * 
 * Diese Klasse demonstriert eine Alternative zur klassischen Implementierung des
 * Singleton-Patterns unter Verwendung einer Enum. Diese Methode ist von Joshua Bloch
 * in "Effective Java" empfohlen und bietet eine thread-sichere, serialisierungssichere
 * und reflection-sichere Singleton-Implementierung.
 */
public enum EnumSingleton {
    
    INSTANCE;
    
    // Logger als nicht-statische Variable deklarieren
    private final Logger logger = LoggerFactory.getLogger(EnumSingleton.class);
    
    // Simulierte Konfigurationseinstellungen für ein verteiltes System
    private final Map<String, String> configuration;
    
    /**
     * Konstruktor für die Enum-Instanz.
     */
    EnumSingleton() {
        logger.info("EnumSingleton wird initialisiert");
        
        // Simulierte Konfigurationseinstellungen
        configuration = new HashMap<>();
        configuration.put("db.host", "localhost");
        configuration.put("db.port", "3306");
        configuration.put("service.url", "http://api.example.com");
        configuration.put("cache.timeToLive", "3600");
        configuration.put("system.mode", "production");
        
        logger.info("Konfiguration geladen mit {} Einstellungen", configuration.size());
    }
    
    /**
     * Gibt einen Konfigurationswert zurück.
     *
     * @param key der Schlüssel der Konfigurationseinstellung
     * @return der Wert der Konfigurationseinstellung oder null, wenn nicht gefunden
     */
    public String getConfigValue(String key) {
        logger.debug("Konfigurationswert abgerufen: {}", key);
        return configuration.get(key);
    }
    
    /**
     * Setzt einen Konfigurationswert.
     *
     * @param key der Schlüssel der Konfigurationseinstellung
     * @param value der Wert der Konfigurationseinstellung
     */
    public void setConfigValue(String key, String value) {
        logger.info("Konfigurationswert geändert: {} = {}", key, value);
        configuration.put(key, value);
    }
    
    /**
     * Gibt alle Konfigurationseinstellungen zurück.
     *
     * @return eine Map mit allen Konfigurationseinstellungen
     */
    public Map<String, String> getAllConfig() {
        // Gibt eine Kopie zurück, um den internen Zustand zu schützen
        return new HashMap<>(configuration);
    }
    
    /**
     * Simuliert das Anwenden von Konfigurationsänderungen in einem verteilten System.
     */
    public void applyConfigToDistributedSystem() {
        logger.info("Wende Konfiguration auf verteiltes System an");
        
        // Simuliert einen Network-Call oder eine Broadcast-Operation
        try {
            Thread.sleep(50);
            logger.info("Konfigurationsänderungen wurden auf alle Knoten übertragen");
        } catch (InterruptedException e) {
            logger.error("Übertragung der Konfiguration unterbrochen", e);
            Thread.currentThread().interrupt();
        }
    }
}