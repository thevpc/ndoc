package net.thevpc.halfa.main;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.debug.HDebugFrame;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentRendererListener;
import net.thevpc.halfa.spi.renderer.HDocumentScreenRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NStringUtils;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ServiceHelper {
    MainFrame mainFrame;
    HEngine engine;
    private List<HDocumentRendererListener> registeredHDocumentRendererListener = new ArrayList<>();
    private List<HMessageList> registeredMessages = new ArrayList<>();
    HDocumentRendererListener currListener = new HDocumentRendererListener() {
        @Override
        public void onChangedCompiledDocument(HDocument compiledDocument) {
            for (HDocumentRendererListener r : registeredHDocumentRendererListener) {
                r.onChangedCompiledDocument(compiledDocument);
            }
        }

        @Override
        public void onChangedRawDocument(HDocument rawDocument) {
            for (HDocumentRendererListener r : registeredHDocumentRendererListener) {
                r.onChangedRawDocument(rawDocument);
            }
        }

        @Override
        public void onChangedPage(HNode page) {
            for (HDocumentRendererListener r : registeredHDocumentRendererListener) {
                r.onChangedPage(page);
            }
        }

        @Override
        public void onCloseView() {
            for (HDocumentRendererListener r : registeredHDocumentRendererListener) {
                r.onCloseView();
            }
        }
    };
    private HMessageList currentMessageList=new HMessageList() {
        @Override
        public void addMessage(HMessageType type, NMsg message, Throwable error, HResource source) {
            for (HMessageList r : registeredMessages) {
                r.addMessage(type, message, error, source);
            }
        }
    };
    public ServiceHelper(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        engine = new HEngineImpl(mainFrame.getSession());
    }

    private static NPath confPath(NSession session) {
        NPath c = session.getAppConfFolder();
        if (c == null) {
            c = NPath.ofUserHome(session).resolve(".config");
        }
        return c.resolve("HalfaOpenFile.conf");
    }

    private static NPath loadPath(NSession session) {
        try {
            String s = NStringUtils.trimToNull(confPath(session).readString());
            if (s != null) {
                return NPath.of(s, session);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static void savePath(NPath path, NSession session) {
        try {
            NPath p = confPath(session);
            p.getParent().mkdirs();
            p.writeString(path.toAbsolute().normalize().toString());
        } catch (Exception e) {
            //
        }
    }

    public void showDebug() {
        HDebugFrame debugFrame = new HDebugFrame(engine);
        registeredMessages.add(debugFrame.messages());
        registeredHDocumentRendererListener.add(debugFrame.rendererListener());
        debugFrame.setOnClose(new Runnable() {
            @Override
            public void run() {
                registeredMessages.remove(debugFrame.messages());
                registeredHDocumentRendererListener.remove(debugFrame.rendererListener());
            }
        });
        debugFrame.run();
    }

    public void showOpenFile() {
        NSession session = mainFrame.getSession();
        NPath nPath = loadPath(session);
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (nPath != null) {
            f.setCurrentDirectory(nPath.toFile().get());
        }
        int r = f.showOpenDialog(mainFrame.getContentPane());
        if (r == JFileChooser.APPROVE_OPTION) {
            File sf = f.getSelectedFile();
            if (sf != null) {
                NPath path = NPath.of(sf, session);
                savePath(path, session);
                HDocumentScreenRenderer renderer = engine.newScreenRenderer();
                renderer.setMessages(currentMessageList);
                renderer.addRendererListener(currListener);
                renderer.renderPath(path);
            }
        }
    }

    public void doExit() {
        System.exit(0);
    }
}
