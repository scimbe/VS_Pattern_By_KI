package de.becke.vs.pattern.interceptor.pipeline;

import de.becke.vs.pattern.interceptor.core.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

/**
 * Ein Pipeline-Interceptor, der Nachrichten validiert.
 * 
 * Diese Klasse prüft Nachrichten mit einer benutzerdefinierten Validierungsfunktion
 * und gibt nur gültige Nachrichten an den nächsten Interceptor weiter.
 */
public class ValidationPipelineInterceptor implements PipelineInterceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationPipelineInterceptor.class);
    
    private final Predicate<Object> validator;
    private final String validationErrorMessage;
    private final String name;
    
    /**
     * Erstellt einen neuen ValidationPipelineInterceptor.
     * 
     * @param validator Die Validierungsfunktion
     * @param validationErrorMessage Die Fehlermeldung bei ungültiger Nachricht
     */
    public ValidationPipelineInterceptor(Predicate<Object> validator, String validationErrorMessage) {
        this(validator, validationErrorMessage, "ValidationInterceptor");
    }
    
    /**
     * Erstellt einen neuen ValidationPipelineInterceptor mit benutzerdefiniertem Namen.
     * 
     * @param validator Die Validierungsfunktion
     * @param validationErrorMessage Die Fehlermeldung bei ungültiger Nachricht
     * @param name Der Name des Interceptors
     */
    public ValidationPipelineInterceptor(Predicate<Object> validator, String validationErrorMessage, String name) {
        this.validator = validator;
        this.validationErrorMessage = validationErrorMessage;
        this.name = name;
    }
    
    @Override
    public boolean intercept(Object message, Context context, InterceptorChain chain) {
        if (message == null) {
            LOGGER.warn("Validierungsfehler: Nachricht ist null");
            context.setAttribute("validation.error", "Nachricht ist null");
            return false;
        }
        
        if (!validator.test(message)) {
            LOGGER.warn("Validierungsfehler: {}", validationErrorMessage);
            context.setAttribute("validation.error", validationErrorMessage);
            return false;
        }
        
        LOGGER.debug("Validierung erfolgreich");
        return chain.proceed(message, context);
    }
    
    @Override
    public String getName() {
        return name;
    }
}