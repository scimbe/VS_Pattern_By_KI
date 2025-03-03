package de.becke.vs.pattern.facade.remote;

/**
 * Eine Klasse zur Darstellung eines Benutzerprofils mit erweiterten Informationen.
 */
public class UserProfile {
    private String userId;
    private String name;
    private String email;
    private String[] orderHistory;
    
    /**
     * Gibt die Benutzer-ID zurück.
     * 
     * @return Die Benutzer-ID
     */
    public String getUserId() {
        return userId;
    }
    
    /**
     * Setzt die Benutzer-ID.
     * 
     * @param userId Die Benutzer-ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
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
     * @param name Der Name des Benutzers
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
     * @param email Die E-Mail-Adresse des Benutzers
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gibt die Bestellhistorie des Benutzers zurück.
     * 
     * @return Die Bestellhistorie des Benutzers
     */
    public String[] getOrderHistory() {
        return orderHistory;
    }
    
    /**
     * Setzt die Bestellhistorie des Benutzers.
     * 
     * @param orderHistory Die Bestellhistorie des Benutzers
     */
    public void setOrderHistory(String[] orderHistory) {
        this.orderHistory = orderHistory;
    }
}