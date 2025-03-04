# Interceptor-Pattern Dokumentation

Diese Dokumentation enthält verschiedene Diagramme zur Veranschaulichung des Interceptor-Patterns und der konkreten Implementierung in diesem Projekt.

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
    Client[Client] --> |verwendet| Dispatcher[Dispatcher]
    
    subgraph "Interceptor-Pattern Grundstruktur"
        Dispatcher
        Context[Context]
        Interceptor[Interceptor Interface]
        Operation[Operation Interface]
        
        Dispatcher --> |verwaltet| Interceptor
        Dispatcher --> |verwendet| Context
        Dispatcher --> |führt aus| Operation
        Interceptor --> |interagiert mit| Context
    end
    
    subgraph "Projekt-Implementierungen"
        LoggingInterceptor[Logging Interceptor] -.-> |implementiert| Interceptor
        SecurityInterceptor[Security Interceptor] -.-> |implementiert| Interceptor
        CachingInterceptor[Caching Interceptor] -.-> |implementiert| Interceptor
        PerformanceInterceptor[Performance Interceptor] -.-> |implementiert| Interceptor
        PipelineInterceptor[Pipeline Interceptor] -.-> |implementiert| Interceptor
    end
    
    Client --> Main[Main Demo]
    Main --> |verwendet| LoggingInterceptor
    Main --> |verwendet| SecurityInterceptor
    Main --> |verwendet| CachingInterceptor
    Main --> |verwendet| PerformanceInterceptor
    Main --> |verwendet| PipelineDemo[Pipeline Demo]
    PipelineDemo --> |verwendet| PipelineInterceptor
```

## Klassendiagramme

### Allgemeines Interceptor-Klassendiagramm

Das folgende Diagramm zeigt die allgemeine Struktur des Interceptor-Patterns:

```mermaid
classDiagram
    class Client {
        +performOperation()
    }
    
    class Dispatcher {
        -interceptors: List~Interceptor~
        +registerInterceptor(interceptor: Interceptor)
        +removeInterceptor(interceptor: Interceptor)
        +dispatch(context: Context, operation: Operation): boolean
        -preProcess(context: Context): boolean
        -postProcess(context: Context)
        -handleException(context: Context, exception: Exception): boolean
    }
    
    class Context {
        -executionId: String
        -startTime: long
        -endTime: long
        -input: Object
        -result: Object
        -successful: boolean
        -attributes: Map~String, Object~
        +getExecutionId(): String
        +getInput(): Object
        +setInput(input: Object)
        +getResult(): Object
        +setResult(result: Object)
        +isSuccessful(): boolean
        +setSuccessful(successful: boolean)
        +getAttribute(key: String): Object
        +setAttribute(key: String, value: Object)
        +getAttributes(): Map~String, Object~
        +getDuration(): long
    }
    
    class Interceptor {
        <<interface>>
        +preProcess(context: Context): boolean
        +postProcess(context: Context)
        +handleException(context: Context, exception: Exception): boolean
    }
    
    class Operation {
        <<interface>>
        +execute(context: Context): Object
    }
    
    class ConcreteInterceptor {
        +preProcess(context: Context): boolean
        +postProcess(context: Context)
        +handleException(context: Context, exception: Exception): boolean
    }
    
    Client --> Dispatcher: uses
    Dispatcher o-- Interceptor: contains
    Dispatcher --> Context: creates/manages
    Dispatcher --> Operation: executes
    Interceptor --> Context: manipulates
    ConcreteInterceptor ..|> Interceptor: implements
```

### Logging-Interceptor Klassendiagramm

```mermaid
classDiagram
    class Interceptor {
        <<interface>>
        +preProcess(context: Context): boolean
        +postProcess(context: Context)
        +handleException(context: Context, exception: Exception): boolean
    }
    
    class LoggingInterceptor {
        -name: String
        -level: LogLevel
        +LoggingInterceptor()
        +LoggingInterceptor(name: String)
        +LoggingInterceptor(name: String, level: LogLevel)
        +preProcess(context: Context): boolean
        +postProcess(context: Context)
        +handleException(context: Context, exception: Exception): boolean
        -log(format: String, args: Object...): void
    }
    
    class LogLevel {
        <<enumeration>>
        DEBUG
        INFO
        WARN
        ERROR
    }
    
    LoggingInterceptor ..|> Interceptor: implements
    LoggingInterceptor --> LogLevel: uses
```

### Caching-Interceptor Klassendiagramm

```mermaid
classDiagram
    class Interceptor {
        <<interface>>
        +preProcess(context: Context): boolean
        +postProcess(context: Context)
        +handleException(context: Context, exception: Exception): boolean
    }
    
    class CachingInterceptor {
        -cache: Map~String, CacheEntry~
        -timeToLiveMs: long
        +CachingInterceptor()
        +CachingInterceptor(timeToLiveMs: long)
        +preProcess(context: Context): boolean
        +postProcess(context: Context)
        +handleException(context: Context, exception: Exception): boolean
        -generateCacheKey(context: Context): String
        +invalidate(cacheKey: String): boolean
        +invalidateAll(): void
        +cleanUp(): void
    }
    
    class CacheEntry {
        -value: Object
        -expiryTime: long
        +CacheEntry(value: Object, timeToLiveMs: long)
        +getValue(): Object
        +isExpired(): boolean
    }
    
    CachingInterceptor ..|> Interceptor: implements
    CachingInterceptor *-- CacheEntry: contains
```

### Pipeline-Interceptor Klassendiagramm

```mermaid
classDiagram
    class PipelineInterceptor {
        <<interface>>
        +intercept(message: Object, context: Context, chain: InterceptorChain): boolean
        +getName(): String
    }
    
    class InterceptorChain {
        -interceptors: List~PipelineInterceptor~
        -currentIndex: int
        +InterceptorChain(interceptors: List~PipelineInterceptor~)
        +proceed(message: Object, context: Context): boolean
        +hasNext(): boolean
        +size(): int
        +getCurrentIndex(): int
        +reset(): void
    }
    
    class PipelineDispatcher {
        -interceptors: List~PipelineInterceptor~
        +registerInterceptor(interceptor: PipelineInterceptor): void
        +removeInterceptor(interceptor: PipelineInterceptor): boolean
        +getInterceptors(): List~PipelineInterceptor~
        +process(message: Object, context: Context): boolean
        +processAndTransform(input: T, context: Context, transformer: Function~Context, R~): R
    }
    
    class LoggingPipelineInterceptor {
        -name: String
        +LoggingPipelineInterceptor()
        +LoggingPipelineInterceptor(name: String)
        +intercept(message: Object, context: Context, chain: InterceptorChain): boolean
        +getName(): String
    }
    
    PipelineDispatcher o-- PipelineInterceptor: manages
    PipelineDispatcher --> InterceptorChain: creates
    InterceptorChain o-- PipelineInterceptor: contains
    LoggingPipelineInterceptor ..|> PipelineInterceptor: implements
```

## Sequenzdiagramme

### Grundlegendes Interceptor-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Dispatcher
    participant Interceptors as Interceptor(s)
    participant Operation
    
    Client->>+Dispatcher: dispatch(context, operation)
    
    Dispatcher->>+Interceptors: preProcess(context)
    Interceptors-->>-Dispatcher: true (continue)
    
    Dispatcher->>+Operation: execute(context)
    Operation-->>-Dispatcher: result
    
    Dispatcher->>Dispatcher: context.setResult(result)
    
    Dispatcher->>+Interceptors: postProcess(context)
    Interceptors-->>-Dispatcher: void
    
    Dispatcher-->>-Client: true (success)
```

### Fehlerbehandlungs-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Dispatcher
    participant Interceptors as Interceptor(s)
    participant Operation
    
    Client->>+Dispatcher: dispatch(context, operation)
    
    Dispatcher->>+Interceptors: preProcess(context)
    Interceptors-->>-Dispatcher: true (continue)
    
    Dispatcher->>+Operation: execute(context)
    Operation-->>-Dispatcher: throws Exception
    
    Dispatcher->>+Interceptors: handleException(context, exception)
    Interceptors-->>-Dispatcher: true (handled)
    
    Dispatcher->>Dispatcher: context.setSuccessful(false)
    
    Dispatcher-->>-Client: true (handled)
```

### Pipeline-Interceptor-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant PipelineDispatcher
    participant Chain as InterceptorChain
    participant Interceptor1
    participant Interceptor2
    participant Interceptor3
    
    Client->>+PipelineDispatcher: process(message, context)
    PipelineDispatcher->>+Chain: create new chain
    PipelineDispatcher->>Chain: proceed(message, context)
    
    Chain->>+Interceptor1: intercept(message, context, chain)
    Interceptor1->>Interceptor1: pre-processing
    Interceptor1->>+Chain: chain.proceed(message, context)
    
    Chain->>+Interceptor2: intercept(message, context, chain)
    Interceptor2->>Interceptor2: pre-processing
    Interceptor2->>+Chain: chain.proceed(message, context)
    
    Chain->>+Interceptor3: intercept(message, context, chain)
    Interceptor3->>Interceptor3: pre-processing
    Interceptor3->>Interceptor3: core processing
    Interceptor3->>Interceptor3: post-processing
    Interceptor3-->>-Chain: result
    
    Chain-->>-Interceptor2: result
    Interceptor2->>Interceptor2: post-processing
    Interceptor2-->>-Chain: result
    
    Chain-->>-Interceptor1: result
    Interceptor1->>Interceptor1: post-processing
    Interceptor1-->>-Chain: result
    
    Chain-->>-PipelineDispatcher: result
    PipelineDispatcher-->>-Client: result
```

### Verteiltes Tracing-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Gateway
    participant Service1
    participant Service2
    
    Client->>+Gateway: send request
    
    Gateway->>+Gateway: TracingInterceptor.preProcess
    Note over Gateway: Generate TraceID
    
    Gateway->>+Service1: forward request with TraceID
    Service1->>+Service1: TracingInterceptor.preProcess
    Note over Service1: Generate SpanID, link to TraceID
    Service1->>Service1: process request
    Service1->>+Service1: TracingInterceptor.postProcess
    Service1-->>-Gateway: response
    
    Gateway->>+Service2: forward to service2 with TraceID
    Service2->>+Service2: TracingInterceptor.preProcess
    Note over Service2: Generate SpanID, link to TraceID
    Service2->>Service2: process request
    Service2->>+Service2: TracingInterceptor.postProcess
    Service2-->>-Gateway: response
    
    Gateway->>+Gateway: TracingInterceptor.postProcess
    Gateway-->>-Client: final response
```

## Zustandsdiagramme

### Interceptor-Ausführungszustandsdiagramm

```mermaid
stateDiagram-v2
    [*] --> Ready
    
    Ready --> PreProcessing: dispatch()
    
    PreProcessing --> OperationExecution: preProcess() returns true
    PreProcessing --> Finished: preProcess() returns false
    
    OperationExecution --> PostProcessing: operation succeeds
    OperationExecution --> ErrorHandling: operation throws exception
    
    ErrorHandling --> PostProcessing: handleException() returns true
    ErrorHandling --> Finished: handleException() returns false
    
    PostProcessing --> Finished: postProcess() completes
    
    Finished --> [*]
```

### Caching-Interceptor Zustandsdiagramm

```mermaid
stateDiagram-v2
    [*] --> CheckCache
    
    CheckCache --> CacheHit: key found and valid
    CheckCache --> CacheMiss: key not found or expired
    
    CacheHit --> ReturnCachedResult: set result from cache
    ReturnCachedResult --> [*]: skip operation execution
    
    CacheMiss --> ExecuteOperation: continue execution
    ExecuteOperation --> StoreInCache: operation successful
    ExecuteOperation --> [*]: operation failed
    
    StoreInCache --> [*]
```

## Aktivitätsdiagramme

### Grundlegendes Interceptor-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Erstelle Context]
    B --> C[Rufe preProcess aller Interceptoren auf]
    
    C --> D{Alle preProcess erfolgreich?}
    D -->|Ja| E[Führe Operation aus]
    D -->|Nein| F[Setze erfolglos Status]
    
    E --> G{Operation erfolgreich?}
    G -->|Ja| H[Setze Ergebnis im Context]
    G -->|Nein| I[Rufe handleException der Interceptoren auf]
    
    I --> J{Exception behandelt?}
    J -->|Ja| K[Setze behandelten Status]
    J -->|Nein| L[Wirf Exception erneut]
    
    H --> M[Rufe postProcess aller Interceptoren auf]
    K --> M
    
    F --> N[Ende]
    M --> N
    L --> N
```

### Pipeline-Interceptor Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Erstelle InterceptorChain]
    B --> C[Rufe chain.proceed auf]
    
    C --> D[Hole nächsten Interceptor]
    D --> E{Interceptor vorhanden?}
    
    E -->|Ja| F[Rufe intercept auf]
    E -->|Nein| G[Verarbeitung abgeschlossen]
    
    F --> H{Weitermachen?}
    H -->|Ja| I[Erhöhe currentIndex]
    H -->|Nein| J[Verarbeitung abgebrochen]
    
    I --> C
    
    G --> K[Ende mit Erfolg]
    J --> L[Ende mit Abbruch]
```

### HTTP-Interceptor Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Eingehende HTTP-Anfrage]
    B --> C[Erstelle Context]
    
    C --> D[AuthenticationInterceptor]
    D --> E{Authentifiziert?}
    
    E -->|Ja| F[SecurityInterceptor]
    E -->|Nein| G[Setze 401 Unauthorized]
    
    F --> H{Autorisiert?}
    H -->|Ja| I[ContentNegotiationInterceptor]
    H -->|Nein| J[Setze 403 Forbidden]
    
    I --> K[CachingInterceptor]
    K --> L{Cache-Treffer?}
    
    L -->|Ja| M[Setze Ergebnis aus Cache]
    L -->|Nein| N[Führe Controller-Methode aus]
    
    N --> O[PerformanceInterceptor]
    M --> O
    
    O --> P[Setze HTTP-Antwort]
    G --> P
    J --> P
    
    P --> Q[Ende]
```

### Fehlerbehandlungs-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Verarbeitung in Interceptor]
    B --> C{Fehler aufgetreten?}
    
    C -->|Nein| D[Normale Verarbeitung]
    C -->|Ja| E[Fehlerkontext erstellen]
    
    E --> F[LoggingInterceptor.handleException]
    F --> G{Behandelt?}
    
    G -->|Nein| H[SecurityInterceptor.handleException]
    G -->|Ja| I[Fehler als behandelt markieren]
    
    H --> J{Behandelt?}
    J -->|Nein| K[Benutzerdefinierter Interceptor.handleException]
    J -->|Ja| I
    
    K --> L{Behandelt?}
    L -->|Nein| M[Unbehandelte Exception weiterwerfen]
    L -->|Ja| I
    
    I --> N[Standardergebnis setzen]
    D --> O[Normales Ergebnis setzen]
    
    M --> P[Fehler an Client melden]
    N --> Q[Kontrolliertes Ergebnis zurückgeben]
    O --> Q
    
    P --> R[Ende]
    Q --> R
```