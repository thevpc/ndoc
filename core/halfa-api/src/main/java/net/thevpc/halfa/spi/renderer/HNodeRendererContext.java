package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

import java.util.List;

public interface HNodeRendererContext {

    default HSizeRequirements sizeRequirementsOf(HNode p) {
        return manager().getRenderer(p.type()).get().sizeRequirements(p, this);
    }

    HNodeRendererManager manager();

    HMessageList messages();

    HNodeRendererContext dryMode();

    boolean isDry();

    Bounds2 getGlobalBounds();

    HGraphics graphics();

    Bounds2 getBounds();

    NSession session();

    void render(HNode p);

    void render(HNode p, HNodeRendererContext ctx);

    HEngine engine();

    HDocumentFactory documentFactory();

    <T> NOptional<T> computePropertyValue(HNode t, String s, String... synonyms);

    List<HProp> computeProperties(HNode t);

    NPath resolvePath(NPath path, HNode node);

    NPath resolvePath(String path, HNode node);

    HNodeRendererContext withDefaultStyles(HNode node, HProperties defaultStyles);

    HNodeRendererContext withBounds(HNode t, Bounds2 bounds2);

    HNodeRendererContext withGraphics(HGraphics graphics);
}
