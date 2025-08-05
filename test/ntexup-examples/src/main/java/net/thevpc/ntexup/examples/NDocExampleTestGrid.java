/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.examples;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import  net.thevpc.ntexup.api.document.style.NDocPropName;
import  net.thevpc.ntexup.api.document.style.NDocProps;
import net.thevpc.ntexup.api.engine.NDocEngine;
import net.thevpc.ntexup.api.document.NDocument;
import net.thevpc.ntexup.engine.DefaultNDocEngine;
import net.thevpc.ntexup.api.renderer.NDocDocumentRenderer;
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
        NTxDocumentFactory f = e.documentFactory();
        NDocument d = f.ofDocument(null)
                .add(f.ofPage()
                        .addChild(
                                f.ofGrid(3, 2)
                                        .setProperty(NDocPropName.COLUMNS_WEIGHT, NElement.ofDoubleArray(1, 2, 3, 4, 5, 6))
                                        .addChild(f.ofRectangle()
                                                .setProperty(NDocProps.backgroundColor(Color.BLUE))
                                                .setProperty(NDocProps.colspan(4))
                                        )
                                        .addChild(
                                                f.ofRectangle().setProperty(NDocProps.backgroundColor(Color.RED))
                                                        .setProperty(NDocProps.rowspan(4))
                                        )
                                        .addChild(f.ofGrid(4, 4)
                                                .setProperty(NDocProps.colspan(3))
                                                .setProperty(NDocProps.rowspan(2))
                                                .addChild(f.ofRectangle().setProperty(NDocProps.backgroundColor(Color.BLUE))
                                                        .setProperty(NDocProps.colspan(5))
                                                        .setProperty(NDocProps.rowspan(2))
                                                )
                                                .addChild(f.ofRectangle().setProperty(NDocProps.backgroundColor(Color.RED)))
                                                .addChild(f.ofRectangle().setProperty(NDocProps.backgroundColor(Color.GREEN)))
                                                .addChild(f.ofRectangle().setProperty(NDocProps.backgroundColor(Color.YELLOW))))
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
