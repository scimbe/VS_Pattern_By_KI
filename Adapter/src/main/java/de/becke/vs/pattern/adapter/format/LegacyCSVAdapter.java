package de.becke.vs.pattern.adapter.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Ein Adapter, der das moderne DataFormatConverter-Interface für ein Legacy-CSV-System implementiert.
 * Dieser Adapter ermöglicht die Integration des Legacy-Systems in moderne Anwendungen.
 */
public class LegacyCSVAdapter implements DataFormatConverter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LegacyCSVAdapter.class);
    
    private final LegacyCSVSystem legacySystem;
    
    /**
     * Erstellt einen neuen LegacyCSVAdapter.
     * 
     * @param legacySystem Das zu adaptierende Legacy-System
     */
    public LegacyCSVAdapter(LegacyCSVSystem legacySystem) {
        this.legacySystem = legacySystem;
        LOGGER.info("LegacyCSVAdapter initialisiert");
    }
    
    @Override
    public String toXml(DataObject data) {
        LOGGER.info("Adapter konvertiert DataObject zu XML via CSV");
        
        // Erst zu CSV konvertieren (Legacy-Format)
        String csv = legacySystem.convertToCSV(
                data.getId(), 
                data.getName(), 
                data.getDescription(), 
                data.getValue());
        
        // Dann manuell zu XML konvertieren
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<data>\n");
        
        // CSV-Daten parsen und in XML-Tags umwandeln
        Object[] parts = legacySystem.parseFromCSV(csv);
        xml.append("  <id>").append(parts[0]).append("</id>\n");
        xml.append("  <name>").append(parts[1]).append("</name>\n");
        xml.append("  <description>").append(parts[2]).append("</description>\n");
        xml.append("  <value>").append(parts[3]).append("</value>\n");
        xml.append("</data>");
        
        return xml.toString();
    }
    
    @Override
    public DataObject fromXml(String xml) throws FormatConversionException {
        LOGGER.info("Adapter konvertiert XML zu DataObject via CSV");
        
        try {
            // XML manuell parsen
            String idPattern = "<id>(.*?)</id>";
            String namePattern = "<name>(.*?)</name>";
            String descPattern = "<description>(.*?)</description>";
            String valuePattern = "<value>(.*?)</value>";
            
            String id = extractTagValue(xml, idPattern);
            String name = extractTagValue(xml, namePattern);
            String desc = extractTagValue(xml, descPattern);
            int value = Integer.parseInt(extractTagValue(xml, valuePattern));
            
            // Zu CSV konvertieren und dann wieder parsen (um das Legacy-System zu nutzen)
            String csv = legacySystem.convertToCSV(id, name, desc, value);
            Object[] parts = legacySystem.parseFromCSV(csv);
            
            // Neues DataObject erstellen
            DataObject data = new DataObject();
            data.setId((String) parts[0]);
            data.setName((String) parts[1]);
            data.setDescription((String) parts[2]);
            data.setValue((Integer) parts[3]);
            
            return data;
        } catch (Exception e) {
            throw new FormatConversionException("Fehler bei der Konvertierung von XML zu DataObject: " + e.getMessage(), e);
        }
    }
    
    /**
     * Hilfsmethode zum Extrahieren von Werten aus XML-Tags.
     * 
     * @param xml Der XML-String
     * @param pattern Das Muster des Tags
     * @return Der extrahierte Wert
     */
    private String extractTagValue(String xml, String pattern) {
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(xml);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    @Override
    public String toJson(DataObject data) {
        LOGGER.info("Adapter konvertiert DataObject zu JSON via CSV");
        
        // Erst zu CSV konvertieren (Legacy-Format)
        String csv = legacySystem.convertToCSV(
                data.getId(), 
                data.getName(), 
                data.getDescription(), 
                data.getValue());
        
        // Dann manuell zu JSON konvertieren
        Object[] parts = legacySystem.parseFromCSV(csv);
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"id\": \"").append(parts[0]).append("\",\n");
        json.append("  \"name\": \"").append(parts[1]).append("\",\n");
        json.append("  \"description\": \"").append(parts[2]).append("\",\n");
        json.append("  \"value\": ").append(parts[3]).append("\n");
        json.append("}");
        
        return json.toString();
    }
    
    @Override
    public DataObject fromJson(String json) throws FormatConversionException {
        LOGGER.info("Adapter konvertiert JSON zu DataObject via CSV");
        
        try {
            // JSON manuell parsen
            String idPattern = "\"id\"\\s*:\\s*\"([^\"]+)\"";
            String namePattern = "\"name\"\\s*:\\s*\"([^\"]+)\"";
            String descPattern = "\"description\"\\s*:\\s*\"([^\"]+)\"";
            String valuePattern = "\"value\"\\s*:\\s*(\\d+)";
            
            String id = extractJsonValue(json, idPattern);
            String name = extractJsonValue(json, namePattern);
            String desc = extractJsonValue(json, descPattern);
            int value = Integer.parseInt(extractJsonValue(json, valuePattern));
            
            // Zu CSV konvertieren und dann wieder parsen (um das Legacy-System zu nutzen)
            String csv = legacySystem.convertToCSV(id, name, desc, value);
            Object[] parts = legacySystem.parseFromCSV(csv);
            
            // Neues DataObject erstellen
            DataObject data = new DataObject();
            data.setId((String) parts[0]);
            data.setName((String) parts[1]);
            data.setDescription((String) parts[2]);
            data.setValue((Integer) parts[3]);
            
            return data;
        } catch (Exception e) {
            throw new FormatConversionException("Fehler bei der Konvertierung von JSON zu DataObject: " + e.getMessage(), e);
        }
    }
    
    /**
     * Hilfsmethode zum Extrahieren von Werten aus JSON.
     * 
     * @param json Der JSON-String
     * @param pattern Das Muster des Feldes
     * @return Der extrahierte Wert
     */
    private String extractJsonValue(String json, String pattern) {
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(json);
        
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    @Override
    public byte[] toBinary(DataObject data) {
        LOGGER.info("Adapter konvertiert DataObject zu binärem Format via CSV");
        
        // Erst zu CSV konvertieren (Legacy-Format)
        String csv = legacySystem.convertToCSV(
                data.getId(), 
                data.getName(), 
                data.getDescription(), 
                data.getValue());
        
        // Einfach die CSV-Bytes zurückgeben
        return csv.getBytes();
    }
    
    @Override
    public DataObject fromBinary(byte[] binary) throws FormatConversionException {
        LOGGER.info("Adapter konvertiert binäres Format zu DataObject via CSV");
        
        try {
            // Bytes zu CSV-String konvertieren
            String csv = new String(binary);
            
            // CSV mit dem Legacy-System parsen
            Object[] parts = legacySystem.parseFromCSV(csv);
            
            // Neues DataObject erstellen
            DataObject data = new DataObject();
            data.setId((String) parts[0]);
            data.setName((String) parts[1]);
            data.setDescription((String) parts[2]);
            data.setValue((Integer) parts[3]);
            
            return data;
        } catch (Exception e) {
            throw new FormatConversionException("Fehler bei der Konvertierung von binärem Format zu DataObject: " + e.getMessage(), e);
        }
    }
    
    /**
     * Direkter Zugriff auf die Legacy-Funktionalität.
     * 
     * @param data Das zu verarbeitende DataObject
     * @return Das Ergebnis der Legacy-Verarbeitung
     */
    public String processWithLegacySystem(DataObject data) {
        String csv = legacySystem.convertToCSV(
                data.getId(), 
                data.getName(), 
                data.getDescription(), 
                data.getValue());
        
        return legacySystem.processCSVData(csv);
    }
}