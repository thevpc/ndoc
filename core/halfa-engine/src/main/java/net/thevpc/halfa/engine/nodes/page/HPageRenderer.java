package net.thevpc.halfa.engine.nodes.page;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.engin.spibase.renderer.HNodeRendererBase;
import net.thevpc.halfa.spi.eval.HValueByName;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HSizeRef;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.awt.*;

public class HPageRenderer extends HNodeRendererBase {
    public HPageRenderer() {
        super(HNodeType.PAGE);
    }

    @Override
    public void renderMain(HNode p, HNodeRendererContext ctx) {
        Bounds2 b = HValueByName.selfBounds(p, null, null, ctx);

         drawBackground(ctx.graphics(), ctx, b);
//        drawGrid(ctx.graphics(), b);

        for (HNode child : p.children()) {
            ctx.render(child, ctx);
        }
        //perhaps add page sum ??
    }

    private void drawGrid(HGraphics g,Bounds2 b) {
        int width = b.getWidth().intValue();
        int height = b.getHeight().intValue();
        Color color = Color.gray;
        g.setColor(color);
        TsonElement rowsSize = Tson.ofDouble(10, "%");
        TsonElement columnsSize = Tson.ofDouble(10, "%");

        Stroke os = g.getStroke();
        Stroke dashed = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{9}, 0);

        g.setStroke(dashed);

        HSizeRef sizeRef = new HSizeRef(b.getWidth(), b.getHeight(), b.getWidth(), b.getHeight());
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

    private void drawBackground(HGraphics g, HNodeRendererContext ctx, Bounds2 b) {

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
