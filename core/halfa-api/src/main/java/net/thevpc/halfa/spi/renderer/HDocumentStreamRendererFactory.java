/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.spi.renderer;

import net.thevpc.nuts.NCallableSupport;

/**
 * @author vpc
 */
public interface HDocumentStreamRendererFactory extends HDocumentRendererFactory {

    NCallableSupport<HDocumentStreamRenderer> createDocumentStreamRenderer(HDocumentRendererFactoryContext context);

    default NCallableSupport<HDocumentRenderer> createDocumentRenderer(HDocumentRendererFactoryContext context) {
        return (NCallableSupport) createDocumentStreamRenderer(context);
    }

}
