package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.model.Bounds2;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NOptional;

public interface HNodeRendererContext {
    HNodeRendererContext dryMode();

    boolean isDry();

    Bounds2 getGlobalBounds();

    HGraphics graphics();

    Bounds2 getBounds();

    NSession session();

    HSizeRequirements render(HNode p) ;

    HSizeRequirements rootRender(HNode p, HNodeRendererContext ctx);

    HEngine engine();

    HDocumentFactory documentFactory();

    <T> NOptional<T> getProperty(HNode t, String s);

    HNodeRendererContext withDefaultStyles(HNode node, HProperties defaultStyles);

    NPath resolvePath(NPath path, HNode node);

    NPath resolvePath(String path,HNode node) ;

    HNodeRendererContext withBounds(HNode t, Bounds2 bounds2);
}
