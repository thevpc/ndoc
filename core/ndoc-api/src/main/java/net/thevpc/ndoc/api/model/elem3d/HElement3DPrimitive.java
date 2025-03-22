package net.thevpc.ndoc.api.model.elem3d;

import java.awt.*;

public interface HElement3DPrimitive extends HElement3D {
    Element3DPrimitiveType type();

    Paint getBackgroundPaint();

    HElement3DPrimitive setBackgroundPaint(Paint backgroundPaint);

    Paint getLinePaint();

    HElement3DPrimitive setLinePaint(Paint contourPaint);

    Stroke getLineStroke();

    HElement3DPrimitive setLineStroke(Stroke contourStroke);

    Composite getComposite();

    HElement3DPrimitive setComposite(Composite composite);
}
