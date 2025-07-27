package net.thevpc.ndoc.engine.control;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.List;

public class CtrlNDocNodeInclude extends CtrlNDocNodeBase {
    private List<NElement> __args = new ArrayList<>();

    public CtrlNDocNodeInclude(NDocResource source,List<NElement> __args) {
        super(NDocNodeType.CTRL_INCLUDE,source);
        this.__args.addAll(__args);
    }


    public List<NElement> getCallArgs() {
        return __args;
    }

    @Override
    public NDocNode copy() {
        CtrlNDocNodeInclude c = new CtrlNDocNodeInclude(source(),__args);
        copyTo(c);
        return c;
    }

    @Override
    public NDocNode copyTo(NDocNode other) {
        super.copyTo(other);
        if (other instanceof CtrlNDocNodeInclude) {
            CtrlNDocNodeInclude oc = (CtrlNDocNodeInclude) other;
            oc.__args = new ArrayList<>(__args);
        }
        return this;
    }


    public CtrlNDocNodeInclude setArgs(List<NElement> __args) {
        this.__args = __args;
        return this;
    }
}
