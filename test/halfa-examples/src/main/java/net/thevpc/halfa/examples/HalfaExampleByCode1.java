/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.style.HProps;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.model.elem2d.HAlign;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;

/**
 * @author vpc
 */
public class HalfaExampleByCode1 {

    public static void main(String[] args) {
        NSession session = Nuts.openWorkspace();
        HEngine e = new HEngineImpl(session);
        HDocumentFactory f = e.documentFactory();
        HDocument d = f.ofDocument()
                .add(f.ofPage()
                        .add(
                                f.ofStack()
                                        .setFontSize(50)
                                        .setFontBold(true)
                                        .add(
                                                f.ofRectangle()
                                                        .setPosition(0, 0)
                                                        .setSize(100, 15)
                                                        .setBackgroundColor("#3f4e70")
                                        )
                                        .add(
                                                f.ofRectangle()
                                                        .setPosition(0, 15)
                                                        .setSize(100, 1)
                                                        .setBackgroundColor("#21283a")
                                        )
                                        .add(
                                                f.ofPlain("Ceci est un Titre pas comme les autres")
                                                        .setPosition(50, 3)
                                                        .setForegroundColor("white")
                                                        .setFontSize(40)
                                                        .setOrigin(HAlign.TOP)
                                        )
                                        .add(
                                                f.ofCircle()
                                                        .setPosition(70, 50)
                                                        .setSize(20)
                                                        .setForegroundColor("yellow")
                                                        .setBackgroundColor("#ffaa00")
                                                        .setOrigin(HAlign.CENTER)
                                        )
                                        .add(
                                                f.ofRectangle()
                                                        .setPosition(80, 70)
                                                        .setSize(20, 20)
                                                        .setProperty(HProps.preserveShapeRatio())
                                                        .setForegroundColor("green")
                                                        .setBackgroundColor("#555500")
                                                        .setOrigin(HAlign.CENTER)
                                        )
                                        .add(
                                                f.ofGrid(2, 2)
                                                        .setProperty(HProps.rowsWeight(1, 3))
                                                        .add(f.ofGlue())
                                                        .add(f.ofGlue())
                                                        .add(

                                                                f.ofUnorderedList()
                                                                        .add(f.ofPlain("Ceci est un premier point"))
                                                                        .add(f.ofPlain("Ceci est un second point"))
                                                                        .add(f.ofPlain("Ceci est un troisieme point"))
                                                                        .setGridColor("gray")

                                                        )
                                        )
                                        .add(
                                                f.ofEquation("x=\\frac{-b \\pm \\sqrt {b^2-4ac}}{2a}")
                                                        .setPosition(10, 80)
                                                        .setOrigin(HAlign.LEFT)
                                        )
                        )
                );
        HDocumentRenderer renderer = e.newScreenRenderer();
        renderer.render(d);
    }
}
