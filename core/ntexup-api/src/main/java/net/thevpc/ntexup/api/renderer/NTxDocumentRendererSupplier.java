/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.NTxDocument;

/**
 * @author vpc
 */
public interface NTxDocumentRendererSupplier {
    NTxDocument get(NTxDocumentRendererContext renderer);
}
