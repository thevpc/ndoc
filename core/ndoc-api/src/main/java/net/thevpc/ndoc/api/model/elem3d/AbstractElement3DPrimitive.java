package net.thevpc.ndoc.api.model.elem3d;

import java.awt.*;

public abstract class AbstractElement3DPrimitive extends AbstractElement3D implements HElement3DPrimitive {
    private Paint backgroundPaint;
    private Paint contourPaint;
    private Stroke contourStroke;
    private Composite composite;


    public Composite getComposite() {
        return composite;
    }

    public HElement3DPrimitive setComposite(Composite composite) {
        this.composite = composite;
        return this;
    }

    public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    public HElement3DPrimitive setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
        return this;
    }

    public Paint getLinePaint() {
        return contourPaint;
    }

    public HElement3DPrimitive setLinePaint(Paint contourPaint) {
        this.contourPaint = contourPaint;
        return this;
    }

    public Stroke getLineStroke() {
        return contourStroke;
    }

    public HElement3DPrimitive setLineStroke(Stroke contourStroke) {
        this.contourStroke = contourStroke;
        return this;
    }
}
