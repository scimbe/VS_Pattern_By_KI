package de.becke.vs.pattern.adapter.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein Adapter, der das Legacy-Benutzersystem in die moderne UserManagementSystem-Schnittstelle integriert.
 */
public class LegacyUserSystemAdapter implements UserManagementSystem {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyUserSystemAdapter.class);
    
    private final LegacyUserSystem legacySystem;
    
    /**
     * Erstellt einen neuen Adapter für das Legacy-Benutzersystem.
     * 
     * @param legacySystem Das zu adaptierende Legacy-Benutzersystem
     */
    public LegacyUserSystemAdapter(LegacyUserSystem legacySystem) {
        this.legacySystem = legacySystem;
        LOGGER.info("LegacyUserSystemAdapter initialisiert");
    }
    
    @Override
    public User getUser(String userId) {
        LOGGER.info("Adapter: Rufe Benutzer mit ID {} ab", userId);
        
        try {
            // Konvertiere String-ID zu int für das Legacy-System
            int legacyId = Integer.parseInt(userId);
            
            // Rufe Benutzer vom Legacy-System ab
            LegacyUser legacyUser = legacySystem.findUserById(legacyId);
            
            if (legacyUser == null) {
                LOGGER.warn("Benutzer mit ID {} nicht gefunden", userId);
                return null;
            }
            
            // Konvertiere Legacy-Benutzer in modernen Benutzer
            return convertLegacyUser(legacyUser);
            
        } catch (NumberFormatException e) {
            LOGGER.error("Ungültige Benutzer-ID: {}", userId);
            return null;
        }
    }
    
    @Override
    public boolean createUser(User user) {
        LOGGER.info("Adapter: Erstelle neuen Benutzer: {}", user.getUsername());
        
        // Extrahiere Vor- und Nachname aus dem vollständigen Namen
        String fullName = user.getFirstName() + " " + user.getLastName();
        
        // Füge Benutzer zum Legacy-System hinzu
        int userId = legacySystem.addUser(
                user.getUsername(),
                fullName,
                user.getEmail(),
                // In einer realen Implementierung müsste das Passwort aus dem Hash extrahiert werden
                // Hier verwenden wir eine Dummy-Implementierung
                "DefaultPass123"
        );
        
        if (userId == -1) {
            LOGGER.warn("Fehler beim Erstellen des Benutzers");
            return false;
        }
        
        // Setze die generierte ID im Benutzer
        user.setId(String.valueOf(userId));
        
        return true;
    }
    
    @Override
    public boolean updateUser(User user) {
        LOGGER.info("Adapter: Aktualisiere Benutzer: {}", user.getUsername());
        
        try {
            // Konvertiere String-ID zu int für das Legacy-System
            int legacyId = Integer.parseInt(user.getId());
            
            // Kombiniere Vor- und Nachname für das Legacy-System
            String fullName = user.getFirstName() + " " + user.getLastName();
            
            // Aktualisiere Benutzer im Legacy-System
            return legacySystem.updateUser(
                    legacyId,
                    fullName,
                    user.getEmail(),
                    null // Kein Passwort-Update in diesem Beispiel
            );
            
        } catch (NumberFormatException e) {
            LOGGER.error("Ungültige Benutzer-ID: {}", user.getId());
            return false;
        }
    }
    
    @Override
    public boolean deleteUser(String userId) {
        LOGGER.info("Adapter: Lösche Benutzer mit ID {}", userId);
        
        try {
            // Konvertiere String-ID zu int für das Legacy-System
            int legacyId = Integer.parseInt(userId);
            
            // Entferne Benutzer aus dem Legacy-System
            return legacySystem.removeUser(legacyId);
            
        } catch (NumberFormatException e) {
            LOGGER.error("Ungültige Benutzer-ID: {}", userId);
            return false;
        }
    }
    
    @Override
    public User authenticateUser(String username, String password) {
        LOGGER.info("Adapter: Authentifiziere Benutzer: {}", username);
        
        // Überprüfe Anmeldedaten im Legacy-System
        LegacyUser legacyUser = legacySystem.checkCredentials(username, password);
        
        if (legacyUser == null) {
            LOGGER.warn("Authentifizierung fehlgeschlagen für Benutzer: {}", username);
            return null;
        }
        
        // Konvertiere Legacy-Benutzer in modernen Benutzer
        User user = convertLegacyUser(legacyUser);
        
        // Aktualisiere den letzten Login-Zeitpunkt
        user.setLastLogin(legacyUser.getLastLogin());
        
        return user;
    }
    
    /**
     * Konvertiert einen Legacy-Benutzer in einen modernen Benutzer.
     * 
     * @param legacyUser Der zu konvertierende Legacy-Benutzer
     * @return Der konvertierte moderne Benutzer
     */
    private User convertLegacyUser(LegacyUser legacyUser) {
        User user = new User();
        
        // Setze ID als String
        user.setId(String.valueOf(legacyUser.getUserId()));
        
        // Übertrage einfache Felder
        user.setUsername(legacyUser.getLogin());
        user.setEmail(legacyUser.getEmail());
        user.setPasswordHash(legacyUser.getPassword()); // In einer realen Implementierung müsste der Hash angepasst werden
        user.setCreatedAt(legacyUser.getCreationDate());
        user.setLastLogin(legacyUser.getLastLogin());
        
        // Konvertiere Status von int zu boolean
        user.setActive(legacyUser.getStatus() == 1);
        
        // Teile den Namen in Vor- und Nachname auf
        String fullName = legacyUser.getName();
        String[] nameParts = fullName.split(" ", 2);
        
        if (nameParts.length > 0) {
            user.setFirstName(nameParts[0]);
            
            if (nameParts.length > 1) {
                user.setLastName(nameParts[1]);
            } else {
                user.setLastName(""); // Fallback, wenn kein Nachname vorhanden ist
            }
        }
        
        return user;
    }
    
    /**
     * Konvertiert einen modernen Benutzer in einen Legacy-Benutzer.
     * 
     * @param user Der zu konvertierende moderne Benutzer
     * @return Der konvertierte Legacy-Benutzer
     */
    private LegacyUser convertToLegacyUser(User user) {
        LegacyUser legacyUser = new LegacyUser();
        
        // Konvertiere ID von String zu int
        try {
            legacyUser.setUserId(Integer.parseInt(user.getId()));
        } catch (NumberFormatException e) {
            LOGGER.warn("Konnte ID nicht konvertieren: {}", user.getId());
        }
        
        // Übertrage einfache Felder
        legacyUser.setLogin(user.getUsername());
        legacyUser.setEmail(user.getEmail());
        legacyUser.setPassword(user.getPasswordHash()); // In einer realen Implementierung müsste der Hash angepasst werden
        legacyUser.setCreationDate(user.getCreatedAt());
        legacyUser.setLastLogin(user.getLastLogin());
        
        // Konvertiere Status von boolean zu int
        legacyUser.setStatus(user.isActive() ? 1 : 0);
        
        // Kombiniere Vor- und Nachname
        legacyUser.setName(user.getFirstName() + " " + user.getLastName());
        
        return legacyUser;
    }
}