package de.becke.vs.pattern.dependencyinjection.servicelocator;

import de.becke.vs.pattern.dependencyinjection.common.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Eine Implementierung des Service Locator Patterns, das als Alternative zu
 * Dependency Injection verwendet werden kann.
 * 
 * Das Service Locator Pattern ist ein Entwurfsmuster, das einen zentralen Registry
 * für Dienste bereitstellt, den Clients verwenden können, um Dienste abzurufen.
 * Es bietet mehr Kontrolle für den Client, verletzt aber auch das Dependency
 * Inversion Principle, da Clients jetzt direkt vom Service Locator abhängen.
 */
public class ServiceLocator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLocator.class);
    
    // Singleton-Instanz des ServiceLocator
    private static ServiceLocator instance;
    
    // Registry der Services
    private final Map<String, RemoteService> services = new HashMap<>();
    
    /**
     * Privater Konstruktor für das Singleton-Pattern.
     */
    private ServiceLocator() {
        LOGGER.info("ServiceLocator initialisiert");
    }
    
    /**
     * Gibt die einzige Instanz des ServiceLocator zurück.
     * 
     * @return Die ServiceLocator-Instanz
     */
    public static synchronized ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator();
        }
        return instance;
    }
    
    /**
     * Registriert einen Service mit dem angegebenen Namen.
     * 
     * @param serviceName Der Name des Services
     * @param service Der zu registrierende Service
     */
    public void registerService(String serviceName, RemoteService service) {
        LOGGER.info("Registriere Service '{}' mit Namen '{}'", service.getServiceName(), serviceName);
        services.put(serviceName, service);
    }
    
    /**
     * Gibt einen registrierten Service zurück.
     * 
     * @param serviceName Der Name des abzurufenden Services
     * @return Der registrierte Service
     * @throws ServiceNotFoundException wenn kein Service mit dem angegebenen Namen gefunden wurde
     */
    public RemoteService getService(String serviceName) {
        RemoteService service = services.get(serviceName);
        
        if (service == null) {
            String message = "Service mit Namen '" + serviceName + "' nicht gefunden";
            LOGGER.error(message);
            throw new ServiceNotFoundException(message);
        }
        
        LOGGER.info("Service '{}' abgerufen", service.getServiceName());
        return service;
    }
    
    /**
     * Entfernt einen Service aus der Registry.
     * 
     * @param serviceName Der Name des zu entfernenden Services
     * @return true, wenn der Service entfernt wurde, sonst false
     */
    public boolean unregisterService(String serviceName) {
        if (services.containsKey(serviceName)) {
            RemoteService removed = services.remove(serviceName);
            LOGGER.info("Service '{}' mit Namen '{}' entfernt", removed.getServiceName(), serviceName);
            return true;
        }
        
        LOGGER.warn("Konnte Service mit Namen '{}' nicht entfernen, da nicht gefunden", serviceName);
        return false;
    }
    
    /**
     * Gibt alle registrierten Service-Namen zurück.
     * 
     * @return Ein Set mit allen Service-Namen
     */
    public Set<String> getAllServiceNames() {
        return services.keySet();
    }
    
    /**
     * Prüft, ob ein Service mit dem angegebenen Namen existiert.
     * 
     * @param serviceName Der zu prüfende Service-Name
     * @return true, wenn der Service existiert, sonst false
     */
    public boolean hasService(String serviceName) {
        return services.containsKey(serviceName);
    }
    
    /**
     * Gibt die Anzahl der registrierten Services zurück.
     * 
     * @return Die Anzahl der Services
     */
    public int getServiceCount() {
        return services.size();
    }
    
    /**
     * Löscht alle registrierten Services.
     */
    public void clearServices() {
        LOGGER.info("Lösche alle Services aus dem ServiceLocator");
        services.clear();
    }
}