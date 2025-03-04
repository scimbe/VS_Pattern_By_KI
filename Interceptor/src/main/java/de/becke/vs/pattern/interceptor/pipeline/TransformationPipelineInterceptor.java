package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Ein Pipeline-Interceptor, der Nachrichten transformiert.
 * 
 * Diese Klasse transformiert Nachrichten mit einer benutzerdefinierten Funktion,
 * bevor sie an den nächsten Interceptor weitergegeben werden.
 */
public class TransformationPipelineInterceptor implements PipelineInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformationPipelineInterceptor.class);
    
    private final Function<Object, Object> transformer;
    private final String name;
    
    /**
     * Erstellt einen neuen TransformationPipelineInterceptor.
     * 
     * @param transformer Die Transformationsfunktion
     * @param name Der Name der Transformation für die Protokollierung
     */
    public TransformationPipelineInterceptor(Function<Object, Object> transformer, String name) {
        this.transformer = transformer;
        this.name = name;
    }
    
    @Override
    public boolean intercept(Object message, Context context, InterceptorChain chain) {
        LOGGER.debug("Transformiere Nachricht mit '{}'-Transformation", name);
        
        try {
            Object transformedMessage = transformer.apply(message);
            
            // Speichere die ursprüngliche Nachricht im Kontext
            context.setAttribute("transformation.original." + name, message);
            
            // Speichere die transformierte Nachricht im Kontext
            context.setAttribute("transformation.result." + name, transformedMessage);
            
            // Gib die transformierte Nachricht an den nächsten Interceptor weiter
            return chain.proceed(transformedMessage, context);
        } catch (Exception e) {
            LOGGER.error("Fehler bei der Transformation '{}': {}", name, e.getMessage(), e);
            context.setAttribute("transformation.error." + name, e.getMessage());
            return false;
        }
    }
    
    @Override
    public String getName() {
        return "Transformation[" + name + "]";
    }
}