package net.thevpc.ntexup.api.parser;

import net.thevpc.ntexup.api.document.node.NTxItem;
import net.thevpc.nuts.NCallableSupport;

/**
 * @author vpc
 */
public interface NTxNodeParserFactory {

    NCallableSupport<NTxItem> parseNode(NTxNodeFactoryParseContext context);

}
