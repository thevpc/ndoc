package net.thevpc.ndoc.engine.parser.ctrlnodes;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.elem.NElement;

public class CtrlNDocNodeName extends CtrlNDocNodeBase {
    private NElement __varName;

    private CtrlNDocNodeName(NDocResource source) {
        super(NDocNodeType.CTRL_NAME,source);
    }

    public CtrlNDocNodeName(NDocResource source, NElement __varName) {
        super(NDocNodeType.CTRL_NAME,source);
        this.__varName=__varName;
    }

    public NElement getVarName() {
        return __varName;
    }

    @Override
    public NDocNode copy() {
        CtrlNDocNodeName c = new CtrlNDocNodeName(source());
        copyTo(c);
        return c;
    }

    @Override
    public NDocNode copyTo(NDocNode other) {
        super.copyTo(other);
        if (other instanceof CtrlNDocNodeName) {
            CtrlNDocNodeName oc = (CtrlNDocNodeName) other;
            oc.__varName = __varName;
        }
        return this;
    }

    @Override
    public String toString() {
        return "Name("+__varName+')';
    }
}
