package net.thevpc.ntexup.debug;


import net.thevpc.ntexup.api.engine.NTxEngine;

import javax.swing.*;
import java.awt.*;

public class NDocDebugPanel extends JPanel {
    private NDocDebugModel model = new NDocDebugModel();
    private NDocDocumentPanel compiledDocument;
    private NDocDocumentPanel rawDocument;
    private NDocNodePanel page;
    private JTextAreaNDocMessageList textAreaHMessageList;

    public NDocDebugPanel(NTxEngine engine) {
        super(new BorderLayout());
        model.setEngine(engine);
        textAreaHMessageList = new JTextAreaNDocMessageList();
        model.setMessageList(textAreaHMessageList);
        JTabbedPane pane = new JTabbedPane();
        add(pane);
        pane.addTab("Page", page = new NDocNodePanel(
                model.getEngine(),
                () -> model.getCurrentPage()
        ));
        pane.addTab("Document", compiledDocument = new NDocDocumentPanel(
                model.getEngine(),
                () -> model.getCompiledDocument(),
                textAreaHMessageList
        ));
        pane.addTab("Raw", rawDocument = new NDocDocumentPanel(
                model.getEngine(),
                () -> model.getRawDocument(),
                textAreaHMessageList
        ));
        pane.addTab("Messages", textAreaHMessageList);
    }

    public void updateContent() {
        page.updateContent();
        compiledDocument.updateContent();
        rawDocument.updateContent();
        textAreaHMessageList.updateContent();
    }

    public NDocDebugModel model() {
        return model;
    }
}
