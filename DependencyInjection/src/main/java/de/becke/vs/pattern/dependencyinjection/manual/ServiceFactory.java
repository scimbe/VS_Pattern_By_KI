package de.becke.vs.pattern.dependencyinjection.manual;

import de.becke.vs.pattern.dependencyinjection.common.FaultTolerantService;
import de.becke.vs.pattern.dependencyinjection.common.RemoteService;
import de.becke.vs.pattern.dependencyinjection.common.RemoteServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eine einfache Factory-Klasse, die verschiedene Arten von Services erstellt.
 * Dies ist ein Beispiel für den Factory-Aspekt der Dependency Injection.
 */
public class ServiceFactory {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceFactory.class);
    
    /**
     * Erstellt einen einfachen RemoteService mit dem angegebenen Namen.
     * 
     * @param serviceName Der Name des Services
     * @return Ein neuer RemoteService
     */
    public RemoteService createSimpleService(String serviceName) {
        LOGGER.info("Erstelle einfachen RemoteService mit Namen: {}", serviceName);
        return new RemoteServiceImpl(serviceName);
    }
    
    /**
     * Erstellt einen RemoteService mit simulierter Latenz.
     * 
     * @param serviceName Der Name des Services
     * @param latency Die simulierte Latenz in Millisekunden
     * @return Ein neuer RemoteService mit der angegebenen Latenz
     */
    public RemoteService createServiceWithLatency(String serviceName, int latency) {
        LOGGER.info("Erstelle RemoteService mit Namen: {} und Latenz: {}ms", serviceName, latency);
        return new RemoteServiceImpl(serviceName, latency);
    }
    
    /**
     * Erstellt einen fehlertoleranten Service mit automatischem Failover.
     * 
     * @param primaryServiceName Der Name des primären Services
     * @param fallbackServiceName Der Name des Fallback-Services
     * @param maxRetries Die maximale Anzahl von Wiederholungsversuchen
     * @return Ein neuer fehlertoleranter Service
     */
    public RemoteService createFaultTolerantService(
            String primaryServiceName, String fallbackServiceName, int maxRetries) {
        
        LOGGER.info("Erstelle fehlertoleranten Service mit primär: {}, fallback: {}, retries: {}", 
                primaryServiceName, fallbackServiceName, maxRetries);
        
        RemoteService primaryService = new RemoteServiceImpl(primaryServiceName);
        RemoteService fallbackService = new RemoteServiceImpl(fallbackServiceName);
        
        return new FaultTolerantService(primaryService, fallbackService, maxRetries);
    }
    
    /**
     * Erstellt einen Fehler-Service, der Fehler für Tests simuliert.
     * 
     * @param serviceName Der Name des Services
     * @param failureRate Die Ausfallrate (0.0 - 1.0)
     * @return Ein neuer Service, der gelegentlich Fehler wirft
     */
    public RemoteService createFailingService(String serviceName, double failureRate) {
        LOGGER.info("Erstelle fehlerhaften Service mit Namen: {} und Ausfallrate: {}", 
                serviceName, failureRate);
        
        return new RemoteService() {
            private final RemoteService delegate = new RemoteServiceImpl(serviceName);
            
            @Override
            public String executeOperation() {
                simulateRandomFailure(failureRate);
                return delegate.executeOperation();
            }
            
            @Override
            public String executeOperation(String parameter) {
                simulateRandomFailure(failureRate);
                return delegate.executeOperation(parameter);
            }
            
            @Override
            public String getServiceName() {
                return serviceName + "(Fehlerrate: " + failureRate + ")";
            }
            
            private void simulateRandomFailure(double rate) {
                if (Math.random() < rate) {
                    throw new RuntimeException("Simulierter Fehler in " + serviceName);
                }
            }
        };
    }
}