package de.becke.vs.pattern.observer.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Eine konkrete Implementierung des Subject-Interfaces, die Daten verwaltet
 * und Observer über Änderungen informiert.
 */
public class DataSource implements Subject {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DataSource.class);
    
    // Liste der registrierten Observer
    private final List<Observer> observers = new ArrayList<>();
    
    // Daten, die verwaltet werden
    private String data;
    
    /**
     * Konstruktor mit initialen Daten.
     * 
     * @param initialData Die initialen Daten
     */
    public DataSource(String initialData) {
        this.data = initialData;
        LOGGER.info("DataSource initialisiert mit Daten: {}", initialData);
    }
    
    @Override
    public void addObserver(Observer observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            LOGGER.info("Observer hinzugefügt, jetzt {} Observer registriert", observers.size());
        }
    }
    
    @Override
    public void removeObserver(Observer observer) {
        if (observers.remove(observer)) {
            LOGGER.info("Observer entfernt, noch {} Observer registriert", observers.size());
        }
    }
    
    @Override
    public void notifyObservers() {
        notifyObservers(null);
    }
    
    @Override
    public void notifyObservers(Object arg) {
        LOGGER.info("Benachrichtige {} Observer über Änderungen", observers.size());
        
        // Erstelle eine Kopie der Observer-Liste, um Concurrent-Modification-Probleme zu vermeiden
        List<Observer> observersCopy = new ArrayList<>(observers);
        
        for (Observer observer : observersCopy) {
            try {
                observer.update(this, arg);
            } catch (Exception e) {
                LOGGER.error("Fehler beim Benachrichtigen eines Observers: {}", e.getMessage(), e);
                // Im Fehlerfall wird die Benachrichtigung der anderen Observer fortgesetzt
            }
        }
    }
    
    /**
     * Setzt die Daten und benachrichtigt alle Observer.
     * 
     * @param newData Die neuen Daten
     */
    public void setData(String newData) {
        LOGGER.info("Daten werden geändert von '{}' zu '{}'", this.data, newData);
        
        // Speichere die alten Daten, um sie als Argument an die Observer zu übergeben
        String oldData = this.data;
        
        // Aktualisiere die Daten
        this.data = newData;
        
        // Benachrichtige alle Observer mit den alten Daten als Argument
        notifyObservers(oldData);
    }
    
    /**
     * Gibt die aktuellen Daten zurück.
     * 
     * @return Die aktuellen Daten
     */
    public String getData() {
        return data;
    }
    
    /**
     * Simuliert eine Datenaktualisierung aus einer externen Quelle (z.B. Datenbank, Sensor).
     */
    public void simulateDataUpdate() {
        LOGGER.info("Simuliere Datenaktualisierung aus externer Quelle");
        setData("Aktualisierte Daten: " + System.currentTimeMillis());
    }
}