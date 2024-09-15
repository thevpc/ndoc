/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.elem.base.text.text;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.base.format.ToTsonHelper;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vpc
 */
public class HTextParser extends HNodeParserBase {

    public HTextParser() {
        super(false, HNodeType.TEXT);
    }

    @Override
    public NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        switch (tsonElement.type()) {
            case STRING: {
                return NOptional.of(
                        context.documentFactory().ofText(tsonElement.toStr().raw())
                );
            }
        }
        return super.parseItem(id, tsonElement, context);
    }

    @Override
    protected String acceptTypeName(TsonElement e) {
        switch (e.type()) {
            case STRING: {
                return id();
            }
        }
        return super.acceptTypeName(e);
    }

    @Override
    protected boolean processArguments(String id, TsonElement tsonElement, HNode node, TsonElement[] arguments, HDocumentFactory f, HNodeFactoryParseContext context) {
        TsonElement lang=null;
        TsonElement value=null;
        List<TsonElement> others=new ArrayList<>();
        for (TsonElement currentArg : arguments) {
            switch (currentArg.type()) {
                case STRING: {
                    others.add(currentArg);
                    break;
                }
                case NAME: {
                    others.add(currentArg);
                    break;
                }
                case PAIR: {
                    NOptional<ObjEx.SimplePair> sp = ObjEx.of(currentArg).asSimplePair();
                    if (sp.isPresent()) {
                        ObjEx.SimplePair spp = sp.get();
                        ObjEx v = spp.getValue();
                        switch (spp.getNameId()) {
                            case HPropName.VALUE:
                            case "content": {
                                value=v.asTson().get();
                                break;
                            }
                            case HPropName.FILE: {
                                NPath nPath = context.resolvePath(v.asStringOrName().get().trim());
                                context.document().resources().add(nPath);
                                try {
                                    value= Tson.of(nPath.readString().trim());
                                } catch (Exception ex) {
                                    context.messages().addError(NMsg.ofC("unable to load source file %s as %s", v.asStringOrName().get().trim(), nPath));
                                }
                                break;
                            }
                            case HPropName.LANG: {
                                lang= v.asTson().get();
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        while(!others.isEmpty()) {
            if (lang == null && value == null) {
                if (others.size() == 1) {
                    node.setProperty(HPropName.VALUE, others.get(0));
                    others.remove(0);
                } else {
                    String v = others.get(0).toStr().stringValue();
                    if (v.matches("[a-zA-Z0-9+._-]+")) {
                        node.setProperty(HPropName.LANG, others.get(0));
                        others.remove(0);
                    } else {
                        node.setProperty(HPropName.VALUE, others.get(0));
                        others.remove(0);
                        others.clear();
                    }
                }
            }else if (value == null) {
                node.setProperty(HPropName.VALUE, others.get(0));
                others.remove(0);
            }else if (lang == null) {
                String v = others.get(0).toStr().stringValue();
                if (v.matches("[a-zA-Z0-9+._-]+")) {
                    node.setProperty(HPropName.LANG, others.get(0));
                }
                others.remove(0);
            }else{
                others.clear();
            }
        }
        return super.processArguments(id, tsonElement, node, arguments, f, context);
    }

    @Override
    protected boolean processArgument(String id, TsonElement tsonElement, HNode node, TsonElement currentArg, TsonElement[] allArguments, int currentArgIndex, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (currentArg.type()) {
            case STRING: {
                //already processed
                return true;
            }
            case NAME: {
                //already processed
                return true;
            }
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(currentArg).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case HPropName.VALUE:
                        case "content":
                        case HPropName.FILE:
                        case HPropName.LANG: {
                            //already processed
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.processArgument(id, tsonElement, node, currentArg, allArguments, currentArgIndex, f, context);
    }

    @Override
    public TsonElement toTson(HNode item) {
        return ToTsonHelper
                .of(item, engine())
                .inlineStringProp(HPropName.VALUE)
                .build();
    }
}
