package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;

/**
 * Interface für Pipeline-Interceptoren im verteilten Kontext.
 * 
 * Pipeline-Interceptoren ermöglichen die Verkettung von Verarbeitungsschritten,
 * wobei jeder Interceptor die Kontrolle über die Fortsetzung der Kette hat.
 */
public interface PipelineInterceptor {
    
    /**
     * Verarbeitet eine Nachricht im Kontext einer Interceptor-Kette.
     * 
     * @param message Die zu verarbeitende Nachricht
     * @param context Der Kontext der Operation
     * @param chain Die Interceptor-Kette zur Weitergabe an den nächsten Interceptor
     * @return true, wenn die Verarbeitung erfolgreich war, false sonst
     */
    boolean intercept(Object message, Context context, InterceptorChain chain);
    
    /**
     * Gibt den Namen des Interceptors zurück.
     * 
     * @return Der Name des Interceptors
     */
    default String getName() {
        return this.getClass().getSimpleName();
    }
}