/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.examples;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.style.HProps;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.ndoc.spi.renderer.NDocDocumentRenderer;
import net.thevpc.nuts.Nuts;
import net.thevpc.tson.Tson;

import java.awt.*;

/**
 * @author vpc
 */
public class NDocExampleTestGrid {

    public static void main(String[] args) {
        Nuts.openWorkspace().share();
        NDocEngine e = new DefaultNDocEngine();
        NDocDocumentFactory f = e.documentFactory();
        NDocument d = f.ofDocument()
                .add(f.ofPage()
                        .add(
                                f.ofGrid(3, 2)
                                        .setProperty(HPropName.COLUMNS_WEIGHT, NElements.of().of(new double[]{1, 2, 3, 4, 5, 6}))
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
        NDocDocumentRenderer renderer = e.newScreenRenderer().get();
        renderer.render(d);
    }
}
