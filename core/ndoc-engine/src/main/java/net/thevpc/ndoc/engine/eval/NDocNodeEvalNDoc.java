/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.eval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.model.fct.NDocFunction;
import net.thevpc.ndoc.api.model.fct.NDocFunctionArg;
import net.thevpc.ndoc.api.model.fct.NDocFunctionContext;
import net.thevpc.ndoc.api.model.node.NDocItem;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.ndoc.spi.eval.NDocObjectEvalContext;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.reflect.NReflectUtils;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

/**
 * @author vpc
 */
public class NDocNodeEvalNDoc implements NDocObjectEvalContext {

    //    private NDocNode node;
    private NDocEngine engine;

    public NDocNodeEvalNDoc(NDocEngine engine) {
        this.engine = engine;
    }

    public NElement evalVar(NDocNode node, String varName) {
        NDocItem nn = (node);
//        NDocNode stop = null;
        while (nn != null) {
            if (nn instanceof NDocNode) {
                NDocNode nd = (NDocNode) nn;
                NOptional<NElement> var = nd.getVar(varName);
                if (var.isPresent()) {
                    return var.get();
                }
            }
            nn = nn.parent();
        }
        return null;
    }

    public NElement evalArray(NDocNode node, NElement element, NElement[] indices) {
        if (element == null) {
            return NElement.ofNull();
        }
        if (indices.length == 0) {
            return element;
        }
        NElement u = eval(node, indices[0]);
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
    public NElement eval(NDocNode node, NElement element) {
        if (element == null) {
            return NElement.ofNull();
        }
        if (element instanceof NElement) {
            NElement ee = ((NElement) element);
            switch (ee.type()) {
                case ANTI_QUOTED_STRING:
                case SINGLE_QUOTED_STRING:
                case DOUBLE_QUOTED_STRING:
                case TRIPLE_ANTI_QUOTED_STRING:
                case TRIPLE_SINGLE_QUOTED_STRING:
                case TRIPLE_DOUBLE_QUOTED_STRING:
                case LINE_STRING:
                {
                    String u = ee.asStringValue().get();
                    if(u.indexOf("$")>=0){
                        NPrimitiveElementBuilder b = ee.asPrimitive().get().builder();
                        b.setString(NMsg.ofV(u, new Function<String, Object>() {
                            @Override
                            public Object apply(String s) {
                                return evalVar(node, s);
                            }
                        }).toString());
                        return b.build();
                    }

                }
                case NAME: {
                    String u = ee.asStringValue().get();
                    if (u.startsWith("$")) {
                        String varName = u.substring(1);
                        return evalVar(node, varName);
                    }
                    break;
                }
                case NAMED_UPLET: {
                    NUpletElement ff = ((NUpletElement) element);
                    if (ff.isNamed()) {
                        NDocFunctionArg[] args = ff.params()
                                .stream().map(x -> engine.createRawArg(node, x)).toArray(NDocFunctionArg[]::new);
                        NOptional<NDocFunction> f = engine.findFunction(node, ff.name().get(), args);
                        if (f.isPresent()) {
                            return eval(node, f.get().invoke(args, new NDocFunctionContext() {
                            }));
                        }
                        List<NElement> r = ff.params()
                                .stream().map(x -> eval(node, x)).collect(Collectors.toList());
                        return ff.builder().setParams(r).build();
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
                        NElement arrVal = evalVar(node, varName);
                        return evalArray(node, arrVal, r.children().toArray(new NElement[0]));
                    } else if (u == null) {
                        // this is an implicit array
                        List<NElement> children = r.children();
                        if (children.size() == 0) {
                            return NElement.ofArray();
                        } else {
                            List<NElement> newChildren = new ArrayList<>();
                            for (NElement c : children) {
                                NElement[] zz = interpretAsArrayItems_interval(node, c);
                                if (zz != null) {
                                    newChildren.addAll(Arrays.asList(zz));
                                } else {
                                    newChildren.add(eval(node, c));
                                }
                            }
                            return NElement.ofArray(newChildren.toArray(new NElement[0]));
                        }
                    }
                    break;
                }
            }
        }
        return element;
    }

    private NElement[] interpretAsArrayItems_interval(NDocNode node, NElement c) {
        if (c.type() == NElementType.OP_MINUS_GT && c.isBinaryOperator()) {
            NOperatorElement g = c.asOperator().get();
            NElement f = eval(node, g.first().get());
            NElement s = eval(node, g.second().get());
            if (f.isNumber() && s.isNumber()) {
                NElementType ct = NElements.of().commonNumberType(f.type(), s.type());
                if (ct.isAnyNumber()) {
                    Number fn = f.asNumberType(ct).get().asNumberValue().get();
                    Number sn = s.asNumberType(ct).get().asNumberValue().get();
                    int u = NReflectUtils.compareNumbers(fn, sn);
                    List<NElement> all = new ArrayList<>();
                    if (u == 0) {
                        all.add(NElement.ofNumber(fn));
                    } else if (u < 0) {
                        Number i = fn;
                        while (NReflectUtils.compareNumbers(i, sn) <= 0) {
                            all.add(NElement.ofNumber(i));
                            i = NReflectUtils.addNumbers(i, (byte) 1);
                        }
                    } else if (u > 0) {
                        Number i = fn;
                        while (NReflectUtils.compareNumbers(i, sn) >= 0) {
                            all.add(NElement.ofNumber(i));
                            i = NReflectUtils.addNumbers(i, (byte) -1);
                        }
                    }
                    return all.toArray(new NElement[0]);
                }
            }
        }
        return null;
    }


}
