/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.renderer.screen;

import net.thevpc.ndoc.api.renderer.NDocDocumentRenderer;
import net.thevpc.ndoc.api.renderer.NDocDocumentRendererFactory;
import net.thevpc.ndoc.api.renderer.NDocDocumentRendererFactoryContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NMsg;

/**
 * @author vpc
 */
public class ScreenDocumentStreamRendererFactory implements NDocDocumentRendererFactory {

    @Override
    public NCallableSupport<NDocDocumentRenderer> createDocumentRenderer(NDocDocumentRendererFactoryContext context) {
        switch (String.valueOf(context.rendererType()).toLowerCase()) {
            case "screen":
                return NCallableSupport.ofValid( () -> new ScreenDocumentRenderer(context.engine()));
            default:
                return NCallableSupport.ofInvalid(() -> NMsg.ofPlain("factory"));
        }
    }

}
