package net.thevpc.ndoc.extension.plot2d;

import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.elem.*;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.nuts.util.NStringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FunctionPlotInfoLoader {

    public GlobalPlotData loadGlobalPlotData(NDocNode p, NDocNodeRendererContext renderContext, NDocNodeCustomBuilderContext buildContext) {
        GlobalPlotData d = new GlobalPlotData();
        NDocEngine engine = renderContext.engine();
        class A {
            Double asDouble(String name, Double d) {
                return NDocValue.of(engine.evalExpression(p.getPropertyValue(name).orElse(NElement.ofDouble(d)), p, renderContext.varProvider())).asDouble().orElse(d);
            }

            Boolean asBoolean(String name, Boolean d) {
                return NDocValue.of(engine.evalExpression(p.getPropertyValue(name).orElse(NElement.ofBoolean(d)), p, renderContext.varProvider())).asBoolean().orElse(d);
            }

            Color asColor(String name, Color d) {
                return NDocValue.of(engine.evalExpression(p.getPropertyValue(name).orElse(NDocUtils.toElement(d)), p, renderContext.varProvider())).asColor().orElse(d);
            }

            Stroke asStroke(String name, Stroke d) {
                NElement ev = engine.evalExpression(p.getPropertyValue(name).orElse(NDocUtils.toElement(d)), p, renderContext.varProvider());
                if (ev != null && !ev.isNull()) {
                    Stroke stroke = renderContext.graphics().createStroke(ev);
                    if (stroke != null) {
                        return stroke;
                    }
                }
                return d;
            }
        }
        A a = new A();
        d.xmin = a.asDouble("xmin", -100.0);
        d.xmax = a.asDouble("xmax", +100.0);
        d.ymin = a.asDouble("ymin", null);
        d.ymax = a.asDouble("ymax", null);
        d.majorGridSpacing = a.asDouble("majorGridSpacing", 10.0);
        d.showMajorGrid = a.asBoolean("showMajorGrid", true);
        d.majorGridColor =a.asColor("majorGridColor", null);
        d.majorGridStroke =a.asStroke("majorGridStroke", null);
        d.minorGridSpacing = a.asDouble("minorGridSpacing", 1.0);
        d.showMinorGrid = a.asBoolean("showMinorGrid", true);
        d.minorGridColor =a.asColor("minorGridColor", null);
        d.minorGridStroke =a.asStroke("minorGridStroke", null);
        return d;
    }

    public java.util.List<FunctionPlotInfo> loadBody(NElement element, NDocNodeCustomBuilderContext buildContext) {
        NListContainerElement nListContainerElement = element.asListContainer().orNull();
        java.util.List<FunctionPlotInfo> all = new ArrayList<>();
        if (nListContainerElement != null && !nListContainerElement.isAnyUplet()) {
            List<NElement> c = nListContainerElement.children();
            for (NElement child : c) {
                if (child.isNamedObject()) {
                    FunctionPlotInfo a = load(child.asObject().get(), buildContext);
                    if (a != null) {
                        all.add(a);
                    }
                }else{
                    buildContext.engine().log().log(NMsg.ofC("unexpected plot child %s", NDocUtils.snippet(child)));
                }
            }
        }
        if (all.isEmpty()) {
            FunctionPlotInfo f = new FunctionPlotInfo();
            f.args=1;
            f.var1="x";
            f.plotType=PlotType.CURVE;
            f.fexpr=NElement.ofString("sin(x)");
            all.add(f);
        }
        return all;
    }

    private FunctionPlotInfo load(NObjectElement child, NDocNodeCustomBuilderContext buildContext) {
        FunctionPlotInfo i = new FunctionPlotInfo();
        switch (NNameFormat.VAR_NAME.format(NStringUtils.firstNonNull(child.name().orNull(),"").trim())) {
            case "curve": {
                i.plotType=PlotType.CURVE;
                break;
            }
            default: {
                buildContext.engine().log().log(NMsg.ofC("unexpected plot type %s", child.name().orNull()));
                return null;
            }
        }
        for (NElement e : child.children()) {
            if (e.isNamedPair()) {
                switch (e.asPair().get().key().asStringValue().get()) {
                    case "title": {
                        i.title = e.asPair().get().value();
                        break;
                    }
                    case "color": {
                        i.color = e.asPair().get().value();
                        break;
                    }
                    case "stroke": {
                        i.stroke = e.asPair().get().value();
                        break;
                    }
                    default: {
                        buildContext.engine().log().log(NMsg.ofC("unexpected function declaration %s", NDocUtils.snippet(e)));
                    }
                }
            } else if (e.isPair()) {
                NPairElement p = e.asPair().get();
                NElement k = p.key();
                if (k.isNamedUplet("f")) {
                    NUpletElement fk = k.asUplet().get();
                    if (fk.params().size() == 1 && fk.params().get(0).isName()) {
                        i.fexpr = p.value();
                        i.var1 = fk.params().get(0).asNameValue().get();
                        i.args = 1;
                    } else if (fk.params().size() == 2 && fk.params().get(0).isName() && fk.params().get(1).isName()) {
                        i.fexpr = p.value();
                        i.var1 = fk.params().get(0).asNameValue().get();
                        i.var2 = fk.params().get(1).asNameValue().get();
                        i.args = 2;
                    } else if (fk.params().size() == 3 && fk.params().get(0).isName() && fk.params().get(1).isName() && fk.params().get(2).isName()) {
                        i.fexpr = p.value();
                        i.var1 = fk.params().get(0).asNameValue().get();
                        i.var2 = fk.params().get(1).asNameValue().get();
                        i.var3 = fk.params().get(2).asNameValue().get();
                        i.args = 3;
                    } else if (fk.params().size() == 4 && fk.params().get(0).isName() && fk.params().get(1).isName() && fk.params().get(2).isName() && fk.params().get(3).isName()) {
                        i.fexpr = p.value();
                        i.var1 = fk.params().get(0).asNameValue().get();
                        i.var2 = fk.params().get(1).asNameValue().get();
                        i.var3 = fk.params().get(2).asNameValue().get();
                        i.var4 = fk.params().get(3).asNameValue().get();
                        i.args = 4;
                    } else {
                        buildContext.engine().log().log(NMsg.ofC("unexpected function declaration %s", NDocUtils.snippet(k)));
                    }
                } else {
                    buildContext.engine().log().log(NMsg.ofC("unexpected function declaration %s", NDocUtils.snippet(k)));
                }
            }
        }
        return i;
    }
}
