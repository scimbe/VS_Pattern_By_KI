package de.becke.vs.pattern.interceptor.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Repräsentiert eine Antwort in einem verteilten System.
 */
public class Response {
    
    private final int statusCode;
    private final Map<String, String> headers;
    private final String body;
    
    /**
     * Erstellt eine neue Antwort mit den angegebenen Parametern.
     * 
     * @param statusCode Der HTTP-Statuscode oder Operationsstatus
     * @param headers Die HTTP-Header oder Metadaten
     * @param body Der Antwortkörper
     */
    public Response(int statusCode, Map<String, String> headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers != null ? new HashMap<>(headers) : new HashMap<>();
        this.body = body;
    }
    
    /**
     * Gibt den HTTP-Statuscode oder Operationsstatus zurück.
     * 
     * @return Der Statuscode
     */
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     * Gibt die HTTP-Header oder Metadaten zurück.
     * 
     * @return Die Header
     */
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }
    
    /**
     * Gibt einen bestimmten HTTP-Header zurück.
     * 
     * @param name Der Name des Headers
     * @return Der Wert des Headers oder null, wenn der Header nicht existiert
     */
    public String getHeader(String name) {
        return headers.get(name);
    }
    
    /**
     * Gibt den Antwortkörper zurück.
     * 
     * @return Der Antwortkörper
     */
    public String getBody() {
        return body;
    }
    
    /**
     * Überprüft, ob die Antwort erfolgreich war (Statuscode im Bereich 200-299).
     * 
     * @return true, wenn die Antwort erfolgreich war, sonst false
     */
    public boolean isSuccess() {
        return statusCode >= 200 && statusCode < 300;
    }
    
    /**
     * Builder für die Response-Klasse.
     */
    public static class Builder {
        private int statusCode;
        private Map<String, String> headers = new HashMap<>();
        private String body;
        
        /**
         * Setzt den HTTP-Statuscode oder Operationsstatus.
         * 
         * @param statusCode Der Statuscode
         * @return Dieser Builder für Method Chaining
         */
        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }
        
        /**
         * Fügt einen HTTP-Header hinzu.
         * 
         * @param name Der Name des Headers
         * @param value Der Wert des Headers
         * @return Dieser Builder für Method Chaining
         */
        public Builder header(String name, String value) {
            this.headers.put(name, value);
            return this;
        }
        
        /**
         * Setzt alle HTTP-Header.
         * 
         * @param headers Die HTTP-Header
         * @return Dieser Builder für Method Chaining
         */
        public Builder headers(Map<String, String> headers) {
            this.headers = new HashMap<>(headers);
            return this;
        }
        
        /**
         * Setzt den Antwortkörper.
         * 
         * @param body Der Antwortkörper
         * @return Dieser Builder für Method Chaining
         */
        public Builder body(String body) {
            this.body = body;
            return this;
        }
        
        /**
         * Erstellt eine neue Response mit den gesetzten Parametern.
         * 
         * @return Die erstellte Response
         */
        public Response build() {
            return new Response(statusCode, headers, body);
        }
    }
}