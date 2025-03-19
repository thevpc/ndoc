package net.thevpc.halfa.spi.base.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HMsg;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HItemList;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.base.model.DefaultHNode;
import net.thevpc.halfa.spi.base.format.ToTsonHelper;
import net.thevpc.halfa.spi.eval.HParseHelper;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.HNodeParser;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.*;

import java.util.HashMap;
import java.util.Map;

public abstract class HNodeParserBase implements HNodeParser {

    private String id;
    private String[] aliases;
    private boolean container;
    private HEngine engine;
    private String nodeId;

    public HNodeParserBase(boolean container, String id, String... aliases) {
        this.id = id;
        this.aliases = aliases;
        this.container = container;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
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

    protected void processImplicitStyles(ParseArgumentInfo info) {

    }

    protected boolean isAcceptableArgKeyPair(String s) {
        return false;
    }

    protected static class ParseArgumentInfo {
        public String id;
        public String uid;
        public TsonElement tsonElement;
        public HNode node;
        public TsonElement currentArg;
        public TsonElement[] arguments;
        public int currentArgIndex;
        public HDocumentFactory f;
        public HNodeFactoryParseContext context;
        public Map<String, Object> props = new HashMap<>();
    }

    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                if (info.currentArg.isSimplePair()) {
                    TsonPair p = info.currentArg.toPair();
                    String sid = net.thevpc.halfa.api.util.HUtils.uid(p.key().stringValue());
                    if (HParserUtils.isCommonStyleProperty(sid)) {
                        info.node.setProperty(sid, p.value());
                        return true;
                    }
                }
                break;
            }
            case NAME: {
                ObjEx h = ObjEx.of(info.currentArg);
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String pid = net.thevpc.halfa.api.util.HUtils.uid(u.get());
                    if (HParserUtils.isCommonStyleProperty(pid)) {
                        //will be processed later
                        info.node.setProperty(pid, Tson.ofTrue());
                        return true;
                    }
                }
                break;
            }
        }
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
                if (acceptTypeName(e.toName().value())) {
                    return e.toName().value();
                }
                break;
            }
            case UPLET:
            case NAMED_UPLET:
            {
                TsonUplet uplet = e.toUplet();
                if (uplet.isNamed() && acceptTypeName(uplet.name())) {
                    return uplet.name();
                }
                break;
            }
            case OBJECT:
            case NAMED_PARAMETRIZED_OBJECT:
            case PARAMETRIZED_OBJECT:
            case NAMED_OBJECT:
            {
                TsonObject h = e.toObject();
                if (h.isNamed() && acceptTypeName(h.name())) {
                    return h.name();
                }
                break;
            }
            case ARRAY:
            case NAMED_PARAMETRIZED_ARRAY:
            case PARAMETRIZED_ARRAY:
            case NAMED_ARRAY:

            {
                TsonArray h = e.toArray();
                if (h.isNamed() && acceptTypeName(h.name())) {
                    return h.name();
                }
                break;
            }
        }
        return null;
    }

    private boolean acceptTypeName(String id) {
        String oid = net.thevpc.halfa.api.util.HUtils.uid(id);
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
        return NCallableSupport.invalid(() -> NMsg.ofC("invalid %s : %s", id(), e));
    }

    public String resolveEffectiveId(String id) {
        String nId = getNodeId();
        if (!NBlankable.isBlank(nId)) {
            return net.thevpc.halfa.api.util.HUtils.uid(nId);
        }
        return HUtils.uid(id);
    }

    public void onStartParsingItem(String id, HNode p, TsonElement tsonElement, HNodeFactoryParseContext context) {

    }

    public void onFinishParsingItem(String id, HNode p, TsonElement tsonElement, HNodeFactoryParseContext context) {

    }

    protected boolean processArgumentAsCommonStyleProperty(ParseArgumentInfo info) {
        if (HParserUtils.isCommonStyleProperty(info.id)) {
            ObjEx es = ObjEx.of(info.currentArg);
            if (es.isFunction()) {
                info.context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid argument %s. did you mean %s:%s ?",
                        info.context.source(),
                        info.currentArg,
                        es.name(), Tson.ofUplet(es.args().toArray(new TsonElementBase[0]))
                ).asSevere(), info.context.source()));
                // empty result
                return false;
            }
            info.context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid argument %s in : %s", info.context.source(), info.currentArg, info.tsonElement).asSevere(), info.context.source()));
            return false;
        } else {
            info.context.messages().log(HMsg.of(NMsg.ofC("[%s] invalid argument %s in : %s", info.context.source(), info.currentArg, info.tsonElement).asSevere(), info.context.source()));
            return false;
        }
    }

    protected boolean processArguments(ParseArgumentInfo info) {
        for (int i = 0; i < info.arguments.length; i++) {
            TsonElement currentArg = info.arguments[i];
            info.currentArg = currentArg;
            info.currentArgIndex = i;
            if (!processArgument(info)) {
                return processArgumentAsCommonStyleProperty(info);
            }
        }
        return true;
    }

    public NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        HEngine engine = context.engine();
        HDocumentFactory f = context.documentFactory();
        HNode p = context.documentFactory().of(resolveEffectiveId(id));
        HNodeFactoryParseContext context2 = context.push(p);
        onStartParsingItem(id, p, tsonElement, context);
        switch (tsonElement.type()) {
            case NAMED_UPLET:

            case OBJECT:
            case NAMED_PARAMETRIZED_OBJECT:
            case PARAMETRIZED_OBJECT:
            case NAMED_OBJECT:

            case ARRAY:
            case NAMED_PARAMETRIZED_ARRAY:
            case PARAMETRIZED_ARRAY:
            case NAMED_ARRAY:
            {
                ObjEx ee = ObjEx.of(tsonElement);
                HParseHelper.fillAnnotations(tsonElement, p);
                ParseArgumentInfo info = new ParseArgumentInfo();
                info.id = id;
                info.uid = HUtils.uid(id);
                info.tsonElement = tsonElement;
                info.node = p;
                info.arguments = ee.args().toArray(new TsonElement[0]);
                info.f = f;
                info.context = context2;
                if (!processArguments(info)) {
                    context2.messages().log(HMsg.of(NMsg.ofC("[%s] invalid arguments %s in : %s", context2.source(), ee.args(), tsonElement).asSevere(), context2.source()));
                    return NOptional.of(new HItemList());
                }
                processImplicitStyles(info);
                for (TsonElement e : ee.body()) {
                    NOptional<HItem> u = engine.newNode(e, context2);
                    if (u.isPresent()) {
                        p.append(u.get());
                    } else {
                        NOptional<HItem> finalU = u;
                        context2.messages().log(HMsg.of(NMsg.ofC("[%s] error parsing child : %s : %s", context2.source(), e, finalU.getMessage().get()).asSevere(), context2.source()));
                        return NOptional.of(new HItemList());
                    }
                }
                break;
            }
        }
        onFinishParsingItem(id, p, tsonElement, context);
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
