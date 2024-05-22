package net.thevpc.halfa.spi.nodes;

import net.thevpc.halfa.api.node.HNode;
import net.thevpc.nuts.NCallableSupport;

/**
 *
 * @author vpc
 */
public interface HNodeParserFactory {

    NCallableSupport<HNode> parseNode(HNodeFactoryParseContext context);

}
