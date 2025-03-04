package de.becke.vs.pattern.dependencyinjection.guice;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.becke.vs.pattern.dependencyinjection.common.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein Client, der die Dependency Injection mit Guice demonstriert.
 */
public class GuiceDIClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GuiceDIClient.class);
    
    // Feld-Injektion mit Named-Annotation
    @Inject
    @Named("primaryService")
    private RemoteService primaryService;
    
    // Feld-Injektion mit Named-Annotation
    @Inject
    @Named("secondaryService")
    private RemoteService secondaryService;
    
    // Weitere Services, die via Annotation injiziert werden
    @Inject
    @Named("slowService")
    private RemoteService slowService;
    
    @Inject
    @Named("unreliableService")
    private RemoteService unreliableService;
    
    // Privates Feld für den fehlertoleranten Service
    private final RemoteService faultTolerantService;
    
    /**
     * Erstellt einen neuen GuiceDIClient mit dem fehlertoleranten Service.
     * Diese Methode verwendet Konstruktor-Injektion.
     * 
     * @param faultTolerantService Der fehlertolerante Service
     */
    @Inject
    public GuiceDIClient(@Named("faultTolerantService") RemoteService faultTolerantService) {
        this.faultTolerantService = faultTolerantService;
        LOGGER.info("GuiceDIClient initialisiert mit fehlertoleranten Service: {}", 
                faultTolerantService.getServiceName());
    }
    
    /**
     * Führt Operationen mit verschiedenen injizierten Services aus.
     * 
     * @param parameter Der Eingabeparameter
     */
    public void executeServices(String parameter) {
        LOGGER.info("\n=== Ausführung mit verschiedenen Guice-injizierten Services ===");
        
        try {
            // Führe mit primärem Service aus
            String result1 = primaryService.executeOperation(parameter);
            LOGGER.info("Primärer Service Ergebnis: {}", result1);
            
            // Führe mit sekundärem Service aus
            String result2 = secondaryService.executeOperation(parameter);
            LOGGER.info("Sekundärer Service Ergebnis: {}", result2);
            
            // Führe mit langsamem Service aus
            String result3 = slowService.executeOperation(parameter);
            LOGGER.info("Langsamer Service Ergebnis: {}", result3);
            
            // Führe mit fehlertolerantem Service aus
            String result4 = faultTolerantService.executeOperation(parameter);
            LOGGER.info("Fehlertoleranter Service Ergebnis: {}", result4);
            
            // Versuche mit unzuverlässigem Service
            try {
                String result5 = unreliableService.executeOperation(parameter);
                LOGGER.info("Unzuverlässiger Service Ergebnis: {}", result5);
            } catch (Exception e) {
                LOGGER.error("Fehler beim unzuverlässigen Service: {}", e.getMessage());
            }
            
        } catch (Exception e) {
            LOGGER.error("Fehler bei der Ausführung der Services: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Gibt die Namen aller injizierten Services zurück.
     * 
     * @return Eine Zeichenkette mit allen Service-Namen
     */
    public String listAllServices() {
        StringBuilder sb = new StringBuilder();
        sb.append("Verfügbare Guice-Services:\n");
        sb.append("- Primär: ").append(primaryService.getServiceName()).append("\n");
        sb.append("- Sekundär: ").append(secondaryService.getServiceName()).append("\n");
        sb.append("- Langsam: ").append(slowService.getServiceName()).append("\n");
        sb.append("- Fehlertolerant: ").append(faultTolerantService.getServiceName()).append("\n");
        sb.append("- Unzuverlässig: ").append(unreliableService.getServiceName());
        return sb.toString();
    }
}