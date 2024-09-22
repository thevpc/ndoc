package net.thevpc.halfa.test;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.SizeD;
import net.thevpc.halfa.elem.base.container.grid.HNodeGridBagLayout;

import java.awt.*;

public class HNodeGridBagLayoutTest {
    public static void main(String[] args) {
        HNodeGridBagLayout t=new HNodeGridBagLayout(
                new Insets(0,0,0,0),
                false,
                new Bounds2(0,0,100,100),
                new HNodeGridBagLayout.HNodeGridNode[]{
                        new HNodeGridBagLayout.HNodeGridNode("one").pos(0,0).setPreferredSize(new SizeD(10,10)).setFill(HNodeGridBagLayout.Fill.BOTH).setAnchor(HNodeGridBagLayout.Anchor.NORTHWEST)
//                        ,new HNodeGridBagLayout.HNodeGridNode("two").pos(0,1).setPreferredSize(new SizeD(10,10)).setFill(HNodeGridBagLayout.Fill.BOTH).setAnchor(HNodeGridBagLayout.Anchor.NORTHWEST)
                }
        );
        t.doLayout();
        for (HNodeGridBagLayout.HNodeGridNode o : t.children()) {
            System.out.println(o);
        }
    }
}
