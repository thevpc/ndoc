package net.thevpc.halfa.main;

import net.thevpc.halfa.config.HalfaViewerConfigManager;
import net.thevpc.halfa.engine.renderer.common.elem2d.ImageUtils;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainFrame extends JFrame {

    private NSession session;
    private ServiceHelper serviceHelper;
    private JList recentFilesComponent;
    private DefaultListModel recentFilesComponentModel;

    public MainFrame(NSession session) {
        this.session = session;
        serviceHelper = new ServiceHelper(this);
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
        return jmb;
    }

    private JComponent createCenter() {
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(createMenu(), BorderLayout.NORTH);
        recentFilesComponentModel = new DefaultListModel();
        recentFilesComponent = new JList(recentFilesComponentModel);
        recentFilesComponent.setCellRenderer(new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        recentFilesComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    Object selectedValue = recentFilesComponent.getSelectedValue();
                    if(selectedValue instanceof String){
                        serviceHelper.showOpenFile();
                    }else if(selectedValue instanceof HalfaViewerConfigManager.HalfaProject){
                        serviceHelper.openProject(
                                NPath.of(((HalfaViewerConfigManager.HalfaProject) selectedValue).getPath(),session)
                        );
                    }
                }
            }
        });
        JPanel center=new JPanel(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0;
        g.gridy=0;
        g.gridheight=2;
        g.weightx=1;
        g.insets=new Insets(10,10,10,10);

        center.add(createButtons(), g);

        g = new GridBagConstraints();
        g.gridx=1;
        g.gridy=0;
        g.fill=GridBagConstraints.VERTICAL;
        g.weighty=2;
        g.weightx=2;
        g.anchor=GridBagConstraints.NORTH;
        g.insets=new Insets(10,10,10,10);
        center.add(createRecentFiles(), g);

        updateRecentFiles();
        jPanel.add(center, BorderLayout.CENTER);
        return jPanel;
    }
    private JComponent createRecentFiles(){
        JPanel center=new JPanel(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0;
        g.gridy=0;
        g.anchor=GridBagConstraints.WEST;
        center.add(new JLabel("Recent Project..."), g);

        g = new GridBagConstraints();
        g.gridx=0;
        g.gridy=1;
        g.fill=GridBagConstraints.VERTICAL;
        g.weighty=2;
        g.weightx=2;
        g.anchor=GridBagConstraints.NORTH;
        center.add(new JScrollPane(recentFilesComponent), g);
        return center;
    }

    private JComponent createButtons(){
        JPanel p=new JPanel(new GridLayout(-1,1));

        JButton newProjectButton = new JButton("New Project...");
        newProjectButton.addActionListener(e->serviceHelper.showNewProject());
        p.add(newProjectButton);

        JButton newFileButton = new JButton("New Project...");
        newFileButton.addActionListener(e->serviceHelper.showNewFile());
        p.add(newFileButton);

        JButton openExisting = new JButton("Open existing project...");
        openExisting.addActionListener(e->serviceHelper.showOpenFile());
        p.add(openExisting);
        return p;
    }

    private void updateRecentFiles() {
        DefaultListModel recentFilesComponentModel = new DefaultListModel();
        recentFilesComponentModel.addElement("Open...");
        for (HalfaViewerConfigManager.HalfaProject p : serviceHelper.config().getRecentProjects()) {
            recentFilesComponentModel.addElement(p);
        }
        SwingUtilities.invokeLater(() -> {
            this.recentFilesComponentModel = recentFilesComponentModel;
            recentFilesComponent.setModel(recentFilesComponentModel);
        });
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
        menu.add(createMenuItemNewFolder());
        menu.addSeparator();
        menu.add(createMenuItemExit());
        return menu;
    }


    private JMenuItem createMenuItemExit() {
        JMenuItem menu = new JMenuItem("Exit");
        menu.addActionListener(e -> serviceHelper.doExit());
        return menu;
    }

    private JMenuItem createMenuItemOpenFile() {
        JMenuItem menu = new JMenuItem("Open....");
        menu.addActionListener(e -> {
            serviceHelper.showOpenFile();
            updateRecentFiles();
        });
        return menu;
    }

    private JMenuItem createMenuItemNewFolder() {
        JMenuItem menu = new JMenuItem("New Project....");
        menu.addActionListener(e -> {
            serviceHelper.showNewProject();
            updateRecentFiles();
        });
        return menu;
    }

}