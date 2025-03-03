package de.becke.vs.pattern.adapter;

import de.becke.vs.pattern.adapter.format.DataObject;
import de.becke.vs.pattern.adapter.format.DefaultFormatConverter;
import de.becke.vs.pattern.adapter.format.FormatConversionException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testklasse für den Format-Adapter.
 */
public class FormatAdapterTest {

    /**
     * Testet die XML-Konvertierung des DefaultFormatConverter.
     */
    @Test
    public void testXmlConversion() throws FormatConversionException {
        // Erstelle einen Format-Konverter
        DefaultFormatConverter converter = new DefaultFormatConverter();
        
        // Erstelle ein Test-Datenobjekt
        DataObject originalData = new DataObject("test-123", "Test Object", "Test Description", 42);
        
        // Konvertiere zu XML
        String xml = converter.toXml(originalData);
        
        // Konvertiere zurück zu DataObject
        DataObject convertedData = converter.fromXml(xml);
        
        // Überprüfe die Konvertierung
        assertEquals("ID sollte erhalten bleiben", originalData.getId(), convertedData.getId());
        assertEquals("Name sollte erhalten bleiben", originalData.getName(), convertedData.getName());
        assertEquals("Beschreibung sollte erhalten bleiben", originalData.getDescription(), convertedData.getDescription());
        assertEquals("Wert sollte erhalten bleiben", originalData.getValue(), convertedData.getValue());
    }
    
    /**
     * Testet die JSON-Konvertierung des DefaultFormatConverter.
     */
    @Test
    public void testJsonConversion() throws FormatConversionException {
        // Erstelle einen Format-Konverter
        DefaultFormatConverter converter = new DefaultFormatConverter();
        
        // Erstelle ein Test-Datenobjekt
        DataObject originalData = new DataObject("test-456", "JSON Test", "Testing JSON conversion", 99);
        
        // Konvertiere zu JSON
        String json = converter.toJson(originalData);
        
        // Konvertiere zurück zu DataObject
        DataObject convertedData = converter.fromJson(json);
        
        // Überprüfe die Konvertierung
        assertEquals("ID sollte erhalten bleiben", originalData.getId(), convertedData.getId());
        assertEquals("Name sollte erhalten bleiben", originalData.getName(), convertedData.getName());
        assertEquals("Beschreibung sollte erhalten bleiben", originalData.getDescription(), convertedData.getDescription());
        assertEquals("Wert sollte erhalten bleiben", originalData.getValue(), convertedData.getValue());
    }
    
    /**
     * Testet die Fehlerbehandlung bei ungültigen XML-Eingaben.
     */
    @Test(expected = FormatConversionException.class)
    public void testInvalidXmlConversion() throws FormatConversionException {
        // Erstelle einen Format-Konverter
        DefaultFormatConverter converter = new DefaultFormatConverter();
        
        // Versuche, ungültiges XML zu konvertieren
        converter.fromXml("<invalid>XML<missing>tags</invalid>");
    }
}