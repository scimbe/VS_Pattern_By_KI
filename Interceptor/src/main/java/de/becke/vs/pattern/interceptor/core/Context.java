package de.becke.vs.pattern.interceptor.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Der Kontext für die Ausführung einer Operation, der von Interceptoren verwendet wird.
 * 
 * Diese Klasse enthält alle relevanten Informationen für die Verarbeitung einer Anfrage 
 * oder Operation und speichert zusätzliche Daten, die von Interceptoren benötigt werden.
 */
public class Context {
    
    /**
     * Eindeutige ID für diese Ausführung
     */
    private final String executionId;
    
    /**
     * Startzeit der Ausführung
     */
    private final long startTime;
    
    /**
     * Endzeit der Ausführung
     */
    private long endTime;
    
    /**
     * Eingabeparameter für die Operation
     */
    private Object input;
    
    /**
     * Ergebnis der Operation
     */
    private Object result;
    
    /**
     * Zustandsinformationen
     */
    private boolean successful;
    
    /**
     * Zusätzliche Attribute, die von Interceptoren gesetzt werden können
     */
    private final Map<String, Object> attributes = new HashMap<>();
    
    /**
     * Erstellt einen neuen Kontext mit einer zufälligen ID.
     */
    public Context() {
        this.executionId = UUID.randomUUID().toString();
        this.startTime = System.currentTimeMillis();
        this.successful = true; // Optimistisch angenommen
    }
    
    /**
     * Erstellt einen neuen Kontext mit einer zufälligen ID und Eingabedaten.
     * 
     * @param input Die Eingabedaten für die Operation
     */
    public Context(Object input) {
        this();
        this.input = input;
    }
    
    /**
     * Gibt die eindeutige ID dieser Ausführung zurück.
     * 
     * @return Die Ausführungs-ID
     */
    public String getExecutionId() {
        return executionId;
    }
    
    /**
     * Gibt die Startzeit der Ausführung zurück.
     * 
     * @return Die Startzeit in Millisekunden
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * Gibt die Endzeit der Ausführung zurück.
     * 
     * @return Die Endzeit in Millisekunden oder 0, wenn noch nicht gesetzt
     */
    public long getEndTime() {
        return endTime;
    }
    
    /**
     * Setzt die Endzeit der Ausführung.
     * 
     * @param endTime Die Endzeit in Millisekunden
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    /**
     * Berechnet die Dauer der Ausführung.
     * 
     * @return Die Dauer in Millisekunden oder -1, wenn die Endzeit noch nicht gesetzt wurde
     */
    public long getDuration() {
        return endTime > 0 ? endTime - startTime : -1;
    }
    
    /**
     * Gibt die Eingabedaten für die Operation zurück.
     * 
     * @return Die Eingabedaten
     */
    public Object getInput() {
        return input;
    }
    
    /**
     * Setzt die Eingabedaten für die Operation.
     * 
     * @param input Die Eingabedaten
     */
    public void setInput(Object input) {
        this.input = input;
    }
    
    /**
     * Gibt das Ergebnis der Operation zurück.
     * 
     * @return Das Ergebnis
     */
    public Object getResult() {
        return result;
    }
    
    /**
     * Setzt das Ergebnis der Operation.
     * 
     * @param result Das Ergebnis
     */
    public void setResult(Object result) {
        this.result = result;
    }
    
    /**
     * Gibt an, ob die Operation erfolgreich war.
     * 
     * @return true, wenn die Operation erfolgreich war, sonst false
     */
    public boolean isSuccessful() {
        return successful;
    }
    
    /**
     * Setzt den Erfolgsstatus der Operation.
     * 
     * @param successful Der Erfolgsstatus
     */
    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }
    
    /**
     * Fügt ein Attribut zum Kontext hinzu.
     * 
     * @param key Der Schlüssel des Attributs
     * @param value Der Wert des Attributs
     */
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }
    
    /**
     * Gibt ein Attribut aus dem Kontext zurück.
     * 
     * @param key Der Schlüssel des Attributs
     * @return Der Wert des Attributs oder null, wenn nicht vorhanden
     */
    public Object getAttribute(String key) {
        return attributes.get(key);
    }
    
    /**
     * Gibt alle Attribute aus dem Kontext zurück.
     * 
     * @return Eine Map mit allen Attributen
     */
    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }
    
    /**
     * Entfernt ein Attribut aus dem Kontext.
     * 
     * @param key Der Schlüssel des zu entfernenden Attributs
     * @return Der Wert des entfernten Attributs oder null, wenn nicht vorhanden
     */
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }
    
    /**
     * Gibt eine typisierte Version eines Attributs zurück.
     * 
     * @param key Der Schlüssel des Attributs
     * @param clazz Die erwartete Klasse des Attributs
     * @param <T> Der Typ des Attributs
     * @return Das Attribut oder null, wenn nicht vorhanden oder vom falschen Typ
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttributeAs(String key, Class<T> clazz) {
        Object value = attributes.get(key);
        if (value != null && clazz.isInstance(value)) {
            return (T) value;
        }
        return null;
    }
}