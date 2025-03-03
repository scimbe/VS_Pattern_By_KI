package de.becke.vs.pattern.adapter.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstriert die Verwendung des Protokolladapters zwischen SOAP und REST.
 */
public class ProtocolAdapterDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtocolAdapterDemo.class);
    
    /**
     * Führt die Protokolladapter-Demonstration durch.
     */
    public void runDemo() {
        LOGGER.info("Starte Protokolladapter-Demonstration");
        
        // Erzeuge den SOAP-Service (Adaptee)
        SoapService soapService = new SoapService();
        
        // Erzeuge den Adapter für den SOAP-Service
        RestService adapter = new SoapToRestAdapter(soapService);
        
        // Demonstriere die verschiedenen REST-Operationen über den Adapter
        
        // 1. GET-Anfrage
        LOGGER.info("1. Führe REST GET-Anfrage aus...");
        RestResponse getResponse = adapter.get("123");
        LOGGER.info("GET-Antwort: {}", getResponse);
        
        // 2. POST-Anfrage
        LOGGER.info("\n2. Führe REST POST-Anfrage aus...");
        RestResponse postResponse = adapter.post("{\"name\": \"Neue Ressource\", \"value\": 42}");
        LOGGER.info("POST-Antwort: {}", postResponse);
        
        // 3. PUT-Anfrage
        LOGGER.info("\n3. Führe REST PUT-Anfrage aus...");
        RestResponse putResponse = adapter.put("123", "{\"name\": \"Aktualisierte Ressource\", \"value\": 99}");
        LOGGER.info("PUT-Antwort: {}", putResponse);
        
        // 4. DELETE-Anfrage
        LOGGER.info("\n4. Führe REST DELETE-Anfrage aus...");
        RestResponse deleteResponse = adapter.delete("123");
        LOGGER.info("DELETE-Antwort: {}", deleteResponse);
        
        // Direkter Zugriff auf den SOAP-Service zum Vergleich
        LOGGER.info("\n5. Direkter Zugriff auf SOAP-Service zum Vergleich...");
        SoapRequest directRequest = new SoapRequest("GetResource", "123", null);
        SoapResponse directResponse = soapService.executeRequest(directRequest);
        LOGGER.info("Direkte SOAP-Antwort: {}", directResponse);
        
        LOGGER.info("Protokolladapter-Demonstration abgeschlossen");
    }
}