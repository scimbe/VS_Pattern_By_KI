package de.becke.vs.pattern.adapter.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein API-Gateway-Adapter, der verschiedene Microservices hinter einer einheitlichen REST-Schnittstelle verbirgt.
 * 
 * Dieses Gateway funktioniert als Adapter, der die unterschiedlichen Formate und Protokolle
 * der Microservices in ein einheitliches Format für Clients umwandelt.
 */
public class ApiGateway {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGateway.class);
    
    private final UserService userService;
    private final ProductService productService;
    
    /**
     * Erstellt ein neues API-Gateway.
     * 
     * @param userService Der Benutzer-Service
     * @param productService Der Produkt-Service
     */
    public ApiGateway(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
        LOGGER.info("API-Gateway initialisiert");
    }
    
    /**
     * Verarbeitet eine HTTP-Anfrage und leitet sie an den entsprechenden Microservice weiter.
     * 
     * @param path Der Anfragepfad
     * @param method Die HTTP-Methode (GET, POST, PUT, DELETE)
     * @param body Der Anfragekörper (optional)
     * @return Die API-Antwort im JSON-Format
     */
    public ApiResponse processRequest(String path, String method, String body) {
        LOGGER.info("API-Gateway: Verarbeite {} Anfrage an {}", method, path);
        
        // Pfad analysieren
        String[] pathParts = path.split("/");
        
        // Standardwerte für die Antwort
        int statusCode = 200;
        String responseBody = "";
        String contentType = "application/json";
        
        try {
            // Die ersten Pfadteile sollten das Ressourcentyp und optional die ID enthalten
            if (pathParts.length < 2) {
                return createErrorResponse(400, "Ungültiger Pfad");
            }
            
            // Überprüfe, ob die HTTP-Methode unterstützt wird
            if (!method.equals("GET") && !method.equals("POST") && 
                !method.equals("PUT") && !method.equals("DELETE")) {
                return createErrorResponse(405, "Methode nicht unterstützt: " + method);
            }
            
            String resourceType = pathParts[1];
            String resourceId = pathParts.length > 2 ? pathParts[2] : null;
            
            // Je nach Ressourcentyp an den entsprechenden Service weiterleiten
            switch (resourceType) {
                case "users":
                    responseBody = handleUserRequest(method, resourceId, body);
                    break;
                    
                case "products":
                    responseBody = handleProductRequest(method, resourceId, body);
                    // Produkt-Service gibt XML zurück, also konvertieren zu JSON
                    responseBody = convertXmlToJson(responseBody);
                    break;
                    
                default:
                    return createErrorResponse(404, "Ressource nicht gefunden: " + resourceType);
            }
            
            // Prüfe, ob die Antwort einen Fehler enthält
            if (responseBody.contains("\"error\"")) {
                // Extrahiere den Fehlertext für die Bestimmung des passenden Statuscodes
                String errorMessage = extractJsonValue(responseBody, "\"error\"\\s*:\\s*\"([^\"]+)\"");
                
                if (errorMessage.contains("Benutzer-ID erforderlich") || 
                    errorMessage.contains("Produkt-ID erforderlich")) {
                    return new ApiResponse(400, responseBody, contentType);
                }
            }
            
            return new ApiResponse(statusCode, responseBody, contentType);
            
        } catch (Exception e) {
            LOGGER.error("Fehler bei der Verarbeitung der Anfrage: {}", e.getMessage());
            return createErrorResponse(500, "Interner Serverfehler: " + e.getMessage());
        }
    }
    
    /**
     * Verarbeitet eine Anfrage an den Benutzer-Service.
     * 
     * @param method Die HTTP-Methode
     * @param userId Die Benutzer-ID (optional)
     * @param body Der Anfragekörper (optional)
     * @return Die Antwort des Benutzer-Service
     */
    private String handleUserRequest(String method, String userId, String body) {
        LOGGER.info("Verarbeite Benutzeranfrage: {} {}", method, userId != null ? userId : "");
        
        switch (method) {
            case "GET":
                if (userId != null) {
                    return userService.getUser(userId);
                } else {
                    return "{ \"error\": \"Benutzer-ID erforderlich\" }";
                }
                
            case "POST":
                return userService.createUser(body);
                
            case "PUT":
                if (userId != null) {
                    return userService.updateUser(userId, body);
                } else {
                    return "{ \"error\": \"Benutzer-ID erforderlich\" }";
                }
                
            case "DELETE":
                if (userId != null) {
                    return userService.deleteUser(userId);
                } else {
                    return "{ \"error\": \"Benutzer-ID erforderlich\" }";
                }
                
            default:
                // Diese Bedingung sollte nicht erreicht werden, da wir bereits im processRequest prüfen
                return "{ \"error\": \"Methode nicht unterstützt: " + method + "\" }";
        }
    }
    
    /**
     * Verarbeitet eine Anfrage an den Produkt-Service.
     * 
     * @param method Die HTTP-Methode
     * @param productId Die Produkt-ID (optional)
     * @param body Der Anfragekörper (optional)
     * @return Die Antwort des Produkt-Service (im XML-Format)
     */
    private String handleProductRequest(String method, String productId, String body) {
        LOGGER.info("Verarbeite Produktanfrage: {} {}", method, productId != null ? productId : "");
        
        switch (method) {
            case "GET":
                if (productId != null) {
                    return productService.getProduct(productId);
                } else {
                    return productService.getAllProducts();
                }
                
            case "POST":
                return productService.createProduct(body);
                
            case "PUT":
                if (productId != null) {
                    return productService.updateProduct(productId, body);
                } else {
                    return "<error>Produkt-ID erforderlich</error>";
                }
                
            case "DELETE":
                if (productId != null) {
                    return productService.deleteProduct(productId);
                } else {
                    return "<error>Produkt-ID erforderlich</error>";
                }
                
            default:
                // Diese Bedingung sollte nicht erreicht werden, da wir bereits im processRequest prüfen
                return "<error>Methode nicht unterstützt: " + method + "</error>";
        }
    }
    
    /**
     * Konvertiert XML in JSON (vereinfachte Implementierung).
     * 
     * @param xml Die XML-Zeichenkette
     * @return Die JSON-Zeichenkette
     */
    private String convertXmlToJson(String xml) {
        LOGGER.info("Konvertiere XML zu JSON: {}", xml);
        
        // In einer realen Implementierung würde hier eine richtige XML-zu-JSON-Konvertierung stattfinden
        // Für dieses Beispiel implementieren wir eine sehr einfache Konvertierung
        
        // Überprüfe auf Fehlermeldung
        if (xml.contains("<error>")) {
            String errorMsg = extractXmlContent(xml, "error");
            return "{ \"error\": \"" + errorMsg + "\" }";
        }
        
        // Überprüfe, ob es sich um eine einzelne Produkt-Antwort handelt
        if (xml.contains("<product>") && !xml.contains("<products>")) {
            String id = extractXmlContent(xml, "id");
            String name = extractXmlContent(xml, "name");
            String price = extractXmlContent(xml, "price");
            String stock = extractXmlContent(xml, "stock");
            
            return String.format(
                    "{ \"id\": \"%s\", \"name\": \"%s\", \"price\": %s, \"stock\": %s }",
                    id, name, price, stock);
        }
        
        // Überprüfe, ob es sich um eine Produktliste handelt
        if (xml.contains("<products>")) {
            // Vereinfachte Verarbeitung - in einer realen Implementierung würde ein richtiger XML-Parser verwendet werden
            String result = "{ \"products\": [";
            
            // Einfache Extraktion mehrerer Produkte (sehr vereinfacht)
            String productsContent = xml.substring(xml.indexOf("<products>") + 10, xml.indexOf("</products>"));
            String[] products = productsContent.split("</product>");
            
            for (int i = 0; i < products.length - 1; i++) {
                String product = products[i] + "</product>";
                
                String id = extractXmlContent(product, "id");
                String name = extractXmlContent(product, "name");
                String price = extractXmlContent(product, "price");
                String stock = extractXmlContent(product, "stock");
                
                result += String.format(
                        "{ \"id\": \"%s\", \"name\": \"%s\", \"price\": %s, \"stock\": %s }",
                        id, name, price, stock);
                
                if (i < products.length - 2) {
                    result += ", ";
                }
            }
            
            result += "] }";
            return result;
        }
        
        // Überprüfe auf Ergebnismeldung
        if (xml.contains("<result>")) {
            String status = extractXmlContent(xml, "status");
            String message = extractXmlContent(xml, "message");
            
            return String.format(
                    "{ \"status\": \"%s\", \"message\": \"%s\" }",
                    status, message);
        }
        
        // Fallback: Einfach "xml" in JSON-Attribut einpacken
        return "{ \"xml\": \"" + xml.replace("\"", "\\\"") + "\" }";
    }
    
    /**
     * Extrahiert den Inhalt eines XML-Tags.
     * 
     * @param xml Die XML-Zeichenkette
     * @param tagName Der Name des Tags
     * @return Der Inhalt des Tags
     */
    private String extractXmlContent(String xml, String tagName) {
        String startTag = "<" + tagName + ">";
        String endTag = "</" + tagName + ">";
        
        int startIndex = xml.indexOf(startTag) + startTag.length();
        int endIndex = xml.indexOf(endTag);
        
        if (startIndex >= 0 && endIndex >= 0) {
            return xml.substring(startIndex, endIndex);
        }
        
        return "";
    }
    
    /**
     * Extrahiert einen Wert aus einer JSON-Zeichenkette.
     * 
     * @param json Die JSON-Zeichenkette
     * @param pattern Das RegEx-Muster
     * @return Der extrahierte Wert
     */
    private String extractJsonValue(String json, String pattern) {
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(json);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return "";
    }
    
    /**
     * Erstellt eine Fehlerantwort.
     * 
     * @param statusCode Der HTTP-Statuscode
     * @param message Die Fehlermeldung
     * @return Die Fehlerantwort
     */
    private ApiResponse createErrorResponse(int statusCode, String message) {
        String responseBody = String.format("{ \"error\": \"%s\", \"status\": %d }", message, statusCode);
        return new ApiResponse(statusCode, responseBody, "application/json");
    }
}