package net.thevpc.ndoc.engine.parser;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.parser.*;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.util.ToElementHelper;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.document.node.*;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;


import java.util.Collections;
import java.util.List;

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

    protected void processImplicitStyles(NDocArgumentReader info) {

    }

    protected boolean isAcceptableArgKeyPair(String s) {
        return false;
    }

    protected boolean processArgument(NDocArgumentReader info) {
        return defaultProcessArgument(info);
    }

    public boolean defaultProcessArgument(NDocArgumentReader info) {
        NElement e = info.peek();
        if (e != null) {
            if (e.isNamedPair()) {
                NPairElement p = e.asPair().get();
                String sid = NDocUtils.uid(p.key().asStringValue().get());
                if (HParserUtils.isCommonStyleProperty(sid)) {
                    info.node().setProperty(sid, NDocUtils.addCompilerDeclarationPath(p.value(), info.source()));
                    info.read();
                    return true;
                }
            } else if (e.isName()) {
                String sid = NDocUtils.uid(e.asStringValue().get());
                if (HParserUtils.isCommonStyleProperty(sid)) {
                    info.node().setProperty(sid, NDocUtils.addCompilerDeclarationPath(NElement.ofTrue(), info.source()));
                    info.read();
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isAncestorScene3D(NDocNode p0) {
        NDocItem p = p0;
        while (p != null) {
            if (p instanceof NDocNode) {
                if (NDocNodeType.SCENE3D.equals(((NDocNode) p).type())) {
                    return true;
                }
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
            case NAMED_UPLET: {
                NUpletElement uplet = e.asUplet().get();
                if (acceptTypeName(uplet.name().orNull())) {
                    return uplet.name().orNull();
                }
                break;
            }
            case NAMED_PARAMETRIZED_OBJECT:
            case NAMED_OBJECT: {
                NObjectElement h = e.toObject().get();
                if (acceptTypeName(h.name().orNull())) {
                    return h.name().orNull();
                }
                break;
            }
            case NAMED_PARAMETRIZED_ARRAY:
            case NAMED_ARRAY: {
                NArrayElement h = e.toArray().get();
                if (acceptTypeName(h.name().orNull())) {
                    return h.name().orNull();
                }
                break;
            }
        }
        return null;
    }

    private boolean acceptTypeName(String id) {
        String oid = NDocUtils.uid(id);
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
    public NCallableSupport<NDocItem> parseNode(NDocNodeFactoryParseContext context) {
        NElement e = context.element();

        String s = acceptTypeName(e);
        if (s != null) {
            return NCallableSupport.ofValid(
                    () -> {
                        NOptional<NDocItem> o = parseItem(s, e, context);
                        if (!o.isPresent()) {
                            return parseItem(s, e, context).get();
                        }
                        return o.get();
                    }
            );
        }
        //context.messages().addError(NMsg.ofC("invalid %s : %s", id(), e), context.source());
        return NCallableSupport.ofInvalid(() -> NMsg.ofC("invalid %s : %s", id(), e));
    }

    public String resolveEffectiveId(String id) {
        String nId = getNodeId();
        if (!NBlankable.isBlank(nId)) {
            return NDocUtils.uid(nId);
        }
        return NDocUtils.uid(id);
    }

    public void onStartParsingItem(String id, NDocNode p, NElement element, NDocNodeFactoryParseContext context) {

    }

    public void onFinishParsingItem(NDocAllArgumentReader info) {

    }

    protected boolean processArgumentAsCommonStyleProperty(NDocArgumentReader info) {
        NElement currentArg = info.peek();
        if (HParserUtils.isCommonStyleProperty(info.getId())) {
            NDocValue es = NDocValue.of(currentArg);
            if (es.isFunction()) {
                info.parseContext().messages().log(NMsg.ofC("[%s] invalid argument %s. did you mean %s:%s ?",
                        info.parseContext().source(),
                        currentArg,
                        es.name(), NElement.ofUplet(es.args().toArray(new NElement[0]))
                ).asSevere(), info.parseContext().source());
                // empty result
                return false;
            }
            info.parseContext().messages().log(NMsg.ofC("[%s] invalid argument %s in : %s", info.parseContext().source(), currentArg, info.element()).asSevere(), info.parseContext().source());
            return false;
        } else {
            info.parseContext().messages().log(NMsg.ofC("[%s] invalid argument %s in : %s", NDocUtils.shortName(info.parseContext().source()), NDocUtils.snippet(currentArg), NDocUtils.snippet(info.element())).asSevere(), info.parseContext().source());
            return false;
        }
    }

    protected void processArguments(NDocArgumentReader info) {
        while (!info.isEmpty()) {
            int before = info.availableCount();
            boolean b = processArgument(info);
            int after = info.availableCount();
            if (b) {
                if (after == before) {
                    processArgument(info);
                    NElement skipped = info.peek();
                    engine().log().log(NMsg.ofC("process arg is buggy, skipped %s...", NDocUtils.snippet(skipped)));
                    info.read();
                }
            } else {
                if (!processArgumentAsCommonStyleProperty(info)) {
                    NElement skipped = info.peek();
                    engine().log().log(NMsg.ofC("[%s] invalid argument %s", id,
                                    NDocUtils.snippet(skipped)
                            ).asSevere(), info.source()
                    );
                    info.read();
                }
            }
        }
    }

    public NOptional<NDocItem> parseItem(String id, NElement element, NDocNodeFactoryParseContext context) {
        NDocEngine engine = context.engine();
        NDocDocumentFactory f = context.documentFactory();
        switch (element.type()) {
            case NAMED_UPLET:

            case OBJECT:
            case NAMED_PARAMETRIZED_OBJECT:
            case PARAMETRIZED_OBJECT:
            case NAMED_OBJECT:

            case ARRAY:
            case NAMED_PARAMETRIZED_ARRAY:
            case PARAMETRIZED_ARRAY:
            case NAMED_ARRAY: {
                NDocNode p = context.documentFactory().of(resolveEffectiveId(id));
                p.setSource(context.source());
                NDocNodeFactoryParseContext context2 = context.push(p);
                onStartParsingItem(id, p, element, context);
//                NDocObjEx ee = NDocObjEx.of(element);
                NDocParseHelper.fillAnnotations(element, p);
                NDocArgumentReaderImpl info = new NDocArgumentReaderImpl();
                info.setContext(context2);
                info.setId(id);
                info.setUid(NDocUtils.uid(id));
                info.setElement(NDocUtils.addCompilerDeclarationPath(element, info.parseContext().source()));
                info.setNode(p);
                switch (element.type()) {
                    case UPLET:
                    case NAMED_UPLET: {
                        info.setArguments(element.asUplet().get().params().toArray(new NElement[0]));
                        break;
                    }
                    default: {
                        info.setArguments(element.asParametrizedContainer().flatMap(x -> x.params()).orElse(Collections.emptyList()).toArray(new NElement[0]));
                    }
                }
                if (info.allArguments() != null) {
                    NElement[] arguments = info.allArguments();
                    for (int i = 0; i < arguments.length; i++) {
                        info.allArguments()[i] = NDocUtils.addCompilerDeclarationPath(arguments[i], info.parseContext().source());
                    }
                }
                info.setF(f);
                processArguments(info);
                processImplicitStyles(info);
                processChildren(info);
                onFinishParsingItem(info);
                return NOptional.of(p);
            }
        }
        return NOptional.ofNamedEmpty(NMsg.ofC("%s item %s", id(), element));
    }

    protected void processChildren(NDocAllArgumentReader info) {
        NElement element = info.element();
        List<NElement> body = null;
        switch (element.type()) {
            case UPLET:
            case NAMED_UPLET: {
                return;
            }
            default: {
                body = element.asListContainer().map(x -> x.children()).orElse(Collections.emptyList());
            }
        }
        for (NElement e : body) {
            NOptional<NDocItem> u = engine.newNode(e, info.parseContext());
            if (u.isPresent()) {
                info.node().append(u.get());
            } else {
                info.parseContext().messages().log(NMsg.ofC("[%s] error parsing child : %s : %s", info.parseContext().source(), e, u.getMessage().get()).asSevere(), info.parseContext().source());
            }
        }
    }

    @Override
    public NElement toElem(NDocNode item) {
        return ToElementHelper.of((NDocNode) item, engine)
                .build();
    }

    @Override
    public NDocNode newNode() {
        return engine().newDefaultNode(id());
    }

    @Override
    public boolean validateNode(NDocNode node) {
        return false;
    }

    protected NCallableSupport<NDocItem> _invalidSupport(NMsg msg, NDocNodeFactoryParseContext context) {
        msg = msg.asError();
        context.messages().log(msg.asError());
        return NCallableSupport.ofInvalid(msg);
    }

    protected void _logError(NMsg nMsg, NDocNodeFactoryParseContext context) {
        context.messages().log(nMsg.asError(), context.source());
    }
}
