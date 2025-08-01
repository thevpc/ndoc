package net.thevpc.ndoc.main;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.engine.NDocTemplateInfo;
import net.thevpc.ndoc.config.NDocProject;
import net.thevpc.ndoc.config.NDocViewerConfigManager;
import net.thevpc.ndoc.config.UserConfig;
import net.thevpc.ndoc.config.UserConfigManager;
import net.thevpc.ndoc.debug.HDebugFrame;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.ndoc.main.components.NewProjectPanel;
import net.thevpc.ndoc.api.renderer.NDocDocumentRendererListener;
import net.thevpc.ndoc.api.renderer.NDocDocumentScreenRenderer;
import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRenderer;
import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRendererConfig;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NStringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ServiceHelper {

    public static final FileFilter NDOC_FILTER = new FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }
            return f.getName().toLowerCase().endsWith(".ndoc");
        }

        @Override
        public String getDescription() {
            return "NDoc File";
        }
    };
    MainFrame mainFrame;
    NDocEngine engine;
    UserConfigManager usersConfigManager;
    NDocViewerConfigManager configManager;
    NDocDocumentRendererListenerList currListeners = new NDocDocumentRendererListenerList();
    HDebugFrame debugFrame;

    public ServiceHelper(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.engine = new DefaultNDocEngine();
        this.debugFrame = new HDebugFrame(engine);
        this.engine.addLog(debugFrame.messages());
        this.currListeners.add(new NDocDocumentRendererListener() {

            @Override
            public void onSaveDocument(NDocument document, NDocDocumentStreamRendererConfig config) {
                doSavePDf(document, config);
            }

        });
        this.configManager = new NDocViewerConfigManager();
        this.usersConfigManager = new UserConfigManager();
        this.currListeners.add(debugFrame.rendererListener());
        this.debugFrame.setOnClose(new Runnable() {
            @Override
            public void run() {
                //currentMessages.remove(debugFrame.messages());
                //currListeners.remove(debugFrame.rendererListener());
            }
        });
    }

    public NDocEngine engine() {
        return engine;
    }

    public void showDebug() {
        debugFrame.run();
    }

    public NPath getLatestProjectPath() {
        NDocProject[] p = config().getRecentProjects();
        if (p != null && p.length > 0) {
            return NPath.of(p[0].getPath());
        }
        return null;
    }

    public NDocViewerConfigManager config() {
        return configManager;
    }

    public UserConfigManager usersConfig() {
        return usersConfigManager;
    }

    public void openProject(NPath path) {
        configManager.markAccessed(path);
        NDocDocumentScreenRenderer renderer = engine.newScreenRenderer().get();
        renderer.addRendererListener(currListeners);
        renderer.renderPath(path);
    }

    public void showOpenFile() {
        NPath nPath = configManager.getLastAccessedPath();
        JFileChooser f = new JFileChooser();
        f.setFileFilter(NDOC_FILTER);
        f.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (nPath != null) {
            f.setCurrentDirectory(nPath.toFile().get());
        }
        int r = f.showOpenDialog(mainFrame.getContentPane());
        if (r == JFileChooser.APPROVE_OPTION) {
            File sf = f.getSelectedFile();
            if (sf != null) {
                openProject(NPath.of(sf));
            }
        }
    }

    public void showNewProject() {
        NewProjectPanel projectPanel = new NewProjectPanel(this);
        if (projectPanel.showDialog(mainFrame.getContentPane())) {
            NDocTemplateInfo selectedTemplate = projectPanel.getSelectedTemplate();
            if (selectedTemplate == null) {
                selectedTemplate = Arrays.stream(engine.getTemplates()).filter(NDocTemplateInfo::recommended).findAny().orElse(null);
            }
            if (selectedTemplate == null) {
                JOptionPane.showMessageDialog(mainFrame.getContentPane(), "Missing template", "Warning", JOptionPane.ERROR_MESSAGE);
                return;
            }
            NPath rootPath = NPath.of(NStringUtils.firstNonBlank(projectPanel.getSelectedRootFolder(), "."));
            NPath newProjectPath;
            String baseName = NStringUtils.trim(projectPanel.getSelectedProjectName());
            if (NBlankable.isBlank(baseName)) {
                baseName = "new-project";
            }
            int index = 1;
            newProjectPath = null;
            while (true) {
                String nn = index == 1 ? baseName : (baseName + "-" + index);
                newProjectPath = rootPath.resolve(nn);
                if (!newProjectPath.exists()) {
                    break;
                }
                index++;
            }
            if (index != 1) {
                if (JOptionPane.showConfirmDialog(mainFrame.getContentPane(), "Folder " + baseName + " already exists. Should we consider new name " + newProjectPath.getName(), "Warning", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    //usersConfig().saveUserConfig(newUserConfig);
                } else {
                    return;
                }
            }
            Map<String, String> propValues = projectPanel.getPropValues();
            engine.createProject(newProjectPath, NPath.of(selectedTemplate.url()), propValues::get);
            UserConfig newUserConfig = projectPanel.getUserConfig();
            UserConfig oldUser = usersConfig().loadUserConfig(newUserConfig.getId());
            if (oldUser != null && Objects.equals(newUserConfig, oldUser)) {
                if (JOptionPane.showConfirmDialog(mainFrame.getContentPane(), "User Info changed. Do you want to save them?", "Warning", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    usersConfig().saveUserConfig(newUserConfig);
                }
            } else if (oldUser == null) {
                usersConfig().saveUserConfig(newUserConfig);
            }
            openProject(newProjectPath);
        }
    }

    public void showNewFile() {
        NPath nPath = configManager.getLastAccessedPath();
        JFileChooser f = new JFileChooser();
        f.setFileFilter(NDOC_FILTER);
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
                    if (!sf.getName().endsWith(".ndoc")) {
                        sf = new File(sf.getParent(), sf.getName() + ".ndoc");
                    }
                    NPath.of(sf).writeString("//NDoc File\n");
                }
            }
        }
    }

    public NDocEngine getEngine() {
        return engine;
    }

    private static boolean isNDocFile(NPath x) {
        return x.getName().endsWith(".ndoc");
    }

    public void doExit() {
        System.exit(0);
    }

    public void doSavePDf(NDocument document, NDocDocumentStreamRendererConfig config) {
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
                NPath outputPdfPath = NPath.of(sf);

                NDocDocumentStreamRenderer renderer = engine.newPdfRenderer().get();
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
