/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.spi.eval;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.nuts.elem.NArrayElement;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NNamedElement;
import net.thevpc.nuts.elem.NUpletElement;
import net.thevpc.nuts.util.NOptional;

/**
 * @author vpc
 */
public class NDocNodeEvalNDoc implements NDocObjectEvalContext {

    private HNode node;
    private List<NDocNodeEvalFunctionFactory> functions = new ArrayList<>();

    public NDocNodeEvalNDoc(HNode node) {
        this.node = node;
    }

    public void addFunctions(NDocNodeEvalFunctionFactory f) {
        if (f != null) {
            functions.add(f);
        }
    }

    public NElement evalVar(String varName) {
        HNode nn = (node);
//        HNode stop = null;
        while (nn != null) {
            NOptional<NElement> var = nn.getVar(varName);
            if (var.isPresent()) {
                return var.get();
            }
//            for (HNode cc : nn.children()) {
//                if (cc == stop) {
//                    break;
//                }
//                if (HNodeType.ASSIGN.equals(cc.type())) {
//                    String oName = cc.getName();
//                    if (Objects.equals(oName, varName)) {
//                        Object pp = cc.getPropertyValue(HPropName.VALUE).orNull();
//                        return eval(TsonUtils.toTson(pp));
//                    }
//                }
//            }
//            stop = nn;
            nn = nn.parent();
        }
        return null;
    }

    public NOptional<NElement> evalFunction(String name, NElement[] args) {
        for (NDocNodeEvalFunctionFactory function : functions) {
            NOptional<NElement> a = function.evalFunction(name, args);
            if (a.isPresent()) {
                return a;
            }
        }
        return NOptional.ofNamedEmpty("function " + name);
    }

    public NElement evalArray(NElement element, NElement[] indices) {
        if (element == null) {
            return null;
        }
        if (indices.length == 0) {
            return element;
        }
        NElement u = eval(indices[0]);
        NOptional<Integer> i = NDocObjEx.of(u).asInt();
        if (i.isPresent()) {
            int ii = i.get();
            NOptional<Object[]> asObjectArray = NDocObjEx.of(element).asObjectArray();
            if (asObjectArray.isPresent()) {
                Object[] obj = asObjectArray.get();
                int len = obj.length;
                if (ii == 0) {
                    ii = 1;
                }
                if (ii < 0) {
                    ii = len + len;
                }
                if (ii - 1 >= 0 && ii - 1 < len) {
                    return (NElement) obj[ii - 1];
                }
            }
        }
        return null;
    }

    @Override
    public NElement eval(NElement element) {
        if (element == null) {
            return null;
        }
        if (element instanceof NElement) {
            NElement ee = ((NElement) element);
            switch (ee.type()) {
                case NAME: {
                    String u = ee.asStringValue().get();
                    if (u.startsWith("$")) {
                        String varName = u.substring(1);
                        return evalVar(varName);
                    }
                    break;
                }
                case NAMED_UPLET: {
                    NUpletElement ff = ((NUpletElement) element);
                    if (ff.isNamed()) {
                        List<NElement> r = ff.params()
                                .stream().map(x -> eval(x)).collect(Collectors.toList());
                        NOptional<NElement> oo = evalFunction(ff.name().get(), r.toArray(new NElement[0]));
                        if (!oo.isEmpty()) {
                            return oo.get();
                        }
                        return element;//NElements.of().ofUplet(ff.name(), r.toArray(new Object[0])).build();
                    }
                    return element;
                }

                case ARRAY:
                case NAMED_PARAMETRIZED_ARRAY:
                case PARAMETRIZED_ARRAY:
                case NAMED_ARRAY: {
                    NArrayElement r = ee.asArray().get();
                    String u = r.name().orNull();
                    if (u != null && u.startsWith("$")) {
                        String varName = u.substring(1);
                        NElement arrVal = evalVar(varName);
                        return evalArray(arrVal, r.children().toArray(new NElement[0]));
                    }
                    break;
                }
            }
        }
        return element;
    }

    public static boolean asBoolean(NElement e) {
        switch (e.type()) {
            case BOOLEAN:
                return e.asBooleanValue().get();
            default: {
                if (e.isNumber()) {
                    return e.asDoubleValue().get().doubleValue() != 0.0;
                }
                return !e.isNull();
            }
        }
    }
}
