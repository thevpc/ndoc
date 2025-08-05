package net.thevpc.ntexup.api.document.node;

import net.thevpc.ntexup.api.source.NTxSource;

public interface NTxItem {
    NTxItem parent();
    NTxSource source();
}
