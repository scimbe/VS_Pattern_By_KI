package de.becke.vs.pattern.pipeline.core;

/**
 * Eine Exception, die bei Fehlern während der Pipeline-Verarbeitung geworfen wird.
 */
public class PipelineException extends Exception {
    
    /**
     * Erstellt eine neue PipelineException mit einer Nachricht.
     * 
     * @param message Die Fehlernachricht
     */
    public PipelineException(String message) {
        super(message);
    }
    
    /**
     * Erstellt eine neue PipelineException mit einer Nachricht und einer Ursache.
     * 
     * @param message Die Fehlernachricht
     * @param cause Die Ursache des Fehlers
     */
    public PipelineException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Erstellt eine neue PipelineException mit einer Nachricht, einer Ursache und dem Namen der Stage.
     * 
     * @param message Die Fehlernachricht
     * @param cause Die Ursache des Fehlers
     * @param stageName Der Name der Stage, in der der Fehler aufgetreten ist
     */
    public PipelineException(String message, Throwable cause, String stageName) {
        super("Fehler in Stage '" + stageName + "': " + message, cause);
    }
}