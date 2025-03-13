package net.thevpc.halfa.debug;

import net.thevpc.halfa.api.document.HLogger;
import net.thevpc.halfa.api.document.HMsg;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.nuts.util.NMsg;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.logging.Level;

public class JTextAreaHMessageList extends JPanel implements HLogger {
    private JTextArea view;

    public JTextAreaHMessageList() {
        super(new BorderLayout());
        view = new JTextArea();
        add(new JScrollPane(view));
    }

    @Override
    public void log(HMsg msg) {
        Instant time = Instant.now();
        NMsg nmsg=msg.message();
        Level type=nmsg.getLevel();
        Throwable error=msg.error();
        HResource source=msg.source();
        if (type == null) {
            type = Level.INFO;
        }

        NMsg mm = NMsg.ofC("[%s] [%s] [%s] %s",
                time,
                type,
                source == null ? null : source.shortName(),
                nmsg
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
