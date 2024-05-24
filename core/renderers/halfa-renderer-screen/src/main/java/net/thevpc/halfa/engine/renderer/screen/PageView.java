package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HPage;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextImpl;
import net.thevpc.halfa.engine.renderer.screen.renderers.containers.*;
import net.thevpc.halfa.engine.renderer.screen.renderers.fillers.HFillerRenderer;
import net.thevpc.halfa.engine.renderer.screen.renderers.fillers.HVoidRenderer;
import net.thevpc.halfa.engine.renderer.screen.renderers.images.HImageRenderer;
import net.thevpc.halfa.engine.renderer.screen.renderers.shapes.*;
import net.thevpc.halfa.engine.renderer.screen.renderers.text.HLatexEquationRenderer;
import net.thevpc.halfa.engine.renderer.screen.renderers.text.HTextRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PageView extends JComponent {
    public static Dimension REF_SIZE = new Dimension(1024, 800);
    private HPage page;
    private int index;
    private String uuid;
    private DocumentView documentView;
    private Map<HNodeType, AbstractHPartRenderer> renderers = new HashMap<>();

    public PageView(HPage page, String uuid, int index, DocumentView documentView) {
        this.page = page;
        this.index = index;
        this.uuid = uuid;
        this.documentView = documentView;
        renderers.put(HNodeType.TEXT, new HTextRenderer());
        renderers.put(HNodeType.RECTANGLE, new HRectangleRenderer());
        renderers.put(HNodeType.ELLIPSE, new HEllipseRenderer());
        renderers.put(HNodeType.POLYGON, new HPolygonRenderer());
        renderers.put(HNodeType.POLYLINE, new HPolylineRenderer());
        renderers.put(HNodeType.LINE, new HLineRenderer());
        renderers.put(HNodeType.ARC, new HArcRenderer());
        renderers.put(HNodeType.EQUATION, new HLatexEquationRenderer());
        renderers.put(HNodeType.IMAGE, new HImageRenderer());
        renderers.put(HNodeType.GRID, new HGridContainerRenderer());
        renderers.put(HNodeType.FLOW, new HFlowContainerRenderer());
        renderers.put(HNodeType.STACK, new HStackContainerRenderer());
        renderers.put(HNodeType.FILLER, new HFillerRenderer());
        renderers.put(HNodeType.UNORDERED_LIST, new HUnorderedListRenderer());
        renderers.put(HNodeType.ORDERED_LIST, new HOrderedListRenderer());
        renderers.put(HNodeType.VOID, new HVoidRenderer());
    }

    public JComponent component() {
        return this;
    }

    public void showPage() {
        documentView.showPage(this);
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

//        g2d.setBackground(Color.white);
//        g2d.clearRect(0, 0, size.width, size.height);

        //background
        Color startColor = Color.white;
        Color endColor = new Color(0xa3a3a3);
        GradientPaint gradient = new GradientPaint(0, 0, startColor, size.width, size.height, endColor);
        g2d.setPaint(gradient);
        g2d.fill(new Rectangle(0,0,size.width,size.height));

        HPartRendererContext ctx = new MyHPartRendererContextImpl(g2d, size);
        //paintPagePart(computeParts(), ctx);
        for (HNode child : page.children()) {
            paintPagePart(child, ctx);
        }
    }

//    public HNode computeParts() {
//        List<HNode> parts = page.children();
//
//        return documentView.engine().documentFactory().stack(0, 0, parts.toArray(new HNode[0]));
//    }

    public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
        switch (p.type()) {
            case CTRL_ASSIGN: {
                return null;
            }
        }
        AbstractHPartRenderer r = renderers.get(p.type());
        if (r == null) {
            throw new IllegalArgumentException("missing renderer for " + p.type());
        }
        return r.paintPagePart(p, ctx);
    }


    private class MyHPartRendererContextImpl extends HPartRendererContextImpl {
        public MyHPartRendererContextImpl(Graphics2D g2d, Dimension size) {
            super(g2d, size, new Bounds2(0, 0, size.getWidth(), size.getHeight()));
        }

        @Override
        public Bounds2 paintPagePart(HNode p, HPartRendererContext ctx) {
            return PageView.this.paintPagePart(p, ctx);
        }

        @Override
        public HEngine engine() {
            return documentView.engine();
        }

        @Override
        public HDocumentFactory documentFactory() {
            return engine().documentFactory();
        }

        @Override
        public HSizeRequirements computeSizeRequirements(HNode p, HPartRendererContext ctx) {
            AbstractHPartRenderer r = renderers.get(p.type());
            if (r == null) {
                throw new IllegalArgumentException("missing renderer for " + p.type());
            }
            return r.computeSizeRequirements(p, ctx);
        }
    }

    public HPage getPage() {
        return page;
    }
}
