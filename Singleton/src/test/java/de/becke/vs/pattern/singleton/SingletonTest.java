package de.becke.vs.pattern.singleton;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Testklasse für die Überprüfung der Singleton-Implementierungen.
 */
public class SingletonTest {

    /**
     * Testet, ob BasicSingleton wirklich nur eine Instanz zurückgibt.
     */
    @Test
    public void testBasicSingletonIdentity() {
        BasicSingleton instance1 = BasicSingleton.getInstance();
        BasicSingleton instance2 = BasicSingleton.getInstance();
        
        // Beide Instanzen sollten identisch sein
        assertSame("BasicSingleton sollte die gleiche Instanz zurückgeben", instance1, instance2);
        
        // Änderungen an einer Instanz sollten für beide sichtbar sein
        instance1.setData("Testdaten");
        assertEquals("Daten sollten für beide Instanzen identisch sein", "Testdaten", instance2.getData());
    }
    
    /**
     * Testet, ob ThreadSafeSingleton wirklich nur eine Instanz zurückgibt.
     */
    @Test
    public void testThreadSafeSingletonIdentity() {
        ThreadSafeSingleton instance1 = ThreadSafeSingleton.getInstance();
        ThreadSafeSingleton instance2 = ThreadSafeSingleton.getInstance();
        
        // Beide Instanzen sollten identisch sein
        assertSame("ThreadSafeSingleton sollte die gleiche Instanz zurückgeben", instance1, instance2);
    }
    
    /**
     * Testet, ob EnumSingleton wirklich nur eine Instanz zurückgibt.
     */
    @Test
    public void testEnumSingletonIdentity() {
        EnumSingleton instance1 = EnumSingleton.INSTANCE;
        EnumSingleton instance2 = EnumSingleton.INSTANCE;
        
        // Beide Instanzen sollten identisch sein
        assertSame("EnumSingleton sollte die gleiche Instanz zurückgeben", instance1, instance2);
        
        // Änderungen an einer Instanz sollten für beide sichtbar sein
        instance1.setConfigValue("test.key", "test.value");
        assertEquals("Konfigurationsdaten sollten für beide Instanzen identisch sein", 
                "test.value", instance2.getConfigValue("test.key"));
    }
    
    /**
     * Testet die Thread-Sicherheit des ThreadSafeSingleton in einer Mehrthread-Umgebung.
     */
    @Test
    public void testThreadSafeSingletonInMultithreadedEnvironment() throws Exception {
        final int THREAD_COUNT = 10;
        final CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<ThreadSafeSingleton>> futures = new ArrayList<>();
        
        // Starte mehrere Threads, die gleichzeitig auf die Singleton-Instanz zugreifen
        for (int i = 0; i < THREAD_COUNT; i++) {
            futures.add(executorService.submit(() -> {
                // Warte, bis alle Threads bereit sind
                latch.await();
                // Hole die Singleton-Instanz
                return ThreadSafeSingleton.getInstance();
            }));
        }
        
        // Starte alle Threads gleichzeitig
        latch.countDown();
        
        // Sammle die Ergebnisse
        ThreadSafeSingleton firstInstance = futures.get(0).get();
        for (Future<ThreadSafeSingleton> future : futures) {
            ThreadSafeSingleton instance = future.get();
            // Alle zurückgegebenen Instanzen sollten identisch sein
            assertSame("ThreadSafeSingleton sollte in allen Threads die gleiche Instanz zurückgeben", 
                    firstInstance, instance);
        }
        
        executorService.shutdown();
    }
    
    /**
     * Testet die Funktionalität des EnumSingleton für die Konfigurationsverwaltung.
     */
    @Test
    public void testEnumSingletonConfiguration() {
        EnumSingleton configManager = EnumSingleton.INSTANCE;
        
        // Teste den Standardwert
        assertEquals("localhost", configManager.getConfigValue("db.host"));
        
        // Ändere einen Wert und prüfe, ob er aktualisiert wurde
        configManager.setConfigValue("db.host", "192.168.1.1");
        assertEquals("192.168.1.1", configManager.getConfigValue("db.host"));
        
        // Füge einen neuen Wert hinzu und prüfe, ob er gesetzt wurde
        configManager.setConfigValue("new.setting", "new.value");
        assertEquals("new.value", configManager.getConfigValue("new.setting"));
    }
}