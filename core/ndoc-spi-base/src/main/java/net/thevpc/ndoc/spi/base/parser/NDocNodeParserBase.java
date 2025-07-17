package net.thevpc.ndoc.spi.base.parser;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocMsg;
import net.thevpc.ndoc.api.model.node.HItem;
import net.thevpc.ndoc.api.model.node.HItemList;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.model.node.NDocNodeType;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.model.DefaultNDocNode;
import net.thevpc.ndoc.spi.base.format.ToElementHelper;
import net.thevpc.ndoc.spi.eval.NDocParseHelper;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.ndoc.spi.nodes.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.spi.NDocNodeParser;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class NDocNodeParserBase implements NDocNodeParser {

    private String id;
    private String[] aliases;
    private boolean container;
    private NDocEngine engine;
    private String nodeId;

    public NDocNodeParserBase(boolean container, String id, String... aliases) {
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

    public NDocEngine engine() {
        return engine;
    }

    public void init(NDocEngine engine) {
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
        public NElement tsonElement;
        public NDocNode node;
        public NElement currentArg;
        public NElement[] arguments;
        public int currentArgIndex;
        public NDocDocumentFactory f;
        public NDocNodeFactoryParseContext context;
        public Map<String, Object> props = new HashMap<>();
    }

    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case PAIR: {
                if (info.currentArg.isSimplePair()) {
                    NPairElement p = info.currentArg.asPair().get();
                    String sid = net.thevpc.ndoc.api.util.HUtils.uid(p.key().asStringValue().get());
                    if (HParserUtils.isCommonStyleProperty(sid)) {
                        info.node.setProperty(sid, p.value());
                        return true;
                    }
                }
                break;
            }
            case NAME: {
                NDocObjEx h = NDocObjEx.of(info.currentArg);
                NOptional<String> u = h.asStringOrName();
                if (u.isPresent()) {
                    String pid = net.thevpc.ndoc.api.util.HUtils.uid(u.get());
                    if (HParserUtils.isCommonStyleProperty(pid)) {
                        //will be processed later
                        info.node.setProperty(pid, NElement.ofTrue());
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    protected boolean isAncestorScene3D(NDocNode p) {
        while (p != null) {
            if (p.type() == NDocNodeType.SCENE3D) {
                return true;
            }
            p = p.parent();
        }
        return false;
    }

    protected String acceptTypeName(NElement e) {
        switch (e.type()) {
            case NAME: {
                if (acceptTypeName(e.asStringValue().get())) {
                    return e.asStringValue().get();
                }
                break;
            }
            case UPLET:
            case NAMED_UPLET:
            {
                NUpletElement uplet = e.asUplet().get();
                if (uplet.isNamed() && acceptTypeName(uplet.name().orNull())) {
                    return uplet.name().orNull();
                }
                break;
            }
            case OBJECT:
            case NAMED_PARAMETRIZED_OBJECT:
            case PARAMETRIZED_OBJECT:
            case NAMED_OBJECT:
            {
                NObjectElement h = e.toObject().get();
                if (h.isNamed() && acceptTypeName(h.name().orNull())) {
                    return h.name().orNull();
                }
                break;
            }
            case ARRAY:
            case NAMED_PARAMETRIZED_ARRAY:
            case PARAMETRIZED_ARRAY:
            case NAMED_ARRAY:

            {
                NArrayElement h = e.toArray().get();
                if (h.isNamed() && acceptTypeName(h.name().orNull())) {
                    return h.name().orNull();
                }
                break;
            }
        }
        return null;
    }

    private boolean acceptTypeName(String id) {
        String oid = net.thevpc.ndoc.api.util.HUtils.uid(id);
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
    public NCallableSupport<HItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement e = context.element();
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
            return net.thevpc.ndoc.api.util.HUtils.uid(nId);
        }
        return HUtils.uid(id);
    }

    public void onStartParsingItem(String id, NDocNode p, NElement tsonElement, NDocNodeFactoryParseContext context) {

    }

    public void onFinishParsingItem(String id, NDocNode p, NElement tsonElement, NDocNodeFactoryParseContext context) {

    }

    protected boolean processArgumentAsCommonStyleProperty(ParseArgumentInfo info) {
        if (HParserUtils.isCommonStyleProperty(info.id)) {
            NDocObjEx es = NDocObjEx.of(info.currentArg);
            if (es.isFunction()) {
                info.context.messages().log(NDocMsg.of(NMsg.ofC("[%s] invalid argument %s. did you mean %s:%s ?",
                        info.context.source(),
                        info.currentArg,
                        es.name(), NElement.ofUplet(es.args().toArray(new NElement[0]))
                ).asSevere(), info.context.source()));
                // empty result
                return false;
            }
            info.context.messages().log(NDocMsg.of(NMsg.ofC("[%s] invalid argument %s in : %s", info.context.source(), info.currentArg, info.tsonElement).asSevere(), info.context.source()));
            return false;
        } else {
            info.context.messages().log(NDocMsg.of(NMsg.ofC("[%s] invalid argument %s in : %s", info.context.source(), info.currentArg, info.tsonElement).asSevere(), info.context.source()));
            return false;
        }
    }

    protected boolean processArguments(ParseArgumentInfo info) {
        for (int i = 0; i < info.arguments.length; i++) {
            NElement currentArg = info.arguments[i];
            info.currentArg = currentArg;
            info.currentArgIndex = i;
            if (!processArgument(info)) {
                return processArgumentAsCommonStyleProperty(info);
            }
        }
        return true;
    }

    public NOptional<HItem> parseItem(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
        NDocEngine engine = context.engine();
        NDocDocumentFactory f = context.documentFactory();
        NDocNode p = context.documentFactory().of(resolveEffectiveId(id));
        NDocNodeFactoryParseContext context2 = context.push(p);
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
//                NDocObjEx ee = NDocObjEx.of(tsonElement);
                NDocParseHelper.fillAnnotations(tsonElement, p);
                ParseArgumentInfo info = new ParseArgumentInfo();
                info.id = id;
                info.uid = HUtils.uid(id);
                info.tsonElement = tsonElement;
                info.node = p;
                List<NElement> body;
                switch (tsonElement.type()) {
                    case UPLET:
                    case NAMED_UPLET:
                    {
                        info.arguments = tsonElement.asUplet().get().params().toArray(new NElement[0]);
                        body= Collections.emptyList();
                        break;
                    }
                    default:{
                        info.arguments = tsonElement.asParametrizedContainer().flatMap(x->x.params()).orElse(Collections.emptyList()).toArray(new NElement[0]);
                        body = tsonElement.asListContainer().map(x->x.children()).orElse(Collections.emptyList());
                    }
                }

                info.f = f;
                info.context = context2;
                if (!processArguments(info)) {
                    context2.messages().log(NDocMsg.of(NMsg.ofC("[%s] invalid arguments %s in : %s", context2.source(), info.arguments, tsonElement).asSevere(), context2.source()));
                    return NOptional.of(new HItemList());
                }
                processImplicitStyles(info);
                for (NElement e : body) {
                    NOptional<HItem> u = engine.newNode(e, context2);
                    if (u.isPresent()) {
                        p.append(u.get());
                    } else {
                        NOptional<HItem> finalU = u;
                        context2.messages().log(NDocMsg.of(NMsg.ofC("[%s] error parsing child : %s : %s", context2.source(), e, finalU.getMessage().get()).asSevere(), context2.source()));
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
    public NElement toElem(NDocNode item) {
        return ToElementHelper.of((NDocNode) item, engine)
                .build();
    }

    @Override
    public NDocNode newNode() {
        return new DefaultNDocNode(id());
    }

    @Override
    public boolean validateNode(NDocNode node) {
        return false;
    }
}
