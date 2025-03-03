package de.becke.vs.pattern.adapter.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein simulierter Produkt-Microservice.
 */
public class ProductService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    
    /**
     * Ruft Produktinformationen ab.
     * 
     * @param productId Die Produkt-ID
     * @return Eine XML-Zeichenkette mit den Produktinformationen
     */
    public String getProduct(String productId) {
        LOGGER.info("ProductService: Rufe Produkt mit ID {} ab", productId);
        
        // Simuliere Datenbankabfrage und gebe XML zurück
        return String.format(
                "<product><id>%s</id><name>Produkt %s</name><price>99.99</price><stock>42</stock></product>", 
                productId, productId);
    }
    
    /**
     * Ruft alle Produkte ab.
     * 
     * @return Eine XML-Zeichenkette mit allen Produkten
     */
    public String getAllProducts() {
        LOGGER.info("ProductService: Rufe alle Produkte ab");
        
        // Simuliere Datenbankabfrage
        return "<products>" +
                "<product><id>1</id><name>Produkt 1</name><price>99.99</price><stock>42</stock></product>" +
                "<product><id>2</id><name>Produkt 2</name><price>149.99</price><stock>23</stock></product>" +
                "<product><id>3</id><name>Produkt 3</name><price>199.99</price><stock>15</stock></product>" +
                "</products>";
    }
    
    /**
     * Erstellt ein neues Produkt.
     * 
     * @param productData Die Produktdaten als XML-Zeichenkette
     * @return Eine XML-Zeichenkette mit dem Ergebnis
     */
    public String createProduct(String productData) {
        LOGGER.info("ProductService: Erstelle neues Produkt mit Daten: {}", productData);
        
        // Simuliere Produkt-Erstellung
        return "<result><status>success</status><message>Produkt erstellt</message></result>";
    }
    
    /**
     * Aktualisiert ein Produkt.
     * 
     * @param productId Die Produkt-ID
     * @param productData Die aktualisierten Produktdaten als XML-Zeichenkette
     * @return Eine XML-Zeichenkette mit dem Ergebnis
     */
    public String updateProduct(String productId, String productData) {
        LOGGER.info("ProductService: Aktualisiere Produkt mit ID {} und Daten: {}", productId, productData);
        
        // Simuliere Produkt-Aktualisierung
        return "<result><status>success</status><message>Produkt aktualisiert</message></result>";
    }
    
    /**
     * Löscht ein Produkt.
     * 
     * @param productId Die Produkt-ID
     * @return Eine XML-Zeichenkette mit dem Ergebnis
     */
    public String deleteProduct(String productId) {
        LOGGER.info("ProductService: Lösche Produkt mit ID {}", productId);
        
        // Simuliere Produkt-Löschung
        return "<result><status>success</status><message>Produkt gelöscht</message></result>";
    }
}