package net.thevpc.halfa.engine.renderer.pdf;

import net.thevpc.halfa.spi.renderer.*;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NMsg;

/**
 * Factory class for creating PDF document stream renderers.
 */
public class PdfDocumentStreamRendererFactory implements HDocumentRendererFactory {

    @Override
    public NCallableSupport<HDocumentRenderer> createDocumentRenderer(HDocumentRendererFactoryContext context) {
        switch (String.valueOf(context.rendererType()).toLowerCase()) {
            case "pdf":
                return NCallableSupport.of(10, () -> {
                    HDocumentStreamRendererConfig config = new HDocumentStreamRendererConfig();
                    return new PdfDocumentRenderer(context.engine(), context.session(), config);
                });
            default:
                return NCallableSupport.invalid(() -> NMsg.ofPlain("Invalid renderer type: " + context.rendererType()));
        }
    }
}
