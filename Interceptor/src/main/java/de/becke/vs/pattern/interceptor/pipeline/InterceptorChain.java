package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert eine Kette von Pipeline-Interceptoren.
 * 
 * Diese Klasse verwaltet die Ausführung einer Sequenz von Interceptoren
 * und ermöglicht die Weitergabe der Kontrolle von einem Interceptor zum nächsten.
 */
public class InterceptorChain {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InterceptorChain.class);
    
    private final List<PipelineInterceptor> interceptors;
    private int currentIndex = 0;
    
    /**
     * Erstellt eine neue Interceptor-Kette mit den angegebenen Interceptoren.
     * 
     * @param interceptors Die zu verkettenden Interceptoren
     */
    public InterceptorChain(List<PipelineInterceptor> interceptors) {
        this.interceptors = new ArrayList<>(interceptors);
    }
    
    /**
     * Führt den nächsten Interceptor in der Kette aus.
     * 
     * @param message Die zu verarbeitende Nachricht
     * @param context Der Kontext der Operation
     * @return true, wenn die Verarbeitung erfolgreich war, false sonst
     */
    public boolean proceed(Object message, Context context) {
        if (currentIndex < interceptors.size()) {
            PipelineInterceptor interceptor = interceptors.get(currentIndex++);
            LOGGER.debug("Führe Pipeline-Interceptor aus: {}", interceptor.getName());
            
            try {
                return interceptor.intercept(message, context, this);
            } catch (Exception e) {
                LOGGER.error("Fehler beim Ausführen des Pipeline-Interceptors {}: {}", 
                        interceptor.getName(), e.getMessage(), e);
                context.setAttribute("pipeline.error", e);
                return false;
            }
        } else {
            // Ende der Kette erreicht
            LOGGER.debug("Pipeline-Verarbeitung abgeschlossen");
            return true;
        }
    }
    
    /**
     * Prüft, ob weitere Interceptoren in der Kette vorhanden sind.
     * 
     * @return true, wenn weitere Interceptoren vorhanden sind, false sonst
     */
    public boolean hasNext() {
        return currentIndex < interceptors.size();
    }
    
    /**
     * Gibt die Gesamtzahl der Interceptoren in der Kette zurück.
     * 
     * @return Die Anzahl der Interceptoren
     */
    public int size() {
        return interceptors.size();
    }
    
    /**
     * Gibt die aktuelle Position in der Kette zurück.
     * 
     * @return Der Index des aktuellen Interceptors
     */
    public int getCurrentIndex() {
        return currentIndex;
    }
    
    /**
     * Setzt die Kette zurück, so dass sie erneut verwendet werden kann.
     */
    public void reset() {
        currentIndex = 0;
    }
}