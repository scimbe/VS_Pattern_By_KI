package de.becke.vs.pattern.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hauptklasse zur Demonstration der verschiedenen Singleton-Implementierungen.
 * 
 * Diese Klasse zeigt die Verwendung der verschiedenen Singleton-Varianten
 * und deren Anwendung in Szenarien verteilter Systeme.
 */
public class Main {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        LOGGER.info("Starte Demonstration des Singleton-Patterns in verteilten Systemen");
        
        // Basic Singleton Demonstration
        demonstrateBasicSingleton();
        
        // Thread-safe Singleton Demonstration
        demonstrateThreadSafeSingleton();
        
        // Enum Singleton Demonstration
        demonstrateEnumSingleton();
        
        // Demonstration: Verifizierung, dass es sich wirklich um dieselbe Instanz handelt
        demonstrateIdentity();
        
        // Demonstration: Thread-Safety mit simulierten Client-Anfragen
        demonstrateMultithreadedAccess();
        
        LOGGER.info("Demonstration abgeschlossen");
    }
    
    /**
     * Demonstriert die grundlegende Singleton-Implementierung.
     */
    private static void demonstrateBasicSingleton() {
        LOGGER.info("\n--- Basic Singleton Demonstration ---");
        
        // Erstes Abrufen der Instanz
        BasicSingleton instance1 = BasicSingleton.getInstance();
        LOGGER.info("Initial gespeicherte Daten: {}", instance1.getData());
        
        // Ändern der Daten
        instance1.setData("Aktualisierte Daten");
        
        // Zweites Abrufen der Instanz - sollte dieselbe Instanz sein
        BasicSingleton instance2 = BasicSingleton.getInstance();
        LOGGER.info("Daten aus zweitem Abruf: {}", instance2.getData());
        
        // Simulierte Remote-Operation
        LOGGER.info("Remote-Aufruf: {}", instance1.getRemoteData());
    }
    
    /**
     * Demonstriert die thread-sichere Singleton-Implementierung.
     */
    private static void demonstrateThreadSafeSingleton() {
        LOGGER.info("\n--- Thread-safe Singleton Demonstration ---");
        
        // Abrufen der Instanz
        ThreadSafeSingleton instance = ThreadSafeSingleton.getInstance();
        LOGGER.info("Verbindungsstatus: {}", instance.getConnectionState());
        
        // Ausführen einer Remote-Operation
        String result = instance.executeRemoteOperation("GetData");
        LOGGER.info("Ergebnis: {}", result);
        
        // Verbindung schließen und versuchen, eine weitere Operation auszuführen
        instance.closeConnection();
        result = instance.executeRemoteOperation("GetData");
        LOGGER.info("Ergebnis nach Verbindungstrennung: {}", result);
    }
    
    /**
     * Demonstriert die Enum-basierte Singleton-Implementierung.
     */
    private static void demonstrateEnumSingleton() {
        LOGGER.info("\n--- Enum Singleton Demonstration ---");
        
        // Zugriff auf die Singleton-Instanz
        EnumSingleton configManager = EnumSingleton.INSTANCE;
        
        // Konfiguration anzeigen
        LOGGER.info("Datenbank-Host: {}", configManager.getConfigValue("db.host"));
        LOGGER.info("Service-URL: {}", configManager.getConfigValue("service.url"));
        
        // Konfiguration ändern
        configManager.setConfigValue("system.mode", "development");
        LOGGER.info("Neuer System-Modus: {}", configManager.getConfigValue("system.mode"));
        
        // Änderungen auf das verteilte System anwenden
        configManager.applyConfigToDistributedSystem();
    }
    
    /**
     * Bestätigt, dass die zurückgegebenen Instanzen tatsächlich identisch sind.
     */
    private static void demonstrateIdentity() {
        LOGGER.info("\n--- Identitätsnachweis der Singletons ---");
        
        // Zwei Referenzen auf das BasicSingleton
        BasicSingleton basic1 = BasicSingleton.getInstance();
        BasicSingleton basic2 = BasicSingleton.getInstance();
        LOGGER.info("BasicSingleton Identitätstest: {}", basic1 == basic2);
        
        // Zwei Referenzen auf das ThreadSafeSingleton
        ThreadSafeSingleton safe1 = ThreadSafeSingleton.getInstance();
        ThreadSafeSingleton safe2 = ThreadSafeSingleton.getInstance();
        LOGGER.info("ThreadSafeSingleton Identitätstest: {}", safe1 == safe2);
        
        // Zwei Referenzen auf das EnumSingleton
        EnumSingleton enum1 = EnumSingleton.INSTANCE;
        EnumSingleton enum2 = EnumSingleton.INSTANCE;
        LOGGER.info("EnumSingleton Identitätstest: {}", enum1 == enum2);
    }
    
    /**
     * Demonstriert den Zugriff auf einen Singleton aus mehreren Threads.
     */
    private static void demonstrateMultithreadedAccess() {
        LOGGER.info("\n--- Multithreaded Access Demonstration ---");
        
        // Simuliere mehrere Client-Anfragen in einem verteilten System
        for (int i = 0; i < 3; i++) {
            final int clientId = i;
            
            Thread clientThread = new Thread(() -> {
                LOGGER.info("Client {} versucht, auf ThreadSafeSingleton zuzugreifen", clientId);
                ThreadSafeSingleton instance = ThreadSafeSingleton.getInstance();
                LOGGER.info("Client {} hat Zugriff auf ThreadSafeSingleton mit Status: {}", 
                        clientId, instance.getConnectionState());
                
                // Simulate some work
                String result = instance.executeRemoteOperation("GetDataForClient" + clientId);
                LOGGER.info("Client {} Ergebnis: {}", clientId, result);
            });
            
            clientThread.start();
        }
        
        // Warte kurz, um die Thread-Ausführung zu beobachten
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            LOGGER.error("Thread unterbrochen", e);
            Thread.currentThread().interrupt();
        }
    }
}