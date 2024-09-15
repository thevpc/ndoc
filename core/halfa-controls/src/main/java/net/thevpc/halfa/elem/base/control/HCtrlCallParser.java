package net.thevpc.halfa.elem.base.control;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HItemList;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.base.model.DefaultHNode;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.*;

public class HCtrlCallParser extends HNodeParserBase {

    public HCtrlCallParser() {
        super(false, HNodeType.CALL);
    }

    @Override
    public NCallableSupport<HItem> parseNode(HNodeFactoryParseContext context) {
        TsonElement c = context.element();
        HEngine engine = context.engine();
        HDocumentFactory f = engine.documentFactory();
        switch (c.type()) {
            case FUNCTION: {
                TsonFunction p = c.toFunction();
                return NCallableSupport.of(10, () -> {
                    TsonElementList args = p.args();
                    HNode node = new DefaultHNode(HNodeType.CALL);
                    for (TsonElement arg : args) {
                        if(arg.isSimplePair()){
                            TsonPair pair = arg.toPair();
                            switch (HUtils.uid(pair.key().stringValue())){
                                case HPropName.NAME:{
                                    node.setProperty(HPropName.NAME, pair.key());
                                    break;
                                }
                                case HPropName.VALUE:{
                                    node.setProperty(HPropName.VALUE, pair.value());
                                    break;
                                }
                            }
                        }
                    }
                    return node;
                });
            }
        }
        return NCallableSupport.invalid(s -> NMsg.ofC("[%s] unable to resolve node : %s", HUtils.shortName(context.source()), c));
    }


}
