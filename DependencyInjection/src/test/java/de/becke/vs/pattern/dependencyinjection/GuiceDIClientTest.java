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
import com.google.inject.name.Names;
import com.google.inject.Key;

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
        
        assertEquals("Ergebnis von GuicePrimaryService für Parameter: " + parameter, primaryService.executeOperation(parameter));
        assertEquals("Ergebnis von GuiceSecondaryService für Parameter: " + parameter, secondaryService.executeOperation(parameter));
        assertEquals("Ergebnis von SlowGuiceService für Parameter: " + parameter, slowService.executeOperation(parameter));
        assertEquals("Ergebnis von GuicePrimaryService für Parameter: " + parameter, faultTolerantService.executeOperation(parameter));
        
        // Entfernt Assertion für unreliableService, da dieser gelegentlich Fehler wirft
    }

    @Test
    public void testErrorHandling() {
        String parameter = "TestParameter";
        try {
            // Rufen Sie den Unreliable-Service wiederholt auf, bis er einen Fehler wirft
            // oder brechen Sie nach einer bestimmten Anzahl von Versuchen ab
            RemoteService unreliableService = injector.getInstance(
                Key.get(RemoteService.class, Names.named("unreliableService")));
            
            boolean errorThrown = false;
            for (int i = 0; i < 10; i++) {
                try {
                    String result = unreliableService.executeOperation(parameter);
                    // Wenn kein Fehler, prüfen, ob die Ausgabe korrekt ist
                    assertTrue("Unreliable Service Ergebnis sollte korrekt sein",
                        result.contains("Ergebnis von UnreliableGuiceService für Parameter: " + parameter));
                } catch (RuntimeException e) {
                    // Fehler gefangen, Test erfolgreich
                    assertTrue("Fehler sollte die erwartete Meldung enthalten",
                        e.getMessage().contains("Simulierter Fehler im UnreliableGuiceService"));
                    errorThrown = true;
                    break;
                }
            }
            
            // Wir erwarten, dass irgendwann ein Fehler aufgetreten ist
            assertTrue("Der unzuverlässige Service sollte einen Fehler werfen", errorThrown);
            
        } catch (Exception e) {
            assertTrue("Fehler beim unzuverlässigen Service sollte behandelt werden",
                e.getMessage().contains("Simulierter Fehler im UnreliableGuiceService"));
        }
    }
}