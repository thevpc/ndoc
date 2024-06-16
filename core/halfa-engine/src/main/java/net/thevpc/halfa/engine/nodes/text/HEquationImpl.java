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
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public class HEquationImpl extends AbstractHNodeTypeFactory {

    public HEquationImpl() {
        super(false, HNodeType.EQUATION,"eq");
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
