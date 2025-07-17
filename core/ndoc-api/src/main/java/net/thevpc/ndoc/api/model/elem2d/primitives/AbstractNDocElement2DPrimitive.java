package net.thevpc.ndoc.api.model.elem2d.primitives;

import net.thevpc.ndoc.api.model.elem2d.NDocElement2DPrimitive;

import java.awt.*;

public abstract class AbstractNDocElement2DPrimitive implements NDocElement2DPrimitive {
    private Paint backgroundPaint;
    private Paint contourPaint;
    private Stroke contourStroke;
    private Composite composite;


    @Override
    public Composite getComposite() {
        return composite;
    }

    @Override
    public NDocElement2DPrimitive setComposite(Composite composite) {
        this.composite = composite;
        return this;
    }

    @Override
    public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    @Override
    public NDocElement2DPrimitive setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
        return this;
    }

    @Override
    public Paint getLinePaint() {
        return contourPaint;
    }

    @Override
    public NDocElement2DPrimitive setLinePaint(Paint contourPaint) {
        this.contourPaint = contourPaint;
        return this;
    }

    @Override
    public Stroke getLineStroke() {
        return contourStroke;
    }

    @Override
    public NDocElement2DPrimitive setLineStroke(Stroke contourStroke) {
        this.contourStroke = contourStroke;
        return this;
    }
}
