package de.becke.vs.pattern.proxy.broker;

import de.becke.vs.pattern.proxy.common.RemoteService;
import de.becke.vs.pattern.proxy.common.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Ein Broker-Proxy, der als Vermittler zwischen Clients und einer Gruppe von Diensten fungiert.
 * 
 * Diese Implementierung verbirgt die Komplexität der Dienstlandschaft vor den Clients und
 * stellt eine einheitliche Schnittstelle für den Zugriff auf verschiedene Dienste bereit.
 * Der Broker verwaltet Dienste nach Kategorien und leitet Anfragen entsprechend weiter.
 */
public class BrokerProxy implements RemoteService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BrokerProxy.class);
    
    // Die verfügbaren Dienste, organisiert nach Kategorien
    private final Map<String, Set<ServiceProvider>> serviceProviders = new HashMap<>();
    
    // Die registrierten Clients
    private final Set<ClientInfo> registeredClients = new HashSet<>();
    
    /**
     * Erstellt einen neuen Broker-Proxy.
     */
    public BrokerProxy() {
        LOGGER.info("BrokerProxy initialisiert");
    }
    
    /**
     * Registriert einen neuen Client beim Broker.
     * 
     * @param clientInfo Die Client-Informationen
     * @return Eine eindeutige Client-ID
     */
    public String registerClient(ClientInfo clientInfo) {
        registeredClients.add(clientInfo);
        LOGGER.info("Client '{}' mit Berechtigungen {} registriert",
                clientInfo.getClientId(), clientInfo.getPermissions());
        return clientInfo.getClientId();
    }
    
    /**
     * Registriert einen Dienstanbieter für eine bestimmte Kategorie.
     * 
     * @param category Die Dienstkategorie
     * @param provider Der Dienstanbieter
     */
    public void registerServiceProvider(String category, ServiceProvider provider) {
        serviceProviders.computeIfAbsent(category, k -> new HashSet<>()).add(provider);
        LOGGER.info("Dienstanbieter '{}' für Kategorie '{}' registriert",
                provider.getProviderId(), category);
    }
    
    /**
     * Entfernt einen Dienstanbieter aus einer bestimmten Kategorie.
     * 
     * @param category Die Dienstkategorie
     * @param providerId Die ID des Dienstanbieters
     * @return true, wenn der Anbieter erfolgreich entfernt wurde, sonst false
     */
    public boolean unregisterServiceProvider(String category, String providerId) {
        Set<ServiceProvider> providers = serviceProviders.get(category);
        
        if (providers == null) {
            LOGGER.warn("Keine Dienstanbieter für Kategorie '{}' gefunden", category);
            return false;
        }
        
        boolean removed = providers.removeIf(provider -> provider.getProviderId().equals(providerId));
        
        if (removed) {
            LOGGER.info("Dienstanbieter '{}' aus Kategorie '{}' entfernt", providerId, category);
            
            // Entferne die Kategorie, wenn keine Anbieter mehr vorhanden sind
            if (providers.isEmpty()) {
                serviceProviders.remove(category);
                LOGGER.info("Kategorie '{}' entfernt, da keine Anbieter mehr vorhanden sind", category);
            }
        } else {
            LOGGER.warn("Dienstanbieter '{}' in Kategorie '{}' nicht gefunden", providerId, category);
        }
        
        return removed;
    }
    
    /**
     * Führt eine Anfrage an einen Dienst in einer bestimmten Kategorie aus.
     * 
     * @param clientId Die ID des anfragenden Clients
     * @param category Die Dienstkategorie
     * @param serviceName Der Name des angeforderten Dienstes
     * @param parameter Der Parameter für die Anfrage
     * @return Das Ergebnis der Anfrage
     * @throws ServiceException Wenn die Anfrage nicht ausgeführt werden kann
     */
    public String invokeService(String clientId, String category, String serviceName, String parameter)
            throws ServiceException {
        LOGGER.info("Client '{}' fordert Dienst '{}' in Kategorie '{}' an", clientId, serviceName, category);
        
        // Überprüfe, ob der Client registriert ist
        ClientInfo client = findClient(clientId);
        if (client == null) {
            LOGGER.error("Client '{}' ist nicht registriert", clientId);
            throw new ServiceException("Client nicht registriert: " + clientId,
                    ServiceException.ErrorType.AUTHENTICATION_FAILED);
        }
        
        // Überprüfe, ob der Client Zugriff auf die Kategorie hat
        if (!client.hasPermission(category)) {
            LOGGER.error("Client '{}' hat keine Berechtigung für Kategorie '{}'", clientId, category);
            throw new ServiceException("Keine Berechtigung für Kategorie: " + category,
                    ServiceException.ErrorType.UNAUTHORIZED);
        }
        
        // Finde Dienstanbieter für die angegebene Kategorie
        Set<ServiceProvider> providers = serviceProviders.get(category);
        if (providers == null || providers.isEmpty()) {
            LOGGER.error("Keine Dienstanbieter für Kategorie '{}' gefunden", category);
            throw new ServiceException("Keine Dienstanbieter für Kategorie: " + category,
                    ServiceException.ErrorType.SERVICE_UNAVAILABLE);
        }
        
        // Suche nach einem Anbieter, der den angeforderten Dienst anbietet
        for (ServiceProvider provider : providers) {
            if (provider.providesService(serviceName)) {
                LOGGER.info("Dienstanbieter '{}' gefunden für Dienst '{}'", provider.getProviderId(), serviceName);
                
                try {
                    // Führe den Dienst aus
                    RemoteService service = provider.getService(serviceName);
                    String result = service.request(parameter);
                    
                    LOGGER.info("Dienst '{}' in Kategorie '{}' erfolgreich ausgeführt", serviceName, category);
                    return result;
                } catch (ServiceException e) {
                    LOGGER.error("Fehler bei der Ausführung von Dienst '{}': {}", serviceName, e.getMessage());
                    throw e;
                }
            }
        }
        
        LOGGER.error("Kein Anbieter gefunden, der Dienst '{}' in Kategorie '{}' anbietet", serviceName, category);
        throw new ServiceException("Dienst nicht gefunden: " + serviceName,
                ServiceException.ErrorType.RESOURCE_NOT_FOUND);
    }
    
    /**
     * Findet einen registrierten Client anhand seiner ID.
     * 
     * @param clientId Die Client-ID
     * @return Die Client-Informationen oder null, wenn der Client nicht gefunden wurde
     */
    private ClientInfo findClient(String clientId) {
        for (ClientInfo client : registeredClients) {
            if (client.getClientId().equals(clientId)) {
                return client;
            }
        }
        return null;
    }
    
    /**
     * Gibt alle verfügbaren Dienste in einer bestimmten Kategorie zurück.
     * 
     * @param category Die Dienstkategorie
     * @return Eine Map mit Dienstnamen als Schlüssel und Anbieter-IDs als Werte
     */
    public Map<String, Set<String>> getAvailableServices(String category) {
        Set<ServiceProvider> providers = serviceProviders.get(category);
        Map<String, Set<String>> services = new HashMap<>();
        
        if (providers != null) {
            for (ServiceProvider provider : providers) {
                for (String serviceName : provider.getAvailableServices()) {
                    services.computeIfAbsent(serviceName, k -> new HashSet<>())
                            .add(provider.getProviderId());
                }
            }
        }
        
        return services;
    }
    
    /**
     * Gibt alle verfügbaren Dienstkategorien zurück.
     * 
     * @return Die verfügbaren Kategorien
     */
    public Set<String> getAvailableCategories() {
        return serviceProviders.keySet();
    }
    
    /**
     * Gibt alle registrierten Clients zurück.
     * 
     * @return Die registrierten Clients
     */
    public Collection<ClientInfo> getRegisteredClients() {
        return new HashSet<>(registeredClients);
    }
    
    // RemoteService-Methoden, die für die Broker-Proxy-Funktionalität verwendet werden
    
    @Override
    public String request() throws ServiceException {
        LOGGER.info("BrokerProxy: Einfache Anfrage empfangen");
        throw new ServiceException("Einfache Anfragen werden nicht unterstützt, bitte verwenden Sie invokeService",
                ServiceException.ErrorType.INVALID_REQUEST);
    }
    
    @Override
    public String request(String parameter) throws ServiceException {
        LOGGER.info("BrokerProxy: Anfrage mit Parameter empfangen: '{}'", parameter);
        throw new ServiceException("Parametrisierte Anfragen werden nicht unterstützt, bitte verwenden Sie invokeService",
                ServiceException.ErrorType.INVALID_REQUEST);
    }
    
    @Override
    public String complexRequest(int id, String data, String[] options) throws ServiceException {
        LOGGER.info("BrokerProxy: Komplexe Anfrage empfangen (ID: {})", id);
        
        // Interpretiere die komplexe Anfrage als Service-Invocation
        if (options.length >= 3) {
            String clientId = options[0];
            String category = options[1];
            String serviceName = options[2];
            
            LOGGER.info("Interpretiere komplexe Anfrage als Service-Invocation: Client={}, Kategorie={}, Dienst={}",
                    clientId, category, serviceName);
            
            return invokeService(clientId, category, serviceName, data);
        }
        
        throw new ServiceException("Ungültiges Format für komplexe Anfrage, " +
                "erwartet: [clientId, category, serviceName] als options",
                ServiceException.ErrorType.INVALID_REQUEST);
    }
}