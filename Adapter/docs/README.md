# Adapter-Pattern Dokumentation

Diese Dokumentation enthält verschiedene Diagramme zur Veranschaulichung des Adapter-Patterns und der konkreten Implementierung in diesem Projekt.

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
    Client[Client] --> |verwendet| Target[Target Interface]
    Target --> |definiert| Adapter[Adapter]
    Adapter --> |ruft auf| Adaptee[Legacy System/Adaptee]
    
    subgraph "Adapter-Pattern Grundstruktur"
        Target
        Adapter
        Adaptee
    end
    
    subgraph "Projekt-Implementierungen"
        FormatAdapter[Format Adapter] --> Target
        ProtocolAdapter[Protocol Adapter] --> Target
        MessagingAdapter[Messaging Adapter] --> Target
        LegacySystemAdapter[Legacy System Adapter] --> Target
        ApiGatewayAdapter[API Gateway Adapter] --> Target
    end
    
    Client --> Main[Main Demo]
    Main --> |verwendet| FormatAdapter
    Main --> |verwendet| ProtocolAdapter
    Main --> |verwendet| MessagingAdapter
    Main --> |verwendet| LegacySystemAdapter
    Main --> |verwendet| ApiGatewayAdapter
```

## Klassendiagramme

### Allgemeines Adapter-Klassendiagramm

Das folgende Diagramm zeigt die allgemeine Struktur des Adapter-Patterns:

```mermaid
classDiagram
    class Client {
        +method()
    }
    
    class Target {
        <<interface>>
        +request()
    }
    
    class Adapter {
        -adaptee: Adaptee
        +request()
    }
    
    class Adaptee {
        +specificRequest()
    }
    
    Client --> Target : uses
    Adapter ..|> Target : implements
    Adapter --> Adaptee : adapts
```

### Format-Adapter-Klassendiagramm

```mermaid
classDiagram
    class DataFormatConverter {
        <<interface>>
        +toXml(data: DataObject): String
        +fromXml(xml: String): DataObject
        +toJson(data: DataObject): String
        +fromJson(json: String): DataObject
        +toBinary(data: DataObject): byte[]
        +fromBinary(binary: byte[]): DataObject
    }
    
    class DefaultFormatConverter {
        +toXml(data: DataObject): String
        +fromXml(xml: String): DataObject
        +toJson(data: DataObject): String
        +fromJson(json: String): DataObject
        +toBinary(data: DataObject): byte[]
        +fromBinary(binary: byte[]): DataObject
    }
    
    class LegacyCSVAdapter {
        -legacySystem: LegacyCSVSystem
        +toXml(data: DataObject): String
        +fromXml(xml: String): DataObject
        +toJson(data: DataObject): String
        +fromJson(json: String): DataObject
        +toBinary(data: DataObject): byte[]
        +fromBinary(binary: byte[]): DataObject
        +processWithLegacySystem(data: DataObject): String
    }
    
    class LegacyCSVSystem {
        +convertToCSV(id: String, name: String, description: String, value: int): String
        +processCSVData(csvData: String): String
        +parseFromCSV(csvData: String): Object[]
        +exportToLegacyFormat(data: DataObject): String
        +importFromLegacyFormat(csvData: String): DataObject
    }
    
    class DataObject {
        -id: String
        -name: String
        -description: String
        -value: int
        +getId(): String
        +setId(id: String): void
        +getName(): String
        +setName(name: String): void
        +getDescription(): String
        +setDescription(description: String): void
        +getValue(): int
        +setValue(value: int): void
    }
    
    class FormatConversionException {
        +FormatConversionException(message: String)
        +FormatConversionException(message: String, cause: Throwable)
    }
    
    DefaultFormatConverter ..|> DataFormatConverter
    LegacyCSVAdapter ..|> DataFormatConverter
    LegacyCSVAdapter --> LegacyCSVSystem
    DataFormatConverter ..> DataObject
    DataFormatConverter ..> FormatConversionException
```

### Protokoll-Adapter-Klassendiagramm

```mermaid
classDiagram
    class RestService {
        <<interface>>
        +get(resourceId: String): RestResponse
        +post(data: String): RestResponse
        +put(resourceId: String, data: String): RestResponse
        +delete(resourceId: String): RestResponse
    }
    
    class SoapToRestAdapter {
        -soapService: SoapService
        +get(resourceId: String): RestResponse
        +post(data: String): RestResponse
        +put(resourceId: String, data: String): RestResponse
        +delete(resourceId: String): RestResponse
        -convertToRestResponse(soapResponse: SoapResponse): RestResponse
    }
    
    class SoapService {
        +executeRequest(request: SoapRequest): SoapResponse
    }
    
    class SoapRequest {
        -operation: String
        -resourceId: String
        -data: String
        +getOperation(): String
        +setOperation(operation: String): void
        +getResourceId(): String
        +setResourceId(resourceId: String): void
        +getData(): String
        +setData(data: String): void
    }
    
    class SoapResponse {
        -responseCode: int
        -responseData: String
        +getResponseCode(): int
        +setResponseCode(responseCode: int): void
        +getResponseData(): String
        +setResponseData(responseData: String): void
        +isSuccess(): boolean
    }
    
    class RestResponse {
        -statusCode: int
        -body: String
        -contentType: String
        +getStatusCode(): int
        +getBody(): String
        +getContentType(): String
        +isSuccess(): boolean
    }
    
    SoapToRestAdapter ..|> RestService
    SoapToRestAdapter --> SoapService
    SoapService ..> SoapRequest
    SoapService ..> SoapResponse
    SoapToRestAdapter ..> RestResponse
```

### Legacy-System-Adapter-Klassendiagramm

```mermaid
classDiagram
    class UserManagementSystem {
        <<interface>>
        +getUser(userId: String): User
        +createUser(user: User): boolean
        +updateUser(user: User): boolean
        +deleteUser(userId: String): boolean
        +authenticateUser(username: String, password: String): User
    }
    
    class LegacyUserSystemAdapter {
        -legacySystem: LegacyUserSystem
        +getUser(userId: String): User
        +createUser(user: User): boolean
        +updateUser(user: User): boolean
        +deleteUser(userId: String): boolean
        +authenticateUser(username: String, password: String): User
        -convertLegacyUser(legacyUser: LegacyUser): User
        -convertToLegacyUser(user: User): LegacyUser
    }
    
    class LegacyUserSystem {
        -users: Map<Integer, LegacyUser>
        -nextUserId: int
        +addUser(login: String, name: String, email: String, password: String): int
        +findUserById(userId: int): LegacyUser
        +findUserByLogin(login: String): LegacyUser
        +updateUser(userId: int, name: String, email: String, password: String): boolean
        +removeUser(userId: int): boolean
        +deactivateUser(userId: int): boolean
        +checkCredentials(login: String, password: String): LegacyUser
        -hashPassword(password: String): String
    }
    
    class User {
        -id: String
        -username: String
        -firstName: String
        -lastName: String
        -email: String
        -passwordHash: String
        -createdAt: Date
        -lastLogin: Date
        -active: boolean
        +getId(): String
        +setId(id: String): void
        +getUsername(): String
        +setUsername(username: String): void
        +getEmail(): String
        +setEmail(email: String): void
        +getFullName(): String
    }
    
    class LegacyUser {
        -userId: int
        -login: String
        -name: String
        -email: String
        -password: String
        -creationDate: Date
        -lastLogin: Date
        -status: int
        +getUserId(): int
        +setUserId(userId: int): void
        +getLogin(): String
        +setLogin(login: String): void
        +getName(): String
        +setName(name: String): void
    }
    
    LegacyUserSystemAdapter ..|> UserManagementSystem
    LegacyUserSystemAdapter --> LegacyUserSystem
    UserManagementSystem ..> User
    LegacyUserSystem ..> LegacyUser
    LegacyUserSystemAdapter ..> User
    LegacyUserSystemAdapter ..> LegacyUser
```

### API-Gateway-Adapter-Klassendiagramm

```mermaid
classDiagram
    class ApiGateway {
        -userService: UserService
        -productService: ProductService
        +processRequest(path: String, method: String, body: String): ApiResponse
        -handleUserRequest(method: String, userId: String, body: String): String
        -handleProductRequest(method: String, productId: String, body: String): String
        -convertXmlToJson(xml: String): String
        -extractXmlContent(xml: String, tagName: String): String
        -createErrorResponse(statusCode: int, message: String): ApiResponse
    }
    
    class ApiResponse {
        -statusCode: int
        -body: String
        -contentType: String
        +getStatusCode(): int
        +getBody(): String
        +getContentType(): String
        +isSuccess(): boolean
    }
    
    class UserService {
        +getUser(userId: String): String
        +createUser(userData: String): String
        +updateUser(userId: String, userData: String): String
        +deleteUser(userId: String): String
    }
    
    class ProductService {
        +getProduct(productId: String): String
        +getAllProducts(): String
        +createProduct(productData: String): String
        +updateProduct(productId: String, productData: String): String
        +deleteProduct(productId: String): String
    }
    
    ApiGateway --> UserService
    ApiGateway --> ProductService
    ApiGateway ..> ApiResponse
```

### Messaging-Adapter-Klassendiagramm

```mermaid
classDiagram
    class MessagingService {
        <<interface>>
        +sendMessage(recipient: String, message: String): boolean
        +receiveMessages(recipient: String): Message[]
        +getServiceType(): String
    }
    
    class JmsAdapter {
        -jmsService: JmsMessageService
        -senderIdentity: String
        +sendMessage(recipient: String, message: String): boolean
        +receiveMessages(recipient: String): Message[]
        +getServiceType(): String
        -convertJmsMessage(jmsMessage: JmsMessage): Message
    }
    
    class JmsMessageService {
        -messageStore: Map<String, List<JmsMessage>>
        +sendJmsMessage(sender: String, destination: String, messageText: String, properties: Map<String, Object>): String
        +receiveJmsMessages(destination: String, acknowledgeMode: int): JmsMessage[]
        +acknowledgeMessage(messageId: String): void
    }
    
    class JmsMessage {
        -messageId: String
        -sender: String
        -destination: String
        -messageText: String
        -timestamp: long
        -properties: Map<String, Object>
        +getMessageId(): String
        +getSender(): String
        +getDestination(): String
        +getMessageText(): String
        +getTimestamp(): long
        +getProperty(name: String): Object
        +getProperties(): Map<String, Object>
    }
    
    class Message {
        -sender: String
        -recipient: String
        -content: String
        -timestamp: Date
        -messageId: String
        -read: boolean
        +getSender(): String
        +setSender(sender: String): void
        +getRecipient(): String
        +setRecipient(recipient: String): void
        +markAsRead(): void
    }
    
    JmsAdapter ..|> MessagingService
    JmsAdapter --> JmsMessageService
    JmsMessageService ..> JmsMessage
    JmsAdapter ..> Message
```

## Sequenzdiagramme

### Format-Adapter-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client as Client
    participant FAdapter as LegacyCSVAdapter
    participant LegacySystem as LegacyCSVSystem
    
    Client->>+FAdapter: toXml(dataObject)
    FAdapter->>+LegacySystem: convertToCSV(id, name, description, value)
    LegacySystem-->>-FAdapter: csvString
    FAdapter->>+LegacySystem: parseFromCSV(csvString)
    LegacySystem-->>-FAdapter: parsedData
    FAdapter-->>-Client: xmlString
    
    Client->>+FAdapter: fromXml(xmlString)
    FAdapter->>FAdapter: extractTagValue(xml, patterns)
    FAdapter->>+LegacySystem: convertToCSV(id, name, description, value)
    LegacySystem-->>-FAdapter: csvString
    FAdapter->>+LegacySystem: parseFromCSV(csvString)
    LegacySystem-->>-FAdapter: parsedData
    FAdapter-->>-Client: dataObject
```

### Protokoll-Adapter-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client as Client
    participant RestAdapter as SoapToRestAdapter
    participant SoapService as SoapService
    
    Client->>+RestAdapter: get(resourceId)
    RestAdapter->>+RestAdapter: Erstelle SoapRequest
    RestAdapter->>+SoapService: executeRequest(soapRequest)
    SoapService-->>-RestAdapter: soapResponse
    RestAdapter->>+RestAdapter: convertToRestResponse(soapResponse)
    RestAdapter-->>-Client: restResponse
    
    Client->>+RestAdapter: post(data)
    RestAdapter->>+RestAdapter: Erstelle SoapRequest
    RestAdapter->>+SoapService: executeRequest(soapRequest)
    SoapService-->>-RestAdapter: soapResponse
    RestAdapter->>+RestAdapter: convertToRestResponse(soapResponse)
    RestAdapter-->>-Client: restResponse
```

### Legacy-System-Adapter-Sequenzdiagramm

```mermaid
sequenceDiagram
    participant Client as Client
    participant LAdapter as LegacyUserSystemAdapter
    participant LegacySystem as LegacyUserSystem
    
    Client->>+LAdapter: getUser(userId)
    LAdapter->>+LegacySystem: findUserById(legacyId)
    LegacySystem-->>-LAdapter: legacyUser
    LAdapter->>+LAdapter: convertLegacyUser(legacyUser)
    LAdapter-->>-Client: user
    
    Client->>+LAdapter: authenticateUser(username, password)
    LAdapter->>+LegacySystem: checkCredentials(username, password)
    LegacySystem-->>-LAdapter: legacyUser
    LAdapter->>+LAdapter: convertLegacyUser(legacyUser)
    LAdapter-->>-Client: user
```

## Zustandsdiagramme

### Nachrichtenverarbeitung in JmsAdapter

```mermaid
stateDiagram-v2
    [*] --> Idle
    
    Idle --> SendingMessage: sendMessage() aufgerufen
    SendingMessage --> CreatingJmsMessage: Erstelle JMS-Eigenschaften
    CreatingJmsMessage --> SendingJmsMessage: Rufe jmsService.sendJmsMessage() auf
    SendingJmsMessage --> MessageSent: Nachricht erfolgreich gesendet
    SendingJmsMessage --> SendError: Fehler bei der Übertragung
    SendError --> Idle
    MessageSent --> Idle
    
    Idle --> ReceivingMessages: receiveMessages() aufgerufen
    ReceivingMessages --> FetchingJmsMessages: Rufe jmsService.receiveJmsMessages() auf
    FetchingJmsMessages --> ConvertingMessages: JMS-Nachrichten erhalten
    ConvertingMessages --> MessagesConverted: Nachrichten konvertiert
    MessagesConverted --> Idle
    FetchingJmsMessages --> ReceiveError: Fehler beim Empfangen
    ReceiveError --> Idle
```

### API-Gateway-Zustandsdiagramm

```mermaid
stateDiagram-v2
    [*] --> WaitingForRequest
    
    WaitingForRequest --> ProcessingRequest: Anfrage erhalten
    ProcessingRequest --> AnalyzingPath: Pfad analysieren
    
    AnalyzingPath --> ProcessingUserRequest: Pfad = "/users/..."
    AnalyzingPath --> ProcessingProductRequest: Pfad = "/products/..."
    AnalyzingPath --> RequestError: Ungültiger Pfad
    
    ProcessingUserRequest --> ExecutingUserOperation: Rufe UserService auf
    ProcessingProductRequest --> ExecutingProductOperation: Rufe ProductService auf
    ExecutingProductOperation --> ConvertingResponseFormat: XML zu JSON konvertieren
    ExecutingUserOperation --> CreatingResponse: Erstelle Antwort
    ConvertingResponseFormat --> CreatingResponse
    
    RequestError --> CreatingErrorResponse
    CreatingErrorResponse --> SendingResponse
    CreatingResponse --> SendingResponse
    
    SendingResponse --> WaitingForRequest
```

## Aktivitätsdiagramme

### Format-Adapter-Aktivitätsdiagramm

```mermaid
graph TD
    A[Start] --> B{Format überprüfen}
    B -->|XML| C[XML zu DataObject konvertieren]
    B -->|JSON| D[JSON zu DataObject konvertieren]
    B -->|Binary| E[Binary zu DataObject konvertieren]
    B -->|CSV| F[CSV zu DataObject über Legacy-System konvertieren]
    
    C --> G[DataObject zurückgeben]
    D --> G
    E --> G
    F --> G
    
    G --> H[Ende]
```

### Protokoll-Adapter-Aktivitätsdiagramm

```mermaid
graph TD
    A[Start] --> B{HTTP-Methode?}
    B -->|GET| C[SoapRequest mit Operation=GetResource erstellen]
    B -->|POST| D[SoapRequest mit Operation=CreateResource erstellen]
    B -->|PUT| E[SoapRequest mit Operation=UpdateResource erstellen]
    B -->|DELETE| F[SoapRequest mit Operation=DeleteResource erstellen]
    
    C --> G[SoapService.executeRequest aufrufen]
    D --> G
    E --> G
    F --> G
    
    G --> H[SOAP-Antwort zu REST-Antwort konvertieren]
    H --> I[REST-Antwort zurückgeben]
    I --> J[Ende]
```
