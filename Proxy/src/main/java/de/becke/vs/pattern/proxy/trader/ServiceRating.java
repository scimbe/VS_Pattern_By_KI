package de.becke.vs.pattern.proxy.trader;

import java.time.LocalDateTime;

/**
 * Stellt eine Bewertung für einen Dienst durch einen Client dar.
 * 
 * Diese Klasse speichert Informationen über die Bewertung eines Dienstes,
 * einschließlich der Bewertung selbst, eines Kommentars und des Zeitpunkts.
 */
public class ServiceRating {
    
    private final String clientId;
    private final double rating;
    private final String comment;
    private final LocalDateTime timestamp;
    
    /**
     * Erstellt eine neue ServiceRating-Instanz.
     * 
     * @param clientId Die ID des Clients, der die Bewertung abgegeben hat
     * @param rating Die Bewertung (0.0 bis 5.0)
     * @param comment Ein optionaler Kommentar
     */
    public ServiceRating(String clientId, double rating, String comment) {
        this.clientId = clientId;
        this.rating = clampRating(rating);
        this.comment = comment;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Gibt die ID des Clients zurück.
     * 
     * @return Die Client-ID
     */
    public String getClientId() {
        return clientId;
    }
    
    /**
     * Gibt die Bewertung zurück.
     * 
     * @return Die Bewertung (0.0 bis 5.0)
     */
    public double getRating() {
        return rating;
    }
    
    /**
     * Gibt den Kommentar zurück.
     * 
     * @return Der Kommentar
     */
    public String getComment() {
        return comment;
    }
    
    /**
     * Gibt den Zeitpunkt der Bewertung zurück.
     * 
     * @return Der Zeitpunkt
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Begrenzt die Bewertung auf einen Wert zwischen 0.0 und 5.0.
     * 
     * @param rating Die zu begrenzende Bewertung
     * @return Die begrenzte Bewertung
     */
    private double clampRating(double rating) {
        return Math.max(0.0, Math.min(5.0, rating));
    }
    
    @Override
    public String toString() {
        return String.format("Bewertung von %s: %.1f/5.0 - %s (%s)", 
                clientId, rating, comment, timestamp);
    }
}