# Proxy Pattern (Stellvertreter)

## Übersicht

Das Proxy-Pattern ist ein strukturelles Entwurfsmuster, das eine Stellvertreterobjekt für ein anderes Objekt bereitstellt, um dessen Zugriff zu kontrollieren, zu erweitern oder zu schützen. Dieses Pattern wird in verteilten Systemen häufig eingesetzt, um Netzwerkkommunikation zu optimieren, Zugriffe zu steuern und Systemkomponenten zu entkoppeln.

## Anwendungsfälle in Verteilten Systemen

- **Zugriffskontrolle**: Überprüfung von Berechtigungen, bevor Anfragen weitergeleitet werden
- **Caching**: Zwischenspeichern von Anfrageergebnissen zur Leistungsverbesserung
- **Lastverteilung**: Verteilung von Anfragen auf mehrere Server
- **Remote-Zugriff**: Vereinfachung der Kommunikation mit entfernten Diensten
- **Service-Discovery**: Dynamisches Auffinden von Diensten in einem verteilten System
- **Protokollkonvertierung**: Übersetzung zwischen verschiedenen Kommunikationsprotokollen

## Proxy-Arten

### 1. Forward-Proxy

Ein Forward-Proxy steht zwischen Client und Internet und leitet Anfragen des Clients an das Internet weiter.

**Vorteile**:
- Anonymisierung von Client-Anfragen
- Filterung von Inhalten
- Caching von häufig angeforderten Ressourcen

**Anwendung in verteilten Systemen**:
- Zugriffssteuerung in Unternehmensnetzwerken
- Bandbreitenoptimierung
- Sicherheitsprüfungen

### 2. Reverse-Proxy

Ein Reverse-Proxy nimmt Anfragen vom Internet entgegen und leitet sie an interne Server weiter.

**Vorteile**:
- Lastenausgleich
- SSL-Terminierung
- Sicherheit durch Verbergen der internen Struktur

**Anwendung in verteilten Systemen**:
- API-Gateways
- Microservice-Architekturen
- Content-Delivery-Netzwerke (CDN)

### 3. Caching-Proxy

Ein Caching-Proxy speichert die Ergebnisse von Anfragen zwischen, um wiederholte Anfragen schneller zu bedienen.

**Vorteile**:
- Reduzierung der Latenz
- Verringerung der Netzwerklast
- Erhöhung der Verfügbarkeit

**Anwendung in verteilten Systemen**:
- Web-Caching
- Datenbank-Caching
- Content-Delivery-Netzwerke

### 4. Load-Balancing-Proxy

Ein Load-Balancing-Proxy verteilt eingehende Anfragen auf mehrere Server, um die Last zu verteilen.

**Vorteile**:
- Erhöhte Skalierbarkeit
- Verbesserte Verfügbarkeit
- Ausfallsicherheit

**Anwendung in verteilten Systemen**:
- Hochverfügbare Dienste
- Cloud-Infrastrukturen
- Microservice-Orchestrierung

### 5. Broker und Trader Ausprägungen

#### Broker-Proxy

Ein Broker-Proxy fungiert als Vermittler zwischen Clients und einer Gruppe von Diensten, wobei er die Dienste für Clients transparent macht.

**Vorteile**:
- Entkopplung von Clients und Diensten
- Zentrale Zugriffssteuerung
- Vereinfachte Client-Implementierung

**Anwendung in verteilten Systemen**:
- Message-oriented Middleware
- Service-oriented Architectures (SOA)
- Event-driven Systems

#### Trader-Proxy

Ein Trader-Proxy erweitert das Broker-Konzept um dynamische Dienstauffindung und -auswahl.

**Vorteile**:
- Dynamische Bindung von Clients an Dienste
- Qualitätsbasierte Dienstauswahl
- Transparente Diensterneuerung

**Anwendung in verteilten Systemen**:
- Service-Discovery-Systeme
- Cloud-Umgebungen mit dynamischer Bereitstellung
- IoT-Netzwerke mit wechselnden Geräten

## Grundlegende Struktur

```java
// Gemeinsame Schnittstelle für reales Objekt und Proxy
public interface Service {
    void request();
}

// Reales Objekt
public class RealService implements Service {
    @Override
    public void request() {
        // Die eigentliche Implementierung
    }
}

// Proxy
public class ServiceProxy implements Service {
    private RealService realService;
    
    @Override
    public void request() {
        // Vor-Verarbeitung
        
        if (realService == null) {
            realService = new RealService();
        }
        realService.request();
        
        // Nach-Verarbeitung
    }
}
```

## Implementation im Verteilten Kontext

In verteilten Systemen werden Proxies typischerweise mit Netzwerkkommunikation gekoppelt:

```java
// Remote-Proxy (vereinfacht)
public class RemoteServiceProxy implements Service {
    private String serviceUrl;
    
    public RemoteServiceProxy(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
    
    @Override
    public void request() {
        // Erstellt HTTP-Anfrage an serviceUrl
        // Parst die Antwort
        // Gibt das Ergebnis zurück
    }
}
```

## Vor- und Nachteile

### Vorteile

- **Transparenz**: Clients müssen nicht wissen, ob sie mit dem realen Objekt oder einem Proxy interagieren
- **Kontrolle**: Ermöglicht zusätzliche Funktionalität wie Caching, Zugriffskontrolle, Protokollierung
- **Entkopplung**: Verbirgt Komplexität und Implementierungsdetails vor dem Client
- **Skalierbarkeit**: Unterstützt Lastverteilung und verteilte Verarbeitung

### Nachteile

- **Komplexität**: Fügt zusätzliche Schichten und potenzielle Fehlerquellen hinzu
- **Latenz**: Kann die Antwortzeit durch zusätzliche Indirektion erhöhen
- **Overhead**: Zusätzlicher Ressourcenbedarf durch Proxy-Operationen

## Implementierungen in diesem Projekt

Dieses Projekt enthält verschiedene Implementierungen des Proxy-Patterns:

1. **Forward-Proxy**: Demonstration eines Clients, der über einen Proxy auf entfernte Dienste zugreift
2. **Reverse-Proxy**: Implementierung eines API-Gateways für Microservices
3. **Caching-Proxy**: Leistungsoptimierung durch Zwischenspeichern von Anfrageergebnissen
4. **Load-Balancing-Proxy**: Verteilung von Anfragen auf mehrere Backend-Server
5. **Broker-Proxy**: Vermittlung zwischen Clients und Diensten
6. **Trader-Proxy**: Dynamisches Service-Discovery und -Auswahl basierend auf Anforderungen

Jede Implementierung demonstriert die spezifischen Merkmale und Anwendungsfälle der jeweiligen Proxy-Art im Kontext verteilter Systeme.