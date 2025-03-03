package de.becke.vs.pattern.observer.distributed;

/**
 * Schnittstelle für die Behandlung von Ereignissen in einem verteilten Observer-Pattern.
 * 
 * Diese Schnittstelle entspricht der Observer-Rolle in einem verteilten Kontext und
 * wird von Klassen implementiert, die auf Ereignisse aus einem verteilten System reagieren sollen.
 */
@FunctionalInterface
public interface EventHandler {
    
    /**
     * Wird aufgerufen, wenn ein Ereignis auf einem bestimmten Topic empfangen wird.
     * 
     * @param topic Das Topic, auf dem das Ereignis empfangen wurde
     * @param content Der Inhalt des Ereignisses
     */
    void onEvent(String topic, String content);
}