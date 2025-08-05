/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.examples;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.document.NTxDocument;
import net.thevpc.ntexup.engine.DefaultNTxEngine;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public class NDocExampleFromFile2 {

    public static void main(String[] args) {
        Nuts.openWorkspace().share();
        NTxEngine e = new DefaultNTxEngine();
        NPath file = NPath.of("src/ndoc/test1.ndoc").toAbsolute().normalize();
        NTxDocument doc = e.loadDocument(file).get();

        System.out.println(e.toElement(doc));
    }
}
