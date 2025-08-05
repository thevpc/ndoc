package net.thevpc.ntexup.api.document.elem3d;

import java.awt.*;

public abstract class AbstractNTxElement3DPrimitive extends AbstractNTxElement3D implements NtxElement3DPrimitive {
    private Paint backgroundPaint;
    private Paint contourPaint;
    private Stroke contourStroke;
    private Composite composite;


    public Composite getComposite() {
        return composite;
    }

    public NtxElement3DPrimitive setComposite(Composite composite) {
        this.composite = composite;
        return this;
    }

    public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    public NtxElement3DPrimitive setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
        return this;
    }

    public Paint getLinePaint() {
        return contourPaint;
    }

    public NtxElement3DPrimitive setLinePaint(Paint contourPaint) {
        this.contourPaint = contourPaint;
        return this;
    }

    public Stroke getLineStroke() {
        return contourStroke;
    }

    public NtxElement3DPrimitive setLineStroke(Stroke contourStroke) {
        this.contourStroke = contourStroke;
        return this;
    }
}
