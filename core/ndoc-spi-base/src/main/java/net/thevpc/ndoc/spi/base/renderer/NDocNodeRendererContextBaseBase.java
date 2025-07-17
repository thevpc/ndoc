package net.thevpc.ndoc.spi.base.renderer;

import net.thevpc.ndoc.NDocDocumentFactory;
import net.thevpc.ndoc.api.model.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.model.node.NDocNode;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.style.NDocProperties;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.util.NDocSizeRef;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.io.NPath;


public abstract class NDocNodeRendererContextBaseBase implements NDocNodeRendererContext {
    public void render(NDocNode p) {
        render(p, this);
    }

    @Override
    public NDocDocumentFactory documentFactory() {
        return engine().documentFactory();
    }

    @Override
    public NPath resolvePath(NPath path, NDocNode node) {
        if (path.isAbsolute()) {
            return path;
        }
        return resolvePath(NElement.ofString(path.toString()), node);
    }

    @Override
    public NDocNodeRendererContext withDefaultStyles(NDocNode node, NDocProperties defaultStyles) {
        return new NDocNodeRendererContextDelegate(node, this, null, defaultStyles, isDry(), null);
    }

    @Override
    public NDocNodeRendererContext withBounds(NDocNode t, NDocBounds2 bounds2) {
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
    public NPath resolvePath(NElement path, NDocNode node) {
        Object src = engine().computeSource(node);
        NPath sp = (src instanceof NDocResource)?((NDocResource) src).path().orNull():null;
        return HUtils.resolvePath(path, src);
    }

    @Override
    public NDocSizeRef sizeRef() {
        NDocBounds2 b = getBounds();
        NDocBounds2 gb = getGlobalBounds();
        return new NDocSizeRef(
                b.getWidth(),b.getHeight(),
                gb.getWidth(),gb.getHeight()
        );
    }
}
