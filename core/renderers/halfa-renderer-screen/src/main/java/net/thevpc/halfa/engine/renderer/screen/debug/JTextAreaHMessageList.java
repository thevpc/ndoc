package net.thevpc.halfa.engine.renderer.screen.debug;

import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageType;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.util.NMsg;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;

public class JTextAreaHMessageList extends JPanel implements HMessageList {
    private JTextArea view;

    public JTextAreaHMessageList() {
        super(new BorderLayout());
        view = new JTextArea();
        add(new JScrollPane(view));
    }

    @Override
    public void addMessage(HMessageType type, NMsg message, Throwable error, HResource source) {
        Instant time=Instant.now();
        NMsg mm=NMsg.ofC("[%s] [%s] [%s] %s", 
                time, 
                type,
                source == null ? null : source.shortName(),
                message
        );
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                view.append(mm.toString());
                view.append("\n");
            }
        });
    }

    public void updateContent() {

    }
}
