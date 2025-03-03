package de.becke.vs.pattern.proxy.forward;

import de.becke.vs.pattern.proxy.common.RemoteService;
import de.becke.vs.pattern.proxy.common.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Ein Forward-Proxy für RemoteServices.
 * 
 * Diese Implementierung agiert als Vermittler zwischen dem Client und 
 * mehreren RemoteServices. Sie stellt zusätzliche Funktionen wie 
 * Protokollierung, Zugangskontrolle und Inhaltsfilterung bereit.
 */
public class ForwardProxy implements RemoteService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ForwardProxy.class);
    
    private final RemoteService targetService;
    private final AccessController accessController;
    private final ContentFilter contentFilter;
    private final boolean loggingEnabled;
    
    // Zähler für die Statistik
    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final Map<String, AtomicInteger> parameterFrequency = new HashMap<>();
    
    /**
     * Erstellt einen Forward-Proxy für einen bestimmten Zieldienst.
     * 
     * @param targetService Der Zieldienst, an den Anfragen weitergeleitet werden
     * @param accessController Der Controller für die Zugangskontrolle
     * @param contentFilter Der Filter für die Inhaltsfilterung
     * @param loggingEnabled Gibt an, ob detaillierte Protokollierung aktiviert ist
     */
    public ForwardProxy(RemoteService targetService, AccessController accessController, 
                        ContentFilter contentFilter, boolean loggingEnabled) {
        this.targetService = targetService;
        this.accessController = accessController;
        this.contentFilter = contentFilter;
        this.loggingEnabled = loggingEnabled;
        
        LOGGER.info("ForwardProxy initialisiert mit Zugangskontrolle: {}, Inhaltsfilterung: {}, Protokollierung: {}",
                accessController != null ? "aktiviert" : "deaktiviert",
                contentFilter != null ? "aktiviert" : "deaktiviert",
                loggingEnabled ? "aktiviert" : "deaktiviert");
    }
    
    @Override
    public String request() throws ServiceException {
        LOGGER.info("ForwardProxy: Einfache Anfrage empfangen");
        
        // Erhöhe Anfragenzähler für Statistik
        requestCount.incrementAndGet();
        
        // Führe die Anfrage aus
        String response = executeRequest(() -> targetService.request());
        
        LOGGER.info("ForwardProxy: Einfache Anfrage bearbeitet");
        return response;
    }
    
    @Override
    public String request(String parameter) throws ServiceException {
        LOGGER.info("ForwardProxy: Anfrage mit Parameter '{}' empfangen", parameter);
        
        // Erhöhe Anfragenzähler für Statistik
        requestCount.incrementAndGet();
        
        // Überprüfe Zugangsrechte, falls Zugangskontrolle aktiviert ist
        if (accessController != null && !accessController.checkAccess(parameter)) {
            LOGGER.warn("ForwardProxy: Zugriff auf Parameter '{}' verweigert", parameter);
            throw new ServiceException("Zugriff verweigert für Parameter: " + parameter, 
                    ServiceException.ErrorType.UNAUTHORIZED);
        }
        
        // Filtere den Parameter, falls Inhaltsfilterung aktiviert ist
        String filteredParameter = parameter;
        if (contentFilter != null) {
            filteredParameter = contentFilter.filterContent(parameter);
            if (loggingEnabled && !filteredParameter.equals(parameter)) {
                LOGGER.info("ForwardProxy: Parameter gefiltert von '{}' zu '{}'", parameter, filteredParameter);
            }
        }
        
        // Aktualisiere die Parameter-Häufigkeitsstatistik
        parameterFrequency.computeIfAbsent(parameter, k -> new AtomicInteger(0)).incrementAndGet();
        
        // Führe die Anfrage mit dem gefilterten Parameter aus
        String response = executeRequest(() -> targetService.request(filteredParameter));
        
        // Filtere die Antwort, falls Inhaltsfilterung aktiviert ist
        if (contentFilter != null) {
            String filteredResponse = contentFilter.filterContent(response);
            if (loggingEnabled && !filteredResponse.equals(response)) {
                LOGGER.info("ForwardProxy: Antwort gefiltert");
            }
            response = filteredResponse;
        }
        
        LOGGER.info("ForwardProxy: Anfrage mit Parameter bearbeitet");
        return response;
    }
    
    @Override
    public String complexRequest(int id, String data, String[] options) throws ServiceException {
        LOGGER.info("ForwardProxy: Komplexe Anfrage empfangen (ID: {})", id);
        
        // Erhöhe Anfragenzähler für Statistik
        requestCount.incrementAndGet();
        
        // Überprüfe Zugangsrechte, falls Zugangskontrolle aktiviert ist
        if (accessController != null && !accessController.checkComplexAccess(id, data, options)) {
            LOGGER.warn("ForwardProxy: Zugriff auf komplexe Anfrage mit ID {} verweigert", id);
            throw new ServiceException("Zugriff auf komplexe Anfrage verweigert", 
                    ServiceException.ErrorType.UNAUTHORIZED);
        }
        
        // Filtere die Daten, falls Inhaltsfilterung aktiviert ist
        String filteredData = data;
        String[] filteredOptions = options;
        
        if (contentFilter != null) {
            filteredData = contentFilter.filterContent(data);
            
            filteredOptions = new String[options.length];
            for (int i = 0; i < options.length; i++) {
                filteredOptions[i] = contentFilter.filterContent(options[i]);
            }
            
            if (loggingEnabled && (!filteredData.equals(data) || hasFilteredOptions(options, filteredOptions))) {
                LOGGER.info("ForwardProxy: Komplexe Anfragedaten gefiltert");
            }
        }
        
        // Führe die komplexe Anfrage aus
        String response = executeRequest(() -> targetService.complexRequest(id, filteredData, filteredOptions));
        
        // Filtere die Antwort, falls Inhaltsfilterung aktiviert ist
        if (contentFilter != null) {
            String filteredResponse = contentFilter.filterContent(response);
            if (loggingEnabled && !filteredResponse.equals(response)) {
                LOGGER.info("ForwardProxy: Komplexe Antwort gefiltert");
            }
            response = filteredResponse;
        }
        
        LOGGER.info("ForwardProxy: Komplexe Anfrage bearbeitet");
        return response;
    }
    
    /**
     * Führt eine Anfrage aus und behandelt mögliche Fehler.
     * 
     * @param request Die auszuführende Anfrage
     * @return Die Antwort der Anfrage
     * @throws ServiceException Wenn bei der Ausführung der Anfrage ein Fehler auftritt
     */
    private String executeRequest(RequestExecutor request) throws ServiceException {
        try {
            // Protokolliere Start der Anfrage
            long startTime = System.currentTimeMillis();
            
            // Führe die Anfrage aus
            String response = request.execute();
            
            // Protokolliere Ende der Anfrage
            if (loggingEnabled) {
                long duration = System.currentTimeMillis() - startTime;
                LOGGER.info("ForwardProxy: Anfrage in {}ms bearbeitet", duration);
            }
            
            return response;
        } catch (ServiceException e) {
            // Protokolliere den Fehler
            LOGGER.error("ForwardProxy: Fehler bei der Ausführung der Anfrage: {}", e.getMessage());
            
            // Leite die Exception weiter
            throw e;
        }
    }
    
    /**
     * Überprüft, ob die gefilterten Optionen von den ursprünglichen abweichen.
     * 
     * @param original Die ursprünglichen Optionen
     * @param filtered Die gefilterten Optionen
     * @return true, wenn mindestens eine Option gefiltert wurde, sonst false
     */
    private boolean hasFilteredOptions(String[] original, String[] filtered) {
        if (original.length != filtered.length) {
            return true;
        }
        
        for (int i = 0; i < original.length; i++) {
            if (!original[i].equals(filtered[i])) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Gibt statistische Informationen über die verarbeiteten Anfragen zurück.
     * 
     * @return Ein String mit statistischen Informationen
     */
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();
        stats.append("ForwardProxy Statistik:\n");
        stats.append("  Gesamtzahl der Anfragen: ").append(requestCount.get()).append("\n");
        
        if (!parameterFrequency.isEmpty()) {
            stats.append("  Parameter-Häufigkeit:\n");
            parameterFrequency.forEach((param, count) -> 
                stats.append("    '").append(param).append("': ").append(count.get()).append("\n")
            );
        }
        
        return stats.toString();
    }
    
    /**
     * Gibt die häufigsten Parameter zurück.
     * 
     * @param limit Die maximale Anzahl der zurückzugebenden Parameter
     * @return Eine Map mit Parametern und ihrer Häufigkeit, sortiert nach Häufigkeit
     */
    public Map<String, Integer> getTopParameters(int limit) {
        return parameterFrequency.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().get() - e1.getValue().get())
                .limit(limit)
                .collect(HashMap::new, 
                        (map, entry) -> map.put(entry.getKey(), entry.getValue().get()), 
                        HashMap::putAll);
    }
    
    /**
     * Funktionales Interface für die Ausführung einer Anfrage.
     */
    @FunctionalInterface
    private interface RequestExecutor {
        String execute() throws ServiceException;
    }
}