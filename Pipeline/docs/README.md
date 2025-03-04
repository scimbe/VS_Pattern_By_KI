# Pipeline-Pattern Dokumentation

Diese Dokumentation enthält verschiedene Diagramme zur Veranschaulichung des Pipeline-Patterns und der konkreten Implementierung in diesem Projekt.

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
    Client[Client] --> |verwendet| Pipeline[Pipeline]

    subgraph "Pipeline-Pattern Grundstruktur"
        Pipeline --> |enthält| Stage[Pipeline Stage]
        Pipeline --> |nutzt| Context[Pipeline Context]
        Context --> |beinhaltet| Metadata[Metadaten]
        Stage --> |nutzt| Context
    end

    subgraph "Projekt-Implementierungen"
        SyncPipeline[Synchrone Pipeline] -.-> |implementiert| Pipeline
        AsyncPipeline[Asynchrone Pipeline] -.-> |implementiert| Pipeline
        DistPipeline[Verteilte Pipeline] -.-> |implementiert| Pipeline
    end

    Client --> Main[Main Demo]
    Main --> |verwendet| SyncPipeline
    Main --> |verwendet| AsyncPipeline
    Main --> |verwendet| DistPipeline
```

## Klassendiagramme

### Allgemeines Pipeline-Klassendiagramm

Das folgende Diagramm zeigt die allgemeine Struktur des Pipeline-Patterns:

```mermaid
classDiagram
    class Client {
        +executePipeline()
    }

    class Pipeline~I, O~ {
        -name: String
        -stages: List~PipelineStage~
        +addStage(stage: PipelineStage): Pipeline
        +execute(input: I): O
        +getName(): String
        +getStages(): List~PipelineStage~
    }

    class PipelineStage~I, O~ {
        <<interface>>
        +process(input: I, context: PipelineContext): O
        +getStageName(): String
    }

    class PipelineContext {
        -executionId: String
        -startTime: long
        -attributes: Map~String, Object~
        -error: Throwable
        +setAttribute(key: String, value: Object)
        +getAttribute(key: String): Object
        +setError(error: Throwable)
        +getError(): Throwable
        +getDuration(): long
    }

    class PipelineException {
        +PipelineException(message: String)
        +PipelineException(message: String, cause: Throwable)
        +PipelineException(message: String, cause: Throwable, stageName: String)
    }

    Client --> Pipeline : uses
    Pipeline --> PipelineStage : contains
    Pipeline --> PipelineContext : creates
    PipelineStage --> PipelineContext : uses
    PipelineStage ..> PipelineException : throws
    Pipeline ..> PipelineException : throws
```

### Sequential Pipeline-Klassendiagramm

```mermaid
classDiagram
    class Pipeline~I, O~ {
        -name: String
        -stages: List~PipelineStage~
        +addStage(stage: PipelineStage): Pipeline
        +execute(input: I): O
        +getName(): String
        +getStages(): List~PipelineStage~
    }

    class PipelineStage~I, O~ {
        <<interface>>
        +process(input: I, context: PipelineContext): O
        +getStageName(): String
    }

    class PipelineContext {
        -executionId: String
        -startTime: long
        -attributes: Map~String, Object~
        -error: Throwable
        +setAttribute(key: String, value: Object)
        +getAttribute(key: String): Object
    }

    class TextToUpperCaseStage {
        +process(input: String, context: PipelineContext): String
        +getStageName(): String
    }

    class RemoveSpacesStage {
        +process(input: String, context: PipelineContext): String
        +getStageName(): String
    }

    class AddPrefixStage {
        -prefix: String
        +process(input: String, context: PipelineContext): String
        +getStageName(): String
    }
    
    Pipeline --> PipelineStage : contains
    Pipeline --> PipelineContext : creates
    TextToUpperCaseStage ..|> PipelineStage : implements
    RemoveSpacesStage ..|> PipelineStage : implements
    AddPrefixStage ..|> PipelineStage : implements
```

### Asynchrone Pipeline-Klassendiagramm

```mermaid
classDiagram
    class AsyncPipeline~I, O~ {
        -name: String
        -stages: List~AsyncPipelineStage~
        +addStage(stage: AsyncPipelineStage): AsyncPipeline
        +execute(input: I): O
        +executeAsync(input: I): CompletableFuture~O~
        +getName(): String
        +getStages(): List~AsyncPipelineStage~
    }

    class AsyncPipelineStage~I, O~ {
        <<interface>>
        +processAsync(input: I, context: PipelineContext): CompletableFuture~O~
        +getStageName(): String
    }

    class PipelineContext {
        -executionId: String
        -startTime: long
        -attributes: Map~String, Object~
        -error: Throwable
        +setAttribute(key: String, value: Object)
        +getAttribute(key: String): Object
    }

    class CompletableFuture~T~ {
        +thenApply(Function~T,U~): CompletableFuture~U~
        +thenAccept(Consumer~T~): CompletableFuture~Void~
        +thenCompose(Function~T,CompletableFuture~U~~): CompletableFuture~U~
        +exceptionally(Function~Throwable,T~): CompletableFuture~T~
        +get(): T
    }
    
    AsyncPipeline --> AsyncPipelineStage : contains
    AsyncPipeline --> PipelineContext : creates
    AsyncPipelineStage --> CompletableFuture : returns
```

### Verteilte Pipeline-Klassendiagramm

```mermaid
classDiagram
    class DistributedPipeline~I, O~ {
        -name: String
        -stageEndpoints: Map~String, ServiceEndpoint~
        -stageSequence: List~String~
        +registerStage(stageName: String, endpoint: ServiceEndpoint): DistributedPipeline
        +execute(input: I): O
        +getName(): String
    }

    class ServiceEndpoint~I, O~ {
        <<interface>>
        +invokeService(input: I, context: PipelineContext): O
        +getEndpointAddress(): String
    }

    class PipelineContext {
        -executionId: String
        -startTime: long
        -attributes: Map~String, Object~
        -error: Throwable
        +setAttribute(key: String, value: Object)
        +getAttribute(key: String): Object
    }
    
    DistributedPipeline --> ServiceEndpoint : contains
    DistributedPipeline --> PipelineContext : creates
```

## Sequenzdiagramme

### Sequentielle Pipeline-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Pipeline
    participant Stage1
    participant Stage2
    participant Stage3
    participant Context

    Client->>+Pipeline: execute(input)
    Pipeline->>+Context: create
    Context-->>-Pipeline: context
    
    Pipeline->>+Stage1: process(input, context)
    Stage1-->>-Pipeline: result1
    
    Pipeline->>+Stage2: process(result1, context)
    Stage2-->>-Pipeline: result2
    
    Pipeline->>+Stage3: process(result2, context)
    Stage3-->>-Pipeline: result3
    
    Pipeline-->>-Client: result3
```

### Asynchrone Pipeline-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant AsyncPipeline
    participant Stage1
    participant Stage2
    participant Stage3
    participant Context

    Client->>+AsyncPipeline: executeAsync(input)
    AsyncPipeline->>+Context: create
    Context-->>-AsyncPipeline: context
    
    AsyncPipeline->>+Stage1: processAsync(input, context)
    Stage1-->>-AsyncPipeline: CompletableFuture<result1>
    
    AsyncPipeline->>+Stage2: processAsync(result1, context)
    Stage2-->>-AsyncPipeline: CompletableFuture<result2>
    
    AsyncPipeline->>+Stage3: processAsync(result2, context)
    Stage3-->>-AsyncPipeline: CompletableFuture<result3>
    
    AsyncPipeline-->>-Client: CompletableFuture<result3>
    
    Note over Client,AsyncPipeline: Client kann auf CompletableFuture warten oder Callbacks registrieren
```

### Verteilte Pipeline-Sequenzdiagramm 

```mermaid
sequenceDiagram
    participant Client
    participant DistPipeline as DistributedPipeline
    participant Context
    participant Service1
    participant Service2
    participant Service3
    
    Client->>+DistPipeline: execute(input) 
    DistPipeline->>+Context: create
    Context-->>-DistPipeline: context

    DistPipeline->>+Service1: invokeService(input, context)
    Service1-->>-DistPipeline: result1

    DistPipeline->>+Service2: invokeService(result1, context)
    Service2-->>-DistPipeline: result2

    DistPipeline->>+Service3: invokeService(result2, context)
    Service3-->>-DistPipeline: result3

    DistPipeline-->>-Client: result3
```

## Zustandsdiagramme

### Pipeline-Ausführungszustandsdiagramm

```mermaid
stateDiagram-v2
    [*] --> Initialisierung

    Initialisierung --> Stage1 : Erste Stage ausführen
    Stage1 --> Stage2 : Erfolgreich
    Stage1 --> Fehlerzustand : Fehler
    
    Stage2 --> Stage3 : Erfolgreich
    Stage2 --> Fehlerzustand : Fehler
    
    Stage3 --> Abgeschlossen : Erfolgreich
    Stage3 --> Fehlerzustand : Fehler
    
    Abgeschlossen --> [*]
    Fehlerzustand --> [*]
```

### Asynchrone Pipeline-Zustandsdiagramm

```mermaid
stateDiagram-v2
    [*] --> Initialisierung

    Initialisierung --> AsyncStage1 : Future initialisieren
    AsyncStage1 --> AsyncStage2 : thenCompose()
    AsyncStage1 --> Fehlerzustand : exceptionally()
    
    AsyncStage2 --> AsyncStage3 : thenCompose()
    AsyncStage2 --> Fehlerzustand : exceptionally()
    
    AsyncStage3 --> Abwarten : CompletableFuture erzeugt
    AsyncStage3 --> Fehlerzustand : exceptionally()
    
    Abwarten --> Abgeschlossen : .get() oder .join() aufgerufen
    Abwarten --> Callback : .thenAccept() registriert
    
    Callback --> [*]
    Abgeschlossen --> [*]
    Fehlerzustand --> [*]
```

## Aktivitätsdiagramme

### Grundlegendes Pipeline-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Eingabe vorbereiten]
    B --> C[Pipeline erstellen]
    C --> D[Stages hinzufügen]
    D --> E[Pipeline ausführen]
    
    E --> F{Stage 1}
    F --> |Erfolg| G{Stage 2}
    F --> |Fehler| K[Fehlerbehandlung]
    
    G --> |Erfolg| H{Stage 3}
    G --> |Fehler| K
    
    H --> |Erfolg| I[Ergebnis zurückgeben]
    H --> |Fehler| K
    
    I --> J[Ende]
    K --> L[Exception werfen]
    L --> J
```

### Asynchrone Pipeline-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Eingabe vorbereiten]
    B --> C[AsyncPipeline erstellen]
    C --> D[AsyncStages hinzufügen]
    D --> E[executeAsync aufrufen]
    
    E --> F[CompletableFuture mit Eingabe erstellen]
    F --> G[Stage 1 als thenCompose verknüpfen]
    G --> H[Stage 2 als thenCompose verknüpfen]
    H --> I[Stage 3 als thenCompose verknüpfen]
    
    I --> J[CompletableFuture zurückgeben]
    J --> K{Auf Ergebnis warten?}
    
    K ---> |Ja| L[Future.get aufrufen]
    K ---> |Nein| M[Callbacks registrieren]
    
    L --> N[Ergebnis zurückgeben]
    M --> O[Async weiterverarbeiten]
    
    N --> P[Ende]
    O --> P
```

### Verteilte Pipeline-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Eingabe vorbereiten]
    B --> C[DistributedPipeline erstellen]
    C --> D[ServiceEndpoints registrieren]
    D --> E[Reihenfolge der Stages festlegen]
    E --> F[execute aufrufen]
    
    F --> G[Stage 1 Service aufrufen]
    G --> H{Service 1 erfolgreich?}
    
    H ---> |Ja| I[Stage 2 Service aufrufen]
    H ---> |Nein| Q[Fehlerbehandlung]
    
    I --> J{Service 2 erfolgreich?}
    J ---> |Ja| K[Stage 3 Service aufrufen]
    J ---> |Nein| Q
    
    K --> L{Service 3 erfolgreich?}
    L ---> |Ja| M[Ergebnis zurückgeben]
    L ---> |Nein| Q
    
    M --> N[Ende]
    Q --> O[Exception werfen]
    O --> P[Fehlerinformationen im Kontext speichern]
    P --> N
```

### Fehlerbehandlung in der Pipeline

```mermaid
flowchart TD
    A[Start] --> B{Fehlertyp?}
    
    B ---> |Validierungsfehler| C[Im Kontext speichern]
    B ---> |Technischer Fehler| D[Logging]
    B ---> |Geschäftsfehler| E[Alternative Verarbeitung]
    
    C --> F[PipelineException erstellen]
    D --> F
    E --> G[Alternative Route in Pipeline]
    
    F --> H[Pipeline abbrechen]
    G --> I[Weiter mit nächster Stage]
    
    H --> J[Exception nach oben propagieren]
    I --> K[Ergebnis markieren]
    
    J --> L[Ende]
    K --> L
```
