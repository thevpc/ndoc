/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.api.renderer;

import net.thevpc.nuts.NCallableSupport;

/**
 * @author vpc
 */
public interface NDocDocumentRendererFactory {

    NCallableSupport<NDocDocumentRenderer> createDocumentRenderer(NDocDocumentRendererFactoryContext context);

}
