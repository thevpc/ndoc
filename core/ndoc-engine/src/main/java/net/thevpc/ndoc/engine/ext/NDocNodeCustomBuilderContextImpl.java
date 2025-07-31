package net.thevpc.ndoc.engine.ext;

import net.thevpc.ndoc.engine.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.NDocNodeParser;
import net.thevpc.ndoc.api.parser.NDocArgumentParseInfo;
import net.thevpc.ndoc.api.renderer.*;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.util.ToElementHelper;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;

import java.util.*;

public class NDocNodeCustomBuilderContextImpl implements NDocNodeCustomBuilderContext {
    NDocNodeCustomBuilder builder;
    RenderAction renderMainAction;
    SizeRequirementsAction sizeRequirementsAction;
    SelfBoundsAction selfBoundsAction;
    String id;
    String[] aliases;
    ToElemAction toElem;
    NDocNodeRenderer createdRenderer;
    NDocNodeParserBase createdParser;
    NDocTextRendererFlavor createdTextFlavor;
    List<ProcessParamAction> processSingleArgumentList;
    boolean compiled;
    NDocEngine engine;
    Set<String> knownArgNames;
    CustomNamedParamAction customNamedParamAction;
    RenderTextAction renderTextAction;
    RenderConvertAction renderConvertAction;
    RenderEmbeddedTextAction renderEmbeddedTextAction;

    public NDocNodeCustomBuilderContextImpl(NDocNodeCustomBuilder builder, NDocEngine engine) {
        this.builder = builder;
        this.engine = engine;
    }

    @Override
    public NDocNodeCustomBuilderContext sizeRequirements(SizeRequirementsAction e) {
        this.sizeRequirementsAction = e;
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext selfBounds(SelfBoundsAction e) {
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
    public NDocNodeCustomBuilderContext id(String id) {
        requireNonCompiled();
        this.id = id;
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext alias(String... aliases) {
        requireNonCompiled();
        this.aliases = aliases;
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext withToElem(ToElemAction e) {
        requireNonCompiled();
        this.toElem = e;
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext withToElem(String... propNames) {
        return withToElem((item, context) -> ToElementHelper.of(
                        item,
                        context.engine()
                )
                .addChildrenByName(propNames)
                .build());
    }

    @Override
    public NDocNodeCustomBuilderContext renderComponent(RenderAction e) {
        requireNonCompiled();
        NAssert.requireNull(renderTextAction, "renderText");
        this.renderMainAction = e;
        if (renderMainAction != null) {
            this.renderTextAction = null;
            this.renderEmbeddedTextAction = null;
            this.renderConvertAction = null;
        }
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext renderText(RenderTextAction renderTextAction) {
        return renderText(renderTextAction, null);
    }

    @Override
    public NDocNodeCustomBuilderContext renderConvert(RenderConvertAction renderTextAction) {
        this.renderConvertAction = renderTextAction;
        if (this.renderConvertAction != null) {
            this.renderTextAction = null;
            this.renderEmbeddedTextAction = null;
            this.renderMainAction = null;
        }
        return this;
    }

    public NDocNodeCustomBuilderContext renderText(RenderTextAction renderTextAction, RenderEmbeddedTextAction embeddedTextAction) {
        requireNonCompiled();
        if (embeddedTextAction != null) {
            NAssert.requireNonNull(renderTextAction, "renderTextAction");
        }
        if (renderTextAction != null) {
            this.renderMainAction = null;
            this.renderConvertAction = null;
        }
        this.renderTextAction = renderTextAction;
        this.renderEmbeddedTextAction = embeddedTextAction;
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext addParamName(String name) {
        if (!NBlankable.isBlank(name)) {
            knownArgNames.add(NDocUtils.uid(name));
        }
        return this;
    }

    public NDocNodeCustomBuilderContext parseDefaults() {
        return parseParam(new ProcessParamAction() {
            @Override
            public boolean processParam(NDocArgumentParseInfo info, NDocNodeCustomBuilderContext buildContext) {
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
    public NDocNodeCustomBuilderContext parseParam(ProcessParamAction e) {
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
    public NDocEngine engine() {
        return engine;
    }

    public boolean isAncestorScene3D(NDocNode p0) {
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

    public NDocNodeParser createParser() {
        requireCompiled();
        if (createdParser == null) {
            createdParser = new CustomNDocNodeParserFromBuilder(this);
        }
        return createdParser;
    }

    public NDocTextRendererFlavor createTextFlavor() {
        if (renderTextAction != null && createdTextFlavor == null) {
            createdTextFlavor = new CustomNDocTextRendererFlavorFromBuilder2(this);
        }
        return createdTextFlavor;
    }

    public NDocNodeRenderer createRenderer() {
        requireCompiled();
        if (createdRenderer == null) {
            if (renderConvertAction != null) {
                createdRenderer = new NDocNodeRendererAsConverter(this);
            } else if (renderTextAction != null) {
                createdRenderer = new NDocNodeRendererAsText(this);
            } else {
                createdRenderer = new NDocNodeRendererAsDefault(this);
            }
        }
        return createdRenderer;
    }

    @Override
    public String toString() {
        return "NDocNodeCustomBuilderContextImpl{" +
                "id='" + id + '\'' +
                ", aliases=" + Arrays.toString(aliases) +
                '}';
    }
}
