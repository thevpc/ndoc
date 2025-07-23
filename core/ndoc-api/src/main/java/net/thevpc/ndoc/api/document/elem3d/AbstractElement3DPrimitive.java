package net.thevpc.ndoc.api.document.elem3d;

import java.awt.*;

public abstract class AbstractElement3DPrimitive extends AbstractElement3D implements NDocElement3DPrimitive {
    private Paint backgroundPaint;
    private Paint contourPaint;
    private Stroke contourStroke;
    private Composite composite;


    public Composite getComposite() {
        return composite;
    }

    public NDocElement3DPrimitive setComposite(Composite composite) {
        this.composite = composite;
        return this;
    }

    public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    public NDocElement3DPrimitive setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
        return this;
    }

    public Paint getLinePaint() {
        return contourPaint;
    }

    public NDocElement3DPrimitive setLinePaint(Paint contourPaint) {
        this.contourPaint = contourPaint;
        return this;
    }

    public Stroke getLineStroke() {
        return contourStroke;
    }

    public NDocElement3DPrimitive setLineStroke(Stroke contourStroke) {
        this.contourStroke = contourStroke;
        return this;
    }
}
