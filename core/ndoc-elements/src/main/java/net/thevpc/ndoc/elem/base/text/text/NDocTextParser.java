/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.text.text;

import net.thevpc.ndoc.api.document.HMsg;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vpc
 */
public class NDocTextParser extends NDocNodeParserBase {

    public NDocTextParser() {
        super(false, HNodeType.TEXT);
    }

    @Override
    public NOptional<HItem> parseItem(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
        switch (tsonElement.type()) {
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
            {
                return NOptional.of(
                        context.documentFactory().ofText(tsonElement.toStr().raw())
                );
            }
        }
        return super.parseItem(id, tsonElement, context);
    }

    @Override
    protected String acceptTypeName(NElement e) {
        switch (e.type()) {
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
            {
                return id();
            }
        }
        return super.acceptTypeName(e);
    }

    @Override
    protected boolean processArguments(ParseArgumentInfo info) {
        NElement lang=null;
        NElement value=null;
        List<NElement> others=new ArrayList<>();
        for (NElement currentArg : info.arguments) {
            switch (currentArg.type()) {
                case DOUBLE_QUOTED_STRING:
                case SINGLE_QUOTED_STRING:
                case ANTI_QUOTED_STRING:
                case TRIPLE_DOUBLE_QUOTED_STRING:
                case TRIPLE_SINGLE_QUOTED_STRING:
                case TRIPLE_ANTI_QUOTED_STRING:
                case LINE_STRING:
                {
                    others.add(currentArg);
                    break;
                }
                case NAME: {
                    others.add(currentArg);
                    break;
                }
                case PAIR: {
                    NOptional<NDocObjEx.SimplePair> sp = NDocObjEx.of(currentArg).asSimplePair();
                    if (sp.isPresent()) {
                        NDocObjEx.SimplePair spp = sp.get();
                        NDocObjEx v = spp.getValue();
                        switch (spp.getNameId()) {
                            case HPropName.VALUE:
                            case "content": {
                                value=v.asTson().get();
                                break;
                            }
                            case HPropName.FILE: {
                                NPath nPath = info.context.resolvePath(v.asStringOrName().get().trim());
                                info.context.document().resources().add(nPath);
                                try {
                                    value= NElements.of().of(nPath.readString().trim());
                                } catch (Exception ex) {
                                    info.context.messages().log(
                                           HMsg.of(NMsg.ofC("unable to load source file %s as %s", v.asStringOrName().get().trim(), nPath).asSevere()));
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
                    info.node.setProperty(HPropName.VALUE, others.get(0));
                    others.remove(0);
                } else {
                    String v = others.get(0).toStr().stringValue();
                    if (v.matches("[a-zA-Z0-9+._-]+")) {
                        info.node.setProperty(HPropName.LANG, others.get(0));
                        others.remove(0);
                    } else {
                        info.node.setProperty(HPropName.VALUE, others.get(0));
                        others.remove(0);
                        others.clear();
                    }
                }
            }else if (value == null) {
                info.node.setProperty(HPropName.VALUE, others.get(0));
                others.remove(0);
            }else if (lang == null) {
                String v = others.get(0).toStr().stringValue();
                if (v.matches("[a-zA-Z0-9+._-]+")) {
                    info.node.setProperty(HPropName.LANG, others.get(0));
                }
                others.remove(0);
            }else{
                others.clear();
            }
        }
        return super.processArguments(info);
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
            {
                //already processed
                return true;
            }
            case NAME: {
                //already processed
                return true;
            }
            case PAIR: {
                NOptional<NDocObjEx.SimplePair> sp = NDocObjEx.of(info.currentArg).asSimplePair();
                if (sp.isPresent()) {
                    NDocObjEx.SimplePair spp = sp.get();
                    NDocObjEx v = spp.getValue();
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
        return super.processArgument(info);
    }

    @Override
    public NElement toElem(HNode item) {
        return ToElementHelper
                .of(item, engine())
                .inlineStringProp(HPropName.VALUE)
                .build();
    }
}
