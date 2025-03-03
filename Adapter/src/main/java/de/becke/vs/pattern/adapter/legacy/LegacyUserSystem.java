package de.becke.vs.pattern.adapter.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Ein Legacy-Benutzersystem mit veralteter API.
 * 
 * Diese Klasse repräsentiert ein älteres System, das adaptiert werden soll.
 * Die Methoden und Datenstrukturen sind nicht mit dem modernen UserManagementSystem kompatibel.
 */
public class LegacyUserSystem {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyUserSystem.class);
    
    // Vereinfachte In-Memory-Benutzerdatenbank
    private final Map<Integer, LegacyUser> users = new HashMap<>();
    private int nextUserId = 1000;
    
    /**
     * Erstellt ein neues Legacy-Benutzersystem mit einigen Beispielbenutzern.
     */
    public LegacyUserSystem() {
        LOGGER.info("Initialisiere Legacy-Benutzersystem");
        
        // Füge einige Beispielbenutzer hinzu
        addUser("admin", "Administrator", "admin@example.com", "admin123");
        addUser("jdoe", "John Doe", "john@example.com", "password");
        addUser("asmith", "Alice Smith", "alice@example.com", "secure123");
    }
    
    /**
     * Fügt einen neuen Benutzer hinzu.
     * 
     * @param login Der Login-Name
     * @param name Der vollständige Name
     * @param email Die E-Mail-Adresse
     * @param password Das Passwort
     * @return Die ID des erstellten Benutzers oder -1 bei Fehler
     */
    public int addUser(String login, String name, String email, String password) {
        // Überprüfe, ob Login bereits existiert
        for (LegacyUser user : users.values()) {
            if (user.getLogin().equals(login)) {
                LOGGER.warn("Login '{}' existiert bereits", login);
                return -1;
            }
        }
        
        // Erstelle einen neuen Benutzer
        int userId = nextUserId++;
        LegacyUser user = new LegacyUser();
        user.setUserId(userId);
        user.setLogin(login);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(hashPassword(password));
        user.setCreationDate(new Date());
        user.setStatus(1); // 1 = aktiv
        
        // Füge den Benutzer zur Datenbank hinzu
        users.put(userId, user);
        LOGGER.info("Benutzer hinzugefügt: {} (ID: {})", login, userId);
        
        return userId;
    }
    
    /**
     * Findet einen Benutzer anhand seiner ID.
     * 
     * @param userId Die Benutzer-ID
     * @return Der gefundene Benutzer oder null, wenn nicht gefunden
     */
    public LegacyUser findUserById(int userId) {
        LOGGER.info("Suche Benutzer mit ID: {}", userId);
        return users.get(userId);
    }
    
    /**
     * Findet einen Benutzer anhand seines Logins.
     * 
     * @param login Der Login-Name
     * @return Der gefundene Benutzer oder null, wenn nicht gefunden
     */
    public LegacyUser findUserByLogin(String login) {
        LOGGER.info("Suche Benutzer mit Login: {}", login);
        
        for (LegacyUser user : users.values()) {
            if (user.getLogin().equals(login)) {
                return user;
            }
        }
        
        return null;
    }
    
    /**
     * Aktualisiert einen bestehenden Benutzer.
     * 
     * @param userId Die Benutzer-ID
     * @param name Der neue Name (oder null, wenn unverändert)
     * @param email Die neue E-Mail-Adresse (oder null, wenn unverändert)
     * @param password Das neue Passwort (oder null, wenn unverändert)
     * @return true, wenn der Benutzer erfolgreich aktualisiert wurde, sonst false
     */
    public boolean updateUser(int userId, String name, String email, String password) {
        LOGGER.info("Aktualisiere Benutzer mit ID: {}", userId);
        
        LegacyUser user = users.get(userId);
        if (user == null) {
            LOGGER.warn("Benutzer mit ID {} nicht gefunden", userId);
            return false;
        }
        
        if (name != null) {
            user.setName(name);
        }
        
        if (email != null) {
            user.setEmail(email);
        }
        
        if (password != null) {
            user.setPassword(hashPassword(password));
        }
        
        return true;
    }
    
    /**
     * Entfernt einen Benutzer.
     * 
     * @param userId Die ID des zu entfernenden Benutzers
     * @return true, wenn der Benutzer erfolgreich entfernt wurde, sonst false
     */
    public boolean removeUser(int userId) {
        LOGGER.info("Entferne Benutzer mit ID: {}", userId);
        
        LegacyUser user = users.remove(userId);
        return user != null;
    }
    
    /**
     * Deaktiviert einen Benutzer.
     * 
     * @param userId Die ID des zu deaktivierenden Benutzers
     * @return true, wenn der Benutzer erfolgreich deaktiviert wurde, sonst false
     */
    public boolean deactivateUser(int userId) {
        LOGGER.info("Deaktiviere Benutzer mit ID: {}", userId);
        
        LegacyUser user = users.get(userId);
        if (user == null) {
            LOGGER.warn("Benutzer mit ID {} nicht gefunden", userId);
            return false;
        }
        
        user.setStatus(0); // 0 = inaktiv
        return true;
    }
    
    /**
     * Überprüft die Anmeldedaten eines Benutzers.
     * 
     * @param login Der Login-Name
     * @param password Das Passwort
     * @return Der Benutzer, wenn die Anmeldung erfolgreich war, sonst null
     */
    public LegacyUser checkCredentials(String login, String password) {
        LOGGER.info("Überprüfe Anmeldedaten für Login: {}", login);
        
        LegacyUser user = findUserByLogin(login);
        if (user == null) {
            LOGGER.warn("Benutzer mit Login {} nicht gefunden", login);
            return null;
        }
        
        // Überprüfe, ob Benutzer aktiv ist
        if (user.getStatus() != 1) {
            LOGGER.warn("Benutzer {} ist nicht aktiv", login);
            return null;
        }
        
        // Überprüfe Passwort
        String hashedPassword = hashPassword(password);
        if (!user.getPassword().equals(hashedPassword)) {
            LOGGER.warn("Ungültiges Passwort für Benutzer {}", login);
            return null;
        }
        
        // Aktualisiere Login-Zeit
        user.setLastLogin(new Date());
        
        return user;
    }
    
    /**
     * Simuliert eine einfache Passwort-Hash-Funktion.
     * 
     * @param password Das zu hashende Passwort
     * @return Der Passwort-Hash
     */
    private String hashPassword(String password) {
        // In einer realen Implementierung würde hier ein sicherer Hash-Algorithmus verwendet werden
        return "HASH:" + password.hashCode();
    }
}