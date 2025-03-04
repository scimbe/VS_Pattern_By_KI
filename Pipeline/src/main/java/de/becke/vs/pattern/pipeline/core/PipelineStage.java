package de.becke.vs.pattern.pipeline.core;

/**
 * Eine Schnittstelle, die eine Phase (Stage) in einer Pipeline repräsentiert.
 * 
 * @param <I> Der Typ der Eingabedaten
 * @param <O> Der Typ der Ausgabedaten
 */
public interface PipelineStage<I, O> {
    
    /**
     * Verarbeitet die Eingabedaten und erzeugt Ausgabedaten.
     * 
     * @param input Die Eingabedaten
     * @param context Der Pipeline-Kontext mit zusätzlichen Metadaten
     * @return Die Ausgabedaten
     * @throws PipelineException Wenn ein Fehler während der Verarbeitung auftritt
     */
    O process(I input, PipelineContext context) throws PipelineException;
    
    /**
     * Gibt den Namen der Stage zurück.
     * 
     * @return Der Name der Stage
     */
    String getStageName();
}