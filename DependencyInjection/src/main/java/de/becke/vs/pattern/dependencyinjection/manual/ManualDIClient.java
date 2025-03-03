package de.becke.vs.pattern.dependencyinjection.manual;

import de.becke.vs.pattern.dependencyinjection.common.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eine Beispielklasse, die demonstriert, wie Dependency Injection manuell
 * implementiert werden kann, ohne ein Framework zu verwenden.
 */
public class ManualDIClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ManualDIClient.class);
    
    // Konstruktor-Injektion: Die Abhängigkeit wird über den Konstruktor injiziert
    private final RemoteService service;
    
    // Optionale Abhängigkeit, die über Setter-Injektion bereitgestellt werden kann
    private RemoteService secondaryService;
    
    /**
     * Erstellt einen neuen Client mit dem angegebenen Service.
     * Dies ist ein Beispiel für Konstruktor-Injektion.
     * 
     * @param service Der zu verwendende Remote-Service
     */
    public ManualDIClient(RemoteService service) {
        if (service == null) {
            throw new IllegalArgumentException("Service darf nicht null sein");
        }
        this.service = service;
        LOGGER.info("ManualDIClient initialisiert mit Service: {}", service.getServiceName());
    }
    
    /**
     * Setzt einen sekundären Service.
     * Dies ist ein Beispiel für Setter-Injektion.
     * 
     * @param secondaryService Der sekundäre Service
     */
    public void setSecondaryService(RemoteService secondaryService) {
        this.secondaryService = secondaryService;
        LOGGER.info("Sekundärer Service gesetzt: {}", 
                secondaryService != null ? secondaryService.getServiceName() : "null");
    }
    
    /**
     * Führt eine Operation mit dem primären Service aus.
     * 
     * @param parameter Der Eingabeparameter
     * @return Das Ergebnis der Operation
     */
    public String executeOperation(String parameter) {
        LOGGER.info("Führe Operation mit primärem Service aus");
        return service.executeOperation(parameter);
    }
    
    /**
     * Führt eine Operation mit dem sekundären Service aus, falls verfügbar.
     * Ansonsten wird der primäre Service verwendet.
     * 
     * @param parameter Der Eingabeparameter
     * @return Das Ergebnis der Operation
     */
    public String executeSecondaryOperation(String parameter) {
        if (secondaryService != null) {
            LOGGER.info("Führe Operation mit sekundärem Service aus");
            return secondaryService.executeOperation(parameter);
        } else {
            LOGGER.info("Kein sekundärer Service verfügbar, verwende primären Service");
            return service.executeOperation(parameter);
        }
    }
    
    /**
     * Gibt den Namen des primären Services zurück.
     * 
     * @return Der Name des primären Services
     */
    public String getPrimaryServiceName() {
        return service.getServiceName();
    }
    
    /**
     * Gibt den Namen des sekundären Services zurück oder "Nicht verfügbar",
     * wenn kein sekundärer Service gesetzt ist.
     * 
     * @return Der Name des sekundären Services oder "Nicht verfügbar"
     */
    public String getSecondaryServiceName() {
        return secondaryService != null ? secondaryService.getServiceName() : "Nicht verfügbar";
    }
}