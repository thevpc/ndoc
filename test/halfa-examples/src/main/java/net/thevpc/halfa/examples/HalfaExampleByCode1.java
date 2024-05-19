/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.HalfaDocumentFactory;
import net.thevpc.halfa.api.HStyles;
import net.thevpc.halfa.api.HalfaEngine;
import net.thevpc.halfa.api.model.HDocument;
import net.thevpc.halfa.api.model.HAnchor;
import net.thevpc.halfa.engine.DefaultHalfaEngine;
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
        HalfaEngine e = new DefaultHalfaEngine(session);
        HalfaDocumentFactory f = e.factory();
        HDocument d = f.document()
                .add(f.page()
                                .add(
                                        f.container()
                                                .set(HStyles.fontSize(40))
                                                .set(HStyles.fontBold())
                                                .add(
                                                        f.text(40, 40, "Hello")
                                                                .set(HStyles.position(50, 0))
                                                                .set(HStyles.anchor(HAnchor.NORTH_CENTER))
                                                )
                                                .add(
                                                        f.latexEquation(40, 50, "x=\\frac{-b \\pm \\sqrt {b^2-4ac}}{2a}")
                                                                .set(HStyles.position(50, 50))
                                                                .set(HStyles.anchor(HAnchor.CENTER))
                                                )
                                )
                );
        HDocumentRenderer renderer = e.newRenderer("screen");
        renderer.render(d);
    }
}
