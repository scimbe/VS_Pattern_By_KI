package de.becke.vs.pattern.adapter.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstriert die Verwendung von Formatadaptern.
 */
public class FormatAdapterDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormatAdapterDemo.class);
    
    /**
     * Führt die Formatadapter-Demonstration durch.
     */
    public void runDemo() {
        LOGGER.info("Starte Formatadapter-Demonstration");
        
        // Erstelle ein Beispiel-Datenobjekt
        DataObject sampleData = new DataObject("id-123", "Beispielobjekt", "Ein Beispiel für Formatadapter", 42);
        
        // 1. Standard-Formatkonverter
        demonstrateStandardConverter(sampleData);
        
        // 2. Legacy-CSV-Adapter
        demonstrateLegacyCSVAdapter(sampleData);
        
        // 3. Konvertierung zwischen Formaten
        demonstrateFormatConversion();
        
        LOGGER.info("Formatadapter-Demonstration abgeschlossen");
    }
    
    /**
     * Demonstriert den Standard-Formatkonverter.
     * 
     * @param data Das zu konvertierende Datenobjekt
     */
    private void demonstrateStandardConverter(DataObject data) {
        LOGGER.info("\n1. Standard-Formatkonverter Demonstration:");
        
        DefaultFormatConverter converter = new DefaultFormatConverter();
        
        // XML-Konvertierung
        String xml = converter.toXml(data);
        LOGGER.info("Zu XML konvertiert:\n{}", xml);
        
        try {
            DataObject fromXml = converter.fromXml(xml);
            LOGGER.info("Zurück zu DataObject: {}", fromXml);
        } catch (FormatConversionException e) {
            LOGGER.error("Fehler bei XML-Konvertierung: {}", e.getMessage());
        }
        
        // JSON-Konvertierung
        String json = converter.toJson(data);
        LOGGER.info("Zu JSON konvertiert:\n{}", json);
        
        try {
            DataObject fromJson = converter.fromJson(json);
            LOGGER.info("Zurück zu DataObject: {}", fromJson);
        } catch (FormatConversionException e) {
            LOGGER.error("Fehler bei JSON-Konvertierung: {}", e.getMessage());
        }
        
        // Binäre Konvertierung
        byte[] binary = converter.toBinary(data);
        LOGGER.info("Zu Binärformat konvertiert (Länge: {} Bytes)", binary.length);
        
        try {
            DataObject fromBinary = converter.fromBinary(binary);
            LOGGER.info("Zurück zu DataObject: {}", fromBinary);
        } catch (FormatConversionException e) {
            LOGGER.error("Fehler bei Binär-Konvertierung: {}", e.getMessage());
        }
    }
    
    /**
     * Demonstriert den Legacy-CSV-Adapter.
     * 
     * @param data Das zu konvertierende Datenobjekt
     */
    private void demonstrateLegacyCSVAdapter(DataObject data) {
        LOGGER.info("\n2. Legacy-CSV-Adapter Demonstration:");
        
        // Erstelle das Legacy-System und den Adapter
        LegacyCSVSystem legacySystem = new LegacyCSVSystem();
        LegacyCSVAdapter adapter = new LegacyCSVAdapter(legacySystem);
        
        // Direkter Zugriff auf das Legacy-System
        String csv = legacySystem.convertToCSV(data.getId(), data.getName(), data.getDescription(), data.getValue());
        LOGGER.info("Legacy-System CSV-Format: {}", csv);
        LOGGER.info("Legacy-System Verarbeitung: {}", legacySystem.processCSVData(csv));
        
        // Zugriff über den Adapter
        String xml = adapter.toXml(data);
        LOGGER.info("Adapter (XML über Legacy): \n{}", xml);
        
        String json = adapter.toJson(data);
        LOGGER.info("Adapter (JSON über Legacy): \n{}", json);
        
        byte[] binary = adapter.toBinary(data);
        LOGGER.info("Adapter (Binär über Legacy, Länge: {} Bytes)", binary.length);
        
        // Direkte Legacy-Verarbeitung über den Adapter
        String result = adapter.processWithLegacySystem(data);
        LOGGER.info("Legacy-Verarbeitung über Adapter: {}", result);
    }
    
    /**
     * Demonstriert die Konvertierung zwischen verschiedenen Formaten.
     */
    private void demonstrateFormatConversion() {
        LOGGER.info("\n3. Konvertierung zwischen Formaten:");
        
        // Erstelle die Konverter
        DefaultFormatConverter standardConverter = new DefaultFormatConverter();
        LegacyCSVSystem legacySystem = new LegacyCSVSystem();
        LegacyCSVAdapter legacyAdapter = new LegacyCSVAdapter(legacySystem);
        
        try {
            // Erstelle Beispieldaten im XML-Format (mit Standard-Konverter)
            DataObject originalData = new DataObject("format-123", "Formatkonvertierung", "Demonstration der Formatkonvertierung", 99);
            String xml = standardConverter.toXml(originalData);
            LOGGER.info("Originaldaten als XML (Standard-Konverter):\n{}", xml);
            
            // Konvertiere XML zu DataObject mit Legacy-Adapter
            DataObject convertedData = legacyAdapter.fromXml(xml);
            LOGGER.info("Zu DataObject konvertiert mit Legacy-Adapter: {}", convertedData);
            
            // Konvertiere zu JSON mit Standard-Konverter
            String json = standardConverter.toJson(convertedData);
            LOGGER.info("Zu JSON konvertiert mit Standard-Konverter:\n{}", json);
            
            // Konvertiere JSON zurück zu DataObject mit Legacy-Adapter
            DataObject finalData = legacyAdapter.fromJson(json);
            LOGGER.info("Zurück zu DataObject mit Legacy-Adapter: {}", finalData);
            
            // Überprüfe die Konvertierungskette
            boolean dataPreserved = originalData.getId().equals(finalData.getId()) && 
                                   originalData.getName().equals(finalData.getName()) &&
                                   originalData.getValue() == finalData.getValue();
            
            LOGGER.info("Datenintegrität nach Konvertierungskette erhalten: {}", dataPreserved);
            
        } catch (FormatConversionException e) {
            LOGGER.error("Fehler bei der Formatkonvertierung: {}", e.getMessage());
        }
    }
}