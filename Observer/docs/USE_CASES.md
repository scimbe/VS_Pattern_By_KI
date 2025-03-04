# Anwendungsbeispiele des Observer-Patterns in verteilten Systemen

Dieses Dokument stellt reale Anwendungsfälle des Observer-Patterns in verteilten Systemen vor und analysiert deren Implementierungsdetails.

## Übersicht der Anwendungsfälle

```mermaid
mindmap
  root((Observer-Pattern))
    Echtzeit-Anwendungen
      Dashboards
      Monitoring-Systeme
      Kollaborative Editoren
      Chat-Anwendungen
    Systemarchitektur
      Event-Driven Architecture
      Microservices
      Reactive Systems
      CQRS
    Datenkonsistenz
      Cache-Invalidierung
      Datenreplikation
      Master-Slave Synchronisation
      Verteilte Transaktionen
    User Interfaces
      UI-Updates
      Daten-Binding
      Fortschrittsanzeigen
      Benachrichtigungssysteme
```

## Detaillierte Anwendungsfälle

### 1. Echtzeit-Dashboard für verteilte Systemüberwachung

```mermaid
flowchart TD
    A[Monitoring Agents] -->|Messwerte senden| B[Event Bus]
    B -->|Benachrichtigung| C[Dashboard Server]
    C -->|WebSocket Push| D[Web Clients]
    
    subgraph "Server-Cluster"
        A
    end
    
    subgraph "Messaging System"
        B
    end
    
    subgraph "Anwendungsserver"
        C
    end
    
    subgraph "Benutzer"
        D
    end
```

#### Sequenzdiagramm eines Dashboard-Updates

```mermaid
sequenceDiagram
    participant Agent as Monitoring Agent
    participant Bus as Event Bus
    participant Server as Dashboard Server
    participant Client as Web Client
    
    Client->>Server: WebSocket Verbindung herstellen
    Server->>Bus: Abonnieren von Monitoring-Events
    
    Agent->>Bus: Metrik-Update senden (CPU: 85%)
    Bus->>Server: Event weiterleiten
    Server->>Client: Update via WebSocket
    
    Agent->>Bus: Metrik-Update senden (RAM: 62%)
    Bus->>Server: Event weiterleiten
    Server->>Client: Update via WebSocket
    
    Client->>Server: Filter setzen (nur CPU > 90%)
    Agent->>Bus: Metrik-Update senden (CPU: 95%)
    Bus->>Server: Event weiterleiten
    Server->>Client: Update via WebSocket
    
    Agent->>Bus: Metrik-Update senden (CPU: 82%)
    Bus->>Server: Event weiterleiten
    Note right of Server: Update wird gefiltert,<br>nicht an Client gesendet
```

### 2. Kollaborativer Dokumenteneditor

```mermaid
flowchart LR
    A[Client 1] -->|Änderung| B[Document Service]
    A -->|Subscribe| B
    C[Client 2] -->|Änderung| B
    C -->|Subscribe| B
    D[Client 3] -->|Änderung| B
    D -->|Subscribe| B
    
    B -->|Publish| A
    B -->|Publish| C
    B -->|Publish| D
    
    subgraph "Backend"
        B
    end
    
    subgraph "Clients"
        A
        C
        D
    end
```

#### Aktivitätsdiagramm einer kollaborativen Bearbeitung

```mermaid
stateDiagram-v2
    [*] --> ClientVerbunden
    
    ClientVerbunden --> DokumentAbonniert: Dokument öffnen
    DokumentAbonniert --> ÄnderungGesendet: Lokale Änderung
    ÄnderungGesendet --> ÄnderungBestätigt: Server bestätigt
    ÄnderungBestätigt --> DokumentAbonniert: Bereit für weitere Änderungen
    
    DokumentAbonniert --> RemoteÄnderungEmpfangen: Update von anderem Client
    RemoteÄnderungEmpfangen --> DokumentAktualisiert: Lokales Dokument aktualisieren
    DokumentAktualisiert --> KonflikteGeprüft: Auf Konflikte prüfen
    KonflikteGeprüft --> DokumentAbonniert: Keine Konflikte
    KonflikteGeprüft --> KonflikteBehoben: Konflikte beheben
    KonflikteBehoben --> ÄnderungGesendet: Konfliktlösung senden
    
    DokumentAbonniert --> Abonnement beendet: Dokument schließen
    Abonnement beendet --> [*]
```

### 3. Microservice-Event-Benachrichtigungen

```mermaid
flowchart TD
    A[Bestellungsservice] -->|Bestätigt| B[Kafka Topic: orders.confirmed]
    B -->|Subscribe| C[Lagerservice]
    B -->|Subscribe| D[Versandservice]
    B -->|Subscribe| E[Kundenbenachrichtigungsservice]
    
    C -->|Artikel reserviert| F[Kafka Topic: inventory.reserved]
    F -->|Subscribe| D
    
    D -->|Versand initiiert| G[Kafka Topic: shipping.initiated]
    G -->|Subscribe| E
    
    E -->|Kundenbenachrichtigung gesendet| H[Kafka Topic: notification.sent]
    H -->|Subscribe| A
    
    subgraph "Event-getriebene Microservices"
        A
        C
        D
        E
    end
    
    subgraph "Event Streams"
        B
        F
        G
        H
    end
```

#### Komponentendiagramm einer Microservice-Architektur

```mermaid
flowchart TD
    A[Event Producer] -->|1. Event erzeugen| B[Event Broker]
    B -->|2. Event verteilen| C[Event Consumer A]
    B -->|2. Event verteilen| D[Event Consumer B]
    
    subgraph "Producer Service"
        A
    end
    
    subgraph "Message Broker"
        B
    end
    
    subgraph "Consumer Services"
        C
        D
    end
    
    C -->|3. Status aktualisieren| E[Datenbank A]
    D -->|3. Status aktualisieren| F[Datenbank B]
    D -->|4. Neues Event erzeugen| G[Event Producer B]
    G -->|5. Folge-Event| B
```

## Design-Entscheidungen bei verteilten Observern

```mermaid
flowchart TD
    A[Design-Entscheidungen] --> B{Synchronität}
    
    B -->|"Synchron"| C[Blockierendes Warten]
    B -->|"Asynchron"| D[Non-blocking Kommunikation]
    
    C --> E{Fehlerausbreitung}
    D --> F{Zustellgarantie}
    
    E -->|"Sofort unterbrechen"| G[Fail-Fast]
    E -->|"Fehler isolieren"| H[Resiliente Verarbeitung]
    
    F -->|"Mindestens einmal"| I[At-least-once Delivery]
    F -->|"Genau einmal"| J[Exactly-once Delivery]
    
    G --> K[Atomare Operationen wichtig]
    H --> L[Kontinuität wichtiger als Konsistenz]
    
    I --> M[Idempotente Ereignishandler]
    J --> N[Komplexes Message Tracking]
```

## Evolutionspfad für Observer-Implementierungen

```mermaid
flowchart LR
    A[Einfache Observer-Implementierung] --> B{Skalierungsprobleme?}
    B -->|Ja| C[Thread-Pooling einführen]
    B -->|Nein| D[Beibehalten der einfachen Implementierung]
    
    C --> E{Verteilte Umgebung?}
    E -->|Ja| F[Event Bus einführen]
    E -->|Nein| G[Lokal optimieren]
    
    F --> H{Hohe Last?}
    H -->|Ja| I[Message Broker einführen]
    H -->|Nein| J[Event Bus beibehalten]
    
    I --> K{Ausfallsicherheit?}
    K -->|Ja| L[Redundante Message Broker]
    K -->|Nein| M[Einfachen Message Broker beibehalten]
    
    L --> N[Event Sourcing]
    M --> O[CQRS-Muster]
```

## Praktische Umsetzungsbeispiele

### Beispiel 1: Implementierung eines verteilten Observer-Patterns mit JMS

```java
// Publisher-Seite
public class EventPublisher {
    private final ConnectionFactory connectionFactory;
    private final String topicName;
    
    public EventPublisher(ConnectionFactory connectionFactory, String topicName) {
        this.connectionFactory = connectionFactory;
        this.topicName = topicName;
    }
    
    public void publishEvent(String eventType, String eventData) throws JMSException {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
            
            Topic topic = session.createTopic(topicName);
            MessageProducer producer = session.createProducer(topic);
            
            // Event als JSON serialisieren
            String jsonEvent = String.format("{'eventType':'%s','data':'%s'}", 
                    eventType, eventData);
            
            TextMessage message = session.createTextMessage(jsonEvent);
            producer.send(message);
            
            System.out.println("Event veröffentlicht: " + jsonEvent);
        }
    }
}

// Subscriber-Seite
public class EventSubscriber implements MessageListener {
    private final ConnectionFactory connectionFactory;
    private final String topicName;
    private final String subscriberId;
    private Connection connection;
    
    public EventSubscriber(ConnectionFactory connectionFactory, 
                           String topicName, 
                           String subscriberId) {
        this.connectionFactory = connectionFactory;
        this.topicName = topicName;
        this.subscriberId = subscriberId;
    }
    
    public void subscribe() throws JMSException {
        connection = connectionFactory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        
        Topic topic = session.createTopic(topicName);
        MessageConsumer consumer = session.createConsumer(topic);
        
        consumer.setMessageListener(this);
        connection.start();
        
        System.out.println("Subscriber " + subscriberId + " hat Topic " + 
                topicName + " abonniert");
    }
    
    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                
                System.out.println("Subscriber " + subscriberId + 
                        " hat Nachricht empfangen: " + text);
                
                // Event verarbeiten...
                processEvent(text);
            }
        } catch (JMSException e) {
            System.err.println("Fehler beim Verarbeiten der Nachricht: " + e.getMessage());
        }
    }
    
    private void processEvent(String eventJson) {
        // Event-Verarbeitung implementieren
        System.out.println("Verarbeite Event: " + eventJson);
    }
    
    public void unsubscribe() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Subscriber " + subscriberId + 
                        " hat Abonnement beendet");
            }
        } catch (JMSException e) {
            System.err.println("Fehler beim Beenden des Abonnements: " + e.getMessage());
        }
    }
}
```

### Beispiel 2: Verteiltes Observer-Pattern mit Redis Pub/Sub

```java
public class RedisPubSubExample {
    private static final String CHANNEL = "events";
    
    // Publisher-Seite
    public static class EventPublisher {
        private final Jedis jedis;
        
        public EventPublisher(String redisHost, int redisPort) {
            this.jedis = new Jedis(redisHost, redisPort);
        }
        
        public void publishEvent(String eventType, String eventData) {
            // Event als JSON serialisieren
            String jsonEvent = String.format("{'eventType':'%s','data':'%s'}", 
                    eventType, eventData);
            
            // Veröffentlichen über Redis Pub/Sub
            long receiverCount = jedis.publish(CHANNEL, jsonEvent);
            
            System.out.println("Event an " + receiverCount + 
                    " Empfänger veröffentlicht: " + jsonEvent);
        }
        
        public void close() {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    
    // Subscriber-Seite
    public static class EventSubscriber {
        private final Jedis jedis;
        private final String subscriberId;
        private final JedisPubSub jedisPubSub;
        
        public EventSubscriber(String redisHost, int redisPort, String subscriberId) {
            this.jedis = new Jedis(redisHost, redisPort);
            this.subscriberId = subscriberId;
            
            // PubSub-Handler erstellen
            this.jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    System.out.println("Subscriber " + subscriberId + 
                            " hat Nachricht auf Kanal " + channel + 
                            " empfangen: " + message);
                    
                    // Event verarbeiten
                    processEvent(message);
                }
                
                @Override
                public void onSubscribe(String channel, int subscribedChannels) {
                    System.out.println("Subscriber " + subscriberId + 
                            " hat Kanal " + channel + " abonniert");
                }
                
                @Override
                public void onUnsubscribe(String channel, int subscribedChannels) {
                    System.out.println("Subscriber " + subscriberId + 
                            " hat Abonnement für Kanal " + channel + " beendet");
                }
            };
        }
        
        public void subscribe() {
            // Starte Subscription in separatem Thread
            new Thread(() -> {
                System.out.println("Subscriber " + subscriberId + 
                        " startet Abonnement für Kanal " + CHANNEL);
                jedis.subscribe(jedisPubSub, CHANNEL);
            }).start();
        }
        
        public void unsubscribe() {
            if (jedisPubSub != null) {
                jedisPubSub.unsubscribe();
            }
            
            if (jedis != null) {
                jedis.close();
            }
        }
        
        private void processEvent(String eventJson) {
            // Event-Verarbeitung implementieren
            System.out.println("Verarbeite Event: " + eventJson);
        }
    }
}
```

### Beispiel 3: Verteiltes Observer-Pattern mit Spring Cloud Stream

```java
// Config-Klasse
@Configuration
@EnableBinding(EventChannels.class)
public class EventStreamConfig {
    // Konfiguration für Cloud Stream
}

// Kanaldefinitionen
public interface EventChannels {
    String INPUT_CHANNEL = "event-input";
    String OUTPUT_CHANNEL = "event-output";
    
    @Input(INPUT_CHANNEL)
    SubscribableChannel input();
    
    @Output(OUTPUT_CHANNEL)
    MessageChannel output();
}

// Event-Publisher
@Service
public class EventPublisher {
    private final EventChannels channels;
    
    @Autowired
    public EventPublisher(EventChannels channels) {
        this.channels = channels;
    }
    
    public void publishEvent(String eventType, Object eventData) {
        Event event = new Event(eventType, eventData);
        Message<Event> message = MessageBuilder.withPayload(event).build();
        
        boolean sent = channels.output().send(message);
        if (sent) {
            System.out.println("Event erfolgreich veröffentlicht: " + event);
        } else {
            System.err.println("Fehler beim Veröffentlichen des Events: " + event);
        }
    }
}

// Event-Consumer (Observer)
@Service
public class EventConsumer {
    
    @StreamListener(EventChannels.INPUT_CHANNEL)
    public void handleEvent(Event event) {
        System.out.println("Event empfangen: " + event);
        
        // Event basierend auf Typ verarbeiten
        switch(event.getType()) {
            case "USER_CREATED":
                handleUserCreated(event);
                break;
            case "ORDER_PLACED":
                handleOrderPlaced(event);
                break;
            default:
                System.out.println("Unbekannter Event-Typ: " + event.getType());
        }
    }
    
    private void handleUserCreated(Event event) {
        System.out.println("Verarbeite USER_CREATED Event: " + event.getData());
        // Implementierung...
    }
    
    private void handleOrderPlaced(Event event) {
        System.out.println("Verarbeite ORDER_PLACED Event: " + event.getData());
        // Implementierung...
    }
}

// Event-Klasse
public class Event {
    private String type;
    private Object data;
    private long timestamp;
    
    public Event() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Event(String type, Object data) {
        this();
        this.type = type;
        this.data = data;
    }
    
    // Getter und Setter...
}
```
