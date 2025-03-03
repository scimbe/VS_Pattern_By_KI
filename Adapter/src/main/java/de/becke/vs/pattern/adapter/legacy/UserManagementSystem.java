package de.becke.vs.pattern.adapter.legacy;

/**
 * Repräsentiert ein modernes User-Management-System.
 * 
 * Dies ist die Schnittstelle, die vom Client in der modernen Anwendung erwartet wird.
 */
public interface UserManagementSystem {
    
    /**
     * Ruft einen Benutzer nach seiner ID ab.
     * 
     * @param userId Die ID des Benutzers
     * @return Der Benutzer oder null, wenn nicht gefunden
     */
    User getUser(String userId);
    
    /**
     * Erstellt einen neuen Benutzer.
     * 
     * @param user Der zu erstellende Benutzer
     * @return true, wenn der Benutzer erfolgreich erstellt wurde, sonst false
     */
    boolean createUser(User user);
    
    /**
     * Aktualisiert einen bestehenden Benutzer.
     * 
     * @param user Der zu aktualisierende Benutzer
     * @return true, wenn der Benutzer erfolgreich aktualisiert wurde, sonst false
     */
    boolean updateUser(User user);
    
    /**
     * Löscht einen Benutzer nach seiner ID.
     * 
     * @param userId Die ID des zu löschenden Benutzers
     * @return true, wenn der Benutzer erfolgreich gelöscht wurde, sonst false
     */
    boolean deleteUser(String userId);
    
    /**
     * Authentifiziert einen Benutzer mit Benutzername und Passwort.
     * 
     * @param username Der Benutzername
     * @param password Das Passwort
     * @return Der authentifizierte Benutzer oder null bei Fehler
     */
    User authenticateUser(String username, String password);
}