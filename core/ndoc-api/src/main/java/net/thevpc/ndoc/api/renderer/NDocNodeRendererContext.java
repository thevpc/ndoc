package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.elem2d.*;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.log.NDocLogger;
import net.thevpc.ndoc.api.document.node.*;
import net.thevpc.ndoc.api.document.style.*;

import net.thevpc.ndoc.api.document.NDocSizeRequirements;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.api.util.NDocSizeRef;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;

public interface NDocNodeRendererContext {
    String CAPABILITY_PRINT = "print";
    String CAPABILITY_ANIMATE = "animate";

    default NDocSizeRequirements sizeRequirementsOf(NDocNode p) {
        return manager().getRenderer(p.type()).get().sizeRequirements(p, this);
    }

    NDocBounds2 selfBounds(NDocNode e);

    NDocBounds2 defaultSelfBounds(NDocNode e);

    NDocNodeRendererManager manager();

    NDocLogger log();

    NDocNodeRendererContext dryMode();

    ImageObserver imageObserver();

    boolean isDry();

    NDocBounds2 getGlobalBounds();

    NDocGraphics graphics();

    NDocBounds2 getBounds();


    void render(NDocNode p);

    void render(NDocNode p, NDocNodeRendererContext ctx);

    NDocEngine engine();

    NDocDocumentFactory documentFactory();

    NOptional<NElement> computePropertyValue(NDocNode t, String s, String... synonyms);

    List<NDocProp> computeProperties(NDocNode t);

    NPath resolvePath(NPath path, NDocNode node);

    NPath resolvePath(NElement path, NDocNode node);

    NDocNodeRendererContext withDefaultStyles(NDocNode node, NDocProperties defaultStyles);

    NDocNodeRendererContext withBounds(NDocNode t, NDocBounds2 bounds2);

    NDocNodeRendererContext withGraphics(NDocGraphics graphics);

    NDocSizeRef sizeRef();

    boolean isPrint();

    boolean isAnimate();

    void repaint();

    Object getCapability(String name);

    boolean hasCapability(String name);

    public boolean isCapability(String name);

    void highlightNutsText(String lang, String rawText, NText parsedText, NDocNode p, NDocTextRendererBuilder result);

    NOptional<Paint> getColorProperty(String propName, NDocNode t);

    NDocDouble2 getOrigin(NDocNode t, NDocDouble2 a);

    NDocDouble2 getPosition(NDocNode t, NDocDouble2 a);

    NElement getStroke(NDocNode t);

    NDocBounds2 selfBounds(NDocNode t, NDocDouble2 selfSize, NDocDouble2 minSize);

    boolean isVisible(NDocNode t);

    double getFontSize(NDocNode t);

    String getFontFamily(NDocNode t);

    boolean isFontUnderlined(NDocNode t);

    boolean isFontStrike(NDocNode t);

    boolean isFontBold(NDocNode t);

    boolean isFontItalic(NDocNode t);

    Font getFont(NDocNode t);

    NDocDouble2 getRoundCornerArcs(NDocNode t);

    int getColSpan(NDocNode t);

    int getRowSpan(NDocNode t);

    Boolean get3D(NDocNode t);

    Boolean getRaised(NDocNode t);

    NOptional<Shadow> readStyleAsShadow(NDocNode p, String s);

    Paint getForegroundColor(NDocNode p, boolean force);

    Paint resolveGridColor(NDocNode p);

    Paint resolveBackgroundColor(NDocNode p);

    boolean isDrawContour(NDocNode p);

    boolean requireDrawGrid(NDocNode p);

    boolean requireFillBackground(NDocNode p);

    int getColumns(NDocNode p);

    int getRows(NDocNode p);

    boolean isDebug(NDocNode p);

    int getDebugLevel(NDocNode p);

    Color getDebugColor(NDocNode p);

    NOptional<NDocPoint2D> getStyleAsShadowDistance(Object sv);

    SizeD mapDim(SizeD d, SizeD base);

    Stroke resolveStroke(NDocNode t);

    boolean withStroke(NDocNode t, Runnable r);

    boolean applyStroke(NDocNode t);

    void applyFont(NDocNode t);

    SizeD mapDim(double w, double h);

    NDocBounds2 bounds(NDocNode t, NDocNodeRendererContext ctx);

    boolean applyForeground(NDocNode t, boolean force);

    boolean applyBackgroundColor(NDocNode t);

    boolean applyGridColor(NDocNode t, boolean force);

    void paintDebugBox(NDocNode t, NDocBounds2 a, boolean force);

    void paintDebugBox(NDocNode t, NDocBounds2 a);

    NOptional<Color> colorFromPaint(Paint p);

    void paintBorderLine(NDocNode t, NDocBounds2 a);

    void paintBackground(NDocNode t, NDocBounds2 a);
}
