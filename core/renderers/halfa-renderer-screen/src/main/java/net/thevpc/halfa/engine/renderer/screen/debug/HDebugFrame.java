package net.thevpc.halfa.engine.renderer.screen.debug;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.spi.renderer.HDocumentRendererListener;

import javax.swing.*;
import java.awt.*;

public class HDebugFrame extends JFrame {
    private HDebugPanel debugPanel;
    HDocumentRendererListener hDocumentRendererListener = new HDocumentRendererListener() {
        @Override
        public void onChangedCompiledDocument(HDocument compiledDocument) {
            model().setCompiledDocument(compiledDocument);
            updateContent();
        }

        @Override
        public void onChangedRawDocument(HDocument rawDocument) {
            model().setRawDocument(rawDocument);
            updateContent();
        }
    };

    public HDebugFrame(HEngine engine) {
        setTitle("DebugFrame");
        setContentPane(debugPanel = new HDebugPanel(engine));
        setMinimumSize(new Dimension(400, 600));
    }

    public HMessageList messages() {
        return model().messages();
    }
    public HDocumentRendererListener rendererListener() {
        return hDocumentRendererListener;
    }

    public HDebugModel model() {
        return debugPanel.model();
    }

    public void updateContent() {
        debugPanel.updateContent();
    }

    public void run() {
        updateContent();
        setVisible(true);
    }
}
