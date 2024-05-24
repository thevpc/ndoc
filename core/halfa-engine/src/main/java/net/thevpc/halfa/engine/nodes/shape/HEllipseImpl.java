package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HEllipse;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.tson.TsonElement;

public class HEllipseImpl extends AbstractHNode implements HEllipse {

    public HEllipseImpl() {
    }

    @Override
    public HNodeType type() {
        return HNodeType.ELLIPSE;
    }

    @Override
    public TsonElement toTson() {
        return ToTsonHelper.of(this).build();
    }

}
