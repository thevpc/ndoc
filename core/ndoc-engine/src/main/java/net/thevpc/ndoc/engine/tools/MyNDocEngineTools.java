package net.thevpc.ndoc.engine.tools;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.engine.NDocEngineTools;

class MyNDocEngineTools implements NDocEngineTools {
    NDocEngine engine;

    public MyNDocEngineTools(NDocEngine engine) {
        this.engine = engine;
    }

}
