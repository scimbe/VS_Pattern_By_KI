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
    Client[Client] --> |verwendet| Pipeline[Pipeline Interface]

    subgraph "Pipeline-Pattern Grundstruktur"
        Pipeline
        Stage[Pipeline Stage]
        Context[Pipeline Context]
        Pipeline --> |besteht aus| Stage
        Pipeline --> |verwendet| Context
        Stage --> |verarbeitet| Context
    end

    subgraph "Projekt-Implementierungen"
        LinearPipeline[Linear Pipeline] -.-> |implementiert| Pipeline
        ParallelPipeline[Parallel Pipeline] -.-> |implementiert| Pipeline
        ConditionalPipeline[Conditional Pipeline] -.-> |implementiert| Pipeline
        DistributedPipeline[Distributed Pipeline] -.-> |implementiert| Pipeline
        EventDrivenPipeline[Event-driven Pipeline] -.-> |implementiert| Pipeline
    end

    Client --> Main[Main Demo]
    Main --> |verwendet| LinearPipeline
    Main --> |verwendet| ParallelPipeline
    Main --> |verwendet| ConditionalPipeline
```

## Klassendiagramme

### Allgemeines Pipeline-Klassendiagramm

Das folgende Diagramm zeigt die allgemeine Struktur des Pipeline-Patterns:

```mermaid
classDiagram
    class Client {
        +processPipeline()
    }

    class Pipeline~C~ {
        <<interface>>
        +addStage(stage: Stage~C~): Pipeline~C~
        +process(context: C): C
    }

    class Stage~C~ {
        <<interface>>
        +process(context: C): C
    }

    class Context {
        -data: Map~String, Object~
        +get(key: String): Object
        +set(key: String, value: Object): void
        +containsKey(key: String): boolean
    }

    class ConcreteStage~C~ {
        +process(context: C): C
    }

    class ConcretePipeline~C~ {
        -stages: List~Stage~C~~
        +addStage(stage: Stage~C~): Pipeline~C~
        +process(context: C): C
    }

    Client --> Pipeline : uses
    Pipeline --> Stage : contains
    Stage --> Context : processes
    ConcreteStage ..|> Stage : implements
    ConcretePipeline ..|> Pipeline : implements
```

### Lineare Pipeline-Klassendiagramm

```mermaid
classDiagram
    class Pipeline~C~ {
        <<interface>>
        +addStage(stage: Stage~C~): Pipeline~C~
        +process(context: C): C
    }

    class Stage~C~ {
        <<interface>>
        +process(context: C): C
    }

    class LinearPipeline~C~ {
        -stages: List~Stage~C~~
        +addStage(stage: Stage~C~): Pipeline~C~
        +process(context: C): C
    }

    class SimpleStage~C~ {
        -processor: Function~C, C~
        +process(context: C): C
    }
    
    LinearPipeline ..|> Pipeline : implements
    SimpleStage ..|> Stage : implements
    LinearPipeline --> Stage : contains
```

### Parallele Pipeline-Klassendiagramm

```mermaid
classDiagram
    class Pipeline~C~ {
        <<interface>>
        +addStage(stage: Stage~C~): Pipeline~C~
        +process(context: C): C
    }

    class ParallelPipeline~C~ {
        -stages: List~Stage~C~~
        -executor: ExecutorService
        +addStage(stage: Stage~C~): Pipeline~C~
        +process(context: C): C
        +processAsync(context: C): CompletableFuture~C~
    }

    class Context {
        -data: Map~String, Object~
        -threadSafeData: ConcurrentHashMap~String, Object~
        +get(key: String): Object
        +set(key: String, value: Object): void
        +containsKey(key: String): boolean
    }
    
    ParallelPipeline ..|> Pipeline : implements
    ParallelPipeline --> Context : processes
```

### Conditional Pipeline-Klassendiagramm

```mermaid
classDiagram
    class ConditionalPipeline~C~ {
        -defaultStages: List~Stage~C~~
        -conditions: Map~Predicate~C~, List~Stage~C~~~
        +addStage(stage: Stage~C~): Pipeline~C~
        +addConditionalStage(condition: Predicate~C~, stage: Stage~C~): ConditionalPipeline~C~
        +process(context: C): C
    }

    class ConditionalStage~C~ {
        -condition: Predicate~C~
        -trueStage: Stage~C~
        -falseStage: Stage~C~
        +process(context: C): C
    }
    
    ConditionalPipeline ..|> Pipeline : implements
    ConditionalStage ..|> Stage : implements
```

## Sequenzdiagramme

### Lineare Pipeline-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Pipeline as LinearPipeline
    participant Stage1
    participant Stage2
    participant Stage3
    participant Context

    Client->>+Pipeline: process(context)
    Pipeline->>+Stage1: process(context)
    Stage1->>Context: get/set data
    Stage1-->>-Pipeline: return modified context
    
    Pipeline->>+Stage2: process(context)
    Stage2->>Context: get/set data
    Stage2-->>-Pipeline: return modified context
    
    Pipeline->>+Stage3: process(context)
    Stage3->>Context: get/set data
    Stage3-->>-Pipeline: return modified context
    
    Pipeline-->>-Client: return final context
```

### Parallele Pipeline-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Pipeline as ParallelPipeline
    participant Executor
    participant Stage1
    participant Stage2
    participant Stage3
    participant Context

    Client->>+Pipeline: processAsync(context)
    Pipeline->>Pipeline: create shallow copy of context for each stage
    
    Pipeline->>+Executor: submit Stage1 task
    Pipeline->>+Executor: submit Stage2 task
    Pipeline->>+Executor: submit Stage3 task
    
    par Execution in parallel
        Executor->>+Stage1: process(contextCopy1)
        Stage1->>Context: get/set data
        Stage1-->>-Executor: return modified contextCopy1
        
        Executor->>+Stage2: process(contextCopy2)
        Stage2->>Context: get/set data
        Stage2-->>-Executor: return modified contextCopy2
        
        Executor->>+Stage3: process(contextCopy3)
        Stage3->>Context: get/set data
        Stage3-->>-Executor: return modified contextCopy3
    end
    
    Pipeline->>Pipeline: merge all modified contexts
    Pipeline-->>-Client: return CompletableFuture with final context
```

## Zustandsdiagramme

### Pipeline-Verarbeitungszustandsdiagramm

```mermaid
stateDiagram-v2
    [*] --> Initialized
    
    Initialized --> Processing: process() called
    
    Processing --> StageExecution: for each stage
    StageExecution --> StageExecution: next stage
    StageExecution --> Completed: all stages processed
    StageExecution --> Failed: stage error
    
    Completed --> [*]: return context
    Failed --> [*]: throw exception
```

### Parallele Pipeline-Zustandsdiagramm

```mermaid
stateDiagram-v2
    [*] --> Initialized
    
    Initialized --> ContextCopying: processAsync() called
    ContextCopying --> ParallelExecution: contexts prepared
    
    ParallelExecution --> Stage1Execution
    ParallelExecution --> Stage2Execution
    ParallelExecution --> Stage3Execution
    
    Stage1Execution --> ResultCollecting: stage completed
    Stage2Execution --> ResultCollecting: stage completed
    Stage3Execution --> ResultCollecting: stage completed
    
    ResultCollecting --> MergeResults: all stages completed
    ResultCollecting --> HandleFailure: any stage failed
    
    MergeResults --> Completed: merge successful
    HandleFailure --> Failed: set exception on future
    
    Completed --> [*]: complete future
    Failed --> [*]: complete future exceptionally
```

## Aktivitätsdiagramme

### Grundlegendes Pipeline-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Initialisiere Pipeline mit Stages]
    B --> C[Initialisiere Kontext mit Eingabedaten]
    C --> D[Übergebe Kontext an Pipeline]
    D --> E[Erstes Stage verarbeitet Kontext]
    E --> F[Nächstes Stage verarbeitet Kontext]
    F --> G{Weitere Stages?}
    G -->|Ja| F
    G -->|Nein| H[Ergebniskontext zurückgeben]
    H --> I[Ende]
```

### Verzweigte Pipeline-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Initialisiere Pipeline]
    B --> C[Füge bedingte Stages hinzu]
    C --> D[Initialisiere Kontext]
    D --> E[Übergebe Kontext an Pipeline]
    E --> F{Bedingung prüfen}
    F -->|Wahr| G[Verzweigung A ausführen]
    F -->|Falsch| H[Verzweigung B ausführen]
    G --> I[Ergebnis in Kontext speichern]
    H --> I
    I --> J[Nächster Schritt oder Ende]
    J --> K{Weitere Schritte?}
    K -->|Ja| F
    K -->|Nein| L[Ende]
```