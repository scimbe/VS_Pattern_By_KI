package de.becke.vs.pattern.callback.retry;

import de.becke.vs.pattern.callback.common.Callback;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Ein Callback-Wrapper, der automatische Wiederholungsversuche für fehlgeschlagene
 * asynchrone Operationen implementiert.
 * 
 * @param <T> Der Typ des Ergebnisses der asynchronen Operation.
 */
public class RetryCallback<T> implements Callback<T> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RetryCallback.class);
    
    // Der ursprüngliche Callback
    private final Callback<T> originalCallback;
    
    // Die Operation, die wiederholt werden soll
    private final Supplier<CompletableFuture<T>> operation;
    
    // Die Retry-Konfiguration
    private final Retry retry;
    
    // Scheduler für asynchrone Wiederholungsversuche
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    /**
     * Erstellt einen neuen RetryCallback mit Standardkonfiguration.
     * 
     * @param originalCallback Der ursprüngliche Callback.
     * @param operation Die zu wiederholende Operation.
     */
    public RetryCallback(Callback<T> originalCallback, Supplier<CompletableFuture<T>> operation) {
        this(originalCallback, operation, createDefaultRetry());
    }
    
    /**
     * Erstellt einen neuen RetryCallback mit angepasster Konfiguration.
     * 
     * @param originalCallback Der ursprüngliche Callback.
     * @param operation Die zu wiederholende Operation.
     * @param retryConfig Die Retry-Konfiguration.
     */
    public RetryCallback(Callback<T> originalCallback, Supplier<CompletableFuture<T>> operation, 
                        RetryConfig retryConfig) {
        this(originalCallback, operation, 
             RetryRegistry.of(retryConfig).retry("retryCallback"));
    }
    
    /**
     * Erstellt einen neuen RetryCallback mit einem bestimmten Retry-Objekt.
     * 
     * @param originalCallback Der ursprüngliche Callback.
     * @param operation Die zu wiederholende Operation.
     * @param retry Das Retry-Objekt.
     */
    public RetryCallback(Callback<T> originalCallback, Supplier<CompletableFuture<T>> operation, 
                        Retry retry) {
        
        this.originalCallback = originalCallback;
        this.operation = operation;
        this.retry = retry;
        
        // Log info ohne die problematische IntervalFunction
        LOGGER.info("RetryCallback erstellt mit maximalen Versuchen: {}",
                retry.getRetryConfig().getMaxAttempts());
    }
    
    /**
     * Erstellt eine Standardkonfiguration für Wiederholungsversuche.
     * 
     * @return Die Standardkonfiguration.
     */
    private static RetryConfig createDefaultRetry() {
        return RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(1))
                .retryOnException(e -> true) // Wiederholung bei allen Exceptions
                .build();
    }
    
    /**
     * Erstellt eine angepasste Konfiguration für Wiederholungsversuche.
     * 
     * @param maxAttempts Die maximale Anzahl von Versuchen.
     * @param waitDurationMs Die Wartezeit zwischen den Versuchen in Millisekunden.
     * @param retryPredicate Ein Prädikat, das bestimmt, bei welchen Exceptions wiederholt werden soll.
     * @return Die angepasste Konfiguration.
     */
    public static RetryConfig createCustomRetry(int maxAttempts, long waitDurationMs, 
                                             Predicate<Throwable> retryPredicate) {
        return RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .waitDuration(Duration.ofMillis(waitDurationMs))
                .retryOnException(retryPredicate)
                .build();
    }
    
    /**
     * Führt die Operation mit automatischen Wiederholungsversuchen aus.
     */
    public void execute() {
        LOGGER.info("Führe Operation mit Wiederholungsversuchen aus");
        
        Callable<T> retryableOperation = Retry.decorateCallable(retry, () -> {
            try {
                return operation.get().join();
            } catch (Exception e) {
                LOGGER.error("Fehler bei der Ausführung der Operation: {}", e.getMessage(), e);
                throw e;
            }
        });
        
        // Füge Listener für Retry-Ereignisse hinzu
        retry.getEventPublisher()
                .onRetry(event -> LOGGER.info("Wiederholungsversuch #{}: {}", 
                        event.getNumberOfRetryAttempts(), event.getLastThrowable().getMessage()))
                .onSuccess(event -> LOGGER.info("Operation erfolgreich nach {} Versuchen", 
                        event.getNumberOfRetryAttempts() + 1))
                .onError(event -> LOGGER.error("Operation fehlgeschlagen nach {} Versuchen: {}", 
                        event.getNumberOfRetryAttempts() + 1, 
                        event.getLastThrowable().getMessage()));
        
        // Führe die Operation asynchron aus
        CompletableFuture.supplyAsync(() -> {
            try {
                return retryableOperation.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(this::onSuccess)
          .exceptionally(e -> {
              onError(e.getCause() != null ? e.getCause() : e);
              return null;
          });
    }
    
    @Override
    public void onSuccess(T result) {
        LOGGER.info("RetryCallback: Operation erfolgreich abgeschlossen");
        originalCallback.onSuccess(result);
        shutdown();
    }
    
    @Override
    public void onError(Throwable exception) {
        LOGGER.error("RetryCallback: Operation fehlgeschlagen: {}", exception.getMessage(), exception);
        originalCallback.onError(exception);
        shutdown();
    }
    
    /**
     * Stoppt den Scheduler und gibt alle Ressourcen frei.
     */
    private void shutdown() {
        scheduler.shutdown();
    }
}