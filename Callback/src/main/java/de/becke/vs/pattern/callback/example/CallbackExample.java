package de.becke.vs.pattern.callback.example;

import de.becke.vs.pattern.callback.common.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Beispielimplementierung des Callback-Patterns für verteilte Systeme.
 * Diese Klasse demonstriert verschiedene Arten von Callbacks und deren Anwendung.
 */
public class CallbackExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackExample.class);
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    /**
     * Führt eine synchrone Operation mit einem Callback aus.
     * 
     * @param data Die zu verarbeitenden Daten
     * @param callback Der Callback für die Ergebnisrückgabe
     */
    public void performSynchronousOperation(String data, Callback<String> callback) {
        LOGGER.info("Starte synchrone Operation mit Daten: {}", data);
        
        try {
            // Simuliere eine Verarbeitung
            String result = "Verarbeitet: " + data.toUpperCase();
            LOGGER.info("Synchrone Operation erfolgreich abgeschlossen");
            
            // Rufe den Erfolgs-Callback auf
            callback.onSuccess(result);
        } catch (Exception e) {
            LOGGER.error("Fehler bei synchroner Operation: {}", e.getMessage(), e);
            callback.onError(e);
        }
    }

    /**
     * Führt eine asynchrone Operation mit einem Callback aus.
     * 
     * @param data Die zu verarbeitenden Daten
     * @param callback Der Callback für die Ergebnisrückgabe
     */
    public void performAsynchronousOperation(String data, Callback<String> callback) {
        LOGGER.info("Starte asynchrone Operation mit Daten: {}", data);
        
        EXECUTOR.submit(() -> {
            try {
                // Simuliere eine zeitintensive Verarbeitung
                TimeUnit.SECONDS.sleep(2);
                String result = "Asynchron verarbeitet: " + data.toUpperCase();
                LOGGER.info("Asynchrone Operation erfolgreich abgeschlossen");
                
                // Rufe den Erfolgs-Callback auf
                callback.onSuccess(result);
            } catch (Exception e) {
                LOGGER.error("Fehler bei asynchroner Operation: {}", e.getMessage(), e);
                callback.onError(e);
            }
        });
    }

    /**
     * Führt eine asynchrone Operation aus und gibt ein CompletableFuture zurück.
     * 
     * @param data Die zu verarbeitenden Daten
     * @return Ein CompletableFuture, das das Ergebnis liefert
     */
    public CompletableFuture<String> performAsynchronousOperationWithFuture(String data) {
        LOGGER.info("Starte asynchrone Operation mit Future und Daten: {}", data);
        
        CompletableFuture<String> future = new CompletableFuture<>();
        
        EXECUTOR.submit(() -> {
            try {
                // Simuliere eine zeitintensive Verarbeitung
                TimeUnit.SECONDS.sleep(2);
                String result = "Future verarbeitet: " + data.toUpperCase();
                LOGGER.info("Asynchrone Operation mit Future erfolgreich abgeschlossen");
                
                // Setze das Ergebnis in das Future
                future.complete(result);
            } catch (Exception e) {
                LOGGER.error("Fehler bei asynchroner Operation mit Future: {}", e.getMessage(), e);
                future.completeExceptionally(e);
            }
        });
        
        return future;
    }

    /**
     * Führt eine asynchrone Operation mit mehreren Zwischenergebnissen aus.
     * 
     * @param data Die zu verarbeitenden Daten
     * @param progressCallback Der Callback für Fortschrittsaktualisierungen
     * @param finalCallback Der Callback für das Endergebnis
     */
    public void performProgressOperation(String data, Callback<Integer> progressCallback, Callback<String> finalCallback) {
        LOGGER.info("Starte Operation mit Fortschrittsbenachrichtigungen: {}", data);
        
        EXECUTOR.submit(() -> {
            try {
                // Simuliere eine Operation mit mehreren Schritten
                StringBuilder resultBuilder = new StringBuilder();
                String[] parts = data.split(" ");
                
                for (int i = 0; i < parts.length; i++) {
                    // Simuliere Verarbeitung für jedes Wort
                    TimeUnit.MILLISECONDS.sleep(500);
                    resultBuilder.append(parts[i].toUpperCase()).append(" ");
                    
                    // Berechne und melde Fortschritt
                    int progress = (i + 1) * 100 / parts.length;
                    LOGGER.info("Fortschritt: {}%", progress);
                    progressCallback.onSuccess(progress);
                }
                
                String result = resultBuilder.toString().trim();
                LOGGER.info("Operation mit Fortschrittsbenachrichtigungen erfolgreich abgeschlossen");
                
                // Rufe den Erfolgs-Callback mit dem Endergebnis auf
                finalCallback.onSuccess(result);
            } catch (Exception e) {
                LOGGER.error("Fehler bei Operation mit Fortschrittsbenachrichtigungen: {}", e.getMessage(), e);
                finalCallback.onError(e);
            }
        });
    }

    /**
     * Demonstriert die Verwendung von verschachtelten Callbacks für sequentielle asynchrone Operationen.
     * 
     * @param data Die zu verarbeitenden Daten
     * @param finalCallback Der Callback für das Endergebnis
     */
    public void performNestedCallbacks(String data, Callback<String> finalCallback) {
        LOGGER.info("Starte verschachtelte Callback-Operation mit Daten: {}", data);
        
        // Erste asynchrone Operation
        performAsynchronousOperation(data, new Callback<String>() {
            @Override
            public void onSuccess(String firstResult) {
                LOGGER.info("Erste Operation abgeschlossen, Ergebnis: {}", firstResult);
                
                // Zweite asynchrone Operation, verwendet Ergebnis der ersten
                performAsynchronousOperation(firstResult, new Callback<String>() {
                    @Override
                    public void onSuccess(String secondResult) {
                        LOGGER.info("Zweite Operation abgeschlossen, Ergebnis: {}", secondResult);
                        
                        // Dritte asynchrone Operation, verwendet Ergebnis der zweiten
                        performAsynchronousOperation(secondResult, new Callback<String>() {
                            @Override
                            public void onSuccess(String finalResult) {
                                LOGGER.info("Dritte Operation abgeschlossen, Endergebnis: {}", finalResult);
                                finalCallback.onSuccess(finalResult);
                            }
                            
                            @Override
                            public void onError(Throwable exception) {
                                LOGGER.error("Fehler in dritter Operation: {}", exception.getMessage());
                                finalCallback.onError(exception);
                            }
                        });
                    }
                    
                    @Override
                    public void onError(Throwable exception) {
                        LOGGER.error("Fehler in zweiter Operation: {}", exception.getMessage());
                        finalCallback.onError(exception);
                    }
                });
            }
            
            @Override
            public void onError(Throwable exception) {
                LOGGER.error("Fehler in erster Operation: {}", exception.getMessage());
                finalCallback.onError(exception);
            }
        });
    }

    /**
     * Demonstriert die Verwendung von CompletableFuture für sequentielle asynchrone Operationen.
     * 
     * @param data Die zu verarbeitenden Daten
     * @return Ein CompletableFuture, das das Endergebnis liefert
     */
    public CompletableFuture<String> performChainedFutures(String data) {
        LOGGER.info("Starte verkettete Future-Operation mit Daten: {}", data);
        
        return performAsynchronousOperationWithFuture(data)
                .thenComposeAsync(firstResult -> {
                    LOGGER.info("Erste Future-Operation abgeschlossen, Ergebnis: {}", firstResult);
                    return performAsynchronousOperationWithFuture(firstResult);
                })
                .thenComposeAsync(secondResult -> {
                    LOGGER.info("Zweite Future-Operation abgeschlossen, Ergebnis: {}", secondResult);
                    return performAsynchronousOperationWithFuture(secondResult);
                })
                .thenApplyAsync(finalResult -> {
                    LOGGER.info("Dritte Future-Operation abgeschlossen, Endergebnis: {}", finalResult);
                    return "Verkettetes Ergebnis: " + finalResult;
                });
    }

    /**
     * Beendet den ExecutorService.
     */
    public void shutdown() {
        LOGGER.info("Beende ExecutorService");
        EXECUTOR.shutdown();
        try {
            if (!EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOGGER.error("Shutdown wurde unterbrochen", e);
            EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Einfacher Callback für Demonstrationszwecke.
     *
     * @param <T> Der Typ des Ergebnisses
     */
    public static class SimpleCallback<T> implements Callback<T> {
        private final String name;
        
        public SimpleCallback(String name) {
            this.name = name;
        }
        
        @Override
        public void onSuccess(T result) {
            LOGGER.info("Callback '{}' erfolgreich mit Ergebnis: {}", name, result);
        }
        
        @Override
        public void onError(Throwable exception) {
            LOGGER.error("Callback '{}' fehlgeschlagen: {}", name, exception.getMessage());
        }
    }
}