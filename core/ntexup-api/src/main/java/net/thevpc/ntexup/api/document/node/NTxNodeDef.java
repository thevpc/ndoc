package net.thevpc.ntexup.api.document.node;

import net.thevpc.ntexup.api.source.NDocResource;

public interface NTxNodeDef extends NTxItem {
    String name();

    NTxNodeDefParam[] params();

    NTxNode[] body();

    NDocResource source();
}
