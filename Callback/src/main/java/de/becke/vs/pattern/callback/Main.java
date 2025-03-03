package de.becke.vs.pattern.callback;

import de.becke.vs.pattern.callback.common.Callback;
import de.becke.vs.pattern.callback.common.OperationStatus;
import de.becke.vs.pattern.callback.example.CallbackExample;
import de.becke.vs.pattern.callback.polling.PollingAsyncService;
import de.becke.vs.pattern.callback.retry.RetryCallback;
import de.becke.vs.pattern.callback.simple.SimpleAsyncService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Hauptklasse zur Demonstration des Callback-Patterns in verschiedenen Varianten.
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Starte Demonstration des Callback-Patterns");

        try {
            // Demonstration des grundlegenden Callback-Patterns
            demonstrateBasicCallbacks();
            
            // Demonstration des Polling-Patterns
            demonstratePollingCallback();
            
            // Demonstration des Webhook-Patterns
            // Hinweis: Dies erfordert die Möglichkeit, auf einen HTTP-Port zu lauschen
            // demonstrateWebhookCallback();
            
            // Demonstration des Message-basierten Callback-Patterns
            // Hinweis: Dies erfordert einen laufenden RabbitMQ-Server
            // demonstrateMessageBasedCallback();
            
            // Demonstration des Retry-Callback-Patterns
            demonstrateRetryCallback();
            
            // Demonstration des modernen Callback-Patterns mit CompletableFuture
            demonstrateCompletableFutureCallback();

        } catch (Exception e) {
            LOGGER.error("Fehler bei der Demonstration: {}", e.getMessage(), e);
        }

        LOGGER.info("Demonstration abgeschlossen");
    }

    /**
     * Demonstriert die Verwendung von grundlegenden synchronen und asynchronen Callbacks.
     */
    private static void demonstrateBasicCallbacks() {
        LOGGER.info("\n--- Grundlegende Callback-Pattern-Demonstration ---");
        
        CallbackExample callbackExample = new CallbackExample();
        
        // Einfacher synchroner Callback
        LOGGER.info("1. Einfacher synchroner Callback:");
        callbackExample.performSynchronousOperation("test-daten", 
                new CallbackExample.SimpleCallback<>("SynchronerCallback"));
        
        // Einfacher asynchroner Callback
        LOGGER.info("\n2. Einfacher asynchroner Callback:");
        callbackExample.performAsynchronousOperation("test-daten", 
                new CallbackExample.SimpleCallback<>("AsynchronerCallback"));
        
        // Callback mit Fortschrittsbenachrichtigungen
        LOGGER.info("\n3. Callback mit Fortschrittsbenachrichtigungen:");
        callbackExample.performProgressOperation("dies ist ein test",
                new CallbackExample.SimpleCallback<>("Fortschritt"),
                new CallbackExample.SimpleCallback<>("Endergebnis"));
        
        // Verschachtelte Callbacks
        LOGGER.info("\n4. Verschachtelte Callbacks (Callback Hell):");
        callbackExample.performNestedCallbacks("initial-daten",
                new CallbackExample.SimpleCallback<>("FinalerCallback"));
        
        // Warte auf den Abschluss asynchroner Operationen
        sleep(5000);
        
        callbackExample.shutdown();
    }

    /**
     * Demonstriert das Callback-Pattern mit einem Polling-Mechanismus.
     */
    private static void demonstratePollingCallback() {
        LOGGER.info("\n--- Polling-basiertes Callback-Pattern ---");
        
        PollingAsyncService pollingService = new PollingAsyncService();
        
        try {
            // Starte eine lang andauernde Operation
            String operationId = pollingService.startOperation("Eingabedaten", input -> {
                // Simuliere lang andauernde Verarbeitung
                sleep(3000);
                return "Verarbeitungsergebnis für " + input;
            });
            
            LOGGER.info("Operation gestartet mit ID: {}", operationId);
            
            // Polling in einer Schleife
            LOGGER.info("Polling für Ergebnisse...");
            for (int i = 0; i < 10; i++) {
                sleep(500);
                LOGGER.info("Polling-Versuch {}: Status = {}", i + 1, pollingService.getStatus(operationId));
                
                if (pollingService.getStatus(operationId) == OperationStatus.COMPLETED) {
                    String result = pollingService.getResult(operationId);
                    LOGGER.info("Operation abgeschlossen! Ergebnis: {}", result);
                    break;
                } else if (pollingService.getStatus(operationId) == OperationStatus.FAILED) {
                    Throwable error = pollingService.getError(operationId);
                    LOGGER.error("Operation fehlgeschlagen: {}", error.getMessage());
                    break;
                }
            }
            
            // Alternativ: Blockierendes Warten
            LOGGER.info("\nStarte zweite Operation mit blockierendem Warten");
            String operationId2 = pollingService.startOperation("Zweite Eingabe", input -> {
                sleep(2000);
                return "Zweites Ergebnis für " + input;
            });
            
            try {
                String result = pollingService.waitForResult(operationId2, 5000);
                LOGGER.info("Zweite Operation abgeschlossen! Ergebnis: {}", result);
            } catch (Exception e) {
                LOGGER.error("Fehler beim Warten auf Ergebnis: {}", e.getMessage());
            }
            
        } finally {
            pollingService.shutdown();
        }
    }

    /**
     * Demonstriert das Callback-Pattern mit Wiederholungsversuchen.
     */
    private static void demonstrateRetryCallback() {
        LOGGER.info("\n--- Retry-Callback-Pattern ---");
        
        // Erstelle einen SimpleAsyncService
        SimpleAsyncService simpleService = new SimpleAsyncService();
        
        // Definiere einen fehlschlagenden Prozessor
        Function<String, String> failingProcessor = input -> {
            sleep(500);
            // Simuliere einen gelegentlichen Fehler
            double random = Math.random();
            if (random < 0.7) {
                LOGGER.info("Simuliere Fehler bei der Verarbeitung (Wahrscheinlichkeit: {})", random);
                throw new RuntimeException("Simulierter Fehler bei der Verarbeitung");
            }
            return "Verarbeitet: " + input;
        };
        
        // Erstelle einen Callback, der die Ergebnisse protokolliert
        Callback<String> loggingCallback = new Callback<String>() {
            @Override
            public void onSuccess(String result) {
                LOGGER.info("Erfolgreiche Verarbeitung nach Wiederholungsversuchen: {}", result);
            }
            
            @Override
            public void onError(Throwable exception) {
                LOGGER.error("Endgültiger Fehler nach Wiederholungsversuchen: {}", exception.getMessage());
            }
        };
        
        // Erstelle einen RetryCallback
        RetryCallback<String> retryCallback = new RetryCallback<>(
                loggingCallback,
                () -> {
                    CompletableFuture<String> future = new CompletableFuture<>();
                    simpleService.processAsynchronously("Retry-Eingabe", failingProcessor, new Callback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            future.complete(result);
                        }
                        
                        @Override
                        public void onError(Throwable exception) {
                            future.completeExceptionally(exception);
                        }
                    });
                    return future;
                });
        
        // Führe den RetryCallback aus
        LOGGER.info("Starte asynchrone Operation mit Wiederholungsversuchen");
        retryCallback.execute();
        
        // Warte auf den Abschluss der Operation
        sleep(5000);
    }

    /**
     * Demonstriert das moderne Callback-Pattern mit CompletableFuture.
     */
    private static void demonstrateCompletableFutureCallback() {
        LOGGER.info("\n--- CompletableFuture-basiertes Callback-Pattern ---");
        
        CallbackExample callbackExample = new CallbackExample();
        
        try {
            // Einfaches CompletableFuture
            LOGGER.info("1. Einfaches CompletableFuture:");
            CompletableFuture<String> future = callbackExample.performAsynchronousOperationWithFuture("future-daten");
            
            future.thenAccept(result -> {
                LOGGER.info("Future-Ergebnis erhalten: {}", result);
            }).exceptionally(ex -> {
                LOGGER.error("Future-Fehler: {}", ex.getMessage());
                return null;
            });
            
            // Verkettete CompletableFutures
            LOGGER.info("\n2. Verkettete CompletableFutures:");
            callbackExample.performChainedFutures("verkettete-daten")
                    .thenAccept(result -> {
                        LOGGER.info("Verkettetes Future-Ergebnis erhalten: {}", result);
                    })
                    .exceptionally(ex -> {
                        LOGGER.error("Verketteter Future-Fehler: {}", ex.getMessage());
                        return null;
                    });
            
            // Warte auf den Abschluss der asynchronen Operationen
            sleep(10000);
            
        } finally {
            callbackExample.shutdown();
        }
    }

    /**
     * Hilfsmethode zum Schlafen des Threads.
     * 
     * @param millis Die Schlafzeit in Millisekunden
     */
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            LOGGER.error("Thread-Schlaf unterbrochen", e);
            Thread.currentThread().interrupt();
        }
    }
}