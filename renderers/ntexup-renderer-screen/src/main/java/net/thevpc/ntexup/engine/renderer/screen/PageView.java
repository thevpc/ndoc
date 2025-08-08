package net.thevpc.ntexup.engine.renderer.screen;

import net.thevpc.ntexup.api.engine.NTxCompiledDocument;
import net.thevpc.ntexup.api.engine.NTxCompiledPage;
import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.renderer.NTxNodeRenderer;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.nuts.time.NChronometer;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class PageView extends JComponent {
    public static Dimension REF_SIZE = new Dimension(1024, 768);
    private NTxCompiledPage page;
    private long pageStartTime;
    private String uuid;
    private NTxEngine engine;
    private NTxCompiledDocument document;

    public PageView(
            NTxCompiledDocument document,
            NTxCompiledPage page,
            NTxEngine engine
    ) {
        this.document = document;
        this.page = page;
        this.uuid = UUID.randomUUID().toString();
        this.engine = engine;

    }

    public NTxEngine engine() {
        return engine;
    }

    public JComponent component() {
        return this;
    }

    public String id() {
        return uuid;
    }

    public int index() {
        return page.index();
    }

    void onHide() {

    }

    synchronized void onShow() {
        page.compiledPage();
        this.pageStartTime = System.currentTimeMillis();
    }

    public boolean isLoading() {
        return !page.isCompiled() || !document.isCompiled();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    public void paintComponent(Graphics g) {
        NChronometer c = NChronometer.startNow();
        super.paintComponent(g);
        Dimension size = getSize();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        NTxNodeRendererContext ctx = new ScreenNTxPartRendererContext(this,
                engine.createGraphics(g2d)
                , size, pageStartTime);
        if (page.isCompiled()) {
            NTxNode p = page.compiledPage();
            NTxNodeRenderer r = engine.getRenderer(p.type()).get();
            r.render(p, ctx);
        }
        c.stop();
        //System.out.println("NChronometer::paintComponent "+c);
    }


    public void render(NTxNode p, NTxNodeRendererContext ctx) {

    }

    public Object source() {
        return page.source();
    }

    public NTxCompiledPage page() {
        return page;
    }
}
