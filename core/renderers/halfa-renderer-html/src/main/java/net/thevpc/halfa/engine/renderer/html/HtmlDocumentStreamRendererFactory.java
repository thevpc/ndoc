/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.renderer.html;

import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRendererFactory;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRendererFactoryContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NMsg;

/**
 *
 * @author vpc
 */
public class HtmlDocumentStreamRendererFactory implements HDocumentStreamRendererFactory {

    @Override
    public NCallableSupport<HDocumentStreamRenderer> create(HDocumentStreamRendererFactoryContext context) {
        switch (String.valueOf(context.rendererType()).toLowerCase()) {
            case "html":
                return NCallableSupport.of(10, () -> new HtmlDocumentRenderer(context.engine(), context.session()));
            default:
                return NCallableSupport.invalid(s -> NMsg.ofPlain("factory"));
        }
    }

}
