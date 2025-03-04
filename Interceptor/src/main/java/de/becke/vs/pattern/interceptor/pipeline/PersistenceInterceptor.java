package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interceptor für Datenpersistenz.
 */
public class PersistenceInterceptor implements PipelineInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceInterceptor.class);
    
    @Override
    public boolean intercept(Object message, Context context, InterceptorChain chain) {
        LOGGER.info("Persistenzdienst speichert Nachricht");
        
        // Simuliere Datenpersistenz
        Message msg = (Message) message;
        String entityId = "DB-" + System.currentTimeMillis();
        
        context.setAttribute("persistence.stored", true);
        context.setAttribute("persistence.entityId", entityId);
        
        // Nachricht für die weitere Verarbeitung anpassen
        Message persistedMsg = msg.withPayload(msg.getPayload());
        persistedMsg.addHeader("X-Entity-ID", entityId);
        persistedMsg.addHeader("X-Stored", "true");
        
        // Ergebnis setzen
        context.setResult("Message stored with ID: " + entityId);
        
        // Kette fortsetzen
        return chain.proceed(persistedMsg, context);
    }
    
    @Override
    public String getName() {
        return "PersistenceInterceptor";
    }
}