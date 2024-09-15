/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.elem.base.text.plain;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.base.format.ToTsonHelper;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.tson.TsonElement;

/**
 * @author vpc
 */
public class HPlainTextParser extends HNodeParserBase {

    public HPlainTextParser() {
        super(false, HNodeType.PLAIN);
    }

    protected String acceptTypeName(TsonElement e) {
        switch (e.type()) {
            case STRING: {
                return id();
            }
        }
        return super.acceptTypeName(e);
    }

    @Override
    protected boolean processArgument(String id, TsonElement tsonElement, HNode node, TsonElement currentArg, TsonElement[] allArguments, int currentArgIndex, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (currentArg.type()) {
            case STRING: {
                node.setProperty(HPropName.VALUE, currentArg);
                return true;
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
