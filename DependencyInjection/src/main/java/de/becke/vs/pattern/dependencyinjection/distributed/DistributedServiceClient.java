package de.becke.vs.pattern.dependencyinjection.distributed;

import de.becke.vs.pattern.dependencyinjection.common.RemoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Ein Beispiel-Client für verteilte Dienste mit dynamischer Service-Discovery.
 * 
 * Diese Klasse kombiniert den Service Locator Ansatz mit Dependency Injection
 * für eine verteilte Umgebung. Der Client verwendet die ServiceRegistry
 * zur dynamischen Entdeckung von Diensten zur Laufzeit.
 */
public class DistributedServiceClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedServiceClient.class);
    
    // Die Service-Registry zur dynamischen Dienstentdeckung
    private final ServiceRegistry registry;
    
    /**
     * Erstellt einen neuen Client mit der Standard-ServiceRegistry.
     */
    public DistributedServiceClient() {
        this(ServiceRegistry.getInstance());
    }
    
    /**
     * Erstellt einen neuen Client mit der angegebenen ServiceRegistry.
     * Dies ermöglicht Dependency Injection für Tests.
     * 
     * @param registry Die zu verwendende ServiceRegistry
     */
    public DistributedServiceClient(ServiceRegistry registry) {
        this.registry = registry;
        LOGGER.info("DistributedServiceClient initialisiert");
    }
    
    /**
     * Führt eine Operation mit dem angegebenen Service aus.
     * 
     * @param serviceId Die ID des zu verwendenden Services
     * @param parameter Der Eingabeparameter für die Operation
     * @return Das Ergebnis der Operation
     * @throws ServiceRegistry.ServiceDiscoveryException wenn der Service nicht gefunden wurde
     */
    public String executeServiceOperation(String serviceId, String parameter) {
        LOGGER.info("Führe Operation mit verteiltem Service '{}' aus", serviceId);
        
        // Hole einen Proxy für den Service aus der Registry
        RemoteService serviceProxy = registry.getServiceProxy(serviceId);
        
        // Führe die Operation aus
        return serviceProxy.executeOperation(parameter);
    }
    
    /**
     * Registriert einen Test-Service in der Registry.
     * 
     * @param serviceId Die ID des Services
     * @param host Der Hostname oder die IP-Adresse
     * @param port Der Port
     * @param latency Die simulierte Netzwerklatenz in Millisekunden
     */
    public void registerTestService(String serviceId, String host, int port, int latency) {
        String endpoint = String.format("http://%s:%d/api/%s", host, port, serviceId);
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("latency", String.valueOf(latency));
        metadata.put("protocol", "http");
        metadata.put("version", "1.0");
        
        LOGGER.info("Registriere Test-Service '{}' auf '{}'", serviceId, endpoint);
        registry.registerService(serviceId, endpoint, metadata);
    }
    
    /**
     * Listet alle verfügbaren Services auf.
     * 
     * @return Eine Zeichenkette mit Informationen zu allen verfügbaren Services
     */
    public String listAvailableServices() {
        String[] serviceIds = registry.getAllServiceIds();
        
        StringBuilder sb = new StringBuilder();
        sb.append("Verfügbare verteilte Services (").append(serviceIds.length).append("):\n");
        
        for (String serviceId : serviceIds) {
            try {
                RemoteService proxy = registry.getServiceProxy(serviceId);
                sb.append("- ").append(serviceId).append(": ").append(proxy.getServiceName()).append("\n");
            } catch (ServiceRegistry.ServiceDiscoveryException e) {
                sb.append("- ").append(serviceId).append(": NICHT VERFÜGBAR\n");
            }
        }
        
        return sb.toString();
    }
}