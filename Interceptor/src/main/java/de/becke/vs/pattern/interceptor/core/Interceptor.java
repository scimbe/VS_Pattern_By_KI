package de.becke.vs.pattern.interceptor.core;

/**
 * Die Basis-Schnittstelle für alle Interceptoren im System.
 * 
 * Interceptoren können bestimmte Operationen vor und nach einem Prozessschritt ausführen,
 * ohne den eigentlichen Prozessfluss zu verändern. Sie ermöglichen das Hinzufügen von
 * querschnittlichen Belangen wie Protokollierung, Sicherheit oder Überwachung.
 */
public interface Interceptor {
    
    /**
     * Wird vor der Ausführung der eigentlichen Operation aufgerufen.
     * 
     * @param context Der Kontext, der die relevanten Informationen für die Ausführung enthält
     * @return true, wenn die Operation fortgesetzt werden soll, false um abzubrechen
     */
    boolean preProcess(Context context);
    
    /**
     * Wird nach der Ausführung der eigentlichen Operation aufgerufen.
     * 
     * @param context Der Kontext, der die relevanten Informationen und das Ergebnis enthält
     */
    void postProcess(Context context);
    
    /**
     * Wird bei einem Fehler während der Ausführung der eigentlichen Operation aufgerufen.
     * 
     * @param context Der Kontext zum Zeitpunkt des Fehlers
     * @param exception Die aufgetretene Exception
     * @return true, wenn der Fehler als behandelt betrachtet werden soll, false sonst
     */
    boolean handleException(Context context, Exception exception);
}