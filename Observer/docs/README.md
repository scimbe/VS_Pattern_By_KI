# Observer-Pattern Dokumentation

Diese Dokumentation enthält verschiedene Diagramme zur Veranschaulichung des Observer-Patterns und der konkreten Implementierung in diesem Projekt.

## Inhalt

- [Komponentendiagramm](#komponentendiagramm)
- [Klassendiagramme](#klassendiagramme)
- [Sequenzdiagramme](#sequenzdiagramme)
- [Zustandsdiagramme](#zustandsdiagramme)
- [Aktivitätsdiagramme](#aktivitätsdiagramme)

## Komponentendiagramm

Das folgende Diagramm zeigt die Hauptkomponenten des Projekts und ihre Beziehungen zueinander:

```mermaid
graph TD
    Client[Client] --> |verwendet| Observer[Observer Interface]
    Client[Client] --> |verwendet| Subject[Subject Interface]

    subgraph "Observer-Pattern Grundstruktur"
        Observer[Observer Interface]
        Subject[Subject Interface]
        ConcreteSubject[Concrete Subject]
        ConcreteObserver[Concrete Observer]
        
        Subject --> |benachrichtigt| Observer
        ConcreteSubject -.-> |implementiert| Subject
        ConcreteObserver -.-> |implementiert| Observer
        ConcreteSubject --> |benachrichtigt| ConcreteObserver
    end

    subgraph "Projekt-Implementierungen"
        BasicObserver[Basic Observer] -.-> |implementiert| Observer
        DataSource[Data Source] -.-> |implementiert| Subject
        DistributedObserver[Distributed Observer] -.-> |implementiert| Observer
        MessageBroker[Message Broker] -.-> |implementiert| Subject
    end

    Client --> Main[Main Demo]
    Main --> |verwendet| BasicObserver
    Main --> |verwendet| DataSource
    Main --> |verwendet| DistributedObserver
    Main --> |verwendet| MessageBroker
```

## Klassendiagramme

### Allgemeines Observer-Klassendiagramm

Das folgende Diagramm zeigt die allgemeine Struktur des Observer-Patterns:

```mermaid
classDiagram
    class Subject {
        <<interface>>
        +addObserver(observer: Observer)
        +removeObserver(observer: Observer)
        +notifyObservers()
        +notifyObservers(arg: Object)
    }

    class Observer {
        <<interface>>
        +update(subject: Subject, arg: Object)
    }

    class ConcreteSubject {
        -observers: List~Observer~
        -state: Object
        +getState(): Object
        +setState(state: Object)
    }

    class ConcreteObserver {
        -observerState: Object
        +update(subject: Subject, arg: Object)
    }

    Subject ..> Observer : notifies
    ConcreteSubject ..|> Subject : implements
    ConcreteObserver ..|> Observer : implements
    ConcreteSubject o-- Observer : contains
```

### DataSource-Klassendiagramm

```mermaid
classDiagram
    class Subject {
        <<interface>>
        +addObserver(observer: Observer)
        +removeObserver(observer: Observer)
        +notifyObservers()
        +notifyObservers(arg: Object)
    }

    class Observer {
        <<interface>>
        +update(subject: Subject, arg: Object)
    }

    class DataSource {
        -observers: List~Observer~
        -data: String
        +getData(): String
        +setData(data: String)
        +simulateDataUpdate()
    }

    class DataObserver {
        -name: String
        -lastData: String
        +update(subject: Subject, arg: Object)
        +getLastData(): String
        +getName(): String
    }

    Subject ..> Observer : notifies
    DataSource ..|> Subject : implements
    DataObserver ..|> Observer : implements
    DataSource o-- Observer : contains
```

### Verteiltes Observer-Klassendiagramm

```mermaid
classDiagram
    class Publisher {
        -publisherId: String
        -broker: MessageBroker
        +publishEvent(topic: String, eventData: String)
        +simulateEvents(topic: String, count: int, delayMs: long)
        +getPublisherId(): String
    }

    class MessageBroker {
        -connectionFactory: ConnectionFactory
        -connection: Connection
        -session: Session
        -connected: boolean
        -producers: Map~String, MessageProducer~
        -consumers: Map~String, Map~String, MessageConsumer~~
        +start()
        +publish(topic: String, content: String)
        +subscribe(topic: String, subscriberId: String, handler: EventHandler)
        +unsubscribe(topic: String, subscriberId: String)
        +stop()
    }

    class Subscriber {
        -subscriberId: String
        -broker: MessageBroker
        -messageCounters: Map~String, AtomicInteger~
        +subscribe(topic: String, handler: EventHandler)
        +subscribe(topic: String)
        +unsubscribe(topic: String)
        +getMessageCount(topic: String): int
        +getSubscriberId(): String
    }

    class EventHandler {
        <<interface>>
        +onEvent(topic: String, content: String)
    }

    Publisher --> MessageBroker : uses
    Subscriber --> MessageBroker : uses
    Subscriber ..> EventHandler : uses
```

## Sequenzdiagramme

### Einfaches Observer-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Subject as DataSource
    participant Observer1 as DataObserver 1
    participant Observer2 as DataObserver 2

    Client->>Subject: create(initialData)
    Client->>Observer1: create("Observer-1")
    Client->>Observer2: create("Observer-2")
    
    Client->>Subject: addObserver(observer1)
    Client->>Subject: addObserver(observer2)
    
    Client->>Subject: setData("Neue Daten")
    Subject->>Subject: Daten aktualisieren
    Subject->>Observer1: update(this, oldData)
    Observer1->>Subject: getData()
    Subject-->>Observer1: "Neue Daten"
    Observer1->>Observer1: processData()
    
    Subject->>Observer2: update(this, oldData)
    Observer2->>Subject: getData()
    Subject-->>Observer2: "Neue Daten"
    Observer2->>Observer2: processData()
    
    Client->>Subject: removeObserver(observer1)
    Client->>Subject: setData("Weitere Daten")
    Subject->>Subject: Daten aktualisieren
    Subject->>Observer2: update(this, oldData)
    Observer2->>Subject: getData()
    Subject-->>Observer2: "Weitere Daten"
    Observer2->>Observer2: processData()
```

### Verteiltes Observer-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Publisher
    participant MessageBroker
    participant Subscriber1
    participant Subscriber2
    
    Client->>Publisher: create("Publisher-1", broker)
    Client->>Subscriber1: create("Subscriber-1", broker)
    Client->>Subscriber2: create("Subscriber-2", broker)
    
    Client->>Subscriber1: subscribe("sensors.data")
    Subscriber1->>MessageBroker: subscribe("sensors.data", "Subscriber-1", handler)
    MessageBroker-->>Subscriber1: Consumer registriert
    
    Client->>Subscriber2: subscribe("sensors.data")
    Subscriber2->>MessageBroker: subscribe("sensors.data", "Subscriber-2", handler)
    MessageBroker-->>Subscriber2: Consumer registriert
    
    Client->>Publisher: publishEvent("sensors.data", "Temperatur: 23.5°C")
    Publisher->>MessageBroker: publish("sensors.data", "{'publisher':'Publisher-1', 'data':'Temperatur: 23.5°C'}")
    MessageBroker->>Subscriber1: onEvent("sensors.data", "{'publisher':'Publisher-1', 'data':'Temperatur: 23.5°C'}")
    Subscriber1->>Subscriber1: processEvent()
    MessageBroker->>Subscriber2: onEvent("sensors.data", "{'publisher':'Publisher-1', 'data':'Temperatur: 23.5°C'}")
    Subscriber2->>Subscriber2: processEvent()
    
    Client->>Subscriber1: unsubscribe("sensors.data")
    Subscriber1->>MessageBroker: unsubscribe("sensors.data", "Subscriber-1")
    MessageBroker-->>Subscriber1: Consumer entfernt
    
    Client->>Publisher: publishEvent("sensors.data", "Temperatur: 24.0°C")
    Publisher->>MessageBroker: publish("sensors.data", "{'publisher':'Publisher-1', 'data':'Temperatur: 24.0°C'}")
    MessageBroker->>Subscriber2: onEvent("sensors.data", "{'publisher':'Publisher-1', 'data':'Temperatur: 24.0°C'}")
    Subscriber2->>Subscriber2: processEvent()
```

## Zustandsdiagramme

### Observer-Lebenszyklus

```mermaid
stateDiagram-v2
    [*] --> Erstellt: Konstruktor aufgerufen
    
    Erstellt --> Registriert: addObserver()
    Registriert --> Benachrichtigt: notifyObservers()
    
    Benachrichtigt --> Aktualisiert: update() bearbeitet
    Aktualisiert --> Benachrichtigt: erneute Benachrichtigung
    
    Aktualisiert --> Registriert: warte auf nächste Benachrichtigung
    
    Registriert --> Deregistriert: removeObserver()
    Deregistriert --> [*]: Garbage Collection
    
    Registriert --> [*]: Programm Ende
    Deregistriert --> Registriert: erneutes addObserver()
```

### Verteilter Observer-Lebenszyklus

```mermaid
stateDiagram-v2
    [*] --> Erstellt: Konstruktor aufgerufen
    
    Erstellt --> Verbunden: Message Broker start()
    Verbunden --> Abonniert: subscribe()
    
    Abonniert --> Empfangend: Event empfangen
    Empfangend --> Verarbeitend: Event verarbeiten
    Verarbeitend --> Empfangend: Neues Event
    Verarbeitend --> Abonniert: Verarbeitung abgeschlossen
    
    Abonniert --> Abonnement_Beendet: unsubscribe()
    Abonnement_Beendet --> Verbunden: Warte auf neues Abonnement
    
    Verbunden --> Getrennt: stop()
    Getrennt --> [*]: Ressourcen freigegeben
```

## Aktivitätsdiagramme

### Grundlegendes Observer-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start: Zustandsänderung] --> B[Subject.setState()]
    B --> C[Speichere neuen Zustand]
    C --> D[notifyObservers aufrufen]
    D --> E[Kopie der Observer-Liste erstellen]
    E --> F{Weitere Observer?}
    
    F -->|Ja| G[Nächsten Observer auswählen]
    G --> H[Observer.update aufrufen]
    H --> I[Observer fragt Zustand ab]
    I --> J[Observer verarbeitet Zustand]
    J --> F
    
    F -->|Nein| K[Ende der Benachrichtigung]
    K --> L[Ende]
```

### Verteiltes Observer-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start: Ereignis tritt auf] --> B[Publisher.publishEvent]
    B --> C[Erstelle Nachricht]
    C --> D[MessageBroker.publish]
    D --> E[Sende an Message Broker]
    E --> F[Message Broker verteilt Nachricht]
    
    F --> G{Weitere Subscriber?}
    G -->|Ja| H[Sende an nächsten Subscriber]
    H --> I[Subscriber.onEvent]
    I --> J[Subscriber verarbeitet Ereignis]
    J --> G
    
    G -->|Nein| K[Veröffentlichung abgeschlossen]
    K --> L[Ende]
```
