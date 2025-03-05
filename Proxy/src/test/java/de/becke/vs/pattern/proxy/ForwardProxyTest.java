package de.becke.vs.pattern.proxy;

import de.becke.vs.pattern.proxy.forward.ForwardProxy;
import de.becke.vs.pattern.proxy.common.RealRemoteService;
import de.becke.vs.pattern.proxy.common.ServiceException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ForwardProxyTest {

    private ForwardProxy forwardProxy;

    @Before
    public void setUp() {
        RealRemoteService realService = new RealRemoteService("TestService");
        forwardProxy = new ForwardProxy(realService, null, null, false);
    }

    @Test
    public void testForwardRequest() {
        try {
            String result = forwardProxy.request("test");
            assertNotNull("Result should not be null", result);
            assertTrue("Result should contain 'TestService'", result.contains("TestService"));
        } catch (ServiceException e) {
            fail("ServiceException should not be thrown");
        }
    }

    @Test
    public void testForwardRequestWithNullParameter() {
        try {
            String result = forwardProxy.request(null);
            assertNotNull("Result should not be null", result);
            assertTrue("Result should contain 'TestService'", result.contains("TestService"));
        } catch (ServiceException e) {
            fail("ServiceException should not be thrown");
        }
    }

    @Test
    public void testForwardRequestWithException() {
        try {
            forwardProxy.request("exception");
            fail("ServiceException should be thrown");
        } catch (ServiceException e) {
            assertNotNull("Exception should not be null", e);
        }
    }
}
