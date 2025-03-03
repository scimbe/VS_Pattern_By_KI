package de.becke.vs.pattern.callback.common;

/**
 * Eine generische Callback-Schnittstelle, die den Ergebnistyp parametrisiert.
 * 
 * @param <T> Der Typ des Ergebnisses, das bei erfolgreicher Operation zurückgegeben wird.
 */
public interface Callback<T> {
    
    /**
     * Wird aufgerufen, wenn die asynchrone Operation erfolgreich abgeschlossen wurde.
     * 
     * @param result Das Ergebnis der Operation.
     */
    void onSuccess(T result);
    
    /**
     * Wird aufgerufen, wenn bei der asynchronen Operation ein Fehler aufgetreten ist.
     * 
     * @param exception Die aufgetretene Exception.
     */
    void onError(Throwable exception);
}