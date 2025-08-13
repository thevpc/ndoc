package net.thevpc.ntexup.extension.plot2d;

import net.thevpc.ntexup.api.extension.NTxNodeBuilder;
import net.thevpc.ntexup.api.engine.NTxNodeCustomBuilderContext;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.node.NTxNodeType;
import net.thevpc.ntexup.api.parser.NTxAllArgumentReader;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.extension.plot2d.model.NTxDrawContext;
import net.thevpc.ntexup.extension.plot2d.model.NTxFunctionPlotInfo;

import java.util.List;


/**
 *
 */
public class NTxPlot2DBuilder implements NTxNodeBuilder {

    @Override
    public void build(NTxNodeCustomBuilderContext builderContext) {
        builderContext.id(NTxNodeType.PLOT2D)
                .parseParam()
                .named(
                        "x",
                        "majorGridSpacing",
                        "showMajorGrid",
                        "majorGridColor",
                        "majorGridStroke",

                        "minorGridSpacing",
                        "showMinorGrid",
                        "minorGridColor",
                        "minorGridStroke"
                ).end()
                .processChildren(this::processChildren)
                .renderComponent(this::renderMain)
        ;
    }

    public void processChildren(NTxAllArgumentReader info, NTxNodeCustomBuilderContext buildContext) {
        List<NTxFunctionPlotInfo> all = new NTxFunctionPlotInfoLoader().loadBody(info.element(), buildContext);
        info.node().setUserObject("def", all);
    }


    public void renderMain(NTxNode p, NTxNodeRendererContext renderContext, NTxNodeCustomBuilderContext builderContext) {
        NTxDrawContext drawContext = NTxDrawContextRenderCompiler.compile(p, renderContext);
        NTxJFreeChartHelper.drawCurves(p, renderContext, drawContext);
    }

}
