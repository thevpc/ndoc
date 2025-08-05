package net.thevpc.ntexup.debug;


import net.thevpc.ntexup.api.engine.NTxEngine;

import javax.swing.*;
import java.awt.*;

public class NTxDebugPanel extends JPanel {
    private NTxDebugModel model = new NTxDebugModel();
    private NTxDocumentPanel compiledDocument;
    private NTxDocumentPanel rawDocument;
    private NTxNodePanel page;
    private JTextAreaNTxMessageList textAreaHMessageList;

    public NTxDebugPanel(NTxEngine engine) {
        super(new BorderLayout());
        model.setEngine(engine);
        textAreaHMessageList = new JTextAreaNTxMessageList();
        model.setMessageList(textAreaHMessageList);
        JTabbedPane pane = new JTabbedPane();
        add(pane);
        pane.addTab("Page", page = new NTxNodePanel(
                model.getEngine(),
                () -> model.getCurrentPage()
        ));
        pane.addTab("Document", compiledDocument = new NTxDocumentPanel(
                model.getEngine(),
                () -> model.getCompiledDocument(),
                textAreaHMessageList
        ));
        pane.addTab("Raw", rawDocument = new NTxDocumentPanel(
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

    public NTxDebugModel model() {
        return model;
    }
}
