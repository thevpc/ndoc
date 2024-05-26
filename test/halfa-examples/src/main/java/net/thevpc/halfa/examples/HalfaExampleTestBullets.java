/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.examples;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.style.HProps;
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
public class HalfaExampleTestBullets {

    public static void main(String[] args) {
        NSession session = Nuts.openWorkspace();
        HEngine e = new HEngineImpl(session);
        HDocumentFactory f = e.documentFactory();
        HDocument d = f.ofDocument()
                .add(f.ofPage()
                        .add(
//                                f.text("Example 1").set(HStyles.fontSize(12))
                                f.ofUnorderedList()
                                        .setProperty(HProps.gridColor(Color.GRAY))
                                        .add(f.ofPlain("Example 1").setProperty(HProps.backgroundColor(Color.CYAN)))
//                                        .add(f.square(80).set(HStyles.backgroundColor(Color.YELLOW)).set(HStyles.position(HAlign.LEFT)).set(HStyles.origin(HAlign.LEFT)))
//                                        .add(f.square(80).set(HStyles.backgroundColor(Color.RED)).set(HStyles.position(HAlign.LEFT)).set(HStyles.origin(HAlign.LEFT)))
//                                        .add(f.square(80).set(HStyles.backgroundColor(Color.BLUE)).set(HStyles.position(HAlign.LEFT)).set(HStyles.origin(HAlign.LEFT)))
//                                        .add(f.text("Example 1").set(HStyles.fontSize(12)).set(HStyles.position(HAlign.LEFT)).set(HStyles.origin(HAlign.LEFT)).set(HStyles.backgroundColor(Color.CYAN)))
//                                        .add(f.text("Example 2").set(HStyles.fontSize(12)).set(HStyles.position(HAlign.LEFT)).set(HStyles.origin(HAlign.LEFT)))
//                                        .add(f.text("Example 3").set(HStyles.fontSize(12)).set(HStyles.position(HAlign.LEFT)).set(HStyles.origin(HAlign.LEFT)))
//                                        .add(f.text("Example 4").set(HStyles.fontSize(12)).set(HStyles.position(HAlign.LEFT))).set(HStyles.origin(HAlign.LEFT))
//                                        .add(f.text("Example 5").set(HStyles.fontSize(12)).set(HStyles.position(HAlign.LEFT)).set(HStyles.origin(HAlign.LEFT)))
//                                        .add(f.text("Example 6").set(HStyles.fontSize(12)).set(HStyles.position(HAlign.LEFT)).set(HStyles.origin(HAlign.LEFT)))
//                        .add(
//                                cc(f)
//                        )
                        ));


        HDocumentRenderer renderer = e.newScreenRenderer();
        renderer.render(d);
    }

//    private static HGridContainer cc(HalfaDocumentFactory f){
//        List<HNode> all = new ArrayList<>();
//        int nb=6;
//        for (int i = 0; i < nb; i++) {
//            all.add(f.circle(50)
//                    .set(HStyles.anchor(HAnchor.CENTER))
//                    .set(HStyles.backgroundColor(Color.GREEN))
//                    .set(HStyles.lineColor(Color.GREEN.darker()))
//            );
////            all.add(child.set(HStyles.anchor(HAnchor.LEFT)));
//            if(i%2==0) {
//                all.add(f.rectangle().set(HStyles.backgroundColor(Color.BLUE)).set(HStyles.anchor(HAnchor.LEFT)));
//            }else{
//                all.add(f.rectangle().set(HStyles.backgroundColor(Color.RED)).set(HStyles.anchor(HAnchor.LEFT)));
//            }
//        }
//        return f.grid(2, nb, all.toArray(new HNode[0]))
//                .set(HStyles.columnsWeight(1, 20))
//                ;
//    }
}
