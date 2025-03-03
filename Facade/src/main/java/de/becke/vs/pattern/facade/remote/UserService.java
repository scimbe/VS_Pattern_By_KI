package de.becke.vs.pattern.facade.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stellt eine vereinfachte Implementierung eines Benutzerdienstes dar.
 * 
 * In einer realen Anwendung würde diese Klasse Remote-Aufrufe an einen
 * Benutzerdienst durchführen, der Benutzerinformationen verwaltet.
 */
public class UserService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    
    private final String serviceUrl;
    
    /**
     * Erstellt einen neuen UserService mit dem angegebenen Service-Endpunkt.
     * 
     * @param serviceUrl Die URL des Benutzerdienstes
     */
    public UserService(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        LOGGER.debug("UserService initialisiert mit URL: {}", serviceUrl);
    }
    
    /**
     * Ruft Informationen zu einem Benutzer ab.
     * 
     * @param userId Die Benutzer-ID
     * @return Ein User-Objekt oder null, wenn der Benutzer nicht gefunden wurde
     */
    public User getUser(String userId) {
        LOGGER.info("Rufe Benutzer ab: {}", userId);
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(100); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Benutzer-ID
            if (userId == null || userId.isEmpty()) {
                LOGGER.warn("Ungültige Benutzer-ID: {}", userId);
                return null;
            }
            
            // Simuliere einen Benutzer mit Testdaten
            return new User(userId, "Benutzer " + userId, userId + "@example.com");
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler beim Abrufen des Benutzers: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }
    
    /**
     * Aktualisiert Benutzerinformationen.
     * 
     * @param user Die zu aktualisierenden Benutzerinformationen
     * @return true, wenn die Aktualisierung erfolgreich war, sonst false
     */
    public boolean updateUser(User user) {
        LOGGER.info("Aktualisiere Benutzer: {}", user.getId());
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(150); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Benutzerdaten
            if (user == null || user.getId() == null || user.getId().isEmpty()) {
                LOGGER.warn("Ungültige Benutzerdaten: {}", user);
                return false;
            }
            
            // Simuliere eine erfolgreiche Aktualisierung
            return true;
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler beim Aktualisieren des Benutzers: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Erstellt einen neuen Benutzer.
     * 
     * @param name Der Name des Benutzers
     * @param email Die E-Mail-Adresse des Benutzers
     * @return Die ID des erstellten Benutzers oder null bei einem Fehler
     */
    public String createUser(String name, String email) {
        LOGGER.info("Erstelle Benutzer: {} ({})", name, email);
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(200); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Benutzerdaten
            if (name == null || name.isEmpty() || email == null || email.isEmpty()) {
                LOGGER.warn("Ungültige Benutzerdaten: name={}, email={}", name, email);
                return null;
            }
            
            // Simuliere eine Benutzer-ID-Generierung
            String userId = "user_" + System.currentTimeMillis();
            LOGGER.info("Benutzer erstellt mit ID: {}", userId);
            
            return userId;
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler beim Erstellen des Benutzers: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }
}