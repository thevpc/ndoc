/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

/**
 *
 * @author vpc
 */
public class HalfaExampleFromFolder {

    public static void main(String[] args) {
        NSession session = Nuts.openWorkspace();
        HEngine e = new HEngineImpl(session);
        NPath file = NPath.of("/home/vpc/education/education/modules/hyperfrequences/", session).toAbsolute().normalize();
        //NPath file = NPath.of("src/halfa/project", session).toAbsolute().normalize();
        System.out.println(file);
        System.out.println(e.loadDocument(file).get().toTson());
        e.newScreenRenderer().renderSupplier(()->e.loadDocument(file).get());
    }
}
