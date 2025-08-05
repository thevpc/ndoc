package net.thevpc.ntexup.api.renderer;

import net.thevpc.ntexup.api.document.NTxDocumentFactory;
import net.thevpc.ntexup.api.document.elem2d.*;
import net.thevpc.ntexup.api.engine.NDocEngine;
import net.thevpc.ntexup.api.eval.NDocVarProvider;
import net.thevpc.ntexup.api.log.NDocLogger;
import net.thevpc.ntexup.api.document.node.*;
import net.thevpc.ntexup.api.document.style.*;

import net.thevpc.ntexup.api.document.NDocSizeRequirements;
import net.thevpc.ntexup.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ntexup.api.util.NDocSizeRef;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;

public interface NDocNodeRendererContext {
    String CAPABILITY_PRINT = "print";
    String CAPABILITY_ANIMATE = "animate";

    default NDocSizeRequirements sizeRequirementsOf(NTxNode p) {
        return manager().getRenderer(p.type()).get().sizeRequirements(p, this);
    }

    NDocBounds2 selfBounds(NTxNode e);

    NDocBounds2 defaultSelfBounds(NTxNode e);

    NDocNodeRendererManager manager();

    NDocLogger log();

    NDocNodeRendererContext dryMode();

    NDocVarProvider varProvider();

    long getPageStartTime();

    ImageObserver imageObserver();

    boolean isDry();

    NDocBounds2 getGlobalBounds();

    NDocGraphics graphics();

    NDocBounds2 getBounds();


    void render(NTxNode p);

    void render(NTxNode p, NDocNodeRendererContext ctx);

    NDocEngine engine();

    NTxDocumentFactory documentFactory();

    NOptional<NElement> computePropertyValue(NTxNode t, String s, String[] synonyms, NDocVarProvider varProvider);

    NOptional<NElement> computePropertyValue(NTxNode t, String s, String... synonyms);

    List<NTxProp> computeProperties(NTxNode t);

    NDocNodeRendererContext withDefaultStyles(NTxNode node, NDocProperties defaultStyles);

    NDocNodeRendererContext withBounds(NTxNode t, NDocBounds2 bounds2);

    NDocNodeRendererContext withGraphics(NDocGraphics graphics);

    NDocSizeRef sizeRef();

    boolean isPrint();

    boolean isAnimate();

    void repaint();

    Object getCapability(String name);

    boolean hasCapability(String name);

    public boolean isCapability(String name);

    void highlightNutsText(String lang, String rawText, NText parsedText, NTxNode p, NDocTextRendererBuilder result);

    NOptional<Paint> getColorProperty(String propName, NTxNode t);

    NDocDouble2 getOrigin(NTxNode t, NDocDouble2 a);

    NDocDouble2 getPosition(NTxNode t, NDocDouble2 a);

    NElement getStroke(NTxNode t);

    NDocBounds2 selfBounds(NTxNode t, NDocDouble2 selfSize, NDocDouble2 minSize);

    boolean isVisible(NTxNode t);

    double getFontSize(NTxNode t);

    String getFontFamily(NTxNode t);

    boolean isFontUnderlined(NTxNode t);

    boolean isFontStrike(NTxNode t);

    boolean isFontBold(NTxNode t);

    boolean isFontItalic(NTxNode t);

    Font getFont(NTxNode t);

    NDocDouble2 getRoundCornerArcs(NTxNode t);

    int getColSpan(NTxNode t);

    int getRowSpan(NTxNode t);

    Boolean get3D(NTxNode t);

    Boolean getRaised(NTxNode t);

    NOptional<Shadow> readStyleAsShadow(NTxNode p, String s);

    Paint getForegroundColor(NTxNode p, boolean force);

    Paint resolveGridColor(NTxNode p);

    Paint resolveBackgroundColor(NTxNode p);

    boolean isDrawContour(NTxNode p);

    boolean requireDrawGrid(NTxNode p);

    boolean requireFillBackground(NTxNode p);

    int getColumns(NTxNode p);

    int getRows(NTxNode p);

    boolean isDebug(NTxNode p);

    int getDebugLevel(NTxNode p);

    Color getDebugColor(NTxNode p);

    NOptional<NDocPoint2D> getStyleAsShadowDistance(Object sv);

    SizeD mapDim(SizeD d, SizeD base);

    Stroke resolveStroke(NTxNode t);

    boolean withStroke(NTxNode t, Runnable r);

    boolean applyStroke(NTxNode t);

    void applyFont(NTxNode t);

    SizeD mapDim(double w, double h);

    NDocBounds2 bounds(NTxNode t, NDocNodeRendererContext ctx);

    boolean applyForeground(NTxNode t, boolean force);

    boolean applyBackgroundColor(NTxNode t);

    boolean applyGridColor(NTxNode t, boolean force);

    void paintDebugBox(NTxNode t, NDocBounds2 a, boolean force);

    void paintDebugBox(NTxNode t, NDocBounds2 a);

    NOptional<Color> colorFromPaint(Paint p);

    void paintBorderLine(NTxNode t, NDocBounds2 a);

    void paintBackground(NTxNode t, NDocBounds2 a);
}
