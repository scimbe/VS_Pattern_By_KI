package de.becke.vs.pattern.dependencyinjection.spring;

import de.becke.vs.pattern.dependencyinjection.common.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Ein Client, der die Dependency Injection mit Spring demonstriert.
 */
@Component
public class SpringDIClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringDIClient.class);
    
    // Konstruktor-Injektion für den primären Service
    private final RemoteService primaryService;
    
    // Feld-Injektion für den sekundären Service
    @Autowired
    @Qualifier("secondaryService")
    private RemoteService secondaryService;
    
    // Weitere Services, die via Annotation injiziert werden
    @Autowired
    @Qualifier("slowService")
    private RemoteService slowService;
    
    @Autowired
    @Qualifier("unreliableService")
    private RemoteService unreliableService;
    
    // Privates Feld für den fehlertoleranten Service, das via Setter injiziert wird
    private RemoteService faultTolerantService;
    
    /**
     * Erstellt einen neuen SpringDIClient mit dem primären Service.
     * Diese Methode verwendet Konstruktor-Injektion.
     * 
     * @param primaryService Der primäre Service (automatisch vom Spring Container injiziert)
     */
    @Autowired
    public SpringDIClient(RemoteService primaryService) {
        this.primaryService = primaryService;
        LOGGER.info("SpringDIClient initialisiert mit primärem Service: {}", primaryService.getServiceName());
    }
    
    /**
     * Setzt den fehlertoleranten Service.
     * Diese Methode verwendet Setter-Injektion.
     * 
     * @param faultTolerantService Der fehlertolerante Service
     */
    @Autowired
    public void setFaultTolerantService(RemoteService faultTolerantService) {
        this.faultTolerantService = faultTolerantService;
        LOGGER.info("Fehlertoleranter Service gesetzt: {}", faultTolerantService.getServiceName());
    }
    
    /**
     * Führt Operationen mit verschiedenen injizierten Services aus.
     * 
     * @param parameter Der Eingabeparameter
     */
    public void executeServices(String parameter) {
        LOGGER.info("\n=== Ausführung mit verschiedenen Services ===");
        
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
        sb.append("Verfügbare Services:\n");
        sb.append("- Primär: ").append(primaryService.getServiceName()).append("\n");
        sb.append("- Sekundär: ").append(secondaryService.getServiceName()).append("\n");
        sb.append("- Langsam: ").append(slowService.getServiceName()).append("\n");
        sb.append("- Fehlertolerant: ").append(faultTolerantService.getServiceName()).append("\n");
        sb.append("- Unzuverlässig: ").append(unreliableService.getServiceName());
        return sb.toString();
    }
}