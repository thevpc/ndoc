package net.thevpc.ndoc.engine.log;

import net.thevpc.ndoc.api.log.NDocMsg;
import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.nuts.util.NMsg;

import java.util.logging.Level;

public class SilentNDocLogger implements NDocLogger {
    private int errorCount;

    public SilentNDocLogger() {

    }
    @Override
    public void log(NMsg message) {
        log(NDocMsg.of(message));
    }

    @Override
    public void log(NMsg message, NDocResource source) {
        log(NDocMsg.of(message, source));
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

    public boolean isSuccessful() {
        return errorCount == 0;
    }

    public int getErrorCount() {
        return errorCount;
    }
}
