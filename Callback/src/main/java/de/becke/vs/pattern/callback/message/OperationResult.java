package de.becke.vs.pattern.callback.message;

import de.becke.vs.pattern.callback.common.OperationStatus;

/**
 * Stellt das Ergebnis einer asynchronen Operation dar.
 * 
 * @param <T> Der Typ des Ergebnisses.
 */
public class OperationResult<T> {
    
    private final String operationId;
    private final OperationStatus status;
    private final T result;
    private final Throwable error;
    
    /**
     * Erstellt ein neues OperationResult-Objekt.
     * 
     * @param operationId Die ID der Operation.
     * @param status Der Status der Operation.
     * @param result Das Ergebnis der Operation (null bei Fehler).
     * @param error Der aufgetretene Fehler (null bei Erfolg).
     */
    public OperationResult(String operationId, OperationStatus status, T result, Throwable error) {
        this.operationId = operationId;
        this.status = status;
        this.result = result;
        this.error = error;
    }
    
    /**
     * Gibt die ID der Operation zurück.
     * 
     * @return Die Operations-ID.
     */
    public String getOperationId() {
        return operationId;
    }
    
    /**
     * Gibt den Status der Operation zurück.
     * 
     * @return Der Status.
     */
    public OperationStatus getStatus() {
        return status;
    }
    
    /**
     * Gibt das Ergebnis der Operation zurück, wenn diese erfolgreich war.
     * 
     * @return Das Ergebnis oder null, wenn die Operation nicht erfolgreich war.
     */
    public T getResult() {
        return result;
    }
    
    /**
     * Gibt den aufgetretenen Fehler zurück, wenn die Operation fehlgeschlagen ist.
     * 
     * @return Der aufgetretene Fehler oder null, wenn die Operation nicht fehlgeschlagen ist.
     */
    public Throwable getError() {
        return error;
    }
    
    /**
     * Überprüft, ob die Operation erfolgreich abgeschlossen wurde.
     * 
     * @return true, wenn die Operation erfolgreich war, sonst false.
     */
    public boolean isSuccess() {
        return status == OperationStatus.COMPLETED;
    }
    
    /**
     * Überprüft, ob bei der Operation ein Fehler aufgetreten ist.
     * 
     * @return true, wenn bei der Operation ein Fehler aufgetreten ist, sonst false.
     */
    public boolean isError() {
        return status == OperationStatus.FAILED;
    }
    
    @Override
    public String toString() {
        if (isSuccess()) {
            return "OperationResult{" +
                    "operationId='" + operationId + '\'' +
                    ", status=" + status +
                    ", result=" + result +
                    '}';
        } else {
            return "OperationResult{" +
                    "operationId='" + operationId + '\'' +
                    ", status=" + status +
                    ", error=" + (error != null ? error.getMessage() : "null") +
                    '}';
        }
    }
}