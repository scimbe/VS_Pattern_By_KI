package de.becke.vs.pattern.adapter.format;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Implementierung der DataFormatConverter-Schnittstelle zur Konvertierung zwischen verschiedenen Datenformaten.
 */
public class DefaultFormatConverter implements DataFormatConverter {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFormatConverter.class);
    
    // Gson für JSON-Konvertierung
    private final Gson gson = new Gson();
    
    @Override
    public String toXml(DataObject data) {
        LOGGER.info("Konvertiere DataObject zu XML");
        
        // In einer realen Implementierung würde hier eine XML-Bibliothek verwendet werden
        // Für dieses Beispiel erstellen wir einen einfachen XML-String
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<data>\n");
        xml.append("  <id>").append(data.getId()).append("</id>\n");
        xml.append("  <name>").append(data.getName()).append("</name>\n");
        xml.append("  <description>").append(data.getDescription()).append("</description>\n");
        xml.append("  <value>").append(data.getValue()).append("</value>\n");
        xml.append("</data>");
        
        return xml.toString();
    }
    
    @Override
    public DataObject fromXml(String xml) throws FormatConversionException {
        LOGGER.info("Konvertiere XML zu DataObject");
        
        try {
            // In einer realen Implementierung würde hier eine XML-Bibliothek verwendet werden
            // Für dieses Beispiel parsen wir einfach den XML-String manuell
            DataObject data = new DataObject();
            
            // Einfaches Parsing (nur zu Demonstrationszwecken)
            String idPattern = "<id>(.*?)</id>";
            String namePattern = "<name>(.*?)</name>";
            String descPattern = "<description>(.*?)</description>";
            String valuePattern = "<value>(.*?)</value>";
            
            data.setId(extractTagValue(xml, idPattern));
            data.setName(extractTagValue(xml, namePattern));
            data.setDescription(extractTagValue(xml, descPattern));
            
            String valueStr = extractTagValue(xml, valuePattern);
            data.setValue(Integer.parseInt(valueStr));
            
            return data;
        } catch (Exception e) {
            throw new FormatConversionException("Fehler beim Parsen des XML: " + e.getMessage(), e);
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
        LOGGER.info("Konvertiere DataObject zu JSON");
        return gson.toJson(data);
    }
    
    @Override
    public DataObject fromJson(String json) throws FormatConversionException {
        LOGGER.info("Konvertiere JSON zu DataObject");
        
        try {
            return gson.fromJson(json, DataObject.class);
        } catch (Exception e) {
            throw new FormatConversionException("Fehler beim Parsen des JSON: " + e.getMessage(), e);
        }
    }
    
    @Override
    public byte[] toBinary(DataObject data) {
        LOGGER.info("Konvertiere DataObject zu binärem Format");
        
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            
            // In einer realen Implementierung würde hier ein effizienteres Serialisierungsformat verwendet werden
            // Für dieses Beispiel erstellen wir ein einfaches binäres Format
            byte[] idBytes = data.getId().getBytes(StandardCharsets.UTF_8);
            byte[] nameBytes = data.getName().getBytes(StandardCharsets.UTF_8);
            byte[] descBytes = data.getDescription().getBytes(StandardCharsets.UTF_8);
            int value = data.getValue();
            
            // Schreibe die Längen der Strings
            oos.writeInt(idBytes.length);
            oos.writeInt(nameBytes.length);
            oos.writeInt(descBytes.length);
            
            // Schreibe die Byte-Arrays
            oos.write(idBytes);
            oos.write(nameBytes);
            oos.write(descBytes);
            
            // Schreibe den Wert
            oos.writeInt(value);
            
            oos.flush();
            return bos.toByteArray();
            
        } catch (IOException e) {
            LOGGER.error("Fehler bei der Binärkonvertierung: {}", e.getMessage(), e);
            // In einer realen Implementierung sollte eine Exception geworfen werden
            return new byte[0];
        }
    }
    
    @Override
    public DataObject fromBinary(byte[] binary) throws FormatConversionException {
        LOGGER.info("Konvertiere binäres Format zu DataObject");
        
        try (ByteArrayInputStream bis = new ByteArrayInputStream(binary);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            
            // Lese die Längen der Strings
            int idLength = ois.readInt();
            int nameLength = ois.readInt();
            int descLength = ois.readInt();
            
            // Lese die Byte-Arrays
            byte[] idBytes = new byte[idLength];
            byte[] nameBytes = new byte[nameLength];
            byte[] descBytes = new byte[descLength];
            
            ois.read(idBytes);
            ois.read(nameBytes);
            ois.read(descBytes);
            
            // Lese den Wert
            int value = ois.readInt();
            
            // Erstelle das DataObject
            DataObject data = new DataObject();
            data.setId(new String(idBytes, StandardCharsets.UTF_8));
            data.setName(new String(nameBytes, StandardCharsets.UTF_8));
            data.setDescription(new String(descBytes, StandardCharsets.UTF_8));
            data.setValue(value);
            
            return data;
            
        } catch (IOException e) {
            throw new FormatConversionException("Fehler beim Parsen des binären Formats: " + e.getMessage(), e);
        }
    }
}