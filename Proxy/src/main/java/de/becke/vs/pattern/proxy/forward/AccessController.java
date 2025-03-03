package de.becke.vs.pattern.proxy.forward;

/**
 * Schnittstelle für die Zugangskontrolle im Forward-Proxy.
 * 
 * Diese Schnittstelle definiert Methoden zur Überprüfung von Zugriffsrechten
 * für verschiedene Arten von Anfragen an einen Remote-Dienst.
 */
public interface AccessController {
    
    /**
     * Überprüft, ob eine einfache Anfrage ohne Parameter zugelassen ist.
     * 
     * @return true, wenn der Zugriff erlaubt ist, sonst false
     */
    boolean checkAccess();
    
    /**
     * Überprüft, ob eine Anfrage mit dem angegebenen Parameter zugelassen ist.
     * 
     * @param parameter Der zu überprüfende Parameter
     * @return true, wenn der Zugriff erlaubt ist, sonst false
     */
    boolean checkAccess(String parameter);
    
    /**
     * Überprüft, ob eine komplexe Anfrage mit den angegebenen Parametern zugelassen ist.
     * 
     * @param id Die ID der Anfrage
     * @param data Die Daten der Anfrage
     * @param options Die Optionen der Anfrage
     * @return true, wenn der Zugriff erlaubt ist, sonst false
     */
    boolean checkComplexAccess(int id, String data, String[] options);
}