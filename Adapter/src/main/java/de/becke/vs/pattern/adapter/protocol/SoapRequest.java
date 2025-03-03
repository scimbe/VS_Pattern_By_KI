package de.becke.vs.pattern.adapter.protocol;

/**
 * Stellt eine SOAP-Anfrage dar.
 */
public class SoapRequest {
    
    private String operation;
    private String resourceId;
    private String data;
    
    /**
     * Erstellt eine neue SOAP-Anfrage.
     */
    public SoapRequest() {
    }
    
    /**
     * Erstellt eine neue SOAP-Anfrage mit den angegebenen Parametern.
     * 
     * @param operation Die auszuführende Operation
     * @param resourceId Die ID der Ressource (optional)
     * @param data Die Anfragedaten (optional)
     */
    public SoapRequest(String operation, String resourceId, String data) {
        this.operation = operation;
        this.resourceId = resourceId;
        this.data = data;
    }
    
    /**
     * Gibt die Operation der Anfrage zurück.
     * 
     * @return Die Operation
     */
    public String getOperation() {
        return operation;
    }
    
    /**
     * Setzt die Operation der Anfrage.
     * 
     * @param operation Die Operation
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    /**
     * Gibt die Ressourcen-ID der Anfrage zurück.
     * 
     * @return Die Ressourcen-ID
     */
    public String getResourceId() {
        return resourceId;
    }
    
    /**
     * Setzt die Ressourcen-ID der Anfrage.
     * 
     * @param resourceId Die Ressourcen-ID
     */
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
    
    /**
     * Gibt die Daten der Anfrage zurück.
     * 
     * @return Die Anfragedaten
     */
    public String getData() {
        return data;
    }
    
    /**
     * Setzt die Daten der Anfrage.
     * 
     * @param data Die Anfragedaten
     */
    public void setData(String data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return String.format("SoapRequest [operation=%s, resourceId=%s, data=%s]", 
                operation, resourceId, data);
    }
}