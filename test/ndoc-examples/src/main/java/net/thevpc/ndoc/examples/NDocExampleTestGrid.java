/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.examples;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import  net.thevpc.ndoc.api.document.style.NDocProps;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.ndoc.api.renderer.NDocDocumentRenderer;
import net.thevpc.nuts.Nuts;
import net.thevpc.nuts.elem.NElement;


import java.awt.*;

/**
 * @author vpc
 */
public class NDocExampleTestGrid {

    public static void main(String[] args) {
        Nuts.openWorkspace().share();
        NDocEngine e = new DefaultNDocEngine();
        NDocDocumentFactory f = e.documentFactory();
        NDocument d = f.ofDocument(null)
                .add(f.ofPage()
                        .add(
                                f.ofGrid(3, 2)
                                        .setProperty(NDocPropName.COLUMNS_WEIGHT, NElement.ofDoubleArray(1, 2, 3, 4, 5, 6))
                                        .add(f.ofRectangle()
                                                .setProperty(NDocProps.backgroundColor(Color.BLUE))
                                                .setProperty(NDocProps.colspan(4))
                                        )
                                        .add(
                                                f.ofRectangle().setProperty(NDocProps.backgroundColor(Color.RED))
                                                        .setProperty(NDocProps.rowspan(4))
                                        )
                                        .add(f.ofGrid(4, 4)
                                                .setProperty(NDocProps.colspan(3))
                                                .setProperty(NDocProps.rowspan(2))
                                                .add(f.ofRectangle().setProperty(NDocProps.backgroundColor(Color.BLUE))
                                                        .setProperty(NDocProps.colspan(5))
                                                        .setProperty(NDocProps.rowspan(2))
                                                )
                                                .add(f.ofRectangle().setProperty(NDocProps.backgroundColor(Color.RED)))
                                                .add(f.ofRectangle().setProperty(NDocProps.backgroundColor(Color.GREEN)))
                                                .add(f.ofRectangle().setProperty(NDocProps.backgroundColor(Color.YELLOW))))
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
