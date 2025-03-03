package de.becke.vs.pattern.callback.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Basisklasse für asynchrone Operationen mit Callback-Unterstützung.
 * Diese Klasse bietet grundlegende Funktionalität für die Ausführung asynchroner
 * Operationen mit Callbacks und ermöglicht das Überwachen des Status.
 * 
 * @param <T> Der Typ des Ergebnisses der asynchronen Operation.
 */
public abstract class AsyncOperation<T> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncOperation.class);
    
    // Ein gemeinsamer Thread-Pool für asynchrone Ausführungen
    protected static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    
    // Eine eindeutige ID für die Nachverfolgung der Operation
    private final String operationId;
    
    // Der Status der Operation
    private OperationStatus status;
    
    // Das Ergebnis der Operation (wenn erfolgreich)
    private T result;
    
    // Die bei der Operation aufgetretene Exception (wenn fehlgeschlagen)
    private Throwable error;
    
    /**
     * Erstellt eine neue asynchrone Operation mit einer generierten ID.
     */
    public AsyncOperation() {
        this(UUID.randomUUID().toString());
    }
    
    /**
     * Erstellt eine neue asynchrone Operation mit der angegebenen ID.
     * 
     * @param operationId Die ID für die Operation.
     */
    public AsyncOperation(String operationId) {
        this.operationId = operationId;
        this.status = OperationStatus.PENDING;
    }
    
    /**
     * Führt die asynchrone Operation aus und ruft den angegebenen Callback auf,
     * wenn die Operation abgeschlossen ist.
     * 
     * @param callback Der Callback, der nach Abschluss aufgerufen werden soll.
     */
    public void execute(Callback<T> callback) {
        if (status != OperationStatus.PENDING) {
            LOGGER.warn("Operation '{}' wurde bereits ausgeführt: {}", operationId, status);
            executeCallback(callback);
            return;
        }
        
        LOGGER.info("Starte asynchrone Operation '{}'", operationId);
        status = OperationStatus.RUNNING;
        
        EXECUTOR.submit(() -> {
            try {
                // Führe die eigentliche Implementierung aus
                T operationResult = executeAsync();
                LOGGER.info("Operation '{}' erfolgreich abgeschlossen", operationId);
                
                // Setze das Ergebnis und aktualisiere den Status
                synchronized (this) {
                    result = operationResult;
                    status = OperationStatus.COMPLETED;
                }
                
                // Rufe den Erfolgs-Callback auf
                if (callback != null) {
                    callback.onSuccess(result);
                }
            } catch (Throwable e) {
                LOGGER.error("Fehler bei der Ausführung von Operation '{}': {}", operationId, e.getMessage(), e);
                
                // Setze den Fehler und aktualisiere den Status
                synchronized (this) {
                    error = e;
                    status = OperationStatus.FAILED;
                }
                
                // Rufe den Fehler-Callback auf
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }
    
    /**
     * Führt die asynchrone Operation aus und gibt ein CompletableFuture zurück,
     * das das Ergebnis der Operation darstellt.
     * 
     * @return Ein CompletableFuture, das das Ergebnis der Operation darstellt.
     */
    public CompletableFuture<T> executeAsync() {
        CompletableFuture<T> future = new CompletableFuture<>();
        
        execute(new Callback<T>() {
            @Override
            public void onSuccess(T result) {
                future.complete(result);
            }
            
            @Override
            public void onError(Throwable exception) {
                future.completeExceptionally(exception);
            }
        });
        
        return future;
    }
    
    /**
     * Gibt die ID der Operation zurück.
     * 
     * @return Die Operations-ID.
     */
    public String getOperationId() {
        return operationId;
    }
    
    /**
     * Gibt den aktuellen Status der Operation zurück.
     * 
     * @return Der aktuelle Status.
     */
    public synchronized OperationStatus getStatus() {
        return status;
    }
    
    /**
     * Gibt das Ergebnis der Operation zurück, wenn diese erfolgreich abgeschlossen wurde.
     * 
     * @return Das Ergebnis der Operation oder null, wenn die Operation nicht erfolgreich war.
     */
    public synchronized T getResult() {
        return result;
    }
    
    /**
     * Gibt die bei der Operation aufgetretene Exception zurück, wenn diese fehlgeschlagen ist.
     * 
     * @return Die aufgetretene Exception oder null, wenn die Operation nicht fehlgeschlagen ist.
     */
    public synchronized Throwable getError() {
        return error;
    }
    
    /**
     * Führt den entsprechenden Callback abhängig vom aktuellen Status der Operation aus.
     * 
     * @param callback Der auszuführende Callback.
     */
    private void executeCallback(Callback<T> callback) {
        if (callback == null) {
            return;
        }
        
        switch (status) {
            case COMPLETED:
                callback.onSuccess(result);
                break;
            case FAILED:
                callback.onError(error);
                break;
            case RUNNING:
            case PENDING:
                // Für laufende oder ausstehende Operationen machen wir nichts,
                // da die Callbacks aufgerufen werden, wenn die Operation abgeschlossen ist
                break;
        }
    }
    
    /**
     * Die zu implementierende Methode, die die eigentliche asynchrone Operation ausführt.
     * 
     * @return Das Ergebnis der Operation.
     * @throws Exception Wenn bei der Ausführung ein Fehler auftritt.
     */
    protected abstract T doExecute() throws Exception;
    
    /**
     * Führt die eigentliche Logik der asynchronen Operation aus.
     * 
     * @return Das Ergebnis der Operation.
     * @throws Exception Wenn bei der Ausführung ein Fehler auftritt.
     */
    private T executeAsync() throws Exception {
        LOGGER.debug("Führe asynchrone Operation '{}' aus", operationId);
        return doExecute();
    }
}