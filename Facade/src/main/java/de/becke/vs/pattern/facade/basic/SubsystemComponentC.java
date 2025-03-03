package de.becke.vs.pattern.facade.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eine Komponente des Subsystems, die für die Verarbeitung von Daten zuständig ist.
 */
public class SubsystemComponentC {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SubsystemComponentC.class);
    
    /**
     * Erstellt eine neue SubsystemComponentC-Instanz.
     */
    public SubsystemComponentC() {
        LOGGER.debug("SubsystemComponentC initialisiert");
    }
    
    /**
     * Verarbeitet die Daten basierend auf den Analyseergebnissen.
     * 
     * @param preprocessedData Die vorverarbeiteten Daten
     * @param analysisResult Das Ergebnis der Analyse
     * @return Das Ergebnis der Verarbeitung
     */
    public String process(String preprocessedData, AnalysisResult analysisResult) {
        LOGGER.debug("Verarbeite Daten: {} mit Analyseergebnis: {}", 
                preprocessedData, analysisResult);
        
        // Verschiedene Verarbeitungslogik basierend auf der Kategorie
        String category = analysisResult.getCategory();
        switch (category) {
            case "REQUEST":
                return handleRequest(preprocessedData, analysisResult.getPriority());
            case "ORDER":
                return handleOrder(preprocessedData, analysisResult.getPriority());
            case "ISSUE":
                return handleIssue(preprocessedData, analysisResult.getPriority());
            default:
                return handleGeneric(preprocessedData, analysisResult.getPriority());
        }
    }
    
    /**
     * Verarbeitet die validierten Daten mit höherer Sorgfalt.
     * 
     * @param validatedData Die validierten Daten
     * @param analysisResult Das Ergebnis der Analyse
     * @return Das Ergebnis der Verarbeitung
     */
    public String processWithValidation(String validatedData, AnalysisResult analysisResult) {
        LOGGER.debug("Verarbeite validierte Daten: {} mit Analyseergebnis: {}", 
                validatedData, analysisResult);
        
        // Zusätzliche Sicherheitsmaßnahmen für validierte Daten
        String result = "VALIDIERT: " + process(validatedData, analysisResult);
        
        // Protokolliere die Verarbeitung von validierten Daten für Audit-Zwecke
        LOGGER.info("Validierte Daten verarbeitet mit Konfidenz: {}", 
                analysisResult.getConfidence());
        
        return result;
    }
    
    /**
     * Führt eine schnelle, vereinfachte Verarbeitung ohne umfangreiche Analyse durch.
     * 
     * @param preprocessedData Die vorverarbeiteten Daten
     * @return Das Ergebnis der Verarbeitung
     */
    public String quickProcess(String preprocessedData) {
        LOGGER.debug("Schnelle Verarbeitung: {}", preprocessedData);
        
        // Einfache Verarbeitung für schnellen Durchsatz
        return "Schnell verarbeitet: " + preprocessedData;
    }
    
    /**
     * Behandelt eine Anfrage basierend auf ihrer Priorität.
     * 
     * @param data Die Anfragedaten
     * @param priority Die Priorität der Anfrage
     * @return Das Ergebnis der Behandlung
     */
    private String handleRequest(String data, int priority) {
        LOGGER.debug("Behandle Anfrage mit Priorität {}: {}", priority, data);
        
        // Simuliere unterschiedliche Verarbeitung basierend auf Priorität
        if (priority <= 2) {
            return "DRINGENDE ANFRAGE BEARBEITET: " + data;
        } else {
            return "STANDARDANFRAGE BEARBEITET: " + data;
        }
    }
    
    /**
     * Behandelt eine Bestellung basierend auf ihrer Priorität.
     * 
     * @param data Die Bestelldaten
     * @param priority Die Priorität der Bestellung
     * @return Das Ergebnis der Behandlung
     */
    private String handleOrder(String data, int priority) {
        LOGGER.debug("Behandle Bestellung mit Priorität {}: {}", priority, data);
        
        // Simuliere unterschiedliche Verarbeitung basierend auf Priorität
        if (priority <= 2) {
            return "EXPRESS-BESTELLUNG AUFGEGEBEN: " + data;
        } else {
            return "STANDARD-BESTELLUNG AUFGEGEBEN: " + data;
        }
    }
    
    /**
     * Behandelt ein Problem basierend auf seiner Priorität.
     * 
     * @param data Die Problemdaten
     * @param priority Die Priorität des Problems
     * @return Das Ergebnis der Behandlung
     */
    private String handleIssue(String data, int priority) {
        LOGGER.debug("Behandle Problem mit Priorität {}: {}", priority, data);
        
        // Simuliere unterschiedliche Verarbeitung basierend auf Priorität
        if (priority <= 2) {
            return "KRITISCHES PROBLEM ESKALIERT: " + data;
        } else if (priority <= 3) {
            return "WICHTIGES PROBLEM GEMELDET: " + data;
        } else {
            return "PROBLEM PROTOKOLLIERT: " + data;
        }
    }
    
    /**
     * Behandelt generische Daten basierend auf ihrer Priorität.
     * 
     * @param data Die Daten
     * @param priority Die Priorität
     * @return Das Ergebnis der Behandlung
     */
    private String handleGeneric(String data, int priority) {
        LOGGER.debug("Behandle generische Daten mit Priorität {}: {}", priority, data);
        
        // Generische Verarbeitung
        return "DATEN VERARBEITET: " + data;
    }
}