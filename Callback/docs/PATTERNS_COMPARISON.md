# Vergleich des Callback-Patterns mit anderen asynchronen Mustern

Dieses Dokument vergleicht das Callback-Pattern mit anderen asynchronen Entwurfsmustern und hilft bei der Entscheidung, welches Muster in verschiedenen Situationen am besten geeignet ist.

## Callback vs. andere asynchrone Muster

```mermaid
graph TD
    A[Asynchrone Entwurfsmuster] --> B[Callback]
    A --> C[Future/Promise]
    A --> D[Observer]
    A --> E[Event-Driven]
    A --> F[Reactive]
    A --> G[Async/Await]
    A --> H[Actor]
    
    B --- B1[Übergabe von Funktionen für spätere Ausführung]
    C --- C1[Stellvertreter für zukünftiges Ergebnis]
    D --- D1[Benachrichtigung an viele Beobachter bei Zustandsänderungen]
    E --- E1[Ereignisgesteuerte Programmierung]
    F --- F1[Deklarative Verarbeitung von Datenströmen]
    G --- G1[Synchroner Stil für asynchronen Code]
    H --- H1[Nachrichtenbasierte Kommunikation zwischen Akteuren]
```

## Entscheidungshilfe: Welches Muster wann?

```mermaid
graph TD
    A[Welches asynchrone Muster?] --> B{Single vs. Multiple Receiver?}
    B -->|Single| C{Einfache Ergebnisverarbeitung?}
    B -->|Multiple| D{Push-basiert?}
    
    C -->|Ja| E{Moderner Code?}
    C -->|Nein| F[Callback verwenden]
    
    D -->|Ja| G[Observer verwenden]
    D -->|Nein| H[Event-System verwenden]
    
    E -->|Ja| I[CompletableFuture oder Async/Await verwenden]
    E -->|Nein| F
    
    I --> J{Komplexe Transformation?}
    
    J -->|Ja| K[Reactive Programming verwenden]
    J -->|Nein| L[CompletableFuture verwenden]
```

## Detaillierter Vergleich: Callback vs. andere Muster

### Callback vs. Future/Promise

```mermaid
graph LR
    A[Vergleich] --> B[Callback]
    A --> C[Future/Promise]
    
    B --> B1[Zweck: Benachrichtigung über Ergebnis/Fehler]
    B --> B2[Fokus: Prozedurales Paradigma]
    B --> B3[Problem: "Callback Hell" bei Verschachtelung]
    B --> B4[Struktur: Funktionen werden übergeben]
    
    C --> C1[Zweck: Stellvertreter für ausstehende Ergebnisse]
    C --> C2[Fokus: Funktionales Paradigma]
    C --> C3[Vorteil: Methoden-Verkettung vermeidet "Callback Hell"]
    C --> C4[Struktur: Rückgabe eines Future-Objekts]
```

### Callback vs. Observer

```mermaid
graph LR
    A[Vergleich] --> B[Callback]
    A --> C[Observer]
    
    B --> B1[Typischerweise 1:1-Beziehung]
    B --> B2[Unmittelbare Benachrichtigung]
    B --> B3[Einmaliger Aufruf]
    B --> B4[Eng gekoppelt: kennt den Empfänger]
    
    C --> C1[1:n-Beziehung (viele Beobachter)]
    C --> C2[Kann verzögert benachrichtigen]
    C --> C3[Mehrere Aufrufe möglich]
    C --> C4[Lose gekoppelt: kennt Beobachter nicht direkt]
```

### Callback vs. Event-Driven

```mermaid
graph LR
    A[Vergleich] --> B[Callback]
    A --> C[Event-Driven]
    
    B --> B1[Zweck: Ergebnis einer Operation]
    B --> B2[Zeitlich: Fokus auf Beendigung]
    B --> B3[Struktur: Funktionsübergabe]
    B --> B4[Kopplung: Direkter Aufruf]
    
    C --> C1[Zweck: Ereignisbehandlung]
    C --> C2[Zeitlich: Ereignisse jederzeit möglich]
    C --> C3[Struktur: Event Loop und Handler]
    C --> C4[Kopplung: Indirekte Kommunikation über Events]
```

### Callback vs. Reactive

```mermaid
graph LR
    A[Vergleich] --> B[Callback]
    A --> C[Reactive]
    
    B --> B1[Imperativer Stil]
    B --> B2[Einzelereignis-orientiert]
    B --> B3[Manuelles Fehler-Handling]
    B --> B4[Sequentiell]
    
    C --> C1[Deklarativer Stil]
    C --> C2[Datenstrom-orientiert]
    C --> C3[Eingebaute Fehlerbehandlung]
    C --> C4[Operatoren für Transformation]
```

## Anwendungsfälle verschiedener Muster in Verteilten Systemen

```mermaid
graph TD
    A[Asynchrone Muster in Verteilten Systemen] --> B[Callback]
    A --> C[Future/Promise]
    A --> D[Reactive]
    A --> E[Actor]
    
    B --> B1[Remote Procedure Calls]
    B --> B2[Event Notification]
    B --> B3[Webhook-Integration]
    
    C --> C1[Parallele Datenverarbeitung]
    C --> C2[API-Anfragen]
    C --> C3[Koordination von Microservices]
    
    D --> D1[Stream Processing]
    D --> D2[Echtzeit-Daten]
    D --> D3[Event Sourcing]
    
    E --> E1[Fehlertolerante Systeme]
    E --> E2[Hochskalierbare Dienste]
    E --> E3[Parallele Verarbeitung mit Zustand]
```

## Kombination von Mustern

```mermaid
graph TD
    A[Muster-Kombinationen] --> B[Callback + Future]
    A --> C[Callback + Retry]
    A --> D[Callback + Circuit Breaker]
    A --> E[Callback + Throttling]
    
    B --> B1[Asynchrone APIs mit Fortschrittsüberwachung]
    C --> C1[Fehlertolerante asynchrone Operationen]
    D --> D1[Fehlertolerante verteilte Aufrufe]
    E --> E1[Lastausgleich für asynchrone Verarbeitung]
```

## Evolutionspfad für asynchrone Muster

```mermaid
graph LR
    A[Synchrone Aufrufe] --> B[Einfache Callbacks]
    B --> C[Strukturierte Callbacks]
    C --> D[Future/Promise APIs]
    D --> E[CompletableFuture]
    E --> F[Reactive Streams]
    F --> G[Async/Await]
    
    B --> H[Callback Hell]
    H --> I[Schwierige Wartbarkeit]
    I --> D
```

## Retry Callback-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Retry as RetryCallback
    participant Op as Operation
    participant Original as OriginalCallback
    
    Client->>+Retry: execute()
    Retry->>+Retry: Retry.decorateCallable()
    
    loop Retry attempts
        Retry->>+Op: execute operation
        
        alt Success
            Op-->>-Retry: result
            Retry->>+Original: onSuccess(result)
            Original-->>-Retry: return
            Retry-->>-Client: return
        else Error
            Op-->>-Retry: exception
            
            alt Retry limit not reached
                Note over Retry: Wait backoff time
                Retry->>Retry: retry attempt
            else Max retries reached
                Retry->>+Original: onError(exception)
                Original-->>-Retry: return
                Retry-->>-Client: return
            end
        end
    end
```

## Vergleichsmatrix: Vor- und Nachteile

| Muster | Stärken | Schwächen | Ideale Anwendungsfälle |
|--------|---------|-----------|------------------------|
| Callback | Einfach zu implementieren, weit verbreitet | Callback Hell, schwer zu verketten | Einfache asynchrone Operationen, Event Handler |
| Future/Promise | Methoden-Verkettung, bessere Lesbarkeit | Erhöht Komplexität | Parallele Datenverarbeitung, Orchestrierung mehrerer asynchroner Operationen |
| Observer | Lose Kopplung, 1:n-Benachrichtigung | Schwieriges Debugging, Reihenfolge nicht garantiert | GUI-Events, Zustandsänderungsnotifikationen |
| Reactive | Leistungsfähige Operatoren, Backpressure | Steile Lernkurve | Streaming-Daten, komplexe Ereignisketten, hohe Nebenläufigkeit |
| Async/Await | Lesbarkeit wie synchroner Code | Nicht in allen Sprachen verfügbar | Vereinfachung komplexer asynchroner Logik |
| Actor | Stark isolierte Komponenten | Komplexes Modell, Overhead | Hochskalierbare Systeme, Fehlertoleranz |

## Adaption von Callback-Pattern in verteilten Umgebungen

```mermaid
graph TD
    A[Callback in verteilten Systemen] --> B[Lokale Callbacks]
    A --> C[Remote Callbacks/Webhooks]
    A --> D[Message-basierte Callbacks]
    A --> E[Polling-basierte Callbacks]
    
    B --> B1[Innerhalb einer JVM]
    C --> C1[HTTP-Callbacks zu externen Systemen]
    D --> D1[Über Message Broker/Queue]
    E --> E1[Statusabfrage und Polling]
    
    B1 --> F[Direkte Thread-Kommunikation]
    C1 --> G[Netzwerklatenz und Ausfälle]
    D1 --> H[Entkopplung und Zuverlässigkeit]
    E1 --> I[Einfach aber ineffizient]
    
    G --> J[Erfordert Wiederholungslogik]
    H --> K[Komplexere Infrastruktur]
    I --> L[Erhöhte Netzwerklast]
```

## Migration von Callback zu modernen Alternativen

```mermaid
graph TD
    A[Migration von Callback] --> B[Zu CompletableFuture]
    A --> C[Zu Reactive]
    A --> D[Zu Async/Await]
    
    B --> B1[Callback-Interfaces durch Future-Rückgaben ersetzen]
    B --> B2[thenApply/thenAccept für Verkettung nutzen]
    
    C --> C1[Datenstrom-Modell einführen]
    C --> C2[Reaktive Operatoren für Transformationen]
    
    D --> D1[Einführung von async-Methoden]
    D --> D2[await für wartende Operationen]
    
    B1 --> E[Herausforderung: Bestehende Clients anpassen]
    C1 --> F[Herausforderung: Paradigmenwechsel]
    D1 --> G[Herausforderung: Sprachunterstützung]
```
