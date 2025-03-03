package de.becke.vs.pattern.observer.basic;

/**
 * Die Observer-Schnittstelle definiert die Methode, die aufgerufen wird,
 * wenn ein Subject den Observer über Änderungen informiert.
 */
public interface Observer {
    
    /**
     * Wird aufgerufen, wenn sich der Zustand des beobachteten Subjects ändert.
     * 
     * @param subject Das Subject, das die Änderung ausgelöst hat
     * @param arg Optionales Argument mit zusätzlichen Informationen zur Änderung
     */
    void update(Subject subject, Object arg);
}
