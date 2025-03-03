package de.becke.vs.pattern.adapter.format;

/**
 * Schnittstelle für die Formatkonvertierung.
 * Diese Schnittstelle definiert Methoden zum Konvertieren von Datenobjekten in
 * verschiedene Formate und umgekehrt.
 */
public interface DataFormatConverter {
    
    /**
     * Konvertiert ein DataObject in das XML-Format.
     * 
     * @param data Das zu konvertierende DataObject
     * @return Die XML-Repräsentation
     */
    String toXml(DataObject data);
    
    /**
     * Konvertiert eine XML-Repräsentation in ein DataObject.
     * 
     * @param xml Die XML-Repräsentation
     * @return Das konvertierte DataObject
     * @throws FormatConversionException Wenn die Konvertierung fehlschlägt
     */
    DataObject fromXml(String xml) throws FormatConversionException;
    
    /**
     * Konvertiert ein DataObject in das JSON-Format.
     * 
     * @param data Das zu konvertierende DataObject
     * @return Die JSON-Repräsentation
     */
    String toJson(DataObject data);
    
    /**
     * Konvertiert eine JSON-Repräsentation in ein DataObject.
     * 
     * @param json Die JSON-Repräsentation
     * @return Das konvertierte DataObject
     * @throws FormatConversionException Wenn die Konvertierung fehlschlägt
     */
    DataObject fromJson(String json) throws FormatConversionException;
    
    /**
     * Konvertiert ein DataObject in ein binäres Format.
     * 
     * @param data Das zu konvertierende DataObject
     * @return Die binäre Repräsentation als Byte-Array
     */
    byte[] toBinary(DataObject data);
    
    /**
     * Konvertiert eine binäre Repräsentation in ein DataObject.
     * 
     * @param binary Die binäre Repräsentation
     * @return Das konvertierte DataObject
     * @throws FormatConversionException Wenn die Konvertierung fehlschlägt
     */
    DataObject fromBinary(byte[] binary) throws FormatConversionException;
}