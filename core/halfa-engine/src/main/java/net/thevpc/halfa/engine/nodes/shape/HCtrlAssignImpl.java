package net.thevpc.halfa.engine.nodes.shape;

import net.thevpc.halfa.api.node.HCtrlAssign;
import net.thevpc.halfa.api.node.HEllipse;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.engine.nodes.AbstractHNode;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.halfa.spi.TsonSer;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public class HCtrlAssignImpl extends AbstractHNode implements HCtrlAssign {

    private String left;
    private Object right;


    public HCtrlAssignImpl() {
    }

    public String getLeft() {
        return left;
    }

    public HCtrlAssignImpl setLeft(String left) {
        this.left = left;
        return this;
    }

    public Object getRight() {
        return right;
    }

    public HCtrlAssignImpl setRight(Object right) {
        this.right = right;
        return this;
    }

    @Override
    public HNodeType type() {
        return HNodeType.CTRL_ASSIGN;
    }

    @Override
    public String toString() {
        return left + " = " + right;
    }

    @Override
    public TsonElement toTson() {
        return Tson.pair("$" + left, TsonSer.toTson(right));
    }


}
