package de.becke.vs.pattern.interceptor.http;

import de.becke.vs.pattern.interceptor.core.Context;
import de.becke.vs.pattern.interceptor.core.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Ein Interceptor für HTTP-Anfragen, der grundlegende Authentifizierung unterstützt.
 * 
 * Diese Klasse extrahiert Authentifizierungsinformationen aus HTTP-Headers
 * und fügt sie dem Kontext hinzu.
 */
public class AuthenticationInterceptor implements Interceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);
    
    // Speichert Benutzername und Passwort für einfache Authentifizierung
    private final Map<String, String> users = new HashMap<>();
    
    /**
     * Erstellt einen neuen AuthenticationInterceptor ohne vordefinierte Benutzer.
     */
    public AuthenticationInterceptor() {
        // Leerer Konstruktor
    }
    
    /**
     * Fügt einen Benutzer hinzu.
     * 
     * @param username Der Benutzername
     * @param password Das Passwort
     */
    public void addUser(String username, String password) {
        if (username != null && !username.trim().isEmpty() && password != null) {
            users.put(username, password);
        }
    }
    
    @Override
    public boolean preProcess(Context context) {
        // Extrahiere den Authorization-Header
        Map<String, String> headers = context.getAttributeAs("http.headers", Map.class);
        if (headers == null || !headers.containsKey("Authorization")) {
            LOGGER.debug("Keine Authorization-Header in der Anfrage für Operation [ID: {}]", 
                    context.getExecutionId());
            context.setAttribute("security.authenticated", false);
            return true;
        }
        
        String authHeader = headers.get("Authorization");
        
        // Prüfe auf Basic Authentication
        if (authHeader.startsWith("Basic ")) {
            try {
                // Dekodiere den Base64-Teil
                String base64Credentials = authHeader.substring("Basic ".length());
                String credentials = new String(Base64.getDecoder().decode(base64Credentials));
                
                // Trenne Benutzername und Passwort
                String[] parts = credentials.split(":", 2);
                if (parts.length == 2) {
                    String username = parts[0];
                    String password = parts[1];
                    
                    // Authentifiziere
                    boolean isAuthenticated = authenticate(username, password);
                    context.setAttribute("security.authenticated", isAuthenticated);
                    
                    if (isAuthenticated) {
                        context.setAttribute("security.username", username);
                        LOGGER.debug("Benutzer '{}' erfolgreich authentifiziert für Operation [ID: {}]", 
                                username, context.getExecutionId());
                    } else {
                        LOGGER.warn("Authentifizierung fehlgeschlagen für Benutzer '{}' bei Operation [ID: {}]", 
                                username, context.getExecutionId());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Fehler bei der Verarbeitung des Authorization-Headers: {}", e.getMessage(), e);
                context.setAttribute("security.authenticated", false);
            }
        } else if (authHeader.startsWith("Bearer ")) {
            // JWT oder OAuth Token
            String token = authHeader.substring("Bearer ".length());
            boolean isAuthenticated = validateToken(token);
            context.setAttribute("security.authenticated", isAuthenticated);
            
            if (isAuthenticated) {
                LOGGER.debug("Token erfolgreich validiert für Operation [ID: {}]", context.getExecutionId());
                // Hier könnte man weitere Token-Informationen extrahieren
            } else {
                LOGGER.warn("Token-Validierung fehlgeschlagen für Operation [ID: {}]", context.getExecutionId());
            }
        }
        
        return true;
    }
    
    @Override
    public void postProcess(Context context) {
        // Nichts zu tun nach der Operation
    }
    
    @Override
    public boolean handleException(Context context, Exception exception) {
        // Wir behandeln keine Exceptions
        return false;
    }
    
    /**
     * Authentifiziert einen Benutzer mit Benutzername und Passwort.
     * 
     * @param username Der Benutzername
     * @param password Das Passwort
     * @return true, wenn die Authentifizierung erfolgreich war, sonst false
     */
    private boolean authenticate(String username, String password) {
        // Prüfe, ob der Benutzer existiert und das Passwort stimmt
        return users.containsKey(username) && users.get(username).equals(password);
    }
    
    /**
     * Validiert ein Token.
     * 
     * @param token Das zu validierende Token
     * @return true, wenn das Token gültig ist, sonst false
     */
    private boolean validateToken(String token) {
        // Dies ist eine einfache Implementierung - in der Praxis würde man
        // eine richtige JWT/OAuth-Validierung durchführen
        return token != null && !token.isEmpty();
    }
}