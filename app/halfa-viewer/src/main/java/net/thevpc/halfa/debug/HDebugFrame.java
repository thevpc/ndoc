package net.thevpc.halfa.debug;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HLogger;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.base.renderer.HImageUtils;
import net.thevpc.halfa.spi.renderer.HDocumentRendererListener;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRendererConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class HDebugFrame extends JFrame {
    private HDebugPanel debugPanel;
    private Runnable onClose;
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

        @Override
        public void onChangedPage(HNode page) {
            model().setCurrentPage(page);
            updateContent();
        }

        @Override
        public void onSaveDocument(HDocument document, HDocumentStreamRendererConfig config) {

        }

        @Override
        public void onCloseView() {
            model().setCurrentPage(null);
            model().setCompiledDocument(null);
            model().setRawDocument(null);
            updateContent();
        }
    };

    public Runnable getOnClose() {
        return onClose;
    }

    public HDebugFrame setOnClose(Runnable onClose) {
        this.onClose = onClose;
        return this;
    }

    public HDebugFrame(HEngine engine) {

        setTitle("DebugFrame");
        setContentPane(debugPanel = new HDebugPanel(engine));
        setMinimumSize(new Dimension(400, 600));
        this.setIconImage(
                HImageUtils.resizeImage(
                        new ImageIcon(getClass().getResource("/net/thevpc/halfa/halfa.png")).getImage(),
                        16, 16)
        );
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                if(onClose != null) {
                    onClose.run();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if(onClose != null) {
                    onClose.run();
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
    }

    public HLogger messages() {
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
