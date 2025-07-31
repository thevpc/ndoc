package net.thevpc.ndoc.test;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.SizeD;
import net.thevpc.ndoc.elem.base.container.NDocNodeGridBagLayout;

import java.awt.*;

public class NDocNodeGridBagLayoutTest {
    public static void main(String[] args) {
        NDocNodeGridBagLayout t=new NDocNodeGridBagLayout(
                new Insets(0,0,0,0),
                false,
                new NDocBounds2(0,0,100,100),
                new NDocNodeGridBagLayout.NDocNodeGridNode[]{
                        new NDocNodeGridBagLayout.NDocNodeGridNode("one").pos(0,0).setPreferredSize(new SizeD(10,10)).setFill(NDocNodeGridBagLayout.Fill.BOTH).setAnchor(NDocNodeGridBagLayout.Anchor.NORTHWEST)
//                        ,new NDocNodeGridBagLayout.NDocNodeGridNode("two").pos(0,1).setPreferredSize(new SizeD(10,10)).setFill(NDocNodeGridBagLayout.Fill.BOTH).setAnchor(HNodeGridBagLayout.Anchor.NORTHWEST)
                }
        );
        t.doLayout();
        for (NDocNodeGridBagLayout.NDocNodeGridNode o : t.children()) {
            System.out.println(o);
        }
    }
}
