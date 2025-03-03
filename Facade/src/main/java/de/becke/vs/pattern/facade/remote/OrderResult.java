package de.becke.vs.pattern.facade.remote;

/**
 * Eine Klasse zur Darstellung des Ergebnisses einer Bestellverarbeitung.
 */
public class OrderResult {
    private final boolean success;
    private final String message;
    private final String orderId;
    
    /**
     * Erstellt ein neues OrderResult mit den angegebenen Werten.
     * 
     * @param success Gibt an, ob die Bestellung erfolgreich war
     * @param message Eine Meldung zum Ergebnis
     * @param orderId Die Bestellnummer (oder null bei einem Fehler)
     */
    public OrderResult(boolean success, String message, String orderId) {
        this.success = success;
        this.message = message;
        this.orderId = orderId;
    }
    
    /**
     * Gibt an, ob die Bestellung erfolgreich war.
     * 
     * @return true, wenn die Bestellung erfolgreich war, sonst false
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * Gibt die Meldung zum Ergebnis zurück.
     * 
     * @return Die Meldung zum Ergebnis
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gibt die Bestellnummer zurück.
     * 
     * @return Die Bestellnummer oder null bei einem Fehler
     */
    public String getOrderId() {
        return orderId;
    }
}