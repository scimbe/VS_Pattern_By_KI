package de.becke.vs.pattern.interceptor.http;

import de.becke.vs.pattern.interceptor.core.Context;
import de.becke.vs.pattern.interceptor.core.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Ein Interceptor für die Cross-Origin Resource Sharing (CORS) Behandlung.
 * 
 * Diese Klasse fügt CORS-Header zu HTTP-Antworten hinzu, um
 * Cross-Origin-Anfragen zu ermöglichen.
 */
public class CorsInterceptor implements Interceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CorsInterceptor.class);
    
    private final String allowedOrigins;
    private final String allowedMethods;
    private final String allowedHeaders;
    private final boolean allowCredentials;
    private final int maxAge;
    
    /**
     * Erstellt einen neuen CorsInterceptor mit Standardkonfiguration.
     */
    public CorsInterceptor() {
        this("*", "GET, POST, PUT, DELETE, OPTIONS", "Content-Type, Authorization", true, 86400);
    }
    
    /**
     * Erstellt einen neuen CorsInterceptor mit benutzerdefinierter Konfiguration.
     * 
     * @param allowedOrigins Erlaubte Ursprünge (z.B. "*" oder "https://example.com")
     * @param allowedMethods Erlaubte HTTP-Methoden
     * @param allowedHeaders Erlaubte HTTP-Header
     * @param allowCredentials Ob Credentials erlaubt sind
     * @param maxAge Max-Age der CORS-Präflight-Anfragen in Sekunden
     */
    public CorsInterceptor(String allowedOrigins, String allowedMethods, String allowedHeaders, 
                          boolean allowCredentials, int maxAge) {
        this.allowedOrigins = allowedOrigins;
        this.allowedMethods = allowedMethods;
        this.allowedHeaders = allowedHeaders;
        this.allowCredentials = allowCredentials;
        this.maxAge = maxAge;
    }
    
    @Override
    public boolean preProcess(Context context) {
        LOGGER.debug("CORS Interceptor: preProcess für Operation [ID: {}]", context.getExecutionId());
        
        // Für OPTIONS-Anfragen setzen wir sofort die CORS-Header und brechen ab
        Map<String, String> requestHeaders = context.getAttributeAs("http.headers", Map.class);
        String method = context.getAttributeAs("http.method", String.class);
        
        if (method != null && method.equalsIgnoreCase("OPTIONS")) {
            LOGGER.debug("CORS Präflight-Anfrage erkannt");
            addCorsHeaders(context);
            
            // Setze ein leeres Ergebnis und brich die weitere Verarbeitung ab
            context.setResult("");
            return false;
        }
        
        return true;
    }
    
    @Override
    public void postProcess(Context context) {
        LOGGER.debug("CORS Interceptor: postProcess für Operation [ID: {}]", context.getExecutionId());
        
        // Füge CORS-Header zur Antwort hinzu
        addCorsHeaders(context);
    }
    
    @Override
    public boolean handleException(Context context, Exception exception) {
        // Wir behandeln keine Exceptions
        return false;
    }
    
    /**
     * Fügt CORS-Header zum Kontext hinzu.
     * 
     * @param context Der Kontext
     */
    private void addCorsHeaders(Context context) {
        Map<String, String> responseHeaders = context.getAttributeAs("http.responseHeaders", Map.class);
        
        if (responseHeaders == null) {
            // Wenn noch keine Response-Headers existieren, erstellen wir eine neue Map
            responseHeaders = new HashMap<>();
            context.setAttribute("http.responseHeaders", responseHeaders);
        }
        
        // Setze die CORS-Header
        responseHeaders.put("Access-Control-Allow-Origin", allowedOrigins);
        responseHeaders.put("Access-Control-Allow-Methods", allowedMethods);
        responseHeaders.put("Access-Control-Allow-Headers", allowedHeaders);
        responseHeaders.put("Access-Control-Allow-Credentials", String.valueOf(allowCredentials));
        responseHeaders.put("Access-Control-Max-Age", String.valueOf(maxAge));
    }
}