package net.thevpc.ndoc.spi.renderer;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.HLogger;
import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.spi.model.NDocSizeRequirements;
import net.thevpc.ndoc.spi.util.HSizeRef;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.ImageObserver;
import java.util.List;

public interface NDocNodeRendererContext {
    String CAPABILITY_PRINT="print";
    String CAPABILITY_ANIMATE ="animate";

    default NDocSizeRequirements sizeRequirementsOf(HNode p) {
        return manager().getRenderer(p.type()).get().sizeRequirements(p, this);
    }

    NDocNodeRendererManager manager();

    HLogger log();

    NDocNodeRendererContext dryMode();

    ImageObserver imageObserver();

    boolean isDry();

    Bounds2 getGlobalBounds();

    NDocGraphics graphics();

    Bounds2 getBounds();


    void render(HNode p);

    void render(HNode p, NDocNodeRendererContext ctx);

    NDocEngine engine();

    NDocDocumentFactory documentFactory();

    NOptional<NElement> computePropertyValue(HNode t, String s, String... synonyms);

    List<HProp> computeProperties(HNode t);

    NPath resolvePath(NPath path, HNode node);

    NPath resolvePath(NElement path, HNode node);

    NDocNodeRendererContext withDefaultStyles(HNode node, HProperties defaultStyles);

    NDocNodeRendererContext withBounds(HNode t, Bounds2 bounds2);

    NDocNodeRendererContext withGraphics(NDocGraphics graphics);

    HSizeRef sizeRef();

    boolean isPrint();

    boolean isAnimate();

    void repaint();

    Object getCapability(String name);

    boolean hasCapability(String name);

    public boolean isCapability(String name);
}
