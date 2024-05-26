/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.renderer.screen;


import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.spi.renderer.HDocumentRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentScreenRenderer;
import net.thevpc.nuts.NSession;

import java.util.function.Supplier;

/**
 * @author vpc
 */
public class ScreenDocumentRenderer implements HDocumentScreenRenderer {

    private final NSession session;
    private HEngine halfaEngine;

    public ScreenDocumentRenderer(HEngine halfaEngine, NSession session) {
        this.session = session;
        this.halfaEngine = halfaEngine;
    }

    @Override
    public void render(HDocument document) {
        DocumentView dv = new DocumentView(()->document, halfaEngine, session);
        dv.show();
    }

    @Override
    public void renderSupplier(Supplier<HDocument> document) {
        DocumentView dv = new DocumentView(document, halfaEngine, session);
        dv.show();
    }
}
