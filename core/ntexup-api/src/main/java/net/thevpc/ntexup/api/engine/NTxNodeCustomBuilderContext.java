package net.thevpc.ntexup.api.engine;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.NTxItem;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NTxProp;
import net.thevpc.ntexup.api.document.NDocSizeRequirements;
import net.thevpc.ntexup.api.parser.NDocAllArgumentReader;
import net.thevpc.ntexup.api.parser.NDocArgumentReader;
import net.thevpc.ntexup.api.parser.NDocNodeFactoryParseContext;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NDocTextToken;
import net.thevpc.ntexup.api.renderer.text.NDocTextOptions;
import net.thevpc.ntexup.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.reserved.util.NReservedSimpleCharQueue;

import java.util.List;
import java.util.function.Predicate;

public interface NTxNodeCustomBuilderContext {
    String id();

    NTxNodeCustomBuilderContext id(String id);

    NTxNodeCustomBuilderContext alias(String... aliases);

    public NTxNodeCustomBuilderContext parseAny(NTxItemSpecialParser a);

    public NTxNodeCustomBuilderContext parseAny(Predicate<NElement> a);

    public NTxNodeCustomBuilderContext parseAny(Predicate<NElement> a, int support);

    NTxNodeCustomBuilderContext sizeRequirements(SizeRequirementsAction e);

    NTxNodeCustomBuilderContext selfBounds(SelfBoundsAction e);

    NTxNodeCustomBuilderContext withToElem(ToElemAction e);

    NTxNodeCustomBuilderContext withToElem(String... props);

    NTxNodeCustomBuilderContext parseDefaultParams();

    NTxNodeCustomBuilderContext renderComponent(RenderAction e);

    NTxNodeCustomBuilderContext renderText(RenderTextAction renderTextAction, RenderEmbeddedTextAction embeddedTextAction);

    NTxNodeCustomBuilderContext renderText(RenderTextAction renderTextAction);

    NTxNodeCustomBuilderContext renderConvert(RenderConvertAction renderConvertAction);

    NamedParamAction parseParam();

    NTxNodeCustomBuilderContext parseParam(ProcessParamAction e);

    NTxNodeCustomBuilderContext afterParsingAllParams(ProcessNodeAction e);
    NTxNodeCustomBuilderContext processChildren(ProcessNodeAction e);

    NTxEngine engine();

    boolean isAncestorScene3D(NTxNode p0);

    NTxNodeCustomBuilderContext addParamName(String points);


    interface NTxItemSpecialParser {
        NCallableSupport<NTxItem> parseElement(String id, NElement element, NDocNodeFactoryParseContext context);
    }

    interface NamedParamAction {
        NTxNodeCustomBuilderContext.NamedParamAction ignoreDuplicates();

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

        NTxNodeCustomBuilderContext then();

        NTxNodeCustomBuilderContext end();

        NamedParamAction asFlags();
    }

    interface PropResolver {
        NTxProp resolve(String uid, NElement value, NDocArgumentReader info, NTxNodeCustomBuilderContext buildContext);
    }

    interface ToElemAction {
        NElement toElem(NTxNode item, NTxNodeCustomBuilderContext buildContext);
    }

    interface RenderAction {
        void renderMain(NTxNode p, NDocNodeRendererContext renderContext, NTxNodeCustomBuilderContext buildContext);
    }

    interface RenderTextAction {
        void buildText(String text, NDocTextOptions options, NTxNode p, NDocNodeRendererContext renderContext, NDocTextRendererBuilder builder, NTxNodeCustomBuilderContext buildContext);
    }

    interface RenderConvertAction {
        NTxNode convert(NTxNode p, NDocNodeRendererContext ctx, NTxNodeCustomBuilderContext buildContext);
    }

    interface RenderEmbeddedTextAction {
        List<NDocTextToken> parseImmediate(NReservedSimpleCharQueue queue, NDocNodeRendererContext renderContext, NTxNodeCustomBuilderContext buildContext);
    }

    interface SizeRequirementsAction {
        NDocSizeRequirements sizeRequirements(NTxNode p, NDocNodeRendererContext renderContext, NTxNodeCustomBuilderContext buildContext);
    }

    interface SelfBoundsAction {
        NTxBounds2 selfBounds(NTxNode p, NDocNodeRendererContext renderContext, NTxNodeCustomBuilderContext buildContext);
    }

    interface ProcessParamAction {
        boolean processParam(NDocArgumentReader info, NTxNodeCustomBuilderContext buildContext);
    }

    interface ProcessNodeAction {
        void processNode(NDocAllArgumentReader info, NTxNodeCustomBuilderContext buildContext);
    }
}
