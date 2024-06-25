package net.thevpc.halfa.debug;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.tson.TsonElement;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public abstract class TsonPanel extends JPanel {
    private Supplier<HDocument> model;
    private JTextArea view;

    public TsonPanel() {
        super(new BorderLayout());
        view = new JTextArea();
        add(new JScrollPane(view));
    }

    public abstract TsonElement createTson();

    public void updateContent() {
        String txt = "";
        try {
            TsonElement e = createTson();
            if (e != null) {
                txt = e.toString();
            }
        } catch (Exception ex) {
            txt = ex.toString();
        }
        String finalTxt = txt;
        if (SwingUtilities.isEventDispatchThread()) {
            view.setText(finalTxt);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    view.setText(finalTxt);
                }
            });
        }
    }
}
