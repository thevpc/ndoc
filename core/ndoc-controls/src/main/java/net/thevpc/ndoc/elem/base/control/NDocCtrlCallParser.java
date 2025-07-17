package net.thevpc.ndoc.elem.base.control;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.style.NDocPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.model.DefaultNDocNode;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.elem.NUpletElement;
import net.thevpc.nuts.util.NMsg;

import java.util.List;

public class NDocCtrlCallParser extends NDocNodeParserBase {

    public NDocCtrlCallParser() {
        super(false, NDocNodeType.CALL);
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
                NUpletElement p = c.asUplet().get();
                if(p.isNamed()) {
                    return NCallableSupport.of(10, () -> {
                        List<NElement> args = p.params();
                        NDocNode node = new DefaultNDocNode(NDocNodeType.CALL);
                        for (NElement arg : args) {
                            if (arg.isSimplePair()) {
                                NPairElement pair = arg.asPair().get();
                                switch (HUtils.uid(pair.key().asStringValue().get())) {
                                    case NDocPropName.NAME: {
                                        node.setProperty(NDocPropName.NAME, pair.key());
                                        break;
                                    }
                                    case NDocPropName.VALUE: {
                                        node.setProperty(NDocPropName.VALUE, pair.value());
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
