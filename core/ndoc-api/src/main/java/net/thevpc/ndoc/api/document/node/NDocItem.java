package net.thevpc.ndoc.api.document.node;

import net.thevpc.ndoc.api.parser.NDocResource;

public interface NDocItem {
    NDocItem parent();
    NDocResource source();
}
