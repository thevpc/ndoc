/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.nodes.text;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.*;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.engine.nodes.AbstractHNodeTypeFactory;
import net.thevpc.halfa.engine.nodes.ToTsonHelper;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonStringLayout;

/**
 * @author vpc
 */
public class HTextImpl extends AbstractHNodeTypeFactory {
    public HTextImpl() {
        super(false, HNodeType.TEXT);
    }
    public NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        switch (tsonElement.type()){
            case STRING:{
                return NOptional.of(
                        context.documentFactory().ofText(tsonElement.toStr().raw())
                );
            }
        }
        return super.parseItem(id, tsonElement, context);
    }

    protected String acceptTypeName(TsonElement e) {
        switch (e.type()){
            case STRING:{
                return id();
            }
        }
        return super.acceptTypeName(e);
    }

    @Override
    protected boolean processArg(String id, HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (e.type()) {
            case STRING: {
                p.setProperty(HProp.ofString(HPropName.VALUE, e.toStr().raw()));
                return true;
            }
        }
        return false;
    }

    @Override
    public TsonElement toTson(HNode item) {
        return ToTsonHelper
                .of(item,engine())
                .inlineStringProp(HPropName.VALUE)
                .build();
    }
}
