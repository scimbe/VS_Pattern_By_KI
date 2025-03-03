package de.becke.vs.pattern.dependencyinjection.servicelocator;

/**
 * Exception-Klasse, die geworfen wird, wenn ein angeforderter Service im
 * ServiceLocator nicht gefunden werden kann.
 */
public class ServiceNotFoundException extends RuntimeException {
    
    /**
     * Erstellt eine neue ServiceNotFoundException mit einer Nachricht.
     * 
     * @param message Die Fehlermeldung
     */
    public ServiceNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Erstellt eine neue ServiceNotFoundException mit einer Nachricht und einer Ursache.
     * 
     * @param message Die Fehlermeldung
     * @param cause Die Ursache des Fehlers
     */
    public ServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}