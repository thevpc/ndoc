package net.thevpc.ndoc;

import com.formdev.flatlaf.FlatLightLaf;
import net.thevpc.ndoc.main.MainFrame;
import net.thevpc.nuts.*;
import net.thevpc.nuts.lib.nswing.NSwingUtils;

/**
 * @author vpc
 */
public class NDocViewer implements NApplication {

    public static void main(String[] args) {
        new NDocViewer().main(NMainArgs.of(args));
    }

    @Override
    public void run() {
        NWorkspace.of().share();
        NSwingUtils.setSharedWorkspaceInstance();
        FlatLightLaf.setup(new com.formdev.flatlaf.FlatDarculaLaf());
        MainFrame mainFrame=new MainFrame();
        mainFrame.setVisible(true);

    }
}
