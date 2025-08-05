package net.thevpc.ntexup.api.document.elem3d;

import java.awt.*;

public interface NtxElement3DPrimitive extends NtxElement3D {
    NDocElement3DPrimitiveType type();

    Paint getBackgroundPaint();

    NtxElement3DPrimitive setBackgroundPaint(Paint backgroundPaint);

    Paint getLinePaint();

    NtxElement3DPrimitive setLinePaint(Paint contourPaint);

    Stroke getLineStroke();

    NtxElement3DPrimitive setLineStroke(Stroke contourStroke);

    Composite getComposite();

    NtxElement3DPrimitive setComposite(Composite composite);
}
