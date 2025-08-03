package net.thevpc.ndoc.engine.renderer;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.elem2d.*;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.eval.NDocVar;
import net.thevpc.ndoc.api.eval.NDocVarProvider;
import net.thevpc.ndoc.api.source.NDocResource;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocSizeRef;
import net.thevpc.ndoc.engine.util.NDocNodeRendererUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;


public abstract class NDocNodeRendererContextBaseBase implements NDocNodeRendererContext {
    private NDocVarProvider nDocVarProvider = new NDocVarProvider() {
        @Override
        public NOptional<NDocVar> findVar(String varName, NDocNode node) {
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
    public void render(NDocNode p) {
        render(p, this);
    }

    @Override
    public NDocDocumentFactory documentFactory() {
        return engine().documentFactory();
    }

    @Override
    public NDocBounds2 selfBounds(NDocNode e) {
        return engine().renderManager().getRenderer(e.type()).get().selfBounds(e, this);
    }

    @Override
    public NDocBounds2 defaultSelfBounds(NDocNode e) {
        return NDocValueByName.selfBounds(e, null, null, this);
    }

    @Override
    public NDocNodeRendererContext withDefaultStyles(NDocNode node, NDocProperties defaultStyles) {
        return new NDocNodeRendererContextDelegate(node, this, null, defaultStyles, isDry(), null);
    }

    @Override
    public NDocNodeRendererContext withBounds(NDocNode t, NDocBounds2 bounds2) {
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
    public NOptional<Paint> getColorProperty(String propName, NDocNode t) {
        return NDocValueByName.getColorProperty(propName, t, this);
    }

    @Override
    public NDocDouble2 getOrigin(NDocNode t, NDocDouble2 a) {
        return NDocValueByName.getOrigin(t, this, a);
    }

    @Override
    public NDocDouble2 getPosition(NDocNode t, NDocDouble2 a) {
        return NDocValueByName.getPosition(t, this, a);
    }

    @Override
    public NElement getStroke(NDocNode t) {
        return NDocValueByName.getStroke(t, this);
    }

    @Override
    public NDocBounds2 selfBounds(NDocNode t, NDocDouble2 selfSize, NDocDouble2 minSize) {
        return NDocValueByName.selfBounds(t, selfSize, minSize, this);
    }

    @Override
    public boolean isVisible(NDocNode t) {
        return NDocValueByName.isVisible(t, this);
    }

    @Override
    public double getFontSize(NDocNode t) {
        return NDocValueByName.getFontSize(t, this);
    }

    @Override
    public String getFontFamily(NDocNode t) {
        return NDocValueByName.getFontFamily(t, this);
    }

    @Override
    public boolean isFontUnderlined(NDocNode t) {
        return NDocValueByName.isFontUnderlined(t, this);
    }

    @Override
    public boolean isFontStrike(NDocNode t) {
        return NDocValueByName.isFontStrike(t, this);
    }

    @Override
    public boolean isFontBold(NDocNode t) {
        return NDocValueByName.isFontBold(t, this);
    }

    @Override
    public boolean isFontItalic(NDocNode t) {
        return NDocValueByName.isFontItalic(t, this);
    }

    @Override
    public Font getFont(NDocNode t) {
        return NDocValueByName.getFont(t, this);
    }

    @Override
    public NDocDouble2 getRoundCornerArcs(NDocNode t) {
        return NDocValueByName.getRoundCornerArcs(t, this);
    }

    @Override
    public int getColSpan(NDocNode t) {
        return NDocValueByName.getColSpan(t, this);
    }

    @Override
    public int getRowSpan(NDocNode t) {
        return NDocValueByName.getRowSpan(t, this);
    }

    @Override
    public Boolean get3D(NDocNode t) {
        return NDocValueByName.get3D(t, this);
    }

    @Override
    public Boolean getRaised(NDocNode t) {
        return NDocValueByName.getRaised(t, this);
    }

    @Override
    public NOptional<Shadow> readStyleAsShadow(NDocNode p, String s) {
        return NDocValueByName.readStyleAsShadow(p, s, this);
    }

    @Override
    public Paint getForegroundColor(NDocNode p, boolean force) {
        return NDocValueByName.getForegroundColor(p, this, force);
    }

    @Override
    public Paint resolveGridColor(NDocNode p) {
        return NDocValueByName.resolveGridColor(p, this);
    }

    @Override
    public Paint resolveBackgroundColor(NDocNode p) {
        return NDocValueByName.resolveBackgroundColor(p, this);
    }

    @Override
    public boolean isDrawContour(NDocNode p) {
        return NDocValueByName.isDrawContour(p, this);
    }

    @Override
    public boolean requireDrawGrid(NDocNode p) {
        return NDocValueByName.requireDrawGrid(p, this);
    }

    @Override
    public boolean requireFillBackground(NDocNode p) {
        return NDocValueByName.requireFillBackground(p, this);
    }


    @Override
    public int getColumns(NDocNode p) {
        return NDocValueByName.getColumns(p, this);
    }

    @Override
    public int getRows(NDocNode p) {
        return NDocValueByName.getRows(p, this);
    }

    public boolean isDebug(NDocNode p) {
        return NDocValueByName.isDebug(p, this);
    }

    @Override
    public int getDebugLevel(NDocNode p) {
        return NDocValueByName.getDebugLevel(p, this);
    }

    @Override
    public Color getDebugColor(NDocNode p) {
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
    public Stroke resolveStroke(NDocNode t) {
        return NDocNodeRendererUtils.resolveStroke(t, graphics(), this);

    }

    @Override
    public boolean withStroke(NDocNode t, Runnable r) {
        return NDocNodeRendererUtils.withStroke(t, graphics(), this, r);

    }

    @Override
    public boolean applyStroke(NDocNode t) {
        return NDocNodeRendererUtils.applyStroke(t, graphics(), this);

    }

    @Override
    public void applyFont(NDocNode t) {
        NDocNodeRendererUtils.applyFont(t,graphics(), this);

    }

    @Override
    public SizeD mapDim(double w, double h) {
        return NDocNodeRendererUtils.mapDim(w,h, this);

    }

    @Override
    public NDocBounds2 bounds(NDocNode t, NDocNodeRendererContext ctx) {
        return NDocNodeRendererUtils.bounds(t, this);

    }

    @Override
    public boolean applyForeground(NDocNode t, boolean force) {
        return NDocNodeRendererUtils.applyForeground(t,graphics(), this, force);

    }

    @Override
    public boolean applyBackgroundColor(NDocNode t) {
        return NDocNodeRendererUtils.applyBackgroundColor(t,graphics(), this);

    }

    @Override
    public boolean applyGridColor(NDocNode t, boolean force) {
        return NDocNodeRendererUtils.applyGridColor(t,graphics(), this, force);

    }


    @Override
    public void paintDebugBox(NDocNode t, NDocBounds2 a, boolean force) {
        NDocNodeRendererUtils.paintDebugBox(t, this,graphics(), a,force);

    }

    @Override
    public void paintDebugBox(NDocNode t, NDocBounds2 a) {
        NDocNodeRendererUtils.paintDebugBox(t, this,graphics(), a);
    }

    @Override
    public NOptional<Color> colorFromPaint(Paint p) {
        return NDocNodeRendererUtils.colorFromPaint(p);
    }

    @Override
    public void paintBorderLine(NDocNode t, NDocBounds2 a) {
        NDocNodeRendererUtils.paintBorderLine(t, this,graphics(), a);
    }

    @Override
    public void paintBackground(NDocNode t, NDocBounds2 a) {
        NDocNodeRendererUtils.paintBackground(t, this,graphics(), a);
    }
}
