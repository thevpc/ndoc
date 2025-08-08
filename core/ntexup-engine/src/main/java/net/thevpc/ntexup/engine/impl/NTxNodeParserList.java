package net.thevpc.ntexup.engine.impl;

import net.thevpc.ntexup.api.parser.NTxNodeParser;

import java.util.*;

public class NTxNodeParserList extends NtxServiceListImpl2<NTxNodeParser> {
    public NTxNodeParserList(DefaultNTxEngine engine) {
        super("node parser", NTxNodeParser.class, engine);
    }

    @Override
    protected List<String> aliasesOf(NTxNodeParser nTxNodeParser) {
        return Arrays.asList(nTxNodeParser.aliases());
    }

    @Override
    protected String idOf(NTxNodeParser nTxNodeParser) {
        return nTxNodeParser.id();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    protected void onAfterNewService(NTxNodeParser renderer, boolean custom) {
        renderer.init(engine);
    }
}
