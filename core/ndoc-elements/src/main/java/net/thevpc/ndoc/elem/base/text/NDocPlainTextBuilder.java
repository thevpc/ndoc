/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.text.plain;

import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.NDocArgumentParseInfo;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.util.ToElementHelper;
import net.thevpc.nuts.elem.NElement;

/**
 * @author vpc
 */
public class NDocPlainTextParser extends NDocNodeCustomBuilder {

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {

    }

    public NDocPlainTextParser() {
        super(false, NDocNodeType.PLAIN);
    }

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
    protected boolean processArgument(NDocArgumentParseInfo info) {
        switch (info.getCurrentArg().type()) {
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
            {
                info.getNode().setProperty(NDocPropName.VALUE, info.getCurrentArg());
                return true;
            }
        }
        return super.processArgument(info);
    }

    @Override
    public NElement toElem(NDocNode item) {
        return ToElementHelper
                .of(item, engine())
                .inlineStringProp(NDocPropName.VALUE)
                .build();
    }
}
