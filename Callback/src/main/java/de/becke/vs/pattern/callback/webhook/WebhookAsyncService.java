package de.becke.vs.pattern.callback.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.becke.vs.pattern.callback.common.OperationStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Ein Dienst, der asynchrone Operationen mit Webhook-basierten Callbacks implementiert.
 * Bei diesem Ansatz wird die asynchrone Operation gestartet und der Dienst ruft nach Abschluss
 * der Operation eine vom Client angegebene URL auf, um das Ergebnis zu übermitteln.
 */
public class WebhookAsyncService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookAsyncService.class);
    
    // Ein Thread-Pool für die asynchrone Ausführung
    private final ExecutorService executor = Executors.newCachedThreadPool();
    
    // Ein Scheduler für die verzögerte Wiederholung von Callbacks
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // Speichert Informationen über laufende Operationen
    private final Map<String, OperationInfo<?>> operations = new HashMap<>();
    
    // JSON-Mapper für die Serialisierung von Objekten
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // HTTP-Client für das Senden von Webhook-Callbacks
    private final CloseableHttpClient httpClient = HttpClients.createDefault();
    
    // Maximale Anzahl von Wiederholungsversuchen für Callbacks
    private static final int MAX_RETRY_ATTEMPTS = 3;
    
    /**
     * Startet eine asynchrone Operation und gibt eine ID zurück, mit der der Status
     * der Operation abgefragt werden kann. Nach Abschluss der Operation wird ein
     * Callback an die angegebene URL gesendet.
     * 
     * @param input Die Eingabedaten für die Operation.
     * @param processor Die Funktion zur Verarbeitung der Daten.
     * @param callbackUrl Die URL, an die der Callback gesendet werden soll.
     * @param <T> Der Typ der Eingabedaten.
     * @param <R> Der Typ des Ergebnisses.
     * @return Eine ID zur Abfrage des Status der Operation.
     */
    public <T, R> String startOperation(T input, Function<T, R> processor, String callbackUrl) {
        String operationId = UUID.randomUUID().toString();
        LOGGER.info("Starte Operation mit ID: {} und Callback-URL: {}", operationId, callbackUrl);
        
        // Erstelle einen Eintrag für die Operation
        OperationInfo<R> operationInfo = new OperationInfo<>();
        operationInfo.callbackUrl = callbackUrl;
        
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
                
                // Sende den Callback
                sendCallback(operationId, operationInfo, 0);
                
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
                
                // Sende den Fehler-Callback
                sendCallback(operationId, operationInfo, 0);
                
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
     * Sendet einen Callback an die angegebene URL.
     * 
     * @param operationId Die ID der Operation.
     * @param operationInfo Informationen über die Operation.
     * @param retryCount Die aktuelle Anzahl von Wiederholungsversuchen.
     */
    private <R> void sendCallback(String operationId, OperationInfo<R> operationInfo, int retryCount) {
        if (operationInfo.callbackUrl == null) {
            LOGGER.warn("Keine Callback-URL für Operation: {}", operationId);
            return;
        }
        
        try {
            LOGGER.info("Sende Callback für Operation {} an URL: {}", 
                    operationId, operationInfo.callbackUrl);
            
            // Erstelle ein Map mit den Callback-Daten
            Map<String, Object> callbackData = new HashMap<>();
            callbackData.put("operationId", operationId);
            callbackData.put("status", operationInfo.status.name());
            
            if (operationInfo.status == OperationStatus.COMPLETED) {
                callbackData.put("result", operationInfo.result);
            } else if (operationInfo.status == OperationStatus.FAILED) {
                callbackData.put("error", operationInfo.error.getMessage());
                callbackData.put("errorType", operationInfo.error.getClass().getName());
            }
            
            // Konvertiere die Daten in JSON
            String jsonData = objectMapper.writeValueAsString(callbackData);
            
            // Erstelle einen POST-Request
            HttpPost httpPost = new HttpPost(operationInfo.callbackUrl);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(jsonData, StandardCharsets.UTF_8));
            
            // Sende den Request
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    LOGGER.info("Callback für Operation {} erfolgreich gesendet", operationId);
                } else {
                    LOGGER.warn("Callback für Operation {} fehlgeschlagen: {}", 
                            operationId, response.getStatusLine());
                    
                    // Wiederhole den Callback, wenn die maximale Anzahl nicht erreicht ist
                    if (retryCount < MAX_RETRY_ATTEMPTS) {
                        // Berechne die Verzögerung basierend auf einer exponentiellen Backoff-Strategie
                        long delayMs = (long) (Math.pow(2, retryCount) * 1000);
                        LOGGER.info("Wiederhole Callback für Operation {} in {} ms", 
                                operationId, delayMs);
                        
                        scheduler.schedule(
                                () -> sendCallback(operationId, operationInfo, retryCount + 1),
                                delayMs, TimeUnit.MILLISECONDS);
                    } else {
                        LOGGER.error("Maximale Anzahl von Wiederholungsversuchen für Callback " +
                                "der Operation {} erreicht", operationId);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("Fehler beim Senden des Callbacks für Operation {}: {}", 
                    operationId, e.getMessage(), e);
            
            // Wiederhole den Callback, wenn die maximale Anzahl nicht erreicht ist
            if (retryCount < MAX_RETRY_ATTEMPTS) {
                // Berechne die Verzögerung basierend auf einer exponentiellen Backoff-Strategie
                long delayMs = (long) (Math.pow(2, retryCount) * 1000);
                LOGGER.info("Wiederhole Callback für Operation {} in {} ms", 
                        operationId, delayMs);
                
                scheduler.schedule(
                        () -> sendCallback(operationId, operationInfo, retryCount + 1),
                        delayMs, TimeUnit.MILLISECONDS);
            } else {
                LOGGER.error("Maximale Anzahl von Wiederholungsversuchen für Callback " +
                        "der Operation {} erreicht", operationId);
            }
        }
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
        scheduler.schedule(() -> {
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
        scheduler.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            httpClient.close();
        } catch (InterruptedException | IOException e) {
            executor.shutdownNow();
            scheduler.shutdownNow();
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
        private String callbackUrl;
    }
}