package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HRectangle;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.tson.TsonElement;

public class HRectangleImpl extends AbstractHNode implements HRectangle {

    public HRectangleImpl() {
    }


    @Override
    public HNodeType type() {
        return HNodeType.RECTANGLE;
    }

    @Override
    public TsonElement toTson() {
        return ToTsonHelper.of(this)
                .build();
    }
}
