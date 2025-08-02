package net.thevpc.ndoc;

import net.thevpc.nuts.io.NPath;

import java.util.ArrayList;
import java.util.List;

public class Options {
    public List<NPath> paths = new ArrayList<>();
    public NPath toPdf;
    public Action action = Action.OPEN;
    public boolean swing;
    public boolean console;
    public boolean showLogs;
    public boolean reopen;

    public Options requireViewer() {
        this.swing = true;
        return this;
    }
}
