package de.becke.vs.pattern.singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hauptklasse zur Demonstration der verschiedenen Singleton-Implementierungen.
 * 
 * Diese Klasse zeigt die Verwendung der verschiedenen Singleton-Varianten
 * und deren Anwendung in Szenarien verteilter Systeme, insbesondere für Microservices.
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
        
        // Demonstration: Serialisierungstest (wichtig für Microservices)
        demonstrateSerialization();
        
        // Demonstration: Microservice Load-Balancing Simulation
        demonstrateMicroserviceScenario();
        
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
        
        final int threadCount = 10;
        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch doneSignal = new CountDownLatch(threadCount);
        
        // Simuliere mehrere Client-Anfragen in einem verteilten System
        for (int i = 0; i < threadCount; i++) {
            final int clientId = i;
            
            Thread clientThread = new Thread(() -> {
                try {
                    // Warte auf das Startsignal (simuliert gleichzeitigen Zugriff)
                    startSignal.await();
                    
                    LOGGER.info("Client {} versucht, auf ThreadSafeSingleton zuzugreifen", clientId);
                    ThreadSafeSingleton instance = ThreadSafeSingleton.getInstance();
                    LOGGER.info("Client {} hat Zugriff auf ThreadSafeSingleton mit Status: {}", 
                            clientId, instance.getConnectionState());
                    
                    // Simuliere Arbeit
                    String result = instance.executeRemoteOperation("GetDataForClient" + clientId);
                    LOGGER.info("Client {} Ergebnis: {}", clientId, result);
                } catch (InterruptedException e) {
                    LOGGER.error("Thread unterbrochen", e);
                    Thread.currentThread().interrupt();
                } finally {
                    doneSignal.countDown();
                }
            });
            
            clientThread.start();
        }
        
        // Alle Threads gleichzeitig starten
        startSignal.countDown();
        
        // Warten, bis alle Threads fertig sind
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            LOGGER.error("Warten auf Threads unterbrochen", e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Demonstriert die Serialisierungs- und Deserialisierungssicherheit der Singletons.
     * Dies ist wichtig für verteilte Systeme, in denen Objekte über das Netzwerk übertragen werden.
     */
    private static void demonstrateSerialization() {
        LOGGER.info("\n--- Serialisierungssicherheit Demonstration ---");
        
        try {
            // BasicSingleton serialisieren
            BasicSingleton original = BasicSingleton.getInstance();
            original.setData("Daten vor Serialisierung");
            
            // Serialisierung
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(original);
            oos.close();
            
            // Deserialisierung
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            BasicSingleton deserialized = (BasicSingleton) ois.readObject();
            ois.close();
            
            // Sind die Instanzen identisch?
            LOGGER.info("Original == Deserialisiert: {}", original == deserialized);
            LOGGER.info("Original Daten: {}", original.getData());
            LOGGER.info("Deserialisierte Daten: {}", deserialized.getData());
            
            // Das gleiche für ThreadSafeSingleton
            ThreadSafeSingleton tsOriginal = ThreadSafeSingleton.getInstance();
            
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(tsOriginal);
            oos.close();
            
            bais = new ByteArrayInputStream(baos.toByteArray());
            ois = new ObjectInputStream(bais);
            ThreadSafeSingleton tsDeserialized = (ThreadSafeSingleton) ois.readObject();
            ois.close();
            
            LOGGER.info("ThreadSafeSingleton Original == Deserialisiert: {}", tsOriginal == tsDeserialized);
            
        } catch (Exception e) {
            LOGGER.error("Fehler während Serialisierungstest", e);
        }
    }
    
    /**
     * Demonstriert ein typisches Microservice-Szenario mit multiplen Instanzen.
     */
    private static void demonstrateMicroserviceScenario() {
        LOGGER.info("\n--- Microservice-Szenario Demonstration ---");
        
        // Simuliert mehrere Microservice-Instanzen mit einem Thread-Pool
        ExecutorService servicePool = Executors.newFixedThreadPool(3);
        
        // Konfigurationsänderung simulieren
        EnumSingleton configManager = EnumSingleton.INSTANCE;
        configManager.setConfigValue("service.endpoint", "/api/v2/data");
        
        // Simuliere Anfragen an verschiedene Microservice-Instanzen
        for (int i = 0; i < 5; i++) {
            final int requestId = i;
            servicePool.submit(() -> {
                LOGGER.info("Microservice bearbeitet Anfrage {}", requestId);
                
                // Konfiguration abrufen (sollte in allen Instanzen gleich sein)
                String endpoint = configManager.getConfigValue("service.endpoint");
                LOGGER.info("Anfrage {} verwendet Endpoint: {}", requestId, endpoint);
                
                // Datenbank-Verbindung simulieren
                ThreadSafeSingleton connectionManager = ThreadSafeSingleton.getInstance();
                String result = connectionManager.executeRemoteOperation("ProcessRequest" + requestId);
                LOGGER.info("Ergebnis für Anfrage {}: {}", requestId, result);
            });
        }
        
        // Aufräumen
        servicePool.shutdown();
        try {
            // Warte maximal 2 Sekunden, bis alle Aufgaben abgeschlossen sind
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            LOGGER.error("Warten unterbrochen", e);
            Thread.currentThread().interrupt();
        }
        
        if (!servicePool.isTerminated()) {
            servicePool.shutdownNow();
        }
    }
}