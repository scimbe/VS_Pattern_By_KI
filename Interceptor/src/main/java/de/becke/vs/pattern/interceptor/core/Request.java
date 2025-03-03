package de.becke.vs.pattern.interceptor.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Repräsentiert eine Anfrage in einem verteilten System.
 */
public class Request {
    
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final String body;
    
    /**
     * Erstellt eine neue Anfrage mit den angegebenen Parametern.
     * 
     * @param method Die HTTP-Methode oder Operationsart
     * @param path Der Pfad oder Endpunkt
     * @param headers Die HTTP-Header oder Metadaten
     * @param parameters Die Anfrageparameter
     * @param body Der Anfragekörper
     */
    public Request(String method, String path, Map<String, String> headers, 
                  Map<String, String> parameters, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers != null ? new HashMap<>(headers) : new HashMap<>();
        this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
        this.body = body;
    }
    
    /**
     * Gibt die HTTP-Methode oder Operationsart zurück.
     * 
     * @return Die Methode
     */
    public String getMethod() {
        return method;
    }
    
    /**
     * Gibt den Pfad oder Endpunkt zurück.
     * 
     * @return Der Pfad
     */
    public String getPath() {
        return path;
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
     * Gibt die Anfrageparameter zurück.
     * 
     * @return Die Parameter
     */
    public Map<String, String> getParameters() {
        return new HashMap<>(parameters);
    }
    
    /**
     * Gibt einen bestimmten Anfrageparameter zurück.
     * 
     * @param name Der Name des Parameters
     * @return Der Wert des Parameters oder null, wenn der Parameter nicht existiert
     */
    public String getParameter(String name) {
        return parameters.get(name);
    }
    
    /**
     * Gibt den Anfragekörper zurück.
     * 
     * @return Der Anfragekörper
     */
    public String getBody() {
        return body;
    }
    
    /**
     * Builder für die Request-Klasse.
     */
    public static class Builder {
        private String method;
        private String path;
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> parameters = new HashMap<>();
        private String body;
        
        /**
         * Setzt die HTTP-Methode oder Operationsart.
         * 
         * @param method Die Methode
         * @return Dieser Builder für Method Chaining
         */
        public Builder method(String method) {
            this.method = method;
            return this;
        }
        
        /**
         * Setzt den Pfad oder Endpunkt.
         * 
         * @param path Der Pfad
         * @return Dieser Builder für Method Chaining
         */
        public Builder path(String path) {
            this.path = path;
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
         * Fügt einen Anfrageparameter hinzu.
         * 
         * @param name Der Name des Parameters
         * @param value Der Wert des Parameters
         * @return Dieser Builder für Method Chaining
         */
        public Builder parameter(String name, String value) {
            this.parameters.put(name, value);
            return this;
        }
        
        /**
         * Setzt alle Anfrageparameter.
         * 
         * @param parameters Die Anfrageparameter
         * @return Dieser Builder für Method Chaining
         */
        public Builder parameters(Map<String, String> parameters) {
            this.parameters = new HashMap<>(parameters);
            return this;
        }
        
        /**
         * Setzt den Anfragekörper.
         * 
         * @param body Der Anfragekörper
         * @return Dieser Builder für Method Chaining
         */
        public Builder body(String body) {
            this.body = body;
            return this;
        }
        
        /**
         * Erstellt eine neue Request mit den gesetzten Parametern.
         * 
         * @return Die erstellte Request
         */
        public Request build() {
            return new Request(method, path, headers, parameters, body);
        }
    }
}