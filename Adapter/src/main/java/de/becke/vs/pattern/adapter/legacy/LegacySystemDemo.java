package de.becke.vs.pattern.adapter.legacy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstriert die Verwendung des Legacy-System-Adapters.
 */
public class LegacySystemDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(LegacySystemDemo.class);
    
    /**
     * Führt die Legacy-System-Adapter-Demonstration durch.
     */
    public void runDemo() {
        LOGGER.info("Starte Legacy-System-Adapter-Demonstration");
        
        // 1. Initialisierung der Systeme
        demonstrateInitialization();
        
        // 2. Benutzerverwaltung über den Adapter
        demonstrateUserManagement();
        
        // 3. Authentifizierung über beide Systeme
        demonstrateAuthentication();
        
        LOGGER.info("Legacy-System-Adapter-Demonstration abgeschlossen");
    }
    
    /**
     * Demonstriert die Initialisierung der Systeme.
     */
    private void demonstrateInitialization() {
        LOGGER.info("\n1. Systeme initialisieren:");
        
        // Erstelle das Legacy-System
        LegacyUserSystem legacySystem = new LegacyUserSystem();
        
        // Erstelle den Adapter für das Legacy-System
        UserManagementSystem modernSystem = new LegacyUserSystemAdapter(legacySystem);
        
        // Zugriff auf einen Benutzer über das Legacy-System direkt
        LegacyUser legacyUser = legacySystem.findUserByLogin("admin");
        LOGGER.info("Legacy-System Benutzer: {}", legacyUser);
        
        // Zugriff auf denselben Benutzer über den Adapter
        User modernUser = modernSystem.getUser(String.valueOf(legacyUser.getUserId()));
        LOGGER.info("Modernes System Benutzer (über Adapter): {}", modernUser);
    }
    
    /**
     * Demonstriert die Benutzerverwaltung über den Adapter.
     */
    private void demonstrateUserManagement() {
        LOGGER.info("\n2. Benutzerverwaltung über den Adapter:");
        
        // Erstelle das Legacy-System und den Adapter
        LegacyUserSystem legacySystem = new LegacyUserSystem();
        UserManagementSystem adapter = new LegacyUserSystemAdapter(legacySystem);
        
        // Erstelle einen neuen Benutzer im modernen Format
        User newUser = new User();
        newUser.setUsername("jsmith");
        newUser.setFirstName("John");
        newUser.setLastName("Smith");
        newUser.setEmail("john.smith@example.com");
        
        // Erstelle den Benutzer über den Adapter
        boolean created = adapter.createUser(newUser);
        LOGGER.info("Benutzer erstellt: {} (ID: {})", created, newUser.getId());
        
        // Rufe den erstellten Benutzer ab
        User retrievedUser = adapter.getUser(newUser.getId());
        LOGGER.info("Abgerufener Benutzer: {}", retrievedUser);
        
        // Aktualisiere den Benutzer
        retrievedUser.setEmail("john.smith.updated@example.com");
        boolean updated = adapter.updateUser(retrievedUser);
        LOGGER.info("Benutzer aktualisiert: {}", updated);
        
        // Rufe den aktualisierten Benutzer ab
        User updatedUser = adapter.getUser(newUser.getId());
        LOGGER.info("Aktualisierter Benutzer: {}", updatedUser);
        
        // Überprüfe, ob der Benutzer auch im Legacy-System aktualisiert wurde
        LegacyUser legacyUser = legacySystem.findUserById(Integer.parseInt(newUser.getId()));
        LOGGER.info("Legacy-Benutzer nach Aktualisierung: {}", legacyUser);
        
        // Lösche den Benutzer
        boolean deleted = adapter.deleteUser(newUser.getId());
        LOGGER.info("Benutzer gelöscht: {}", deleted);
        
        // Versuche, den gelöschten Benutzer abzurufen
        User deletedUser = adapter.getUser(newUser.getId());
        LOGGER.info("Gelöschter Benutzer abrufbar: {}", deletedUser != null);
    }
    
    /**
     * Demonstriert die Authentifizierung über beide Systeme.
     */
    private void demonstrateAuthentication() {
        LOGGER.info("\n3. Authentifizierung über beide Systeme:");
        
        // Erstelle das Legacy-System und den Adapter
        LegacyUserSystem legacySystem = new LegacyUserSystem();
        UserManagementSystem adapter = new LegacyUserSystemAdapter(legacySystem);
        
        // Authentifizierung über das Legacy-System direkt
        LegacyUser legacyAuth = legacySystem.checkCredentials("admin", "admin123");
        LOGGER.info("Legacy-Authentifizierung: {}", legacyAuth != null ? "Erfolgreich" : "Fehlgeschlagen");
        if (legacyAuth != null) {
            LOGGER.info("Legacy-Benutzer nach Anmeldung: {}", legacyAuth);
        }
        
        // Authentifizierung über den Adapter
        User modernAuth = adapter.authenticateUser("admin", "admin123");
        LOGGER.info("Moderne Authentifizierung (über Adapter): {}", modernAuth != null ? "Erfolgreich" : "Fehlgeschlagen");
        if (modernAuth != null) {
            LOGGER.info("Moderner Benutzer nach Anmeldung: {}", modernAuth);
        }
        
        // Fehlgeschlagene Authentifizierung
        LegacyUser legacyAuthFailed = legacySystem.checkCredentials("admin", "wrongpassword");
        LOGGER.info("Legacy-Authentifizierung (falsch): {}", legacyAuthFailed != null ? "Erfolgreich" : "Fehlgeschlagen");
        
        User modernAuthFailed = adapter.authenticateUser("admin", "wrongpassword");
        LOGGER.info("Moderne Authentifizierung (falsch): {}", modernAuthFailed != null ? "Erfolgreich" : "Fehlgeschlagen");
    }
}