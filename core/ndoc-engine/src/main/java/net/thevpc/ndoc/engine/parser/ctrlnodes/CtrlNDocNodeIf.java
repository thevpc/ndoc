package net.thevpc.ndoc.engine.parser.ctrlnodes;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.elem.*;

import java.util.ArrayList;
import java.util.List;

public class CtrlNDocNodeIf extends CtrlNDocNodeBase {
    private NElement __cond;
    private List<NDocNode> __trueBloc;
    private List<NDocNode> __falseBloc;

    public CtrlNDocNodeIf(NDocResource source) {
        super(NDocNodeType.CTRL_IF,source);
    }

    public CtrlNDocNodeIf(NDocResource source,NElement __cond,List<NDocNode> __trueBloc,List<NDocNode> __falseBloc) {
        super(NDocNodeType.CTRL_IF,source);
        this.__cond = __cond;
        this.__trueBloc = __trueBloc;
        this.__falseBloc = __falseBloc;
    }

    public NElement getCond() {
        return __cond;
    }

    public List<NDocNode> getTrueBloc() {
        return __trueBloc;
    }

    public List<NDocNode> getFalseBloc() {
        return __falseBloc;
    }

    @Override
    public NDocNode copy() {
        CtrlNDocNodeIf c = new CtrlNDocNodeIf(source());
        copyTo(c);
        return c;
    }

    @Override
    public NDocNode copyTo(NDocNode other) {
        super.copyTo(other);
        if (other instanceof CtrlNDocNodeIf) {
            CtrlNDocNodeIf oc = (CtrlNDocNodeIf) other;
            oc.__cond = __cond;
            oc.__trueBloc =new ArrayList<>(__trueBloc);
            oc.__falseBloc = new ArrayList<>(__falseBloc);
        }
        return this;
    }
}
