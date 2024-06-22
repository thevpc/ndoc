package net.thevpc.halfa.spi.nodes;

import net.thevpc.halfa.api.node.HItem;
import net.thevpc.nuts.NCallableSupport;

/**
 * @author vpc
 */
public interface HNodeParserFactory {

    NCallableSupport<HItem> parseNode(HNodeFactoryParseContext context);

}
