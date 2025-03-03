package de.becke.vs.pattern.adapter.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein simulierter Benutzer-Microservice.
 */
public class UserService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    
    /**
     * Ruft Benutzerinformationen ab.
     * 
     * @param userId Die Benutzer-ID
     * @return Eine JSON-Zeichenkette mit den Benutzerinformationen
     */
    public String getUser(String userId) {
        LOGGER.info("UserService: Rufe Benutzer mit ID {} ab", userId);
        
        // Simuliere Datenbankabfrage
        return String.format(
                "{ \"id\": \"%s\", \"username\": \"user%s\", \"email\": \"user%s@example.com\", \"role\": \"user\" }", 
                userId, userId, userId);
    }
    
    /**
     * Erstellt einen neuen Benutzer.
     * 
     * @param userData Die Benutzerdaten als JSON-Zeichenkette
     * @return Eine JSON-Zeichenkette mit dem Ergebnis
     */
    public String createUser(String userData) {
        LOGGER.info("UserService: Erstelle neuen Benutzer mit Daten: {}", userData);
        
        // Simuliere Benutzer-Erstellung
        return "{ \"status\": \"success\", \"message\": \"Benutzer erstellt\" }";
    }
    
    /**
     * Aktualisiert einen Benutzer.
     * 
     * @param userId Die Benutzer-ID
     * @param userData Die aktualisierten Benutzerdaten als JSON-Zeichenkette
     * @return Eine JSON-Zeichenkette mit dem Ergebnis
     */
    public String updateUser(String userId, String userData) {
        LOGGER.info("UserService: Aktualisiere Benutzer mit ID {} und Daten: {}", userId, userData);
        
        // Simuliere Benutzer-Aktualisierung
        return "{ \"status\": \"success\", \"message\": \"Benutzer aktualisiert\" }";
    }
    
    /**
     * Löscht einen Benutzer.
     * 
     * @param userId Die Benutzer-ID
     * @return Eine JSON-Zeichenkette mit dem Ergebnis
     */
    public String deleteUser(String userId) {
        LOGGER.info("UserService: Lösche Benutzer mit ID {}", userId);
        
        // Simuliere Benutzer-Löschung
        return "{ \"status\": \"success\", \"message\": \"Benutzer gelöscht\" }";
    }
}