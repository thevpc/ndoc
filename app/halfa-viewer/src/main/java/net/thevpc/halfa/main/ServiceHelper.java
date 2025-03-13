package net.thevpc.halfa.main;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.config.HalfaViewerConfigManager;
import net.thevpc.halfa.config.UserConfig;
import net.thevpc.halfa.config.UserConfigManager;
import net.thevpc.halfa.debug.HDebugFrame;
import net.thevpc.halfa.engine.DefaultHEngine;
import net.thevpc.halfa.main.components.NewProjectPanel;
import net.thevpc.halfa.spi.renderer.HDocumentRendererListener;
import net.thevpc.halfa.spi.renderer.HDocumentScreenRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRendererConfig;
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
    UserConfigManager usersConfigManager;
    HalfaViewerConfigManager configManager;
    HDocumentRendererListenerList currListeners = new HDocumentRendererListenerList();

    private HMessageListList currentMessages = new HMessageListList();

    public ServiceHelper(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        engine = new DefaultHEngine();
        currListeners.add(new HDocumentRendererListener() {

            @Override
            public void onSaveDocument(HDocument document, HDocumentStreamRendererConfig config) {
                doSavePDf(document, config);
            }

        });
        configManager = new HalfaViewerConfigManager();
        usersConfigManager = new UserConfigManager();
    }

    public HEngine engine() {
        return engine;
    }

    public void showDebug() {
        HDebugFrame debugFrame = new HDebugFrame(engine);
        currentMessages.add(debugFrame.messages());
        currListeners.add(debugFrame.rendererListener());
        debugFrame.setOnClose(new Runnable() {
            @Override
            public void run() {
                currentMessages.remove(debugFrame.messages());
                currListeners.remove(debugFrame.rendererListener());
            }
        });
        debugFrame.run();
    }

    public HalfaViewerConfigManager config() {
        return configManager;
    }

    public UserConfigManager usersConfig() {
        return usersConfigManager;
    }

    public void openProject(NPath path) {
        configManager.markAccessed(path);
        HDocumentScreenRenderer renderer = engine.newScreenRenderer().get();
        renderer.setLogger(currentMessages);
        renderer.addRendererListener(currListeners);
        renderer.renderPath(path);
    }

    public void showOpenFile() {
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
                openProject(NPath.of(sf));
            }
        }
    }

    public void showNewProject() {
        NewProjectPanel projectPanel = new NewProjectPanel(this);
        if (projectPanel.showDialog(mainFrame.getContentPane())) {
            String selectedTemplate = projectPanel.getSelectedTemplate();
            String templateBootUrl = NStringUtils.firstNonBlank(selectedTemplate, engine.getDefaultTemplateUrl());
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
            if(index!=1){
                if (JOptionPane.showConfirmDialog(mainFrame.getContentPane(), "Folder "+baseName+" already exists. Should we consider new name "+newProjectPath.getName(), "Warning", JOptionPane.WARNING_MESSAGE, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    //usersConfig().saveUserConfig(newUserConfig);
                }else{
                    return;
                }
            }
            Map<String, String> propValues = projectPanel.getPropValues();
            engine.createProject(newProjectPath, NPath.of(templateBootUrl), propValues::get);
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
                    NPath.of(sf).writeString("//Hadra Document File\n");
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

                HDocumentStreamRenderer renderer = engine.newPdfRenderer().get();
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
