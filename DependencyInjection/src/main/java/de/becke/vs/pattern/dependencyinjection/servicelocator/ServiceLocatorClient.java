package de.becke.vs.pattern.dependencyinjection.servicelocator;

import de.becke.vs.pattern.dependencyinjection.common.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein Beispiel-Client, der den ServiceLocator verwendet.
 * 
 * Diese Klasse demonstriert die Verwendung des Service Locator Patterns anstelle
 * von Dependency Injection. Im Gegensatz zu DI, wo Abhängigkeiten explizit
 * übergeben werden, holt der Client hier die benötigten Services selbst.
 */
public class ServiceLocatorClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLocatorClient.class);
    
    // Der Service Locator wird direkt vom Client abgerufen
    private final ServiceLocator serviceLocator;
    
    /**
     * Erstellt einen neuen ServiceLocatorClient.
     */
    public ServiceLocatorClient() {
        // Der Client holt sich den ServiceLocator selbst
        this.serviceLocator = ServiceLocator.getInstance();
        LOGGER.info("ServiceLocatorClient initialisiert");
    }
    
    /**
     * Führt eine Operation mit dem angegebenen Service aus.
     * 
     * @param serviceName Der Name des zu verwendenden Services
     * @param parameter Der Eingabeparameter für die Operation
     * @return Das Ergebnis der Operation
     * @throws ServiceNotFoundException wenn der angeforderte Service nicht gefunden wurde
     */
    public String executeServiceOperation(String serviceName, String parameter) {
        LOGGER.info("Führe Operation mit Service '{}' aus", serviceName);
        
        // Hole den Service vom ServiceLocator
        RemoteService service = serviceLocator.getService(serviceName);
        
        // Führe die Operation aus
        String result = service.executeOperation(parameter);
        LOGGER.info("Operation mit Service '{}' abgeschlossen: {}", serviceName, result);
        
        return result;
    }
    
    /**
     * Registriert einen neuen Service beim ServiceLocator.
     * 
     * @param serviceName Der Name des Services
     * @param service Der zu registrierende Service
     */
    public void registerNewService(String serviceName, RemoteService service) {
        LOGGER.info("Registriere neuen Service '{}'", serviceName);
        serviceLocator.registerService(serviceName, service);
    }
    
    /**
     * Gibt alle verfügbaren Service-Namen zurück.
     * 
     * @return Eine Zeichenkette mit allen Service-Namen
     */
    public String listAvailableServices() {
        StringBuilder sb = new StringBuilder();
        sb.append("Verfügbare Services (").append(serviceLocator.getServiceCount()).append("):\n");
        
        for (String serviceName : serviceLocator.getAllServiceNames()) {
            try {
                RemoteService service = serviceLocator.getService(serviceName);
                sb.append("- ").append(serviceName).append(": ").append(service.getServiceName()).append("\n");
            } catch (ServiceNotFoundException e) {
                sb.append("- ").append(serviceName).append(": NICHT VERFÜGBAR\n");
            }
        }
        
        return sb.toString();
    }
}