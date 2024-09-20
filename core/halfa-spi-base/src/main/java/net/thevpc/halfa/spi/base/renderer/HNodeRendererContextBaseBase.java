package net.thevpc.halfa.spi.base.renderer;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.api.util.NPathHResource;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.HSizeRef;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

public abstract class HNodeRendererContextBaseBase implements HNodeRendererContext {
    public void render(HNode p) {
        render(p, this);
    }

    @Override
    public HDocumentFactory documentFactory() {
        return engine().documentFactory();
    }

    @Override
    public NPath resolvePath(NPath path, HNode node) {
        if (path.isAbsolute()) {
            return path;
        }
        return resolvePath(Tson.of(path.toString()), node);
    }

    @Override
    public HNodeRendererContext withDefaultStyles(HNode node, HProperties defaultStyles) {
        return new HNodeRendererContextDelegate(node, this, null, defaultStyles, isDry(), null);
    }

    @Override
    public HNodeRendererContext withBounds(HNode t, Bounds2 bounds2) {
        return new HNodeRendererContextDelegate(t, this, bounds2, null, isDry(), null);
    }

    @Override
    public HNodeRendererContext dryMode() {
        return new HNodeRendererContextDelegate(null, this, null, null, true, null);
    }

    @Override
    public HNodeRendererContext withGraphics(HGraphics graphics) {
        return new HNodeRendererContextDelegate(null, this, null, null, isDry(), graphics);
    }


    @Override
    public NPath resolvePath(TsonElement path, HNode node) {
        Object src = engine().computeSource(node);
        NPath sp = (src instanceof HResource)?((HResource) src).path().orNull():null;
        return HUtils.resolvePath(path, src,session());
    }

    @Override
    public HSizeRef sizeRef() {
        Bounds2 b = getBounds();
        Bounds2 gb = getGlobalBounds();
        return new HSizeRef(
                b.getWidth(),b.getHeight(),
                gb.getWidth(),gb.getHeight()
        );
    }
}
