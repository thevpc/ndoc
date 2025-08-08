package net.thevpc.ntexup.engine.impl;

import net.thevpc.ntexup.api.parser.NTxNodeParserFactory;

public class NTxNodeParserFactoryList extends  NtxServiceListImpl2<NTxNodeParserFactory> {
    public NTxNodeParserFactoryList(DefaultNTxEngine engine) {
        super("parser factory", NTxNodeParserFactory.class, engine);
    }


}
