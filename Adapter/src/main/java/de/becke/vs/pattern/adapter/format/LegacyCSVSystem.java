package de.becke.vs.pattern.adapter.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repräsentiert ein Legacy-CSV-System, das ein älteres Format für die Datenhaltung verwendet.
 * Diese Klasse simuliert ein vorhandenes System, das mit einem Adapter in moderne Anwendungen integriert werden soll.
 */
public class LegacyCSVSystem {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyCSVSystem.class);
    private static final String DELIMITER = ";";
    
    /**
     * Erstellt ein neues Legacy-CSV-System.
     */
    public LegacyCSVSystem() {
        LOGGER.info("Legacy CSV System initialisiert");
    }
    
    /**
     * Konvertiert die übergebenen Daten in ein CSV-Format.
     * 
     * @param id Die ID des Datensatzes
     * @param name Der Name des Datensatzes
     * @param description Die Beschreibung des Datensatzes
     * @param value Der Wert des Datensatzes
     * @return Die CSV-Repräsentation der Daten
     */
    public String convertToCSV(String id, String name, String description, int value) {
        LOGGER.info("Konvertiere Daten zu CSV-Format: id={}, name={}", id, name);
        
        // Einfache CSV-Formatierung: id;name;description;value
        return id + DELIMITER + 
               name + DELIMITER + 
               description + DELIMITER + 
               value;
    }
    
    /**
     * Verarbeitet die übergebenen CSV-Daten.
     * 
     * @param csvData Die zu verarbeitenden CSV-Daten
     * @return Das Ergebnis der Verarbeitung
     */
    public String processCSVData(String csvData) {
        LOGGER.info("Verarbeite CSV-Daten: {}", csvData);
        
        // Parsen der CSV-Daten
        Object[] parts = parseFromCSV(csvData);
        
        // Simulierte Verarbeitung
        return "Verarbeitet: ID=" + parts[0] + 
               ", Name=" + parts[1] + 
               ", Beschreibung=" + parts[2] + 
               ", Wert=" + parts[3];
    }
    
    /**
     * Parst CSV-Daten und extrahiert die einzelnen Felder.
     * 
     * @param csvData Die zu parsenden CSV-Daten
     * @return Ein Array mit den extrahierten Werten
     */
    public Object[] parseFromCSV(String csvData) {
        LOGGER.info("Parse CSV-Daten: {}", csvData);
        
        // Aufteilen der CSV-Zeichenkette
        String[] parts = csvData.split(DELIMITER);
        
        // Konvertieren der Werte in die entsprechenden Typen
        Object[] result = new Object[4];
        
        // Sicherstellen, dass genügend Teile vorhanden sind
        if (parts.length >= 4) {
            result[0] = parts[0]; // ID (String)
            result[1] = parts[1]; // Name (String)
            result[2] = parts[2]; // Beschreibung (String)
            
            try {
                result[3] = Integer.parseInt(parts[3]); // Wert (int)
            } catch (NumberFormatException e) {
                LOGGER.warn("Konnte Wert nicht parsen: {}", parts[3]);
                result[3] = 0; // Standardwert bei Fehler
            }
        } else {
            LOGGER.error("Ungültiges CSV-Format: {}", csvData);
            // Fülle fehlende Werte mit Standardwerten
            for (int i = 0; i < 3; i++) {
                if (i < parts.length) {
                    result[i] = parts[i];
                } else {
                    result[i] = "";
                }
            }
            result[3] = 0;
        }
        
        return result;
    }
    
    /**
     * Exportiert Daten in das Legacy-CSV-Format.
     * 
     * @param data Die zu exportierenden Daten
     * @return Das exportierte CSV-Format
     */
    public String exportToLegacyFormat(DataObject data) {
        LOGGER.info("Exportiere DataObject zu Legacy-Format: {}", data);
        
        return convertToCSV(
                data.getId(),
                data.getName(),
                data.getDescription(),
                data.getValue());
    }
    
    /**
     * Importiert Daten aus dem Legacy-CSV-Format.
     * 
     * @param csvData Die zu importierenden CSV-Daten
     * @return Das importierte DataObject
     */
    public DataObject importFromLegacyFormat(String csvData) {
        LOGGER.info("Importiere aus Legacy-Format: {}", csvData);
        
        Object[] parts = parseFromCSV(csvData);
        
        DataObject data = new DataObject();
        data.setId((String) parts[0]);
        data.setName((String) parts[1]);
        data.setDescription((String) parts[2]);
        data.setValue((Integer) parts[3]);
        
        return data;
    }
}