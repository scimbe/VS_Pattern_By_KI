package pipeline;

import java.util.concurrent.CompletableFuture;

/**
 * Generische Interface für eine Stage in der Pipeline.
 * @param <I> Eingabetyp
 * @param <O> Ausgabetyp
 */
public interface Stage<I, O> {
    /**
     * Verarbeitet die Eingabe und gibt eine CompletableFuture mit dem Ergebnis zurück.
     * @param input Eingabedaten
     * @return CompletableFuture mit dem Ergebnis der Verarbeitung
     */
    CompletableFuture<O> process(I input);
}