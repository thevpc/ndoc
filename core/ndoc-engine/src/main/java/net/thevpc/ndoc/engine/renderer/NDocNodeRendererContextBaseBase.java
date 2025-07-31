package net.thevpc.ndoc.api.base.renderer;

import net.thevpc.ndoc.api.document.NDocDocumentFactory;
import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocSizeRef;
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
    public NDocBounds2 selfBounds(NDocNode e) {
        return engine().renderManager().getRenderer(e.type()).get().selfBounds(e,  this);
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
        Object src = NDocUtils.sourceOf(node);
        NPath sp = (src instanceof NDocResource)?((NDocResource) src).path().orNull():null;
        return NDocUtils.resolvePath(path, src);
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
