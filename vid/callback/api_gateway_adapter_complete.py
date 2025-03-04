from manim import *
import numpy as np

class AdapterExample(Scene):
    def construct(self):
        # Erstelle einen Lehrfilm über das API-Gateway-Beispiel
        self.show_api_gateway_example()
        self.show_adapter_comparison()
        self.show_implementation_tips()
        self.show_final_notes()
    
    def show_api_gateway_example(self):
        title = Text("API-Gateway als Adapter-Pattern", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Einführung
        explanation = Text(
            "Das API-Gateway ist ein reales Beispiel für das Adapter-Pattern in verteilten Systemen.\n" +
            "Es konvertiert zwischen verschiedenen Protokollen und Datenformaten.",
            font_size=24
        ).next_to(title, DOWN, buff=0.5)
        
        self.play(Write(explanation))
        self.wait(2)
        
        # Komponentendiagramm erstellen
        client = self.create_component("Client\n(Frontend)", BLUE, LEFT*4)
        gateway = self.create_component("API Gateway", YELLOW, ORIGIN)
        user_service = self.create_component("UserService\n(JSON)", RED, RIGHT*2 + UP*2)
        product_service = self.create_component("ProductService\n(XML)", GREEN, RIGHT*2 + DOWN*2)
        
        components = VGroup(client, gateway, user_service, product_service)
        self.play(FadeOut(explanation))
        self.play(Create(components))
        
        # Pfeile zwischen Komponenten
        arrows = VGroup()
        
        client_gateway = Arrow(client.get_right(), gateway.get_left(), buff=0.1)
        client_gateway_label = Text("HTTP/REST", font_size=16).next_to(client_gateway, UP, buff=0.1)
        
        gateway_user = Arrow(gateway.get_right() + UP*0.5, user_service.get_left(), buff=0.1)
        gateway_user_label = Text("JSON", font_size=16).next_to(gateway_user, UP, buff=0.1)
        
        gateway_product = Arrow(gateway.get_right() + DOWN*0.5, product_service.get_left(), buff=0.1)
        gateway_product_label = Text("XML", font_size=16).next_to(gateway_product, DOWN, buff=0.1)
        
        arrows.add(
            client_gateway, client_gateway_label,
            gateway_user, gateway_user_label,
            gateway_product, gateway_product_label
        )
        
        self.play(Create(arrows))
        
        # Gateway-Funktionen hervorheben
        functions_box = Rectangle(height=3, width=2.5, color=WHITE)
        functions_box.move_to(gateway.get_center())
        
        functions = VGroup(
            Text("1. Routing", font_size=16),
            Text("2. Format-Konvertierung", font_size=16),
            Text("3. Einheitliche API", font_size=16)
        ).arrange(DOWN, buff=0.2).move_to(functions_box)
        
        self.play(Create(functions_box), Write(functions))
        
        # Sequenz zeigen: Client -> Gateway -> ProductService -> Gateway -> Client
        request_arrow = Arrow(client.get_top() + RIGHT*0.5, gateway.get_left() + UP*0.5, buff=0.1, color=BLUE)
        request_text = Text("GET /products/123", font_size=14, color=BLUE).next_to(request_arrow, UP, buff=0.1)
        
        xml_arrow = Arrow(gateway.get_right() + DOWN*0.2, product_service.get_left() + UP*0.2, buff=0.1, color=YELLOW)
        xml_text = Text("getProduct(\"123\")", font_size=14, color=YELLOW).next_to(xml_arrow, UP, buff=0.1)
        
        xml_response = Arrow(product_service.get_left() + DOWN*0.2, gateway.get_right() + DOWN*0.8, buff=0.1, color=GREEN)
        xml_response_text = Text("<product>...</product>", font_size=14, color=GREEN).next_to(xml_response, DOWN, buff=0.1)
        
        convert_text = Text("convertXmlToJson()", font_size=16, color=YELLOW)
        convert_text.move_to(gateway.get_center() + DOWN*0.5)
        
        json_response = Arrow(gateway.get_left() + DOWN*0.5, client.get_top() + RIGHT*1, buff=0.1, color=YELLOW)
        json_response_text = Text("{\"product\":...}", font_size=14, color=YELLOW).next_to(json_response, DOWN, buff=0.1)
        
        sequence = VGroup(
            request_arrow, request_text,
            xml_arrow, xml_text,
            xml_response, xml_response_text,
            convert_text,
            json_response, json_response_text
        )
        
        # Animation der Sequenz
        self.play(Create(request_arrow), Write(request_text))
        self.play(Create(xml_arrow), Write(xml_text))
        self.play(Create(xml_response), Write(xml_response_text))
        self.play(Write(convert_text))
        self.play(Create(json_response), Write(json_response_text))
        
        self.wait(2)
        
        # Code-Beispiel
        code_box = Rectangle(height=4, width=6, color=WHITE).set_fill(BLACK, opacity=0.8)
        code_box.to_edge(DOWN, buff=0.5)
        
        code_text = """
public ApiResponse processRequest(String path, String method, String body) {
    // Pfad analysieren und an entsprechenden Service weiterleiten
    if (path.startsWith("/products")) {
        // Produkt-Service gibt XML zurück, also konvertieren
        String xmlResponse = handleProductRequest(method, productId, body);
        
        // Konvertiere zu JSON für einheitliche Client-API
        String jsonResponse = convertXmlToJson(xmlResponse);
        
        return new ApiResponse(200, jsonResponse, "application/json");
    }
    // ...
}
        """
        
        code = Code(
            code=code_text,
            language="java",
            font_size=14,
            line_spacing=0.5,
            background_stroke_width=0
        ).move_to(code_box)
        
        self.play(
            FadeOut(sequence),
            Create(code_box),
            Write(code)
        )
        
        self.wait(3)
        
        # Zusammenfassung und Schlussfolgerung
        self.play(
            FadeOut(components), FadeOut(arrows),
            FadeOut(functions_box), FadeOut(functions),
            FadeOut(code_box), FadeOut(code)
        )
        
        conclusion = Text(
            "Das API-Gateway demonstriert das Adapter-Pattern in verteilten Systemen:\n\n" +
            "• Es konvertiert zwischen verschiedenen Protokollen und Datenformaten\n" +
            "• Es bietet eine einheitliche Schnittstelle für heterogene Dienste\n" +
            "• Es vereinfacht die Integration verschiedener Systeme",
            font_size=28
        ).next_to(title, DOWN, buff=0.5)
        
        self.play(Write(conclusion))
        self.wait(3)
        
        self.play(FadeOut(title), FadeOut(conclusion))
    
    def show_adapter_comparison(self):
        # Vergleich von API-Gateway mit anderen Adaptern
        title = Text("API-Gateway vs. andere Adapter-Typen", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Tabelle mit Vergleichen erstellen
        table = self.create_comparison_table()
        self.play(Create(table))
        
        # Erläuterung zum Vergleich
        explanation = Text(
            "Anders als einfache Adapter müssen API-Gateways oft mit\n" +
            "mehreren Diensten, Protokollen und Datenformaten umgehen.",
            font_size=24
        ).next_to(table, DOWN, buff=0.5)
        
        self.play(Write(explanation))
        self.wait(3)
        
        self.play(FadeOut(title), FadeOut(table), FadeOut(explanation))
    
    def show_implementation_tips(self):
        # Implementierungstipps für API-Gateway-Adapter
        title = Text("Implementierungstipps für API-Gateway-Adapter", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Tipps als Bulletpoints
        tips = VGroup(
            Text("1. Verwenden Sie Objektadapter statt Klassenadapter", font_size=24),
            Text("2. Trennen Sie Routing- und Konvertierungslogik", font_size=24),
            Text("3. Implementieren Sie spezifische Adapter pro Service-Typ", font_size=24),
            Text("4. Nutzen Sie Caching für häufige Anfragen", font_size=24),
            Text("5. Erstellen Sie eine klare Fehlerbehandlungsstrategie", font_size=24)
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.5).next_to(title, DOWN, buff=1)
        
        # Animation sequentiell
        for tip in tips:
            self.play(FadeIn(tip))
            self.wait(0.5)
        
        self.wait(2)
        
        # Code-Struktur zeigen
        code_box = Rectangle(height=5, width=6, color=WHITE).set_fill(BLACK, opacity=0.8)
        code_box.to_edge(DOWN, buff=0.5)
        
        code_text = """
// Empfohlene Struktur für API-Gateway-Adapter
public class ProductServiceAdapter {
    private final ProductService productService;
    private final JsonConverter jsonConverter;
    
    // Adapter für eine spezifische Operation
    public ApiResponse getProduct(String productId) {
        // Service aufrufen
        String xmlResponse = productService.getProduct(productId);
        
        // Fehlerbehandlung
        if (xmlResponse.contains("<error>")) {
            return handleError(xmlResponse);
        }
        
        // Konvertierung durchführen
        String jsonResponse = jsonConverter.convertXmlToJson(xmlResponse);
        
        // Cache-Eintrag aktualisieren (wenn implementiert)
        updateCache(productId, jsonResponse);
        
        return new ApiResponse(200, jsonResponse, "application/json");
    }
    
    // Weitere Methoden...
}
        """
        
        code = Code(
            code=code_text,
            language="java",
            font_size=14,
            line_spacing=0.5,
            background_stroke_width=0
        ).move_to(code_box)
        
        self.play(FadeOut(tips))
        self.play(Create(code_box), Write(code))
        
        self.wait(3)
        self.play(FadeOut(title), FadeOut(code_box), FadeOut(code))
    
    def show_final_notes(self):
        # Abschließende Hinweise
        title = Text("Herausforderungen bei API-Gateway-Adaptern", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Herausforderungen auflisten
        challenges = VGroup(
            Text("• Leistungsprobleme durch zusätzliche Abstraktionsschicht", font_size=28),
            Text("• Umgang mit verschiedenen API-Versionen", font_size=28),
            Text("• Fehlerfortpflanzung zwischen Diensten", font_size=28),
            Text("• Testen der Adapter-Komponenten", font_size=28)
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.5).next_to(title, DOWN, buff=1)
        
        self.play(Write(challenges))
        self.wait(3)
        
        # Fazit
        self.play(FadeOut(challenges))
        
        conclusion = Text(
            "Trotz der Herausforderungen ist das API-Gateway\n" +
            "ein mächtiges Beispiel für das Adapter-Pattern,\n" +
            "das die Komplexität verteilter Systeme bewältigt\n" +
            "und eine einheitliche Schnittstelle bietet.",
            font_size=32
        ).next_to(title, DOWN, buff=1)
        
        self.play(Write(conclusion))
        self.wait(3)
        
        # Abschluss
        self.play(FadeOut(title), FadeOut(conclusion))
        
        end_title = Text("API-Gateway als Adapter-Pattern", font_size=56)
        sub_title = Text("Ein Beispiel aus verteilten Systemen", font_size=32).next_to(end_title, DOWN)
        
        self.play(Write(end_title), Write(sub_title))
        self.wait(2)
        self.play(FadeOut(end_title), FadeOut(sub_title))
    
    def create_component(self, name, color, position):
        rect = Rectangle(height=2, width=3).set_fill(color, opacity=0.2)
        text = Text(name, font_size=20).move_to(rect)
        return VGroup(rect, text).move_to(position)
    
    def create_comparison_table(self):
        # Tabelle erstellen, die API-Gateway mit anderen Adaptern vergleicht
        table_rect = Rectangle(height=5, width=8)
        
        # Tabellenüberschriften
        headers = VGroup(
            Text("Eigenschaft", font_size=20),
            Text("Einfacher Adapter", font_size=20),
            Text("API-Gateway", font_size=20)
        ).arrange(RIGHT, buff=1.5).next_to(table_rect, UP, buff=-0.5)
        
        # Trennlinie unter Überschriften
        header_line = Line(
            table_rect.get_corner(UL) + RIGHT*0.2 + DOWN*1,
            table_rect.get_corner(UR) + LEFT*0.2 + DOWN*1
        )
        
        # Vertikale Trennlinie
        vert_line = Line(
            headers[0].get_right() + RIGHT*0.5 + UP*0.5,
            table_rect.get_corner(DL) + RIGHT*3 + UP*0.2
        )
        
        # Tabellenzeilen
        row1 = VGroup(
            Text("Komplexität", font_size=18),
            Text("Niedrig", font_size=18),
            Text("Hoch", font_size=18)
        ).arrange(RIGHT, buff=2).next_to(header_line, DOWN, buff=0.3)
        
        row2 = VGroup(
            Text("Dienste", font_size=18),
            Text("Einzelne Komponente", font_size=18),
            Text("Multiple Microservices", font_size=18)
        ).arrange(RIGHT, buff=0.8).next_to(row1, DOWN, buff=0.5)
        
        row3 = VGroup(
            Text("Konvertierung", font_size=18),
            Text("Einzelnes Format", font_size=18),
            Text("Multiple Formate", font_size=18)
        ).arrange(RIGHT, buff=1.2).next_to(row2, DOWN, buff=0.5)
        
        row4 = VGroup(
            Text("Routing", font_size=18),
            Text("Nicht vorhanden", font_size=18),
            Text("Zentraler Bestandteil", font_size=18)
        ).arrange(RIGHT, buff=1.2).next_to(row3, DOWN, buff=0.5)
        
        # Farbliche Hervorhebungen
        row1[1].set_color(GREEN)
        row1[2].set_color(RED)
        row4[2].set_color(YELLOW)
        
        # Alle Elemente gruppieren
        table = VGroup(
            table_rect, headers, header_line, vert_line,
            row1, row2, row3, row4
        )
        
        return table
