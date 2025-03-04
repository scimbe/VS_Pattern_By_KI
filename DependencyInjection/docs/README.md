# Dependency Injection Pattern Dokumentation

Diese Dokumentation enthält verschiedene Diagramme zur Veranschaulichung des Dependency Injection Patterns und der konkreten Implementierung in diesem Projekt.

## Inhalt

- [Komponentendiagramm](#komponentendiagramm)
- [Klassendiagramme](#klassendiagramme)
- [Sequenzdiagramme](#sequenzdiagramme)
- [Aktivitätsdiagramme](#aktivitätsdiagramme)

## Komponentendiagramm

Das folgende Diagramm zeigt die Hauptkomponenten des Projekts und ihre Beziehungen zueinander:

```mermaid
graph TD
    Client[Client] --> |verwendet| IService[Service Interface]
    
    subgraph "Dependency Injection Grundstruktur"
        IService
        ServiceImpl[Service Implementation]
        ServiceImpl -.-> |implementiert| IService
        DependencyProvider[Dependency Provider]
        DependencyProvider --> |stellt bereit| IService
    end
    
    subgraph "Projekt-Implementierungen"
        ManualDI[Manual DI] -.-> |implementiert| DependencyProvider
        SpringDI[Spring DI] -.-> |implementiert| DependencyProvider
        GuiceDI[Guice DI] -.-> |implementiert| DependencyProvider
        ServiceLocator[Service Locator] -.-> |Alternative zu| DependencyProvider
    end
    
    ManualDIClient[ManualDIClient] --> ManualDI
    SpringDIClient[SpringDIClient] --> SpringDI
    GuiceDIClient[GuiceDIClient] --> GuiceDI
    ServiceLocatorClient[ServiceLocatorClient] --> ServiceLocator
    
    Client --> |verwendet| ManualDIClient
    Client --> |verwendet| SpringDIClient
    Client --> |verwendet| GuiceDIClient
    Client --> |verwendet| ServiceLocatorClient
```

## Klassendiagramme

### Allgemeines Dependency Injection Klassendiagramm

Das folgende Diagramm zeigt die allgemeine Struktur des Dependency Injection Patterns:

```mermaid
classDiagram
    class Client {
        -service: Service
        +Client(service: Service)
        +executeOperation()
    }

    class Service {
        <<interface>>
        +executeOperation()
    }

    class ServiceImpl {
        +executeOperation()
    }

    class DependencyProvider {
        +getService(): Service
    }

    Client --> Service: verwendet
    ServiceImpl ..|> Service: implementiert
    DependencyProvider --> ServiceImpl: erstellt
    Client ..> DependencyProvider: erhält Abhängigkeiten
```

### Manual Dependency Injection Klassendiagramm

```mermaid
classDiagram
    class ManualDIClient {
        -service: RemoteService
        -secondaryService: RemoteService
        +ManualDIClient(service: RemoteService)
        +setSecondaryService(secondaryService: RemoteService)
        +executeOperation(parameter: String): String
        +executeSecondaryOperation(parameter: String): String
        +getPrimaryServiceName(): String
        +getSecondaryServiceName(): String
    }

    class RemoteService {
        <<interface>>
        +executeOperation(): String
        +executeOperation(parameter: String): String
        +getServiceName(): String
    }

    class RemoteServiceImpl {
        -serviceName: String
        -latency: int
        +RemoteServiceImpl(serviceName: String)
        +RemoteServiceImpl(serviceName: String, latency: int)
        +executeOperation(): String
        +executeOperation(parameter: String): String
        +getServiceName(): String
    }

    class ServiceFactory {
        +createSimpleService(serviceName: String): RemoteService
        +createServiceWithLatency(serviceName: String, latency: int): RemoteService
        +createFaultTolerantService(primaryServiceName: String, fallbackServiceName: String, maxRetries: int): RemoteService
        +createFailingService(serviceName: String, failureRate: double): RemoteService
    }

    ManualDIClient --> RemoteService
    RemoteServiceImpl ..|> RemoteService
    ServiceFactory ..> RemoteServiceImpl: erstellt
    ServiceFactory ..> FaultTolerantService: erstellt
    ManualDIClient ..> ServiceFactory: verwendet
```

### Spring Dependency Injection Klassendiagramm

```mermaid
classDiagram
    class SpringDIClient {
        -primaryService: RemoteService
        -secondaryService: RemoteService
        -slowService: RemoteService
        -unreliableService: RemoteService
        -faultTolerantService: RemoteService
        +SpringDIClient(primaryService: RemoteService)
        +setFaultTolerantService(faultTolerantService: RemoteService)
        +executeServices(parameter: String)
        +listAllServices(): String
    }

    class SpringConfig {
        +primaryService(): RemoteService
        +backupService(): RemoteService
        +faultTolerantService(primaryService: RemoteService, secondaryService: RemoteService): RemoteService
        +highLatencyService(): RemoteService
        +failingService(): RemoteService
    }

    class RemoteService {
        <<interface>>
        +executeOperation(): String
        +executeOperation(parameter: String): String
        +getServiceName(): String
    }

    SpringDIClient --> RemoteService: verwendet
    SpringConfig ..> RemoteService: erstellt Beans
    SpringDIClient ..> SpringConfig: erhält Beans
```

### Guice Dependency Injection Klassendiagramm

```mermaid
classDiagram
    class GuiceDIClient {
        -primaryService: RemoteService
        -secondaryService: RemoteService
        -slowService: RemoteService
        -unreliableService: RemoteService
        -faultTolerantService: RemoteService
        +GuiceDIClient(faultTolerantService: RemoteService)
        +executeServices(parameter: String)
        +listAllServices(): String
    }

    class GuiceModule {
        +configure()
        +provideFaultTolerantService(primary: RemoteService, secondary: RemoteService): RemoteService
        +provideUnreliableService(): RemoteService
    }

    class RemoteService {
        <<interface>>
        +executeOperation(): String
        +executeOperation(parameter: String): String
        +getServiceName(): String
    }

    GuiceDIClient --> RemoteService: verwendet
    GuiceModule ..> RemoteService: bindet Provider
    GuiceDIClient ..> GuiceModule: erhält injizierte Abhängigkeiten
```

### Service Locator Klassendiagramm

```mermaid
classDiagram
    class ServiceLocator {
        -static instance: ServiceLocator
        -services: Map~String, RemoteService~
        -ServiceLocator()
        +static getInstance(): ServiceLocator
        +registerService(serviceName: String, service: RemoteService)
        +getService(serviceName: String): RemoteService
        +unregisterService(serviceName: String): boolean
        +getAllServiceNames(): Set~String~
        +hasService(serviceName: String): boolean
        +getServiceCount(): int
        +clearServices()
    }

    class ServiceLocatorClient {
        -serviceLocator: ServiceLocator
        +ServiceLocatorClient()
        +executeServiceOperation(serviceName: String, parameter: String): String
        +registerNewService(serviceName: String, service: RemoteService)
        +listAvailableServices(): String
    }

    class ServiceNotFoundException {
        +ServiceNotFoundException(message: String)
        +ServiceNotFoundException(message: String, cause: Throwable)
    }

    class RemoteService {
        <<interface>>
        +executeOperation(): String
        +executeOperation(parameter: String): String
        +getServiceName(): String
    }

    ServiceLocatorClient --> ServiceLocator: verwendet
    ServiceLocator --> RemoteService: verwaltet
    ServiceLocator ..> ServiceNotFoundException: wirft
```

## Sequenzdiagramme

### Manual Dependency Injection Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Main
    participant Factory as ServiceFactory
    participant Client as ManualDIClient
    participant Primary as PrimaryService
    participant Secondary as SecondaryService

    Main->>Factory: createSimpleService("PrimaryService")
    Factory-->>Main: primaryService
    
    Main->>Factory: createServiceWithLatency("SecondaryService", 150)
    Factory-->>Main: secondaryService
    
    Main->>+Client: new ManualDIClient(primaryService)
    Client-->>-Main: client
    
    Main->>+Client: setSecondaryService(secondaryService)
    Client-->>-Main: void
    
    Main->>+Client: executeOperation("parameter")
    Client->>+Primary: executeOperation("parameter")
    Primary-->>-Client: result
    Client-->>-Main: result
    
    Main->>+Client: executeSecondaryOperation("parameter")
    Client->>+Secondary: executeOperation("parameter")
    Secondary-->>-Client: result
    Client-->>-Main: result
```

### Spring Dependency Injection Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Main
    participant Context as Spring Context
    participant Config as SpringConfig
    participant Client as SpringDIClient
    participant PrimaryService
    participant SecondaryService

    Main->>+Context: new AnnotationConfigApplicationContext(SpringConfig.class)
    Context->>+Config: primaryService()
    Config-->>-Context: primaryServiceBean
    Context->>+Config: backupService()
    Config-->>-Context: secondaryServiceBean
    Context->>+Config: faultTolerantService(primaryService, secondaryService)
    Config-->>-Context: faultTolerantServiceBean
    Context-->>-Main: context
    
    Main->>+Context: getBean(SpringDIClient.class)
    Context-->>-Main: springDIClient
    
    Main->>+Client: listAllServices()
    Client-->>-Main: serviceList
    
    Main->>+Client: executeServices("parameter")
    Client->>+PrimaryService: executeOperation("parameter")
    PrimaryService-->>-Client: result1
    Client->>+SecondaryService: executeOperation("parameter")
    SecondaryService-->>-Client: result2
    Client-->>-Main: void
```

### Guice Dependency Injection Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Main
    participant Injector
    participant Module as GuiceModule
    participant Client as GuiceDIClient
    participant Service

    Main->>+Injector: Guice.createInjector(new GuiceModule())
    Injector->>+Module: configure()
    Module-->>-Injector: void
    Injector-->>-Main: injector
    
    Main->>+Injector: getInstance(GuiceDIClient.class)
    Injector->>+Client: new GuiceDIClient(faultTolerantService)
    Client-->>-Injector: client
    Injector-->>-Main: client
    
    Main->>+Client: listAllServices()
    Client-->>-Main: serviceList
    
    Main->>+Client: executeServices("parameter")
    Client->>+Service: executeOperation("parameter")
    Service-->>-Client: result
    Client-->>-Main: void
```

### Service Locator Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Main
    participant Locator as ServiceLocator
    participant Client as ServiceLocatorClient
    participant Service as RemoteService

    Main->>+Locator: getInstance()
    Locator-->>-Main: serviceLocator
    
    Main->>+Locator: registerService("defaultService", new RemoteServiceImpl())
    Locator-->>-Main: void
    
    Main->>+Client: new ServiceLocatorClient()
    Client->>+Locator: getInstance()
    Locator-->>-Client: serviceLocator
    Client-->>-Main: client
    
    Main->>+Client: executeServiceOperation("defaultService", "parameter")
    Client->>+Locator: getService("defaultService")
    Locator-->>-Client: service
    Client->>+Service: executeOperation("parameter")
    Service-->>-Client: result
    Client-->>-Main: result
```

## Aktivitätsdiagramme

### Grundlegendes Dependency Injection Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Definiere Service-Schnittstelle]
    B --> C[Implementiere Service]
    C --> D[Erstelle Dependency Provider]
    D --> E[Konfiguriere Abhängigkeiten]
    E --> F[Erzeuge Client mit injizierten Abhängigkeiten]
    F --> G[Client nutzt Service ohne Kenntnis der konkreten Implementierung]
    G --> H[Ende]
```

### Spring Dependency Injection Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Erstelle Spring-Konfigurationsklasse]
    B --> C[Definiere Beans]
    C --> D[Erstelle Client-Klasse mit annotierten Abhängigkeiten]
    D --> E[Erstelle Spring-Kontext]
    E --> F[Spring injiziert Abhängigkeiten automatisch]
    F --> G[Client nutzt injizierte Services]
    G --> H[Ende]
```

### Guice Dependency Injection Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Erstelle Guice-Modul]
    B --> C[Konfiguriere Bindings und Provider]
    C --> D[Erstelle Client mit annotierter Injektion]
    D --> E[Erstelle Guice-Injector]
    E --> F[Injector injiziert Abhängigkeiten]
    F --> G[Client nutzt injizierte Services]
    G --> H[Ende]
```

### Service Locator Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Erstelle Service-Locator-Singleton]
    B --> C[Registriere Services im Locator]
    C --> D[Client fordert Service vom Locator an]
    D --> E{Service gefunden?}
    E -->|Ja| F[Client nutzt Service]
    E -->|Nein| G[Wirf ServiceNotFoundException]
    F --> H[Ende]
    G --> H
```