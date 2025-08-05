package net.thevpc.ntexup.debug;

import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.nuts.elem.NElement;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public abstract class NTxTsonPanel extends JPanel {
    private Supplier<NTxDocument> model;
    private JTextArea view;

    public NTxTsonPanel() {
        super(new BorderLayout());
        view = new JTextArea();
        add(new JScrollPane(view));
    }

    public abstract NElement createTson();

    public void updateContent() {
        String txt = "";
        try {
            NElement e = createTson();
            if (e != null) {
                txt = e.toString();
            }
        } catch (Exception ex) {
            txt = "ERROR EVALUATION TSON : "+ex.toString();
            ex.printStackTrace();
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
