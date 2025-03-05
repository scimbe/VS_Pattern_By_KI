package de.becke.vs.pattern.proxy;

import de.becke.vs.pattern.proxy.common.RemoteService;
import de.becke.vs.pattern.proxy.common.ServiceException;
import de.becke.vs.pattern.proxy.loadbalancing.LoadBalancingProxy;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class LoadBalancingProxyTest {

    private LoadBalancingProxy loadBalancingProxy;
    private List<RemoteService> mockServices;

    @Before
    public void setUp() {
        // Erstelle mehrere Mock-Services
        mockServices = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            RemoteService mockService = mock(RemoteService.class);
            try {
                when(mockService.request(anyString())).thenReturn("Antwort von Service " + i);
                when(mockService.request()).thenReturn("Standard-Antwort von Service " + i);
                when(mockService.complexRequest(anyInt(), anyString(), any()))
                    .thenReturn("Komplexe Antwort von Service " + i);
            } catch (ServiceException e) {
                fail("Exception bei Test-Setup: " + e.getMessage());
            }
            mockServices.add(mockService);
        }
        
        // Erstelle LoadBalancingProxy mit ROUND_ROBIN-Strategie für Tests
        loadBalancingProxy = new LoadBalancingProxy(mockServices, LoadBalancingProxy.Strategy.ROUND_ROBIN);
    }

    @Test
    public void testRoundRobinStrategy() throws ServiceException {
        // Erstellen wir explizit eine neue Instanz für diesen Test, um den Round-Robin-Zähler zurückzusetzen
        List<RemoteService> testServices = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            RemoteService mockService = mock(RemoteService.class);
            try {
                when(mockService.request(anyString())).thenReturn("Antwort von Service " + i);
            } catch (ServiceException e) {
                fail("Exception bei Test-Setup: " + e.getMessage());
            }
            testServices.add(mockService);
        }
        
        // Bei Round-Robin sollten die Services der Reihe nach verwendet werden
        LoadBalancingProxy testProxy = new LoadBalancingProxy(testServices, LoadBalancingProxy.Strategy.ROUND_ROBIN);
        
        testProxy.request("test");
        testProxy.request("test");
        testProxy.request("test");
        testProxy.request("test");
        
        // Verifiziere die Runde durch alle Services
        verify(testServices.get(0), times(2)).request("test");
        verify(testServices.get(1), times(1)).request("test");
        verify(testServices.get(2), times(1)).request("test");
    }

    @Test
    public void testRandomStrategy() throws ServiceException {
        // Bei Random-Strategie ist die Verteilung zufällig
        loadBalancingProxy = new LoadBalancingProxy(mockServices, LoadBalancingProxy.Strategy.RANDOM);
        
        // Viele Anfragen ausführen, um die Zufälligkeit zu testen
        for (int i = 0; i < 20; i++) {
            loadBalancingProxy.request("test");
        }
        
        // Verifiziere, dass alle Services verwendet wurden (keine genaue Anzahl)
        for (RemoteService service : mockServices) {
            verify(service, atLeastOnce()).request("test");
        }
    }

    @Test
    public void testLeastConnectionsStrategy() throws ServiceException {
        // Bei Least-Connections sollte der Service mit den wenigsten aktiven Verbindungen verwendet werden
        loadBalancingProxy = new LoadBalancingProxy(mockServices, LoadBalancingProxy.Strategy.LEAST_CONNECTIONS);
        
        // Erstelle verschiedene aktive Verbindungen durch gleichzeitige Anfragen
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    // Unterschiedliche Anfragetypen verwenden, um verschiedene Service-Methoden zu testen
                    if (index % 3 == 0) {
                        loadBalancingProxy.request();
                    } else if (index % 3 == 1) {
                        loadBalancingProxy.request("thread" + index);
                    } else {
                        loadBalancingProxy.complexRequest(index, "data" + index, new String[]{"option"});
                    }
                    
                    // Ein wenig warten, um die aktiven Verbindungen zu simulieren
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            threads[i].start();
        }
        
        // Warten, bis alle Threads abgeschlossen sind
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Prüfe, ob aktive Verbindungen korrekt verfolgt werden
        Map<RemoteService, Integer> activeConnections = loadBalancingProxy.getActiveConnections();
        assertNotNull("Aktive Verbindungen sollten nicht null sein", activeConnections);
        
        // Alle aktiven Verbindungen sollten 0 sein, nachdem alle Anfragen abgeschlossen sind
        for (Integer connections : activeConnections.values()) {
            assertEquals("Aktive Verbindungen sollten nach Anfragen 0 sein", 0, (int) connections);
        }
    }

    @Test
    public void testAddBackendService() throws ServiceException {
        // Neuen Service hinzufügen
        RemoteService newService = mock(RemoteService.class);
        when(newService.request("test")).thenReturn("Antwort vom neuen Service");
        
        loadBalancingProxy.addBackendService(newService);
        
        // Liste der Backend-Services sollte jetzt 4 Einträge haben
        assertEquals("Nach Hinzufügen sollten 4 Services vorhanden sein", 
                    4, loadBalancingProxy.getBackendServices().size());
        
        // Bei 4 Anfragen mit Round-Robin sollte der neue Service verwendet werden
        loadBalancingProxy.request("test");
        loadBalancingProxy.request("test");
        loadBalancingProxy.request("test");
        loadBalancingProxy.request("test");
        
        // Verifiziere, dass der neue Service aufgerufen wurde
        verify(newService, times(1)).request("test");
    }

    @Test
    public void testRemoveBackendService() throws ServiceException {
        // Service entfernen
        loadBalancingProxy.removeBackendService(mockServices.get(0));
        
        // Liste der Backend-Services sollte jetzt 2 Einträge haben
        assertEquals("Nach Entfernen sollten 2 Services vorhanden sein", 
                    2, loadBalancingProxy.getBackendServices().size());
        
        // Bei 2 Anfragen mit Round-Robin sollten nur die verbliebenen Services verwendet werden
        loadBalancingProxy.request("test");
        loadBalancingProxy.request("test");
        
        // Verifiziere, dass der entfernte Service nicht aufgerufen wurde
        verify(mockServices.get(0), never()).request("test");
        verify(mockServices.get(1), times(1)).request("test");
        verify(mockServices.get(2), times(1)).request("test");
    }

    @Test
    public void testPreventRemovingLastService() {
        // Erstelle LoadBalancingProxy mit nur einem Service
        List<RemoteService> singleService = new ArrayList<>();
        singleService.add(mockServices.get(0));
        loadBalancingProxy = new LoadBalancingProxy(singleService, LoadBalancingProxy.Strategy.ROUND_ROBIN);
        
        // Versuche, den letzten Service zu entfernen (sollte fehlschlagen)
        boolean result = loadBalancingProxy.removeBackendService(mockServices.get(0));
        
        // Sollte fehlschlagen und false zurückgeben
        assertFalse("Entfernen des letzten Services sollte fehlschlagen", result);
        
        // Liste der Backend-Services sollte immer noch 1 Eintrag haben
        assertEquals("Nach versuchtem Entfernen sollte immer noch 1 Service vorhanden sein", 
                    1, loadBalancingProxy.getBackendServices().size());
    }
    
    @Test
    public void testComplexRequestWithLoad() throws ServiceException {
        // Komplexe Anfrage ausführen, um zu testen, ob die Lastverteilung auch dort funktioniert
        // Erstellen wir explizit eine neue Instanz für diesen Test, um den Round-Robin-Zähler zurückzusetzen
        List<RemoteService> testServices = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            RemoteService mockService = mock(RemoteService.class);
            try {
                when(mockService.complexRequest(anyInt(), anyString(), any()))
                    .thenReturn("Komplexe Antwort von Service " + i);
            } catch (ServiceException e) {
                fail("Exception bei Test-Setup: " + e.getMessage());
            }
            testServices.add(mockService);
        }
        
        LoadBalancingProxy testProxy = new LoadBalancingProxy(testServices, LoadBalancingProxy.Strategy.ROUND_ROBIN);
        
        testProxy.complexRequest(123, "complex data", new String[]{"option1", "option2"});
        testProxy.complexRequest(456, "more data", new String[]{"option3"});
        testProxy.complexRequest(789, "even more", new String[]{"option4", "option5"});
        
        // Verifiziere, dass die Services entsprechend der Round-Robin-Strategie verwendet wurden
        verify(testServices.get(0), times(1)).complexRequest(eq(123), eq("complex data"), any());
        verify(testServices.get(1), times(1)).complexRequest(eq(456), eq("more data"), any());
        verify(testServices.get(2), times(1)).complexRequest(eq(789), eq("even more"), any());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructorWithEmptyServiceList() {
        // Sollte eine Exception werfen, wenn keine Services angegeben werden
        new LoadBalancingProxy(new ArrayList<>(), LoadBalancingProxy.Strategy.ROUND_ROBIN);
    }
}
