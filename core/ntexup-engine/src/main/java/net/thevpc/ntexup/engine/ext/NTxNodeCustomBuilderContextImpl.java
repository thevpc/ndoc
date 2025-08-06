package net.thevpc.ntexup.engine.ext;

import net.thevpc.ntexup.api.parser.*;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererFlavor;
import net.thevpc.ntexup.engine.parser.NTxNodeParserBase;
import net.thevpc.ntexup.api.document.node.NTxItem;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.renderer.*;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.ntexup.engine.util.ToElementHelper;
import net.thevpc.nuts.NConstants;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;

import java.util.*;
import java.util.function.Predicate;

public class NTxNodeCustomBuilderContextImpl implements NTxNodeCustomBuilderContext {
    NTxNodeBuilder builder;
    RenderAction renderMainAction;
    SizeRequirementsAction sizeRequirementsAction;
    SelfBoundsAction selfBoundsAction;
    String id;
    String[] aliases;
    ToElemAction toElem;
    NTxNodeRenderer createdRenderer;
    NTxNodeParserBase createdParser;
    NTxTextRendererFlavor createdTextFlavor;
    List<ProcessParamAction> processSingleArgumentList;
    List<ProcessNodeAction> afterProcessAllArgumentsList;
    boolean compiled;
    NTxEngine engine;
    Set<String> knownArgNames;
    NTxItemSpecialParser extraElementSupport;
    Predicate<NElement> extraElementSupportByPredicate;
    int extraElementSupportByPredicateSupport;
    CustomNamedParamAction customNamedParamAction;
    MyRenderTextAction renderTextAction;
    RenderConvertAction renderConvertAction;
    ProcessNodeAction processChildren;

    public NTxNodeCustomBuilderContextImpl(NTxNodeBuilder builder, NTxEngine engine) {
        this.builder = builder;
        this.engine = engine;
    }

    @Override
    public NTxNodeCustomBuilderContext sizeRequirements(SizeRequirementsAction e) {
        this.sizeRequirementsAction = e;
        return this;
    }

    public NTxNodeCustomBuilderContext parseAny(NTxItemSpecialParser a) {
        this.extraElementSupport = a;
        return this;
    }

    @Override
    public NTxNodeCustomBuilderContext parseAny(Predicate<NElement> a) {
        this.extraElementSupportByPredicate = a;
        this.extraElementSupportByPredicateSupport = NConstants.Support.DEFAULT_SUPPORT;
        return this;
    }

    @Override
    public NTxNodeCustomBuilderContext parseAny(Predicate<NElement> a, int support) {
        this.extraElementSupportByPredicate = a;
        this.extraElementSupportByPredicateSupport = support;
        return this;
    }

    @Override
    public NTxNodeCustomBuilderContext selfBounds(SelfBoundsAction e) {
        this.selfBoundsAction = e;
        return this;
    }

    private void requireNonCompiled() {
        if (compiled) {
            throw new NIllegalArgumentException(NMsg.ofC("context is readonly"));
        }
    }

    private void requireCompiled() {
        if (!compiled) {
            throw new NIllegalArgumentException(NMsg.ofC("context is still in building process"));
        }
    }

    public String id() {
        return id;
    }

    @Override
    public NTxNodeCustomBuilderContext id(String id) {
        requireNonCompiled();
        this.id = id;
        return this;
    }

    @Override
    public String[] idAndAliases() {
        LinkedHashSet<String> s = new LinkedHashSet<>();
        if (!NBlankable.isBlank(id)) {
            s.add(id);
        }
        if (aliases != null) {
            for (String a : aliases) {
                if (!NBlankable.isBlank(a)) {
                    s.add(a);
                }
            }
        }
        return s.toArray(new String[0]);
    }

    @Override
    public NTxNodeCustomBuilderContext alias(String... aliases) {
        requireNonCompiled();
        this.aliases = aliases;
        return this;
    }

    @Override
    public NTxNodeCustomBuilderContext withToElem(ToElemAction e) {
        requireNonCompiled();
        this.toElem = e;
        return this;
    }

    @Override
    public NTxNodeCustomBuilderContext withToElem(String... propNames) {
        return withToElem((item, context) -> ToElementHelper.of(
                        item,
                        context.engine()
                )
                .addChildrenByName(propNames)
                .build());
    }

    @Override
    public NTxNodeCustomBuilderContext renderComponent(RenderAction e) {
        requireNonCompiled();
        this.renderMainAction = e;
        if (renderMainAction != null) {
            this.renderTextAction = null;
            this.renderConvertAction = null;
        }
        return this;
    }

    @Override
    public RenderTextAction renderText() {
        this.renderTextAction = new MyRenderTextAction(this);
        this.renderConvertAction = null;
        this.renderMainAction = null;
        return this.renderTextAction;
    }

    @Override
    public NTxNodeCustomBuilderContext renderConvert(RenderConvertAction renderTextAction) {
        this.renderConvertAction = renderTextAction;
        if (this.renderConvertAction != null) {
            this.renderTextAction = null;
            this.renderMainAction = null;
        }
        return this;
    }

    @Override
    public NTxNodeCustomBuilderContext addParamName(String name) {
        if (!NBlankable.isBlank(name)) {
            knownArgNames.add(NTxUtils.uid(name));
        }
        return this;
    }

    public NTxNodeCustomBuilderContext parseDefaultParams() {
        return parseParam(new ProcessParamAction() {
            @Override
            public boolean processParam(NTxArgumentReader info, NTxNodeCustomBuilderContext buildContext) {
                createParser();
                return createdParser.defaultProcessArgument(info);
            }
        });
    }

    @Override
    public NamedParamAction parseParam() {
        return new CustomNamedParamAction(this);
    }


    @Override
    public NTxNodeCustomBuilderContext parseParam(ProcessParamAction e) {
        requireNonCompiled();
        if (e != null) {
            if (customNamedParamAction != null) {
                customNamedParamAction.end();
                this.customNamedParamAction = null;
            }
            if (processSingleArgumentList == null) {
                this.processSingleArgumentList = new ArrayList<>();
            }
            this.processSingleArgumentList.add(e);
        }
        return this;
    }

    @Override
    public NTxNodeCustomBuilderContext afterParsingAllParams(ProcessNodeAction e) {
        requireNonCompiled();
        if (e != null) {
            if (afterProcessAllArgumentsList == null) {
                this.afterProcessAllArgumentsList = new ArrayList<>();
            }
            this.afterProcessAllArgumentsList.add(e);
        }
        return this;
    }

    @Override
    public NTxNodeCustomBuilderContext processChildren(ProcessNodeAction processChildren) {
        this.processChildren = processChildren;
        return this;
    }

    @Override
    public NTxEngine engine() {
        return engine;
    }

    public boolean isAncestorScene3D(NTxNode p0) {
        NTxItem p = p0;
        while (p != null) {
            if (p instanceof NTxNode) {
                if (NTxNodeType.SCENE3D.equals(((NTxNode) p).type())) {
                    return true;
                }
            }
            p = p.parent();
        }
        return false;
    }


    public void compile() {
        if (!compiled) {
            if (customNamedParamAction != null) {
                customNamedParamAction.end();
                this.customNamedParamAction = null;
            }
            NAssert.requireNonBlank(id, "id");
            this.compiled = true;
        }
    }

    public NTxNodeParser createParser() {
        requireCompiled();
        if (createdParser == null) {
            createdParser = new CustomNTxNodeParserFromBuilder(this);
        }
        return createdParser;
    }

    public NTxTextRendererFlavor createTextFlavor() {
        if (renderTextAction != null && createdTextFlavor == null) {
            createdTextFlavor = new CustomNTxTextRendererFlavorFromBuilder(this);
        }
        return createdTextFlavor;
    }

    public NTxNodeRenderer createRenderer() {
        requireCompiled();
        if (createdRenderer == null) {
            if (renderConvertAction != null) {
                createdRenderer = new NTxNodeRendererAsConverter(this);
            } else if (renderTextAction != null) {
                createdRenderer = new NTxNodeRendererAsText(this);
            } else {
                createdRenderer = new NTxNodeRendererAsDefault(this);
            }
        }
        return createdRenderer;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id='" + id + '\'' +
                ", aliases=" + Arrays.toString(aliases) +
                '}';
    }

}
