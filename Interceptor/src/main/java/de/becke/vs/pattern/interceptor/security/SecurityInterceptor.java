package de.becke.vs.pattern.interceptor.security;

import de.becke.vs.pattern.interceptor.core.Context;
import de.becke.vs.pattern.interceptor.core.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Ein Interceptor, der Authentifizierung und Autorisierung durchführt.
 * 
 * Diese Klasse überprüft, ob der Benutzer authentifiziert ist und
 * die erforderlichen Berechtigungen für die Ausführung einer Operation hat.
 */
public class SecurityInterceptor implements Interceptor {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityInterceptor.class);
    
    private final Set<String> requiredRoles;
    private final boolean requireAuthentication;
    
    /**
     * Erstellt einen neuen SecurityInterceptor, der Authentifizierung verlangt.
     */
    public SecurityInterceptor() {
        this(true);
    }
    
    /**
     * Erstellt einen neuen SecurityInterceptor.
     * 
     * @param requireAuthentication Ob Authentifizierung erforderlich ist
     */
    public SecurityInterceptor(boolean requireAuthentication) {
        this.requiredRoles = new HashSet<>();
        this.requireAuthentication = requireAuthentication;
    }
    
    /**
     * Erstellt einen neuen SecurityInterceptor mit bestimmten Rollen.
     * 
     * @param requireAuthentication Ob Authentifizierung erforderlich ist
     * @param requiredRoles Die für den Zugriff erforderlichen Rollen
     */
    public SecurityInterceptor(boolean requireAuthentication, Set<String> requiredRoles) {
        this.requiredRoles = new HashSet<>(requiredRoles);
        this.requireAuthentication = requireAuthentication;
    }
    
    /**
     * Fügt eine erforderliche Rolle hinzu.
     * 
     * @param role Die hinzuzufügende Rolle
     */
    public void addRequiredRole(String role) {
        if (role != null && !role.trim().isEmpty()) {
            requiredRoles.add(role);
        }
    }
    
    @Override
    public boolean preProcess(Context context) {
        // Prüfe Authentifizierung, falls erforderlich
        if (requireAuthentication) {
            Boolean isAuthenticated = context.getAttributeAs("security.authenticated", Boolean.class);
            if (isAuthenticated == null || !isAuthenticated) {
                LOGGER.warn("Sicherheitsverstoß: Nicht authentifizierte Anfrage für Operation [ID: {}]",
                        context.getExecutionId());
                context.setAttribute("security.error", "Authentifizierung erforderlich");
                return false;
            }
        }
        
        // Prüfe Autorisierung, falls Rollen erforderlich sind
        if (!requiredRoles.isEmpty()) {
            Set<?> userRoles = context.getAttributeAs("security.roles", Set.class);
            if (userRoles == null || userRoles.isEmpty()) {
                LOGGER.warn("Sicherheitsverstoß: Keine Rollen verfügbar für Operation [ID: {}]",
                        context.getExecutionId());
                context.setAttribute("security.error", "Autorisierung fehlgeschlagen");
                return false;
            }
            
            // Konvertiere zu Strings und prüfe, ob alle erforderlichen Rollen vorhanden sind
            Set<String> userRolesStr = new HashSet<>();
            for (Object role : userRoles) {
                userRolesStr.add(role.toString());
            }
            
            if (!userRolesStr.containsAll(requiredRoles)) {
                LOGGER.warn("Sicherheitsverstoß: Unzureichende Berechtigungen für Operation [ID: {}]. " +
                        "Benötigt: {}, Vorhanden: {}", context.getExecutionId(), requiredRoles, userRolesStr);
                context.setAttribute("security.error", "Unzureichende Berechtigungen");
                return false;
            }
        }
        
        LOGGER.debug("Sicherheitsprüfung bestanden für Operation [ID: {}]", context.getExecutionId());
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
}