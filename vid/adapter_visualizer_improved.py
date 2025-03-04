from manim import *
import numpy as np

class AdapterPatternVisualization(Scene):
    def construct(self):
        # Titelsequenz
        self.create_title_sequence()
        
        # Grundkonzept des Adapter-Patterns
        self.explain_adapter_basics()
        
        # Protokolladapter: SOAP zu REST
        self.visualize_protocol_adapter()
        
        # Legacy-System-Adapter für Benutzerverwaltung
        self.visualize_legacy_adapter()
        
        # Format-Adapter für Datenkonvertierung
        self.visualize_format_adapter()
        
        # Zusammenfassung und Anwendungsfälle
        self.summarize_adapter_pattern()

    def create_title_sequence(self):
        """Erzeugt die Titelsequenz der Präsentation"""
        title = Text("Das Adapter-Pattern", font_size=72)
        subtitle = Text("Ein strukturelles Entwurfsmuster für Schnittstellenkonvertierung", 
                      font_size=36).next_to(title, DOWN, buff=0.5)
        
        self.play(Write(title), run_time=1.5)
        self.play(FadeIn(subtitle), run_time=1)
        self.wait(2)
        self.play(FadeOut(title), FadeOut(subtitle))
    
    def create_class_box(self, name, color, font_size=32, height=1.5, width=3, opacity=0.2):
        """Hilfsmethode zur Erstellung von einheitlichen Klassenboxen"""
        rect = Rectangle(height=height, width=width).set_fill(color, opacity=opacity)
        text = Text(name, font_size=font_size).move_to(rect)
        return VGroup(rect, text)
    
    def create_labeled_arrow(self, start, end, label, label_position=UP, color=WHITE, buff=0.1):
        """Hilfsmethode zur Erstellung von beschrifteten Pfeilen"""
        arrow = Arrow(start, end, buff=buff, color=color)
        text = Text(label, font_size=24).next_to(arrow, label_position, buff=0.1)
        return arrow, text
    
    def explain_adapter_basics(self):
        """Erklärt die Grundkonzepte des Adapter-Patterns"""
        # Titel
        title = Text("Grundkonzept des Adapter-Patterns", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Klassen erstellen
        client = self.create_class_box("Client", BLUE)
        client.to_edge(LEFT).shift(UP)
        
        target = self.create_class_box("Target Interface", GREEN)
        target.next_to(client, RIGHT, buff=2)
        
        adapter = self.create_class_box("Adapter", YELLOW)
        adapter.next_to(target, DOWN, buff=2)
        
        adaptee = self.create_class_box("Adaptee", RED)
        adaptee.next_to(adapter, RIGHT, buff=2)
        
        # Pfeile zwischen Klassen
        arrow1, arrow1_text = self.create_labeled_arrow(
            client.get_right(), target.get_left(), "verwendet"
        )
        
        arrow2, arrow2_text = self.create_labeled_arrow(
            target.get_bottom(), adapter.get_top(), "implementiert", LEFT
        )
        
        arrow3, arrow3_text = self.create_labeled_arrow(
            adapter.get_right(), adaptee.get_left(), "adaptiert"
        )
        
        # Animation zeigen
        self.play(Create(client), Create(target), Create(adapter), Create(adaptee))
        self.play(Create(arrow1), Write(arrow1_text))
        self.play(Create(arrow2), Write(arrow2_text))
        self.play(Create(arrow3), Write(arrow3_text))
        
        self.wait(2)
        
        # Erklärungstext
        explanation = (
            "Das Adapter-Pattern konvertiert die Schnittstelle einer Klasse\\n" +
            "in eine andere Schnittstelle, die von Clients erwartet wird.\\n" +
            "Es ermöglicht die Zusammenarbeit von Klassen mit\\n" +
            "inkompatiblen Schnittstellen."
        )
        explanation_text = Text(explanation, font_size=24).to_edge(DOWN, buff=0.5)
        self.play(Write(explanation_text))
        
        self.wait(3)
        self.play(
            FadeOut(client), FadeOut(target), FadeOut(adapter), FadeOut(adaptee),
            FadeOut(arrow1), FadeOut(arrow1_text),
            FadeOut(arrow2), FadeOut(arrow2_text),
            FadeOut(arrow3), FadeOut(arrow3_text),
            FadeOut(explanation_text), FadeOut(title)
        )

    def visualize_protocol_adapter(self):
        """Visualisiert einen Protokolladapter (SOAP zu REST)"""
        # Titel
        title = Text("Protokolladapter: SOAP zu REST", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Sequenzdiagramm-Komponenten
        client = self.create_class_box("Client", BLUE, font_size=20, height=0.8, width=1.5)
        client.to_edge(LEFT).shift(UP * 2)
        
        adapter = self.create_class_box("SoapToRestAdapter", YELLOW, font_size=18, height=0.8, width=2)
        adapter.next_to(client, RIGHT, buff=3)
        
        service = self.create_class_box("SoapService", RED, font_size=20, height=0.8, width=1.5)
        service.next_to(adapter, RIGHT, buff=3)
        
        # Aktivitätslinien
        client_line = DashedLine(
            client.get_bottom(),
            client.get_bottom() + DOWN * 5,
            color=BLUE
        )
        
        adapter_line = DashedLine(
            adapter.get_bottom(),
            adapter.get_bottom() + DOWN * 5,
            color=YELLOW
        )
        
        service_line = DashedLine(
            service.get_bottom(),
            service.get_bottom() + DOWN * 5,
            color=RED
        )
        
        self.play(
            Create(client), Create(adapter), Create(service),
            Create(client_line), Create(adapter_line), Create(service_line)
        )
        
        # Sequenzdiagramm-Pfeile
        messages = VGroup()
        
        # 1. REST GET Anfrage
        get_arrow = Arrow(
            client_line.get_start() + DOWN * 0.5,
            adapter_line.get_start() + DOWN * 0.5,
            buff=0.1, color=BLUE
        )
        get_text = Text("GET /resource/123", font_size=16).next_to(get_arrow, UP, buff=0.1)
        messages.add(get_arrow, get_text)
        
        # 2. Adapter erstellt SOAP Request
        create_soap_text = Text("Erstelle SOAP-Request", font_size=16)
        create_soap_text.next_to(adapter_line.get_start() + DOWN * 1, RIGHT, buff=0.5)
        create_soap_text.set_color(YELLOW)
        messages.add(create_soap_text)
        
        # 3. SOAP Anfrage
        soap_arrow = Arrow(
            adapter_line.get_start() + DOWN * 1.5,
            service_line.get_start() + DOWN * 1.5,
            buff=0.1, color=YELLOW
        )
        soap_text = Text("executeRequest(soapRequest)", font_size=16).next_to(soap_arrow, UP, buff=0.1)
        messages.add(soap_arrow, soap_text)
        
        # 4. SOAP Antwort
        soap_response_arrow = Arrow(
            service_line.get_start() + DOWN * 2,
            adapter_line.get_start() + DOWN * 2,
            buff=0.1, color=RED
        )
        soap_response_text = Text("SoapResponse", font_size=16).next_to(soap_response_arrow, UP, buff=0.1)
        messages.add(soap_response_arrow, soap_response_text)
        
        # 5. Adapter konvertiert Antwort
        convert_text = Text("Konvertiere SOAP zu REST", font_size=16)
        convert_text.next_to(adapter_line.get_start() + DOWN * 2.5, RIGHT, buff=0.5)
        convert_text.set_color(YELLOW)
        messages.add(convert_text)
        
        # 6. REST Antwort
        rest_response_arrow = Arrow(
            adapter_line.get_start() + DOWN * 3,
            client_line.get_start() + DOWN * 3,
            buff=0.1, color=YELLOW
        )
        rest_response_text = Text("RestResponse", font_size=16).next_to(rest_response_arrow, UP, buff=0.1)
        messages.add(rest_response_arrow, rest_response_text)
        
        # Animation der Nachrichten sequentiell
        self.play(Create(get_arrow), Write(get_text))
        self.wait(0.5)
        self.play(Write(create_soap_text))
        self.wait(0.5)
        self.play(Create(soap_arrow), Write(soap_text))
        self.wait(0.5)
        self.play(Create(soap_response_arrow), Write(soap_response_text))
        self.wait(0.5)
        self.play(Write(convert_text))
        self.wait(0.5)
        self.play(Create(rest_response_arrow), Write(rest_response_text))
        
        # Codebeispiel
        code_box = Rectangle(height=3.8, width=6, color=WHITE).set_fill(BLACK, opacity=0.8)
        code_box.to_edge(DOWN, buff=0.5)
        
        code_text = """
// Client ruft REST-Methode auf
RestResponse getResponse = adapter.get("123");

// Im Adapter:
public RestResponse get(String resourceId) {
    // Erstelle SOAP-Anfrage aus REST-Parametern
    SoapRequest soapRequest = new SoapRequest("GetResource", resourceId);
    
    // Führe SOAP-Anfrage aus und konvertiere die Antwort
    SoapResponse soapResponse = soapService.executeRequest(soapRequest);
    return convertToRestResponse(soapResponse);
}
        """
        
        code = Text(code_text, font_size=16).move_to(code_box)
        code_group = VGroup(code_box, code)
        
        self.play(Create(code_box), Write(code))
        
        self.wait(3)
        self.play(
            FadeOut(client), FadeOut(adapter), FadeOut(service),
            FadeOut(client_line), FadeOut(adapter_line), FadeOut(service_line),
            FadeOut(messages), FadeOut(code_group), FadeOut(title)
        )

    def visualize_legacy_adapter(self):
        """Visualisiert einen Legacy-System-Adapter"""
        # Titel
        title = Text("Legacy-System-Adapter", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Legende erstellen
        legend_group = VGroup()
        
        # Legende für Modernes System
        modern_rect = Rectangle(height=0.4, width=0.6, color=BLUE).set_fill(BLUE, opacity=0.2)
        modern_text = Text("Modernes System", font_size=16).next_to(modern_rect, RIGHT, buff=0.2)
        modern_legend = VGroup(modern_rect, modern_text)
        
        # Legende für Adapter
        adapter_rect = Rectangle(height=0.4, width=0.6, color=YELLOW).set_fill(YELLOW, opacity=0.2)
        adapter_text = Text("Adapter", font_size=16).next_to(adapter_rect, RIGHT, buff=0.2)
        adapter_legend = VGroup(adapter_rect, adapter_text).next_to(modern_legend, DOWN, aligned_edge=LEFT, buff=0.2)
        
        # Legende für Legacy-System
        legacy_rect = Rectangle(height=0.4, width=0.6, color=RED).set_fill(RED, opacity=0.2)
        legacy_text = Text("Legacy-System", font_size=16).next_to(legacy_rect, RIGHT, buff=0.2)
        legacy_legend = VGroup(legacy_rect, legacy_text).next_to(adapter_legend, DOWN, aligned_edge=LEFT, buff=0.2)
        
        legend_group = VGroup(modern_legend, adapter_legend, legacy_legend).to_edge(LEFT, buff=0.5)
        
        # Klassen für die Visualisierung
        user_interface = self.create_class_box(
            "UserManagementSystem\\n(Interface)", 
            BLUE, font_size=16, height=1.2, width=2.5
        )
        user_interface.shift(UP * 2)
        
        user_class = self.create_class_box(
            "User\\nid: String, username: String", 
            BLUE, font_size=16, height=1.2, width=2.5
        )
        user_class.next_to(user_interface, LEFT, buff=2)
        
        adapter_class = self.create_class_box(
            "LegacyUserSystemAdapter\\nimplements UserManagementSystem", 
            YELLOW, font_size=16, height=1.2, width=2.5
        )
        adapter_class.next_to(user_interface, DOWN, buff=2)
        
        legacy_system = self.create_class_box(
            "LegacyUserSystem\\nusers: Map<Integer, LegacyUser>", 
            RED, font_size=16, height=1.2, width=2.5
        )
        legacy_system.next_to(adapter_class, RIGHT, buff=2)
        
        legacy_user = self.create_class_box(
            "LegacyUser\\nuserId: int, login: String", 
            RED, font_size=16, height=1.2, width=2.5
        )
        legacy_user.next_to(user_class, DOWN, buff=2)
        
        # Pfeile zwischen Klassen
        arrows = VGroup()
        
        # Implementiert-Pfeil
        impl_arrow = Arrow(adapter_class.get_top(), user_interface.get_bottom(), 
                         buff=0.1, color=YELLOW)
        impl_text = Text("implementiert", font_size=16).next_to(impl_arrow, LEFT, buff=0.2)
        arrows.add(impl_arrow, impl_text)
        
        # Adaptiert-Pfeil
        adapt_arrow = Arrow(adapter_class.get_right(), legacy_system.get_left(), 
                          buff=0.1, color=YELLOW)
        adapt_text = Text("adaptiert", font_size=16).next_to(adapt_arrow, UP, buff=0.2)
        arrows.add(adapt_arrow, adapt_text)
        
        # Konvertierungs-Pfeile
        convert_arrow1 = Arrow(adapter_class.get_left(), user_class.get_bottom(), 
                             buff=0.1, color=YELLOW, max_tip_length_to_length_ratio=0.1)
        convert_text1 = Text("convertLegacyUser()", font_size=16).next_to(convert_arrow1, UP, buff=0.2)
        arrows.add(convert_arrow1, convert_text1)
        
        convert_arrow2 = Arrow(adapter_class.get_bottom() + LEFT * 0.5, 
                             legacy_user.get_right(), 
                             buff=0.1, color=YELLOW, max_tip_length_to_length_ratio=0.1)
        convert_text2 = Text("convertToLegacyUser()", font_size=16).next_to(convert_arrow2, DOWN, buff=0.2)
        arrows.add(convert_arrow2, convert_text2)
        
        # Anzeigen der Komponenten
        self.play(FadeIn(legend_group))
        self.play(
            Create(user_interface), Create(user_class),
            Create(adapter_class), Create(legacy_system), Create(legacy_user)
        )
        self.play(Create(arrows))
        
        # Erklärungstext
        explanation = Text(
            "Der Adapter ermöglicht die Integration eines Legacy-Systems in eine moderne Architektur,\\n" +
            "indem er die Datenstrukturen und Methodensignaturen konvertiert.",
            font_size=24
        ).to_edge(DOWN, buff=0.5)
        
        self.play(Write(explanation))
        
        self.wait(3)
        self.play(
            FadeOut(legend_group),
            FadeOut(user_interface), FadeOut(user_class),
            FadeOut(adapter_class), FadeOut(legacy_system), FadeOut(legacy_user),
            FadeOut(arrows), FadeOut(explanation), FadeOut(title)
        )

    def visualize_format_adapter(self):
        """Visualisiert einen Format-Adapter für Datenkonvertierung"""
        # Titel
        title = Text("Format-Adapter für Datenkonvertierung", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Zentrale Datenklasse
        data_obj = Rectangle(height=2, width=3, color=WHITE).set_fill(WHITE, opacity=0.1)
        data_text = VGroup(
            Text("DataObject", font_size=24, color=WHITE),
            Text("id: String", font_size=18, color=WHITE),
            Text("name: String", font_size=18, color=WHITE),
            Text("value: int", font_size=18, color=WHITE)
        ).arrange(DOWN, buff=0.1).move_to(data_obj)
        data_group = VGroup(data_obj, data_text).move_to(ORIGIN)
        
        # Formate erstellen
        # XML-Format
        xml_rect = Rectangle(height=2, width=3, color=BLUE).set_fill(BLUE, opacity=0.1)
        xml_text = VGroup(
            Text("XML Format", font_size=20, color=BLUE),
            Text("<data>", font_size=16),
            Text("  <id>123</id>", font_size=16),
            Text("  <name>Test</name>", font_size=16),
            Text("</data>", font_size=16)
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.05).move_to(xml_rect)
        xml_group = VGroup(xml_rect, xml_text).to_edge(UP).shift(LEFT * 3.5)
        
        # JSON-Format
        json_rect = Rectangle(height=2, width=3, color=GREEN).set_fill(GREEN, opacity=0.1)
        json_text = VGroup(
            Text("JSON Format", font_size=20, color=GREEN),
            Text("{", font_size=16),
            Text('  "id": "123",', font_size=16),
            Text('  "name": "Test"', font_size=16),
            Text("}", font_size=16)
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.05).move_to(json_rect)
        json_group = VGroup(json_rect, json_text).to_edge(UP).shift(RIGHT * 3.5)
        
        # CSV-Format (Legacy)
        csv_rect = Rectangle(height=1.5, width=3, color=RED).set_fill(RED, opacity=0.1)
        csv_text = VGroup(
            Text("Legacy CSV Format", font_size=20, color=RED),
            Text("123;Test;Beschreibung;42", font_size=16)
        ).arrange(DOWN, buff=0.1).move_to(csv_rect)
        csv_group = VGroup(csv_rect, csv_text).next_to(data_group, DOWN, buff=2)
        
        # Konvertierungspfeile
        arrows = VGroup()
        
        # XML <-> DataObject
        xml_to_data = Arrow(xml_group.get_bottom(), data_group.get_top() + LEFT, buff=0.2, color=BLUE)
        xml_to_data_text = Text("fromXml()", font_size=16).next_to(xml_to_data, LEFT, buff=0.1)
        arrows.add(xml_to_data, xml_to_data_text)
        
        data_to_xml = Arrow(data_group.get_top() + LEFT, xml_group.get_bottom(), buff=0.2, color=BLUE)
        data_to_xml_text = Text("toXml()", font_size=16).next_to(data_to_xml, RIGHT, buff=0.1)
        arrows.add(data_to_xml, data_to_xml_text)
        
        # JSON <-> DataObject
        json_to_data = Arrow(json_group.get_bottom(), data_group.get_top() + RIGHT, buff=0.2, color=GREEN)
        json_to_data_text = Text("fromJson()", font_size=16).next_to(json_to_data, RIGHT, buff=0.1)
        arrows.add(json_to_data, json_to_data_text)
        
        data_to_json = Arrow(data_group.get_top() + RIGHT, json_group.get_bottom(), buff=0.2, color=GREEN)
        data_to_json_text = Text("toJson()", font_size=16).next_to(data_to_json, LEFT, buff=0.1)
        arrows.add(data_to_json, data_to_json_text)
        
        # CSV <-> DataObject
        csv_to_data = Arrow(csv_group.get_top(), data_group.get_bottom(), buff=0.2, color=RED)
        csv_to_data_text = Text("LegacyCSVAdapter", font_size=16).next_to(csv_to_data, LEFT, buff=0.1)
        arrows.add(csv_to_data, csv_to_data_text)
        
        data_to_csv = Arrow(data_group.get_bottom(), csv_group.get_top(), buff=0.2, color=RED)
        data_to_csv_text = Text("convertToCSV()", font_size=16).next_to(data_to_csv, RIGHT, buff=0.1)
        arrows.add(data_to_csv, data_to_csv_text)
        
        # Animation der Komponenten
        self.play(Create(data_group))
        self.play(Create(xml_group), Create(json_group), Create(csv_group))
        
        # Animation der Pfeile nach Format-Gruppen
        self.play(
            Create(xml_to_data), Write(xml_to_data_text),
            Create(data_to_xml), Write(data_to_xml_text)
        )
        self.play(
            Create(json_to_data), Write(json_to_data_text),
            Create(data_to_json), Write(data_to_json_text)
        )
        self.play(
            Create(csv_to_data), Write(csv_to_data_text),
            Create(data_to_csv), Write(data_to_csv_text)
        )
        
        # Erklärungstext
        explanation = Text(
            "Der Format-Adapter ermöglicht die Konvertierung zwischen modernen Formaten\\n" +
            "(XML, JSON) und dem Legacy-CSV-Format, das von älteren Systemen verwendet wird.",
            font_size=24
        ).to_edge(DOWN, buff=0.5)
        
        self.play(Write(explanation))
        
        self.wait(3)
        self.play(
            FadeOut(data_group), FadeOut(xml_group), FadeOut(json_group), FadeOut(csv_group),
            FadeOut(arrows), FadeOut(explanation), FadeOut(title)
        )

    def summarize_adapter_pattern(self):
        """Zusammenfassung des Adapter-Patterns"""
        # Titel
        title = Text("Zusammenfassung: Adapter-Pattern", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Anwendungsfälle
        use_cases = VGroup(
            Text("Anwendungsfälle:", font_size=36),
            Text("• Protokolladaption (z.B. REST zu SOAP)", font_size=24),
            Text("• Legacy-System-Integration", font_size=24),
            Text("• Datenkonvertierung zwischen Formaten", font_size=24),
            Text("• API-Harmonisierung", font_size=24),
            Text("• Dienst-Virtualisierung", font_size=24)
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.3).to_edge(LEFT, buff=1)
        
        # Vorteile
        advantages = VGroup(
            Text("Vorteile:", font_size=30, color=GREEN),
            Text("• Wiederverwendbarkeit bestehender Komponenten", font_size=20),
            Text("• Kompatibilität verschiedener Schnittstellen", font_size=20),
            Text("• Entkopplung inkompatibler Komponenten", font_size=20)
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.2)
        
        # Nachteile
        disadvantages = VGroup(
            Text("Nachteile:", font_size=30, color=RED),
            Text("• Zusätzliche Komplexität", font_size=20),
            Text("• Potentieller Leistungsoverhead", font_size=20),
            Text("• Möglicher Datenverlust bei Konvertierung", font_size=20)
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.2)
        
        # Pros und Cons gruppieren
        pros_cons = VGroup(advantages, disadvantages).arrange(DOWN, aligned_edge=LEFT, buff=0.4)
        pros_cons.to_edge(RIGHT, buff=1)
        
        # Animation der Zusammenfassung
        self.play(Write(use_cases))
        self.play(Write(advantages))
        self.play(Write(disadvantages))
        
        # Fazit
        conclusion = Text(
            "Das Adapter-Pattern ist ein mächtiges Werkzeug in verteilten Systemen,\\n" +
            "um unterschiedliche Komponenten nahtlos zu integrieren und\\n" +
            "die Interoperabilität zwischen verschiedenen Technologien zu ermöglichen.",
            font_size=24
        ).to_edge(DOWN, buff=0.5)
        
        self.play(Write(conclusion))
        
        self.wait(4)
        self.play(
            FadeOut(title), FadeOut(use_cases), 
            FadeOut(advantages), FadeOut(disadvantages),
            FadeOut(conclusion)
        )
        
        # Endtitel
        end_title = Text("Ende der Präsentation", font_size=64)
        sub_end = Text("Strukturelle Entwurfsmuster: Adapter-Pattern", font_size=36).next_to(end_title, DOWN)
        
        self.play(Write(end_title), Write(sub_end))
        self.wait(2)
        self.play(FadeOut(end_title), FadeOut(sub_end))
