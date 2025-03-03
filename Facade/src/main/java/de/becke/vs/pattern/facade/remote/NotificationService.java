package de.becke.vs.pattern.facade.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stellt eine vereinfachte Implementierung eines Benachrichtigungsdienstes dar.
 * 
 * In einer realen Anwendung würde diese Klasse Remote-Aufrufe an einen
 * Benachrichtigungsdienst durchführen, der E-Mails, SMS usw. versendet.
 */
public class NotificationService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
    
    private final String serviceUrl;
    
    /**
     * Erstellt einen neuen NotificationService mit dem angegebenen Service-Endpunkt.
     * 
     * @param serviceUrl Die URL des Benachrichtigungsdienstes
     */
    public NotificationService(String serviceUrl) {
        this.serviceUrl = serviceUrl;
        LOGGER.debug("NotificationService initialisiert mit URL: {}", serviceUrl);
    }
    
    /**
     * Sendet eine Bestellbestätigung an einen Benutzer.
     * 
     * @param userId Die Benutzer-ID
     * @param orderId Die Bestellnummer
     * @return true, wenn die Benachrichtigung erfolgreich gesendet wurde, sonst false
     */
    public boolean sendOrderConfirmation(String userId, String orderId) {
        LOGGER.info("Sende Bestellbestätigung an Benutzer: {} für Bestellung: {}", userId, orderId);
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(100); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Parameter
            if (userId == null || userId.isEmpty() || orderId == null || orderId.isEmpty()) {
                LOGGER.warn("Ungültige Parameter für Bestellbestätigung: userId={}, orderId={}", 
                        userId, orderId);
                return false;
            }
            
            // Simuliere eine erfolgreiche Benachrichtigung
            LOGGER.info("Bestellbestätigung erfolgreich gesendet");
            return true;
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler beim Senden der Bestellbestätigung: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Sendet eine Bestellstornierung an einen Benutzer.
     * 
     * @param userId Die Benutzer-ID
     * @param orderId Die Bestellnummer
     * @return true, wenn die Benachrichtigung erfolgreich gesendet wurde, sonst false
     */
    public boolean sendOrderCancellation(String userId, String orderId) {
        LOGGER.info("Sende Bestellstornierung an Benutzer: {} für Bestellung: {}", userId, orderId);
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(100); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Parameter
            if (userId == null || userId.isEmpty() || orderId == null || orderId.isEmpty()) {
                LOGGER.warn("Ungültige Parameter für Bestellstornierung: userId={}, orderId={}", 
                        userId, orderId);
                return false;
            }
            
            // Simuliere eine erfolgreiche Benachrichtigung
            LOGGER.info("Bestellstornierung erfolgreich gesendet");
            return true;
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler beim Senden der Bestellstornierung: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    /**
     * Sendet eine allgemeine Benachrichtigung an einen Benutzer.
     * 
     * @param userId Die Benutzer-ID
     * @param subject Der Betreff der Benachrichtigung
     * @param message Der Inhalt der Benachrichtigung
     * @return true, wenn die Benachrichtigung erfolgreich gesendet wurde, sonst false
     */
    public boolean sendNotification(String userId, String subject, String message) {
        LOGGER.info("Sende Benachrichtigung an Benutzer: {} mit Betreff: {}", userId, subject);
        
        // Simuliere einen Remote-Aufruf
        try {
            Thread.sleep(80); // Simuliere Netzwerklatenz
            
            // Prüfe auf ungültige Parameter
            if (userId == null || userId.isEmpty() || 
                    subject == null || subject.isEmpty() || 
                    message == null || message.isEmpty()) {
                LOGGER.warn("Ungültige Parameter für Benachrichtigung");
                return false;
            }
            
            // Simuliere eine erfolgreiche Benachrichtigung
            LOGGER.info("Benachrichtigung erfolgreich gesendet");
            return true;
            
        } catch (InterruptedException e) {
            LOGGER.error("Fehler beim Senden der Benachrichtigung: {}", e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        }
    }
}