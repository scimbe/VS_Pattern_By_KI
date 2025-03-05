package de.becke.vs.pattern.pipeline.async;

import de.becke.vs.pattern.pipeline.core.PipelineContext;
import de.becke.vs.pattern.pipeline.core.PipelineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Implementierung einer asynchronen Pipeline, die Stages parallel oder asynchron ausführt.
 * 
 * @param <I> Der Eingabetyp der Pipeline
 * @param <O> Der Ausgabetyp der Pipeline
 */
public class AsyncPipeline<I, O> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncPipeline.class);
    
    // Name der Pipeline
    private final String name;
    
    // Liste der Pipeline-Stages
    private final List<AsyncPipelineStage<?, ?>> stages = new ArrayList<>();
    
    /**
     * Erstellt eine neue asynchrone Pipeline mit einem Namen.
     * 
     * @param name Der Name der Pipeline
     */
    public AsyncPipeline(String name) {
        this.name = name;
        LOGGER.info("Asynchrone Pipeline '{}' erstellt", name);
    }
    
    /**
     * Fügt eine Stage zur asynchronen Pipeline hinzu.
     * 
     * @param stage Die hinzuzufügende Stage
     * @param <T> Der Eingabetyp der Stage
     * @param <R> Der Ausgabetyp der Stage
     * @return Diese Pipeline für Method Chaining
     */
    public <T, R> AsyncPipeline<I, O> addStage(AsyncPipelineStage<T, R> stage) {
        stages.add(stage);
        LOGGER.info("Stage '{}' zur asynchronen Pipeline '{}' hinzugefügt", stage.getStageName(), name);
        return this;
    }
    
    /**
     * Führt die asynchrone Pipeline mit der angegebenen Eingabe aus.
     * 
     * @param input Die Eingabe für die Pipeline
     * @return Das Ergebnis der Pipeline
     * @throws PipelineException Wenn ein Fehler während der Ausführung auftritt
     */
    @SuppressWarnings("unchecked")
    public O execute(I input) throws PipelineException {
        LOGGER.info("Starte Ausführung der asynchronen Pipeline '{}'", name);
        
        CompletableFuture<O> future = executeAsync(input);
        
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PipelineException("Ausführung der asynchronen Pipeline wurde unterbrochen", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            
            // Wenn die Ursache eine RuntimeException mit einer PipelineException ist, extrahieren wir die PipelineException
            if (cause instanceof RuntimeException && cause.getCause() instanceof PipelineException) {
                throw (PipelineException) cause.getCause();
            }
            
            // Wenn die Ursache direkt eine PipelineException ist
            if (cause instanceof PipelineException) {
                throw (PipelineException) cause;
            }
            
            // Andernfalls verpacken wir die Ursache in einer neuen PipelineException
            throw new PipelineException("Unerwarteter Fehler in asynchroner Pipeline", cause);
        }
    }
    
    /**
     * Führt die asynchrone Pipeline mit der angegebenen Eingabe aus und gibt ein CompletableFuture zurück.
     * 
     * @param input Die Eingabe für die Pipeline
     * @return Ein CompletableFuture mit dem Ergebnis der Pipeline
     */
    @SuppressWarnings("unchecked")
    public CompletableFuture<O> executeAsync(I input) {
        LOGGER.info("Starte asynchrone Ausführung der Pipeline '{}'", name);
        
        // Erstelle einen neuen Kontext für diesen Pipeline-Durchlauf
        PipelineContext context = new PipelineContext();
        context.setAttribute("pipeline.name", name);
        context.setAttribute("pipeline.type", "async");
        
        // Starte mit einem CompletableFuture, das den Input enthält
        CompletableFuture<Object> future = CompletableFuture.completedFuture(input);
        
        // Verkette alle Stages nacheinander asynchron
        for (int i = 0; i < stages.size(); i++) {
            final int stageIndex = i;
            AsyncPipelineStage<Object, Object> stage = (AsyncPipelineStage<Object, Object>) stages.get(i);
            
            future = future.thenCompose(result -> {
                LOGGER.info("Führe asynchrone Stage '{}' ({}. von {}) aus", 
                        stage.getStageName(), stageIndex + 1, stages.size());
                
                try {
                    // Verarbeite die Eingabe mit der aktuellen Stage
                    return stage.processAsync(result, context)
                        .whenComplete((r, e) -> {
                            if (e != null) {
                                LOGGER.error("Fehler in asynchroner Stage '{}': {}", 
                                        stage.getStageName(), e.getMessage(), e);
                                context.setError(e);
                            } else {
                                LOGGER.info("Asynchrone Stage '{}' erfolgreich abgeschlossen", 
                                        stage.getStageName());
                            }
                        });
                } catch (Exception e) {
                    LOGGER.error("Fehler beim Starten der asynchronen Stage '{}': {}", 
                            stage.getStageName(), e.getMessage(), e);
                    context.setError(e);
                    return CompletableFuture.failedFuture(
                            new PipelineException("Fehler beim Starten der asynchronen Stage '" + 
                                    stage.getStageName() + "'", e));
                }
            });
        }
        
        // Wandle das Ergebnis in den erwarteten Ausgabetyp um
        return future.thenApply(result -> {
            LOGGER.info("Asynchrone Pipeline '{}' erfolgreich abgeschlossen in {}ms", 
                    name, context.getDuration());
            return (O) result;
        }).exceptionally(e -> {
            Throwable cause = e.getCause();
            LOGGER.error("Asynchrone Pipeline '{}' fehlgeschlagen: {}", name, cause != null ? cause.getMessage() : e.getMessage(), e);
            
            // Werfen der originalen Exception, wenn sie eine PipelineException ist
            if (cause instanceof PipelineException) {
                throw new RuntimeException(cause);
            } else {
                // Verpacken der Ursache in eine neue RuntimeException
                throw new RuntimeException(cause != null ? cause : e);
            }
        });
    }
    
    /**
     * Gibt den Namen der Pipeline zurück.
     * 
     * @return Der Name der Pipeline
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gibt eine unveränderliche Liste aller Stages in dieser Pipeline zurück.
     * 
     * @return Die Liste der Stages
     */
    public List<AsyncPipelineStage<?, ?>> getStages() {
        return Collections.unmodifiableList(stages);
    }
}