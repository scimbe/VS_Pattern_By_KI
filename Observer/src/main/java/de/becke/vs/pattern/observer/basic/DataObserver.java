package de.becke.vs.pattern.observer.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Eine konkrete Implementierung des Observer-Interfaces, die auf Änderungen
 * in einem DataSource-Objekt reagiert.
 */
public class DataObserver implements Observer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DataObserver.class);
    
    // Name des Observers für Identifikation in den Logs
    private final String name;
    
    // Zuletzt empfangene Daten
    private String lastData;
    
    /**
     * Konstruktor mit Beobachternamen.
     * 
     * @param name Der Name des Observers
     */
    public DataObserver(String name) {
        this.name = name;
        LOGGER.info("DataObserver '{}' erstellt", name);
    }
    
    @Override
    public void update(Subject subject, Object arg) {
        if (subject instanceof DataSource) {
            DataSource dataSource = (DataSource) subject;
            lastData = dataSource.getData();
            
            if (arg != null) {
                LOGGER.info("Observer '{}' benachrichtigt: Daten geändert von '{}' zu '{}'", 
                        name, arg, lastData);
            } else {
                LOGGER.info("Observer '{}' benachrichtigt: Neue Daten '{}'", 
                        name, lastData);
            }
            
            // Hier könnte in einer realen Anwendung eine komplexere Verarbeitung der Daten stattfinden
            processData(lastData);
        }
    }
    
    /**
     * Simuliert eine Verarbeitung der erhaltenen Daten.
     * 
     * @param data Die zu verarbeitenden Daten
     */
    private void processData(String data) {
        LOGGER.info("Observer '{}' verarbeitet Daten: '{}'", name, data);
        
        // Simuliert eine Verarbeitungszeit
        try {
            Thread.sleep(50);
            LOGGER.info("Observer '{}': Datenverarbeitung abgeschlossen", name);
        } catch (InterruptedException e) {
            LOGGER.error("Verarbeitung unterbrochen", e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Gibt die zuletzt empfangenen Daten zurück.
     * 
     * @return Die zuletzt empfangenen Daten
     */
    public String getLastData() {
        return lastData;
    }
    
    /**
     * Gibt den Namen des Observers zurück.
     * 
     * @return Der Name des Observers
     */
    public String getName() {
        return name;
    }
}