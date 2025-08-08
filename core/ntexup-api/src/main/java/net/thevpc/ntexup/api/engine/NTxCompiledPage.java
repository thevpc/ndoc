package net.thevpc.ntexup.api.engine;

import net.thevpc.ntexup.api.document.node.NTxNode;

public interface NTxCompiledPage {
    int index();

    NTxCompiledDocument document();

    NTxNode compiledPage();

    Object source();

    boolean isCompiled();

    NTxNode rawPage();
}
