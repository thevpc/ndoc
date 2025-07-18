package net.thevpc.ndoc.spi.renderer;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.NDocEngine;
import net.thevpc.ndoc.api.document.NDocLogger;
import net.thevpc.ndoc.api.model.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.style.NDocProp;
import net.thevpc.ndoc.api.style.NDocProperties;
import net.thevpc.ndoc.spi.model.NDocSizeRequirements;
import net.thevpc.ndoc.spi.util.NDocSizeRef;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.ImageObserver;
import java.util.List;

public interface NDocNodeRendererContext {
    String CAPABILITY_PRINT="print";
    String CAPABILITY_ANIMATE ="animate";

    default NDocSizeRequirements sizeRequirementsOf(NDocNode p) {
        return manager().getRenderer(p.type()).get().sizeRequirements(p, this);
    }

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
}
