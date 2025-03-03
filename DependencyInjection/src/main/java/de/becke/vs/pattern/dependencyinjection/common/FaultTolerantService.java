package de.becke.vs.pattern.dependencyinjection.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein Service-Wrapper, der grundlegende Fehlertoleranz-Funktionalitäten bietet.
 * Diese Klasse demonstriert die Komposition von Services durch Dependency Injection.
 */
public class FaultTolerantService implements RemoteService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(FaultTolerantService.class);
    
    private final RemoteService primaryService;
    private final RemoteService fallbackService;
    private final int maxRetries;
    
    /**
     * Erstellt einen neuen FaultTolerantService mit primärem und Fallback-Service.
     * 
     * @param primaryService Der primäre Service, der verwendet werden soll
     * @param fallbackService Der Fallback-Service, der bei Fehlern verwendet wird
     * @param maxRetries Die maximale Anzahl von Wiederholungsversuchen
     */
    public FaultTolerantService(RemoteService primaryService, RemoteService fallbackService, int maxRetries) {
        this.primaryService = primaryService;
        this.fallbackService = fallbackService;
        this.maxRetries = maxRetries;
        
        LOGGER.info("FaultTolerantService initialisiert mit primärem Service '{}', Fallback '{}' und {} max. Versuchen",
                primaryService.getServiceName(), fallbackService.getServiceName(), maxRetries);
    }
    
    @Override
    public String executeOperation() {
        return executeWithFallback(() -> primaryService.executeOperation());
    }
    
    @Override
    public String executeOperation(String parameter) {
        return executeWithFallback(() -> primaryService.executeOperation(parameter));
    }
    
    @Override
    public String getServiceName() {
        return "FaultTolerant(" + primaryService.getServiceName() + ")";
    }
    
    /**
     * Führt eine Operation mit Fehlertoleranz aus.
     * Es werden mehrere Versuche unternommen, und bei anhaltenden Fehlern
     * wird auf den Fallback-Service zurückgegriffen.
     * 
     * @param operation Die auszuführende Operation
     * @return Das Ergebnis der Operation
     */
    private String executeWithFallback(ServiceOperation operation) {
        Exception lastException = null;
        
        // Versuche die Operation mit dem primären Service
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                if (attempt > 1) {
                    LOGGER.info("Wiederholungsversuch {} mit primärem Service", attempt);
                }
                return operation.execute();
            } catch (Exception e) {
                LOGGER.warn("Fehler bei Versuch {} mit primärem Service: {}", attempt, e.getMessage());
                lastException = e;
                
                // Kurze Pause vor dem nächsten Versuch
                try {
                    Thread.sleep(100 * attempt); // Exponentielles Backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        // Wenn alle Versuche mit dem primären Service fehlschlagen, verwende den Fallback
        LOGGER.info("Fallback zum sekundären Service nach {} fehlgeschlagenen Versuchen", maxRetries);
        try {
            return operation.executeWithFallback();
        } catch (Exception e) {
            LOGGER.error("Auch Fallback-Service fehlgeschlagen: {}", e.getMessage());
            throw new RuntimeException("Alle Services fehlgeschlagen", lastException);
        }
    }
    
    /**
     * Funktionales Interface für Service-Operationen.
     */
    private interface ServiceOperation {
        String execute() throws Exception;
        
        /**
         * Führt die Operation mit dem Fallback-Service aus.
         */
        default String executeWithFallback() throws Exception {
            throw new UnsupportedOperationException("Fallback nicht implementiert");
        }
    }
}