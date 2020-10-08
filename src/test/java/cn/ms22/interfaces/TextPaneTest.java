package cn.ms22.interfaces;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.io.IOException;

public class TextPaneTest extends JFrame {

    public TextPaneTest() throws IOException, BadLocationException {
        JPanel basePanel = new JPanel();
        JTextPane jTextPane = new JTextPane();

        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        StyleSheet styleSheet = new StyleSheet();
        styleSheet.addRule(".redColor {color: red;}");
        styleSheet.addRule(".blueColor {color: blue;}");
        htmlEditorKit.setStyleSheet(styleSheet);

        HTMLDocument htmlDocument = (HTMLDocument) htmlEditorKit.createDefaultDocument();
        jTextPane.setDocument(htmlDocument);

        htmlDocument.insertAfterEnd(htmlDocument
                .getCharacterElement(htmlDocument.getEndPosition().getOffset()),
                "<p class=blueColor>Good bye!</p>");
        htmlDocument.insertAfterEnd(htmlDocument
                        .getCharacterElement(htmlDocument.getEndPosition().getOffset()),
                "<p class=redColor>Hello</p>");

        basePanel.add(jTextPane);
        this.add(basePanel);
        this.setBounds(0, 0, 660, 740);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    public static void main(String[] args) {
        try {
            new TextPaneTest();
        } catch (IOException | BadLocationException e) {
            e.printStackTrace();
        }
    }
}
