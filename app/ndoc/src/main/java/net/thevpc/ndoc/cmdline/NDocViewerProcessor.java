package net.thevpc.ndoc.cmdline;

import com.formdev.flatlaf.FlatLightLaf;
import net.thevpc.ndoc.main.MainFrame;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.nswing.NSwingUtils;

import javax.swing.*;

public class NDocViewerProcessor {
    public void runViewer(Options options) {
        NSwingUtils.setSharedWorkspaceInstance();
        FlatLightLaf.setup(new com.formdev.flatlaf.FlatDarculaLaf());
        MainFrame mainFrame = new MainFrame();
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(true);
            if (options.reopen) {
                NPath p = mainFrame.getService().getLatestProjectPath();
                if (p != null) {
                    options.paths.add(p);
                }
            }
            if (options.showLogs) {
                mainFrame.getService().showDebug();
            }
            if (options.paths.isEmpty()) {

            } else {
                for (NPath path : options.paths) {
                    mainFrame.getService().openProject(path);
                }
            }
        });
    }
}
