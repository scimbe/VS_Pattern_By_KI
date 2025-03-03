package de.becke.vs.pattern.facade.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eine Komponente des Subsystems, die für die Vorverarbeitung von Daten zuständig ist.
 */
public class SubsystemComponentA {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SubsystemComponentA.class);
    
    /**
     * Erstellt eine neue SubsystemComponentA-Instanz.
     */
    public SubsystemComponentA() {
        LOGGER.debug("SubsystemComponentA initialisiert");
    }
    
    /**
     * Vorverarbeitet die Eingabedaten für die weitere Verarbeitung.
     * 
     * @param input Die Eingabedaten
     * @return Die vorverarbeiteten Daten
     */
    public String preprocess(String input) {
        LOGGER.debug("Vorverarbeite Daten: {}", input);
        
        // Führe komplexe Vorverarbeitung durch
        // In einem realen System könnte dies Filterung, Normalisierung, 
        // Formatumwandlung usw. umfassen
        String preprocessed = input.trim().toLowerCase();
        
        // Simuliere eine komplexe Verarbeitung
        if (preprocessed.contains("fehler")) {
            LOGGER.warn("Fehler in den Eingabedaten erkannt: {}", input);
            preprocessed = preprocessed.replace("fehler", "korrigiert");
        }
        
        return preprocessed;
    }
    
    /**
     * Führt eine schnelle, vereinfachte Vorverarbeitung durch.
     * 
     * @param input Die Eingabedaten
     * @return Die vorverarbeiteten Daten
     */
    public String quickPreprocess(String input) {
        LOGGER.debug("Schnelle Vorverarbeitung: {}", input);
        
        // Minimale Vorverarbeitung für schnellen Durchsatz
        return input.trim();
    }
    
    /**
     * Validiert die vorverarbeiteten Daten für zusätzliche Sicherheit.
     * 
     * @param preprocessedData Die vorverarbeiteten Daten
     * @return Die validierten Daten
     */
    public String validate(String preprocessedData) {
        LOGGER.debug("Validiere Daten: {}", preprocessedData);
        
        // Überprüft auf ungültige Zeichen oder Muster
        if (preprocessedData.matches(".*[<>\"'].*")) {
            LOGGER.warn("Potenziell gefährliche Zeichen gefunden, werden entfernt");
            return preprocessedData.replaceAll("[<>\"']", "");
        }
        
        if (preprocessedData.isEmpty()) {
            LOGGER.warn("Leere Daten nach Vorverarbeitung, verwende Standardwert");
            return "leer";
        }
        
        return preprocessedData;
    }
}