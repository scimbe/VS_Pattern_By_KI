package de.becke.vs.pattern.pipeline.distributed;

import de.becke.vs.pattern.pipeline.core.PipelineContext;
import de.becke.vs.pattern.pipeline.core.PipelineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementierung einer verteilten Pipeline, die Verarbeitungsschritte auf verschiedene
 * Dienste oder Knoten verteilt.
 * 
 * @param <I> Der Eingabetyp der Pipeline
 * @param <O> Der Ausgabetyp der Pipeline
 */
public class DistributedPipeline<I, O> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DistributedPipeline.class);
    
    // Name der Pipeline
    private final String name;
    
    // Map zur Speicherung von Stage-Endpunkten
    private final Map<String, ServiceEndpoint<?, ?>> stageEndpoints = new HashMap<>();
    
    // Liste zur Speicherung der Reihenfolge der Stages
    private final List<String> stageSequence = new ArrayList<>();
    
    /**
     * Erstellt eine neue verteilte Pipeline mit einem Namen.
     * 
     * @param name Der Name der Pipeline
     */
    public DistributedPipeline(String name) {
        this.name = name;
        LOGGER.info("Verteilte Pipeline '{}' erstellt", name);
    }
    
    /**
     * Registriert eine Stage mit einem Endpunkt in der Pipeline.
     * 
     * @param stageName Der Name der Stage
     * @param endpoint Der Endpunkt der Stage
     * @param <T> Der Eingabetyp der Stage
     * @param <R> Der Ausgabetyp der Stage
     * @return Diese Pipeline für Method Chaining
     */
    public <T, R> DistributedPipeline<I, O> registerStage(String stageName, ServiceEndpoint<T, R> endpoint) {
        stageEndpoints.put(stageName, endpoint);
        stageSequence.add(stageName);
        LOGGER.info("Stage '{}' zur verteilten Pipeline '{}' hinzugefügt", stageName, name);
        return this;
    }
    
    /**
     * Führt die verteilte Pipeline mit der angegebenen Eingabe aus.
     * 
     * @param input Die Eingabe für die Pipeline
     * @return Das Ergebnis der Pipeline
     * @throws PipelineException Wenn ein Fehler während der Ausführung auftritt
     */
    @SuppressWarnings("unchecked")
    public O execute(I input) throws PipelineException {
        LOGGER.info("Starte Ausführung der verteilten Pipeline '{}'", name);
        
        // Erstelle einen neuen Kontext für diesen Pipeline-Durchlauf
        PipelineContext context = new PipelineContext();
        context.setAttribute("pipeline.name", name);
        context.setAttribute("pipeline.type", "distributed");
        
        // Der aktuelle Wert, der durch die Pipeline fließt
        Object current = input;
        
        try {
            // Durchlaufe alle Stages der Pipeline in der definierten Reihenfolge
            for (int i = 0; i < stageSequence.size(); i++) {
                String stageName = stageSequence.get(i);
                ServiceEndpoint<Object, Object> endpoint = (ServiceEndpoint<Object, Object>) stageEndpoints.get(stageName);
                
                if (endpoint == null) {
                    throw new PipelineException("Kein Endpunkt für Stage '" + stageName + "' gefunden");
                }
                
                LOGGER.info("Führe verteilte Stage '{}' ({}. von {}) aus", 
                        stageName, i + 1, stageSequence.size());
                
                try {
                    // Führe den Service aus und erhalte das Ergebnis
                    current = endpoint.invokeService(current, context);
                    
                    LOGGER.info("Verteilte Stage '{}' erfolgreich abgeschlossen", stageName);
                    
                } catch (Exception e) {
                    LOGGER.error("Fehler in verteilter Stage '{}': {}", stageName, e.getMessage(), e);
                    
                    // Setze den Fehler im Kontext und wirf eine PipelineException
                    context.setError(e);
                    throw new PipelineException("Fehler während der Verarbeitung in verteilter Stage '" + 
                            stageName + "'", e);
                }
            }
            
            LOGGER.info("Verteilte Pipeline '{}' erfolgreich abgeschlossen in {}ms", 
                    name, context.getDuration());
            
            // Gib das Endergebnis zurück
            return (O) current;
            
        } catch (PipelineException e) {
            LOGGER.error("Verteilte Pipeline '{}' fehlgeschlagen: {}", name, e.getMessage());
            throw e;
        } catch (Exception e) {
            LOGGER.error("Unerwarteter Fehler in verteilter Pipeline '{}': {}", name, e.getMessage(), e);
            throw new PipelineException("Unerwarteter Fehler in verteilter Pipeline: " + e.getMessage(), e);
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
}