package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interceptor für Authentifizierung und Autorisierung.
 */
public class AuthInterceptor implements PipelineInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthInterceptor.class);
    
    @Override
    public boolean intercept(Object message, Context context, InterceptorChain chain) {
        LOGGER.info("Authentifizierungsdienst prüft Berechtigungen");
        
        // Simuliere Authentifizierung
        Message msg = (Message) message;
        String authToken = msg.getHeader("Authorization");
        
        if (authToken != null && authToken.startsWith("Bearer ")) {
            context.setAttribute("auth.authenticated", true);
            context.setAttribute("auth.username", "testuser");
            
            // Authentifizierungsinformationen zur Nachricht hinzufügen
            Message authenticatedMsg = msg.withPayload(msg.getPayload());
            authenticatedMsg.addHeader("X-Authenticated-User", "testuser");
            
            // Ergebnis setzen
            context.setResult("Authentication successful: user=testuser");
            
            // Weiterleitung mit der authentifizierten Nachricht
            return chain.proceed(authenticatedMsg, context);
        } else {
            LOGGER.warn("Authentifizierung fehlgeschlagen");
            context.setAttribute("auth.authenticated", false);
            context.setAttribute("auth.error", "Invalid or missing token");
            context.setResult("Unauthorized");
            return false;
        }
    }
    
    @Override
    public String getName() {
        return "AuthInterceptor";
    }
}