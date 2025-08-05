/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.engine.renderer.screen;

import net.thevpc.ntexup.api.renderer.NTxDocumentRenderer;
import net.thevpc.ntexup.api.renderer.NDocDocumentRendererFactory;
import net.thevpc.ntexup.api.renderer.NDocDocumentRendererFactoryContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NMsg;

/**
 * @author vpc
 */
public class ScreenDocumentStreamRendererFactory implements NDocDocumentRendererFactory {

    @Override
    public NCallableSupport<NTxDocumentRenderer> createDocumentRenderer(NDocDocumentRendererFactoryContext context) {
        switch (String.valueOf(context.rendererType()).toLowerCase()) {
            case "screen":
                return NCallableSupport.ofValid( () -> new ScreenDocumentRenderer(context.engine()));
            default:
                return NCallableSupport.ofInvalid(() -> NMsg.ofPlain("factory"));
        }
    }

}
