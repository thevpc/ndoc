package net.thevpc.halfa.engine.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.engine.parser.styles.HStyleParser;
import net.thevpc.halfa.spi.util.HParseHelper;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.nodes.HNodeTypeFactory;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonElementHeader;

public abstract class AbstractHNodeTypeFactory implements HNodeTypeFactory {

    private String id;
    private String[] aliases;
    private boolean container;
    private HEngine engine;

    public AbstractHNodeTypeFactory(boolean container, String id, String... aliases) {
        this.id = id;
        this.aliases = aliases;
        this.container = container;
    }

    public HEngine engine() {
        return engine;
    }

    public void init(HEngine engine){
        this.engine=engine;
    }

    public boolean isContainer() {
        return container;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String[] aliases() {
        return aliases;
    }


    protected void processImplicitStyles(String id, HNode p, HDocumentFactory f, HNodeFactoryParseContext context) {

    }
    protected boolean processArg(String id, HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        return false;
    }

    protected boolean processChild(HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        return false;
    }

    protected String acceptTypeName(TsonElement e) {
        switch (e.type()) {
            case FUNCTION: {
                if (acceptTypeName(e.toFunction().getName())) {
                    return e.toFunction().getName();
                }
                break;
            }
            case OBJECT: {
                TsonElementHeader h = e.toObject().getHeader();
                if (h != null) {
                    if (acceptTypeName(h.getName())) {
                        return h.getName();
                    }
                }
                break;
            }
            case ARRAY: {
                TsonElementHeader h = e.toArray().getHeader();
                if (h != null) {
                    if (acceptTypeName(h.getName())) {
                        return h.getName();
                    }
                }
                break;
            }
        }
        return null;
    }

    private boolean acceptTypeName(String id) {
        String oid = HUtils.uid(id);
        if (id.equals(oid)) {
            return true;
        }
        for (String s : aliases()) {
            if (s.equals(oid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public NCallableSupport<HItem> parseNode(HNodeFactoryParseContext context) {
        TsonElement e = context.element();
        String s = acceptTypeName(e);
        if (s != null) {
            return NCallableSupport.of(10,
                    () -> {
                        NOptional<HItem> o = parseItem(s, e, context);
                        if (!o.isPresent()) {
                            return parseItem(s, e, context).get();
                        }
                        return o.get();
                    }
            );
        }
        return NCallableSupport.invalid(ss -> NMsg.ofC("invalid %s : %S", id(), e));
    }

    public NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        HEngine engine = context.engine();
        HDocumentFactory f = context.documentFactory();
        HNode p = context.documentFactory().of(id);
        HNodeFactoryParseContext context2 = context.push(p);
        switch (tsonElement.type()) {
            case FUNCTION:
            case OBJECT:
            case ARRAY: {
                ObjEx ee = new ObjEx(tsonElement);
                if (!HParseHelper.fillAnnotations(tsonElement, p)) {
                    return NOptional.ofNamedError(NMsg.ofC("[%s] page can have only a single parent template", context2.source()));
                }
                processImplicitStyles(id, p, f, context2);
                for (TsonElement e : ee.args()) {
                    NOptional<HProp[]> u = HStyleParser.parseStyle(e, f, context2);
                    if (u.isPresent()) {
                        for (HProp s : u.get()) {
                            p.append(s);
                        }
                    } else {
                        if (!processArg(id, p, e, f, context2)) {
                            return NOptional.ofNamedError(NMsg.ofC("[%s] invalid %s", context2.source(), e));
                        }
                    }
                }
                for (TsonElement e : ee.children()) {
                    if (p instanceof HContainer) {
                        NOptional<HItem> u = engine.newNode(e, context2);
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

    @Override
    public TsonElement toTson(HNode item) {
        return ToTsonHelper.of((HNode) item,engine)
                .build();
    }


    @Override
    public HNode newNode() {
        if (isContainer()) {
            return new DefaultHContainer(id());
        } else {
            return new DefaultHNode(id());
        }
    }

}
