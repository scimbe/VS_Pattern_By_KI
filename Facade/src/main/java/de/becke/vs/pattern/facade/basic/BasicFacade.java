package de.becke.vs.pattern.facade.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eine einfache Implementierung des Facade-Patterns.
 * 
 * Diese Klasse stellt eine vereinfachte Schnittstelle zu einem Subsystem
 * aus mehreren komplexen Komponenten bereit.
 */
public class BasicFacade {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicFacade.class);
    
    private final SubsystemComponentA componentA;
    private final SubsystemComponentB componentB;
    private final SubsystemComponentC componentC;
    
    /**
     * Erstellt eine neue Facade mit den erforderlichen Subsystem-Komponenten.
     */
    public BasicFacade() {
        this.componentA = new SubsystemComponentA();
        this.componentB = new SubsystemComponentB();
        this.componentC = new SubsystemComponentC();
        LOGGER.info("BasicFacade initialisiert mit allen Subsystem-Komponenten");
    }
    
    /**
     * Erstellt eine neue Facade mit benutzerdefinierten Subsystem-Komponenten.
     * 
     * @param componentA Komponente A
     * @param componentB Komponente B
     * @param componentC Komponente C
     */
    public BasicFacade(SubsystemComponentA componentA, SubsystemComponentB componentB, 
                      SubsystemComponentC componentC) {
        this.componentA = componentA;
        this.componentB = componentB;
        this.componentC = componentC;
        LOGGER.info("BasicFacade initialisiert mit benutzerdefinierten Subsystem-Komponenten");
    }
    
    /**
     * Führt eine komplexe Operation aus, die mehrere Subsystem-Komponenten involviert.
     * 
     * @param input Die Eingabedaten für die Operation
     * @return Das Ergebnis der Operation
     */
    public String processOperation(String input) {
        LOGGER.info("Verarbeite Operation mit Eingabe: {}", input);
        
        // Schritt 1: Komponente A vorverarbeitet die Eingabe
        String preprocessedData = componentA.preprocess(input);
        LOGGER.debug("Vorverarbeitete Daten: {}", preprocessedData);
        
        // Schritt 2: Komponente B analysiert die Daten
        AnalysisResult analysisResult = componentB.analyze(preprocessedData);
        LOGGER.debug("Analyseergebnis: {}", analysisResult);
        
        // Schritt 3: Basierend auf der Analyse werden unterschiedliche Aktionen ausgeführt
        String result;
        if (analysisResult.getConfidence() > 0.8) {
            // Hohe Konfidenz: direkte Verarbeitung
            result = componentC.process(preprocessedData, analysisResult);
        } else {
            // Niedrige Konfidenz: zusätzliche Validierung
            String validatedData = componentA.validate(preprocessedData);
            result = componentC.processWithValidation(validatedData, analysisResult);
        }
        
        LOGGER.info("Operation abgeschlossen mit Ergebnis: {}", result);
        return result;
    }
    
    /**
     * Führt eine vereinfachte Operation aus.
     * 
     * @param input Die Eingabedaten für die Operation
     * @return Das Ergebnis der Operation
     */
    public String simpleProcess(String input) {
        LOGGER.info("Führe vereinfachte Verarbeitung aus mit Eingabe: {}", input);
        
        // Direkte Verarbeitung ohne komplexe Analyse
        String preprocessedData = componentA.quickPreprocess(input);
        componentB.recordInput(preprocessedData);
        String result = componentC.quickProcess(preprocessedData);
        
        LOGGER.info("Vereinfachte Verarbeitung abgeschlossen mit Ergebnis: {}", result);
        return result;
    }
    
    /**
     * Führt eine Batch-Verarbeitung für mehrere Eingaben aus.
     * 
     * @param inputs Die zu verarbeitenden Eingaben
     * @return Die Ergebnisse der Verarbeitung
     */
    public String[] batchProcess(String[] inputs) {
        LOGGER.info("Starte Batch-Verarbeitung für {} Eingaben", inputs.length);
        
        String[] results = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            results[i] = processOperation(inputs[i]);
        }
        
        LOGGER.info("Batch-Verarbeitung abgeschlossen");
        return results;
    }
}