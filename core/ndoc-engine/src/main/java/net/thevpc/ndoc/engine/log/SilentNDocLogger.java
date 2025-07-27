package net.thevpc.ndoc.api.engine;

import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.NOut;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NStringUtils;

import java.time.Instant;
import java.util.logging.Level;

public class SilentNDocLogger implements NDocLogger {
    private int errorCount;

    public SilentNDocLogger() {

    }

    @Override
    public void log(NDocMsg message) {
        NMsg msg = message.message();
        Level level = msg.getLevel();
        if (level == null) {
            level = Level.INFO;
        }
        if (level.intValue() >= Level.SEVERE.intValue()) {
            errorCount++;
        }
    }

    public int getErrorCount() {
        return errorCount;
    }
}
