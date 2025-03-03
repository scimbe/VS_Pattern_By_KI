package de.becke.vs.pattern.adapter.messaging;

import java.util.Date;

/**
 * Repräsentiert eine Nachricht im Messaging-System.
 */
public class Message {
    
    private String sender;
    private String recipient;
    private String content;
    private Date timestamp;
    private String messageId;
    private boolean read;
    
    /**
     * Erstellt eine neue Nachricht.
     */
    public Message() {
        this.timestamp = new Date();
        this.read = false;
    }
    
    /**
     * Erstellt eine neue Nachricht mit den angegebenen Parametern.
     * 
     * @param sender Der Absender
     * @param recipient Der Empfänger
     * @param content Der Inhalt
     * @param messageId Die Nachrichten-ID
     */
    public Message(String sender, String recipient, String content, String messageId) {
        this();
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.messageId = messageId;
    }
    
    /**
     * Gibt den Absender zurück.
     * 
     * @return Der Absender
     */
    public String getSender() {
        return sender;
    }
    
    /**
     * Setzt den Absender.
     * 
     * @param sender Der Absender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    /**
     * Gibt den Empfänger zurück.
     * 
     * @return Der Empfänger
     */
    public String getRecipient() {
        return recipient;
    }
    
    /**
     * Setzt den Empfänger.
     * 
     * @param recipient Der Empfänger
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    
    /**
     * Gibt den Inhalt zurück.
     * 
     * @return Der Inhalt
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Setzt den Inhalt.
     * 
     * @param content Der Inhalt
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * Gibt den Zeitstempel zurück.
     * 
     * @return Der Zeitstempel
     */
    public Date getTimestamp() {
        return timestamp;
    }
    
    /**
     * Setzt den Zeitstempel.
     * 
     * @param timestamp Der Zeitstempel
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Gibt die Nachrichten-ID zurück.
     * 
     * @return Die Nachrichten-ID
     */
    public String getMessageId() {
        return messageId;
    }
    
    /**
     * Setzt die Nachrichten-ID.
     * 
     * @param messageId Die Nachrichten-ID
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
    
    /**
     * Gibt zurück, ob die Nachricht gelesen wurde.
     * 
     * @return true, wenn die Nachricht gelesen wurde, sonst false
     */
    public boolean isRead() {
        return read;
    }
    
    /**
     * Setzt, ob die Nachricht gelesen wurde.
     * 
     * @param read true, wenn die Nachricht gelesen wurde, sonst false
     */
    public void setRead(boolean read) {
        this.read = read;
    }
    
    /**
     * Markiert die Nachricht als gelesen.
     */
    public void markAsRead() {
        this.read = true;
    }
    
    @Override
    public String toString() {
        return String.format("Message [id=%s, from=%s, to=%s, time=%s, read=%s, content=%s]", 
                messageId, sender, recipient, timestamp, read, content);
    }
}