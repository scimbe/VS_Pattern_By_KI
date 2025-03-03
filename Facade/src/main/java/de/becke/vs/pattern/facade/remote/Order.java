package de.becke.vs.pattern.facade.remote;

/**
 * Eine Klasse zur Darstellung einer Bestellung.
 */
public class Order {
    private String orderId;
    private String userId;
    private String[] productIds;
    private int[] quantities;
    private String status;
    
    /**
     * Gibt die Bestellnummer zurück.
     * 
     * @return Die Bestellnummer
     */
    public String getOrderId() {
        return orderId;
    }
    
    /**
     * Setzt die Bestellnummer.
     * 
     * @param orderId Die Bestellnummer
     */
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
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
     * Gibt die Produkt-IDs zurück.
     * 
     * @return Die Produkt-IDs
     */
    public String[] getProductIds() {
        return productIds;
    }
    
    /**
     * Setzt die Produkt-IDs.
     * 
     * @param productIds Die Produkt-IDs
     */
    public void setProductIds(String[] productIds) {
        this.productIds = productIds;
    }
    
    /**
     * Gibt die Mengen der Produkte zurück.
     * 
     * @return Die Mengen der Produkte
     */
    public int[] getQuantities() {
        return quantities;
    }
    
    /**
     * Setzt die Mengen der Produkte.
     * 
     * @param quantities Die Mengen der Produkte
     */
    public void setQuantities(int[] quantities) {
        this.quantities = quantities;
    }
    
    /**
     * Gibt den Status der Bestellung zurück.
     * 
     * @return Der Status der Bestellung
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Setzt den Status der Bestellung.
     * 
     * @param status Der Status der Bestellung
     */
    public void setStatus(String status) {
        this.status = status;
    }
}