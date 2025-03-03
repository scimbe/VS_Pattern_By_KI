package de.becke.vs.pattern.adapter.legacy;

import java.util.Date;

/**
 * Repräsentiert einen Benutzer im Legacy-Benutzersystem.
 * 
 * Diese Klasse verwendet eine andere Struktur als die moderne User-Klasse.
 */
public class LegacyUser {
    
    private int userId;      // Numerische ID statt String
    private String login;    // Login statt username
    private String name;     // Ein einzelnes Namensfeld statt firstName und lastName
    private String email;
    private String password; // Direktes Passwort-Feld statt passwordHash
    private Date creationDate; // Anders benannt als in User
    private Date lastLogin;
    private int status;      // Numerischer Status (1=aktiv, 0=inaktiv) statt boolean
    
    /**
     * Erstellt einen neuen Legacy-Benutzer.
     */
    public LegacyUser() {
    }
    
    /**
     * Gibt die Benutzer-ID zurück.
     * 
     * @return Die Benutzer-ID
     */
    public int getUserId() {
        return userId;
    }
    
    /**
     * Setzt die Benutzer-ID.
     * 
     * @param userId Die Benutzer-ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    /**
     * Gibt den Login-Namen zurück.
     * 
     * @return Der Login-Name
     */
    public String getLogin() {
        return login;
    }
    
    /**
     * Setzt den Login-Namen.
     * 
     * @param login Der Login-Name
     */
    public void setLogin(String login) {
        this.login = login;
    }
    
    /**
     * Gibt den Namen zurück.
     * 
     * @return Der Name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Setzt den Namen.
     * 
     * @param name Der Name
     */
    public void setName(String name) {
        this.name = name;
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
     * Gibt das Passwort zurück.
     * 
     * @return Das Passwort
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Setzt das Passwort.
     * 
     * @param password Das Passwort
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Gibt das Erstellungsdatum zurück.
     * 
     * @return Das Erstellungsdatum
     */
    public Date getCreationDate() {
        return creationDate;
    }
    
    /**
     * Setzt das Erstellungsdatum.
     * 
     * @param creationDate Das Erstellungsdatum
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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
     * Gibt den Status zurück.
     * 
     * @return Der Status (1=aktiv, 0=inaktiv)
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * Setzt den Status.
     * 
     * @param status Der Status (1=aktiv, 0=inaktiv)
     */
    public void setStatus(int status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return String.format("LegacyUser [userId=%d, login=%s, name=%s, email=%s, status=%d]", 
                userId, login, name, email, status);
    }
}