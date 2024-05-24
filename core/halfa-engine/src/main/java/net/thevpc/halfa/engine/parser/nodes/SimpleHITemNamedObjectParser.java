package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.engine.parser.styles.HStyleParser;
import net.thevpc.halfa.engine.parser.util.HParseHelper;
import net.thevpc.halfa.engine.parser.util.TsonElementExt;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public abstract class SimpleHITemNamedObjectParser extends AbstractHITemNamedObjectParser {
    public SimpleHITemNamedObjectParser(String... ids) {
        super(ids);
    }

    protected abstract HNode node(HDocumentFactory f);

    protected boolean processArg(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        return false;
    }

    protected boolean processChild(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        return false;
    }

    public NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        HDocumentFactory f = context.documentFactory();
        HNode p = node(f);
        switch (tsonElement.type()) {
            case FUNCTION:
            case OBJECT:
            case ARRAY: {
                TsonElementExt ee = new TsonElementExt(tsonElement);
                if (!HParseHelper.fillAnnotations(tsonElement, p)) {
                    return NOptional.ofNamedError(NMsg.ofC("[%s] page can have only a single parent template", context.source()));
                }
                for (TsonElement e : ee.args()) {
                    NOptional<HStyle[]> u = HStyleParser.parseStyle(e, f, context);
                    if (u.isPresent()) {
                        for (HStyle s : u.get()) {
                            p.append(s);
                        }
                    } else {
                        if (!processArg(p, e, f, context)) {
                            return NOptional.ofNamedError(NMsg.ofC("[%s] invalid %s", context.source(), e));
                        }
                    }
                }
                for (TsonElement e : ee.children()) {
                    if (p instanceof HContainer) {
                        NOptional<HItem> u = null;
                        if (p.type() == HNodeType.PAGE_GROUP) {
                            u = context.engine().newDocumentChild(e, p, context);
                        } else {
                            u = context.engine().newPageChild(e, p, context);
                        }
                        if (u.isPresent()) {
                            p.append(u.get());
                        } else {
                            NOptional<HItem> finalU = u;
                            return NOptional.ofError(
                                    s -> NMsg.ofC("[%s] Error parsing child : %s : %s", context.source(), e, finalU.getMessage().apply(s))
                            );
                        }
                    } else {
                        if (!processChild(p, e, f, context)) {
                            return NOptional.ofNamedError(NMsg.ofC("[%s] invalid %s", context.source(), e));
                        }
                    }
                }
            }
        }
        return NOptional.of(p);
    }
}
