package net.thevpc.ndoc.main.components;

import net.thevpc.ndoc.api.engine.NDocTemplateInfo;
import net.thevpc.ndoc.engine.templates.NDocTemplateInfoImpl;
import net.thevpc.ndoc.main.NDocUIHelper;
import net.thevpc.ndoc.main.ServiceHelper;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class NewProjectPropsPanel extends JPanel {
    Map<String, PropInfo> props = new LinkedHashMap<>();
    JComboBox template;

    public NewProjectPropsPanel(ServiceHelper serviceHelper) {
        super(new GridBagLayout());
        PropInfo[] propInfos={
                new NewProjectPropsPanel.PropInfo("template.title", "Title", "New Document")
                , new NewProjectPropsPanel.PropInfo("template.subtitle", "Sub-Title")
                , new NewProjectPropsPanel.PropInfo("template.subsubtitle", "Sub-Sub-Title")
                , new NewProjectPropsPanel.PropInfo("template.date", "Date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                , new NewProjectPropsPanel.PropInfo("template.version", "Version", "v1.0.0")
        };
        int row = 0;
        add(new JLabel("Template"), NDocUIHelper.forLabel(0, row));
        add(template = new JComboBox<>(), NDocUIHelper.forEditor(1, row));
        template.setEditable(true);
        row++;
        DefaultComboBoxModel aModel = new DefaultComboBoxModel();
        for (NDocTemplateInfo defaultTemplateUrl : serviceHelper.engine().getTemplates()) {
            aModel.addElement(defaultTemplateUrl);
        }
        template.setModel(aModel);

        for (PropInfo s : propInfos) {
            props.put(s.id, s);
            s.field = new JTextField();
            if(s.defaultValue!=null){
                s.field.setText(s.defaultValue);
            }
            add(new JLabel(s.label), NDocUIHelper.forLabel(0, row));
            add(s.field, NDocUIHelper.forEditor(1, row));
            row++;
        }
    }
    public NDocTemplateInfo getSelectedTemplate() {
        Object s = template.getSelectedItem();
        if(s instanceof String){
            String ss=(String)s;
            return new NDocTemplateInfoImpl(
                    ss,
                    ss, false, null, null,
                    ss
            );
        }
        return (NDocTemplateInfo) s;
    }

    public String getSelectedProperty(String property) {
        PropInfo u = props.get(property);
        if (u != null) {
            return u.field.getText();
        }
        return null;
    }

    public static class PropInfo {
        private String id;
        private String label;
        private String defaultValue;
        private JTextField field;

        public PropInfo(String id, String label) {
            this(id, label, null);
        }

        public PropInfo(String id, String label, String defaultValue) {
            this.id = id;
            this.label = label;
            this.defaultValue = defaultValue;
        }
    }

    public Map<String, String> getPropValues() {
        Map<String, String> m = new LinkedHashMap<>();
        for (Map.Entry<String, PropInfo> e : props.entrySet()) {
            m.put(e.getKey(), e.getValue().field.getText());
        }
        return m;
    }
}
