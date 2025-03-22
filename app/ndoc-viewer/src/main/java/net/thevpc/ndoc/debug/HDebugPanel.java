package net.thevpc.ndoc.debug;


import net.thevpc.ndoc.api.NDocEngine;

import javax.swing.*;
import java.awt.*;

public class HDebugPanel extends JPanel {
    private HDebugModel model = new HDebugModel();
    private HDocumentPanel compiledDocument;
    private HDocumentPanel rawDocument;
    private HNodePanel page;
    private JTextAreaHMessageList textAreaHMessageList;

    public HDebugPanel(NDocEngine engine) {
        super(new BorderLayout());
        model.setEngine(engine);
        textAreaHMessageList = new JTextAreaHMessageList();
        model.setMessageList(textAreaHMessageList);
        JTabbedPane pane = new JTabbedPane();
        add(pane);
        pane.addTab("Page", page = new HNodePanel(
                model.getEngine(),
                () -> model.getCurrentPage()
        ));
        pane.addTab("Document", compiledDocument = new HDocumentPanel(
                model.getEngine(),
                () -> model.getCompiledDocument()
        ));
        pane.addTab("Raw", rawDocument = new HDocumentPanel(
                model.getEngine(),
                () -> model.getRawDocument()
        ));
        pane.addTab("Messages", textAreaHMessageList);
    }

    public void updateContent() {
        page.updateContent();
        compiledDocument.updateContent();
        rawDocument.updateContent();
        textAreaHMessageList.updateContent();
    }

    public HDebugModel model() {
        return model;
    }
}
