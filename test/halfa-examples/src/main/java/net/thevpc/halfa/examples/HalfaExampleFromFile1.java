/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public class HalfaExampleFromFile1 {

    public static void main(String[] args) {
        Nuts.openWorkspace().setSharedInstance();
        HEngine e = new HEngineImpl();
        NPath file = NPath.of("/home/mohamed/Desktop/stage/halfa/documentation/tson-doc/main.hd").toAbsolute().normalize();
        System.out.println(file);
        HDocument doc = e.loadDocument(file, null).get();
        System.out.println(e.toTson(doc));
        HDocumentStreamRenderer renderer = e.newStreamRenderer("pdf");
        renderer.setOutput(NPath.ofUserHome().resolve("example.pdf"));
        renderer.render(doc);
    }
}
