package net.thevpc.ndoc.engine.base.functions.colors;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.eval.*;
import net.thevpc.ndoc.api.extension.NDocFunction;
import net.thevpc.ndoc.engine.util.NDocColorUtils;
import net.thevpc.ndoc.engine.util.NDocElementUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class NDocFunctionLinearGradientColor implements NDocFunction {
    @Override
    public String name() {
        return "linearGradientColor";
    }

    @Override
    public NElement invoke(NDocFunctionArgs args, NDocFunctionContext context) {
        boolean cyclic = false;
        NDocDouble2 start = null;
        NDocDouble2 end = null;
        NDocBounds2 selfBounds = NDocValue.of(context.findVar("selfBounds").map(x->x.get()).orNull()).asBounds2().orNull();
        java.util.List<Color> colors = new ArrayList<>();
        for (NDocFunctionArg arg : args.args()) {
            NElement v = arg.eval();
            NDocValue vv = NDocValue.of(v);
            if(vv.isBoolean()) {
                cyclic = v.asBooleanValue().get();
            }else if(vv.isPoint2()){
                if(start==null){
                    start=vv.asDouble2().get();
                }else  if(end==null){
                    end=vv.asDouble2().get();
                }else{
                    context.messages().log(NMsg.ofC("%s: unexpected point arg %s", name(), v));
                }
            }else {
                NOptional<Color[]> c = vv.asColorArrayOrColor();
                if(c.isPresent()) {
                    for (Color color : c.get()) {
                        colors.add(color);
                    }
                }else{
                    context.messages().log(NMsg.ofC("%s: unexpected point arg %s", name(), v));
                }
            }
        }
        if (colors.isEmpty()) {
            context.messages().log(NMsg.ofC("%s: missing colors", name()));
            return NElement.ofNull();
        }
        if (colors.size() == 1) {
            return NDocElementUtils.toElement(colors.get(0));
        }
        if (start == null) {
            start = new NDocDouble2(0, 50);
        }
        if (end == null) {
            end = new NDocDouble2(100 - start.getX(), 100 - start.getY());
        }
        // should consider node size!!
        Point2D start2;
        Point2D end2;
        if(selfBounds!=null){
            start2=new Point2D.Double(
                    start.getX()/100*selfBounds.getWidth()+selfBounds.getX(),
                    start.getY()/100*selfBounds.getHeight()+selfBounds.getY()
            );
            end2=new Point2D.Double(
                    end.getX()/100*selfBounds.getWidth()+selfBounds.getX(),
                    end.getY()/100*selfBounds.getHeight()+selfBounds.getY()
            );
        }else{
            start2 = new Point2D.Double(start.getX(), start.getY());
            end2 = new Point2D.Double(end.getX(), end.getY());
        }

        return NDocElementUtils.toElement(NDocColorUtils.createLinearGradient(start2, end2, colors.toArray(new Color[0]), cyclic));
    }
}
