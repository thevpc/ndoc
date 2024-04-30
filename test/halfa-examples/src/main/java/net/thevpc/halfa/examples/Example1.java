/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.api.HalfaEngine;
import net.thevpc.halfa.api.model.HDocument;
import net.thevpc.halfa.engine.DefaultHalfaEngine;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

/**
 *
 * @author vpc
 */
public class Example1 {

    public static void main(String[] args) {
        NSession session = Nuts.openWorkspace();
        HalfaEngine e = new DefaultHalfaEngine(session);
        NPath file = NPath.of("src/halfa/root.tson", session).toAbsolute().normalize();
        System.out.println(file);
        HDocument doc = e.loadDocument(file);
        HDocumentStreamRenderer renderer = e.newStreamRenderer("pdf");
        renderer.render(doc, NPath.ofUserHome(session).resolve("example.pdf"));
    }
}
