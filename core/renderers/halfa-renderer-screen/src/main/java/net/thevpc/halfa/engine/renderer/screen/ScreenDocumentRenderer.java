/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.renderer.screen;


import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageListImpl;
import net.thevpc.halfa.spi.renderer.AbstractHDocumentRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentScreenRenderer;
import net.thevpc.nuts.NSession;

import java.util.function.Supplier;

/**
 * @author vpc
 */
public class ScreenDocumentRenderer extends AbstractHDocumentRenderer implements HDocumentScreenRenderer {


    public ScreenDocumentRenderer(HEngine engine, NSession session) {
        super(engine, session);
    }

    @Override
    public void render(HDocument document) {
        HMessageList messages2=this.messages;
        if(messages2==null){
            messages2=new HMessageListImpl(session, engine.computeSource(document.root()));
        }
        DocumentView dv = new DocumentView(() -> document, engine, eventListenerDelegate, messages2, session);
        dv.show();
    }

    @Override
    public void renderSupplier(Supplier<HDocument> document) {
        DocumentView dv = new DocumentView(document, engine, eventListenerDelegate, messages, session);
        dv.show();
    }
}
