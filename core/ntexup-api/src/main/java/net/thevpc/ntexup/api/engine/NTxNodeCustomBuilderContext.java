package net.thevpc.ntexup.api.engine;

import net.thevpc.ntexup.api.document.elem2d.NTxBounds2;
import net.thevpc.ntexup.api.document.node.NTxItem;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NTxProp;
import net.thevpc.ntexup.api.document.NTxSizeRequirements;
import net.thevpc.ntexup.api.parser.NTxAllArgumentReader;
import net.thevpc.ntexup.api.parser.NTxArgumentReader;
import net.thevpc.ntexup.api.parser.NTxNodeFactoryParseContext;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.renderer.text.NTxTextToken;
import net.thevpc.ntexup.api.renderer.text.NTxTextOptions;
import net.thevpc.ntexup.api.renderer.text.NTxTextRendererBuilder;
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
        NCallableSupport<NTxItem> parseElement(String id, NElement element, NTxNodeFactoryParseContext context);
    }

    interface NamedParamAction {
        NTxNodeCustomBuilderContext.NamedParamAction ignoreDuplicates();

        NamedParamAction ignoreDuplicates(boolean ignoreDuplicates);

        NamedParamAction matchesLeading(boolean ignoreDuplicates);

        NamedParamAction matchesLeading();

        NamedParamAction named(String... names);

        NamedParamAction matches(Predicate<NTxArgumentReader> predicate);

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
        NTxProp resolve(String uid, NElement value, NTxArgumentReader info, NTxNodeCustomBuilderContext buildContext);
    }

    interface ToElemAction {
        NElement toElem(NTxNode item, NTxNodeCustomBuilderContext buildContext);
    }

    interface RenderAction {
        void renderMain(NTxNode p, NTxNodeRendererContext renderContext, NTxNodeCustomBuilderContext buildContext);
    }

    interface RenderTextAction {
        void buildText(String text, NTxTextOptions options, NTxNode p, NTxNodeRendererContext renderContext, NTxTextRendererBuilder builder, NTxNodeCustomBuilderContext buildContext);
    }

    interface RenderConvertAction {
        NTxNode convert(NTxNode p, NTxNodeRendererContext ctx, NTxNodeCustomBuilderContext buildContext);
    }

    interface RenderEmbeddedTextAction {
        List<NTxTextToken> parseImmediate(NReservedSimpleCharQueue queue, NTxNodeRendererContext renderContext, NTxNodeCustomBuilderContext buildContext);
    }

    interface SizeRequirementsAction {
        NTxSizeRequirements sizeRequirements(NTxNode p, NTxNodeRendererContext renderContext, NTxNodeCustomBuilderContext buildContext);
    }

    interface SelfBoundsAction {
        NTxBounds2 selfBounds(NTxNode p, NTxNodeRendererContext renderContext, NTxNodeCustomBuilderContext buildContext);
    }

    interface ProcessParamAction {
        boolean processParam(NTxArgumentReader info, NTxNodeCustomBuilderContext buildContext);
    }

    interface ProcessNodeAction {
        void processNode(NTxAllArgumentReader info, NTxNodeCustomBuilderContext buildContext);
    }
}
