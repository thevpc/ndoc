/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.spi.eval;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.*;

/**
 * @author vpc
 */
public class HNodeEval implements ObjectEvalContext {

    private HNode node;
    private List<HNodeEvalFunctionFactory> functions = new ArrayList<>();

    public HNodeEval(HNode node) {
        this.node = node;
    }

    public void addFunctions(HNodeEvalFunctionFactory f) {
        if (f != null) {
            functions.add(f);
        }
    }

    public TsonElement evalVar(String varName) {
        HNode nn = (node);
//        HNode stop = null;
        while (nn != null) {
            NOptional<TsonElement> var = nn.getVar(varName);
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

    public NOptional<TsonElement> evalFunction(String name, TsonElement[] args) {
        for (HNodeEvalFunctionFactory function : functions) {
            NOptional<TsonElement> a = function.evalFunction(name, args);
            if (a.isPresent()) {
                return a;
            }
        }
        return NOptional.ofNamedEmpty("function " + name);
    }

    public TsonElement evalArray(TsonElement element, TsonElement[] indices) {
        if (element == null) {
            return null;
        }
        if (indices.length == 0) {
            return element;
        }
        TsonElement u = eval(indices[0]);
        NOptional<Integer> i = ObjEx.of(u).asInt();
        if (i.isPresent()) {
            int ii = i.get();
            NOptional<Object[]> asObjectArray = ObjEx.of(element).asObjectArray();
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
                    return (TsonElement) obj[ii - 1];
                }
            }
        }
        return null;
    }

    @Override
    public TsonElement eval(TsonElement element) {
        if (element == null) {
            return null;
        }
        if (element instanceof TsonElement) {
            TsonElement ee = ((TsonElement) element);
            switch (ee.type()) {
                case NAME: {
                    TsonName r = ee.toName();
                    String u = r.value();
                    if (u.startsWith("$")) {
                        String varName = u.substring(1);
                        return evalVar(varName);
                    }
                    break;
                }
                case UPLET: {
                    TsonUplet ff = ((TsonUplet) element);
                    if (ff.isNamed()) {
                        List<TsonElement> r = ff.args().toList()
                                .stream().map(x -> eval(x)).collect(Collectors.toList());
                        NOptional<TsonElement> oo = evalFunction(ff.name(), r.toArray(new TsonElement[0]));
                        if (!oo.isEmpty()) {
                            return oo.get();
                        }
                        return element;//Tson.ofFunction(ff.name(), r.toArray(new Object[0])).build();
                    }
                    return element;
                }

                case ARRAY: {
                    TsonArray r = ee.toArray();
                    String u = r.name();
                    if (u != null && u.startsWith("$")) {
                        String varName = u.substring(1);
                        TsonElement arrVal = evalVar(varName);
                        return evalArray(arrVal, r.body().toArray());
                    }
                    break;
                }
            }
        }
        return element;
    }

    public static boolean asBoolean(TsonElement e) {
        switch (e.type()) {
            case BOOLEAN:
                return e.toBoolean().value();
            default: {
                if (e.isNumber()) {
                    return e.toNumber().doubleValue() != 0;
                }
                return !e.isNull();
            }
        }
    }
}
