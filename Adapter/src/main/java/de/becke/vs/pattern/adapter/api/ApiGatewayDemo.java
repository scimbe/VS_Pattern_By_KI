package de.becke.vs.pattern.adapter.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstriert die Verwendung des API-Gateway-Adapters.
 */
public class ApiGatewayDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGatewayDemo.class);
    
    /**
     * Führt die API-Gateway-Adapter-Demonstration durch.
     */
    public void runDemo() {
        LOGGER.info("Starte API-Gateway-Adapter-Demonstration");
        
        // Erstelle die benötigten Services
        UserService userService = new UserService();
        ProductService productService = new ProductService();
        
        // Erstelle den API-Gateway-Adapter
        ApiGateway apiGateway = new ApiGateway(userService, productService);
        
        // Simuliere Client-Anfragen
        simulateClientRequests(apiGateway);
        
        LOGGER.info("API-Gateway-Adapter-Demonstration abgeschlossen");
    }
    
    /**
     * Simuliert Client-Anfragen an das API-Gateway.
     * 
     * @param apiGateway Das API-Gateway
     */
    private void simulateClientRequests(ApiGateway apiGateway) {
        LOGGER.info("\n1. Benutzeranfragen:");
        
        // GET-Anfrage für einen Benutzer
        ApiResponse getUserResponse = apiGateway.processRequest("/users/123", "GET", null);
        LOGGER.info("GET /users/123 Antwort (Status {}): {}", 
                getUserResponse.getStatusCode(), getUserResponse.getBody());
        
        // POST-Anfrage zum Erstellen eines Benutzers
        String createUserBody = "{ \"username\": \"newuser\", \"email\": \"new@example.com\", \"role\": \"user\" }";
        ApiResponse createUserResponse = apiGateway.processRequest("/users", "POST", createUserBody);
        LOGGER.info("POST /users Antwort (Status {}): {}", 
                createUserResponse.getStatusCode(), createUserResponse.getBody());
        
        // PUT-Anfrage zum Aktualisieren eines Benutzers
        String updateUserBody = "{ \"email\": \"updated@example.com\" }";
        ApiResponse updateUserResponse = apiGateway.processRequest("/users/123", "PUT", updateUserBody);
        LOGGER.info("PUT /users/123 Antwort (Status {}): {}", 
                updateUserResponse.getStatusCode(), updateUserResponse.getBody());
        
        // DELETE-Anfrage zum Löschen eines Benutzers
        ApiResponse deleteUserResponse = apiGateway.processRequest("/users/123", "DELETE", null);
        LOGGER.info("DELETE /users/123 Antwort (Status {}): {}", 
                deleteUserResponse.getStatusCode(), deleteUserResponse.getBody());
        
        LOGGER.info("\n2. Produktanfragen:");
        
        // GET-Anfrage für ein Produkt
        ApiResponse getProductResponse = apiGateway.processRequest("/products/456", "GET", null);
        LOGGER.info("GET /products/456 Antwort (Status {}): {}", 
                getProductResponse.getStatusCode(), getProductResponse.getBody());
        
        // GET-Anfrage für alle Produkte
        ApiResponse getAllProductsResponse = apiGateway.processRequest("/products", "GET", null);
        LOGGER.info("GET /products Antwort (Status {}): {}", 
                getAllProductsResponse.getStatusCode(), getAllProductsResponse.getBody());
        
        // POST-Anfrage zum Erstellen eines Produkts
        String createProductBody = "<product><name>Neues Produkt</name><price>149.99</price><stock>10</stock></product>";
        ApiResponse createProductResponse = apiGateway.processRequest("/products", "POST", createProductBody);
        LOGGER.info("POST /products Antwort (Status {}): {}", 
                createProductResponse.getStatusCode(), createProductResponse.getBody());
        
        // PUT-Anfrage zum Aktualisieren eines Produkts
        String updateProductBody = "<product><price>159.99</price><stock>15</stock></product>";
        ApiResponse updateProductResponse = apiGateway.processRequest("/products/456", "PUT", updateProductBody);
        LOGGER.info("PUT /products/456 Antwort (Status {}): {}", 
                updateProductResponse.getStatusCode(), updateProductResponse.getBody());
        
        // DELETE-Anfrage zum Löschen eines Produkts
        ApiResponse deleteProductResponse = apiGateway.processRequest("/products/456", "DELETE", null);
        LOGGER.info("DELETE /products/456 Antwort (Status {}): {}", 
                deleteProductResponse.getStatusCode(), deleteProductResponse.getBody());
        
        LOGGER.info("\n3. Fehleranfragen:");
        
        // Ungültiger Pfad
        ApiResponse invalidPathResponse = apiGateway.processRequest("", "GET", null);
        LOGGER.info("Ungültiger Pfad Antwort (Status {}): {}", 
                invalidPathResponse.getStatusCode(), invalidPathResponse.getBody());
        
        // Unbekannte Ressource
        ApiResponse unknownResourceResponse = apiGateway.processRequest("/unknown/123", "GET", null);
        LOGGER.info("Unbekannte Ressource Antwort (Status {}): {}", 
                unknownResourceResponse.getStatusCode(), unknownResourceResponse.getBody());
        
        // Ungültige Methode
        ApiResponse invalidMethodResponse = apiGateway.processRequest("/users/123", "PATCH", null);
        LOGGER.info("Ungültige Methode Antwort (Status {}): {}", 
                invalidMethodResponse.getStatusCode(), invalidMethodResponse.getBody());
    }
}