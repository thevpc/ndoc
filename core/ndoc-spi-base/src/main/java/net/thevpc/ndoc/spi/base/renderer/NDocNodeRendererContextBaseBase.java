package net.thevpc.ndoc.spi.base.renderer;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.resources.HResource;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.util.HSizeRef;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;


public abstract class NDocNodeRendererContextBaseBase implements NDocNodeRendererContext {
    public void render(HNode p) {
        render(p, this);
    }

    @Override
    public NDocDocumentFactory documentFactory() {
        return engine().documentFactory();
    }

    @Override
    public NPath resolvePath(NPath path, HNode node) {
        if (path.isAbsolute()) {
            return path;
        }
        return resolvePath(NElement.ofString(path.toString()), node);
    }

    @Override
    public NDocNodeRendererContext withDefaultStyles(HNode node, HProperties defaultStyles) {
        return new NDocNodeRendererContextDelegate(node, this, null, defaultStyles, isDry(), null);
    }

    @Override
    public NDocNodeRendererContext withBounds(HNode t, Bounds2 bounds2) {
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
    public NPath resolvePath(NElement path, HNode node) {
        Object src = engine().computeSource(node);
        NPath sp = (src instanceof HResource)?((HResource) src).path().orNull():null;
        return HUtils.resolvePath(path, src);
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
