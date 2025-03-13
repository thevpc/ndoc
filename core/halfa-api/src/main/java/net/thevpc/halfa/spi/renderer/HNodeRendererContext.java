package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HLogger;
import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.util.HSizeRef;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;

import java.awt.image.ImageObserver;
import java.util.List;

public interface HNodeRendererContext {
    String CAPABILITY_PRINT="print";
    String CAPABILITY_ANIMATE ="animate";

    default HSizeRequirements sizeRequirementsOf(HNode p) {
        return manager().getRenderer(p.type()).get().sizeRequirements(p, this);
    }

    HNodeRendererManager manager();

    HLogger log();

    HNodeRendererContext dryMode();

    ImageObserver imageObserver();

    boolean isDry();

    Bounds2 getGlobalBounds();

    HGraphics graphics();

    Bounds2 getBounds();


    void render(HNode p);

    void render(HNode p, HNodeRendererContext ctx);

    HEngine engine();

    HDocumentFactory documentFactory();

    NOptional<TsonElement> computePropertyValue(HNode t, String s, String... synonyms);

    List<HProp> computeProperties(HNode t);

    NPath resolvePath(NPath path, HNode node);

    NPath resolvePath(TsonElement path, HNode node);

    HNodeRendererContext withDefaultStyles(HNode node, HProperties defaultStyles);

    HNodeRendererContext withBounds(HNode t, Bounds2 bounds2);

    HNodeRendererContext withGraphics(HGraphics graphics);

    HSizeRef sizeRef();

    boolean isPrint();

    boolean isAnimate();

    void repaint();

    Object getCapability(String name);

    boolean hasCapability(String name);

    public boolean isCapability(String name);
}
