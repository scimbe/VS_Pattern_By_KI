package de.becke.vs.pattern.facade.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eine Facade für entfernte Dienste in einem verteilten System.
 * 
 * Diese Klasse kapselt die Komplexität der Kommunikation mit mehreren
 * entfernten Diensten und bietet eine einfache, einheitliche API.
 */
public class RemoteServiceFacade {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteServiceFacade.class);
    
    private final UserService userService;
    private final OrderService orderService;
    private final InventoryService inventoryService;
    private final NotificationService notificationService;
    
    /**
     * Erstellt eine neue RemoteServiceFacade mit Standardverbindungen.
     */
    public RemoteServiceFacade() {
        this.userService = new UserService("https://api.example.com/users");
        this.orderService = new OrderService("https://api.example.com/orders");
        this.inventoryService = new InventoryService("https://api.example.com/inventory");
        this.notificationService = new NotificationService("https://api.example.com/notifications");
        
        LOGGER.info("RemoteServiceFacade initialisiert mit Standardverbindungen");
    }
    
    /**
     * Erstellt eine neue RemoteServiceFacade mit angegebenen Service-Endpunkten.
     * 
     * @param userServiceUrl Die URL des Benutzerdienstes
     * @param orderServiceUrl Die URL des Bestelldienstes
     * @param inventoryServiceUrl Die URL des Bestandsdienstes
     * @param notificationServiceUrl Die URL des Benachrichtigungsdienstes
     */
    public RemoteServiceFacade(String userServiceUrl, String orderServiceUrl, 
                             String inventoryServiceUrl, String notificationServiceUrl) {
        this.userService = new UserService(userServiceUrl);
        this.orderService = new OrderService(orderServiceUrl);
        this.inventoryService = new InventoryService(inventoryServiceUrl);
        this.notificationService = new NotificationService(notificationServiceUrl);
        
        LOGGER.info("RemoteServiceFacade initialisiert mit benutzerdefinierten Verbindungen");
    }
    
    /**
     * Verarbeitet eine Bestellung von Anfang bis Ende.
     * Diese Methode orchestriert mehrere Remote-Aufrufe zu verschiedenen Diensten.
     * 
     * @param userId Die ID des Benutzers
     * @param productIds Die IDs der zu bestellenden Produkte
     * @param quantities Die Mengen der zu bestellenden Produkte
     * @return Ein Ergebnisobjekt mit Informationen zur Bestellung
     */
    public OrderResult processOrder(String userId, String[] productIds, int[] quantities) {
        LOGGER.info("Verarbeite Bestellung für Benutzer: {}", userId);
        
        try {
            // Schritt 1: Benutzer validieren
            User user = userService.getUser(userId);
            if (user == null) {
                LOGGER.warn("Benutzer nicht gefunden: {}", userId);
                return new OrderResult(false, "Benutzer nicht gefunden", null);
            }
            
            // Schritt 2: Bestand prüfen
            for (int i = 0; i < productIds.length; i++) {
                int availability = inventoryService.checkAvailability(productIds[i]);
                if (availability < quantities[i]) {
                    LOGGER.warn("Unzureichender Bestand für Produkt: {}", productIds[i]);
                    return new OrderResult(false, "Unzureichender Bestand für Produkt: " + productIds[i], null);
                }
            }
            
            // Schritt 3: Bestellung erstellen
            String orderId = orderService.createOrder(userId, productIds, quantities);
            
            // Schritt 4: Bestand aktualisieren
            for (int i = 0; i < productIds.length; i++) {
                inventoryService.updateInventory(productIds[i], -quantities[i]);
            }
            
            // Schritt 5: Benutzer benachrichtigen
            notificationService.sendOrderConfirmation(userId, orderId);
            
            LOGGER.info("Bestellung erfolgreich verarbeitet: {}", orderId);
            return new OrderResult(true, "Bestellung erfolgreich", orderId);
            
        } catch (Exception e) {
            LOGGER.error("Fehler bei der Verarbeitung der Bestellung: {}", e.getMessage(), e);
            return new OrderResult(false, "Fehler: " + e.getMessage(), null);
        }
    }
    
    /**
     * Ruft Benutzerinformationen einschließlich Bestellhistorie und Präferenzen ab.
     * 
     * @param userId Die Benutzer-ID
     * @return Ein UserProfile-Objekt mit umfassenden Benutzerinformationen
     */
    public UserProfile getUserProfile(String userId) {
        LOGGER.info("Hole Benutzerprofil für: {}", userId);
        
        try {
            // Hole Benutzerinformationen
            User user = userService.getUser(userId);
            if (user == null) {
                LOGGER.warn("Benutzer nicht gefunden: {}", userId);
                return null;
            }
            
            // Hole Bestellhistorie
            String[] orderIds = orderService.getUserOrders(userId);
            
            // Erstelle und fülle das Benutzerprofil
            UserProfile profile = new UserProfile();
            profile.setUserId(userId);
            profile.setName(user.getName());
            profile.setEmail(user.getEmail());
            profile.setOrderHistory(orderIds);
            
            LOGGER.info("Benutzerprofil erfolgreich abgerufen");
            return profile;
            
        } catch (Exception e) {
            LOGGER.error("Fehler beim Abrufen des Benutzerprofils: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Prüft die Verfügbarkeit mehrerer Produkte.
     * 
     * @param productIds Die zu prüfenden Produkt-IDs
     * @return Ein Array mit den verfügbaren Mengen für jedes Produkt
     */
    public int[] checkProductAvailability(String[] productIds) {
        LOGGER.info("Prüfe Verfügbarkeit für {} Produkte", productIds.length);
        
        int[] availabilities = new int[productIds.length];
        
        try {
            for (int i = 0; i < productIds.length; i++) {
                availabilities[i] = inventoryService.checkAvailability(productIds[i]);
            }
            
            return availabilities;
        } catch (Exception e) {
            LOGGER.error("Fehler bei der Verfügbarkeitsprüfung: {}", e.getMessage(), e);
            return new int[0];
        }
    }
    
    /**
     * Storniert eine bestehende Bestellung und aktualisiert den Bestand entsprechend.
     * 
     * @param orderId Die ID der zu stornierenden Bestellung
     * @return true, wenn die Stornierung erfolgreich war, sonst false
     */
    public boolean cancelOrder(String orderId) {
        LOGGER.info("Storniere Bestellung: {}", orderId);
        
        try {
            // Bestelldetails abrufen
            Order order = orderService.getOrderDetails(orderId);
            if (order == null) {
                LOGGER.warn("Bestellung nicht gefunden: {}", orderId);
                return false;
            }
            
            // Bestellung stornieren
            boolean cancelled = orderService.cancelOrder(orderId);
            if (!cancelled) {
                LOGGER.warn("Stornierung der Bestellung fehlgeschlagen: {}", orderId);
                return false;
            }
            
            // Bestand zurückgeben
            String[] productIds = order.getProductIds();
            int[] quantities = order.getQuantities();
            
            for (int i = 0; i < productIds.length; i++) {
                inventoryService.updateInventory(productIds[i], quantities[i]);
            }
            
            // Benutzer benachrichtigen
            notificationService.sendOrderCancellation(order.getUserId(), orderId);
            
            LOGGER.info("Bestellung erfolgreich storniert: {}", orderId);
            return true;
            
        } catch (Exception e) {
            LOGGER.error("Fehler bei der Stornierung der Bestellung: {}", e.getMessage(), e);
            return false;
        }
    }
}