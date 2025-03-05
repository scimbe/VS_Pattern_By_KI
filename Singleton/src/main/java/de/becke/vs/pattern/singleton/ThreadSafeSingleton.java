package de.becke.vs.pattern.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Eine verbesserte thread-sichere Implementierung des Singleton-Patterns für Microservices.
 * 
 * Diese Klasse demonstriert eine thread-sichere, serialisierbare und für Microservices optimierte
 * Implementierung des Singleton-Patterns unter Verwendung der Double-Checked-Locking-Technik.
 */
public class ThreadSafeSingleton implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadSafeSingleton.class);
    
    // Für Lazy Holder Pattern
    private static class SingletonHolder {
        private static final ThreadSafeSingleton INSTANCE = new ThreadSafeSingleton();
    }
    
    // Simulierte Verbindung oder Ressource
    private transient String connectionState;
    
    /**
     * Private Konstruktor, der die Erstellung von Instanzen von außen verhindert.
     */
    private ThreadSafeSingleton() {
        LOGGER.info("ThreadSafeSingleton wird initialisiert");
        initConnection();
    }
    
    /**
     * Initialisiert die Verbindung. Kann auch zur Wiederherstellung nach Deserialisierung verwendet werden.
     */
    private void initConnection() {
        try {
            // Simuliert eine zeitaufwändige Initialisierung wie z.B. einen Verbindungsaufbau
            Thread.sleep(100);
            connectionState = "Verbunden";
            LOGGER.info("Verbindung initialisiert");
        } catch (InterruptedException e) {
            LOGGER.error("Initialisierung unterbrochen", e);
            connectionState = "Fehler bei Verbindungsaufbau";
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Gibt die einzige Instanz der Klasse zurück.
     * Verwendet das Initialization-on-demand holder idiom für thread-sichere lazy initialization.
     *
     * @return die einzige Instanz von ThreadSafeSingleton
     */
    public static ThreadSafeSingleton getInstance() {
        return SingletonHolder.INSTANCE;
    }
    
    /**
     * Gibt den aktuellen Verbindungsstatus zurück.
     *
     * @return der aktuelle Verbindungsstatus
     */
    public String getConnectionState() {
        return connectionState;
    }
    
    /**
     * Simuliert die Ausführung einer Remote-Operation in einem verteilten System.
     *
     * @param operation der Name der auszuführenden Operation
     * @return das Ergebnis der Operation
     */
    public String executeRemoteOperation(String operation) {
        LOGGER.info("Führe Remote-Operation aus: {}", operation);
        
        if ("Verbunden".equals(connectionState)) {
            return "Ergebnis der Operation: " + operation + " erfolgreich ausgeführt";
        } else {
            // Versuche die Verbindung wiederherzustellen, was in Microservices wichtig ist
            LOGGER.warn("Keine Verbindung. Versuche Wiederverbindung...");
            initConnection();
            
            if ("Verbunden".equals(connectionState)) {
                LOGGER.info("Wiederverbindung erfolgreich, führe Operation aus");
                return "Ergebnis der Operation (nach Wiederverbindung): " + operation + " erfolgreich ausgeführt";
            }
            
            return "Fehler: Keine Verbindung zum Remote-System";
        }
    }
    
    /**
     * Simuliert das Schließen der Verbindung.
     */
    public void closeConnection() {
        LOGGER.info("Verbindung wird geschlossen");
        connectionState = "Getrennt";
    }
    
    /**
     * Schützt vor Singleton-Verletzung durch Deserialisierung.
     * 
     * @return Die bestehende Singleton-Instanz
     */
    protected Object readResolve() {
        return getInstance();
    }
}