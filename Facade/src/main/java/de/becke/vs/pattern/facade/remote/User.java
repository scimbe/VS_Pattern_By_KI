package de.becke.vs.pattern.facade.remote;

/**
 * Eine Klasse zur Darstellung eines Benutzers.
 */
public class User {
    private final String id;
    private String name;
    private String email;
    
    /**
     * Erstellt einen neuen Benutzer mit den angegebenen Werten.
     * 
     * @param id Die Benutzer-ID
     * @param name Der Name des Benutzers
     * @param email Die E-Mail-Adresse des Benutzers
     */
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    /**
     * Gibt die Benutzer-ID zurück.
     * 
     * @return Die Benutzer-ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Gibt den Namen des Benutzers zurück.
     * 
     * @return Der Name des Benutzers
     */
    public String getName() {
        return name;
    }
    
    /**
     * Setzt den Namen des Benutzers.
     * 
     * @param name Der neue Name des Benutzers
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gibt die E-Mail-Adresse des Benutzers zurück.
     * 
     * @return Die E-Mail-Adresse des Benutzers
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Setzt die E-Mail-Adresse des Benutzers.
     * 
     * @param email Die neue E-Mail-Adresse des Benutzers
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}