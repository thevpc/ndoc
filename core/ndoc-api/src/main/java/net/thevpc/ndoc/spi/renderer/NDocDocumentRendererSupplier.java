/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.spi.renderer;

import net.thevpc.ndoc.api.document.NDocument;

/**
 * @author vpc
 */
public interface NDocDocumentRendererSupplier {
    NDocument get(NDocDocumentRendererContext renderer);
}
