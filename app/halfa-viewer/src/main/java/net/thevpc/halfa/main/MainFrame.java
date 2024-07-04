package net.thevpc.halfa.main;

import net.thevpc.halfa.engine.renderer.common.elem2d.ImageUtils;
import net.thevpc.nuts.NSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

        private NSession session;
    private ServiceHelper serviceHelper;

    public MainFrame(NSession session) {
        this.session=session;
        serviceHelper=new ServiceHelper(this);
        setTitle("HD Viewer Application");
        this.setIconImage(
                ImageUtils.resizeImage(
                        new ImageIcon(getClass().getResource("/net/thevpc/halfa/halfa.png")).getImage(),
                        16, 16)
        );
        setContentPane(createCenter());

//        setJMenuBar(jmb);
        setPreferredSize(new Dimension(600, 400));
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public NSession getSession() {
        return session;
    }

    private JMenuBar createMenu() {
        JMenuBar jmb = new JMenuBar();
        jmb.add(createMenuFile());
        jmb.add(createMenuView());
        jmb.add(createMenuHelp());
        jmb.add(createMenuSave());
        return jmb;



    }



    private JComponent createCenter() {
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(createMenu(),BorderLayout.NORTH);
        JButton openFile = new JButton("Open...");
        openFile.addActionListener(e -> serviceHelper.showOpenFile());
        jPanel.add(openFile);
        return jPanel;
    }

    private JMenu createMenuHelp() {
        JMenu menu = new JMenu("Help");
        menu.add(new JMenuItem("About"));
        return menu;
    }

    private JMenu createMenuView() {
        JMenu menu = new JMenu("View");
        menu.add(createMenuItemDebugPane());
        return menu;
    }

    private JMenuItem createMenuItemDebugPane() {
        JMenuItem r = new JMenuItem("Debug Pane");
        r.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serviceHelper.showDebug();
            }
        });
        return r;
    }

    private JMenu createMenuFile() {
        JMenu menu = new JMenu("File");
        menu.add(createMenuItemOpenFile());
        menu.addSeparator();
        menu.add(createMenuItemExit());
        return menu;
    }
    private JMenu createMenuSave() {
        JMenu menu = new JMenu("Save");


        return menu;
    }

    private JMenuItem createMenuItemExit() {
        JMenuItem menu = new JMenuItem("Exit");
        menu.addActionListener(e -> serviceHelper.doExit());
        return menu;
    }

    private JMenuItem createMenuItemOpenFile() {
        JMenuItem menu = new JMenuItem("Open....");
        menu.addActionListener(e -> serviceHelper.showOpenFile());
        return menu;
    }

}
