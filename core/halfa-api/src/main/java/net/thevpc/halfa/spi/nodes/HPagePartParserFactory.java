package net.thevpc.halfa.spi.nodes;

import net.thevpc.halfa.api.model.HPagePart;
import net.thevpc.nuts.NCallableSupport;

/**
 *
 * @author vpc
 */
public interface HPagePartParserFactory {

    NCallableSupport<HPagePart> parsePagePart(HDocumentItemFactoryParseContext context);

}
