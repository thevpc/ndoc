/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.elem2d.image;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engin.spibase.parser.HNodeParserBase;
import net.thevpc.halfa.engin.spibase.format.ToTsonHelper;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;


/**
 * @author vpc
 */
public class HImageImpl extends HNodeParserBase {

    public HImageImpl() {
        super(false, HNodeType.IMAGE);
    }

    @Override
    protected boolean processArg(String id, HNode node, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case STRING: {
                node.setProperty(HProp.ofString(HPropName.VALUE, e.toStr().raw()));
                return true;
            }
            case PAIR: {
                NOptional<ObjEx.SimplePair> sp = ObjEx.of(e).asSimplePair();
                if (sp.isPresent()) {
                    ObjEx.SimplePair spp = sp.get();
                    ObjEx v = spp.getValue();
                    switch (spp.getNameId()) {
                        case HPropName.VALUE:
                        case HPropName.FILE:
                        case HPropName.TRANSPARENT_COLOR:
                        {
                            node.setProperty(spp.getNameId(), v.raw());
                            return true;
                        }
                        case "content":
                        case "src":
                        {
                            node.setProperty(HPropName.VALUE, v.raw());
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.processArg(id, node, e, f, context);
    }


    @Override
    public TsonElement toTson(HNode item) {
        return ToTsonHelper
                .of(item, engine())
                .inlineStringProp(HPropName.VALUE)
                .build();
    }
}
