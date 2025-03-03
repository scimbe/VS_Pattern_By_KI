package de.becke.vs.pattern.proxy.common;

/**
 * Gemeinsame Schnittstelle für reale Services und ihre Proxies.
 * Diese Schnittstelle definiert den Vertrag, den alle Dienste und ihre
 * Stellvertreter erfüllen müssen.
 */
public interface RemoteService {
    
    /**
     * Führt eine einfache Anfrage ohne Parameter aus.
     * 
     * @return Das Ergebnis der Anfrage
     * @throws ServiceException Wenn bei der Ausführung der Anfrage ein Fehler auftritt
     */
    String request() throws ServiceException;
    
    /**
     * Führt eine Anfrage mit einem Parameter aus.
     * 
     * @param parameter Der Parameter für die Anfrage
     * @return Das Ergebnis der Anfrage
     * @throws ServiceException Wenn bei der Ausführung der Anfrage ein Fehler auftritt
     */
    String request(String parameter) throws ServiceException;
    
    /**
     * Führt eine komplexe Anfrage mit mehreren Parametern aus.
     * 
     * @param id Eine Identifikation für die Anfrage
     * @param data Die zu verarbeitenden Daten
     * @param options Zusätzliche Optionen für die Anfrage
     * @return Das Ergebnis der komplexen Anfrage
     * @throws ServiceException Wenn bei der Ausführung der Anfrage ein Fehler auftritt
     */
    String complexRequest(int id, String data, String[] options) throws ServiceException;
}