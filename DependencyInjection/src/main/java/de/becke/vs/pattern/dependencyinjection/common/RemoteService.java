package de.becke.vs.pattern.dependencyinjection.common;

/**
 * Gemeinsame Schnittstelle für Remote-Services.
 * In realen verteilten Systemen würde dieser Service
 * Remote-Operationen auf einem entfernten System ausführen.
 */
public interface RemoteService {
    
    /**
     * Führt eine Standardoperation aus.
     * 
     * @return Das Ergebnis der Operation
     */
    String executeOperation();
    
    /**
     * Führt eine Operation mit einem Parameter aus.
     * 
     * @param parameter Der Eingabeparameter
     * @return Das Ergebnis der Operation
     */
    String executeOperation(String parameter);
    
    /**
     * Gibt den Namen des Services zurück.
     * 
     * @return Der Name des Services
     */
    String getServiceName();
}