package de.becke.vs.pattern.adapter.api;

/**
 * Repräsentiert eine API-Antwort.
 */
public class ApiResponse {
    
    private final int statusCode;
    private final String body;
    private final String contentType;
    
    /**
     * Erstellt eine neue API-Antwort.
     * 
     * @param statusCode Der HTTP-Statuscode
     * @param body Der Antworttext
     * @param contentType Der Content-Type
     */
    public ApiResponse(int statusCode, String body, String contentType) {
        this.statusCode = statusCode;
        this.body = body;
        this.contentType = contentType;
    }
    
    /**
     * Gibt den HTTP-Statuscode zurück.
     * 
     * @return Der Statuscode
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
     * Gibt den Content-Type zurück.
     * 
     * @return Der Content-Type
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * Überprüft, ob die Antwort erfolgreich war (2xx).
     * 
     * @return true, wenn die Antwort erfolgreich war, sonst false
     */
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }
    
    @Override
    public String toString() {
        return String.format("ApiResponse [status=%d, contentType=%s, body=%s]", 
                statusCode, contentType, body);
    }
}