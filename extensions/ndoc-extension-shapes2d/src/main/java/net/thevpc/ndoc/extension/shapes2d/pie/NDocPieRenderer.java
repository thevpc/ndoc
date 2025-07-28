package net.thevpc.ndoc.extension.shapes2d.pie;

import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.extension.shapes2d.donut.NDocDonutOrPieRenderer;

public class NDocPieRenderer extends NDocDonutOrPieRenderer {

    public NDocPieRenderer() {
        super(NDocNodeType.PIE);
    }
}
