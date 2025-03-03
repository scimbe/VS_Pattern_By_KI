package de.becke.vs.pattern.proxy.common;

/**
 * Eine spezialisierte Exception für Fehler, die bei der Kommunikation
 * mit RemoteServices auftreten können.
 */
public class ServiceException extends Exception {
    
    // Verschiedene Fehlertypen, die von einem Service auftreten können
    public enum ErrorType {
        // Verbindungsprobleme
        CONNECTION_REFUSED,
        CONNECTION_TIMEOUT,
        CONNECTION_LOST,
        
        // Protokollfehler
        PROTOCOL_ERROR,
        SERIALIZATION_ERROR,
        
        // Authentifizierungsfehler
        AUTHENTICATION_FAILED,
        UNAUTHORIZED,
        
        // Anwendungsfehler
        SERVICE_UNAVAILABLE,
        INVALID_REQUEST,
        RESOURCE_NOT_FOUND,
        
        // Unbekannter Fehler
        UNKNOWN_ERROR
    }
    
    private final ErrorType errorType;
    
    /**
     * Konstruktor mit Fehlermeldung und Fehlertyp.
     * 
     * @param message Die Fehlermeldung
     * @param errorType Der Fehlertyp
     */
    public ServiceException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
    
    /**
     * Konstruktor mit Fehlermeldung, Fehlertyp und Ursache.
     * 
     * @param message Die Fehlermeldung
     * @param errorType Der Fehlertyp
     * @param cause Die Ursache des Fehlers
     */
    public ServiceException(String message, ErrorType errorType, Throwable cause) {
        super(message, cause);
        this.errorType = errorType;
    }
    
    /**
     * Gibt den Fehlertyp zurück.
     * 
     * @return Der Fehlertyp dieser Exception
     */
    public ErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * Überprüft, ob der Fehler ein Verbindungsproblem darstellt.
     * 
     * @return true, wenn es sich um ein Verbindungsproblem handelt, sonst false
     */
    public boolean isConnectionError() {
        return errorType == ErrorType.CONNECTION_REFUSED ||
               errorType == ErrorType.CONNECTION_TIMEOUT ||
               errorType == ErrorType.CONNECTION_LOST;
    }
    
    /**
     * Überprüft, ob der Fehler ein Authentifizierungsproblem darstellt.
     * 
     * @return true, wenn es sich um ein Authentifizierungsproblem handelt, sonst false
     */
    public boolean isAuthenticationError() {
        return errorType == ErrorType.AUTHENTICATION_FAILED ||
               errorType == ErrorType.UNAUTHORIZED;
    }
    
    /**
     * Überprüft, ob der Fehler vorübergehend ist und ein Wiederholungsversuch sinnvoll sein könnte.
     * 
     * @return true, wenn der Fehler vorübergehend sein könnte, sonst false
     */
    public boolean isRetryable() {
        return errorType == ErrorType.CONNECTION_TIMEOUT ||
               errorType == ErrorType.CONNECTION_LOST ||
               errorType == ErrorType.SERVICE_UNAVAILABLE;
    }
}