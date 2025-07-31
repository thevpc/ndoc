package net.thevpc.ndoc.api.extension;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.model.NDocSizeRequirements;
import net.thevpc.ndoc.api.parser.NDocArgumentParseInfo;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.NDocTextToken;
import net.thevpc.ndoc.api.renderer.text.NDocTextOptions;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;

import java.util.List;
import java.util.function.Predicate;

public interface NDocNodeCustomBuilderContext {
    String id();

    NDocNodeCustomBuilderContext id(String id);

    NDocNodeCustomBuilderContext alias(String... aliases);

    public NDocNodeCustomBuilderContext parseAny(NDocItemSpecialParser a);

    public NDocNodeCustomBuilderContext parseAny(Predicate<NElement> a);
    public NDocNodeCustomBuilderContext parseAny(Predicate<NElement> a, int support);

    NDocNodeCustomBuilderContext sizeRequirements(SizeRequirementsAction e);

    NDocNodeCustomBuilderContext selfBounds(SelfBoundsAction e);

    NDocNodeCustomBuilderContext withToElem(ToElemAction e);

    NDocNodeCustomBuilderContext withToElem(String... props);

    NDocNodeCustomBuilderContext parseDefaults();

    NDocNodeCustomBuilderContext renderComponent(RenderAction e);

    NDocNodeCustomBuilderContext renderText(RenderTextAction renderTextAction, RenderEmbeddedTextAction embeddedTextAction);

    NDocNodeCustomBuilderContext renderText(RenderTextAction renderTextAction);

    NDocNodeCustomBuilderContext renderConvert(RenderConvertAction renderConvertAction);

    NamedParamAction parseParam();

    NDocNodeCustomBuilderContext parseParam(ProcessParamAction e);

    NDocEngine engine();

    boolean isAncestorScene3D(NDocNode p0);

    NDocNodeCustomBuilderContext addParamName(String points);


    interface NDocItemSpecialParser {
        NCallableSupport<NDocItem> parseElement(String id, NElement tsonElement, NDocNodeFactoryParseContext context);
    }

    interface NamedParamAction {
        NamedParamAction ignoreDuplicates(boolean ignoreDuplicates);

        NamedParamAction named(String... names);

        NamedParamAction matches(Predicate<NElement> predicate);

        NamedParamAction matchesString();

        NamedParamAction matchesName();

        NamedParamAction matchesStringOrName();

        NamedParamAction set(String newName);


        NamedParamAction resolvedAs(PropResolver a);

        NDocNodeCustomBuilderContext then();

        NDocNodeCustomBuilderContext end();

        NamedParamAction asFlags();
    }

    interface PropResolver {
        NDocProp resolve(String uid, NElement value, NDocArgumentParseInfo info, NDocNodeCustomBuilderContext buildContext);
    }

    interface ToElemAction {
        NElement toElem(NDocNode item, NDocNodeCustomBuilderContext buildContext);
    }

    interface RenderAction {
        void renderMain(NDocNode p, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext buildContext);
    }

    interface RenderTextAction {
        void buildText(String text, NDocTextOptions options, NDocNode p, NDocNodeRendererContext renderContext, NDocTextRendererBuilder builder, NDocNodeCustomBuilderContext buildContext);
    }

    interface RenderConvertAction {
        NDocNode convert(NDocNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext buildContext);
    }

    interface RenderEmbeddedTextAction {
        List<NDocTextToken> parseImmediate(NReservedSimpleCharQueue queue, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext buildContext);
    }

    interface SizeRequirementsAction {
        NDocSizeRequirements sizeRequirements(NDocNode p, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext buildContext);
    }

    interface SelfBoundsAction {
        NDocBounds2 selfBounds(NDocNode p, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext buildContext);
    }

    interface ProcessParamAction {
        boolean processParam(NDocArgumentParseInfo info, NDocNodeCustomBuilderContext buildContext);
    }
}
