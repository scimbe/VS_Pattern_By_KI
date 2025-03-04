# Facade-Pattern Dokumentation

Diese Dokumentation enthält verschiedene Diagramme zur Veranschaulichung des Facade-Patterns und der konkreten Implementierung in diesem Projekt.

## Inhalt

- [Komponentendiagramm](#komponentendiagramm)
- [Klassendiagramme](#klassendiagramme)
- [Sequenzdiagramme](#sequenzdiagramme)
- [Strukturdiagramme](#strukturdiagramme)
- [Aktivitätsdiagramme](#aktivitätsdiagramme)

## Komponentendiagramm

Das folgende Diagramm zeigt die Hauptkomponenten des Projekts und ihre Beziehungen zueinander:

```mermaid
graph TD
    Client[Client] --> |verwendet| Facade[Facade]
    
    subgraph "Facade-Pattern Grundstruktur"
        Facade --> SubSystemA[SubsystemComponentA]
        Facade --> SubSystemB[SubsystemComponentB]
        Facade --> SubSystemC[SubsystemComponentC]
    end
    
    subgraph "Projekt-Implementierungen"
        BasicFacade[Basic Facade] -.-> |implementiert| Facade
        RemoteFacade[Remote Service Facade] -.-> |implementiert| Facade
    end
    
    Client --> Main[Main Demo]
    Main --> |verwendet| BasicFacade
    Main --> |verwendet| RemoteFacade
```

## Klassendiagramme

### Allgemeines Facade-Klassendiagramm

Das folgende Diagramm zeigt die allgemeine Struktur des Facade-Patterns:

```mermaid
classDiagram
    class Client {
        +main()
    }
    
    class Facade {
        -subSystemA: SubsystemComponentA
        -subSystemB: SubsystemComponentB
        -subSystemC: SubsystemComponentC
        +operation1()
        +operation2()
    }
    
    class SubsystemComponentA {
        +methodA1()
        +methodA2()
    }
    
    class SubsystemComponentB {
        +methodB1()
        +methodB2()
    }
    
    class SubsystemComponentC {
        +methodC1()
        +methodC2()
    }
    
    Client --> Facade : uses
    Facade --> SubsystemComponentA : uses
    Facade --> SubsystemComponentB : uses
    Facade --> SubsystemComponentC : uses
```

### Basic Facade-Klassendiagramm

```mermaid
classDiagram
    class BasicFacade {
        -componentA: SubsystemComponentA
        -componentB: SubsystemComponentB
        -componentC: SubsystemComponentC
        +BasicFacade()
        +BasicFacade(componentA, componentB, componentC)
        +processOperation(input: String): String
        +simpleProcess(input: String): String
        +batchProcess(inputs: String[]): String[]
    }
    
    class SubsystemComponentA {
        +preprocess(input: String): String
        +quickPreprocess(input: String): String
        +validate(preprocessedData: String): String
    }
    
    class SubsystemComponentB {
        -processedCount: int
        +analyze(preprocessedData: String): AnalysisResult
        +recordInput(data: String)
        +getProcessedCount(): int
    }
    
    class SubsystemComponentC {
        +process(preprocessedData, analysisResult): String
        +processWithValidation(validatedData, analysisResult): String
        +quickProcess(preprocessedData): String
    }
    
    class AnalysisResult {
        -confidence: double
        -category: String
        -priority: int
        +getConfidence(): double
        +getCategory(): String
        +getPriority(): int
    }
    
    BasicFacade --> SubsystemComponentA : uses
    BasicFacade --> SubsystemComponentB : uses
    BasicFacade --> SubsystemComponentC : uses
    SubsystemComponentB ..> AnalysisResult : creates
```

### Remote Service Facade-Klassendiagramm

```mermaid
classDiagram
    class RemoteServiceFacade {
        -userService: UserService
        -orderService: OrderService
        -inventoryService: InventoryService
        -notificationService: NotificationService
        +RemoteServiceFacade()
        +RemoteServiceFacade(userServiceUrl, orderServiceUrl, inventoryServiceUrl, notificationServiceUrl)
        +processOrder(userId, productIds, quantities): OrderResult
        +getUserProfile(userId): UserProfile
        +checkProductAvailability(productIds): int[]
        +cancelOrder(orderId): boolean
    }
    
    class UserService {
        -serviceUrl: String
        +getUser(userId): User
        +updateUser(user): boolean
        +createUser(name, email): String
    }
    
    class OrderService {
        -serviceUrl: String
        +createOrder(userId, productIds, quantities): String
        +getOrderDetails(orderId): Order
        +getUserOrders(userId): String[]
        +cancelOrder(orderId): boolean
    }
    
    class InventoryService {
        -serviceUrl: String
        +checkAvailability(productId): int
        +updateInventory(productId, quantityChange): boolean
        +reserveInventory(productId, quantity): String
    }
    
    class NotificationService {
        -serviceUrl: String
        +sendOrderConfirmation(userId, orderId): boolean
        +sendOrderCancellation(userId, orderId): boolean
        +sendNotification(userId, subject, message): boolean
    }
    
    RemoteServiceFacade --> UserService : uses
    RemoteServiceFacade --> OrderService : uses
    RemoteServiceFacade --> InventoryService : uses
    RemoteServiceFacade --> NotificationService : uses
```

## Sequenzdiagramme

### Basic Facade-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Facade as BasicFacade
    participant CompA as SubsystemComponentA
    participant CompB as SubsystemComponentB
    participant CompC as SubsystemComponentC
    
    Client->>+Facade: processOperation(input)
    Facade->>+CompA: preprocess(input)
    CompA-->>-Facade: preprocessedData
    
    Facade->>+CompB: analyze(preprocessedData)
    CompB-->>-Facade: analysisResult
    
    alt confidence > 0.8
        Facade->>+CompC: process(preprocessedData, analysisResult)
        CompC-->>-Facade: result
    else confidence <= 0.8
        Facade->>+CompA: validate(preprocessedData)
        CompA-->>-Facade: validatedData
        Facade->>+CompC: processWithValidation(validatedData, analysisResult)
        CompC-->>-Facade: result
    end
    
    Facade-->>-Client: result
```

### Remote Service Facade-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client
    participant Facade as RemoteServiceFacade
    participant UserSvc as UserService
    participant InvSvc as InventoryService
    participant OrderSvc as OrderService
    participant NotifSvc as NotificationService
    
    Client->>+Facade: processOrder(userId, productIds, quantities)
    
    Facade->>+UserSvc: getUser(userId)
    UserSvc-->>-Facade: user
    
    loop for each product
        Facade->>+InvSvc: checkAvailability(productId)
        InvSvc-->>-Facade: availability
    end
    
    Facade->>+OrderSvc: createOrder(userId, productIds, quantities)
    OrderSvc-->>-Facade: orderId
    
    loop for each product
        Facade->>+InvSvc: updateInventory(productId, -quantity)
        InvSvc-->>-Facade: success
    end
    
    Facade->>+NotifSvc: sendOrderConfirmation(userId, orderId)
    NotifSvc-->>-Facade: success
    
    Facade-->>-Client: OrderResult
```

## Strukturdiagramme

### Facade-Pattern Strukturdiagramm

```mermaid
graph TD
    A[Facade-Pattern] --> B[Vorteile]
    A --> C[Nachteile]
    A --> D[Varianten]
    
    B --> B1[Vereinfachte Schnittstelle]
    B --> B2[Entkopplung von Client und Subsystem]
    B --> B3[Isolierung von Änderungen]
    B --> B4[Einheitlicher Zugriffspunkt]
    
    C --> C1[Zusätzlicher Abstraktionsoverhead]
    C --> C2[Mögliche Gottklasse]
    C --> C3[Geringere Flexibilität]
    C --> C4[Zusätzlicher Fehlerpunkt]
    
    D --> D1[Standard-Facade]
    D --> D2[Remote Facade]
    D --> D3[API Gateway]
    D --> D4[System Integration Facade]
```

### Facade vs. Alternative Patterns

```mermaid
graph LR
    A[Strukturelle Patterns] --> B[Facade]
    A --> C[Adapter]
    A --> D[Proxy]
    A --> E[Decorator]
    
    B --- B1[Vereinfacht komplexes Subsystem]
    C --- C1[Passt Schnittstellen an]
    D --- D1[Kontrolliert Zugriff]
    E --- E1[Erweitert Funktionalität]
```

## Aktivitätsdiagramme

### Basic Facade-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Eingabedaten empfangen]
    B --> C[Vorverarbeiten der Daten]
    C --> D[Daten analysieren]
    D --> E{Konfidenz > 0.8?}
    
    E -->|Ja| F[Direkte Verarbeitung]
    E -->|Nein| G[Zusätzliche Validierung]
    G --> H[Verarbeitung mit Validierung]
    
    F --> I[Ergebnis zurückgeben]
    H --> I
    I --> J[Ende]
```

### Remote Service Facade-Aktivitätsdiagramm

```mermaid
flowchart TD
    A[Start] --> B[Benutzer validieren]
    B --> C{Benutzer existiert?}
    
    C -->|Nein| D[Fehler zurückgeben]
    C -->|Ja| E[Produktverfügbarkeit prüfen]
    
    E --> F{Alle Produkte verfügbar?}
    F -->|Nein| D
    F -->|Ja| G[Bestellung erstellen]
    
    G --> H[Bestand aktualisieren]
    H --> I[Benutzer benachrichtigen]
    I --> J[Erfolg zurückgeben]
    
    D --> K[Ende]
    J --> K
```