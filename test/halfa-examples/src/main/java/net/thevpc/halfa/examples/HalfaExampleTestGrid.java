/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.engine.HEngineImpl;
import net.thevpc.halfa.spi.renderer.HDocumentRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.Nuts;

import java.awt.*;

/**
 * @author vpc
 */
public class HalfaExampleTestGrid {

    public static void main(String[] args) {
        NSession session = Nuts.openWorkspace();
        HEngine e = new HEngineImpl(session);
        HDocumentFactory f = e.documentFactory();
        HDocument d = f.ofDocument()
                .add(f.ofPage()
                                .add(
                                        f.ofGrid(3,2)
                                                .set(HStyles.columnsWeight(1,2,3,4,5,6))
                                                .add(f.ofRectangle()
                                                        .set(HStyles.backgroundColor(Color.BLUE))
                                                        .set(HStyles.colspan(4))
                                                )
                                                .add(
                                                        f.ofRectangle().set(HStyles.backgroundColor(Color.RED))
                                                                .set(HStyles.rowspan(4))
                                                )
                                                .add(f.ofGrid(4,4)
                                                        .set(HStyles.colspan(3))
                                                        .set(HStyles.rowspan(2))
                                                .add(f.ofRectangle().set(HStyles.backgroundColor(Color.BLUE))
                                                        .set(HStyles.colspan(5))
                                                        .set(HStyles.rowspan(2))
                                                )
                                                .add(f.ofRectangle().set(HStyles.backgroundColor(Color.RED)))
                                                .add(f.ofRectangle().set(HStyles.backgroundColor(Color.GREEN)))
                                                .add(f.ofRectangle().set(HStyles.backgroundColor(Color.YELLOW))))
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
