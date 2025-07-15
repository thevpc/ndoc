package net.thevpc.ndoc.engine.control;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NObjectElementBuilder;
import net.thevpc.nuts.elem.NUpletElementBuilder;
import net.thevpc.nuts.io.NPath;

import java.util.ArrayList;
import java.util.List;

public class CtrlHNodeCall extends CtrlHNodeBase {
    private String __name;
    private List<NElement> __args = new ArrayList<>();
    private NElement __callExpr;
    private List<NElement> __callBody = new ArrayList<>();

    private CtrlHNodeCall() {
        super(HNodeType.CALL);
    }

    public CtrlHNodeCall(NElement c, HResource source) {
        super(HNodeType.CALL);
        __name = HUtils.uid(c.asNamed().get().name().get());
        this.setProperty(HPropName.NAME, NElement.ofString(HUtils.uid(__name)));

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
                __callBody.addAll(fb.children());
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
                __callBody.addAll(fb.children());
            } else {
                throw new IllegalArgumentException("unexpected call : " + c);
            }

        }
        __callExpr = c;
        this.setProperty(HPropName.VALUE, c);
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
    public HNode copy() {
        CtrlHNodeCall c = new CtrlHNodeCall();
        copyTo(c);
        return this;
    }

    @Override
    public void copyTo(HNode other) {
        super.copyTo(other);
        if (other instanceof CtrlHNodeCall) {
            CtrlHNodeCall oc = (CtrlHNodeCall) other;
            oc.__name = __name;
            oc.__args = new ArrayList<>(__args);
            oc.__callExpr = __callExpr;
            oc.__callBody = new ArrayList<>(__callBody);
        }
    }
}
