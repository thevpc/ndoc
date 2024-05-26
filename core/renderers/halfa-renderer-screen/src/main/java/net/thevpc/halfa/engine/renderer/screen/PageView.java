package net.thevpc.halfa.engine.renderer.screen;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextImpl;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.nuts.NSession;

import javax.swing.*;
import java.awt.*;

public class PageView extends JComponent {
    public static Dimension REF_SIZE = new Dimension(1024, 800);
    private HNode page;
    private int index;
    private String uuid;
    private DocumentView documentView;
    private RenderFactoryManager renderFactoryManager;

    public PageView(HNode page, String uuid, int index, DocumentView documentView) {
        this.page = page;
        this.index = index;
        this.uuid = uuid;
        this.documentView = documentView;
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

        HNodeRendererContext ctx = new MyHPartRendererContextImpl(g2d, size,this.documentView.session());
        //paintPagePart(computeParts(), ctx);
        for (HNode child : ((HContainer)page).children()) {
            render(child, ctx);
        }
    }

//    public HNode computeParts() {
//        List<HNode> parts = page.children();
//
//        return documentView.engine().documentFactory().stack(0, 0, parts.toArray(new HNode[0]));
//    }

    public HSizeRequirements render(HNode p, HNodeRendererContext ctx) {
        HNodeRenderer r = documentView.getRenderFactoryManager().renderer(p.type()).get();
        return r.render(p, ctx);
    }


    private class MyHPartRendererContextImpl extends HPartRendererContextImpl {
        public MyHPartRendererContextImpl(Graphics2D g2d, Dimension size, NSession session) {
            super(g2d, size, new Bounds2(0, 0, size.getWidth(), size.getHeight()),session);
        }

        @Override
        public HSizeRequirements rootRender(HNode p, HNodeRendererContext ctx) {
            return PageView.this.render(p,ctx);
        }

        @Override
        public HEngine engine() {
            return documentView.engine();
        }
    }

    public HNode getPage() {
        return page;
    }
}
