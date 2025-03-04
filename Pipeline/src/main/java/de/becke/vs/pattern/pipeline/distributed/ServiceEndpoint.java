package de.becke.vs.pattern.pipeline.distributed;

import de.becke.vs.pattern.pipeline.core.PipelineContext;

/**
 * Eine Schnittstelle, die einen Dienst-Endpunkt in einer verteilten Pipeline repräsentiert.
 * 
 * @param <I> Der Typ der Eingabedaten
 * @param <O> Der Typ der Ausgabedaten
 */
public interface ServiceEndpoint<I, O> {
    
    /**
     * Ruft den Dienst auf und verarbeitet die Eingabedaten.
     * 
     * @param input Die Eingabedaten
     * @param context Der Pipeline-Kontext mit zusätzlichen Metadaten
     * @return Die Ausgabedaten
     * @throws Exception Wenn ein Fehler während des Aufrufs auftritt
     */
    O invokeService(I input, PipelineContext context) throws Exception;
    
    /**
     * Gibt die Adresse des Endpunkts zurück.
     * 
     * @return Die Adresse des Endpunkts (z.B. URL oder Dienstname)
     */
    String getEndpointAddress();
}