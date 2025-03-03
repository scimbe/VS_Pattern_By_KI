package de.becke.vs.pattern.adapter.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein Objektadapter, der eine SOAP-Serviceschnittstelle in eine REST-Serviceschnittstelle umwandelt.
 * Dieser Adapter ermöglicht es REST-Clients, mit einem SOAP-Service zu kommunizieren.
 */
public class SoapToRestAdapter implements RestService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SoapToRestAdapter.class);
    
    private final SoapService soapService;
    
    /**
     * Erstellt einen neuen SOAP-zu-REST-Adapter.
     * 
     * @param soapService Der zu adaptierende SOAP-Service
     */
    public SoapToRestAdapter(SoapService soapService) {
        this.soapService = soapService;
        LOGGER.info("SoapToRestAdapter initialisiert");
    }
    
    @Override
    public RestResponse get(String resourceId) {
        LOGGER.info("Adaptiere REST GET-Anfrage zu SOAP für Ressource: {}", resourceId);
        
        // Erstelle eine SOAP-Anfrage aus den REST-Parametern
        SoapRequest soapRequest = new SoapRequest("GetResource", resourceId, null);
        
        // Führe die SOAP-Anfrage aus
        SoapResponse soapResponse = soapService.executeRequest(soapRequest);
        
        // Konvertiere die SOAP-Antwort in eine REST-Antwort
        return convertToRestResponse(soapResponse);
    }
    
    @Override
    public RestResponse post(String data) {
        LOGGER.info("Adaptiere REST POST-Anfrage zu SOAP mit Daten: {}", data);
        
        // Erstelle eine SOAP-Anfrage aus den REST-Parametern
        SoapRequest soapRequest = new SoapRequest("CreateResource", null, data);
        
        // Führe die SOAP-Anfrage aus
        SoapResponse soapResponse = soapService.executeRequest(soapRequest);
        
        // Konvertiere die SOAP-Antwort in eine REST-Antwort
        return convertToRestResponse(soapResponse);
    }
    
    @Override
    public RestResponse put(String resourceId, String data) {
        LOGGER.info("Adaptiere REST PUT-Anfrage zu SOAP für Ressource: {} mit Daten: {}", resourceId, data);
        
        // Erstelle eine SOAP-Anfrage aus den REST-Parametern
        SoapRequest soapRequest = new SoapRequest("UpdateResource", resourceId, data);
        
        // Führe die SOAP-Anfrage aus
        SoapResponse soapResponse = soapService.executeRequest(soapRequest);
        
        // Konvertiere die SOAP-Antwort in eine REST-Antwort
        return convertToRestResponse(soapResponse);
    }
    
    @Override
    public RestResponse delete(String resourceId) {
        LOGGER.info("Adaptiere REST DELETE-Anfrage zu SOAP für Ressource: {}", resourceId);
        
        // Erstelle eine SOAP-Anfrage aus den REST-Parametern
        SoapRequest soapRequest = new SoapRequest("DeleteResource", resourceId, null);
        
        // Führe die SOAP-Anfrage aus
        SoapResponse soapResponse = soapService.executeRequest(soapRequest);
        
        // Konvertiere die SOAP-Antwort in eine REST-Antwort
        return convertToRestResponse(soapResponse);
    }
    
    /**
     * Konvertiert eine SOAP-Antwort in eine REST-Antwort.
     * 
     * @param soapResponse Die zu konvertierende SOAP-Antwort
     * @return Die konvertierte REST-Antwort
     */
    private RestResponse convertToRestResponse(SoapResponse soapResponse) {
        // In einer realen Implementierung würde hier eine komplexe Konvertierung stattfinden
        // Für dieses Beispiel führen wir eine einfache Konvertierung durch
        
        int statusCode = soapResponse.getResponseCode();
        String responseData = soapResponse.getResponseData();
        
        // Setze den entsprechenden Content-Type basierend auf der Antwort
        String contentType = "application/xml";
        
        LOGGER.info("SOAP-Antwort in REST-Antwort konvertiert: Status {}", statusCode);
        
        return new RestResponse(statusCode, responseData, contentType);
    }
}