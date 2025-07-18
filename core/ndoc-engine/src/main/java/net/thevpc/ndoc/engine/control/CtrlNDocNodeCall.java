package net.thevpc.ndoc.engine.control;

import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NObjectElementBuilder;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.elem.NUpletElementBuilder;
import net.thevpc.nuts.io.NPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CtrlNDocNodeCall extends CtrlNDocNodeBase {
    private String __name;
    private List<NElement> __args = new ArrayList<>();
    private NElement __callExpr;
    private List<NElement> __callBody = new ArrayList<>();
    private Map<String,NElement> __bodyVars = new HashMap<>();

    private CtrlNDocNodeCall() {
        super(NDocNodeType.CALL);
    }

    public CtrlNDocNodeCall(NElement c, NDocResource source) {
        super(NDocNodeType.CALL);
        __name = HUtils.uid(c.asNamed().get().name().get());
        this.setProperty(NDocPropName.NAME, NElement.ofString(HUtils.uid(__name)));

        //inline current file path in the TsonElements
        if (source != null && source.path().orNull() != null) {
            NPath sourcePath = source.path().orNull();
            c = HUtils.addCompilerDeclarationPath(c, sourcePath.toString());
            if (c.isNamedUplet()) {
                NUpletElementBuilder fb = (NUpletElementBuilder) c.builder();
                for (int i = 0; i < fb.params().size(); i++) {
                    NElement u = HUtils.addCompilerDeclarationPath(fb.get(i).orNull(), sourcePath.toString());
                    fb.set(i, u);
                    __args.add(u);
                }
                c = fb.build();
            } else if (c.isAnyObject()) {
                NObjectElementBuilder fb = (NObjectElementBuilder) c.builder();
                List<NElement> args = fb.params().orNull();
                if (args != null) {
                    for (int i = 0; i < args.size(); i++) {
                        NElement u = HUtils.addCompilerDeclarationPath(args.get(i), sourcePath.toString());
                        fb.setParamAt(i, u);
                        __args.add(u);
                    }
                }
                for (NElement child : fb.children()) {
                    if(child.isNamedPair() && child.asPair().get().key().isAnnotated("let")) {
                        NPairElement pe = child.asPair().get();
                        __bodyVars.put(pe.key().asStringValue().get(),pe.value());
                    }else{
                        __callBody.add(child);
                    }
                }
                c = fb.build();
            } else {
                throw new IllegalArgumentException("unexpected call : " + c);
            }
        } else {
            if (c.isNamedUplet()) {
                NUpletElementBuilder fb = (NUpletElementBuilder) c.builder();
                for (int i = 0; i < fb.params().size(); i++) {
                    __args.add(fb.get(i).orNull());
                }
            } else if (c.isAnyObject()) {
                NObjectElementBuilder fb = (NObjectElementBuilder) c.builder();
                List<NElement> args = fb.params().orNull();
                if (args != null) {
                    __args.addAll(args);
                }
                for (NElement child : fb.children()) {
                    if(child.isNamedPair() && child.asPair().get().key().isAnnotated("let")) {
                        NPairElement pe = child.asPair().get();
                        __bodyVars.put(pe.key().asStringValue().get(),pe.value());
                    }else{
                        __callBody.add(child);
                    }
                }
            } else {
                throw new IllegalArgumentException("unexpected call : " + c);
            }

        }
        __callExpr = c;
        this.setProperty(NDocPropName.VALUE, c);
    }

    public Map<String, NElement> getBodyVars() {
        return __bodyVars;
    }

    public String getCallName() {
        return __name;
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
        CtrlNDocNodeCall c = new CtrlNDocNodeCall();
        copyTo(c);
        return this;
    }

    @Override
    public NDocNode copyTo(NDocNode other) {
        super.copyTo(other);
        if (other instanceof CtrlNDocNodeCall) {
            CtrlNDocNodeCall oc = (CtrlNDocNodeCall) other;
            oc.__name = __name;
            oc.__args = new ArrayList<>(__args);
            oc.__callExpr = __callExpr;
            oc.__callBody = new ArrayList<>(__callBody);
            oc.__bodyVars = new HashMap<>(__bodyVars);
        }
        return this;
    }
}
