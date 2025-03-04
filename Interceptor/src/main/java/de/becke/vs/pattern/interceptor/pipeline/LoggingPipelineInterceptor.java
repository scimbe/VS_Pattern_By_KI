package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein Pipeline-Interceptor, der Protokollierung durchführt.
 * 
 * Diese Klasse protokolliert die Verarbeitung einer Nachricht, bevor sie
 * an den nächsten Interceptor in der Kette weitergegeben wird.
 */
public class LoggingPipelineInterceptor implements PipelineInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingPipelineInterceptor.class);
    
    private final String name;
    
    /**
     * Erstellt einen neuen LoggingPipelineInterceptor mit Standardnamen.
     */
    public LoggingPipelineInterceptor() {
        this("PipelineLogger");
    }
    
    /**
     * Erstellt einen neuen LoggingPipelineInterceptor mit benutzerdefiniertem Namen.
     * 
     * @param name Der Name für die Protokollierung
     */
    public LoggingPipelineInterceptor(String name) {
        this.name = name;
    }
    
    @Override
    public boolean intercept(Object message, Context context, InterceptorChain chain) {
        LOGGER.info("[{}] Verarbeite Nachricht: {}", name, message);
        
        // Starte Zeitmessung
        long startTime = System.currentTimeMillis();
        
        // Rufe den nächsten Interceptor in der Kette auf
        boolean result = chain.proceed(message, context);
        
        // Berechne Verarbeitungszeit
        long duration = System.currentTimeMillis() - startTime;
        
        if (result) {
            LOGGER.info("[{}] Verarbeitung erfolgreich abgeschlossen in {}ms", name, duration);
        } else {
            LOGGER.warn("[{}] Verarbeitung fehlgeschlagen nach {}ms", name, duration);
        }
        
        return result;
    }
    
    @Override
    public String getName() {
        return name;
    }
}