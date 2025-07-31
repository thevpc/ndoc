/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.renderer.html;

import net.thevpc.ndoc.api.renderer.NDocDocumentRenderer;
import net.thevpc.ndoc.api.renderer.NDocDocumentRendererFactory;
import net.thevpc.ndoc.api.renderer.NDocDocumentRendererFactoryContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NMsg;

/**
 * @author vpc
 */
public class NDocHtmlDocumentStreamRendererFactory implements NDocDocumentRendererFactory {
    @Override
    public NCallableSupport<NDocDocumentRenderer> createDocumentRenderer(NDocDocumentRendererFactoryContext context) {
        switch (String.valueOf(context.rendererType()).toLowerCase()) {
            case "html":
                return NCallableSupport.valid( () -> new NDocHtmlDocumentRenderer(context.engine()));
            default:
                return NCallableSupport.invalid(() -> NMsg.ofPlain("factory"));
        }
    }


}
