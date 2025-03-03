package de.becke.vs.pattern.adapter.format;

/**
 * Exception, die bei Fehlern während der Formatkonvertierung geworfen wird.
 */
public class FormatConversionException extends Exception {
    
    /**
     * Erstellt eine neue FormatConversionException.
     * 
     * @param message Die Fehlermeldung
     */
    public FormatConversionException(String message) {
        super(message);
    }
    
    /**
     * Erstellt eine neue FormatConversionException mit einer Ursache.
     * 
     * @param message Die Fehlermeldung
     * @param cause Die Ursache
     */
    public FormatConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}