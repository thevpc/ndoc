package net.thevpc.halfa.engine.renderer.screen.debug;


import net.thevpc.halfa.api.HEngine;

import javax.swing.*;
import java.awt.*;

public class HDebugPanel extends JPanel {
    private HDebugModel model = new HDebugModel();
    private HDocumentPanel compiledDocument;
    private HDocumentPanel rawDocument;
    private JTextAreaHMessageList textAreaHMessageList;

    public HDebugPanel(HEngine engine) {
        super(new BorderLayout());
        model.setEngine(engine);
        textAreaHMessageList = new JTextAreaHMessageList();
        model.setMessageList(textAreaHMessageList);
        JTabbedPane pane = new JTabbedPane();
        add(pane);
        pane.addTab("Document", compiledDocument= new HDocumentPanel(
                model.getEngine(),
                () -> model.getCompiledDocument()
        ));
        pane.addTab("Raw", rawDocument=new HDocumentPanel(
                model.getEngine(),
                () -> model.getRawDocument()
        ));
        pane.addTab("Messages", textAreaHMessageList);
    }

    public void updateContent() {
        compiledDocument.updateContent();
        rawDocument.updateContent();
        textAreaHMessageList.updateContent();
    }

    public HDebugModel model() {
        return model;
    }
}
