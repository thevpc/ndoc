/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProps;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentRenderer;
import net.thevpc.nuts.Nuts;
import net.thevpc.tson.Tson;

import java.awt.*;

/**
 * @author vpc
 */
public class HalfaExampleTestGrid {

    public static void main(String[] args) {
        Nuts.openWorkspace().share();
        HEngine e = new HEngineImpl();
        HDocumentFactory f = e.documentFactory();
        HDocument d = f.ofDocument()
                .add(f.ofPage()
                        .add(
                                f.ofGrid(3, 2)
                                        .setProperty(HPropName.COLUMNS_WEIGHT, Tson.of(new double[]{1, 2, 3, 4, 5, 6}))
                                        .add(f.ofRectangle()
                                                .setProperty(HProps.backgroundColor(Color.BLUE))
                                                .setProperty(HProps.colspan(4))
                                        )
                                        .add(
                                                f.ofRectangle().setProperty(HProps.backgroundColor(Color.RED))
                                                        .setProperty(HProps.rowspan(4))
                                        )
                                        .add(f.ofGrid(4, 4)
                                                .setProperty(HProps.colspan(3))
                                                .setProperty(HProps.rowspan(2))
                                                .add(f.ofRectangle().setProperty(HProps.backgroundColor(Color.BLUE))
                                                        .setProperty(HProps.colspan(5))
                                                        .setProperty(HProps.rowspan(2))
                                                )
                                                .add(f.ofRectangle().setProperty(HProps.backgroundColor(Color.RED)))
                                                .add(f.ofRectangle().setProperty(HProps.backgroundColor(Color.GREEN)))
                                                .add(f.ofRectangle().setProperty(HProps.backgroundColor(Color.YELLOW))))
                        )
                )
//                .add(f.page()
//                                .add(
//
//                                )
//                )
                ;
        HDocumentRenderer renderer = e.newScreenRenderer();
        renderer.render(d);
    }
}
