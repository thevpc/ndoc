package net.thevpc.ndoc.api.model.node;

import net.thevpc.ndoc.api.resources.NDocResource;

public interface NDocItem {
    NDocItem parent();
    NDocResource source();
}
