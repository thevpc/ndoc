package net.thevpc.ndoc.api.model.elem2d;

import java.awt.*;

public interface Element2DPrimitive extends HElement2D {
    Element2DPrimitiveType type();

    Composite getComposite();

    Element2DPrimitive setComposite(Composite composite);

    Paint getBackgroundPaint();

    Element2DPrimitive setBackgroundPaint(Paint backgroundPaint);

    Paint getLinePaint();

    Element2DPrimitive setLinePaint(Paint contourPaint);

    Stroke getLineStroke();

    Element2DPrimitive setLineStroke(Stroke contourStroke);
}
