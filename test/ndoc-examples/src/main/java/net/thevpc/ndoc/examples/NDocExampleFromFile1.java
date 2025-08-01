/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.examples;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRenderer;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public class NDocExampleFromFile1 {

    public static void main(String[] args) {
        Nuts.openWorkspace().share();
        NDocEngine e = new DefaultNDocEngine();
        NPath file = NPath.of("/home/mohamed/Desktop/stage/ndoc/documentation/tson-doc/main.ndoc").toAbsolute().normalize();
        System.out.println(file);
        NDocument doc = e.loadDocument(file).get();
        System.out.println(e.toElement(doc));
        NDocDocumentStreamRenderer renderer = e.newPdfRenderer().get();
        renderer.setOutput(NPath.ofUserHome().resolve("example.pdf"));
        renderer.render(doc);
    }
}
