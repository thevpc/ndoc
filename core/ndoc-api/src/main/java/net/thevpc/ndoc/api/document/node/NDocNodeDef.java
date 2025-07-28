package net.thevpc.ndoc.api.document.node;

import net.thevpc.ndoc.api.parser.NDocResource;

public interface NDocNodeDef extends NDocItem {
    String name();

    NDocNodeDefParam[] params();

    NDocNode[] body();

    NDocResource source();
}
