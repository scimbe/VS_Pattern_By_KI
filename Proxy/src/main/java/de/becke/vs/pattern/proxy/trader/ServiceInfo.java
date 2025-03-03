package de.becke.vs.pattern.proxy.trader;

import de.becke.vs.pattern.proxy.common.RemoteService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Enthält Informationen über einen beim Trader registrierten Dienst.
 * 
 * Diese Klasse speichert den eigentlichen Dienst und seine Eigenschaften.
 */
public class ServiceInfo {
    
    private final RemoteService service;
    private final Map<String, Object> properties;
    
    /**
     * Erstellt eine neue ServiceInfo-Instanz.
     * 
     * @param service Der Remote-Dienst
     * @param properties Die Eigenschaften des Dienstes
     */
    public ServiceInfo(RemoteService service, Map<String, Object> properties) {
        this.service = service;
        this.properties = new HashMap<>(properties);
    }
    
    /**
     * Gibt den Remote-Dienst zurück.
     * 
     * @return Der Remote-Dienst
     */
    public RemoteService getService() {
        return service;
    }
    
    /**
     * Gibt die Eigenschaften des Dienstes zurück.
     * 
     * @return Die Diensteigenschaften (als unveränderbare Map)
     */
    public Map<String, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }
    
    /**
     * Gibt den Wert einer bestimmten Eigenschaft zurück.
     * 
     * @param key Der Schlüssel der Eigenschaft
     * @return Der Wert der Eigenschaft oder null, wenn nicht vorhanden
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    /**
     * Gibt den Wert einer bestimmten Eigenschaft als String zurück.
     * 
     * @param key Der Schlüssel der Eigenschaft
     * @param defaultValue Der Standardwert, wenn die Eigenschaft nicht vorhanden ist
     * @return Der Wert der Eigenschaft als String
     */
    public String getStringProperty(String key, String defaultValue) {
        Object value = properties.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    /**
     * Aktualisiert eine Eigenschaft des Dienstes.
     * 
     * @param key Der Schlüssel der Eigenschaft
     * @param value Der neue Wert der Eigenschaft
     */
    public void updateProperty(String key, Object value) {
        properties.put(key, value);
    }
}