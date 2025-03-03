package de.becke.vs.pattern.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eine thread-sichere Implementierung des Singleton-Patterns.
 * 
 * Diese Klasse demonstriert eine thread-sichere Implementierung des Singleton-Patterns
 * unter Verwendung der Double-Checked-Locking-Technik, die für Umgebungen mit
 * mehreren Threads geeignet ist, insbesondere in verteilten Systemen.
 */
public class ThreadSafeSingleton {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadSafeSingleton.class);
    
    // Volatile stellt sicher, dass Änderungen sofort für alle Threads sichtbar sind
    private static volatile ThreadSafeSingleton instance;
    
    // Simulierte Verbindung oder Ressource
    private String connectionState;
    
    /**
     * Private Konstruktor, der die Erstellung von Instanzen von außen verhindert.
     */
    private ThreadSafeSingleton() {
        LOGGER.info("ThreadSafeSingleton wird initialisiert");
        // Simuliert eine zeitaufwändige Initialisierung wie z.B. einen Verbindungsaufbau
        try {
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
     * Gibt die einzige Instanz der Klasse zurück mit Double-Checked Locking
     * für Threadsicherheit und optimale Performance.
     *
     * @return die einzige Instanz von ThreadSafeSingleton
     */
    public static ThreadSafeSingleton getInstance() {
        // Erste Prüfung ohne Synchronisation für bessere Performance
        if (instance == null) {
            // Synchronisiert den Zugriff, wenn instance noch null ist
            synchronized (ThreadSafeSingleton.class) {
                // Zweite Prüfung innerhalb des synchronisierten Blocks
                if (instance == null) {
                    LOGGER.info("Erzeuge neue ThreadSafeSingleton-Instanz");
                    instance = new ThreadSafeSingleton();
                }
            }
        }
        return instance;
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
}