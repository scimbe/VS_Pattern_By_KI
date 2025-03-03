package de.becke.vs.pattern.observer.basic;

/**
 * Die Subject-Schnittstelle definiert Methoden zur Verwaltung von Observern und
 * zum Benachrichtigen dieser über Zustandsänderungen.
 */
public interface Subject {
    
    /**
     * Registriert einen Observer beim Subject.
     * 
     * @param observer Der Observer, der registriert werden soll
     */
    void addObserver(Observer observer);
    
    /**
     * Entfernt einen Observer vom Subject.
     * 
     * @param observer Der Observer, der entfernt werden soll
     */
    void removeObserver(Observer observer);
    
    /**
     * Benachrichtigt alle registrierten Observer ohne zusätzliche Informationen.
     */
    void notifyObservers();
    
    /**
     * Benachrichtigt alle registrierten Observer mit zusätzlichen Informationen.
     * 
     * @param arg Ein Argument mit zusätzlichen Informationen über die Änderung
     */
    void notifyObservers(Object arg);
}
