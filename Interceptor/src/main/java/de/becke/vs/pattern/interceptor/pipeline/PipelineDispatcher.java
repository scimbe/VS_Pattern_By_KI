package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * Dispatcher für die Pipeline-Variante des Interceptor-Patterns.
 * 
 * Der PipelineDispatcher verwaltet eine Reihe von Interceptoren, die als Pipeline
 * angeordnet sind. Jeder Interceptor kann entscheiden, ob er die Verarbeitung an
 * den nächsten Interceptor weitergibt oder abbricht.
 */
public class PipelineDispatcher {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineDispatcher.class);
    
    /**
     * Die Liste der registrierten Pipeline-Interceptoren.
     */
    private final List<PipelineInterceptor> interceptors = new CopyOnWriteArrayList<>();
    
    /**
     * Registriert einen Pipeline-Interceptor.
     * 
     * @param interceptor Der zu registrierende Interceptor
     */
    public void registerInterceptor(PipelineInterceptor interceptor) {
        if (interceptor != null && !interceptors.contains(interceptor)) {
            interceptors.add(interceptor);
            LOGGER.info("Pipeline-Interceptor registriert: {}", interceptor.getName());
        }
    }
    
    /**
     * Entfernt einen Pipeline-Interceptor.
     * 
     * @param interceptor Der zu entfernende Interceptor
     * @return true, wenn der Interceptor vorhanden war und entfernt wurde, sonst false
     */
    public boolean removeInterceptor(PipelineInterceptor interceptor) {
        boolean removed = interceptors.remove(interceptor);
        if (removed) {
            LOGGER.info("Pipeline-Interceptor entfernt: {}", interceptor.getName());
        }
        return removed;
    }
    
    /**
     * Gibt eine Liste aller registrierten Pipeline-Interceptoren zurück.
     * 
     * @return Eine unveränderliche Liste aller Interceptoren
     */
    public List<PipelineInterceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }
    
    /**
     * Verarbeitet eine Nachricht durch die Pipeline der Interceptoren.
     * 
     * @param message Die zu verarbeitende Nachricht
     * @param context Der Kontext der Operation
     * @return true, wenn die Verarbeitung erfolgreich war, false sonst
     */
    public boolean process(Object message, Context context) {
        LOGGER.debug("Starte Pipeline-Verarbeitung für Nachricht: {}", message);
        
        if (interceptors.isEmpty()) {
            LOGGER.warn("Keine Pipeline-Interceptoren registriert");
            return true;
        }
        
        // Erstelle eine Kopie der Interceptoren für diese Verarbeitung
        List<PipelineInterceptor> interceptorsCopy = new ArrayList<>(interceptors);
        
        // Erstelle eine neue Interceptor-Kette
        InterceptorChain chain = new InterceptorChain(interceptorsCopy);
        
        // Starte die Verarbeitung
        try {
            boolean result = chain.proceed(message, context);
            
            if (result) {
                LOGGER.debug("Pipeline-Verarbeitung erfolgreich abgeschlossen");
            } else {
                LOGGER.warn("Pipeline-Verarbeitung wurde abgebrochen");
            }
            
            return result;
        } catch (Exception e) {
            LOGGER.error("Fehler bei der Pipeline-Verarbeitung: {}", e.getMessage(), e);
            context.setAttribute("pipeline.error", e);
            return false;
        }
    }
    
    /**
     * Verarbeitet eine Nachricht durch die Pipeline und transformiert sie.
     * 
     * @param input Die Eingabenachricht
     * @param context Der Kontext der Operation
     * @param transformer Eine Funktion, die das Ergebnis der Pipeline transformiert
     * @param <T> Der Typ der Eingabenachricht
     * @param <R> Der Typ des transformierten Ergebnisses
     * @return Das transformierte Ergebnis oder null, wenn die Verarbeitung fehlschlug
     */
    public <T, R> R processAndTransform(T input, Context context, Function<Context, R> transformer) {
        boolean success = process(input, context);
        
        if (success) {
            return transformer.apply(context);
        } else {
            LOGGER.warn("Transformation übersprungen, da die Pipeline-Verarbeitung fehlschlug");
            return null;
        }
    }
}