package de.becke.vs.pattern.callback.common;

/**
 * Enum zur Darstellung des Status einer asynchronen Operation.
 */
public enum OperationStatus {
    
    /**
     * Die Operation wurde noch nicht gestartet.
     */
    PENDING,
    
    /**
     * Die Operation wird gerade ausgeführt.
     */
    RUNNING,
    
    /**
     * Die Operation wurde erfolgreich abgeschlossen.
     */
    COMPLETED,
    
    /**
     * Bei der Operation ist ein Fehler aufgetreten.
     */
    FAILED
}