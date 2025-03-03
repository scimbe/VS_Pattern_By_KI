# Adapter Pattern (Wrapper)

## Übersicht

Das Adapter-Pattern ist ein strukturelles Entwurfsmuster, das die Schnittstelle einer Klasse in eine andere Schnittstelle übersetzt, die von Clients erwartet wird. Es ermöglicht die Zusammenarbeit von Klassen, die aufgrund inkompatibler Schnittstellen normalerweise nicht zusammenarbeiten könnten. In verteilten Systemen wird das Adapter-Pattern häufig verwendet, um unterschiedliche Komponenten, Protokolle oder Dienste zu integrieren.

## Anwendungsfälle in Verteilten Systemen

- **Protokolladaption**: Übersetzung zwischen unterschiedlichen Kommunikationsprotokollen (z.B. REST zu SOAP)
- **Legacy-System-Integration**: Einbindung älterer Systeme in moderne Architekturen
- **Dienst-Virtualisierung**: Vereinheitlichung des Zugriffs auf unterschiedliche externe Dienste
- **Datenkonvertierung**: Transformation von Datenformaten zwischen Systemen
- **API-Harmonisierung**: Bereitstellung einer einheitlichen Schnittstelle für verschiedene APIs
- **Cloud-Service-Integration**: Anpassung von Cloud-Diensten an lokale Anwendungen

## Adapter-Arten

### 1. Objektadapter

Der Objektadapter nutzt Komposition, um die Zielschnittstelle zu implementieren und delegiert Aufrufe an das adaptierte Objekt.

```java
// Zielschnittstelle, die der Client erwartet
public interface TargetInterface {
    String request();
}

// Zu adaptierende Klasse
public class Adaptee {
    public String specificRequest() {
        return "Spezifische Anfrage beantwortet";
    }
}

// Objektadapter
public class ObjectAdapter implements TargetInterface {
    private Adaptee adaptee;
    
    public ObjectAdapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }
    
    @Override
    public String request() {
        // Adaptiert den Aufruf an die specificRequest-Methode
        return adaptee.specificRequest();
    }
}
```

### 2. Klassenadapter

Der Klassenadapter nutzt Mehrfachvererbung (wo verfügbar, in Java durch Interfaces), um die Zielschnittstelle zu implementieren und von der zu adaptierenden Klasse zu erben.

```java
// Klassenadapter (in Java mit Interface)
public class ClassAdapter extends Adaptee implements TargetInterface {
    @Override
    public String request() {
        // Nutzt geerbte Methode von Adaptee
        return specificRequest();
    }
}
```

### 3. Zwei-Wege-Adapter

Ein Zwei-Wege-Adapter ermöglicht die bidirektionale Adaption zwischen zwei verschiedenen Schnittstellen.

```java
public class TwoWayAdapter implements TargetInterface, AnotherInterface {
    private Adaptee adaptee;
    private Target target;
    
    // Implementierung beider Schnittstellenmethoden
    // mit entsprechender Delegation
}
```

## Implementierung im Verteilten Kontext

### Protokolladapter

```java
public interface RestService {
    Response getResource(String id);
}

public class SoapService {
    public SoapResponse getResourceBySoapRequest(SoapRequest request) {
        // SOAP-spezifische Implementierung
    }
}

public class SoapToRestAdapter implements RestService {
    private SoapService soapService;
    
    public SoapToRestAdapter(SoapService soapService) {
        this.soapService = soapService;
    }
    
    @Override
    public Response getResource(String id) {
        // Konvertiere REST-Anfrage zu SOAP
        SoapRequest soapRequest = createSoapRequest(id);
        
        // Rufe SOAP-Dienst auf
        SoapResponse soapResponse = soapService.getResourceBySoapRequest(soapRequest);
        
        // Konvertiere SOAP-Antwort zu REST
        return convertToRestResponse(soapResponse);
    }
    
    private SoapRequest createSoapRequest(String id) {
        // Erstelle SOAP-Anfrage aus REST-Parameter
    }
    
    private Response convertToRestResponse(SoapResponse soapResponse) {
        // Konvertiere SOAP-Antwort zu REST-Antwort
    }
}
```

## Vor- und Nachteile

### Vorteile

- **Wiederverwendbarkeit**: Ermöglicht die Nutzung vorhandener Komponenten
- **Schnittstellenharmonisierung**: Bietet eine einheitliche Schnittstelle für heterogene Systeme
- **Entkopplung**: Vermeidet direkte Abhängigkeiten zwischen inkompatiblen Komponenten
- **Erweiterbarkeit**: Neue Adapter können ohne Änderung bestehender Systeme hinzugefügt werden
- **Interoperabilität**: Ermöglicht die Kommunikation zwischen unterschiedlichen Technologien

### Nachteile

- **Komplexität**: Zusätzliche Adapter erhöhen die Komplexität des Systems
- **Leistungsoverhead**: Zusätzliche Adapterschicht kann die Leistung beeinträchtigen
- **Datenverlust**: Bei der Konvertierung zwischen verschiedenen Formaten können Informationen verloren gehen
- **Wartungsaufwand**: Änderungen in den Zielschnittstellen erfordern Anpassungen der Adapter

## Adapter vs. Andere Muster

### Adapter vs. Bridge

Während Adapter eine Schnittstelle in eine andere übersetzt, trennt Bridge eine Abstraktion von ihrer Implementierung. Adapter wird typischerweise eingesetzt, um bestehende Komponenten zu integrieren, während Bridge bei der Entwicklung neuer Systeme für zukünftige Flexibilität genutzt wird.

### Adapter vs. Decorator

Decorator erweitert ein Objekt um zusätzliche Funktionalität, ohne seine Schnittstelle zu ändern. Adapter hingegen ändert die Schnittstelle eines Objekts, um Kompatibilität herzustellen.

### Adapter vs. Proxy

Proxy kontrolliert den Zugriff auf ein Objekt, behält aber dieselbe Schnittstelle bei. Adapter verändert die Schnittstelle, um Kompatibilität zu erreichen.

## Implementierungen in diesem Projekt

Dieses Projekt enthält verschiedene Implementierungen des Adapter-Patterns:

1. **Protokolladapter**: Demonstration der Adaption zwischen verschiedenen Kommunikationsprotokollen
2. **Formatadapter**: Adaption zwischen verschiedenen Datenformaten (XML, JSON, binär)
3. **Legacy-System-Adapter**: Integration eines älteren Systems in eine moderne Architektur
4. **Messaging-Adapter**: Vereinheitlichung unterschiedlicher Messaging-Systeme
5. **API-Gateway-Adapter**: Implementierung eines API-Gateways als Adapter für verschiedene Microservices

Jede Implementierung demonstriert die spezifischen Merkmale und Anwendungsfälle der jeweiligen Adapter-Art im Kontext verteilter Systeme.