package net.thevpc.ntexup.cmdline;

import com.formdev.flatlaf.FlatLightLaf;
import net.thevpc.ntexup.main.MainFrame;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.nswing.NSwingUtils;

import javax.swing.*;

public class NTxViewerProcessor {
    public void runViewer(Options options) {
        NSwingUtils.setSharedWorkspaceInstance();
        FlatLightLaf.setup(new com.formdev.flatlaf.FlatDarculaLaf());
        MainFrame mainFrame = new MainFrame();
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(true);
            if (options.action==Action.DUMP) {

            }else if (options.action==Action.DOC) {
                options.paths.add(NPath.of("github://thevpc/ntexup-doc-slides/"));
            }else if (options.reopen) {
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
