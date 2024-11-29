/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.spi.renderer.*;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NMsg;

/**
 * @author vpc
 */
public class ScreenDocumentStreamRendererFactory implements HDocumentRendererFactory {

    @Override
    public NCallableSupport<HDocumentRenderer> createDocumentRenderer(HDocumentRendererFactoryContext context) {
        switch (String.valueOf(context.rendererType()).toLowerCase()) {
            case "screen":
                return NCallableSupport.of(10, () -> new ScreenDocumentRenderer(context.engine(), context.session()));
            default:
                return NCallableSupport.invalid(() -> NMsg.ofPlain("factory"));
        }
    }

}
