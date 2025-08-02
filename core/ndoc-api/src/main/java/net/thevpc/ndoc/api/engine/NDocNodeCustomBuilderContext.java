package net.thevpc.ndoc.api.engine;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocItem;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.NDocSizeRequirements;
import net.thevpc.ndoc.api.parser.NDocAllArgumentReader;
import net.thevpc.ndoc.api.parser.NDocArgumentReader;
import net.thevpc.ndoc.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.renderer.text.NDocTextToken;
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

    NDocNodeCustomBuilderContext parseDefaultParams();

    NDocNodeCustomBuilderContext renderComponent(RenderAction e);

    NDocNodeCustomBuilderContext renderText(RenderTextAction renderTextAction, RenderEmbeddedTextAction embeddedTextAction);

    NDocNodeCustomBuilderContext renderText(RenderTextAction renderTextAction);

    NDocNodeCustomBuilderContext renderConvert(RenderConvertAction renderConvertAction);

    NamedParamAction parseParam();

    NDocNodeCustomBuilderContext parseParam(ProcessParamAction e);

    NDocNodeCustomBuilderContext afterParsingAllParams(ProcessNodeAction e);

    NDocEngine engine();

    boolean isAncestorScene3D(NDocNode p0);

    NDocNodeCustomBuilderContext addParamName(String points);


    interface NDocItemSpecialParser {
        NCallableSupport<NDocItem> parseElement(String id, NElement element, NDocNodeFactoryParseContext context);
    }

    interface NamedParamAction {
        NDocNodeCustomBuilderContext.NamedParamAction ignoreDuplicates();

        NamedParamAction ignoreDuplicates(boolean ignoreDuplicates);

        NamedParamAction matchesLeading(boolean ignoreDuplicates);

        NamedParamAction matchesLeading();

        NamedParamAction named(String... names);

        NamedParamAction matches(Predicate<NDocArgumentReader> predicate);

        NamedParamAction matchesString();

        NamedParamAction matchesName();

        NamedParamAction matchesStringOrName();

        NamedParamAction store(String newName);


        NamedParamAction resolvedAs(PropResolver a);

        NamedParamAction resolvedAsTrimmedBloc();

        NamedParamAction resolvedAsTrimmedPathTextContent();

        NDocNodeCustomBuilderContext then();

        NDocNodeCustomBuilderContext end();

        NamedParamAction asFlags();
    }

    interface PropResolver {
        NDocProp resolve(String uid, NElement value, NDocArgumentReader info, NDocNodeCustomBuilderContext buildContext);
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
        boolean processParam(NDocArgumentReader info, NDocNodeCustomBuilderContext buildContext);
    }

    interface ProcessNodeAction {
        void processNode(NDocAllArgumentReader info, NDocNodeCustomBuilderContext buildContext);
    }
}
