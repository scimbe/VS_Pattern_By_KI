package de.becke.vs.pattern.interceptor.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Der Dispatcher ist verantwortlich für die Verwaltung und Ausführung von Interceptoren.
 * 
 * Diese Klasse verwaltet eine Liste von Interceptoren und führt sie in der richtigen
 * Reihenfolge aus, wenn eine Operation ausgeführt wird.
 */
public class Dispatcher {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Dispatcher.class);
    
    /**
     * Die Liste der registrierten Interceptoren.
     * CopyOnWriteArrayList wird verwendet, um Thread-Sicherheit zu gewährleisten,
     * wenn Interceptoren während der Ausführung hinzugefügt oder entfernt werden.
     */
    private final List<Interceptor> interceptors = new CopyOnWriteArrayList<>();
    
    /**
     * Registriert einen Interceptor.
     * 
     * @param interceptor Der zu registrierende Interceptor
     */
    public void registerInterceptor(Interceptor interceptor) {
        if (interceptor != null && !interceptors.contains(interceptor)) {
            interceptors.add(interceptor);
            LOGGER.info("Interceptor registriert: {}", interceptor.getClass().getSimpleName());
        }
    }
    
    /**
     * Entfernt einen Interceptor.
     * 
     * @param interceptor Der zu entfernende Interceptor
     * @return true, wenn der Interceptor vorhanden war und entfernt wurde, sonst false
     */
    public boolean removeInterceptor(Interceptor interceptor) {
        boolean removed = interceptors.remove(interceptor);
        if (removed) {
            LOGGER.info("Interceptor entfernt: {}", interceptor.getClass().getSimpleName());
        }
        return removed;
    }
    
    /**
     * Gibt eine Liste aller registrierten Interceptoren zurück.
     * 
     * @return Eine unveränderliche Liste aller Interceptoren
     */
    public List<Interceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }
    
    /**
     * Führt alle preProcess-Methoden der registrierten Interceptoren aus.
     * 
     * @param context Der Kontext der Ausführung
     * @return true, wenn alle Interceptoren die Fortsetzung erlauben, sonst false
     */
    public boolean preProcess(Context context) {
        LOGGER.debug("Führe preProcess für {} Interceptoren aus", interceptors.size());
        
        for (Interceptor interceptor : interceptors) {
            try {
                boolean shouldContinue = interceptor.preProcess(context);
                if (!shouldContinue) {
                    LOGGER.info("Interceptor {} hat die Ausführung abgebrochen", 
                            interceptor.getClass().getSimpleName());
                    return false;
                }
            } catch (Exception e) {
                LOGGER.error("Fehler beim Ausführen von preProcess in Interceptor {}: {}", 
                        interceptor.getClass().getSimpleName(), e.getMessage(), e);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Führt alle postProcess-Methoden der registrierten Interceptoren in umgekehrter Reihenfolge aus.
     * 
     * @param context Der Kontext der Ausführung
     */
    public void postProcess(Context context) {
        LOGGER.debug("Führe postProcess für {} Interceptoren aus", interceptors.size());
        
        // Kopie erstellen und umkehren, um die Interceptoren in umgekehrter Reihenfolge auszuführen
        List<Interceptor> reversedInterceptors = new ArrayList<>(interceptors);
        Collections.reverse(reversedInterceptors);
        
        for (Interceptor interceptor : reversedInterceptors) {
            try {
                interceptor.postProcess(context);
            } catch (Exception e) {
                LOGGER.error("Fehler beim Ausführen von postProcess in Interceptor {}: {}", 
                        interceptor.getClass().getSimpleName(), e.getMessage(), e);
                // Wir fahren trotz Fehler fort, um allen Interceptoren die Chance zu geben
            }
        }
    }
    
    /**
     * Führt die handleException-Methoden aller registrierten Interceptoren aus,
     * bis ein Interceptor den Fehler behandelt.
     * 
     * @param context Der Kontext der Ausführung
     * @param exception Die aufgetretene Exception
     * @return true, wenn ein Interceptor den Fehler behandelt hat, sonst false
     */
    public boolean handleException(Context context, Exception exception) {
        LOGGER.debug("Führe handleException für {} Interceptoren aus", interceptors.size());
        
        for (Interceptor interceptor : interceptors) {
            try {
                boolean handled = interceptor.handleException(context, exception);
                if (handled) {
                    LOGGER.info("Interceptor {} hat die Exception behandelt", 
                            interceptor.getClass().getSimpleName());
                    return true;
                }
            } catch (Exception e) {
                LOGGER.error("Fehler beim Ausführen von handleException in Interceptor {}: {}", 
                        interceptor.getClass().getSimpleName(), e.getMessage(), e);
                // Wir fahren trotz Fehler fort
            }
        }
        
        return false;
    }
    
    /**
     * Führt eine Operation mit Interceptoren aus.
     * 
     * @param context Der Kontext der Operation
     * @param operation Die auszuführende Operation
     * @return true, wenn die Operation erfolgreich ausgeführt wurde, sonst false
     */
    public boolean dispatch(Context context, Operation operation) {
        try {
            // Pre-Processing
            boolean continueProcessing = preProcess(context);
            if (!continueProcessing) {
                LOGGER.info("Operation abgebrochen durch Interceptor in preProcess");
                context.setSuccessful(false);
                return false;
            }
            
            // Variable zum Verfolgen, ob eine Exception aufgetreten ist und behandelt wurde
            boolean exceptionHandled = false;
            
            // Führe die eigentliche Operation aus
            try {
                Object result = operation.execute(context);
                context.setResult(result);
                context.setEndTime(System.currentTimeMillis());
                context.setSuccessful(true);
            } catch (Exception e) {
                LOGGER.error("Fehler bei der Ausführung der Operation: {}", e.getMessage(), e);
                context.setSuccessful(false);
                context.setEndTime(System.currentTimeMillis());
                
                // Fehlerbehandlung durch Interceptoren
                boolean handled = handleException(context, e);
                if (handled) {
                    // Wenn ein Interceptor den Fehler behandelt hat, setzen wir den Status auf erfolgreich
                    context.setSuccessful(true);
                    exceptionHandled = true;
                } else {
                    // Wenn kein Interceptor den Fehler behandelt hat, werfen wir ihn weiter
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    } else {
                        throw new RuntimeException("Fehler bei der Operation", e);
                    }
                }
            }
            
            // Post-Processing nur durchführen, wenn keine Exception behandelt wurde
            if (!exceptionHandled) {
                postProcess(context);
            }
            
            return context.isSuccessful();
        } catch (Exception e) {
            LOGGER.error("Unbehandelte Exception im Dispatcher: {}", e.getMessage(), e);
            context.setSuccessful(false);
            return false;
        }
    }
    
    /**
     * Funktionales Interface für die auszuführende Operation.
     */
    @FunctionalInterface
    public interface Operation {
        /**
         * Führt die Operation aus.
         * 
         * @param context Der Kontext der Operation
         * @return Das Ergebnis der Operation
         * @throws Exception Wenn ein Fehler auftritt
         */
        Object execute(Context context) throws Exception;
    }
}