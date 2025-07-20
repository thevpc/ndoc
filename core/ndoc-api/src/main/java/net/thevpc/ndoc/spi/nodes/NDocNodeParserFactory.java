package net.thevpc.ndoc.spi.nodes;

import net.thevpc.ndoc.api.model.node.NDocItem;
import net.thevpc.nuts.NCallableSupport;

/**
 * @author vpc
 */
public interface NDocNodeParserFactory {

    NCallableSupport<NDocItem> parseNode(NDocNodeFactoryParseContext context);

}
