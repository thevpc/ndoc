package net.thevpc.halfa.spi.nodes;

import net.thevpc.halfa.api.HalfaEngine;
import net.thevpc.nuts.NSession;
import net.thevpc.tson.TsonElement;

public interface HDocumentItemFactoryParseContext {

    NSession session();

    HalfaEngine engine();
    
    TsonElement element();

}
