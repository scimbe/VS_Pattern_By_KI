package de.becke.vs.pattern.adapter.messaging;

/**
 * Schnittstelle für einen Messaging-Service.
 * Dies ist die Zielschnittstelle, die vom Client verwendet wird.
 */
public interface MessagingService {
    
    /**
     * Sendet eine Nachricht an einen Empfänger.
     * 
     * @param recipient Der Empfänger
     * @param message Die Nachricht
     * @return true, wenn die Nachricht erfolgreich gesendet wurde, sonst false
     */
    boolean sendMessage(String recipient, String message);
    
    /**
     * Empfängt Nachrichten für einen bestimmten Empfänger.
     * 
     * @param recipient Der Empfänger
     * @return Die empfangenen Nachrichten
     */
    Message[] receiveMessages(String recipient);
    
    /**
     * Gibt den Typ des Messaging-Services zurück.
     * 
     * @return Der Typ des Services
     */
    String getServiceType();
}