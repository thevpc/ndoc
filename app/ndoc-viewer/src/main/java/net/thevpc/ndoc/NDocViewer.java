package net.thevpc.ndoc;

import com.formdev.flatlaf.FlatLightLaf;
import net.thevpc.ndoc.main.MainFrame;
import net.thevpc.nuts.*;
import net.thevpc.nuts.nswing.NSwingUtils;

/**
 * @author vpc
 */
public class NDocViewer  {

    public static void main(String[] args) {
        NApp.builder(args).run();
    }

    @NApp.Main
    public void run() {
        NWorkspace.of().share();
        NSwingUtils.setSharedWorkspaceInstance();
        FlatLightLaf.setup(new com.formdev.flatlaf.FlatDarculaLaf());
        MainFrame mainFrame=new MainFrame();
        mainFrame.setVisible(true);

    }
}
