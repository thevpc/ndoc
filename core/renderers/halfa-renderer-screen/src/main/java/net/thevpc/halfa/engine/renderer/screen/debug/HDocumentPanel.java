package net.thevpc.halfa.engine.renderer.screen.debug;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageType;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.util.NMsg;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.function.Supplier;

public class HDocumentPanel extends JPanel {
    private Supplier<HDocument> model;
    private JTextArea view;
    private HEngine engine;

    public HDocumentPanel(HEngine engine,Supplier<HDocument> model) {
        super(new BorderLayout());
        view = new JTextArea();
        add(new JScrollPane(view));
        this.model = model;
        this.engine = engine;
    }

    public void updateContent() {
        HDocument m=null;
        String txt="";
        try {
            m = model.get();
        }catch (Exception ex){
            txt=ex.toString();
        }
        if(m!=null){
            try {
                txt=engine.toTson(m).toString();
            }catch (Exception ex){
                txt=ex.toString();
            }
        }
        if(txt==null){
            txt="?";
        }
        String finalTxt = txt;
        if(SwingUtilities.isEventDispatchThread()){
            view.setText(finalTxt);
        }else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    view.setText(finalTxt);
                }
            });
        }
    }
}
