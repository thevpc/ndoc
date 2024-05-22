/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;

import java.awt.*;

/**
 * @author vpc
 */
public class HalfaExampleByCode1 {

    public static void main(String[] args) {
        NSession session = Nuts.openWorkspace();
        HEngine e = new HEngineImpl(session);
        HDocumentFactory f = e.documentFactory();
        HDocument d = f.document()
                .add(f.page()
                        .add(
                                f.stack()
                                        .set(HStyles.fontSize(50))
                                        .set(HStyles.fontBold())
                                        .add(
                                                f.rectangle(0, 0, 100, 15)
                                                        .set(HStyles.backgroundColor(new Color(0x3f4e70)))
                                        )
                                        .add(
                                                f.rectangle(0, 15, 100, 1)
                                                        .set(HStyles.backgroundColor(new Color(0X21283a)))
                                        )
                                        .add(
                                                f.text(50, 3, "Ceci est un Titre pas comme les autres")
                                                        .set(HStyles.foregroundColor(Color.WHITE))
                                                        .set(HStyles.fontSize(40))
                                                        .set(HStyles.origin(HAlign.TOP))
                                        )
                                        .add(
                                                f.circle(70, 50, 20)
                                                        .set(HStyles.foregroundColor(Color.YELLOW))
                                                        .set(HStyles.backgroundColor(new Color(0xffaa00)))
                                                        .set(HStyles.lineColor(Color.RED))
                                                        .set(HStyles.origin(HAlign.CENTER))
                                        )
                                        .add(
                                                f.rectangle(80, 70, 20, 20)
                                                        .set(HStyles.preserveShapeRatio())
                                                        .set(HStyles.foregroundColor(Color.GREEN))
                                                        .set(HStyles.backgroundColor(new Color(0x555500)))
                                                        .set(HStyles.lineColor(Color.RED))
                                                        .set(HStyles.origin(HAlign.CENTER))
                                        )
                                        .add(
                                                f.grid(2, 2)
                                                        .set(HStyles.rowsWeight(1, 3))
                                                        .add(f.glue())
                                                        .add(f.glue())
                                                        .add(

                                                                f.unorderedList()
                                                                        .set(HStyles.gridColor(Color.GRAY))
                                                                        .add(f.text("Ceci est un premier point"))
                                                                        .add(f.text("Ceci est un second point"))
                                                                        .add(f.text("Ceci est un troisieme point"))

                                                        )
                                        )
                                        .add(
                                                f.equation(10, 80, "x=\\frac{-b \\pm \\sqrt {b^2-4ac}}{2a}")
                                                        .set(HStyles.origin(HAlign.LEFT))
                                        )
                        )
                );
        HDocumentRenderer renderer = e.newScreenRenderer();
        renderer.render(d);
    }
}
