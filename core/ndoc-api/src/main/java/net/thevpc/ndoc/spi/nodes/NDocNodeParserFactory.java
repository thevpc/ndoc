package net.thevpc.ndoc.spi.nodes;

import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.nuts.NCallableSupport;

/**
 * @author vpc
 */
public interface NDocNodeParserFactory {

    NCallableSupport<HItem> parseNode(NDocNodeFactoryParseContext context);

}
