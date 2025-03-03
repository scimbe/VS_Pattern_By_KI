package de.becke.vs.pattern.dependencyinjection.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Standard-Implementierung der RemoteService-Schnittstelle.
 * Diese Klasse simuliert einen Remote-Service in einem verteilten System.
 */
public class RemoteServiceImpl implements RemoteService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteServiceImpl.class);
    
    private final String serviceName;
    private final int latency;
    
    /**
     * Erstellt einen neuen RemoteServiceImpl mit einem Standardnamen.
     */
    public RemoteServiceImpl() {
        this("DefaultRemoteService", 50);
    }
    
    /**
     * Erstellt einen neuen RemoteServiceImpl mit angegebenem Namen.
     * 
     * @param serviceName Der Name des Services
     */
    public RemoteServiceImpl(String serviceName) {
        this(serviceName, 50);
    }
    
    /**
     * Erstellt einen neuen RemoteServiceImpl mit angegebenem Namen und Latenz.
     * 
     * @param serviceName Der Name des Services
     * @param latency Die simulierte Netzwerklatenz in Millisekunden
     */
    public RemoteServiceImpl(String serviceName, int latency) {
        this.serviceName = serviceName;
        this.latency = latency;
        LOGGER.info("RemoteService '{}' initialisiert mit {}ms Latenz", serviceName, latency);
    }
    
    @Override
    public String executeOperation() {
        LOGGER.info("RemoteService '{}': Führe Standardoperation aus", serviceName);
        simulateNetworkLatency();
        return "Ergebnis von " + serviceName + " (Standard)";
    }
    
    @Override
    public String executeOperation(String parameter) {
        LOGGER.info("RemoteService '{}': Führe Operation mit Parameter '{}' aus", serviceName, parameter);
        simulateNetworkLatency();
        return "Ergebnis von " + serviceName + " für Parameter: " + parameter;
    }
    
    @Override
    public String getServiceName() {
        return serviceName;
    }
    
    /**
     * Simuliert eine Netzwerklatenz, um ein realistischeres Verhalten in einem verteilten System darzustellen.
     */
    private void simulateNetworkLatency() {
        try {
            Thread.sleep(latency);
        } catch (InterruptedException e) {
            LOGGER.warn("Latenz-Simulation unterbrochen", e);
            Thread.currentThread().interrupt();
        }
    }
}