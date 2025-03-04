package de.becke.vs.pattern.interceptor.pipeline;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Repräsentiert eine Nachricht in einem verteilten System.
 * 
 * Diese Klasse enthält die Daten und Metadaten einer Nachricht, die zwischen
 * Diensten in einem verteilten System ausgetauscht wird.
 */
public class Message {
    
    private final String id;
    private final String type;
    private final Object payload;
    private final Map<String, String> headers;
    private final long timestamp;
    
    /**
     * Erstellt eine neue Nachricht mit automatisch generierter ID.
     * 
     * @param type Der Typ der Nachricht
     * @param payload Der Inhalt der Nachricht
     */
    public Message(String type, Object payload) {
        this(UUID.randomUUID().toString(), type, payload, new HashMap<>(), System.currentTimeMillis());
    }
    
    /**
     * Erstellt eine neue Nachricht mit vollständigen Details.
     * 
     * @param id Die ID der Nachricht
     * @param type Der Typ der Nachricht
     * @param payload Der Inhalt der Nachricht
     * @param headers Die Headers der Nachricht
     * @param timestamp Der Zeitstempel der Nachricht
     */
    public Message(String id, String type, Object payload, Map<String, String> headers, long timestamp) {
        this.id = id;
        this.type = type;
        this.payload = payload;
        this.headers = new HashMap<>(headers);
        this.timestamp = timestamp;
    }
    
    /**
     * Gibt die ID der Nachricht zurück.
     * 
     * @return Die ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Gibt den Typ der Nachricht zurück.
     * 
     * @return Der Typ
     */
    public String getType() {
        return type;
    }
    
    /**
     * Gibt den Inhalt der Nachricht zurück.
     * 
     * @return Der Inhalt
     */
    public Object getPayload() {
        return payload;
    }
    
    /**
     * Gibt die Headers der Nachricht zurück.
     * 
     * @return Die Headers
     */
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }
    
    /**
     * Gibt den Wert eines bestimmten Headers zurück.
     * 
     * @param name Der Name des Headers
     * @return Der Wert des Headers oder null, wenn nicht vorhanden
     */
    public String getHeader(String name) {
        return headers.get(name);
    }
    
    /**
     * Fügt einen Header hinzu oder aktualisiert ihn.
     * 
     * @param name Der Name des Headers
     * @param value Der Wert des Headers
     * @return Diese Nachricht für Method Chaining
     */
    public Message addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }
    
    /**
     * Gibt den Zeitstempel der Nachricht zurück.
     * 
     * @return Der Zeitstempel
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Erstellt eine neue Nachricht basierend auf dieser Nachricht mit einem neuen Payload.
     * 
     * @param newPayload Der neue Inhalt
     * @return Die neue Nachricht
     */
    public Message withPayload(Object newPayload) {
        return new Message(id, type, newPayload, headers, timestamp);
    }
    
    /**
     * Erstellt eine neue Nachricht als Antwort auf diese Nachricht.
     * 
     * @param responseType Der Typ der Antwortnachricht
     * @param responsePayload Der Inhalt der Antwortnachricht
     * @return Die Antwortnachricht
     */
    public Message createResponse(String responseType, Object responsePayload) {
        Map<String, String> responseHeaders = new HashMap<>(headers);
        responseHeaders.put("correlationId", id);
        
        return new Message(
                UUID.randomUUID().toString(),
                responseType,
                responsePayload,
                responseHeaders,
                System.currentTimeMillis()
        );
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", payload=" + payload +
                ", headers=" + headers +
                ", timestamp=" + timestamp +
                '}';
    }
}