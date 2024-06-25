/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public class HalfaExampleFromFile2 {

    public static void main(String[] args) {
        NSession session = Nuts.openWorkspace();
        HEngine e = new HEngineImpl(session);
        NPath file = NPath.of("src/halfa/test1.hd", session).toAbsolute().normalize();
        HDocument doc = e.loadDocument(file, null).get();

        System.out.println(e.toTson(doc));
    }
}
