/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.engine.renderer.screen.debug.HDebugFrame;
import net.thevpc.halfa.spi.renderer.HDocumentScreenRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

/**
 *
 * @author vpc
 */
public class HalfaExampleFromFolder2 {

    public static void main(String[] args) {
        NSession session = Nuts.openWorkspace();
        HEngine engine = new HEngineImpl(session);

        HDebugFrame f=new HDebugFrame(engine);
        f.model().setEngine(engine);
        String folder = "/home/vpc/education/education/modules/hyperfrequences/";
//        String folder = "/home/vpc/xprojects/nuts/nuts-enterprise/halfa/test/halfa-examples/src/halfa/examples/example003/";
        NPath file = NPath.of(folder, session).toAbsolute().normalize();
        System.out.println(file);
        System.out.println(engine.toTson(engine.loadDocument(file, f.model().messages()).get()));
        HDocumentScreenRenderer renderer = engine.newScreenRenderer();
        renderer.setMessages(f.messages());
        renderer.addRendererListener(f.rendererListener());
        renderer.renderSupplier(()->engine.loadDocument(file, f.messages()).get());
    }
}
