package de.becke.vs.pattern.facade;

import de.becke.vs.pattern.facade.basic.BasicFacade;
import de.becke.vs.pattern.facade.remote.OrderResult;
import de.becke.vs.pattern.facade.remote.RemoteServiceFacade;
import de.becke.vs.pattern.facade.remote.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hauptklasse zur Demonstration des Facade-Patterns in verteilten Systemen.
 */
public class Main {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        LOGGER.info("Starte Demonstration des Facade-Patterns in verteilten Systemen");
        
        // Demonstration der einfachen Facade
        demonstrateBasicFacade();
        
        // Demonstration der Remote-Service-Facade
        demonstrateRemoteServiceFacade();
        
        LOGGER.info("Demonstration abgeschlossen");
    }
    
    /**
     * Demonstriert die Verwendung der einfachen Facade.
     */
    private static void demonstrateBasicFacade() {
        LOGGER.info("\n--- Einfaches Facade-Pattern ---");
        
        // Erstelle eine Facade-Instanz
        BasicFacade facade = new BasicFacade();
        
        // Führe eine Operation über die Facade aus
        String result = facade.processOperation("Beispieleingabe für Verarbeitung");
        LOGGER.info("Ergebnis der Verarbeitung: {}", result);
        
        // Führe eine vereinfachte Operation aus
        String simpleResult = facade.simpleProcess("Einfache Eingabe");
        LOGGER.info("Ergebnis der einfachen Verarbeitung: {}", simpleResult);
        
        // Führe eine Batch-Verarbeitung durch
        String[] inputs = {"Eingabe 1", "Eingabe 2", "Eingabe mit einem fehler"};
        String[] batchResults = facade.batchProcess(inputs);
        
        for (int i = 0; i < inputs.length; i++) {
            LOGGER.info("Batch-Ergebnis für '{}': {}", inputs[i], batchResults[i]);
        }
    }
    
    /**
     * Demonstriert die Verwendung der Remote-Service-Facade.
     */
    private static void demonstrateRemoteServiceFacade() {
        LOGGER.info("\n--- Remote-Service-Facade-Pattern ---");
        
        // Erstelle eine Remote-Service-Facade-Instanz
        RemoteServiceFacade facade = new RemoteServiceFacade();
        
        // Rufe Benutzerinformationen ab
        UserProfile userProfile = facade.getUserProfile("user_123");
        if (userProfile != null) {
            LOGGER.info("Benutzerprofil abgerufen: {} ({})", userProfile.getName(), userProfile.getEmail());
            LOGGER.info("Bestellhistorie: {} Bestellungen", userProfile.getOrderHistory().length);
        } else {
            LOGGER.warn("Benutzerprofil konnte nicht abgerufen werden");
        }
        
        // Prüfe Produktverfügbarkeit
        String[] productIds = {"product_1", "product_2", "product_3"};
        int[] availabilities = facade.checkProductAvailability(productIds);
        for (int i = 0; i < productIds.length; i++) {
            if (i < availabilities.length) {
                LOGGER.info("Verfügbarkeit für {}: {} Einheiten", productIds[i], availabilities[i]);
            }
        }
        
        // Verarbeite eine Bestellung
        OrderResult orderResult = facade.processOrder("user_123", 
                new String[]{"product_1", "product_2"}, 
                new int[]{1, 2});
        
        if (orderResult.isSuccess()) {
            LOGGER.info("Bestellung erfolgreich verarbeitet: {}", orderResult.getOrderId());
            
            // Storniere die Bestellung
            boolean cancelled = facade.cancelOrder(orderResult.getOrderId());
            LOGGER.info("Bestellung storniert: {}", cancelled);
        } else {
            LOGGER.warn("Bestellung fehlgeschlagen: {}", orderResult.getMessage());
        }
    }
}