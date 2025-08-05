package net.thevpc.ntexup;

import net.thevpc.ntexup.cmdline.*;
import net.thevpc.nuts.*;
import net.thevpc.nuts.cmdline.NCmdLine;

/**
 * @author vpc
 */
@NAppDefinition
public class NTexupMain {

    public static void main(String[] args) {
        NApp.builder(args).run();
    }

    @NAppRunner
    public void run() {
        NWorkspace.of().share();
        Options options = new Options();
        NCmdLine cmdLine = NApp.of().getCmdLine();
        new NTxCmdLineParser().parse(options, cmdLine);
        if (options.viewer) {
            new NTxViewerProcessor().runViewer(options);
        } else {
            new NTxTerminalProcessor().runTerminal(options);
        }
    }


}
