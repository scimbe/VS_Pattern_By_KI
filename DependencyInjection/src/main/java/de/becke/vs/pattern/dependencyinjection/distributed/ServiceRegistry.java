package de.becke.vs.pattern.dependencyinjection.distributed;

import de.becke.vs.pattern.dependencyinjection.common.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Eine Service-Registry für verteilte Systeme, die dynamische Service-Discovery unterstützt.
 * 
 * In einem realen verteilten System würde dies mit Technologien wie Consul, Eureka oder Zookeeper
 * implementiert werden. Diese vereinfachte Implementierung dient nur zur Demonstration.
 */
public class ServiceRegistry {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
    
    // Singleton-Instanz
    private static ServiceRegistry instance;
    
    // Map für registrierte Services, mit ihren Endpunkten und Metadaten
    private final Map<String, ServiceInfo> services = new ConcurrentHashMap<>();
    
    // Cache für dynamisch erstellte Proxy-Objekte
    private final Map<String, RemoteService> serviceProxies = new ConcurrentHashMap<>();
    
    /**
     * Privater Konstruktor für das Singleton-Pattern.
     */
    private ServiceRegistry() {
        LOGGER.info("ServiceRegistry initialisiert");
    }
    
    /**
     * Gibt die einzige Instanz der ServiceRegistry zurück.
     * 
     * @return Die ServiceRegistry-Instanz
     */
    public static synchronized ServiceRegistry getInstance() {
        if (instance == null) {
            instance = new ServiceRegistry();
        }
        return instance;
    }
    
    /**
     * Registriert einen Service mit Endpunkt und Metadaten.
     * 
     * @param serviceId Die ID des Services
     * @param endpoint Der Endpunkt des Services (z.B. URL)
     * @param metadata Zusätzliche Metadaten
     */
    public void registerService(String serviceId, String endpoint, Map<String, String> metadata) {
        LOGGER.info("Registriere Service '{}' mit Endpunkt '{}' und {} Metadaten", 
                serviceId, endpoint, metadata.size());
        
        ServiceInfo info = new ServiceInfo(endpoint, metadata);
        services.put(serviceId, info);
        
        // Lösche einen möglicherweise vorhandenen Proxy-Cache-Eintrag
        serviceProxies.remove(serviceId);
    }
    
    /**
     * Gibt einen Proxy für einen registrierten Service zurück.
     * Der Proxy wird dynamisch erstellt und im Cache gespeichert.
     * 
     * @param serviceId Die ID des Services
     * @return Ein Proxy für den Service
     * @throws ServiceDiscoveryException wenn der Service nicht gefunden wurde
     */
    public RemoteService getServiceProxy(String serviceId) {
        // Versuche zuerst, den Proxy aus dem Cache zu holen
        RemoteService cachedProxy = serviceProxies.get(serviceId);
        if (cachedProxy != null) {
            LOGGER.debug("Verwende zwischengespeicherten Proxy für Service '{}'", serviceId);
            return cachedProxy;
        }
        
        // Hole Service-Info aus der Registry
        ServiceInfo info = services.get(serviceId);
        if (info == null) {
            String message = "Service '" + serviceId + "' nicht in der Registry gefunden";
            LOGGER.error(message);
            throw new ServiceDiscoveryException(message);
        }
        
        // Erstelle einen neuen Proxy
        LOGGER.info("Erstelle neuen Proxy für Service '{}' mit Endpunkt '{}'", 
                serviceId, info.getEndpoint());
        
        RemoteService proxy = createServiceProxy(serviceId, info);
        
        // Speichere den Proxy im Cache
        serviceProxies.put(serviceId, proxy);
        
        return proxy;
    }
    
    /**
     * Erstellt einen Proxy für einen Service.
     * In einer realen Implementierung würde dies einen echten Remote-Proxy erstellen.
     * 
     * @param serviceId Die ID des Services
     * @param info Die Service-Informationen
     * @return Ein Proxy für den Service
     */
    private RemoteService createServiceProxy(String serviceId, ServiceInfo info) {
        // In einer realen Implementierung würde hier ein dynamischer Proxy erstellt,
        // der Remote-Aufrufe über das Netzwerk macht
        
        final String endpoint = info.getEndpoint();
        final Map<String, String> metadata = info.getMetadata();
        
        return new RemoteService() {
            @Override
            public String executeOperation() {
                LOGGER.info("RemoteProxy für '{}' führt Operation auf Endpunkt '{}' aus", 
                        serviceId, endpoint);
                simulateNetworkCall();
                return "Ergebnis von " + serviceId + " auf " + endpoint;
            }
            
            @Override
            public String executeOperation(String parameter) {
                LOGGER.info("RemoteProxy für '{}' führt Operation mit Parameter '{}' auf Endpunkt '{}' aus", 
                        serviceId, parameter, endpoint);
                simulateNetworkCall();
                return "Ergebnis von " + serviceId + " für Parameter '" + parameter + "' auf " + endpoint;
            }
            
            @Override
            public String getServiceName() {
                return serviceId + " (" + endpoint + ")";
            }
            
            private void simulateNetworkCall() {
                // Simuliert einen Netzwerkaufruf
                try {
                    // Verzögerung basierend auf Metadaten oder Standardwert
                    int latency = 100;
                    if (metadata.containsKey("latency")) {
                        latency = Integer.parseInt(metadata.get("latency"));
                    }
                    
                    Thread.sleep(latency);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
    }
    
    /**
     * Entfernt einen Service aus der Registry.
     * 
     * @param serviceId Die ID des zu entfernenden Services
     * @return true, wenn der Service entfernt wurde, sonst false
     */
    public boolean unregisterService(String serviceId) {
        LOGGER.info("Entferne Service '{}' aus der Registry", serviceId);
        
        ServiceInfo removed = services.remove(serviceId);
        serviceProxies.remove(serviceId);
        
        return removed != null;
    }
    
    /**
     * Gibt alle registrierten Service-IDs zurück.
     * 
     * @return Ein Array mit allen Service-IDs
     */
    public String[] getAllServiceIds() {
        return services.keySet().toArray(new String[0]);
    }
    
    /**
     * Interne Klasse zur Speicherung von Service-Informationen.
     */
    private static class ServiceInfo {
        private final String endpoint;
        private final Map<String, String> metadata;
        
        public ServiceInfo(String endpoint, Map<String, String> metadata) {
            this.endpoint = endpoint;
            this.metadata = new HashMap<>(metadata);
        }
        
        public String getEndpoint() {
            return endpoint;
        }
        
        public Map<String, String> getMetadata() {
            return new HashMap<>(metadata);
        }
    }
    
    /**
     * Exception-Klasse für Fehler bei der Service-Discovery.
     */
    public static class ServiceDiscoveryException extends RuntimeException {
        public ServiceDiscoveryException(String message) {
            super(message);
        }
        
        public ServiceDiscoveryException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}