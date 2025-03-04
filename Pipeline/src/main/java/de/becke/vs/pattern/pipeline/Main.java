package de.becke.vs.pattern.pipeline;

import de.becke.vs.pattern.pipeline.core.Pipeline;
import de.becke.vs.pattern.pipeline.core.PipelineContext;
import de.becke.vs.pattern.pipeline.core.PipelineException;
import de.becke.vs.pattern.pipeline.core.PipelineStage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hauptklasse zur Demonstration des Pipeline-Patterns.
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("Starte Pipeline-Pattern Demonstration");

        try {
            // Einfaches Beispiel: Textverarbeitung durch Pipeline
            demonstrateTextProcessingPipeline();
            
            // Erweitertes Beispiel: Zahlenverarbeitung durch Pipeline
            demonstrateNumberProcessingPipeline();
            
            LOGGER.info("Pipeline-Pattern Demonstration erfolgreich abgeschlossen");
        } catch (Exception e) {
            LOGGER.error("Fehler in der Pipeline-Demonstration: {}", e.getMessage(), e);
        }
    }

    /**
     * Demonstriert eine einfache Textverarbeitungs-Pipeline.
     */
    private static void demonstrateTextProcessingPipeline() throws PipelineException {
        LOGGER.info("Starte Textverarbeitungs-Pipeline Demonstration");
        
        // Erstelle eine Pipeline für Textverarbeitung
        Pipeline<String, String> textPipeline = new Pipeline<>("TextProcessingPipeline");
        
        // Füge Stages zur Pipeline hinzu
        textPipeline
            .addStage(new TextToUpperCaseStage())
            .addStage(new RemoveSpacesStage())
            .addStage(new AddPrefixStage("PROCESSED_"));
        
        // Eingabetext
        String inputText = "Dies ist ein Beispieltext für das Pipeline-Pattern";
        LOGGER.info("Eingabetext: {}", inputText);
        
        // Führe die Pipeline aus
        String result = textPipeline.execute(inputText);
        
        LOGGER.info("Ergebnis der Textverarbeitungs-Pipeline: {}", result);
    }

    /**
     * Demonstriert eine Zahlenverarbeitungs-Pipeline.
     */
    private static void demonstrateNumberProcessingPipeline() throws PipelineException {
        LOGGER.info("Starte Zahlenverarbeitungs-Pipeline Demonstration");
        
        // Erstelle eine Pipeline für Zahlenverarbeitung
        Pipeline<Integer, Double> numberPipeline = new Pipeline<>("NumberProcessingPipeline");
        
        // Füge Stages zur Pipeline hinzu
        numberPipeline
            .addStage(new MultiplyByTwoStage())
            .addStage(new AddTenStage())
            .addStage(new DivideByThreeStage());
        
        // Eingabezahl
        int inputNumber = 5;
        LOGGER.info("Eingabezahl: {}", inputNumber);
        
        // Führe die Pipeline aus
        Double result = numberPipeline.execute(inputNumber);
        
        LOGGER.info("Ergebnis der Zahlenverarbeitungs-Pipeline: {}", result);
    }

    /**
     * Pipeline-Stage, die Text in Großbuchstaben umwandelt.
     */
    private static class TextToUpperCaseStage implements PipelineStage<String, String> {
        @Override
        public String process(String input, PipelineContext context) {
            return input.toUpperCase();
        }

        @Override
        public String getStageName() {
            return "TextToUpperCaseStage";
        }
    }

    /**
     * Pipeline-Stage, die Leerzeichen aus einem Text entfernt.
     */
    private static class RemoveSpacesStage implements PipelineStage<String, String> {
        @Override
        public String process(String input, PipelineContext context) {
            return input.replaceAll("\\s+", "");
        }

        @Override
        public String getStageName() {
            return "RemoveSpacesStage";
        }
    }

    /**
     * Pipeline-Stage, die einem Text ein Präfix hinzufügt.
     */
    private static class AddPrefixStage implements PipelineStage<String, String> {
        private final String prefix;

        public AddPrefixStage(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public String process(String input, PipelineContext context) {
            return prefix + input;
        }

        @Override
        public String getStageName() {
            return "AddPrefixStage";
        }
    }

    /**
     * Pipeline-Stage, die eine Zahl mit 2 multipliziert.
     */
    private static class MultiplyByTwoStage implements PipelineStage<Integer, Integer> {
        @Override
        public Integer process(Integer input, PipelineContext context) {
            return input * 2;
        }

        @Override
        public String getStageName() {
            return "MultiplyByTwoStage";
        }
    }

    /**
     * Pipeline-Stage, die zu einer Zahl 10 addiert.
     */
    private static class AddTenStage implements PipelineStage<Integer, Integer> {
        @Override
        public Integer process(Integer input, PipelineContext context) {
            return input + 10;
        }

        @Override
        public String getStageName() {
            return "AddTenStage";
        }
    }

    /**
     * Pipeline-Stage, die eine Zahl durch 3 teilt.
     */
    private static class DivideByThreeStage implements PipelineStage<Integer, Double> {
        @Override
        public Double process(Integer input, PipelineContext context) {
            return input / 3.0;
        }

        @Override
        public String getStageName() {
            return "DivideByThreeStage";
        }
    }
}