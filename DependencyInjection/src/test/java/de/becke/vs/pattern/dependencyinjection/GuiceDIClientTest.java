package de.becke.vs.pattern.dependencyinjection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.becke.vs.pattern.dependencyinjection.common.RemoteService;
import de.becke.vs.pattern.dependencyinjection.guice.GuiceDIClient;
import de.becke.vs.pattern.dependencyinjection.guice.GuiceModule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Testklasse für den GuiceDIClient.
 */
public class GuiceDIClientTest {

    private GuiceDIClient guiceDIClient;
    private Injector injector;

    @Before
    public void setUp() {
        injector = Guice.createInjector(new GuiceModule());
        guiceDIClient = injector.getInstance(GuiceDIClient.class);
    }

    @Test
    public void testDependencyInjection() {
        assertNotNull("GuiceDIClient sollte nicht null sein", guiceDIClient);
        assertNotNull("PrimaryService sollte nicht null sein", guiceDIClient.listAllServices());
    }

    @Test
    public void testServiceUsage() {
        String parameter = "TestParameter";
        guiceDIClient.executeServices(parameter);

        RemoteService primaryService = injector.getInstance(Key.get(RemoteService.class, Names.named("primaryService")));
        RemoteService secondaryService = injector.getInstance(Key.get(RemoteService.class, Names.named("secondaryService")));
        RemoteService slowService = injector.getInstance(Key.get(RemoteService.class, Names.named("slowService")));
        RemoteService faultTolerantService = injector.getInstance(Key.get(RemoteService.class, Names.named("faultTolerantService")));
        RemoteService unreliableService = injector.getInstance(Key.get(RemoteService.class, Names.named("unreliableService")));

        assertEquals("Ergebnis von GuicePrimaryService für Parameter: " + parameter, primaryService.executeOperation(parameter));
        assertEquals("Ergebnis von GuiceSecondaryService für Parameter: " + parameter, secondaryService.executeOperation(parameter));
        assertEquals("Ergebnis von SlowGuiceService für Parameter: " + parameter, slowService.executeOperation(parameter));
        assertEquals("Ergebnis von GuicePrimaryService für Parameter: " + parameter, faultTolerantService.executeOperation(parameter));
        assertTrue("UnreliableService sollte gelegentlich Fehler werfen", unreliableService.executeOperation(parameter).contains("Ergebnis von UnreliableGuiceService für Parameter: " + parameter));
    }

    @Test
    public void testErrorHandling() {
        String parameter = "TestParameter";
        try {
            guiceDIClient.executeServices(parameter);
        } catch (Exception e) {
            assertTrue("Fehler beim unzuverlässigen Service sollte behandelt werden", e.getMessage().contains("Simulierter Fehler im UnreliableGuiceService"));
        }
    }
}
