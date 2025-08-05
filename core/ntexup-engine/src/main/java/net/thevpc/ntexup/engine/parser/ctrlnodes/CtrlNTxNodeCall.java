package net.thevpc.ntexup.engine.parser.ctrlnodes;

import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.source.NDocResource;
import net.thevpc.ntexup.api.util.NDocUtils;
import net.thevpc.nuts.elem.NElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CtrlNTxNodeCall extends CtrlNTxNodeBase {
    private List<NElement> __args = new ArrayList<>();
    private String callName;
    private NElement __callExpr;
    private List<NElement> __callBody = new ArrayList<>();
    private Map<String,NElement> __bodyVars = new HashMap<>();

    public CtrlNTxNodeCall(NDocResource source) {
        super(NTxNodeType.CTRL_CALL,source);
    }



    public CtrlNTxNodeCall setCallName(String callName) {
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
    public NTxNode copy() {
        CtrlNTxNodeCall c = new CtrlNTxNodeCall(source());
        copyTo(c);
        return c;
    }

    @Override
    public NTxNode copyTo(NTxNode other) {
        super.copyTo(other);
        if (other instanceof CtrlNTxNodeCall) {
            CtrlNTxNodeCall oc = (CtrlNTxNodeCall) other;
            oc.callName = callName;
            oc.__args = new ArrayList<>(__args);
            oc.__callExpr = __callExpr;
            oc.__callBody = new ArrayList<>(__callBody);
            oc.__bodyVars = new HashMap<>(__bodyVars);
        }
        return this;
    }


    public CtrlNTxNodeCall setArgs(List<NElement> __args) {
        this.__args = __args;
        return this;
    }

    public CtrlNTxNodeCall setCallExpr(NElement __callExpr) {
        this.__callExpr = __callExpr;
        return this;
    }

    public CtrlNTxNodeCall setCallBody(List<NElement> __callBody) {
        this.__callBody = __callBody;
        return this;
    }

    public CtrlNTxNodeCall setBodyVars(Map<String, NElement> __bodyVars) {
        this.__bodyVars = __bodyVars;
        return this;
    }

    @Override
    public String toString() {
        List<NElement> a = new ArrayList<>();
        a.addAll(__args);
        a.addAll(__bodyVars.keySet().stream().map(x -> NElement.ofName(x)).collect(Collectors.toList()));
        return "call::" + callName + "("+a.stream().map(x -> NDocUtils.snippet(x)).collect(Collectors.joining(","))+")";
    }
}
