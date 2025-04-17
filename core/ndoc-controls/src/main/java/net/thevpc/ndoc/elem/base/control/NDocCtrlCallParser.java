package net.thevpc.ndoc.elem.base.control;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.model.DefaultHNode;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;

public class NDocCtrlCallParser extends NDocNodeParserBase {

    public NDocCtrlCallParser() {
        super(false, HNodeType.CALL);
    }

    @Override
    public NCallableSupport<HItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement c = context.element();
        NDocEngine engine = context.engine();
        NDocDocumentFactory f = engine.documentFactory();
        switch (c.type()) {
            case UPLET:
            case NAMED_UPLET:
            {
                NUpletElement p = c.toUplet();
                if(p.isNamed()) {
                    return NCallableSupport.of(10, () -> {
                        List<NElement> args = p.params();
                        HNode node = new DefaultHNode(HNodeType.CALL);
                        for (NElement arg : args) {
                            if (arg.isSimplePair()) {
                                NPairElement pair = arg.toPair();
                                switch (HUtils.uid(pair.key().stringValue())) {
                                    case HPropName.NAME: {
                                        node.setProperty(HPropName.NAME, pair.key());
                                        break;
                                    }
                                    case HPropName.VALUE: {
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
        }
        return NCallableSupport.invalid(() -> NMsg.ofC("[%s] unable to resolve node : %s", net.thevpc.ndoc.api.util.HUtils.shortName(context.source()), c));
    }


}
