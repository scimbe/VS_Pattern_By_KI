package de.becke.vs.pattern.observer.basic;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testklasse für das grundlegende Observer-Pattern.
 */
public class BasicObserverTest {
    
    /**
     * Testet, ob Observer korrekt benachrichtigt werden, wenn sich die Daten im Subject ändern.
     */
    @Test
    public void testObserverNotification() {
        // Erstelle ein DataSource-Objekt als Subject
        DataSource dataSource = new DataSource("Testdaten");
        
        // Erstelle Observer
        DataObserver observer1 = new DataObserver("TestObserver1");
        DataObserver observer2 = new DataObserver("TestObserver2");
        
        // Registriere Observer beim Subject
        dataSource.addObserver(observer1);
        dataSource.addObserver(observer2);
        
        // Ändere Daten
        String newData = "Aktualisierte Testdaten";
        dataSource.setData(newData);
        
        // Überprüfe, ob die Observer die aktualisierten Daten erhalten haben
        assertEquals("Observer1 sollte die aktualisierten Daten erhalten", 
                newData, observer1.getLastData());
        assertEquals("Observer2 sollte die aktualisierten Daten erhalten", 
                newData, observer2.getLastData());
    }
    
    /**
     * Testet, ob Observer, die nach einer Datenänderung hinzugefügt werden,
     * bei der nächsten Änderung korrekt benachrichtigt werden.
     */
    @Test
    public void testLateRegistration() {
        // Erstelle ein DataSource-Objekt als Subject
        DataSource dataSource = new DataSource("Startdaten");
        
        // Erstelle und registriere ersten Observer
        DataObserver observer1 = new DataObserver("EarlyObserver");
        dataSource.addObserver(observer1);
        
        // Ändere Daten
        dataSource.setData("Erste Aktualisierung");
        
        // Erstelle und registriere zweiten Observer nach der ersten Änderung
        DataObserver observer2 = new DataObserver("LateObserver");
        dataSource.addObserver(observer2);
        
        // Überprüfe, ob der späte Observer noch keine Daten hat
        assertEquals("Erste Aktualisierung", observer1.getLastData());
        assertNull("Später Observer sollte noch keine Daten haben", observer2.getLastData());
        
        // Ändere Daten erneut
        String finalData = "Zweite Aktualisierung";
        dataSource.setData(finalData);
        
        // Überprüfe, ob beide Observer die neuesten Daten haben
        assertEquals("Früher Observer sollte die letzten Daten haben", 
                finalData, observer1.getLastData());
        assertEquals("Später Observer sollte jetzt auch die Daten haben", 
                finalData, observer2.getLastData());
    }
    
    /**
     * Testet, ob Observer nach dem Entfernen keine Benachrichtigungen mehr erhalten.
     */
    @Test
    public void testRemoveObserver() {
        // Erstelle ein DataSource-Objekt als Subject
        DataSource dataSource = new DataSource("Initialdaten");
        
        // Erstelle Observer
        DataObserver observer = new DataObserver("RemovableObserver");
        
        // Registriere Observer
        dataSource.addObserver(observer);
        
        // Ändere Daten
        dataSource.setData("Erste Änderung");
        assertEquals("Observer sollte die erste Änderung erhalten", 
                "Erste Änderung", observer.getLastData());
        
        // Entferne Observer
        dataSource.removeObserver(observer);
        
        // Ändere Daten erneut
        dataSource.setData("Zweite Änderung");
        
        // Observer sollte immer noch die alten Daten haben
        assertEquals("Observer sollte keine neue Benachrichtigung erhalten haben", 
                "Erste Änderung", observer.getLastData());
    }
}