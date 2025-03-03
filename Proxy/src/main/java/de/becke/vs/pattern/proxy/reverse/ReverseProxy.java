package de.becke.vs.pattern.proxy.reverse;

import de.becke.vs.pattern.proxy.common.RemoteService;
import de.becke.vs.pattern.proxy.common.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Ein Reverse-Proxy, der als Einstiegspunkt für mehrere Backend-Dienste dient.
 * 
 * Diese Implementierung nimmt Anfragen entgegen und leitet sie an entsprechende
 * Backend-Dienste weiter, basierend auf dem angeforderten Pfad. Sie bietet
 * zusätzliche Funktionen wie SSL-Terminierung, Anfragenkompression und
 * Verfügbarkeitsüberwachung.
 */
public class ReverseProxy implements RemoteService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ReverseProxy.class);
    
    // Die registrierten Backend-Dienste, zugeordnet zu ihren Pfaden
    private final Map<String, RemoteService> routingTable = new HashMap<>();
    
    // Der Standarddienst, wenn kein spezifischer Pfad gefunden wird
    private RemoteService defaultService;
    
    // Konfigurationsoptionen
    private final boolean sslTermination;
    private final boolean requestCompression;
    private final boolean healthCheckEnabled;
    
    // Statistiken
    private final Map<String, AtomicLong> requestCounters = new HashMap<>();
    private final AtomicLong totalRequests = new AtomicLong(0);
    
    /**
     * Erstellt einen Reverse-Proxy mit den angegebenen Konfigurationsoptionen.
     * 
     * @param sslTermination Aktiviert die SSL-Terminierung
     * @param requestCompression Aktiviert die Anfragenkompression
     * @param healthCheckEnabled Aktiviert die Verfügbarkeitsüberwachung für Backend-Dienste
     */
    public ReverseProxy(boolean sslTermination, boolean requestCompression, boolean healthCheckEnabled) {
        this.sslTermination = sslTermination;
        this.requestCompression = requestCompression;
        this.healthCheckEnabled = healthCheckEnabled;
        
        LOGGER.info("ReverseProxy initialisiert mit SSL-Terminierung: {}, Anfragenkompression: {}, Verfügbarkeitsüberwachung: {}",
                sslTermination, requestCompression, healthCheckEnabled);
    }
    
    /**
     * Registriert einen Backend-Dienst für einen bestimmten Pfad.
     * 
     * @param path Der Pfad, unter dem der Dienst erreichbar sein soll
     * @param service Der zu registrierende Dienst
     */
    public void registerService(String path, RemoteService service) {
        routingTable.put(path, service);
        requestCounters.put(path, new AtomicLong(0));
        LOGGER.info("Backend-Dienst für Pfad '{}' registriert", path);
    }
    
    /**
     * Setzt den Standarddienst, der verwendet wird, wenn kein spezifischer Pfad gefunden wird.
     * 
     * @param service Der als Standard zu verwendende Dienst
     */
    public void setDefaultService(RemoteService service) {
        this.defaultService = service;
        LOGGER.info("Standarddienst gesetzt");
    }
    
    /**
     * Leitet eine Anfrage an den entsprechenden Backend-Dienst basierend auf dem Pfad weiter.
     * 
     * @param path Der Anfragepfad
     * @param requestFunction Die Funktion, die auf dem Backend-Dienst ausgeführt werden soll
     * @return Das Ergebnis der Anfrage
     * @throws ServiceException Wenn die Weiterleitung oder Ausführung fehlschlägt
     */
    private String forwardRequest(String path, ServiceOperation operation) throws ServiceException {
        totalRequests.incrementAndGet();
        
        // Finde den passenden Dienst für den Pfad
        RemoteService targetService = routingTable.get(path);
        
        // Wenn kein passender Dienst gefunden wurde, verwende den Standarddienst
        if (targetService == null) {
            if (defaultService == null) {
                LOGGER.error("Kein Dienst für Pfad '{}' gefunden und kein Standarddienst definiert", path);
                throw new ServiceException("Kein Dienst für Pfad gefunden: " + path,
                        ServiceException.ErrorType.RESOURCE_NOT_FOUND);
            }
            
            targetService = defaultService;
            LOGGER.debug("Verwende Standarddienst für Pfad '{}'", path);
        } else {
            // Erhöhe den Zähler für den spezifischen Pfad
            requestCounters.get(path).incrementAndGet();
            LOGGER.debug("Verwende registrierten Dienst für Pfad '{}'", path);
        }
        
        // Überprüfe die Verfügbarkeit des Dienstes, falls aktiviert
        if (healthCheckEnabled && !isServiceHealthy(targetService)) {
            LOGGER.error("Dienst für Pfad '{}' ist nicht verfügbar", path);
            throw new ServiceException("Dienst nicht verfügbar: " + path,
                    ServiceException.ErrorType.SERVICE_UNAVAILABLE);
        }
        
        // Führe SSL-Terminierung durch, falls aktiviert
        if (sslTermination) {
            LOGGER.debug("Führe SSL-Terminierung durch");
            // In einer realen Implementierung würde hier die SSL-Terminierung stattfinden
        }
        
        // Führe Anfragenkompression durch, falls aktiviert
        if (requestCompression) {
            LOGGER.debug("Führe Anfragenkompression durch");
            // In einer realen Implementierung würde hier die Anfragenkompression stattfinden
        }
        
        try {
            LOGGER.info("Leite Anfrage an Backend-Dienst für Pfad '{}' weiter", path);
            return operation.execute(targetService);
        } catch (ServiceException e) {
            LOGGER.error("Fehler bei der Weiterleitung an Backend-Dienst für Pfad '{}': {}", path, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Überprüft die Verfügbarkeit eines Dienstes.
     * 
     * @param service Der zu überprüfende Dienst
     * @return true, wenn der Dienst verfügbar ist, sonst false
     */
    private boolean isServiceHealthy(RemoteService service) {
        // In einer realen Implementierung würde hier eine Verfügbarkeitsüberwachung stattfinden
        // Für dieses Beispiel gehen wir davon aus, dass alle Dienste verfügbar sind
        return true;
    }
    
    @Override
    public String request() throws ServiceException {
        LOGGER.info("ReverseProxy: Einfache Anfrage ohne Pfad empfangen");
        
        // Verwende den Root-Pfad für einfache Anfragen
        return forwardRequest("/", service -> service.request());
    }
    
    @Override
    public String request(String parameter) throws ServiceException {
        LOGGER.info("ReverseProxy: Anfrage mit Parameter empfangen: '{}'", parameter);
        
        // Interpretiere den Parameter als Pfad
        return forwardRequest(parameter, service -> service.request(parameter));
    }
    
    @Override
    public String complexRequest(int id, String data, String[] options) throws ServiceException {
        LOGGER.info("ReverseProxy: Komplexe Anfrage empfangen (ID: {}, Pfad: '{}')", id, data);
        
        // Interpretiere die Daten als Pfad
        return forwardRequest(data, service -> service.complexRequest(id, data, options));
    }
    
    /**
     * Gibt Statistiken über die Nutzung des Reverse-Proxys zurück.
     * 
     * @return Eine Zeichenkette mit den Statistiken
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("ReverseProxy Statistik:\n");
        stats.append("  Gesamtzahl der Anfragen: ").append(totalRequests.get()).append("\n");
        stats.append("  Anfragen pro Pfad:\n");
        
        routingTable.forEach((path, service) -> {
            long count = requestCounters.getOrDefault(path, new AtomicLong(0)).get();
            stats.append("    ").append(path).append(": ").append(count).append("\n");
        });
        
        return stats.toString();
    }
    
    /**
     * Funktionales Interface für die Ausführung einer Operation auf einem Dienst.
     */
    @FunctionalInterface
    private interface ServiceOperation {
        String execute(RemoteService service) throws ServiceException;
    }
}