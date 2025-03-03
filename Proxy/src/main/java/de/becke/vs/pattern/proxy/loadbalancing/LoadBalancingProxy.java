package de.becke.vs.pattern.proxy.loadbalancing;

import de.becke.vs.pattern.proxy.common.RemoteService;
import de.becke.vs.pattern.proxy.common.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ein Load-Balancing-Proxy, der Anfragen auf mehrere Dienste verteilt.
 * 
 * Diese Implementierung verteilt Anfragen auf mehrere RemoteServices und
 * bietet verschiedene Lastverteilungsstrategien wie Round Robin, Random
 * und Least Connections.
 */
public class LoadBalancingProxy implements RemoteService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalancingProxy.class);
    
    /**
     * Strategie für die Lastverteilung.
     */
    public enum Strategy {
        ROUND_ROBIN,  // Reihum-Verteilung
        RANDOM,       // Zufällige Verteilung
        LEAST_CONNECTIONS // Verteilung zum Dienst mit den wenigsten aktiven Verbindungen
    }
    
    private final List<RemoteService> backendServices;
    private final Strategy loadBalancingStrategy;
    private final Random random = new Random();
    private final AtomicInteger roundRobinCounter = new AtomicInteger(0);
    private final Map<RemoteService, AtomicInteger> activeConnections = new ConcurrentHashMap<>();
    
    /**
     * Erstellt einen Load-Balancing-Proxy mit der angegebenen Strategie.
     * 
     * @param services Die Backend-Dienste, auf die die Last verteilt werden soll
     * @param strategy Die zu verwendende Lastverteilungsstrategie
     */
    public LoadBalancingProxy(List<RemoteService> services, Strategy strategy) {
        if (services == null || services.isEmpty()) {
            throw new IllegalArgumentException("Es muss mindestens ein Backend-Dienst angegeben werden");
        }
        
        this.backendServices = new ArrayList<>(services);
        this.loadBalancingStrategy = strategy;
        
        // Initialisiere die aktiven Verbindungen für jeden Dienst
        for (RemoteService service : backendServices) {
            activeConnections.put(service, new AtomicInteger(0));
        }
        
        LOGGER.info("LoadBalancingProxy initialisiert mit {} Backend-Diensten und Strategie {}",
                backendServices.size(), strategy);
    }
    
    @Override
    public String request() throws ServiceException {
        LOGGER.info("LoadBalancingProxy: Einfache Anfrage empfangen");
        
        // Wähle einen Backend-Dienst aus
        RemoteService selectedService = selectService();
        
        try {
            // Erhöhe den Zähler für aktive Verbindungen
            activeConnections.get(selectedService).incrementAndGet();
            
            LOGGER.info("LoadBalancingProxy: Leite Anfrage an Backend-Dienst weiter");
            return selectedService.request();
        } finally {
            // Verringere den Zähler für aktive Verbindungen
            activeConnections.get(selectedService).decrementAndGet();
        }
    }
    
    @Override
    public String request(String parameter) throws ServiceException {
        LOGGER.info("LoadBalancingProxy: Anfrage mit Parameter '{}' empfangen", parameter);
        
        // Wähle einen Backend-Dienst aus
        RemoteService selectedService = selectService();
        
        try {
            // Erhöhe den Zähler für aktive Verbindungen
            activeConnections.get(selectedService).incrementAndGet();
            
            LOGGER.info("LoadBalancingProxy: Leite Anfrage mit Parameter an Backend-Dienst weiter");
            return selectedService.request(parameter);
        } finally {
            // Verringere den Zähler für aktive Verbindungen
            activeConnections.get(selectedService).decrementAndGet();
        }
    }
    
    @Override
    public String complexRequest(int id, String data, String[] options) throws ServiceException {
        LOGGER.info("LoadBalancingProxy: Komplexe Anfrage empfangen (ID: {})", id);
        
        // Wähle einen Backend-Dienst aus
        RemoteService selectedService = selectService();
        
        try {
            // Erhöhe den Zähler für aktive Verbindungen
            activeConnections.get(selectedService).incrementAndGet();
            
            LOGGER.info("LoadBalancingProxy: Leite komplexe Anfrage an Backend-Dienst weiter");
            return selectedService.complexRequest(id, data, options);
        } finally {
            // Verringere den Zähler für aktive Verbindungen
            activeConnections.get(selectedService).decrementAndGet();
        }
    }
    
    /**
     * Wählt einen Backend-Dienst gemäß der konfigurierten Strategie aus.
     * 
     * @return Der ausgewählte Dienst
     */
    private RemoteService selectService() {
        RemoteService selectedService;
        
        switch (loadBalancingStrategy) {
            case ROUND_ROBIN:
                selectedService = selectRoundRobin();
                break;
            case RANDOM:
                selectedService = selectRandom();
                break;
            case LEAST_CONNECTIONS:
                selectedService = selectLeastConnections();
                break;
            default:
                // Fallback auf Round Robin
                selectedService = selectRoundRobin();
        }
        
        LOGGER.debug("LoadBalancingProxy: Dienst ausgewählt mittels {}-Strategie", loadBalancingStrategy);
        return selectedService;
    }
    
    /**
     * Wählt einen Dienst mit der Round-Robin-Strategie aus.
     * 
     * @return Der ausgewählte Dienst
     */
    private RemoteService selectRoundRobin() {
        // Erhöhe den Zähler und wende Modulo an, um im Bereich der verfügbaren Dienste zu bleiben
        int index = roundRobinCounter.getAndIncrement() % backendServices.size();
        return backendServices.get(index);
    }
    
    /**
     * Wählt einen Dienst zufällig aus.
     * 
     * @return Der ausgewählte Dienst
     */
    private RemoteService selectRandom() {
        int index = random.nextInt(backendServices.size());
        return backendServices.get(index);
    }
    
    /**
     * Wählt den Dienst mit den wenigsten aktiven Verbindungen aus.
     * 
     * @return Der ausgewählte Dienst
     */
    private RemoteService selectLeastConnections() {
        RemoteService leastBusyService = null;
        int minConnections = Integer.MAX_VALUE;
        
        for (RemoteService service : backendServices) {
            int connections = activeConnections.get(service).get();
            
            if (connections < minConnections) {
                minConnections = connections;
                leastBusyService = service;
                
                // Optimierung: Wenn ein Dienst keine Verbindungen hat, wähle ihn sofort aus
                if (connections == 0) {
                    break;
                }
            }
        }
        
        return leastBusyService;
    }
    
    /**
     * Fügt einen neuen Backend-Dienst hinzu.
     * 
     * @param service Der hinzuzufügende Dienst
     */
    public void addBackendService(RemoteService service) {
        backendServices.add(service);
        activeConnections.put(service, new AtomicInteger(0));
        LOGGER.info("LoadBalancingProxy: Backend-Dienst hinzugefügt, jetzt {} Dienste", backendServices.size());
    }
    
    /**
     * Entfernt einen Backend-Dienst.
     * 
     * @param service Der zu entfernende Dienst
     * @return true, wenn der Dienst erfolgreich entfernt wurde, sonst false
     */
    public boolean removeBackendService(RemoteService service) {
        if (backendServices.size() <= 1) {
            LOGGER.warn("LoadBalancingProxy: Kann letzten Dienst nicht entfernen");
            return false;
        }
        
        boolean removed = backendServices.remove(service);
        if (removed) {
            activeConnections.remove(service);
            LOGGER.info("LoadBalancingProxy: Backend-Dienst entfernt, noch {} Dienste", backendServices.size());
        }
        
        return removed;
    }
    
    /**
     * Gibt eine Liste aller Backend-Dienste zurück.
     * 
     * @return Eine nicht veränderbare Liste aller Backend-Dienste
     */
    public List<RemoteService> getBackendServices() {
        return Collections.unmodifiableList(backendServices);
    }
    
    /**
     * Gibt die aktuelle Anzahl aktiver Verbindungen für jeden Dienst zurück.
     * 
     * @return Eine Map mit Diensten als Schlüssel und der Anzahl aktiver Verbindungen als Wert
     */
    public Map<RemoteService, Integer> getActiveConnections() {
        Map<RemoteService, Integer> result = new ConcurrentHashMap<>();
        
        for (Map.Entry<RemoteService, AtomicInteger> entry : activeConnections.entrySet()) {
            result.put(entry.getKey(), entry.getValue().get());
        }
        
        return result;
    }
}