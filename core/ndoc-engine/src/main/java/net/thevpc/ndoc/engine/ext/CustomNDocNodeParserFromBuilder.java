package net.thevpc.ndoc.engine.ext;

import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.parser.NDocArgumentParseInfoImpl;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.parser.NDocParseHelper;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.engine.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.NDocArgumentParseInfo;
import net.thevpc.ndoc.api.util.ToElementHelper;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

class CustomNDocNodeParserFromBuilder extends NDocNodeParserBase {
    private final NDocNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext;

    public CustomNDocNodeParserFromBuilder(NDocNodeCustomBuilderContextImpl myNDocNodeCustomBuilderContext) {
        super(true, myNDocNodeCustomBuilderContext.id, myNDocNodeCustomBuilderContext.aliases);
        this.myNDocNodeCustomBuilderContext = myNDocNodeCustomBuilderContext;
    }

    @Override
    public NElement toElem(NDocNode item) {
        if (myNDocNodeCustomBuilderContext.toElem != null) {
            NElement u = myNDocNodeCustomBuilderContext.toElem.toElem(item, myNDocNodeCustomBuilderContext);
            if (u != null) {
                return u;
            }
        }
        if (myNDocNodeCustomBuilderContext.knownArgNames != null) {
            return ToElementHelper.of(
                            item,
                            engine()
                    )
                    .addChildrenByName(myNDocNodeCustomBuilderContext.knownArgNames.toArray(new String[0]))
                    .build();
        }
        return super.toElem(item);
    }

    @Override
    protected String acceptTypeName(NElement e) {
        String s = super.acceptTypeName(e);
        if(s!=null){
            return s;
        }
        return s;
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
        NDocNodeCustomBuilderContext.NDocItemSpecialParser pp = myNDocNodeCustomBuilderContext.extraElementSupport;
        if(pp==null && myNDocNodeCustomBuilderContext.extraElementSupportByPredicate !=null){
            if(myNDocNodeCustomBuilderContext.extraElementSupportByPredicate.test(e)) {
                pp = new NDocNodeCustomBuilderContext.NDocItemSpecialParser() {
                    @Override
                    public NCallableSupport<NDocItem> parseElement(String id, NElement tsonElement, NDocNodeFactoryParseContext context) {
                        NDocNode p = context.documentFactory().of(resolveEffectiveId(id));
                        p.setSource(context.source());
                        NDocNodeFactoryParseContext context2 = context.push(p);
                        onStartParsingItem(id, p, tsonElement, context);
                        NDocParseHelper.fillAnnotations(tsonElement, p);
                        NDocArgumentParseInfoImpl info = new NDocArgumentParseInfoImpl();
                        info.setContext(context2);
                        info.setId(id);
                        info.setUid(NDocUtils.uid(id));
                        info.setTsonElement(NDocUtils.addCompilerDeclarationPath(tsonElement, info.getContext().source()));
                        info.setNode(p);
                        info.setArguments(new NElement[]{tsonElement});
                        info.setF(context.documentFactory());
                        processArguments(info);
                        processImplicitStyles(info);
                        onFinishParsingItem(id, p, tsonElement, context);
                        return NCallableSupport.ofValid(myNDocNodeCustomBuilderContext.extraElementSupportByPredicateSupport,p);
                    }
                };
            }
        }
        if(pp !=null){
            NCallableSupport<NDocItem> y = pp.parseElement(id(),e,context);
            if(y!=null){
                return y;
            }
        }
        //context.messages().addError(NMsg.ofC("invalid %s : %s", id(), e), context.source());
        return NCallableSupport.ofInvalid(() -> NMsg.ofC("invalid %s : %s", id(), e));
    }

    @Override
    protected boolean processArgument(NDocArgumentParseInfo info) {
        if (myNDocNodeCustomBuilderContext.processSingleArgumentList != null) {
            for (NDocNodeCustomBuilderContext.ProcessParamAction a : myNDocNodeCustomBuilderContext.processSingleArgumentList) {
                if (a.processParam(info, myNDocNodeCustomBuilderContext)) {
                    return true;
                }
            }
            if (myNDocNodeCustomBuilderContext.createdParser.defaultProcessArgument(info)) {
                return true;
            }
            return false;
        }
        return super.processArgument(info);
    }
}
