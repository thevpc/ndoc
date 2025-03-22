package net.thevpc.ndoc.engine.renderer.pdf;

import net.thevpc.ndoc.spi.renderer.*;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NMsg;

/**
 * Factory class for creating PDF document stream renderers.
 */
public class PdfDocumentStreamRendererFactory implements NDocDocumentRendererFactory {

    @Override
    public NCallableSupport<NDocDocumentRenderer> createDocumentRenderer(NDocDocumentRendererFactoryContext context) {
        switch (String.valueOf(context.rendererType()).toLowerCase()) {
            case "pdf":
                return NCallableSupport.of(10, () -> {
                    NDocDocumentStreamRendererConfig config = new NDocDocumentStreamRendererConfig();
                    return new PdfDocumentRenderer(context.engine(), config);
                });
            default:
                return NCallableSupport.invalid(() -> NMsg.ofPlain("Invalid renderer type: " + context.rendererType()));
        }
    }
}
