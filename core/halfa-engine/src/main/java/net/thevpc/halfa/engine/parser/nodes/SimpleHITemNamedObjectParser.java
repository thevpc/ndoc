package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.engine.parser.styles.HStyleParser;
import net.thevpc.halfa.spi.util.HParseHelper;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

public abstract class SimpleHITemNamedObjectParser extends AbstractHITemNamedObjectParser {
    public SimpleHITemNamedObjectParser(String... ids) {
        super(ids);
    }

    @Override
    public boolean accept(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        return true;
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
        HNodeFactoryParseContext context2 = context.push(p);
        switch (tsonElement.type()) {
            case FUNCTION:
            case OBJECT:
            case ARRAY: {
                ObjEx ee = new ObjEx(tsonElement);
                if (!HParseHelper.fillAnnotations(tsonElement, p)) {
                    return NOptional.ofNamedError(NMsg.ofC("[%s] page can have only a single parent template", context2.source()));
                }
                for (TsonElement e : ee.args()) {
                    NOptional<HProp[]> u = HStyleParser.parseStyle(e, f, context2);
                    if (u.isPresent()) {
                        for (HProp s : u.get()) {
                            p.append(s);
                        }
                    } else {
                        if (!processArg(p, e, f, context2)) {
                            return NOptional.ofNamedError(NMsg.ofC("[%s] invalid %s", context2.source(), e));
                        }
                    }
                }
                for (TsonElement e : ee.children()) {
                    if (p instanceof HContainer) {
                        NOptional<HItem> u = context2.engine().newNode(e, context2);
                        if (u.isPresent()) {
                            p.append(u.get());
                        } else {
                            NOptional<HItem> finalU = u;
                            return NOptional.ofError(
                                    s -> NMsg.ofC("[%s] Error parsing child : %s : %s", context2.source(), e, finalU.getMessage().apply(s))
                            );
                        }
                    } else {
                        if (!processChild(p, e, f, context2)) {
                            return NOptional.ofNamedError(NMsg.ofC("[%s] invalid %s", context2.source(), e));
                        }
                    }
                }
            }
        }
        return NOptional.of(p);
    }
}
