package net.thevpc.ndoc.api.document.node;

import net.thevpc.ndoc.api.source.NDocResource;

public interface NDocItem {
    NDocItem parent();
    NDocResource source();
}
