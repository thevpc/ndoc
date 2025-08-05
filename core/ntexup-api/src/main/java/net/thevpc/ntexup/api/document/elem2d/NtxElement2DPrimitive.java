package net.thevpc.ntexup.api.document.elem2d;

import java.awt.*;

public interface NtxElement2DPrimitive extends NtxElement2D {
    NTxElement2DPrimitiveType type();

    Composite getComposite();

    NtxElement2DPrimitive setComposite(Composite composite);

    Paint getBackgroundPaint();

    NtxElement2DPrimitive setBackgroundPaint(Paint backgroundPaint);

    Paint getLinePaint();

    NtxElement2DPrimitive setLinePaint(Paint contourPaint);

    Stroke getLineStroke();

    NtxElement2DPrimitive setLineStroke(Stroke contourStroke);
}
