package net.thevpc.ndoc;

import net.thevpc.ndoc.cmdline.*;
import net.thevpc.nuts.*;
import net.thevpc.nuts.cmdline.NCmdLine;

/**
 * @author vpc
 */
@NAppDefinition
public class NDocViewer {

    public static void main(String[] args) {
        NApp.builder(args).run();
    }

    @NAppRunner
    public void run() {
        NWorkspace.of().share();
        Options options = new Options();
        NCmdLine cmdLine = NApp.of().getCmdLine();
        new NDocCmdLineParser().parse(options, cmdLine);
        if (options.viewer) {
            new NDocViewerProcessor().runViewer(options);
        } else {
            new NDocTerminalProcessor().runTerminal(options);
        }
    }


}
