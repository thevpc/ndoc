package net.thevpc.ndoc;

import com.formdev.flatlaf.FlatLightLaf;
import net.thevpc.ndoc.main.MainFrame;
import net.thevpc.nuts.*;
import net.thevpc.nuts.cmdline.NArg;
import net.thevpc.nuts.cmdline.NCmdLine;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.nswing.NSwingUtils;

import javax.swing.*;

/**
 * @author vpc
 */
@NAppDefinition
public class NDocViewer  {

    public static void main(String[] args) {
        NApp.builder(args).run();
    }

    @NAppRunner
    public void run() {
        NWorkspace.of().share();
        NSwingUtils.setSharedWorkspaceInstance();
        FlatLightLaf.setup(new com.formdev.flatlaf.FlatDarculaLaf());
        MainFrame mainFrame=new MainFrame();
        SwingUtilities.invokeLater(() -> {
            mainFrame.setVisible(true);
            NCmdLine cmdLine = NApp.of().getCmdLine();
            while (!cmdLine.isEmpty()) {
                cmdLine.matcher()
                        .with("--reopen").matchTrueFlag(a->{
                            NPath p = mainFrame.getService().getLatestProjectPath();
                            if(p!=null){
                                mainFrame.getService().openProject(p);
                            }
                        })
                        .with("--open").matchEntry(a->
                                mainFrame.getService().openProject(NPath.of(a.stringValue())))
                        .with("--show-log").matchTrueFlag(a->
                                mainFrame.getService().showDebug())
                        .requireWithDefault();
            }
        });
    }
}
