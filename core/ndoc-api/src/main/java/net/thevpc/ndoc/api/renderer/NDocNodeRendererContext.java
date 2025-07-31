package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.*;
import net.thevpc.ndoc.api.document.style.*;

import net.thevpc.ndoc.api.model.NDocSizeRequirements;
import net.thevpc.ndoc.api.renderer.text.NDocTextRendererBuilder;
import net.thevpc.ndoc.api.util.NDocSizeRef;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.text.NText;
import net.thevpc.nuts.util.NOptional;

import java.awt.image.ImageObserver;
import java.util.List;

public interface NDocNodeRendererContext {
    String CAPABILITY_PRINT="print";
    String CAPABILITY_ANIMATE ="animate";

    default NDocSizeRequirements sizeRequirementsOf(NDocNode p) {
        return manager().getRenderer(p.type()).get().sizeRequirements(p, this);
    }

    NDocBounds2 selfBounds(NDocNode e);

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
}
