package de.becke.vs.pattern.interceptor.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Repräsentiert den Kontext einer Operation, die von Interceptors abgefangen wird.
 * Der Kontext enthält die Anfrage, die Antwort und zusätzliche Attribute, die von Interceptors
 * verwendet werden können, um Informationen weiterzugeben.
 */
public class InterceptionContext {
    
    private final Request request;
    private Response response;
    private final Map<String, Object> attributes;
    private final long startTime;
    
    /**
     * Erstellt einen neuen Kontext für eine Operation.
     * 
     * @param request Die Anfrage für die Operation
     */
    public InterceptionContext(Request request) {
        this.request = request;
        this.attributes = new HashMap<>();
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * Gibt die Anfrage zurück.
     * 
     * @return Die Anfrage
     */
    public Request getRequest() {
        return request;
    }
    
    /**
     * Gibt die Antwort zurück.
     * 
     * @return Die Antwort oder null, wenn noch keine Antwort gesetzt wurde
     */
    public Response getResponse() {
        return response;
    }
    
    /**
     * Setzt die Antwort.
     * 
     * @param response Die Antwort
     */
    public void setResponse(Response response) {
        this.response = response;
    }
    
    /**
     * Fügt ein Attribut hinzu.
     * 
     * @param name Der Name des Attributs
     * @param value Der Wert des Attributs
     */
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
    
    /**
     * Gibt ein Attribut zurück.
     * 
     * @param name Der Name des Attributs
     * @return Der Wert des Attributs oder null, wenn das Attribut nicht existiert
     */
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
    
    /**
     * Gibt alle Attribute zurück.
     * 
     * @return Eine unveränderliche Map aller Attribute
     */
    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }
    
    /**
     * Gibt die Startzeit der Operation zurück.
     * 
     * @return Die Startzeit der Operation in Millisekunden
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * Berechnet die Dauer der Operation in Millisekunden.
     * 
     * @return Die Dauer der Operation in Millisekunden
     */
    public long getDuration() {
        return System.currentTimeMillis() - startTime;
    }
}