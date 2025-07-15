package net.thevpc.ndoc.api.model.node;

import net.thevpc.ndoc.api.resources.HResource;

public interface HNodeDef extends HItem {
    String name();

    HNodeDefParam[] params();

    HNode[] body();

    HResource source();
}
