package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextImpl;
import net.thevpc.halfa.engine.renderer.screen.renderers.HGraphicsImpl;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.renderer.HNodeRendererManager;
import net.thevpc.halfa.spi.util.HSizeRef;
import net.thevpc.nuts.NSession;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class PageView extends JComponent {
    public static Dimension REF_SIZE = new Dimension(1000, 1000);
    private HNode page;
    private int index;
    private String uuid;
    private HNodeRendererManager rendererManager;
    private HEngine engine;
    private NSession session;
    private HMessageList messages;

    public PageView(HNode page, int index,
                    HEngine engine,
                    HNodeRendererManager rendererManager,
                    HMessageList messages,
                    NSession session
    ) {
        this.page = page;
        this.index = index;
        this.uuid = UUID.randomUUID().toString();
        this.rendererManager = rendererManager;
        this.engine = engine;
        this.messages = messages;
        this.session = session;
    }

    public JComponent component() {
        return this;
    }

    public String id() {
        return uuid;
    }

    public int index() {
        return index;
    }

    void onHide() {

    }

    void onShow() {

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension size = getSize();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        HGraphics hg = new HGraphicsImpl(g2d);

        drawBackground(hg);
        if (false) {
            drawGrid(hg);
        }
        HNodeRendererContext ctx = new MyHPartRendererContextImpl(g2d, size, session, messages);
        for (HNode child : page.children()) {
            render(child, ctx);
        }
    }

    private void drawBackground(HGraphics g) {
//        g2d.setBackground(Color.white);
//        g2d.clearRect(0, 0, size.width, size.height);
        Dimension size = getSize();
        Color startColor = Color.white;
        Color endColor = new Color(0xa3a3a3);
        GradientPaint gradient = new GradientPaint(0, 0, startColor, size.width, size.height, endColor);
        g.setPaint(gradient);
        g.fill(new Rectangle(0, 0, size.width, size.height));
    }

    private void drawGrid(HGraphics g) {
        Dimension size = getSize();
        Color color = Color.gray;
        g.setColor(color);
        TsonElement rowsSize = Tson.ofDouble(10,"%");
        TsonElement columnsSize = Tson.ofDouble(10,"%");

        Stroke os = g.getStroke();
        Stroke dashed = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{9}, 0);

        g.setStroke(dashed);

        HSizeRef sizeRef = new HSizeRef(size.getWidth(),size.getHeight(),size.getWidth(),size.getHeight());
        double rowsSizeEff = sizeRef.x(rowsSize).get();
        double columnsSizeEff = sizeRef.y(columnsSize).get();

        int rowsCount = (int) (size.height * 100 / rowsSizeEff + 0.5);
        int columnsCount = (int) (size.width * 100 / columnsSizeEff + 0.5);

        for (int i = 0; i < rowsCount; i++) {
            g.drawLine(0, i * rowsSizeEff,
                    size.width, i * rowsSizeEff
            );
        }
        for (int i = 0; i < columnsCount; i++) {
            g.drawLine(i * columnsSizeEff, 0,
                    i * columnsSizeEff, size.height
            );
        }
        g.setStroke(os);
    }


    public void render(HNode p, HNodeRendererContext ctx) {
        if (p.isTemplate()) {
            return;
        }
        HNodeRenderer r = rendererManager.getRenderer(p.type()).get();
        r.render(p, ctx);
    }


    private class MyHPartRendererContextImpl extends HPartRendererContextImpl {
        public MyHPartRendererContextImpl(Graphics2D g2d, Dimension size, NSession session, HMessageList messages) {
            super(g2d, size, new Bounds2(0, 0, size.getWidth(), size.getHeight()), session, messages);
        }

        @Override
        public HNodeRendererManager manager() {
            return rendererManager;
        }

        @Override
        public void render(HNode p, HNodeRendererContext ctx) {
            PageView.this.render(p, ctx);
        }

        @Override
        public HEngine engine() {
            return engine;
        }
    }

    public HNode getPage() {
        return page;
    }
}
