/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.document.HDocument;

/**
 *
 * @author vpc
 */
public interface HDocumentRendererSupplier {
    HDocument get(HDocumentRendererContext renderer);
}
