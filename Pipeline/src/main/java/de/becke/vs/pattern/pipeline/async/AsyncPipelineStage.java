package de.becke.vs.pattern.pipeline.async;

import de.becke.vs.pattern.pipeline.core.PipelineContext;
import de.becke.vs.pattern.pipeline.core.PipelineException;

import java.util.concurrent.CompletableFuture;

/**
 * Eine Schnittstelle, die eine asynchrone Phase (Stage) in einer Pipeline repräsentiert.
 * 
 * @param <I> Der Typ der Eingabedaten
 * @param <O> Der Typ der Ausgabedaten
 */
public interface AsyncPipelineStage<I, O> {
    
    /**
     * Verarbeitet die Eingabedaten asynchron und erzeugt Ausgabedaten.
     * 
     * @param input Die Eingabedaten
     * @param context Der Pipeline-Kontext mit zusätzlichen Metadaten
     * @return Ein CompletableFuture mit den Ausgabedaten
     */
    CompletableFuture<O> processAsync(I input, PipelineContext context);
    
    /**
     * Gibt den Namen der Stage zurück.
     * 
     * @return Der Name der Stage
     */
    String getStageName();
}