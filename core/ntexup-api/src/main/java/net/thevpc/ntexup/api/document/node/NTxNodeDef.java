package net.thevpc.ntexup.api.document.node;

import net.thevpc.ntexup.api.source.NTxSource;

public interface NTxNodeDef extends NTxItem {
    String name();

    NTxNodeDefParam[] params();

    NTxNode[] body();

    NTxSource source();
}
