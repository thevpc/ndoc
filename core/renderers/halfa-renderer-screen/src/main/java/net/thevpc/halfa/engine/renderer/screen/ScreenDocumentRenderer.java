/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.spi.renderer.AbstractHDocumentRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentScreenRenderer;

import net.thevpc.halfa.spi.renderer.HDocumentRendererSupplier;

/**
 * @author vpc
 */
public class ScreenDocumentRenderer extends AbstractHDocumentRenderer implements HDocumentScreenRenderer {

    public ScreenDocumentRenderer(HEngine engine) {
        super(engine);
    }

    @Override
    public void renderSupplier(HDocumentRendererSupplier document) {
        DocumentView dv = new DocumentView(document, engine, eventListenerDelegate, messages);
    }
}
