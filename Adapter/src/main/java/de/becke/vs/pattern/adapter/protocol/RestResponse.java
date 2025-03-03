package de.becke.vs.pattern.adapter.protocol;

/**
 * Stellt eine standardisierte REST-Antwort dar.
 */
public class RestResponse {
    
    private final int statusCode;
    private final String body;
    private final String contentType;
    
    /**
     * Erstellt eine neue REST-Antwort.
     * 
     * @param statusCode Der HTTP-Statuscode
     * @param body Der Antworttext
     * @param contentType Der Content-Type der Antwort
     */
    public RestResponse(int statusCode, String body, String contentType) {
        this.statusCode = statusCode;
        this.body = body;
        this.contentType = contentType;
    }
    
    /**
     * Gibt den HTTP-Statuscode der Antwort zurück.
     * 
     * @return Der HTTP-Statuscode
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     * Gibt den Antworttext zurück.
     * 
     * @return Der Antworttext
     */
    public String getBody() {
        return body;
    }
    
    /**
     * Gibt den Content-Type der Antwort zurück.
     * 
     * @return Der Content-Type
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Überprüft, ob die Antwort erfolgreich war (Status 2xx).
     * 
     * @return true, wenn die Antwort erfolgreich war, sonst false
     */
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }
    
    @Override
    public String toString() {
        return String.format("RestResponse [status=%d, contentType=%s, body=%s]", 
                statusCode, contentType, body);
    }
}