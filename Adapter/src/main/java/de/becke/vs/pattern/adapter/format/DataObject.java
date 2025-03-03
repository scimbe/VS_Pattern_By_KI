package de.becke.vs.pattern.adapter.format;

/**
 * Ein Datenobjekt, das in verschiedenen Formaten dargestellt werden kann.
 * Diese Klasse repräsentiert eine typische Datenklasse in einer Anwendung.
 */
public class DataObject {
    
    private String id;
    private String name;
    private String description;
    private int value;
    
    /**
     * Erstellt ein neues DataObject.
     */
    public DataObject() {
    }
    
    /**
     * Erstellt ein neues DataObject mit den angegebenen Werten.
     * 
     * @param id Die ID des Objekts
     * @param name Der Name des Objekts
     * @param description Die Beschreibung des Objekts
     * @param value Der Wert des Objekts
     */
    public DataObject(String id, String name, String description, int value) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = value;
    }
    
    /**
     * Gibt die ID des Objekts zurück.
     * 
     * @return Die ID
     */
    public String getId() {
        return id;
    }
    
    /**
     * Setzt die ID des Objekts.
     * 
     * @param id Die ID
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Gibt den Namen des Objekts zurück.
     * 
     * @return Der Name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Setzt den Namen des Objekts.
     * 
     * @param name Der Name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gibt die Beschreibung des Objekts zurück.
     * 
     * @return Die Beschreibung
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Setzt die Beschreibung des Objekts.
     * 
     * @param description Die Beschreibung
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gibt den Wert des Objekts zurück.
     * 
     * @return Der Wert
     */
    public int getValue() {
        return value;
    }
    
    /**
     * Setzt den Wert des Objekts.
     * 
     * @param value Der Wert
     */
    public void setValue(int value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return String.format("DataObject [id=%s, name=%s, description=%s, value=%d]", 
                id, name, description, value);
    }
}