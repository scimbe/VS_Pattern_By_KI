package de.becke.vs.pattern.proxy;

import de.becke.vs.pattern.proxy.forward.ForwardProxy;
import de.becke.vs.pattern.proxy.common.RemoteService;
import de.becke.vs.pattern.proxy.common.ServiceException;
import de.becke.vs.pattern.proxy.forward.AccessController;
import de.becke.vs.pattern.proxy.forward.ContentFilter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ForwardProxyTest {

    private ForwardProxy forwardProxy;
    private RemoteService mockService;
    private AccessController mockAccessController;
    private ContentFilter mockContentFilter;

    @Before
    public void setUp() {
        // Erstelle Mock-Objekte statt echter Implementierungen
        mockService = mock(RemoteService.class);
        mockAccessController = mock(AccessController.class);
        mockContentFilter = mock(ContentFilter.class);
        
        // Konfiguriere Mock-Verhalten
        try {
            when(mockService.request(anyString())).thenReturn("Mock-Antwort von TestService");
            when(mockService.request(isNull())).thenReturn("Mock-Antwort für null");
            when(mockService.request("exception")).thenThrow(
                new ServiceException("Simulierter Fehler", ServiceException.ErrorType.SERVICE_UNAVAILABLE));
                
            // Wichtig: Explizites Verhalten für null-Parameter definieren
            when(mockAccessController.checkAccess(isNull())).thenReturn(true);
            when(mockAccessController.checkAccess(anyString())).thenReturn(true);
            when(mockAccessController.checkAccess("restricted")).thenReturn(false);
            
            when(mockContentFilter.filterContent(isNull())).thenReturn(null);
            when(mockContentFilter.filterContent(anyString())).thenAnswer(i -> i.getArgument(0));
            when(mockContentFilter.isContentAllowed(anyString())).thenReturn(true);
            when(mockContentFilter.isContentAllowed(isNull())).thenReturn(true);
        } catch (ServiceException e) {
            fail("Exception bei Test-Setup: " + e.getMessage());
        }
        
        forwardProxy = new ForwardProxy(mockService, mockAccessController, mockContentFilter, true);
    }

    @Test
    public void testForwardRequest() throws ServiceException {
        String result = forwardProxy.request("test");
        assertNotNull("Result should not be null", result);
        assertTrue("Result should contain test response", result.contains("Mock-Antwort"));
        
        // Verifiziere, dass die erwarteten Methoden aufgerufen wurden
        verify(mockAccessController).checkAccess("test");
        verify(mockContentFilter).filterContent("test");
        verify(mockService).request("test");
    }

    @Test
    public void testForwardRequestWithNullParameter() throws ServiceException {
        String result = forwardProxy.request(null);
        assertNotNull("Result should not be null", result);
        
        // Verifiziere, dass die erwarteten Methoden aufgerufen wurden
        verify(mockAccessController).checkAccess(isNull());
        verify(mockService).request(isNull());
    }

    @Test(expected = ServiceException.class)
    public void testForwardRequestWithException() throws ServiceException {
        forwardProxy.request("exception");
        // Hier sollte eine Exception geworfen werden
    }
    
    @Test(expected = ServiceException.class)
    public void testForwardRequestWithRestrictedAccess() throws ServiceException {
        forwardProxy.request("restricted");
        // Hier sollte eine Unauthorized-Exception geworfen werden
    }
    
    @Test
    public void testComplexRequest() throws ServiceException {
        when(mockService.complexRequest(anyInt(), anyString(), any())).thenReturn("Komplexe Antwort");
        when(mockAccessController.checkComplexAccess(anyInt(), anyString(), any())).thenReturn(true);
        
        String result = forwardProxy.complexRequest(1, "data", new String[]{"option1", "option2"});
        
        assertNotNull("Result should not be null", result);
        assertEquals("Result should match expected output", "Komplexe Antwort", result);
        
        verify(mockAccessController).checkComplexAccess(1, "data", new String[]{"option1", "option2"});
        verify(mockService).complexRequest(eq(1), eq("data"), any());
    }
}
