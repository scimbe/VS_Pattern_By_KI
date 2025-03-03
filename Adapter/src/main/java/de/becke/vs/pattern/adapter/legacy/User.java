package de.becke.vs.pattern.adapter.legacy;

import java.util.Date;

/**
 * Repräsentiert einen Benutzer im modernen User-Management-System.
 */
public class User {
    
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private Date createdAt;
    private Date lastLogin;
    private boolean active;
    
    /**
     * Erstellt einen neuen Benutzer.
     */
    public User() {
        this.createdAt = new Date();
        this.active = true;
    }
    
    /**
     * Erstellt einen neuen Benutzer mit den angegebenen Daten.
     * 
     * @param id Die ID des Benutzers
     * @param username Der Benutzername
     * @param firstName Der Vorname
     * @param lastName Der Nachname
     * @param email Die E-Mail-Adresse
     */
    public User(String id, String username, String firstName, String lastName, String email) {
        this();
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
    
    /**
     * Gibt die ID des Benutzers zurück.
     * 
     * @return Die ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Setzt die ID des Benutzers.
     * 
     * @param id Die ID
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Gibt den Benutzernamen zurück.
     * 
     * @return Der Benutzername
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Setzt den Benutzernamen.
     * 
     * @param username Der Benutzername
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gibt den Vornamen zurück.
     * 
     * @return Der Vorname
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Setzt den Vornamen.
     * 
     * @param firstName Der Vorname
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Gibt den Nachnamen zurück.
     * 
     * @return Der Nachname
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Setzt den Nachnamen.
     * 
     * @param lastName Der Nachname
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Gibt die E-Mail-Adresse zurück.
     * 
     * @return Die E-Mail-Adresse
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Setzt die E-Mail-Adresse.
     * 
     * @param email Die E-Mail-Adresse
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gibt den Password-Hash zurück.
     * 
     * @return Der Password-Hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }
    
    /**
     * Setzt den Password-Hash.
     * 
     * @param passwordHash Der Password-Hash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    /**
     * Gibt das Erstellungsdatum zurück.
     * 
     * @return Das Erstellungsdatum
     */
    public Date getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Setzt das Erstellungsdatum.
     * 
     * @param createdAt Das Erstellungsdatum
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gibt den Zeitpunkt der letzten Anmeldung zurück.
     * 
     * @return Der Zeitpunkt der letzten Anmeldung
     */
    public Date getLastLogin() {
        return lastLogin;
    }
    
    /**
     * Setzt den Zeitpunkt der letzten Anmeldung.
     * 
     * @param lastLogin Der Zeitpunkt der letzten Anmeldung
     */
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    /**
     * Gibt zurück, ob der Benutzer aktiv ist.
     * 
     * @return true, wenn der Benutzer aktiv ist, sonst false
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Setzt, ob der Benutzer aktiv ist.
     * 
     * @param active true, wenn der Benutzer aktiv sein soll, sonst false
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Gibt den vollständigen Namen des Benutzers zurück.
     * 
     * @return Der vollständige Name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    @Override
    public String toString() {
        return String.format("User [id=%s, username=%s, fullName=%s, email=%s, active=%s]", 
                id, username, getFullName(), email, active);
    }
}