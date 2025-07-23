/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.renderer.screen;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.renderer.AbstractNDocDocumentRenderer;
import net.thevpc.ndoc.api.renderer.NDocDocumentScreenRenderer;

import net.thevpc.ndoc.api.renderer.NDocDocumentRendererSupplier;

/**
 * @author vpc
 */
public class ScreenDocumentRenderer extends AbstractNDocDocumentRenderer implements NDocDocumentScreenRenderer {

    public ScreenDocumentRenderer(NDocEngine engine) {
        super(engine);
    }

    @Override
    public void renderSupplier(NDocDocumentRendererSupplier document) {
        DocumentView dv = new DocumentView(document, engine, eventListenerDelegate, messages);
    }
}
