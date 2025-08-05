package net.thevpc.ntexup.engine.renderer;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.elem2d.*;
import net.thevpc.ntexup.api.document.node.NTxNode;
import net.thevpc.ntexup.api.document.style.NTxProperties;
import net.thevpc.ntexup.api.eval.NTxValueByName;
import net.thevpc.ntexup.api.eval.NTxVar;
import net.thevpc.ntexup.api.eval.NTxVarProvider;
import net.thevpc.ntexup.api.util.NTxUtils;
import net.thevpc.ntexup.api.renderer.NTxGraphics;
import net.thevpc.ntexup.api.renderer.NTxNodeRendererContext;
import net.thevpc.ntexup.api.util.NTxSizeRef;
import net.thevpc.ntexup.engine.util.NTxNodeRendererUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;


public abstract class NTxNodeRendererContextBaseBase implements NTxNodeRendererContext {
    private NTxVarProvider nTxVarProvider = new NTxVarProvider() {
        @Override
        public NOptional<NTxVar> findVar(String varName, NTxNode node) {
            switch (varName) {
                case "selfBounds": {
                    return NOptional.of(new NTxVar() {
                        @Override
                        public NElement get() {
                            NTxBounds2 nTxBounds2 = NTxNodeRendererContextBaseBase.this.selfBounds(node);
                            return NTxUtils.toElement(nTxBounds2);
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
    public NTxBounds2 selfBounds(NTxNode e) {
        return engine().renderManager().getRenderer(e.type()).get().selfBounds(e, this);
    }

    @Override
    public NTxBounds2 defaultSelfBounds(NTxNode e) {
        return NTxValueByName.selfBounds(e, null, null, this);
    }

    @Override
    public NTxNodeRendererContext withDefaultStyles(NTxNode node, NTxProperties defaultStyles) {
        return new NTxNodeRendererContextDelegate(node, this, null, defaultStyles, isDry(), null);
    }

    @Override
    public NTxNodeRendererContext withBounds(NTxNode t, NTxBounds2 bounds2) {
        return new NTxNodeRendererContextDelegate(t, this, bounds2, null, isDry(), null);
    }

    @Override
    public NTxNodeRendererContext dryMode() {
        return new NTxNodeRendererContextDelegate(null, this, null, null, true, null);
    }

    @Override
    public NTxNodeRendererContext withGraphics(NTxGraphics graphics) {
        return new NTxNodeRendererContextDelegate(null, this, null, null, isDry(), graphics);
    }

    @Override
    public NTxVarProvider varProvider() {
        return nTxVarProvider;
    }

    @Override
    public NTxSizeRef sizeRef() {
        NTxBounds2 b = getBounds();
        NTxBounds2 gb = getGlobalBounds();
        return new NTxSizeRef(
                b.getWidth(), b.getHeight(),
                gb.getWidth(), gb.getHeight()
        );
    }


    @Override
    public NOptional<Paint> getColorProperty(String propName, NTxNode t) {
        return NTxValueByName.getColorProperty(propName, t, this);
    }

    @Override
    public NTxDouble2 getOrigin(NTxNode t, NTxDouble2 a) {
        return NTxValueByName.getOrigin(t, this, a);
    }

    @Override
    public NTxDouble2 getPosition(NTxNode t, NTxDouble2 a) {
        return NTxValueByName.getPosition(t, this, a);
    }

    @Override
    public NElement getStroke(NTxNode t) {
        return NTxValueByName.getStroke(t, this);
    }

    @Override
    public NTxBounds2 selfBounds(NTxNode t, NTxDouble2 selfSize, NTxDouble2 minSize) {
        return NTxValueByName.selfBounds(t, selfSize, minSize, this);
    }

    @Override
    public boolean isVisible(NTxNode t) {
        return NTxValueByName.isVisible(t, this);
    }

    @Override
    public double getFontSize(NTxNode t) {
        return NTxValueByName.getFontSize(t, this);
    }

    @Override
    public String getFontFamily(NTxNode t) {
        return NTxValueByName.getFontFamily(t, this);
    }

    @Override
    public boolean isFontUnderlined(NTxNode t) {
        return NTxValueByName.isFontUnderlined(t, this);
    }

    @Override
    public boolean isFontStrike(NTxNode t) {
        return NTxValueByName.isFontStrike(t, this);
    }

    @Override
    public boolean isFontBold(NTxNode t) {
        return NTxValueByName.isFontBold(t, this);
    }

    @Override
    public boolean isFontItalic(NTxNode t) {
        return NTxValueByName.isFontItalic(t, this);
    }

    @Override
    public Font getFont(NTxNode t) {
        return NTxValueByName.getFont(t, this);
    }

    @Override
    public NTxDouble2 getRoundCornerArcs(NTxNode t) {
        return NTxValueByName.getRoundCornerArcs(t, this);
    }

    @Override
    public int getColSpan(NTxNode t) {
        return NTxValueByName.getColSpan(t, this);
    }

    @Override
    public int getRowSpan(NTxNode t) {
        return NTxValueByName.getRowSpan(t, this);
    }

    @Override
    public Boolean get3D(NTxNode t) {
        return NTxValueByName.get3D(t, this);
    }

    @Override
    public Boolean getRaised(NTxNode t) {
        return NTxValueByName.getRaised(t, this);
    }

    @Override
    public NOptional<NTxShadow> readStyleAsShadow(NTxNode p, String s) {
        return NTxValueByName.readStyleAsShadow(p, s, this);
    }

    @Override
    public Paint getForegroundColor(NTxNode p, boolean force) {
        return NTxValueByName.getForegroundColor(p, this, force);
    }

    @Override
    public Paint resolveGridColor(NTxNode p) {
        return NTxValueByName.resolveGridColor(p, this);
    }

    @Override
    public Paint resolveBackgroundColor(NTxNode p) {
        return NTxValueByName.resolveBackgroundColor(p, this);
    }

    @Override
    public boolean isDrawContour(NTxNode p) {
        return NTxValueByName.isDrawContour(p, this);
    }

    @Override
    public boolean requireDrawGrid(NTxNode p) {
        return NTxValueByName.requireDrawGrid(p, this);
    }

    @Override
    public boolean requireFillBackground(NTxNode p) {
        return NTxValueByName.requireFillBackground(p, this);
    }


    @Override
    public int getColumns(NTxNode p) {
        return NTxValueByName.getColumns(p, this);
    }

    @Override
    public int getRows(NTxNode p) {
        return NTxValueByName.getRows(p, this);
    }

    public boolean isDebug(NTxNode p) {
        return NTxValueByName.isDebug(p, this);
    }

    @Override
    public int getDebugLevel(NTxNode p) {
        return NTxValueByName.getDebugLevel(p, this);
    }

    @Override
    public Color getDebugColor(NTxNode p) {
        return NTxValueByName.getDebugColor(p, this);
    }

    @Override
    public NOptional<NTxPoint2D> getStyleAsShadowDistance(Object sv) {
        return NTxValueByName.getStyleAsShadowDistance(sv, this);
    }


    @Override
    public NTxSizeD mapDim(NTxSizeD d, NTxSizeD base) {
        return NTxNodeRendererUtils.mapDim(d, base);

    }

    @Override
    public Stroke resolveStroke(NTxNode t) {
        return NTxNodeRendererUtils.resolveStroke(t, graphics(), this);

    }

    @Override
    public boolean withStroke(NTxNode t, Runnable r) {
        return NTxNodeRendererUtils.withStroke(t, graphics(), this, r);

    }

    @Override
    public boolean applyStroke(NTxNode t) {
        return NTxNodeRendererUtils.applyStroke(t, graphics(), this);

    }

    @Override
    public void applyFont(NTxNode t) {
        NTxNodeRendererUtils.applyFont(t,graphics(), this);

    }

    @Override
    public NTxSizeD mapDim(double w, double h) {
        return NTxNodeRendererUtils.mapDim(w,h, this);

    }

    @Override
    public NTxBounds2 bounds(NTxNode t, NTxNodeRendererContext ctx) {
        return NTxNodeRendererUtils.bounds(t, this);

    }

    @Override
    public boolean applyForeground(NTxNode t, boolean force) {
        return NTxNodeRendererUtils.applyForeground(t,graphics(), this, force);

    }

    @Override
    public boolean applyBackgroundColor(NTxNode t) {
        return NTxNodeRendererUtils.applyBackgroundColor(t,graphics(), this);

    }

    @Override
    public boolean applyGridColor(NTxNode t, boolean force) {
        return NTxNodeRendererUtils.applyGridColor(t,graphics(), this, force);

    }


    @Override
    public void paintDebugBox(NTxNode t, NTxBounds2 a, boolean force) {
        NTxNodeRendererUtils.paintDebugBox(t, this,graphics(), a,force);

    }

    @Override
    public void paintDebugBox(NTxNode t, NTxBounds2 a) {
        NTxNodeRendererUtils.paintDebugBox(t, this,graphics(), a);
    }

    @Override
    public NOptional<Color> colorFromPaint(Paint p) {
        return NTxNodeRendererUtils.colorFromPaint(p);
    }

    @Override
    public void paintBorderLine(NTxNode t, NTxBounds2 a) {
        NTxNodeRendererUtils.paintBorderLine(t, this,graphics(), a);
    }

    @Override
    public void paintBackground(NTxNode t, NTxBounds2 a) {
        NTxNodeRendererUtils.paintBackground(t, this,graphics(), a);
    }
}
