package de.becke.vs.pattern.proxy.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Konkrete Implementierung des RemoteService.
 * 
 * Diese Klasse simuliert einen realen Remote-Dienst, der über ein Netzwerk 
 * angesprochen wird. Sie enthält absichtlich Verzögerungen und gelegentliche
 * Fehler, um ein realistisches Szenario darzustellen.
 */
public class RealRemoteService implements RemoteService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RealRemoteService.class);
    
    private final String serviceId;
    private final Random random = new Random();
    private final boolean simulateErrors;
    private final long simulatedLatencyMs;
    
    /**
     * Erstellt einen realen Remote-Dienst mit einer bestimmten ID.
     * 
     * @param serviceId Eine eindeutige ID für diesen Dienst
     */
    public RealRemoteService(String serviceId) {
        this(serviceId, true, 100);
    }
    
    /**
     * Erstellt einen realen Remote-Dienst mit anpassbaren Eigenschaften.
     * 
     * @param serviceId Eine eindeutige ID für diesen Dienst
     * @param simulateErrors Gibt an, ob gelegentliche Fehler simuliert werden sollen
     * @param simulatedLatencyMs Die simulierte Latenz in Millisekunden
     */
    public RealRemoteService(String serviceId, boolean simulateErrors, long simulatedLatencyMs) {
        this.serviceId = serviceId;
        this.simulateErrors = simulateErrors;
        this.simulatedLatencyMs = simulatedLatencyMs;
        LOGGER.info("RealRemoteService '{}' initialisiert (Latenz: {}ms, Fehler: {})", 
                serviceId, simulatedLatencyMs, simulateErrors ? "aktiviert" : "deaktiviert");
    }
    
    @Override
    public String request() throws ServiceException {
        LOGGER.info("RealRemoteService '{}': Einfache Anfrage empfangen", serviceId);
        
        // Simuliere Netzwerklatenz
        simulateLatency();
        
        // Simuliere gelegentliche Fehler
        maybeThrowException();
        
        String response = "Antwort von Service " + serviceId + " auf einfache Anfrage";
        LOGGER.info("RealRemoteService '{}': Einfache Anfrage beantwortet", serviceId);
        return response;
    }
    
    @Override
    public String request(String parameter) throws ServiceException {
        LOGGER.info("RealRemoteService '{}': Anfrage mit Parameter '{}' empfangen", serviceId, parameter);
        
        // Simuliere Netzwerklatenz
        simulateLatency();
        
        // Simuliere gelegentliche Fehler
        maybeThrowException();
        
        // Simuliere eine Verarbeitung
        String response = "Antwort von Service " + serviceId + " auf Parameter '" + parameter + "'";
        LOGGER.info("RealRemoteService '{}': Anfrage mit Parameter beantwortet", serviceId);
        return response;
    }
    
    @Override
    public String complexRequest(int id, String data, String[] options) throws ServiceException {
        LOGGER.info("RealRemoteService '{}': Komplexe Anfrage empfangen (ID: {}, Daten: {}, Optionen: {})",
                serviceId, id, data, Arrays.toString(options));
        
        // Simuliere höhere Latenz für komplexe Anfragen
        simulateLatency(simulatedLatencyMs * 2);
        
        // Simuliere gelegentliche Fehler mit höherer Wahrscheinlichkeit
        maybeThrowException(0.15);
        
        // Simuliere eine komplexere Verarbeitung
        StringBuilder response = new StringBuilder();
        response.append("Ergebnis der komplexen Anfrage von Service ").append(serviceId).append(":\n");
        response.append("ID: ").append(id).append("\n");
        response.append("Verarbeitete Daten: ").append(data).append("\n");
        response.append("Angewandte Optionen: ");
        
        for (int i = 0; i < options.length; i++) {
            response.append(options[i]);
            if (i < options.length - 1) {
                response.append(", ");
            }
        }
        
        LOGGER.info("RealRemoteService '{}': Komplexe Anfrage beantwortet", serviceId);
        return response.toString();
    }
    
    /**
     * Simuliert eine Netzwerklatenz, indem der Thread für eine bestimmte Zeit schläft.
     */
    private void simulateLatency() {
        simulateLatency(simulatedLatencyMs);
    }
    
    /**
     * Simuliert eine Netzwerklatenz mit angegebener Dauer.
     * 
     * @param latencyMs Die zu simulierende Latenz in Millisekunden
     */
    private void simulateLatency(long latencyMs) {
        // Füge eine zufällige Variation zur Latenz hinzu (±25%)
        long actualLatency = (long) (latencyMs * (0.75 + random.nextDouble() * 0.5));
        
        try {
            LOGGER.debug("RealRemoteService '{}': Simuliere Latenz von {}ms", serviceId, actualLatency);
            TimeUnit.MILLISECONDS.sleep(actualLatency);
        } catch (InterruptedException e) {
            LOGGER.warn("RealRemoteService '{}': Latenz-Simulation unterbrochen", serviceId);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Wirft mit einer bestimmten Wahrscheinlichkeit eine Ausnahme, um Netzwerkfehler zu simulieren.
     * 
     * @throws ServiceException Wenn ein simulierter Fehler auftritt
     */
    private void maybeThrowException() throws ServiceException {
        maybeThrowException(0.1); // 10% Fehlerwahrscheinlichkeit als Standard
    }
    
    /**
     * Wirft mit einer angegebenen Wahrscheinlichkeit eine Ausnahme, um Netzwerkfehler zu simulieren.
     * 
     * @param probability Die Wahrscheinlichkeit eines Fehlers (0.0 - 1.0)
     * @throws ServiceException Wenn ein simulierter Fehler auftritt
     */
    private void maybeThrowException(double probability) throws ServiceException {
        if (!simulateErrors) {
            return;
        }
        
        if (random.nextDouble() < probability) {
            // Wähle zufällig einen Fehlertyp aus
            ServiceException.ErrorType[] errorTypes = {
                ServiceException.ErrorType.CONNECTION_TIMEOUT,
                ServiceException.ErrorType.CONNECTION_LOST,
                ServiceException.ErrorType.SERVICE_UNAVAILABLE,
                ServiceException.ErrorType.PROTOCOL_ERROR
            };
            
            ServiceException.ErrorType errorType = errorTypes[random.nextInt(errorTypes.length)];
            String errorMessage = "Simulierter Fehler im RealRemoteService '" + serviceId + "': " + errorType;
            
            LOGGER.warn("RealRemoteService '{}': Werfe simulierten Fehler: {}", serviceId, errorType);
            throw new ServiceException(errorMessage, errorType);
        }
    }
    
    /**
     * Gibt die ID dieses Dienstes zurück.
     * 
     * @return Die Service-ID
     */
    public String getServiceId() {
        return serviceId;
    }
}