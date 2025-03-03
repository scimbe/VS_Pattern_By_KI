package de.becke.vs.pattern.callback.simple;

import de.becke.vs.pattern.callback.common.AsyncOperation;
import de.becke.vs.pattern.callback.common.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Ein einfacher Dienst, der synchrone und asynchrone Operationen mit Callbacks demonstriert.
 */
public class SimpleAsyncService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAsyncService.class);
    
    /**
     * Führt eine synchrone Verarbeitung mit einem Callback durch.
     * In diesem Fall wird der Callback im selben Thread aufgerufen.
     * 
     * @param data Die zu verarbeitenden Daten.
     * @param processor Eine Funktion zur Verarbeitung der Daten.
     * @param callback Der Callback für die Ergebnisse.
     * @param <T> Der Typ der Eingabedaten.
     * @param <R> Der Typ der Ergebnisdaten.
     */
    public <T, R> void processSynchronously(T data, Function<T, R> processor, Callback<R> callback) {
        LOGGER.info("Starte synchrone Verarbeitung von: {}", data);
        
        try {
            // Verarbeite die Daten synchron
            R result = processor.apply(data);
            LOGGER.info("Verarbeitung erfolgreich abgeschlossen");
            
            // Rufe den Erfolgs-Callback auf
            callback.onSuccess(result);
        } catch (Exception e) {
            LOGGER.error("Fehler bei der Verarbeitung: {}", e.getMessage(), e);
            
            // Rufe den Fehler-Callback auf
            callback.onError(e);
        }
    }
    
    /**
     * Führt eine asynchrone Verarbeitung mit einem Callback durch.
     * Der Callback wird in einem separaten Thread aufgerufen, wenn die Verarbeitung abgeschlossen ist.
     * 
     * @param data Die zu verarbeitenden Daten.
     * @param processor Eine Funktion zur Verarbeitung der Daten.
     * @param callback Der Callback für die Ergebnisse.
     * @param <T> Der Typ der Eingabedaten.
     * @param <R> Der Typ der Ergebnisdaten.
     */
    public <T, R> void processAsynchronously(T data, Function<T, R> processor, Callback<R> callback) {
        LOGGER.info("Starte asynchrone Verarbeitung von: {}", data);
        
        // Verwende die AsyncOperation-Klasse für die asynchrone Ausführung
        AsyncOperation<R> operation = new AsyncOperation<R>() {
            @Override
            protected R doExecute() throws Exception {
                return processor.apply(data);
            }
        };
        
        // Führe die Operation asynchron aus
        operation.execute(callback);
    }
    
    /**
     * Ein Beispiel für eine lang andauernde Verarbeitung.
     * 
     * @param input Die Eingabedaten.
     * @return Das Ergebnis der Verarbeitung.
     */
    public static String processWithDelay(String input) {
        try {
            // Simuliere eine länger dauernde Verarbeitung
            LOGGER.info("Verarbeite mit Verzögerung: {}", input);
            TimeUnit.SECONDS.sleep(2);
            return "Verarbeitetes Ergebnis: " + input.toUpperCase();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Verarbeitung unterbrochen", e);
        }
    }
}