package net.thevpc.ndoc.debug;

import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.util.NMsg;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.util.logging.Level;

public class JTextAreaNDocMessageList extends JPanel implements NDocLogger {
    private JTextArea view;

    public JTextAreaNDocMessageList() {
        super(new BorderLayout());
        view = new JTextArea();
        add(new JScrollPane(view));
    }

    @Override
    public void log(NDocMsg msg) {
        Instant time = Instant.now();
        NMsg nmsg=msg.message();
        Level type=nmsg.getLevel();
        Throwable error=msg.error();
        NDocResource source=msg.source();
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
