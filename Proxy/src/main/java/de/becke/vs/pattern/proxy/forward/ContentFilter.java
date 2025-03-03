package de.becke.vs.pattern.proxy.forward;

/**
 * Schnittstelle für die Inhaltsfilterung im Forward-Proxy.
 * 
 * Diese Schnittstelle definiert Methoden zur Filterung von Ein- und Ausgabeinhalten,
 * die durch den Proxy übertragen werden.
 */
public interface ContentFilter {
    
    /**
     * Filtert den übergebenen Inhalt nach bestimmten Regeln.
     * 
     * @param content Der zu filternde Inhalt
     * @return Der gefilterte Inhalt
     */
    String filterContent(String content);
    
    /**
     * Überprüft, ob der Inhalt die Filterregeln erfüllt.
     * 
     * @param content Der zu überprüfende Inhalt
     * @return true, wenn der Inhalt die Regeln erfüllt, sonst false
     */
    boolean isContentAllowed(String content);
}