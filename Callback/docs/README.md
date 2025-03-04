# Callback-Pattern Dokumentation

Diese Dokumentation enthält verschiedene Diagramme zur Veranschaulichung des Callback-Patterns und der konkreten Implementierung in diesem Projekt.

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
    Client[Client] --> |verwendet| Callback[Callback Interface]

    subgraph "Callback-Pattern Grundstruktur"
        Callback
        AsyncOperation[Async Operation]
        AsyncOperation --> |ruft auf| Callback
    end

    subgraph "Projekt-Implementierungen"
        SimpleCallback[Simple Callback] -.-> |implementiert| Callback
        PollingCallback[Polling Callback] -.-> |implementiert| Callback
        WebhookCallback[Webhook Callback] -.-> |implementiert| Callback
        MessageCallback[Message-based Callback] -.-> |implementiert| Callback
        RetryCallback[Retry Callback] -.-> |implementiert| Callback
        FutureCallback[CompletableFuture Callback] -.-> |implementiert| Callback
    end

    Client --> Main[Main Demo]
    Main --> |verwendet| SimpleCallback
    Main --> |verwendet| PollingCallback
    Main --> |verwendet| RetryCallback
    Main --> |verwendet| FutureCallback
```
##
## Klassendiagramme

### Allgemeines Callback-Klassendiagramm

Das folgende Diagramm zeigt die allgemeine Struktur des Callback-Patterns:

```mermaid
classDiagram
    class Client {
        +executeOperation()
    }

    class Callback~T~ {
        <<interface>>
        +onSuccess(result: T)
        +onError(exception: Throwable)
    }

    class AsyncOperation~T~ {
        -operationId: String
        -status: OperationStatus
        -result: T
        -error: Throwable
        +execute(callback: Callback~T~)
        +executeAsync(): CompletableFuture~T~ 
        #doExecute(): T
    }

    class ConcreteCallback~T~ {
        +onSuccess(result: T)
        +onError(exception: Throwable)
    }

    Client --> Callback : uses
    Client --> AsyncOperation : executes
    AsyncOperation --> Callback : notifies
    ConcreteCallback ..|> Callback : implements
```

### Simple Callback-Klassendiagramm

```mermaid
classDiagram
    class Callback~T~ {
        <<interface>>
        +onSuccess(result: T)
        +onError(exception: Throwable)
    }

    class SimpleAsyncService {
        +processSynchronously(data: T, processor: Function~T,R~, callback: Callback~R~)
        +processAsynchronously(data: T, processor: Function~T,R~, callback: Callback~R~)
        +static processWithDelay(input: String): String
    }

    class SimpleCallback~T~ { 
        -name: String
        +onSuccess(result: T)
        +onError(exception: Throwable)
    }
    
    SimpleCallback ..|> Callback : implements
    SimpleAsyncService --> Callback : uses
    SimpleAsyncService --> AsyncOperation : creates
```

### Polling Callback-Klassendiagramm

```mermaid
classDiagram
    class PollingAsyncService {
        -operations: Map~String, OperationInfo~
        -executor: ExecutorService
        -scheduler: ScheduledExecutorService
        +startOperation(input: T, processor: Function~T,R~): String
        +getStatus(operationId: String): OperationStatus
        +getResult(operationId: String): R
        +getError(operationId: String): Throwable
        +waitForResult(operationId: String, timeout: long): R
        -scheduleCleanup(operationId: String, delayMs: long)
    }

    class OperationInfo~R~ {
        -status: OperationStatus
        -result: R
        -error: Throwable
        -completionTime: long
    }
    
    class OperationStatus { 
        <<enumeration>>
        PENDING
        RUNNING
        COMPLETED
        FAILED
    }

    PollingAsyncService *-- OperationInfo : contains
    OperationInfo --> OperationStatus : uses
```

### Retry Callback-Klassendiagramm

```mermaid
classDiagram
    class Callback~T~ {
        <<interface>>
        +onSuccess(result: T)
        +onError(exception: Throwable)
    }

    class RetryCallback~T~ {
        -originalCallback: Callback~T~
        -operation: Supplier~CompletableFuture~T~~
        -retry: Retry
        -scheduler: ScheduledExecutorService
        +execute()
        +onSuccess(result: T)
        +onError(exception: Throwable)
        +static createDefaultRetry(): RetryConfig
        +static createCustomRetry(maxAttempts: int, waitDurationMs: long, retryPredicate: Predicate~Throwable~): RetryConfig
    }

    RetryCallback ..|> Callback : implements
    RetryCallback --> Callback : delegates to
```

### CompletableFuture Callback-Klassendiagramm

```mermaid
classDiagram
    class CallbackExample {
        -executor: ExecutorService
        +performSynchronousOperation(data: String, callback: Callback~String~)
        +performAsynchronousOperation(data: String, callback: Callback~String~)
        +performAsynchronousOperationWithFuture(data: String): CompletableFuture~String~
        +performProgressOperation(data: String, progressCallback: Callback~Integer~, finalCallback: Callback~String~)
        +performNestedCallbacks(data: String, finalCallback: Callback~String~)
        +performChainedFutures(data: String): CompletableFuture~String~
    }

    class CompletableFuture~T~ {
        +thenApply(Function~T,U~): CompletableFuture~U~
        +thenAccept(Consumer~T~): CompletableFuture~Void~
        +thenCompose(Function~T,CompletableFuture~U~~): CompletableFuture~U~
        +exceptionally(Function~Throwable,T~): CompletableFuture~T~
        +get(): T
        +get(timeout: long, unit: TimeUnit): T
    } 
    
    CallbackExample --> CompletableFuture : creates
```

## Sequenzdiagramme

### Simple Callback-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Service as SimpleAsyncService
    participant AsyncOp as AsyncOperation
    participant CB as Callback

    Client->>+Service: processAsynchronously(data, processor, callback)
    Service->>+AsyncOp: create
    Service->>AsyncOp: execute(callback)
    AsyncOp->>AsyncOp: submit to executor
    Service-->>-Client: return

    Note over AsyncOp: Asynchrone Verarbeitung
    AsyncOp->>AsyncOp: doExecute()

    alt Success
        AsyncOp->>+CB: onSuccess(result)
        CB-->>-AsyncOp: return 
    else Error
        AsyncOp->>+CB: onError(exception)
        CB-->>-AsyncOp: return
    end
    AsyncOp-->>-Service: end
```

### Polling Callback-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Service as PollingAsyncService
    participant Op as OperationInfo

    Client->>+Service: startOperation(input, processor)
    Service->>+Op: create
    Service->>Op: store in operations map
    Service->>Service: submit to executor
    Service-->>-Client: return operationId

    Note over Service, Op: Asynchrone Verarbeitung
    Service->>Op: process data
    Service->>Op: update status and result/error

    loop Polling
        Client->>+Service: getStatus(operationId)
        Service->>Op: get status
        Service-->>-Client: return status

        alt Status == COMPLETED
            Client->>+Service: getResult(operationId)
            Service->>Op: get result
            Service-->>-Client: return result
        else Status == FAILED
            Client->>+Service: getError(operationId)
            Service->>Op: get error
            Service-->>-Client: return error
        else Status == RUNNING or PENDING
            Note over Client: Wait and retry
        end
    end

    Service->>Service: scheduleCleanup(operationId)
```

### Retry Callback-Sequenzdiagramm

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
            Retry-->>+Original: onSuccess(result) 
            Original-->>-Retry: return
            Retry-->>-Client: return
        else Error
            Op-->>-Retry: exception
            
            alt Retry limit not reached
                Note over Retry: Wait backoff time
                Retry->>+Retry: retry attempt
            else Max retries reached
                Retry-->>+Original: onError(exception)
                Original-->>-Retry: return
                Retry-->>-Client: return
            end
        end
    end
```

### CompletableFuture Callback-Sequenzdiagramm 

```mermaid
sequenceDiagram
    participant Client
    participant Example as CallbackExample
    participant Future as CompletableFuture
    
    Client->>+Example: performChainedFutures(data) 
    Example->>+Future: performAsynchronousOperationWithFuture(data)
    Example-->>-Client: return CompletableFuture

    Note over Future: Async execution

    Future->>+Future: thenComposeAsync(firstResult)
    Future->>+Future: performAsynchronousOperationWithFuture(firstResult)

    Future->>+Future: thenComposeAsync(secondResult)
    Future->>+Future: performAsynchronousOperationWithFuture(secondResult)

    Future->>+Future: thenApplyAsync(finalResult)

    Client->>+Future: thenAccept(result)
    Future-->>-Client: return

    alt Success
        Future-->>Client: result accepted
    else Error
        Client->>+Future: exceptionally(ex)
        Future-->>-Client: error handled
    end
```

## Zustandsdiagramme

### Callback Operationszustandsdiagramm

```mermaid
stateDiagram-v2
    [*] --> PENDING

    PENDING --> RUNNING: execute() called

    RUNNING --> COMPLETED: operation successful
    RUNNING --> FAILED: operation error

    COMPLETED --> [*]: onSuccess() called
    FAILED --> [*]: onError() called 
```

### Retry Callback-Zustandsdiagramm

```mermaid
stateDiagram-v2
    [*] --> Attempt1

    Attempt1 --> Success: Operation successful
    Attempt1 --> Backoff1: Operation failed

    Backoff1 --> Attempt2: After backoff time

    Attempt2 --> Success: Operation successful
    Attempt2 --> Backoff2: Operation failed

    Backoff2 --> Attempt3: After backoff time

    Attempt3 --> Success: Operation successful
    Attempt3 --> Failed: Operation failed 
    
    Success --> [*]: onSuccess() called
    Failed --> [*]: onError() called
```

## Aktivitätsdiagramme

### Grundlegendes Callback-Aktivitätsdiagramm

```mermaid
graph TD
    A[Start] --> B{Asynchron?}
    B -->|Nein| C[Führe Operation synchron aus]
    B -->|Ja| D[Erstelle Task im Thread-Pool] 

    C --> E{Erfolgreich?}
    D --> F[Operation wird asynchron ausgeführt]
    F --> E

    E -->|Ja| G[Rufe onSuccess() auf]
    E -->|Nein| H[Rufe onError() auf]

    G --> I[Ende]
    H --> I
```

### Polling Callback-Aktivitätsdiagramm

```mermaid
graph TD
    A[Client: Start Operation] --> B[Server: Starte asynchrone Verarbeitung]
    B --> C[Server: Gib Operation-ID zurück]
    C --> D[Client: Beginne Polling]

    D --> E{Status abfragen}
    E --> F{Status}

    F -->|PENDING| G[Warte]
    F -->|RUNNING| G
    F -->|COMPLETED| H[Hole Ergebnis]
    F -->|FAILED| I[Hole Fehler]

    G --> E
    H --> J[Verarbeite Ergebnis]
    I --> K[Behandle Fehler]

    J --> L[Ende]
    K --> L
```

### Moderne CompletableFuture-Aktivitätsdiagramm

```mermaid
graph TD
    A[Start] --> B[Erstelle CompletableFuture]
    B --> C[Führe Operation asynchron aus]
    C --> D{Erfolgreich?}

    D -->|Ja| E[Vervollständige Future mit Ergebnis]
    D -->|Nein| F[Vervollständige Future mit Exception]

    E --> G[thenApply/thenAccept/thenCompose]
    F --> H[exceptionally/handle]

    G --> I[Verkettete Operationen]
    H --> I

    I --> J{Abgeschlossen?}
    J -->|Nein| I
    J -->|Ja| K[Ende]
```
