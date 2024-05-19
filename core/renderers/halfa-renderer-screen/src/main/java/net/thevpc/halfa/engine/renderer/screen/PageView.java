package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.model.*;
import net.thevpc.halfa.engine.renderer.screen.parts.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageView extends JComponent {
    public static Dimension REF_SIZE = new Dimension(800, 600);
    private HPage page;
    private int index;
    private String uuid;
    private DocumentView documentView;
    private Map<HDocumentItemType, AbstractHPartRenderer> renderers = new HashMap<>();

    public PageView(HPage page, String uuid, int index, DocumentView documentView) {
        this.page = page;
        this.index = index;
        this.uuid = uuid;
        this.documentView = documentView;
        renderers.put(HDocumentItemType.TEXT, new HTextRenderer());
        renderers.put(HDocumentItemType.RECTANGLE, new HRectangleRenderer());
        renderers.put(HDocumentItemType.ELLIPSE, new HEllipseRenderer());
        renderers.put(HDocumentItemType.POLYGON, new HPolygonRenderer());
        renderers.put(HDocumentItemType.POLYLINE, new HPolylineRenderer());
        renderers.put(HDocumentItemType.LINE, new HLineRenderer());
        renderers.put(HDocumentItemType.ARC, new HArcRenderer());
        renderers.put(HDocumentItemType.LATEX_EQUATION, new HLatexRenderer());
        renderers.put(HDocumentItemType.IMAGE, new HImageRenderer());
        renderers.put(HDocumentItemType.CONTAINER, new HContainerRenderer());
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
        g2d.setBackground(Color.lightGray);
        g2d.clearRect(0, 0, size.width, size.height);
        HPartRendererContext ctx = new HPartRendererContextImpl(g2d,
                new Point2D.Double(0, 0),
                size) {
            @Override
            public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
                return PageView.this.paintPagePart(p, ctx);
            }


        };
        paintPagePart(computeParts(), ctx);
    }

    public HPagePart computeParts() {
        List<HPagePart> parts = page.getParts();

        return documentView.getHalfaEngine().factory().container(0,0,parts.toArray(new HPagePart[0]));
    }

    public Rectangle2D.Double paintPagePart(HPagePart p, HPartRendererContext ctx) {
        AbstractHPartRenderer r = renderers.get(p.type());
        if (r == null) {
            throw new IllegalArgumentException("missing renderer for " + p.type());
        }
        return r.paintPagePart(p, ctx);
    }


}
