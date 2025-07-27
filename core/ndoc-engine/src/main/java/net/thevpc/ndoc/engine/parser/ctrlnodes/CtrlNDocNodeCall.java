package net.thevpc.ndoc.engine.control;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CtrlNDocNodeCall extends CtrlNDocNodeBase {
    private List<NElement> __args = new ArrayList<>();
    private String callName;
    private NElement __callExpr;
    private List<NElement> __callBody = new ArrayList<>();
    private Map<String,NElement> __bodyVars = new HashMap<>();

    public CtrlNDocNodeCall(NDocResource source) {
        super(NDocNodeType.CTRL_CALL,source);
    }



    public CtrlNDocNodeCall setCallName(String callName) {
        this.callName = callName;
        return this;
    }

    public String getCallName() {
        return callName;
    }

    public Map<String, NElement> getBodyVars() {
        return __bodyVars;
    }

    public List<NElement> getCallArgs() {
        return __args;
    }

    public NElement getCallExpr() {
        return __callExpr;
    }

    public List<NElement> getCallBody() {
        return __callBody;
    }

    @Override
    public NDocNode copy() {
        CtrlNDocNodeCall c = new CtrlNDocNodeCall(source());
        copyTo(c);
        return c;
    }

    @Override
    public NDocNode copyTo(NDocNode other) {
        super.copyTo(other);
        if (other instanceof CtrlNDocNodeCall) {
            CtrlNDocNodeCall oc = (CtrlNDocNodeCall) other;
            oc.callName = callName;
            oc.__args = new ArrayList<>(__args);
            oc.__callExpr = __callExpr;
            oc.__callBody = new ArrayList<>(__callBody);
            oc.__bodyVars = new HashMap<>(__bodyVars);
        }
        return this;
    }


    public CtrlNDocNodeCall setArgs(List<NElement> __args) {
        this.__args = __args;
        return this;
    }

    public CtrlNDocNodeCall setCallExpr(NElement __callExpr) {
        this.__callExpr = __callExpr;
        return this;
    }

    public CtrlNDocNodeCall setCallBody(List<NElement> __callBody) {
        this.__callBody = __callBody;
        return this;
    }

    public CtrlNDocNodeCall setBodyVars(Map<String, NElement> __bodyVars) {
        this.__bodyVars = __bodyVars;
        return this;
    }

    @Override
    public String toString() {
        try {
            List<NElement> a = new ArrayList<>();
            a.addAll(__args);
            a.addAll(__bodyVars.keySet().stream().map(x -> NElement.ofName(x)).collect(Collectors.toList()));
            List<String> li = a.stream().map(x -> NDocUtils.snippet(x)).collect(Collectors.toList());
            return "call_" + callName + li;
        }catch (Exception ex) {
            ex.printStackTrace();
            return super.toString();
        }
    }
}
