package net.thevpc.ndoc.cmdline;

import net.thevpc.nuts.NSession;
import net.thevpc.nuts.cmdline.NCmdLine;
import net.thevpc.nuts.io.NPath;

public class NDocCmdLineParser {
    public void parse(Options options,NCmdLine cmdLine){
        while (!cmdLine.isEmpty()) {
            cmdLine.matcher()
                    .with("--reopen").matchTrueFlag(a -> options.reopen = true)
                    .with("--list-templates").matchFlag(a -> options.action = net.thevpc.ndoc.cmdline.Action.LIST_TEMPLATES)
                    .with("--open").matchEntry(a -> options.paths.add(NPath.of(a.stringValue())))
                    .with("--new").matchTrueFlag(a -> options.action = Action.NEW)
                    .with("--view").matchTrueFlag(a -> options.requireViewer())
                    .with("--view-log").matchTrueFlag(a -> options.requireViewer().showLogs = true)
                    .withNonOption().matchAny(a -> options.paths.add(NPath.of(a.image())))
                    .requireWithDefault();
        }
        if (!options.viewer && !options.console) {
            options.viewer = NSession.of().isGui();
            options.console = !options.viewer;
        } else if (options.viewer && options.console) {
            options.viewer = NSession.of().isGui();
            options.console = !options.viewer;
        }
    }
}
