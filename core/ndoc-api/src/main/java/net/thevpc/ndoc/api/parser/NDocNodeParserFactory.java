package net.thevpc.ndoc.api.parser;

import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.nuts.NCallableSupport;

/**
 * @author vpc
 */
public interface NDocNodeParserFactory {

    NCallableSupport<NDocItem> parseNode(NDocNodeFactoryParseContext context);

}
