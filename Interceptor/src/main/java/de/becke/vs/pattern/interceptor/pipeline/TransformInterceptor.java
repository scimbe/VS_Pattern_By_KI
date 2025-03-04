package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interceptor für Datentransformation.
 */
public class TransformInterceptor implements PipelineInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformInterceptor.class);
    
    @Override
    public boolean intercept(Object message, Context context, InterceptorChain chain) {
        LOGGER.info("Transformationsdienst verarbeitet Nachricht");
        
        // Simuliere Datentransformation
        Message msg = (Message) message;
        if (msg.getPayload() instanceof String) {
            String payload = (String) msg.getPayload();
            String transformed = payload.toUpperCase() + " [TRANSFORMED]";
            
            // Erstelle eine neue Nachricht mit dem transformierten Payload
            Message transformedMsg = msg.withPayload(transformed);
            transformedMsg.addHeader("X-Transformed", "true");
            
            // Ergebnis setzen
            context.setResult(transformed);
            
            // Weiterleitung mit der transformierten Nachricht
            return chain.proceed(transformedMsg, context);
        } else {
            LOGGER.warn("Nicht unterstützter Payload-Typ: {}", 
                    msg.getPayload() != null ? msg.getPayload().getClass().getName() : "null");
            context.setAttribute("transform.error", "Unsupported payload type");
            return false;
        }
    }
    
    @Override
    public String getName() {
        return "TransformInterceptor";
    }
}