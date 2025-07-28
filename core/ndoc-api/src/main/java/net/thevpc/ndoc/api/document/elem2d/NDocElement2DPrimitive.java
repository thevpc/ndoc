package net.thevpc.ndoc.api.document.elem2d;

import java.awt.*;

public interface NDocElement2DPrimitive extends NDocElement2D {
    NDocElement2DPrimitiveType type();

    Composite getComposite();

    NDocElement2DPrimitive setComposite(Composite composite);

    Paint getBackgroundPaint();

    NDocElement2DPrimitive setBackgroundPaint(Paint backgroundPaint);

    Paint getLinePaint();

    NDocElement2DPrimitive setLinePaint(Paint contourPaint);

    Stroke getLineStroke();

    NDocElement2DPrimitive setLineStroke(Stroke contourStroke);
}
