/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.examples;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.style.NDocProps;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.engine.DefaultNDocEngine;
import net.thevpc.ndoc.spi.renderer.NDocDocumentRenderer;
import net.thevpc.nuts.Nuts;

import java.awt.*;

/**
 * @author vpc
 */
public class NDocExampleTestBullets {

    public static void main(String[] args) {
        Nuts.openWorkspace().share();
        NDocEngine e = new DefaultNDocEngine();
        NDocDocumentFactory f = e.documentFactory();
        NDocument d = f.ofDocument(null)
                .add(f.ofPage()
                        .add(
//                                f.text("Example 1").set(HStyles.fontSize(12))
                                f.ofUnorderedList()
                                        .setProperty(NDocProps.gridColor(Color.GRAY))
                                        .add(f.ofPlain("Example 1").setProperty(NDocProps.backgroundColor(Color.CYAN)))
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


        NDocDocumentRenderer renderer = e.newScreenRenderer().get();
        renderer.render(d);
    }

//    private static HGridContainer cc(NDocDocumentFactory f){
//        List<NDocNode> all = new ArrayList<>();
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
//        return f.grid(2, nb, all.toArray(new NDocNode[0]))
//                .set(HStyles.columnsWeight(1, 20))
//                ;
//    }
}
