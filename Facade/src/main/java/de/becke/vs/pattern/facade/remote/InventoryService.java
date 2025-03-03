package de.becke.vs.pattern.facade.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stellt eine vereinfachte Implementierung eines Bestandsdienstes dar.
 * 
 * In einer realen Anwendung würde diese Klasse Remote-Aufrufe an einen
 * Bestandsdienst durchführen, der den Lagerbestand verwaltet.
 */
public class InventoryService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);
    
    private final String serviceUrl;
    
    /**
     * Erstellt einen neuen InventoryService mit dem angegebenen Service-Endpunkt.
     * 
     * @param serviceUrl Die URL des Bestandsdienstes
     */
    public InventoryService(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        LOGGER.debug("InventoryService initialisiert mit URL: {}", serviceUrl);
    }
    
    /**
     * Prüft die Verfügbarkeit eines Produkts.
     * 
     * @param productId Die Produkt-ID
     * @return Die verfügbare Menge oder 0, wenn das Produkt nicht verfügbar ist
     */
    public int checkAvailability(String productId) {
        LOGGER.info("Prüfe Verfügbarkeit für Produkt: {}", productId);
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(100); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Produkt-ID
            if (productId == null || productId.isEmpty()) {
                LOGGER.warn("Ungültige Produkt-ID: {}", productId);
                return 0;
            }
            
            // Simuliere einen Bestand basierend auf der Produkt-ID
            if (productId.contains("1")) {
                return 10;
            } else if (productId.contains("2")) {
                return 5;
            } else if (productId.contains("3")) {
                return 0; // Nicht auf Lager
            } else {
                return 3; // Standardmenge
            }
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler beim Prüfen der Verfügbarkeit: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return 0;
        }
    }
    
    /**
     * Aktualisiert den Bestand eines Produkts.
     * 
     * @param productId Die Produkt-ID
     * @param quantityChange Die Bestandsänderung (positiv für Erhöhung, negativ für Verringerung)
     * @return true, wenn die Aktualisierung erfolgreich war, sonst false
     */
    public boolean updateInventory(String productId, int quantityChange) {
        LOGGER.info("Aktualisiere Bestand für Produkt: {} um {}", productId, quantityChange);
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(120); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Parameter
            if (productId == null || productId.isEmpty()) {
                LOGGER.warn("Ungültige Produkt-ID: {}", productId);
                return false;
            }
            
            // Simuliere eine erfolgreiche Aktualisierung
            return true;
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler beim Aktualisieren des Bestands: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Reserviert Produkte für eine Bestellung.
     * 
     * @param productId Die Produkt-ID
     * @param quantity Die zu reservierende Menge
     * @return Eine Reservierungs-ID oder null bei einem Fehler
     */
    public String reserveInventory(String productId, int quantity) {
        LOGGER.info("Reserviere {} Einheiten von Produkt: {}", quantity, productId);
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(150); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Parameter
            if (productId == null || productId.isEmpty() || quantity <= 0) {
                LOGGER.warn("Ungültige Reservierungsparameter: productId={}, quantity={}", 
                        productId, quantity);
                return null;
            }
            
            // Prüfe Verfügbarkeit
            int available = checkAvailability(productId);
            if (available < quantity) {
                LOGGER.warn("Unzureichender Bestand für Reservierung: verfügbar={}, angefordert={}", 
                        available, quantity);
                return null;
            }
            
            // Simuliere eine Reservierungs-ID-Generierung
            String reservationId = "rsv_" + System.currentTimeMillis();
            LOGGER.info("Reservierung erstellt mit ID: {}", reservationId);
            
            return reservationId;
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler bei der Bestandsreservierung: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return null;
        }
    }
}