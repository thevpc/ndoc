package net.thevpc.halfa.main;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.config.HalfaViewerConfigManager;
import net.thevpc.halfa.debug.HDebugFrame;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentRendererListener;
import net.thevpc.halfa.spi.renderer.HDocumentScreenRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRendererConfig;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;
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
import java.util.function.Function;

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
    HalfaViewerConfigManager configManager;
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
        configManager = new HalfaViewerConfigManager(mainFrame.getSession());
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

    public HalfaViewerConfigManager config() {
        return configManager;
    }

    public void openProject(NPath path) {
        configManager.markAccessed(path);
        HDocumentScreenRenderer renderer = engine.newScreenRenderer();
        renderer.setMessages(currentMessageList);
        renderer.addRendererListener(currListener);
        renderer.renderPath(path);
    }

    public void showOpenFile() {
        NSession session = mainFrame.getSession();
        NPath nPath = configManager.getLastAccessedPath();
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
                openProject(NPath.of(sf, session));
            }
        }
    }

    public void showNewProject() {
        NewProjectPanel projectPanel = new NewProjectPanel();
        int i = JOptionPane.showOptionDialog(mainFrame.getContentPane(), projectPanel, "New Project", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        NSession session = mainFrame.getSession();
        if (i == JOptionPane.OK_OPTION) {
            String selectedTemplate = projectPanel.getSelectedTemplate();
            String templateBootUrl = NStringUtils.firstNonBlank(selectedTemplate, "/home/vpc/xprojects/productivity/halfa-templates/main/simple/v1.0/boot/default");
            NPath rootPath = NPath.of(NStringUtils.firstNonBlank(projectPanel.getSelectedRootFolder(), "."), session);
            if (NBlankable.isBlank(projectPanel.getSelectedProjectName())) {
                String aa = "new-project";
                int index = 1;
                NPath newProjectPath=null;
                while (true) {
                    String nn = index == 1 ? aa : (aa + "-" + index);
                    newProjectPath = rootPath.resolve(nn);
                    if (!newProjectPath.exists()) {
                        break;
                    }
                    index++;
                }
                //engine.createProject(path,"github://thevpc/halfa-templates/main/simple/v1.0/boot/default");
                Function<String, String> vars = m -> {
                    switch (m) {
                        case "title":
                            return "MyDocument";
                        case "subtitle":
                            return "My Subtitle";
                        case "subsubtitle":
                            return "My Sub Subtitle";
                        case "email":
                            return "me@email.com";
                        case "author":
                            return System.getProperty("user.name");
                        case "date":
                            return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        case "templateBootUrl":
                            return templateBootUrl;
                        case "templateUrl": {
                            try {
                                NPath bp = NPath.of(templateBootUrl, session);
                                NPath pp = bp.getParent();
                                if (pp != null && pp.getName().equals("boot")) {
                                    pp = pp.getParent();
                                    if (pp != null) {
                                        pp = pp.resolve("dist");
                                        return pp.toString();
                                    }
                                }
                            } catch (Exception ex) {
                                throw new IllegalArgumentException("Failed to resolve template boot url from " + templateBootUrl);
                            }
                            return templateBootUrl;
                        }
                    }
                    return null;
                };
                engine.createProject(newProjectPath, NPath.of(templateBootUrl, session), vars);
                openProject(newProjectPath);
            }
        }
    }

    public void showNewFile() {
        NSession session = mainFrame.getSession();

        NPath nPath = configManager.getLastAccessedPath();
        JFileChooser f = new JFileChooser();
        f.setFileFilter(HD_FILTER);
        f.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (nPath != null) {
            f.setCurrentDirectory(nPath.toFile().get());
        }
        int r = f.showSaveDialog(mainFrame.getContentPane());
        if (r == JFileChooser.APPROVE_OPTION) {
            File sf = f.getSelectedFile();
            if (sf != null) {
                if (sf.isFile()) {
                    return;
                } else {
                    if (!sf.getName().endsWith(".hd")) {
                        sf = new File(sf.getParent(), sf.getName() + ".hd");
                    }
                    NPath.of(sf, session).writeString("//Hadra Document File\n");
                }
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

    private static class PathField extends JPanel {
        JTextField path;
        JButton button;

        public PathField() {
            super(new GridBagLayout());
            GridBagConstraints g = new GridBagConstraints();
            g.gridx = 0;
            g.gridy = 0;
            g.weightx = 2;
            g.fill = GridBagConstraints.BOTH;
            g.anchor = GridBagConstraints.WEST;
            add(path = new JTextField(), g);
            g = new GridBagConstraints();
            g.gridx = 1;
            g.gridy = 0;
            g.anchor = GridBagConstraints.WEST;
            add(button = new JButton("..."), g);
            button.addActionListener(e -> onSelectPath());
        }

        private void onSelectPath() {
            String oldPath = path.getText();
            JFileChooser f = new JFileChooser();
            f.setFileFilter(HD_FILTER);
            f.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            if (!NBlankable.isBlank(oldPath)) {
                f.setCurrentDirectory(new File(oldPath));
            }
            int r = f.showSaveDialog(this);
            if (r == JFileChooser.APPROVE_OPTION) {
                File sf = f.getSelectedFile();
                path.setText(sf.toString());
            }
        }
    }

    private static class NewProjectPanel extends JPanel {
        JTextField projectName;
        PathField rootFolder;
        JComboBox template;

        public NewProjectPanel() {
            super(new GridBagLayout());

            add(new JLabel("Project Name"), forLabel(0, 0));
            add(projectName = new JTextField(), forEditor(1, 0));

            add(new JLabel("Root Folder"), forLabel(0, 1));
            add(rootFolder = new PathField(), forEditor(1, 1));

            add(new JLabel("Template"), forLabel(0, 2));
            add(template = new JComboBox<>(), forEditor(1, 2));
            template.setEditable(true);
            DefaultComboBoxModel aModel = new DefaultComboBoxModel();
            aModel.addElement("/home/vpc/xprojects/productivity/halfa-templates/main/simple/v1.0/boot/default");
            aModel.addElement("/home/vpc/xprojects/productivity/halfa-templates/main/simple/v1.0/boot/minimum");
            template.setModel(aModel);
        }

        public String getSelectedProjectName() {
            return projectName.getText();
        }

        public String getSelectedRootFolder() {
            return rootFolder.path.getText();
        }

        public String getSelectedTemplate() {
            return (String) template.getSelectedItem();
        }

    }

    private static GridBagConstraints forEditor(int x, int y) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = y;
        g.weightx = 2;
        g.fill = GridBagConstraints.BOTH;
        g.anchor = GridBagConstraints.WEST;
        return g;
    }

    private static GridBagConstraints forLabel(int x, int y) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = y;
        g.anchor = GridBagConstraints.WEST;
        return g;
    }
}



