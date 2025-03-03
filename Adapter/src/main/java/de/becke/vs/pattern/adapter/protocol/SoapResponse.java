package de.becke.vs.pattern.adapter.protocol;

/**
 * Stellt eine SOAP-Antwort dar.
 */
public class SoapResponse {
    
    private int responseCode;
    private String responseData;
    
    /**
     * Erstellt eine neue SOAP-Antwort.
     */
    public SoapResponse() {
    }
    
    /**
     * Erstellt eine neue SOAP-Antwort mit den angegebenen Parametern.
     * 
     * @param responseCode Der Antwortcode
     * @param responseData Die Antwortdaten
     */
    public SoapResponse(int responseCode, String responseData) {
        this.responseCode = responseCode;
        this.responseData = responseData;
    }
    
    /**
     * Gibt den Antwortcode zurück.
     * 
     * @return Der Antwortcode
     */
    public int getResponseCode() {
        return responseCode;
    }
    
    /**
     * Setzt den Antwortcode.
     * 
     * @param responseCode Der Antwortcode
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    
    /**
     * Gibt die Antwortdaten zurück.
     * 
     * @return Die Antwortdaten
     */
    public String getResponseData() {
        return responseData;
    }
    
    /**
     * Setzt die Antwortdaten.
     * 
     * @param responseData Die Antwortdaten
     */
    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
    
    /**
     * Überprüft, ob die Antwort erfolgreich war.
     * 
     * @return true, wenn die Antwort erfolgreich war, sonst false
     */
    public boolean isSuccess() {
        return responseCode >= 200 && responseCode < 300;
    }
    
    @Override
    public String toString() {
        return String.format("SoapResponse [responseCode=%d, responseData=%s]", 
                responseCode, responseData);
    }
}