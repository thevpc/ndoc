package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.elem2d.*;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NDocProperties;
import net.thevpc.ntexup.api.eval.NDocValueByName;
import net.thevpc.ntexup.api.eval.NDocVar;
import net.thevpc.ntexup.api.eval.NDocVarProvider;
import net.thevpc.ntexup.api.util.NDocUtils;
import net.thevpc.ntexup.api.renderer.NDocGraphics;
import net.thevpc.ntexup.api.renderer.NDocNodeRendererContext;
import net.thevpc.ntexup.api.util.NDocSizeRef;
import net.thevpc.ntexup.engine.util.NDocNodeRendererUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;


public abstract class NDocNodeRendererContextBaseBase implements NDocNodeRendererContext {
    private NDocVarProvider nDocVarProvider = new NDocVarProvider() {
        @Override
        public NOptional<NDocVar> findVar(String varName, NTxNode node) {
            switch (varName) {
                case "selfBounds": {
                    return NOptional.of(new NDocVar() {
                        @Override
                        public NElement get() {
                            NDocBounds2 nDocBounds2 = NDocNodeRendererContextBaseBase.this.selfBounds(node);
                            return NDocUtils.toElement(nDocBounds2);
                        }
                    });
                }
            }
            return NOptional.ofNamedEmpty("var " + varName);
        }
    };
    public void render(NTxNode p) {
        render(p, this);
    }

    @Override
    public NTxDocumentFactory documentFactory() {
        return engine().documentFactory();
    }

    @Override
    public NDocBounds2 selfBounds(NTxNode e) {
        return engine().renderManager().getRenderer(e.type()).get().selfBounds(e, this);
    }

    @Override
    public NDocBounds2 defaultSelfBounds(NTxNode e) {
        return NDocValueByName.selfBounds(e, null, null, this);
    }

    @Override
    public NDocNodeRendererContext withDefaultStyles(NTxNode node, NDocProperties defaultStyles) {
        return new NDocNodeRendererContextDelegate(node, this, null, defaultStyles, isDry(), null);
    }

    @Override
    public NDocNodeRendererContext withBounds(NTxNode t, NDocBounds2 bounds2) {
        return new NDocNodeRendererContextDelegate(t, this, bounds2, null, isDry(), null);
    }

    @Override
    public NDocNodeRendererContext dryMode() {
        return new NDocNodeRendererContextDelegate(null, this, null, null, true, null);
    }

    @Override
    public NDocNodeRendererContext withGraphics(NDocGraphics graphics) {
        return new NDocNodeRendererContextDelegate(null, this, null, null, isDry(), graphics);
    }

    @Override
    public NDocVarProvider varProvider() {
        return nDocVarProvider;
    }

    @Override
    public NDocSizeRef sizeRef() {
        NDocBounds2 b = getBounds();
        NDocBounds2 gb = getGlobalBounds();
        return new NDocSizeRef(
                b.getWidth(), b.getHeight(),
                gb.getWidth(), gb.getHeight()
        );
    }


    @Override
    public NOptional<Paint> getColorProperty(String propName, NTxNode t) {
        return NDocValueByName.getColorProperty(propName, t, this);
    }

    @Override
    public NDocDouble2 getOrigin(NTxNode t, NDocDouble2 a) {
        return NDocValueByName.getOrigin(t, this, a);
    }

    @Override
    public NDocDouble2 getPosition(NTxNode t, NDocDouble2 a) {
        return NDocValueByName.getPosition(t, this, a);
    }

    @Override
    public NElement getStroke(NTxNode t) {
        return NDocValueByName.getStroke(t, this);
    }

    @Override
    public NDocBounds2 selfBounds(NTxNode t, NDocDouble2 selfSize, NDocDouble2 minSize) {
        return NDocValueByName.selfBounds(t, selfSize, minSize, this);
    }

    @Override
    public boolean isVisible(NTxNode t) {
        return NDocValueByName.isVisible(t, this);
    }

    @Override
    public double getFontSize(NTxNode t) {
        return NDocValueByName.getFontSize(t, this);
    }

    @Override
    public String getFontFamily(NTxNode t) {
        return NDocValueByName.getFontFamily(t, this);
    }

    @Override
    public boolean isFontUnderlined(NTxNode t) {
        return NDocValueByName.isFontUnderlined(t, this);
    }

    @Override
    public boolean isFontStrike(NTxNode t) {
        return NDocValueByName.isFontStrike(t, this);
    }

    @Override
    public boolean isFontBold(NTxNode t) {
        return NDocValueByName.isFontBold(t, this);
    }

    @Override
    public boolean isFontItalic(NTxNode t) {
        return NDocValueByName.isFontItalic(t, this);
    }

    @Override
    public Font getFont(NTxNode t) {
        return NDocValueByName.getFont(t, this);
    }

    @Override
    public NDocDouble2 getRoundCornerArcs(NTxNode t) {
        return NDocValueByName.getRoundCornerArcs(t, this);
    }

    @Override
    public int getColSpan(NTxNode t) {
        return NDocValueByName.getColSpan(t, this);
    }

    @Override
    public int getRowSpan(NTxNode t) {
        return NDocValueByName.getRowSpan(t, this);
    }

    @Override
    public Boolean get3D(NTxNode t) {
        return NDocValueByName.get3D(t, this);
    }

    @Override
    public Boolean getRaised(NTxNode t) {
        return NDocValueByName.getRaised(t, this);
    }

    @Override
    public NOptional<Shadow> readStyleAsShadow(NTxNode p, String s) {
        return NDocValueByName.readStyleAsShadow(p, s, this);
    }

    @Override
    public Paint getForegroundColor(NTxNode p, boolean force) {
        return NDocValueByName.getForegroundColor(p, this, force);
    }

    @Override
    public Paint resolveGridColor(NTxNode p) {
        return NDocValueByName.resolveGridColor(p, this);
    }

    @Override
    public Paint resolveBackgroundColor(NTxNode p) {
        return NDocValueByName.resolveBackgroundColor(p, this);
    }

    @Override
    public boolean isDrawContour(NTxNode p) {
        return NDocValueByName.isDrawContour(p, this);
    }

    @Override
    public boolean requireDrawGrid(NTxNode p) {
        return NDocValueByName.requireDrawGrid(p, this);
    }

    @Override
    public boolean requireFillBackground(NTxNode p) {
        return NDocValueByName.requireFillBackground(p, this);
    }


    @Override
    public int getColumns(NTxNode p) {
        return NDocValueByName.getColumns(p, this);
    }

    @Override
    public int getRows(NTxNode p) {
        return NDocValueByName.getRows(p, this);
    }

    public boolean isDebug(NTxNode p) {
        return NDocValueByName.isDebug(p, this);
    }

    @Override
    public int getDebugLevel(NTxNode p) {
        return NDocValueByName.getDebugLevel(p, this);
    }

    @Override
    public Color getDebugColor(NTxNode p) {
        return NDocValueByName.getDebugColor(p, this);
    }

    @Override
    public NOptional<NDocPoint2D> getStyleAsShadowDistance(Object sv) {
        return NDocValueByName.getStyleAsShadowDistance(sv, this);
    }


    @Override
    public SizeD mapDim(SizeD d, SizeD base) {
        return NDocNodeRendererUtils.mapDim(d, base);

    }

    @Override
    public Stroke resolveStroke(NTxNode t) {
        return NDocNodeRendererUtils.resolveStroke(t, graphics(), this);

    }

    @Override
    public boolean withStroke(NTxNode t, Runnable r) {
        return NDocNodeRendererUtils.withStroke(t, graphics(), this, r);

    }

    @Override
    public boolean applyStroke(NTxNode t) {
        return NDocNodeRendererUtils.applyStroke(t, graphics(), this);

    }

    @Override
    public void applyFont(NTxNode t) {
        NDocNodeRendererUtils.applyFont(t,graphics(), this);

    }

    @Override
    public SizeD mapDim(double w, double h) {
        return NDocNodeRendererUtils.mapDim(w,h, this);

    }

    @Override
    public NDocBounds2 bounds(NTxNode t, NDocNodeRendererContext ctx) {
        return NDocNodeRendererUtils.bounds(t, this);

    }

    @Override
    public boolean applyForeground(NTxNode t, boolean force) {
        return NDocNodeRendererUtils.applyForeground(t,graphics(), this, force);

    }

    @Override
    public boolean applyBackgroundColor(NTxNode t) {
        return NDocNodeRendererUtils.applyBackgroundColor(t,graphics(), this);

    }

    @Override
    public boolean applyGridColor(NTxNode t, boolean force) {
        return NDocNodeRendererUtils.applyGridColor(t,graphics(), this, force);

    }


    @Override
    public void paintDebugBox(NTxNode t, NDocBounds2 a, boolean force) {
        NDocNodeRendererUtils.paintDebugBox(t, this,graphics(), a,force);

    }

    @Override
    public void paintDebugBox(NTxNode t, NDocBounds2 a) {
        NDocNodeRendererUtils.paintDebugBox(t, this,graphics(), a);
    }

    @Override
    public NOptional<Color> colorFromPaint(Paint p) {
        return NDocNodeRendererUtils.colorFromPaint(p);
    }

    @Override
    public void paintBorderLine(NTxNode t, NDocBounds2 a) {
        NDocNodeRendererUtils.paintBorderLine(t, this,graphics(), a);
    }

    @Override
    public void paintBackground(NTxNode t, NDocBounds2 a) {
        NDocNodeRendererUtils.paintBackground(t, this,graphics(), a);
    }
}
