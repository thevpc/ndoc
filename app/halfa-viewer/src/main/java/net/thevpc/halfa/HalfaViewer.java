package net.thevpc.halfa;

import com.formdev.flatlaf.FlatLightLaf;
import net.thevpc.halfa.main.MainFrame;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;

/**
 * @author vpc
 */
public class HalfaViewer {



    public static void main(String[] args) {
        FlatLightLaf.setup(new com.formdev.flatlaf.FlatDarculaLaf());
        NSession session = Nuts.openWorkspace();
        MainFrame mainFrame=new MainFrame(session);
        mainFrame.setVisible(true);

    }
}
