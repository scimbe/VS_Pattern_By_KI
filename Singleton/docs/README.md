# Singleton-Pattern Dokumentation

Diese Dokumentation enthält verschiedene Diagramme zur Veranschaulichung des Singleton-Patterns und der konkreten Implementierung in diesem Projekt.

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
    Client[Client] --> |verwendet| Singleton[Singleton Instance]

    subgraph "Singleton-Pattern Grundstruktur"
        Singleton
        SingletonClass[Singleton Class]
        SingletonClass --> |erzeugt und liefert| Singleton
    end

    subgraph "Projekt-Implementierungen"
        BasicSingleton[Basic Singleton] -.-> |implementiert| SingletonClass
        ThreadSafeSingleton[Thread-Safe Singleton] -.-> |implementiert| SingletonClass
        EnumSingleton[Enum-Based Singleton] -.-> |implementiert| SingletonClass
        EagerSingleton[Eager Singleton] -.-> |implementiert| SingletonClass
    end

    Client --> Main[Main Demo]
    Main --> |verwendet| BasicSingleton
    Main --> |verwendet| ThreadSafeSingleton
    Main --> |verwendet| EnumSingleton
```

## Klassendiagramme

### Allgemeines Singleton-Klassendiagramm

Das folgende Diagramm zeigt die allgemeine Struktur des Singleton-Patterns:

```mermaid
classDiagram
    class Client {
        +main()
    }

    class Singleton {
        -static instance: Singleton
        -Singleton()
        +static getInstance(): Singleton
        +operation()
    }

    Client --> Singleton : uses
```

### Basic Singleton-Klassendiagramm

```mermaid
classDiagram
    class BasicSingleton {
        -static instance: BasicSingleton
        -data: String
        -BasicSingleton()
        +static getInstance(): BasicSingleton
        +getData(): String
        +setData(data: String): void
        +getRemoteData(): String
    }
```

### Thread-Safe Singleton-Klassendiagramm

```mermaid
classDiagram
    class ThreadSafeSingleton {
        -static volatile instance: ThreadSafeSingleton
        -connectionState: String
        -ThreadSafeSingleton()
        +static getInstance(): ThreadSafeSingleton
        +getConnectionState(): String
        +executeRemoteOperation(operation: String): String
        +closeConnection(): void
    }
```

### Enum-Based Singleton-Klassendiagramm

```mermaid
classDiagram
    class EnumSingleton {
        <<enumeration>>
        INSTANCE
        -configuration: Map~String, String~
        -EnumSingleton()
        +getConfigValue(key: String): String
        +setConfigValue(key: String, value: String): void
        +getAllConfig(): Map~String, String~
        +applyConfigToDistributedSystem(): void
    }
```

## Sequenzdiagramme

### Basic Singleton-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Singleton as BasicSingleton
    
    Client->>+Singleton: getInstance()
    
    alt Instance existiert noch nicht
        Singleton->>Singleton: instance = new BasicSingleton()
    end
    
    Singleton-->>-Client: return instance
    
    Client->>+Singleton: setData(newData)
    Singleton->>Singleton: this.data = newData
    Singleton-->>-Client: return
    
    Client->>+Singleton: getData()
    Singleton-->>-Client: return data
```

### Thread-Safe Singleton-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client as Client (Thread 1)
    participant Client2 as Client (Thread 2)
    participant Singleton as ThreadSafeSingleton
    
    par Thread 1 and Thread 2
        Client->>+Singleton: getInstance()
        Client2->>+Singleton: getInstance()
        
        alt First check (unsynchronized)
            Singleton->>Singleton: instance == null?
        end
        
        Singleton->>Singleton: synchronized(ThreadSafeSingleton.class)
        
        alt Second check (synchronized)
            Singleton->>Singleton: instance == null?
            Singleton->>Singleton: instance = new ThreadSafeSingleton()
        end
        
        Singleton-->>-Client: return instance
        Singleton-->>-Client2: return instance
    end
    
    Client->>+Singleton: executeRemoteOperation("GetData")
    Singleton-->>-Client: return operation result
```

### Enum Singleton-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Enum as EnumSingleton.INSTANCE
    
    Client->>+Enum: getConfigValue("db.host")
    Enum-->>-Client: return "localhost"
    
    Client->>+Enum: setConfigValue("service.url", "https://new-api.example.com")
    Enum->>Enum: configuration.put(key, value)
    Enum-->>-Client: return
    
    Client->>+Enum: applyConfigToDistributedSystem()
    Enum->>Enum: simuliere Netzwerk-Broadcast
    Enum-->>-Client: return
```

## Zustandsdiagramme

### Singleton-Initialisierungszustandsdiagramm

```mermaid
stateDiagram-v2
    [*] --> Uninitialized
    
    Uninitialized --> InitializationInProgress: First getInstance() call
    
    InitializationInProgress --> Initialized: Initialization complete
    
    Initialized --> [*]
    
    note right of Uninitialized
        Instance is null
    end note
    
    note right of InitializationInProgress
        Constructor being executed
    end note
    
    note right of Initialized
        Singleton instance ready for use
    end note
```

### Thread-Safe Singleton-Zustandsdiagramm

```mermaid
stateDiagram-v2
    [*] --> NotInitialized
    
    NotInitialized --> CheckingInstance: Thread calls getInstance()
    
    CheckingInstance --> AcquiringLock: instance == null
    CheckingInstance --> ReturnInstance: instance != null
    
    AcquiringLock --> DoubleChecking: Lock acquired
    
    DoubleChecking --> Creating: still null
    DoubleChecking --> ReleasingLock: already created by other thread
    
    Creating --> ReleasingLock: new instance created
    
    ReleasingLock --> ReturnInstance: Lock released
    
    ReturnInstance --> [*]
```

## Aktivitätsdiagramme

### Singleton-Zugriff-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B{Instance existiert?}
    B -->|Ja| C[Gib Instanz zurück]
    B -->|Nein| D[Erzeuge neue Instanz]
    D --> C
    C --> E[Ende]
```

### Thread-Safe Singleton-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B{Erste Prüfung: Instance null?}
    B -->|Ja| C[Betreten des synchronized Blocks]
    B -->|Nein| G[Gib Instanz zurück]
    
    C --> D{Zweite Prüfung: Instance noch null?}
    D -->|Ja| E[Erzeuge neue Instanz]
    D -->|Nein| F[Verlasse synchronized Block]
    
    E --> F
    F --> G
    G --> H[Ende]
```

### Enum Singleton-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Zugriff auf EnumSingleton.INSTANCE]
    B --> C[JVM garantiert einmalige Initialisierung]
    C --> D[Konstruktor wird ausgeführt]
    D --> E[Initialisiere Konfiguration]
    E --> F[Instanz bereit für Verwendung]
    F --> G[Ende]
```