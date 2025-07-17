package net.thevpc.ndoc.api.model.elem3d;

import java.awt.*;

public interface NDocElement3DPrimitive extends NDocElement3D {
    NDocElement3DPrimitiveType type();

    Paint getBackgroundPaint();

    NDocElement3DPrimitive setBackgroundPaint(Paint backgroundPaint);

    Paint getLinePaint();

    NDocElement3DPrimitive setLinePaint(Paint contourPaint);

    Stroke getLineStroke();

    NDocElement3DPrimitive setLineStroke(Stroke contourStroke);

    Composite getComposite();

    NDocElement3DPrimitive setComposite(Composite composite);
}
