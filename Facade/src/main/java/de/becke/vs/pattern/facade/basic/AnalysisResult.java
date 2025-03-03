package de.becke.vs.pattern.facade.basic;

/**
 * Eine Klasse, die das Ergebnis einer Datenanalyse darstellt.
 */
public class AnalysisResult {
    
    private final double confidence;
    private final String category;
    private final int priority;
    
    /**
     * Erstellt ein neues AnalysisResult mit den angegebenen Werten.
     * 
     * @param confidence Der Konfidenzwert (0.0-1.0)
     * @param category Die Kategorie der Daten
     * @param priority Die Priorität (1-5, wobei 1 die höchste Priorität ist)
     */
    public AnalysisResult(double confidence, String category, int priority) {
        this.confidence = confidence;
        this.category = category;
        this.priority = priority;
    }
    
    /**
     * Gibt den Konfidenzwert der Analyse zurück.
     * 
     * @return Der Konfidenzwert zwischen 0.0 und 1.0
     */
    public double getConfidence() {
        return confidence;
    }
    
    /**
     * Gibt die bestimmte Kategorie der Daten zurück.
     * 
     * @return Die Kategorie
     */
    public String getCategory() {
        return category;
    }
    
    /**
     * Gibt die Priorität der Daten zurück.
     * 
     * @return Die Priorität (1-5, wobei 1 die höchste Priorität ist)
     */
    public int getPriority() {
        return priority;
    }
    
    @Override
    public String toString() {
        return "AnalysisResult{" +
                "confidence=" + confidence +
                ", category='" + category + '\'' +
                ", priority=" + priority +
                '}';
    }
}