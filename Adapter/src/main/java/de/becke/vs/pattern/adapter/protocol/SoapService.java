package de.becke.vs.pattern.adapter.protocol;

/**
 * Repräsentiert einen SOAP-Webservice.
 * Dies ist die zu adaptierende Klasse (Adaptee).
 */
public class SoapService {
    
    /**
     * Führt eine SOAP-Anfrage zum Abrufen einer Ressource aus.
     * 
     * @param request Die SOAP-Anfrage
     * @return Die SOAP-Antwort
     */
    public SoapResponse executeRequest(SoapRequest request) {
        // In einer realen Anwendung würde hier die Kommunikation mit einem SOAP-Service erfolgen
        
        // Simuliere Verarbeitung der SOAP-Anfrage
        String operation = request.getOperation();
        String resourceId = request.getResourceId();
        String data = request.getData();
        
        // Erstelle eine entsprechende SOAP-Antwort basierend auf der Operation
        SoapResponse response = new SoapResponse();
        
        switch (operation) {
            case "GetResource":
                response.setResponseCode(200);
                response.setResponseData("<resource id=\"" + resourceId + "\"><name>Ressource " + resourceId + "</name></resource>");
                break;
            case "CreateResource":
                response.setResponseCode(201);
                response.setResponseData("<result><status>Created</status><data>" + data + "</data></result>");
                break;
            case "UpdateResource":
                response.setResponseCode(200);
                response.setResponseData("<result><status>Updated</status><resourceId>" + resourceId + "</resourceId></result>");
                break;
            case "DeleteResource":
                response.setResponseCode(204);
                response.setResponseData("<result><status>Deleted</status></result>");
                break;
            default:
                response.setResponseCode(400);
                response.setResponseData("<error>Unbekannte Operation: " + operation + "</error>");
                break;
        }
        
        return response;
    }
}