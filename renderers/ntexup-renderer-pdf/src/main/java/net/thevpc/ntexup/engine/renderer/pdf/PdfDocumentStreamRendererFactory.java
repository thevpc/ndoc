package net.thevpc.ntexup.engine.renderer.pdf;

import net.thevpc.ntexup.api.renderer.NTxDocumentRenderer;
import net.thevpc.ntexup.api.renderer.NDocDocumentRendererFactory;
import net.thevpc.ntexup.api.renderer.NDocDocumentRendererFactoryContext;
import net.thevpc.ntexup.api.renderer.NTxDocumentStreamRendererConfig;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NMsg;

/**
 * Factory class for creating PDF document stream renderers.
 */
public class PdfDocumentStreamRendererFactory implements NDocDocumentRendererFactory {

    @Override
    public NCallableSupport<NTxDocumentRenderer> createDocumentRenderer(NDocDocumentRendererFactoryContext context) {
        switch (String.valueOf(context.rendererType()).toLowerCase()) {
            case "pdf":
                return NCallableSupport.ofValid( () -> {
                    NTxDocumentStreamRendererConfig config = new NTxDocumentStreamRendererConfig();
                    return new PdfDocumentRenderer(context.engine(), config);
                });
            default:
                return NCallableSupport.ofInvalid(() -> NMsg.ofPlain("Invalid renderer type: " + context.rendererType()));
        }
    }
}
