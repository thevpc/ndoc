package net.thevpc.ndoc.engine.control;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;

public class CtrlNDocNodeFor extends CtrlNDocNodeBase {
    private NElement __varName;
    private NElement __varExpr;
    private List<NElement> __body = new ArrayList<>();

    private CtrlNDocNodeFor(NDocResource source) {
        super(NDocNodeType.CTRL_FOR,source);
    }

    public CtrlNDocNodeFor(NDocResource source,NElement __varName,NElement __varExpr,List<NElement> __body) {
        super(NDocNodeType.CTRL_FOR,source);
        this.__varName=__varName;
        this.__varExpr=__varExpr;
        this.__body=__body;
    }

    public NElement getVarName() {
        return __varName;
    }

    public NElement getVarExpr() {
        return __varExpr;
    }

    public List<NElement> getBody() {
        return __body;
    }

    @Override
    public NDocNode copy() {
        CtrlNDocNodeFor c = new CtrlNDocNodeFor(source());
        copyTo(c);
        return c;
    }

    @Override
    public NDocNode copyTo(NDocNode other) {
        super.copyTo(other);
        if (other instanceof CtrlNDocNodeFor) {
            CtrlNDocNodeFor oc = (CtrlNDocNodeFor) other;
            oc.__varName = __varName;
            oc.__varExpr = __varExpr;
            oc.__body = new ArrayList<>(__body);
        }
        return this;
    }
}
