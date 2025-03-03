package de.becke.vs.pattern.facade.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eine Komponente des Subsystems, die für die Analyse von Daten zuständig ist.
 */
public class SubsystemComponentB {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SubsystemComponentB.class);
    
    // Statistik über die analysierten Daten
    private int processedCount = 0;
    
    /**
     * Erstellt eine neue SubsystemComponentB-Instanz.
     */
    public SubsystemComponentB() {
        LOGGER.debug("SubsystemComponentB initialisiert");
    }
    
    /**
     * Analysiert die vorverarbeiteten Daten.
     * 
     * @param preprocessedData Die vorverarbeiteten Daten
     * @return Das Ergebnis der Analyse
     */
    public AnalysisResult analyze(String preprocessedData) {
        LOGGER.debug("Analysiere Daten: {}", preprocessedData);
        processedCount++;
        
        // Simuliere eine komplexe Analyse
        double confidence = calculateConfidence(preprocessedData);
        String category = categorize(preprocessedData);
        int priority = determinePriority(preprocessedData, confidence);
        
        AnalysisResult result = new AnalysisResult(confidence, category, priority);
        LOGGER.debug("Analyseergebnis: {}", result);
        
        return result;
    }
    
    /**
     * Zeichnet Eingabedaten für Statistikzwecke auf, ohne eine vollständige Analyse durchzuführen.
     * 
     * @param data Die aufzuzeichnenden Daten
     */
    public void recordInput(String data) {
        LOGGER.debug("Zeichne Eingabedaten auf: {}", data);
        processedCount++;
    }
    
    /**
     * Gibt die Anzahl der bisher verarbeiteten Datensätze zurück.
     * 
     * @return Die Anzahl der verarbeiteten Datensätze
     */
    public int getProcessedCount() {
        return processedCount;
    }
    
    /**
     * Berechnet die Konfidenz für die Daten basierend auf verschiedenen Faktoren.
     * 
     * @param data Die zu analysierenden Daten
     * @return Ein Konfidenzwert zwischen 0.0 und 1.0
     */
    private double calculateConfidence(String data) {
        // Simuliere eine Konfidenzberechnung
        // In einem realen System könnte dies auf ML-Modellen, Heuristiken usw. basieren
        
        if (data == null || data.isEmpty()) {
            return 0.0;
        }
        
        double baseConfidence = 0.5;
        
        // Einfache Heuristiken für die Demonstration
        if (data.length() > 10) {
            baseConfidence += 0.2;
        }
        
        if (data.matches(".*\\d.*")) {
            baseConfidence += 0.1;
        }
        
        if (data.contains("korrigiert")) {
            baseConfidence -= 0.15;
        }
        
        // Begrenze Konfidenz auf 0.0-1.0
        return Math.max(0.0, Math.min(1.0, baseConfidence));
    }
    
    /**
     * Kategorisiert die Daten basierend auf ihrem Inhalt.
     * 
     * @param data Die zu kategorisierenden Daten
     * @return Die Kategorie der Daten
     */
    private String categorize(String data) {
        // Einfache Kategorisierung für die Demonstration
        if (data.contains("anfrage")) {
            return "REQUEST";
        } else if (data.contains("bestellung")) {
            return "ORDER";
        } else if (data.contains("problem")) {
            return "ISSUE";
        } else {
            return "OTHER";
        }
    }
    
    /**
     * Bestimmt die Priorität basierend auf Daten und Konfidenz.
     * 
     * @param data Die Daten
     * @param confidence Die Konfidenz
     * @return Ein Prioritätswert (1-5, wobei 1 die höchste Priorität ist)
     */
    private int determinePriority(String data, double confidence) {
        // Einfache Prioritätsbestimmung für die Demonstration
        if (data.contains("dringend") || data.contains("sofort")) {
            return 1;
        } else if (confidence < 0.3) {
            return 2; // Niedrige Konfidenz erfordert mehr Aufmerksamkeit
        } else if (data.contains("wichtig")) {
            return 3;
        } else {
            return 4;
        }
    }
}