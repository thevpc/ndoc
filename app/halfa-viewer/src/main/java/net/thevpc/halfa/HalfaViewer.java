package net.thevpc.halfa;

import com.formdev.flatlaf.FlatLightLaf;
import net.thevpc.halfa.main.MainFrame;
import net.thevpc.nuts.*;

import javax.swing.*;

/**
 * @author vpc
 */
public class HalfaViewer implements NApplication {

    public static void main(String[] args) {
        new HalfaViewer().run(args);
    }

    @Override
    public void run() {
        NWorkspace ws = NWorkspace.of();
        ws.setSharedInstance();
        SwingUtilities.invokeLater(ws::setSharedInstance);
        FlatLightLaf.setup(new com.formdev.flatlaf.FlatDarculaLaf());
        MainFrame mainFrame=new MainFrame();
        mainFrame.setVisible(true);

    }
}
