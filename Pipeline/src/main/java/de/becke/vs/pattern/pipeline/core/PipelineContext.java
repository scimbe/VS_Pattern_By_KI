package de.becke.vs.pattern.pipeline.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Kontext für die Ausführung einer Pipeline.
 * 
 * Der Kontext enthält Metadaten und Zustandsinformationen, die von verschiedenen
 * Pipeline-Phasen gemeinsam genutzt werden können.
 */
public class PipelineContext {
    
    // ID des Pipeline-Durchlaufs
    private final String executionId;
    
    // Startzeit der Ausführung
    private final long startTime;
    
    // Metadaten und andere Attribute
    private final Map<String, Object> attributes = new HashMap<>();
    
    // Fehlerkontext
    private Throwable error;
    
    // Versuch der Ausführung
    private int attemptCount = 1;
    
    /**
     * Erstellt einen neuen PipelineContext mit einer zufälligen ID.
     */
    public PipelineContext() {
        this.executionId = UUID.randomUUID().toString();
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * Erstellt einen neuen PipelineContext mit der angegebenen ID.
     * 
     * @param executionId Die ID des Pipeline-Durchlaufs
     */
    public PipelineContext(String executionId) {
        this.executionId = executionId;
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * Gibt die ID des Pipeline-Durchlaufs zurück.
     * 
     * @return Die ID des Pipeline-Durchlaufs
     */
    public String getExecutionId() {
        return executionId;
    }
    
    /**
     * Gibt die Startzeit des Pipeline-Durchlaufs zurück.
     * 
     * @return Die Startzeit in Millisekunden
     */
    public long getStartTime() {
        return startTime;
    }
    
    /**
     * Gibt die bisherige Dauer des Pipeline-Durchlaufs zurück.
     * 
     * @return Die Dauer in Millisekunden
     */
    public long getDuration() {
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * Setzt ein Attribut im Kontext.
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
     * Gibt ein Attribut aus dem Kontext mit einem bestimmten Typ zurück.
     * 
     * @param key Der Schlüssel des Attributs
     * @param clazz Die Klasse des erwarteten Typs
     * @param <T> Der generische Typ
     * @return Der Wert des Attributs oder null, wenn nicht vorhanden oder vom falschen Typ
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, Class<T> clazz) {
        Object value = attributes.get(key);
        if (value != null && clazz.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }
    
    /**
     * Entfernt ein Attribut aus dem Kontext.
     * 
     * @param key Der Schlüssel des zu entfernenden Attributs
     * @return Der frühere Wert des Attributs oder null, wenn nicht vorhanden
     */
    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }
    
    /**
     * Gibt alle Attribute im Kontext zurück.
     * 
     * @return Eine Map mit allen Attributen
     */
    public Map<String, Object> getAttributes() {
        return new HashMap<>(attributes);
    }
    
    /**
     * Setzt einen Fehler im Kontext.
     * 
     * @param error Der aufgetretene Fehler
     */
    public void setError(Throwable error) {
        this.error = error;
    }
    
    /**
     * Gibt den Fehler aus dem Kontext zurück.
     * 
     * @return Der Fehler oder null, wenn kein Fehler aufgetreten ist
     */
    public Throwable getError() {
        return error;
    }
    
    /**
     * Prüft, ob ein Fehler im Kontext vorhanden ist.
     * 
     * @return true, wenn ein Fehler vorhanden ist, sonst false
     */
    public boolean hasError() {
        return error != null;
    }
    
    /**
     * Erhöht den Versuchszähler.
     */
    public void incrementAttemptCount() {
        attemptCount++;
    }
    
    /**
     * Gibt die Anzahl der Versuche zurück.
     * 
     * @return Die Anzahl der Versuche
     */
    public int getAttemptCount() {
        return attemptCount;
    }
}