package net.thevpc.ndoc.engine.renderer.page;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocSizeRef;

import net.thevpc.nuts.elem.NElement;

import java.awt.*;

public class NDocPageRenderer extends NDocNodeRendererBase {
    public NDocPageRenderer() {
        super(NDocNodeType.PAGE);
    }

    @Override
    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        NDocBounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);

         drawBackground(ctx.graphics(), ctx, b);
//        drawGrid(ctx.graphics(), b);

        for (NDocNode child : p.children()) {
            ctx.render(child, ctx);
        }
        //perhaps add page sum ??
    }

    private void drawGrid(NDocGraphics g, NDocBounds2 b) {
        int width = b.getWidth().intValue();
        int height = b.getHeight().intValue();
        Color color = Color.gray;
        g.setColor(color);
        NElement rowsSize = NElement.ofDouble(10, "%");
        NElement columnsSize = NElement.ofDouble(10, "%");

        Stroke os = g.getStroke();
        Stroke dashed = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{9}, 0);

        g.setStroke(dashed);

        NDocSizeRef sizeRef = new NDocSizeRef(b.getWidth(), b.getHeight(), b.getWidth(), b.getHeight());
        double rowsSizeEff = sizeRef.x(rowsSize).get();
        double columnsSizeEff = sizeRef.y(columnsSize).get();

        int rowsCount = (int) (height * 100 / rowsSizeEff + 0.5);
        int columnsCount = (int) (width * 100 / columnsSizeEff + 0.5);

        for (int i = 0; i < rowsCount; i++) {
            g.drawLine(0, i * rowsSizeEff,
                    width, i * rowsSizeEff
            );
        }
        for (int i = 0; i < columnsCount; i++) {
            g.drawLine(i * columnsSizeEff, 0,
                    i * columnsSizeEff, height
            );
        }
        g.setStroke(os);
    }

    private void drawBackground(NDocGraphics g, NDocNodeRendererContext ctx, NDocBounds2 b) {

//        g2d.setBackground(Color.white);
//        g2d.clearRect(0, 0, size.width, size.height);
        int width = b.getWidth().intValue();
        int height = b.getHeight().intValue();
        Color startColor = Color.white;
        Color endColor = new Color(0xa3a3a3);
        GradientPaint gradient = new GradientPaint(0, 0, startColor, width, height, endColor);
        g.setPaint(gradient);
        g.fill(new Rectangle(0, 0, width, height));
    }
}
