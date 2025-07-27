/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.examples;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import  net.thevpc.ndoc.api.document.style.NDocProps;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.elem2d.NDocAlign;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.ndoc.api.renderer.NDocDocumentRenderer;
import net.thevpc.nuts.Nuts;

/**
 * @author vpc
 */
public class NDocExampleByCode1 {

    public static void main(String[] args) {
        Nuts.openWorkspace().share();
        NDocEngine e = new DefaultNDocEngine();
        NDocDocumentFactory f = e.documentFactory();
        NDocument d = f.ofDocument(null)
                .add(f.ofPage()
                        .addChild(
                                f.ofGroup()
                                        .setFontSize(50)
                                        .setFontBold(true)
                                        .addChild(
                                                f.ofRectangle()
                                                        .setPosition(0, 0)
                                                        .setSize(100, 15)
                                                        .setBackgroundColor("#3f4e70")
                                        )
                                        .addChild(
                                                f.ofRectangle()
                                                        .setPosition(0, 15)
                                                        .setSize(100, 1)
                                                        .setBackgroundColor("#21283a")
                                        )
                                        .addChild(
                                                f.ofPlain("Ceci est un Titre pas comme les autres")
                                                        .setPosition(50, 3)
                                                        .setForegroundColor("white")
                                                        .setFontSize(40)
                                                        .setOrigin(NDocAlign.TOP)
                                        )
                                        .addChild(
                                                f.ofCircle()
                                                        .setPosition(70, 50)
                                                        .setSize(20)
                                                        .setForegroundColor("yellow")
                                                        .setBackgroundColor("#ffaa00")
                                                        .setOrigin(NDocAlign.CENTER)
                                        )
                                        .addChild(
                                                f.ofRectangle()
                                                        .setPosition(80, 70)
                                                        .setSize(20, 20)
                                                        .setProperty(NDocProps.preserveShapeRatio())
                                                        .setForegroundColor("green")
                                                        .setBackgroundColor("#555500")
                                                        .setOrigin(NDocAlign.CENTER)
                                        )
                                        .addChild(
                                                f.ofGrid(2, 2)
                                                        .setProperty(NDocProps.rowsWeight(1, 3))
                                                        .addChild(f.ofGlue())
                                                        .addChild(f.ofGlue())
                                                        .addChild(

                                                                f.ofUnorderedList()
                                                                        .addChild(f.ofPlain("Ceci est un premier point"))
                                                                        .addChild(f.ofPlain("Ceci est un second point"))
                                                                        .addChild(f.ofPlain("Ceci est un troisieme point"))
                                                                        .setGridColor("gray")

                                                        )
                                        )
                                        .addChild(
                                                f.ofEquation("x=\\frac{-b \\pm \\sqrt {b^2-4ac}}{2a}")
                                                        .setPosition(10, 80)
                                                        .setOrigin(NDocAlign.LEFT)
                                        )
                        )
                );
        NDocDocumentRenderer renderer = e.newScreenRenderer().get();
        renderer.render(d);
    }
}
