package net.thevpc.halfa.spi.nodes;

import net.thevpc.halfa.api.model.HDocumentPart;
import net.thevpc.nuts.NCallableSupport;

/**
 *
 * @author vpc
 */
public interface HDocumentPartParserFactory {

    NCallableSupport<HDocumentPart> parseDocumentPart(HDocumentItemFactoryParseContext context);

}
