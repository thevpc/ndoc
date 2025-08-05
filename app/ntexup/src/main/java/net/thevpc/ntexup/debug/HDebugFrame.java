package net.thevpc.ntexup.debug;

import net.thevpc.ntexup.api.engine.NDocEngine;
import net.thevpc.ntexup.api.document.NDocument;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.engine.util.NDocUtilsImages;
import net.thevpc.ntexup.api.renderer.NDocDocumentRendererListener;
import net.thevpc.ntexup.api.renderer.NDocDocumentStreamRendererConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class HDebugFrame extends JFrame {
    private NDocDebugPanel debugPanel;
    private Runnable onClose;
    NDocDocumentRendererListener hDocumentRendererListener = new NDocDocumentRendererListener() {
        @Override
        public void onChangedCompiledDocument(NDocument compiledDocument) {
            model().setCompiledDocument(compiledDocument);
            updateContent();
        }

        @Override
        public void onChangedRawDocument(NDocument rawDocument) {
            model().setRawDocument(rawDocument);
            updateContent();
        }

        @Override
        public void onChangedPage(NTxNode page) {
            model().setCurrentPage(page);
            updateContent();
        }

        @Override
        public void onSaveDocument(NDocument document, NDocDocumentStreamRendererConfig config) {

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

    public HDebugFrame(NDocEngine engine) {

        setTitle("DebugFrame");
        setContentPane(debugPanel = new NDocDebugPanel(engine));
        setMinimumSize(new Dimension(400, 600));
        this.setIconImage(
                NDocUtilsImages.resizeImage(
                        new ImageIcon(getClass().getResource("/net/thevpc/ntexup/ndoc.png")).getImage(),
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

    public NDocLogger messages() {
        return model().messages();
    }

    public NDocDocumentRendererListener rendererListener() {
        return hDocumentRendererListener;
    }

    public NDocDebugModel model() {
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
