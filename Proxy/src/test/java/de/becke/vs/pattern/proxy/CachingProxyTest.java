package de.becke.vs.pattern.proxy;

import de.becke.vs.pattern.proxy.caching.CacheKey;
import de.becke.vs.pattern.proxy.caching.CachingProxy;
import de.becke.vs.pattern.proxy.common.RemoteService;
import de.becke.vs.pattern.proxy.common.ServiceException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CachingProxyTest {

    private CachingProxy cachingProxy;
    private RemoteService mockService;

    @Before
    public void setUp() {
        // Erstelle Mock-Objekt statt echter Implementierung
        mockService = mock(RemoteService.class);
        
        // Erstelle CachingProxy mit kurzer TTL für Tests
        cachingProxy = new CachingProxy(mockService, 100, 1, TimeUnit.SECONDS);
    }

    @Test
    public void testCacheHit() throws ServiceException {
        // Konfiguriere Mock-Verhalten
        when(mockService.request("testData")).thenReturn("Antwort auf testData");
        
        // Erste Anfrage (Cache-Miss)
        String result1 = cachingProxy.request("testData");
        assertEquals("Antwort auf testData", result1);
        
        // Zweite identische Anfrage (Cache-Hit)
        String result2 = cachingProxy.request("testData");
        assertEquals("Antwort auf testData", result2);
        
        // Verifiziere, dass der Service nur einmal aufgerufen wurde
        verify(mockService, times(1)).request("testData");
    }

    @Test
    public void testCacheMiss() throws ServiceException {
        // Konfiguriere Mock-Verhalten
        when(mockService.request("data1")).thenReturn("Antwort auf data1");
        when(mockService.request("data2")).thenReturn("Antwort auf data2");
        
        // Erste Anfrage
        String result1 = cachingProxy.request("data1");
        assertEquals("Antwort auf data1", result1);
        
        // Andere Anfrage (Cache-Miss)
        String result2 = cachingProxy.request("data2");
        assertEquals("Antwort auf data2", result2);
        
        // Verifiziere, dass der Service zweimal aufgerufen wurde
        verify(mockService, times(1)).request("data1");
        verify(mockService, times(1)).request("data2");
    }

    @Test
    public void testCacheInvalidation() throws ServiceException {
        // Konfiguriere Mock-Verhalten
        when(mockService.request("invalidateTest")).thenReturn("Antwort auf invalidateTest");
        
        // Erste Anfrage (Cache-Miss)
        String result1 = cachingProxy.request("invalidateTest");
        assertEquals("Antwort auf invalidateTest", result1);
        
        // Cache-Eintrag invalidieren
        cachingProxy.invalidateCacheEntry(CacheKey.forParameterizedRequest("invalidateTest"));
        
        // Erneute Anfrage (Cache-Miss nach Invalidierung)
        String result2 = cachingProxy.request("invalidateTest");
        assertEquals("Antwort auf invalidateTest", result2);
        
        // Verifiziere, dass der Service zweimal aufgerufen wurde
        verify(mockService, times(2)).request("invalidateTest");
    }

    @Test
    public void testCacheClear() throws ServiceException {
        // Konfiguriere Mock-Verhalten
        when(mockService.request("clearTest")).thenReturn("Antwort auf clearTest");
        
        // Erste Anfrage (Cache-Miss)
        String result1 = cachingProxy.request("clearTest");
        assertEquals("Antwort auf clearTest", result1);
        
        // Cache leeren
        cachingProxy.clearCache();
        
        // Erneute Anfrage (Cache-Miss nach Leerung)
        String result2 = cachingProxy.request("clearTest");
        assertEquals("Antwort auf clearTest", result2);
        
        // Verifiziere, dass der Service zweimal aufgerufen wurde
        verify(mockService, times(2)).request("clearTest");
    }

    @Test
    public void testComplexRequestCaching() throws ServiceException {
        // Konfiguriere Mock-Verhalten
        when(mockService.complexRequest(1, "complexData", new String[]{"option"}))
            .thenReturn("Komplexe Antwort");
        
        // Erste komplexe Anfrage (Cache-Miss)
        String result1 = cachingProxy.complexRequest(1, "complexData", new String[]{"option"});
        assertEquals("Komplexe Antwort", result1);
        
        // Zweite identische komplexe Anfrage (Cache-Hit)
        String result2 = cachingProxy.complexRequest(1, "complexData", new String[]{"option"});
        assertEquals("Komplexe Antwort", result2);
        
        // Verifiziere, dass der Service nur einmal aufgerufen wurde
        verify(mockService, times(1)).complexRequest(1, "complexData", new String[]{"option"});
    }

    @Test
    public void testCacheStatistics() throws ServiceException {
        // Konfiguriere Mock-Verhalten
        when(mockService.request("statsTest")).thenReturn("Antwort auf statsTest");
        
        // Erste Anfrage (Cache-Miss)
        cachingProxy.request("statsTest");
        
        // Zweite identische Anfrage (Cache-Hit)
        cachingProxy.request("statsTest");
        cachingProxy.request("statsTest");
        
        // Andere Anfrage (Cache-Miss)
        when(mockService.request("otherData")).thenReturn("Antwort auf otherData");
        cachingProxy.request("otherData");
        
        // Statistik prüfen
        String stats = cachingProxy.getCacheStatistics();
        assertNotNull("Statistik sollte nicht null sein", stats);
        assertTrue("Statistik sollte Treffer enthalten", stats.contains("Treffer: 2"));
        assertTrue("Statistik sollte Fehlschläge enthalten", stats.contains("Fehlschläge: 2"));
    }

    @Test(expected = ServiceException.class)
    public void testServiceExceptionPassthrough() throws ServiceException {
        // Konfiguriere Mock-Verhalten um eine Exception zu werfen
        when(mockService.request("exception")).thenThrow(
            new ServiceException("Testfehler", ServiceException.ErrorType.SERVICE_UNAVAILABLE));
        
        // Sollte die Exception durchreichen
        cachingProxy.request("exception");
    }
}
