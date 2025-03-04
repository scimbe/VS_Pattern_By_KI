package de.becke.vs.pattern.pipeline.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Basisimplementierung einer Pipeline, die mehrere Stages sequentiell ausführt.
 * 
 * @param <I> Der Eingabetyp der Pipeline
 * @param <O> Der Ausgabetyp der Pipeline
 */
public class Pipeline<I, O> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);
    
    // Name der Pipeline
    private final String name;
    
    // Liste der Pipeline-Stages
    private final List<PipelineStage<?, ?>> stages = new ArrayList<>();
    
    /**
     * Erstellt eine neue Pipeline mit einem Namen.
     * 
     * @param name Der Name der Pipeline
     */
    public Pipeline(String name) {
        this.name = name;
        LOGGER.info("Pipeline '{}' erstellt", name);
    }
    
    /**
     * Fügt eine Stage zur Pipeline hinzu.
     * 
     * @param stage Die hinzuzufügende Stage
     * @param <T> Der Eingabetyp der Stage
     * @param <R> Der Ausgabetyp der Stage
     * @return Diese Pipeline für Method Chaining
     */
    public <T, R> Pipeline<I, O> addStage(PipelineStage<T, R> stage) {
        stages.add(stage);
        LOGGER.info("Stage '{}' zur Pipeline '{}' hinzugefügt", stage.getStageName(), name);
        return this;
    }
    
    /**
     * Führt die Pipeline mit der angegebenen Eingabe aus.
     * 
     * @param input Die Eingabe für die Pipeline
     * @return Das Ergebnis der Pipeline
     * @throws PipelineException Wenn ein Fehler während der Ausführung auftritt
     */
    @SuppressWarnings("unchecked")
    public O execute(I input) throws PipelineException {
        LOGGER.info("Starte Ausführung der Pipeline '{}'", name);
        
        // Erstelle einen neuen Kontext für diesen Pipeline-Durchlauf
        PipelineContext context = new PipelineContext();
        context.setAttribute("pipeline.name", name);
        
        // Der aktuelle Wert, der durch die Pipeline fließt
        Object current = input;
        
        try {
            // Durchlaufe alle Stages der Pipeline
            for (int i = 0; i < stages.size(); i++) {
                PipelineStage<Object, Object> stage = (PipelineStage<Object, Object>) stages.get(i);
                
                LOGGER.info("Führe Stage '{}' ({}. von {}) aus", 
                        stage.getStageName(), i + 1, stages.size());
                
                try {
                    // Verarbeite die Eingabe mit der aktuellen Stage
                    current = stage.process(current, context);
                    
                    LOGGER.info("Stage '{}' erfolgreich abgeschlossen", stage.getStageName());
                    
                } catch (Exception e) {
                    LOGGER.error("Fehler in Stage '{}': {}", stage.getStageName(), e.getMessage(), e);
                    
                    // Setze den Fehler im Kontext und wirf eine PipelineException
                    context.setError(e);
                    throw new PipelineException("Fehler während der Verarbeitung in Stage '" + 
                            stage.getStageName() + "'", e);
                }
            }
            
            LOGGER.info("Pipeline '{}' erfolgreich abgeschlossen in {}ms", 
                    name, context.getDuration());
            
            // Gib das Endergebnis zurück
            return (O) current;
            
        } catch (PipelineException e) {
            LOGGER.error("Pipeline '{}' fehlgeschlagen: {}", name, e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unerwarteter Fehler in Pipeline '{}': {}", name, e.getMessage(), e);
            throw new PipelineException("Unerwarteter Fehler in Pipeline: " + e.getMessage(), e);
        }
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
    public List<PipelineStage<?, ?>> getStages() {
        return Collections.unmodifiableList(stages);
    }
}