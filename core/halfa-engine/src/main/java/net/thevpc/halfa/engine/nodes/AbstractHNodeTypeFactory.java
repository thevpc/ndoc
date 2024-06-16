package net.thevpc.halfa.engine.nodes;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
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
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonElementBase;
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

    public void init(HEngine engine) {
        this.engine = engine;
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

    protected boolean isAcceptableArgKeyPair(String s) {
        return false;
    }

    protected boolean processArg(String id, HNode p, TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context) {
        return false;
    }

    protected boolean isAncestorScene3D(HNode p) {
        while (p != null) {
            if (p.type() == HNodeType.SCENE3D) {
                return true;
            }
            p = p.parent();
        }
        return false;
    }

    protected String acceptTypeName(TsonElement e) {
        switch (e.type()) {
            case NAME: {
                if (acceptTypeName(e.toName().getName())) {
                    return e.toName().getName();
                }
                break;
            }
            case FUNCTION: {
                if (acceptTypeName(e.toFunction().name())) {
                    return e.toFunction().name();
                }
                break;
            }
            case OBJECT: {
                TsonElementHeader h = e.toObject().getHeader();
                if (h != null) {
                    if (acceptTypeName(h.name())) {
                        return h.name();
                    }
                }
                break;
            }
            case ARRAY: {
                TsonElementHeader h = e.toArray().header();
                if (h != null) {
                    if (acceptTypeName(h.name())) {
                        return h.name();
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
        //context.messages().addError(NMsg.ofC("invalid %s : %s", id(), e), context.source());
        return NCallableSupport.invalid(ss -> NMsg.ofC("invalid %s : %s", id(), e));
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
                ObjEx ee = ObjEx.of(tsonElement);
                HParseHelper.fillAnnotations(tsonElement, p);
                processImplicitStyles(id, p, f, context2);
                for (TsonElement e : ee.args()) {
                    if (!processArg(id, p, e, f, context2)) {
                        NOptional<HProp[]> u = HStyleParser.parseStyle(e, f, context2);
                        if (u.isPresent()) {
                            for (HProp s : u.get()) {
                                p.append(s);
                            }
                        } else {
                            ObjEx es = ObjEx.of(e);
                            if (es.isFunction()) {
                                if (isAcceptableArgKeyPair(es.name())
                                        || HStyleParser.acceptStyleName(es.name())) {
                                    context2.messages().addError(NMsg.ofC("[%s] invalid argument %s. did you mean %s:%s ?",
                                            context2.source(),
                                            e,
                                            es.name(), Tson.ofUplet(es.args().toArray(new TsonElementBase[0]))
                                    ), context2.source());
                                    return NOptional.ofNamedError(NMsg.ofC("[%s] invalid argument %s. did you mean %s:%s ?",
                                            context2.source(),
                                            e,
                                            es.name(), Tson.ofUplet(es.args().toArray(new TsonElementBase[0]))
                                    ));
                                }
                            }
                            context2.messages().addError(NMsg.ofC("[%s] invalid argument %s in : %s", context2.source(), e, tsonElement), context2.source());
                            return NOptional.ofNamedError(NMsg.ofC("[%s] invalid argument %s in : %s", context2.source(), e, tsonElement));
                        }
                    }
                }
                for (TsonElement e : ee.body()) {
                    NOptional<HItem> u = engine.newNode(e, context2);
                    if (u.isPresent()) {
                        p.append(u.get());
                    } else {
                        NOptional<HItem> finalU = u;
                        context2.messages().addError(NMsg.ofC("[%s] Error parsing child : %s : %s", context2.source(), e, finalU.getMessage().apply(context2.session())), context2.source());
                        return NOptional.ofError(
                                s -> NMsg.ofC("[%s] Error parsing child : %s : %s", context2.source(), e, finalU.getMessage().apply(s))
                        );
                    }
                }
            }
        }
        return NOptional.of(p);
    }

    @Override
    public TsonElement toTson(HNode item) {
        return ToTsonHelper.of((HNode) item, engine)
                .build();
    }

    @Override
    public HNode newNode() {
        return new DefaultHNode(id());
    }

    @Override
    public boolean validateNode(HNode node) {
        return false;
    }
}
