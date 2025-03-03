package de.becke.vs.pattern.facade.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stellt eine vereinfachte Implementierung eines Bestelldienstes dar.
 * 
 * In einer realen Anwendung würde diese Klasse Remote-Aufrufe an einen
 * Bestelldienst durchführen, der Bestellungen verwaltet.
 */
public class OrderService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    
    private final String serviceUrl;
    
    /**
     * Erstellt einen neuen OrderService mit dem angegebenen Service-Endpunkt.
     * 
     * @param serviceUrl Die URL des Bestelldienstes
     */
    public OrderService(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        LOGGER.debug("OrderService initialisiert mit URL: {}", serviceUrl);
    }
    
    /**
     * Erstellt eine neue Bestellung.
     * 
     * @param userId Die Benutzer-ID
     * @param productIds Die Produkt-IDs
     * @param quantities Die Mengen der Produkte
     * @return Die ID der erstellten Bestellung oder null bei einem Fehler
     */
    public String createOrder(String userId, String[] productIds, int[] quantities) {
        LOGGER.info("Erstelle Bestellung für Benutzer: {} mit {} Produkten", 
                userId, productIds.length);
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(200); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Parameter
            if (userId == null || productIds == null || quantities == null || 
                    productIds.length != quantities.length || productIds.length == 0) {
                LOGGER.warn("Ungültige Bestellparameter");
                return null;
            }
            
            // Simuliere eine Bestellnummer-Generierung
            String orderId = "order_" + System.currentTimeMillis();
            LOGGER.info("Bestellung erstellt mit ID: {}", orderId);
            
            return orderId;
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler beim Erstellen der Bestellung: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }
    
    /**
     * Ruft Details zu einer Bestellung ab.
     * 
     * @param orderId Die Bestellnummer
     * @return Ein Order-Objekt oder null, wenn die Bestellung nicht gefunden wurde
     */
    public Order getOrderDetails(String orderId) {
        LOGGER.info("Rufe Bestelldetails ab: {}", orderId);
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(150); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Bestellnummer
            if (orderId == null || orderId.isEmpty()) {
                LOGGER.warn("Ungültige Bestellnummer: {}", orderId);
                return null;
            }
            
            // Simuliere eine Bestellung mit Testdaten
            if (orderId.startsWith("order_")) {
                Order order = new Order();
                order.setOrderId(orderId);
                order.setUserId("user_123");
                order.setProductIds(new String[]{"product_1", "product_2"});
                order.setQuantities(new int[]{2, 1});
                order.setStatus("ACTIVE");
                
                return order;
            } else {
                LOGGER.warn("Bestellung nicht gefunden: {}", orderId);
                return null;
            }
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler beim Abrufen der Bestelldetails: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }
    
    /**
     * Ruft alle Bestellungen eines Benutzers ab.
     * 
     * @param userId Die Benutzer-ID
     * @return Ein Array mit den Bestellnummern des Benutzers
     */
    public String[] getUserOrders(String userId) {
        LOGGER.info("Rufe Bestellungen für Benutzer ab: {}", userId);
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(180); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Benutzer-ID
            if (userId == null || userId.isEmpty()) {
                LOGGER.warn("Ungültige Benutzer-ID: {}", userId);
                return new String[0];
            }
            
            // Simuliere Bestellungen mit Testdaten
            return new String[]{"order_1001", "order_1002", "order_1003"};
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler beim Abrufen der Benutzerbestellungen: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return new String[0];
        }
    }
    
    /**
     * Storniert eine Bestellung.
     * 
     * @param orderId Die Bestellnummer
     * @return true, wenn die Stornierung erfolgreich war, sonst false
     */
    public boolean cancelOrder(String orderId) {
        LOGGER.info("Storniere Bestellung: {}", orderId);
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(150); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Bestellnummer
            if (orderId == null || orderId.isEmpty()) {
                LOGGER.warn("Ungültige Bestellnummer: {}", orderId);
                return false;
            }
            
            // Simuliere eine erfolgreiche Stornierung
            return true;
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler beim Stornieren der Bestellung: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}