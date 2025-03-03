package de.becke.vs.pattern.callback.polling;

import de.becke.vs.pattern.callback.common.OperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Ein Dienst, der asynchrone Operationen mit Polling-basiertem Status-Abruf implementiert.
 * Bei diesem Ansatz wird die asynchrone Operation gestartet und der Client muss den
 * Status der Operation abfragen, um zu erfahren, wann sie abgeschlossen ist.
 */
public class PollingAsyncService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PollingAsyncService.class);
    
    // Ein Thread-Pool für die asynchrone Ausführung
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    // Speichert Informationen über laufende Operationen
    private final Map<String, OperationInfo<?>> operations = new HashMap<>();
    
    /**
     * Startet eine asynchrone Operation und gibt eine ID zurück, mit der der Status
     * der Operation abgefragt werden kann.
     * 
     * @param input Die Eingabedaten für die Operation.
     * @param processor Die Funktion zur Verarbeitung der Daten.
     * @param <T> Der Typ der Eingabedaten.
     * @param <R> Der Typ des Ergebnisses.
     * @return Eine ID zur Abfrage des Status der Operation.
     */
    public <T, R> String startOperation(T input, Function<T, R> processor) {
        String operationId = UUID.randomUUID().toString();
        LOGGER.info("Starte Operation mit ID: {}", operationId);
        
        // Erstelle einen Eintrag für die Operation
        OperationInfo<R> operationInfo = new OperationInfo<>();
        synchronized (operations) {
            operations.put(operationId, operationInfo);
        }
        
        // Führe die Operation asynchron aus
        executor.submit(() -> {
            try {
                LOGGER.info("Verarbeite Daten für Operation: {}", operationId);
                R result = processor.apply(input);
                
                // Setze das Ergebnis und den Status
                synchronized (operationInfo) {
                    operationInfo.result = result;
                    operationInfo.status = OperationStatus.COMPLETED;
                    operationInfo.completionTime = System.currentTimeMillis();
                }
                
                LOGGER.info("Operation {} abgeschlossen", operationId);
                
                // Nach einer gewissen Zeit das Ergebnis aus dem Cache entfernen
                scheduleCleanup(operationId, 60000); // 1 Minute
                
            } catch (Exception e) {
                LOGGER.error("Fehler bei der Ausführung von Operation {}: {}", 
                        operationId, e.getMessage(), e);
                
                // Setze den Fehler und den Status
                synchronized (operationInfo) {
                    operationInfo.error = e;
                    operationInfo.status = OperationStatus.FAILED;
                    operationInfo.completionTime = System.currentTimeMillis();
                }
                
                // Nach einer gewissen Zeit das Ergebnis aus dem Cache entfernen
                scheduleCleanup(operationId, 60000); // 1 Minute
            }
        });
        
        return operationId;
    }
    
    /**
     * Gibt den aktuellen Status einer Operation zurück.
     * 
     * @param operationId Die ID der Operation.
     * @return Der aktuelle Status der Operation oder null, wenn die Operation nicht gefunden wurde.
     */
    public OperationStatus getStatus(String operationId) {
        OperationInfo<?> operationInfo = getOperationInfo(operationId);
        return operationInfo != null ? operationInfo.status : null;
    }
    
    /**
     * Gibt das Ergebnis einer abgeschlossenen Operation zurück.
     * 
     * @param operationId Die ID der Operation.
     * @param <R> Der Typ des Ergebnisses.
     * @return Das Ergebnis der Operation oder null, wenn die Operation noch nicht abgeschlossen ist
     *         oder ein Fehler aufgetreten ist.
     */
    @SuppressWarnings("unchecked")
    public <R> R getResult(String operationId) {
        OperationInfo<?> operationInfo = getOperationInfo(operationId);
        if (operationInfo != null && operationInfo.status == OperationStatus.COMPLETED) {
            return (R) operationInfo.result;
        }
        return null;
    }
    
    /**
     * Gibt den Fehler zurück, der bei einer fehlgeschlagenen Operation aufgetreten ist.
     * 
     * @param operationId Die ID der Operation.
     * @return Der aufgetretene Fehler oder null, wenn die Operation nicht fehlgeschlagen ist.
     */
    public Throwable getError(String operationId) {
        OperationInfo<?> operationInfo = getOperationInfo(operationId);
        if (operationInfo != null && operationInfo.status == OperationStatus.FAILED) {
            return operationInfo.error;
        }
        return null;
    }
    
    /**
     * Wartet, bis eine Operation abgeschlossen ist und gibt dann das Ergebnis zurück.
     * Diese Methode ist blockierend und führt ein Polling durch.
     * 
     * @param operationId Die ID der Operation.
     * @param timeout Das Timeout in Millisekunden.
     * @param <R> Der Typ des Ergebnisses.
     * @return Das Ergebnis der Operation.
     * @throws InterruptedException Wenn der Thread während des Wartens unterbrochen wird.
     * @throws RuntimeException Wenn ein Fehler bei der Operation aufgetreten ist.
     * @throws IllegalStateException Wenn das Timeout überschritten wurde oder die Operation nicht gefunden wurde.
     */
    @SuppressWarnings("unchecked")
    public <R> R waitForResult(String operationId, long timeout) 
            throws InterruptedException, RuntimeException {
        
        LOGGER.info("Warte auf Ergebnis für Operation: {}", operationId);
        
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeout;
        
        while (System.currentTimeMillis() < endTime) {
            OperationInfo<?> operationInfo = getOperationInfo(operationId);
            
            if (operationInfo == null) {
                throw new IllegalStateException("Operation nicht gefunden: " + operationId);
            }
            
            synchronized (operationInfo) {
                switch (operationInfo.status) {
                    case COMPLETED:
                        LOGGER.info("Operation {} abgeschlossen", operationId);
                        return (R) operationInfo.result;
                    case FAILED:
                        LOGGER.error("Operation {} fehlgeschlagen: {}", operationId, 
                                operationInfo.error.getMessage());
                        throw new RuntimeException("Operation fehlgeschlagen", operationInfo.error);
                    case PENDING:
                    case RUNNING:
                        // Warte ein wenig, bevor wir erneut prüfen
                        TimeUnit.MILLISECONDS.sleep(100);
                        break;
                }
            }
        }
        
        // Wenn wir hier ankommen, wurde das Timeout überschritten
        throw new IllegalStateException(
                "Timeout beim Warten auf Ergebnis für Operation: " + operationId);
    }
    
    /**
     * Gibt Informationen über eine Operation zurück.
     * 
     * @param operationId Die ID der Operation.
     * @return Informationen über die Operation oder null, wenn die Operation nicht gefunden wurde.
     */
    private OperationInfo<?> getOperationInfo(String operationId) {
        synchronized (operations) {
            return operations.get(operationId);
        }
    }
    
    /**
     * Plant die Entfernung einer Operation aus dem Cache.
     * 
     * @param operationId Die ID der Operation.
     * @param delayMs Die Verzögerung in Millisekunden, nach der die Operation entfernt werden soll.
     */
    private void scheduleCleanup(String operationId, long delayMs) {
        executor.schedule(() -> {
            synchronized (operations) {
                OperationInfo<?> operationInfo = operations.remove(operationId);
                if (operationInfo != null) {
                    LOGGER.debug("Operation {} aus dem Cache entfernt", operationId);
                }
            }
        }, delayMs, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Stoppt den Dienst und gibt alle Ressourcen frei.
     */
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Interne Klasse zur Speicherung von Informationen über eine Operation.
     */
    private static class OperationInfo<R> {
        private OperationStatus status = OperationStatus.PENDING;
        private R result;
        private Throwable error;
        private long completionTime;
    }
}