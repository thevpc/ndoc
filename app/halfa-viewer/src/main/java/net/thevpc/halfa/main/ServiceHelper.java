package net.thevpc.halfa.main;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.debug.HDebugFrame;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentRendererListener;
import net.thevpc.halfa.spi.renderer.HDocumentScreenRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRendererConfig;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NStringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServiceHelper {
    public static final FileFilter HD_FILTER = new FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().toLowerCase().endsWith(".hd");
        }

        @Override
        public String getDescription() {
            return "H Document";
        }
    };
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

        @Override
        public void onSaveDocument(HDocument document, HDocumentStreamRendererConfig config) {
            for (HDocumentRendererListener eventListener : registeredHDocumentRendererListener) {
                eventListener.onSaveDocument(document, config);
            }
        }
    };

    private HMessageList currentMessageList = new HMessageList() {
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
        registeredHDocumentRendererListener.add(new HDocumentRendererListener() {

            @Override
            public void onSaveDocument(HDocument document, HDocumentStreamRendererConfig config) {
                doSavePDf(document, config);
            }

        });
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
        f.setFileFilter(HD_FILTER);
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

    public void showNewFolder() {
        NSession session = mainFrame.getSession();

        String myTitle = "MyDocument";
        String mySubTitle = "Subtitle";
        String mySubTitle2 = "Subtitle2";
        String myEmail = "me@email.com";
        String myDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        NPath nPath = loadPath(session);
        JFileChooser f = new JFileChooser();
        f.setFileFilter(HD_FILTER);
        f.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (nPath != null) {
            f.setCurrentDirectory(nPath.toFile().get());
        }
        int r = f.showSaveDialog(mainFrame.getContentPane());
        if (r == JFileChooser.APPROVE_OPTION) {
            File sf = f.getSelectedFile();
            if (sf != null) {
                NPath path = NPath.of(sf, session);
                savePath(path, session);
                if (path.isRegularFile()) {
                    JOptionPane.showMessageDialog(mainFrame.getContentPane(), "File exists","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (path.isDirectory()
                        &&
                        path.list().stream().anyMatch(x -> isHalfaDocFile(x) || x.isDirectory())
                ) {
                    JOptionPane.showConfirmDialog(mainFrame.getContentPane(), "Folder exists","Error",JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String myName = System.getProperty("user.name");
                path.resolve("01-styles/001-styles.hd").mkParentDirs().writeString("import(\"github://thevpc/halfa-templates/2024/simple-01/**/*.hd\")\n");
                path.resolve("02-pages/0001-cover/0001-page-cover.hd").mkParentDirs().writeString("@cover page{\n" +
                        "    @(title)    text(\"" + myTitle + "\")\n" +
                        "    @(subtitle) text(\"" + mySubTitle + "\")\n" +
                        "    @(subtitle2) text(\"" + mySubTitle2 + "\")\n" +
                        "    @(author)   text(\"" + myName + "\")\n" +
                        "    @(author2)   text(\"" + myEmail + "\")\n" +
                        "    @(date)     text(\"" + myDate + "\")\n" +
                        "    @(version)  text(\"v1.0\")\n" +
                        "}\n");
                path.resolve("01-pages/9999-conclusion/9999-page-conclusion.hd").mkParentDirs().writeString("@conclusion page{\n" +
                        "    @(title)    text(\"Thanks\"),\n" +
                        "    @(author)   text(\"" + myEmail + "\"),\n" +
                        "}\n");
                path.resolve("main.hd").mkParentDirs().writeString("import(\"01-styles/**/*.hd\")\n" +
                        "import(\"02-pages/**/*.hd\")\n");
                HDocumentScreenRenderer renderer = engine.newScreenRenderer();
                renderer.setMessages(currentMessageList);
                renderer.addRendererListener(currListener);
                renderer.renderPath(path);
            }
        }
    }

    private static boolean isHalfaDocFile(NPath x) {
        return x.getName().endsWith(".hd");
    }

    public void doExit() {
        System.exit(0);
    }

    public void doSavePDf(HDocument document, HDocumentStreamRendererConfig config) {
        NSession session = mainFrame.getSession();
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int r = f.showOpenDialog(mainFrame.getContentPane());
        if (r == JFileChooser.APPROVE_OPTION) {
            File sf = f.getSelectedFile();
            if (sf != null) {
                if (!sf.exists()) {
                    if (!sf.getName().endsWith(".pdf")) {
                        sf = new File(sf.getParent(), sf.getName() + ".pdf");
                    }
                }
                NPath outputPdfPath = NPath.of(sf, session);

                HDocumentStreamRenderer renderer = engine.newPdfRenderer();
                renderer.setStreamRendererConfig(config);
                renderer.setOutput(outputPdfPath);
                renderer.render(document);


                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(sf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Desktop is not supported on this system.");
                }
            }
        }
    }

}



