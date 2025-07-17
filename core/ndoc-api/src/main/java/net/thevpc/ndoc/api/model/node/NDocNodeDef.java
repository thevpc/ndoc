package net.thevpc.ndoc.api.model.node;

import net.thevpc.ndoc.api.resources.NDocResource;

public interface NDocNodeDef extends HItem {
    String name();

    NDocNodeDefParam[] params();

    NDocNode[] body();

    NDocResource source();
}
