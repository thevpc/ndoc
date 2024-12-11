package net.thevpc.halfa.main.components;

import net.thevpc.halfa.config.UserConfig;
import net.thevpc.halfa.main.HadraUIHelper;
import net.thevpc.halfa.main.ServiceHelper;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class NewProjectPanel extends JPanel {
    JTextField projectName;
    PathField rootFolder;
    ServiceHelper serviceHelper;
    NewProjectPropsPanel props;
    UserConfigPanel userConfPanel;

    public NewProjectPanel(ServiceHelper serviceHelper) {
        super(new GridBagLayout());
        this.serviceHelper = serviceHelper;

        add(new JLabel("Project Name"), HadraUIHelper.forLabel(0, 0));
        add(projectName = new JTextField(), HadraUIHelper.forEditor(1, 0));
        projectName.setText("new-project");

        add(new JLabel("Root Folder"), HadraUIHelper.forLabel(0, 1));
        add(rootFolder = new PathField(), HadraUIHelper.forEditor(1, 1));
        rootFolder.path.setText(System.getProperty("user.dir"));
        {
            props = new NewProjectPropsPanel(serviceHelper);
            GridBagConstraints g = new GridBagConstraints();
            g.gridx = 0;
            g.gridy = 3;
            g.weightx = 4;
            g.weighty = 2;
            g.gridwidth = 2;
            g.gridheight = 2;
            g.fill = GridBagConstraints.BOTH;
            g.anchor = GridBagConstraints.WEST;
            g.insets = new Insets(2, 2, 2, 2);
            props.setBorder(BorderFactory.createTitledBorder("Document Properties"));
            add(props, g);
        }
        {
            userConfPanel = new UserConfigPanel(serviceHelper.usersConfig().loadUserConfigs());
            GridBagConstraints g = new GridBagConstraints();
            g.gridx = 0;
            g.gridy = 5;
            g.weightx = 4;
            g.weighty = 2;
            g.gridwidth = 2;
            g.gridheight = 2;
            g.fill = GridBagConstraints.BOTH;
            g.anchor = GridBagConstraints.WEST;
            g.insets = new Insets(2, 2, 2, 2);
            userConfPanel.setBorder(BorderFactory.createTitledBorder("Author Properties"));
            add(userConfPanel, g);
        }
    }

    public UserConfig getUserConfig() {
        return userConfPanel.getUserConfig();
    }

    public Map<String, String> getPropValues() {
        Map<String, String> propValues = props.getPropValues();
        UserConfig userConfig = getUserConfig();
        propValues.put("template.username", userConfig.getUsername());
        propValues.put("template.fullName", userConfig.getFullName());
        propValues.put("template.author", userConfig.getFullName());
        propValues.put("template.firstName", userConfig.getFirstName());
        propValues.put("template.lastName", userConfig.getLastName());
        propValues.put("template.emailAddress", userConfig.getEmailAddress());
        propValues.put("template.affiliation", userConfig.getAffiliation());
        return propValues;
    }


    public String getSelectedProjectName() {
        return projectName.getText();
    }

    public String getSelectedRootFolder() {
        return rootFolder.path.getText();
    }

    public String getSelectedTemplate() {
        return props.getSelectedTemplate();
    }

    public boolean showDialog(Component parent) {
        int i = JOptionPane.showOptionDialog(parent, this, "New Project", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        return i == JOptionPane.OK_OPTION;
    }
}
