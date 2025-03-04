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
        title = Text("Das Adapter-Pattern", font_size=72)
        subtitle = Text("Ein strukturelles Entwurfsmuster für Schnittstellenkonvertierung", 
                      font_size=36).next_to(title, DOWN, buff=0.5)
        
        self.play(Write(title), run_time=1.5)
        self.play(FadeIn(subtitle), run_time=1)
        self.wait(2)
        self.play(FadeOut(title), FadeOut(subtitle))
    
    def explain_adapter_basics(self):
        # Grundkonzept des Adapter-Patterns
        title = Text("Grundkonzept des Adapter-Patterns", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Klassen erstellen
        client_rect = Rectangle(height=1.5, width=3).set_fill(BLUE, opacity=0.2)
        client_text = Text("Client", font_size=32).move_to(client_rect)
        client = VGroup(client_rect, client_text).to_edge(LEFT).shift(UP)
        
        target_rect = Rectangle(height=1.5, width=3).set_fill(GREEN, opacity=0.2)
        target_text = Text("Target Interface", font_size=32).move_to(target_rect)
        target = VGroup(target_rect, target_text).next_to(client, RIGHT, buff=2)
        
        adapter_rect = Rectangle(height=1.5, width=3).set_fill(YELLOW, opacity=0.2)
        adapter_text = Text("Adapter", font_size=32).move_to(adapter_rect)
        adapter = VGroup(adapter_rect, adapter_text).next_to(target, DOWN, buff=2)
        
        adaptee_rect = Rectangle(height=1.5, width=3).set_fill(RED, opacity=0.2)
        adaptee_text = Text("Adaptee", font_size=32).move_to(adaptee_rect)
        adaptee = VGroup(adaptee_rect, adaptee_text).next_to(adapter, RIGHT, buff=2)
        
        # Pfeile zwischen Klassen
        arrow1 = Arrow(client.get_right(), target.get_left())
        arrow1_text = Text("verwendet", font_size=24).next_to(arrow1, UP, buff=0.1)
        
        arrow2 = Arrow(target.get_bottom(), adapter.get_top())
        arrow2_text = Text("implementiert", font_size=24).next_to(arrow2, LEFT, buff=0.1)
        
        arrow3 = Arrow(adapter.get_right(), adaptee.get_left())
        arrow3_text = Text("adaptiert", font_size=24).next_to(arrow3, UP, buff=0.1)
        
        # Animation zeigen
        self.play(Create(client), Create(target), Create(adapter), Create(adaptee))
        self.play(Create(arrow1), Write(arrow1_text))
        self.play(Create(arrow2), Write(arrow2_text))
        self.play(Create(arrow3), Write(arrow3_text))
        
        self.wait(2)
        
        # Erklärungstext
        explanation = """
        Das Adapter-Pattern konvertiert die Schnittstelle einer Klasse 
        in eine andere Schnittstelle, die von Clients erwartet wird.
        Es ermöglicht die Zusammenarbeit von Klassen mit 
        inkompatiblen Schnittstellen.
        """
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
        # Protokolladapter-Demo: SOAP zu REST
        title = Text("Protokolladapter: SOAP zu REST", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Grafische Darstellung des Sequenzdiagramms
        client = Rectangle(height=0.8, width=1.5, color=BLUE).set_fill(BLUE, opacity=0.2)
        client_text = Text("Client", font_size=20).move_to(client)
        client_group = VGroup(client, client_text).to_edge(LEFT).shift(UP * 2)
        
        adapter = Rectangle(height=0.8, width=1.5, color=YELLOW).set_fill(YELLOW, opacity=0.2)
        adapter_text = Text("SoapToRestAdapter", font_size=18).move_to(adapter)
        adapter_group = VGroup(adapter, adapter_text).next_to(client_group, RIGHT, buff=3)
        
        soap_service = Rectangle(height=0.8, width=1.5, color=RED).set_fill(RED, opacity=0.2)
        soap_text = Text("SoapService", font_size=20).move_to(soap_service)
        soap_group = VGroup(soap_service, soap_text).next_to(adapter_group, RIGHT, buff=3)
        
        # Aktivitätslinien
        client_line = Line(
            client_group.get_bottom(),
            client_group.get_bottom() + DOWN * 5,
            color=BLUE
        )
        
        adapter_line = Line(
            adapter_group.get_bottom(),
            adapter_group.get_bottom() + DOWN * 5,
            color=YELLOW
        )
        
        soap_line = Line(
            soap_group.get_bottom(),
            soap_group.get_bottom() + DOWN * 5,
            color=RED
        )
        
        self.play(
            Create(client_group), Create(adapter_group), Create(soap_group),
            Create(client_line), Create(adapter_line), Create(soap_line)
        )
        
        # Sequenzdiagramm-Pfeile
        # REST GET Anfrage
        get_arrow = Arrow(
            client_line.get_start() + DOWN * 0.5,
            adapter_line.get_start() + DOWN * 0.5,
            buff=0.1, color=BLUE
        )
        get_text = Text("GET /resource/123", font_size=16).next_to(get_arrow, UP, buff=0.1)
        
        # Adapter erstellt SOAP Request
        create_soap_text = Text("Erstelle SOAP-Request", font_size=16)
        create_soap_text.next_to(adapter_line.get_start() + DOWN * 1, RIGHT, buff=0.5)
        create_soap_text.set_color(YELLOW)
        
        # SOAP Anfrage
        soap_arrow = Arrow(
            adapter_line.get_start() + DOWN * 1.5,
            soap_line.get_start() + DOWN * 1.5,
            buff=0.1, color=YELLOW
        )
        soap_text = Text("executeRequest(soapRequest)", font_size=16).next_to(soap_arrow, UP, buff=0.1)
        
        # SOAP Antwort
        soap_response_arrow = Arrow(
            soap_line.get_start() + DOWN * 2,
            adapter_line.get_start() + DOWN * 2,
            buff=0.1, color=RED
        )
        soap_response_text = Text("SoapResponse", font_size=16).next_to(soap_response_arrow, UP, buff=0.1)
        
        # Adapter konvertiert Antwort
        convert_text = Text("Konvertiere SOAP zu REST", font_size=16)
        convert_text.next_to(adapter_line.get_start() + DOWN * 2.5, RIGHT, buff=0.5)
        convert_text.set_color(YELLOW)
        
        # REST Antwort
        rest_response_arrow = Arrow(
            adapter_line.get_start() + DOWN * 3,
            client_line.get_start() + DOWN * 3,
            buff=0.1, color=YELLOW
        )
        rest_response_text = Text("RestResponse", font_size=16).next_to(rest_response_arrow, UP, buff=0.1)
        
        # Animation der Sequenz
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
        
        # Code-Beispiel
        code_text = """
        // Client ruft REST-Methode auf
        RestResponse getResponse = adapter.get("123");
        
        // Im Adapter:
        public RestResponse get(String resourceId) {
            // Erstelle SOAP-Anfrage aus REST-Parametern
            SoapRequest soapRequest = new SoapRequest("GetResource", resourceId, null);
            
            // Führe SOAP-Anfrage aus
            SoapResponse soapResponse = soapService.executeRequest(soapRequest);
            
            // Konvertiere SOAP-Antwort zu REST-Antwort
            return convertToRestResponse(soapResponse);
        }
        """
        code = Code(
            code=code_text,
            language="java",
            font_size=18,
            line_spacing=0.5,
            background_stroke_width=0
        ).to_edge(DOWN, buff=0.5)
        
        self.play(Write(code))
        
        self.wait(3)
        self.play(
            FadeOut(client_group), FadeOut(adapter_group), FadeOut(soap_group),
            FadeOut(client_line), FadeOut(adapter_line), FadeOut(soap_line),
            FadeOut(get_arrow), FadeOut(get_text),
            FadeOut(create_soap_text),
            FadeOut(soap_arrow), FadeOut(soap_text),
            FadeOut(soap_response_arrow), FadeOut(soap_response_text),
            FadeOut(convert_text),
            FadeOut(rest_response_arrow), FadeOut(rest_response_text),
            FadeOut(code), FadeOut(title)
        )

    def visualize_legacy_adapter(self):
        # Legacy-System-Adapter
        title = Text("Legacy-System-Adapter", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Legende erstellen
        legend = VGroup()
        
        modern_rect = Rectangle(height=0.4, width=0.6, color=BLUE).set_fill(BLUE, opacity=0.2)
        modern_text = Text("Modernes System", font_size=16).next_to(modern_rect, RIGHT, buff=0.2)
        modern_legend = VGroup(modern_rect, modern_text)
        
        adapter_rect = Rectangle(height=0.4, width=0.6, color=YELLOW).set_fill(YELLOW, opacity=0.2)
        adapter_text = Text("Adapter", font_size=16).next_to(adapter_rect, RIGHT, buff=0.2)
        adapter_legend = VGroup(adapter_rect, adapter_text).next_to(modern_legend, DOWN, aligned_edge=LEFT)
        
        legacy_rect = Rectangle(height=0.4, width=0.6, color=RED).set_fill(RED, opacity=0.2)
        legacy_text = Text("Legacy-System", font_size=16).next_to(legacy_rect, RIGHT, buff=0.2)
        legacy_legend = VGroup(legacy_rect, legacy_text).next_to(adapter_legend, DOWN, aligned_edge=LEFT)
        
        legend = VGroup(modern_legend, adapter_legend, legacy_legend).to_edge(LEFT, buff=0.5)
        
        # Klassen für die Visualisierung
        user_interface = Rectangle(height=1.2, width=2.5, color=BLUE).set_fill(BLUE, opacity=0.2)
        user_interface_text = VGroup(
            Text("UserManagementSystem", font_size=16),
            Text("(Interface)", font_size=14)
        ).arrange(DOWN, buff=0.1).move_to(user_interface)
        user_interface_group = VGroup(user_interface, user_interface_text).shift(UP * 2)
        
        user_class = Rectangle(height=1.2, width=2.5, color=BLUE).set_fill(BLUE, opacity=0.2)
        user_class_text = VGroup(
            Text("User", font_size=16),
            Text("id: String, username: String", font_size=14)
        ).arrange(DOWN, buff=0.1).move_to(user_class)
        user_class_group = VGroup(user_class, user_class_text).next_to(user_interface_group, LEFT, buff=2)
        
        adapter_class = Rectangle(height=1.2, width=2.5, color=YELLOW).set_fill(YELLOW, opacity=0.2)
        adapter_class_text = VGroup(
            Text("LegacyUserSystemAdapter", font_size=16),
            Text("implements UserManagementSystem", font_size=14)
        ).arrange(DOWN, buff=0.1).move_to(adapter_class)
        adapter_class_group = VGroup(adapter_class, adapter_class_text).next_to(user_interface_group, DOWN, buff=2)
        
        legacy_interface = Rectangle(height=1.2, width=2.5, color=RED).set_fill(RED, opacity=0.2)
        legacy_interface_text = VGroup(
            Text("LegacyUserSystem", font_size=16),
            Text("users: Map<Integer, LegacyUser>", font_size=14)
        ).arrange(DOWN, buff=0.1).move_to(legacy_interface)
        legacy_interface_group = VGroup(legacy_interface, legacy_interface_text).next_to(adapter_class_group, RIGHT, buff=2)
        
        legacy_user = Rectangle(height=1.2, width=2.5, color=RED).set_fill(RED, opacity=0.2)
        legacy_user_text = VGroup(
            Text("LegacyUser", font_size=16),
            Text("userId: int, login: String", font_size=14)
        ).arrange(DOWN, buff=0.1).move_to(legacy_user)
        legacy_user_group = VGroup(legacy_user, legacy_user_text).next_to(user_class_group, DOWN, buff=2)
        
        # Pfeile zwischen Klassen
        impl_arrow = Arrow(adapter_class_group.get_top(), user_interface_group.get_bottom(), 
                         buff=0.1, color=YELLOW)
        impl_text = Text("implementiert", font_size=16).next_to(impl_arrow, LEFT, buff=0.2)
        
        adapt_arrow = Arrow(adapter_class_group.get_right(), legacy_interface_group.get_left(), 
                          buff=0.1, color=YELLOW)
        adapt_text = Text("adaptiert", font_size=16).next_to(adapt_arrow, UP, buff=0.2)
        
        convert_arrow1 = Arrow(adapter_class_group.get_left(), user_class_group.get_bottom(), 
                             buff=0.1, color=YELLOW, max_tip_length_to_length_ratio=0.1)
        convert_text1 = Text("convertLegacyUser()", font_size=16).next_to(convert_arrow1, DOWN, buff=0.2)
        
        convert_arrow2 = Arrow(adapter_class_group.get_bottom() + LEFT * 0.5, 
                             legacy_user_group.get_right(), 
                             buff=0.1, color=YELLOW, max_tip_length_to_length_ratio=0.1)
        convert_text2 = Text("convertToLegacyUser()", font_size=16).next_to(convert_arrow2, RIGHT, buff=0.2)
        
        # Anzeigen der Komponenten
        self.play(FadeIn(legend))
        self.play(
            Create(user_interface_group), Create(user_class_group),
            Create(adapter_class_group), Create(legacy_interface_group),
            Create(legacy_user_group)
        )
        self.play(
            Create(impl_arrow), Write(impl_text),
            Create(adapt_arrow), Write(adapt_text),
            Create(convert_arrow1), Write(convert_text1),
            Create(convert_arrow2), Write(convert_text2)
        )
        
        # Erklärungstext
        explanation = Text(
            "Der Adapter ermöglicht die Integration eines Legacy-Systems in eine moderne Architektur,\n" +
            "indem er die Datenstrukturen und Methodensignaturen konvertiert.",
            font_size=24
        ).to_edge(DOWN, buff=0.5)
        
        self.play(Write(explanation))
        
        self.wait(3)
        self.play(
            FadeOut(legend),
            FadeOut(user_interface_group), FadeOut(user_class_group),
            FadeOut(adapter_class_group), FadeOut(legacy_interface_group),
            FadeOut(legacy_user_group),
            FadeOut(impl_arrow), FadeOut(impl_text),
            FadeOut(adapt_arrow), FadeOut(adapt_text),
            FadeOut(convert_arrow1), FadeOut(convert_text1),
            FadeOut(convert_arrow2), FadeOut(convert_text2),
            FadeOut(explanation), FadeOut(title)
        )

    def visualize_format_adapter(self):
        # Format-Adapter
        title = Text("Format-Adapter für Datenkonvertierung", font_size=48)
        self.play(Write(title))
        self.play(title.animate.to_edge(UP))
        
        # Darstellung der verschiedenen Datenformate
        formats = VGroup()
        
        # Daten-Objekt in der Mitte
        data_obj = Rectangle(height=2, width=3, color=WHITE).set_fill(WHITE, opacity=0.1)
        data_text = VGroup(
            Text("DataObject", font_size=24, color=WHITE),
            Text("id: String", font_size=18, color=WHITE),
            Text("name: String", font_size=18, color=WHITE),
            Text("value: int", font_size=18, color=WHITE)
        ).arrange(DOWN, buff=0.1).move_to(data_obj)
        data_group = VGroup(data_obj, data_text).move_to(ORIGIN)
        
        # XML-Format
        xml_rect = Rectangle(height=2, width=3, color=BLUE).set_fill(BLUE, opacity=0.1)
        xml_text = VGroup(
            Text("XML Format", font_size=20, color=BLUE),
            Text("<data>", font_size=16),
            Text("  <id>123</id>", font_size=16),
            Text("  <name>Test</name>", font_size=16),
            Text("</data>", font_size=16)
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.05).move_to(xml_rect)
        xml_group = VGroup(xml_rect, xml_text).to_edge(UP+LEFT, buff=1.5)
        
        # JSON-Format
        json_rect = Rectangle(height=2, width=3, color=GREEN).set_fill(GREEN, opacity=0.1)
        json_text = VGroup(
            Text("JSON Format", font_size=20, color=GREEN),
            Text("{", font_size=16),
            Text('  "id": "123",', font_size=16),
            Text('  "name": "Test"', font_size=16),
            Text("}", font_size=16)
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.05).move_to(json_rect)
        json_group = VGroup(json_rect, json_text).to_edge(UP+RIGHT, buff=1.5)
        
        # CSV-Format (Legacy)
        csv_rect = Rectangle(height=2, width=3, color=RED).set_fill(RED, opacity=0.1)
        csv_text = VGroup(
            Text("Legacy CSV Format", font_size=20, color=RED),
            Text("123;Test;Beschreibung;42", font_size=16)
        ).arrange(DOWN, buff=0.1).move_to(csv_rect)
        csv_group = VGroup(csv_rect, csv_text).to_edge(DOWN, buff=1.5)
        
        # Pfeile für Konvertierungen
        xml_to_data = Arrow(xml_group.get_bottom(), data_group.get_top()+LEFT, buff=0.2, color=BLUE)
        xml_to_data_text = Text("fromXml()", font_size=16).next_to(xml_to_data, LEFT, buff=0.1)
        
        data_to_xml = Arrow(data_group.get_top()+LEFT, xml_group.get_bottom(), buff=0.2, color=BLUE)
        data_to_xml_text = Text("toXml()", font_size=16).next_to(data_to_xml, RIGHT, buff=0.1)
        
        json_to_data = Arrow(json_group.get_bottom(), data_group.get_top()+RIGHT, buff=0.2, color=GREEN)
        json_to_data_text = Text("fromJson()", font_size=16).next_to(json_to_data, RIGHT, buff=0.1)
        
        data_to_json = Arrow(data_group.get_top()+RIGHT, json_group.get_bottom(), buff=0.2, color=GREEN)
        data_to_json_text = Text("toJson()", font_size=16).next_to(data_to_json, LEFT, buff=0.1)
        
        csv_to_data = Arrow(csv_group.get_top(), data_group.get_bottom(), buff=0.2, color=RED)
        csv_to_data_text = Text("LegacyCSVAdapter", font_size=16).next_to(csv_to_data, LEFT, buff=0.1)
        
        data_to_csv = Arrow(data_group.get_bottom(), csv_group.get_top(), buff=0.2, color=RED)
        data_to_csv_text = Text("convertToCSV()", font_size=16).next_to(data_to_csv, RIGHT, buff=0.1)
        
        # Animation des Format-Adapters
        self.play(Create(data_group))
        self.play(Create(xml_group), Create(json_group), Create(csv_group))
        
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
        
        # Beispiel einer Formatkonvertierung
        example_text = Text(
            "Der LegacyCSVAdapter ermöglicht die Konvertierung zwischen modernen Formaten\n" +
            "(XML, JSON) und dem Legacy-CSV-Format, das von älteren Systemen verwendet wird.",
            font_size=24
        ).to_edge(DOWN, buff=0.5)
        
        self.play(Write(example_text))
        
        self.wait(3)
        self.play(
            FadeOut(data_group), FadeOut(xml_group), FadeOut(json_group), FadeOut(csv_group),
            FadeOut(xml_to_data), FadeOut(xml_to_data_text),
            FadeOut(data_to_xml), FadeOut(data_to_xml_text),
            FadeOut(json_to_data), FadeOut(json_to_data_text),
            FadeOut(data_to_json), FadeOut(data_to_json_text),
            FadeOut(csv_to_data), FadeOut(csv_to_data_text),
            FadeOut(data_to_csv), FadeOut(data_to_csv_text),
            FadeOut(example_text), FadeOut(title)
        )

    def summarize_adapter_pattern(self):
        # Zusammenfassung
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
        
        # Vorteile und Nachteile
        pros_cons = VGroup(
            Text("Vorteile:", font_size=30, color=GREEN),
            Text("• Wiederverwendbarkeit bestehender Komponenten", font_size=20),
            Text("• Kompatibilität verschiedener Schnittstellen", font_size=20),
            Text("• Entkopplung inkompatibler Komponenten", font_size=20),
            Text("Nachteile:", font_size=30, color=RED),
            Text("• Zusätzliche Komplexität", font_size=20),
            Text("• Potentieller Leistungsoverhead", font_size=20),
            Text("• Möglicher Datenverlust bei Konvertierung", font_size=20)
        ).arrange(DOWN, aligned_edge=LEFT, buff=0.2).to_edge(RIGHT, buff=1)
        
        # Animation der Zusammenfassung
        self.play(Write(use_cases))
        self.play(Write(pros_cons))
        
        # Fazit
        conclusion = Text(
            "Das Adapter-Pattern ist ein mächtiges Werkzeug in verteilten Systemen,\n" +
            "um unterschiedliche Komponenten nahtlos zu integrieren und\n" +
            "die Interoperabilität zwischen verschiedenen Technologien zu ermöglichen.",
            font_size=24
        ).to_edge(DOWN, buff=0.5)
        
        self.play(Write(conclusion))
        
        self.wait(4)
        self.play(
            FadeOut(title), FadeOut(use_cases), FadeOut(pros_cons), FadeOut(conclusion)
        )
        
        # Endtitel
        end_title = Text("Ende der Präsentation", font_size=64)
        sub_end = Text("Strukturelle Entwurfsmuster: Adapter-Pattern", font_size=36).next_to(end_title, DOWN)
        
        self.play(Write(end_title), Write(sub_end))
        self.wait(2)
        self.play(FadeOut(end_title), FadeOut(sub_end))
