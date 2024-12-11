/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.renderer.html;

import net.thevpc.halfa.spi.renderer.*;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NMsg;

/**
 * @author vpc
 */
public class HtmlDocumentStreamRendererFactory implements HDocumentRendererFactory {
    @Override
    public NCallableSupport<HDocumentRenderer> createDocumentRenderer(HDocumentRendererFactoryContext context) {
        switch (String.valueOf(context.rendererType()).toLowerCase()) {
            case "html":
                return NCallableSupport.of(10, () -> new HtmlDocumentRenderer(context.engine()));
            default:
                return NCallableSupport.invalid(() -> NMsg.ofPlain("factory"));
        }
    }


}
