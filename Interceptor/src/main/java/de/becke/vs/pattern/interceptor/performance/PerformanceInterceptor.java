package de.becke.vs.pattern.interceptor.performance;

import de.becke.vs.pattern.interceptor.core.Context;
import de.becke.vs.pattern.interceptor.core.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein Interceptor, der die Leistung von Operationen überwacht.
 * 
 * Diese Klasse misst die Ausführungszeit von Operationen und protokolliert
 * Warnungen, wenn die Zeit einen bestimmten Schwellenwert überschreitet.
 */
public class PerformanceInterceptor implements Interceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PerformanceInterceptor.class);
    
    // Standardschwellenwerte in Millisekunden
    private static final long DEFAULT_WARNING_THRESHOLD = 100;
    private static final long DEFAULT_ERROR_THRESHOLD = 500;
    
    private final long warningThreshold;
    private final long errorThreshold;
    
    /**
     * Erstellt einen neuen PerformanceInterceptor mit Standardschwellenwerten.
     */
    public PerformanceInterceptor() {
        this(DEFAULT_WARNING_THRESHOLD, DEFAULT_ERROR_THRESHOLD);
    }
    
    /**
     * Erstellt einen neuen PerformanceInterceptor mit benutzerdefinierten Schwellenwerten.
     * 
     * @param warningThreshold Der Schwellenwert in Millisekunden, ab dem eine Warnung protokolliert wird
     * @param errorThreshold Der Schwellenwert in Millisekunden, ab dem ein Fehler protokolliert wird
     */
    public PerformanceInterceptor(long warningThreshold, long errorThreshold) {
        this.warningThreshold = warningThreshold;
        this.errorThreshold = errorThreshold;
    }
    
    @Override
    public boolean preProcess(Context context) {
        // Wir nutzen die Startzeit aus dem Context
        return true;
    }
    
    @Override
    public void postProcess(Context context) {
        long duration = context.getDuration();
        
        // Speichere die Dauer im Kontext
        context.setAttribute("performance.duration", duration);
        
        // Protokolliere die Dauer
        if (duration >= errorThreshold) {
            LOGGER.error("Leistungsproblem: Operation [ID: {}] dauerte {}ms (Schwellenwert: {}ms)",
                    context.getExecutionId(), duration, errorThreshold);
        } else if (duration >= warningThreshold) {
            LOGGER.warn("Leistungswarnung: Operation [ID: {}] dauerte {}ms (Schwellenwert: {}ms)",
                    context.getExecutionId(), duration, warningThreshold);
        } else {
            LOGGER.debug("Leistung: Operation [ID: {}] dauerte {}ms",
                    context.getExecutionId(), duration);
        }
    }
    
    @Override
    public boolean handleException(Context context, Exception exception) {
        // Wir behandeln keine Exceptions
        return false;
    }
}