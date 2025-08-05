package net.thevpc.ntexup.api.document.elem2d.primitives;

import net.thevpc.ntexup.api.document.elem2d.NtxElement2DPrimitive;

import java.awt.*;

public abstract class AbstractNtxElement2DPrimitive implements NtxElement2DPrimitive {
    private Paint backgroundPaint;
    private Paint contourPaint;
    private Stroke contourStroke;
    private Composite composite;


    @Override
    public Composite getComposite() {
        return composite;
    }

    @Override
    public NtxElement2DPrimitive setComposite(Composite composite) {
        this.composite = composite;
        return this;
    }

    @Override
    public Paint getBackgroundPaint() {
        return backgroundPaint;
    }

    @Override
    public NtxElement2DPrimitive setBackgroundPaint(Paint backgroundPaint) {
        this.backgroundPaint = backgroundPaint;
        return this;
    }

    @Override
    public Paint getLinePaint() {
        return contourPaint;
    }

    @Override
    public NtxElement2DPrimitive setLinePaint(Paint contourPaint) {
        this.contourPaint = contourPaint;
        return this;
    }

    @Override
    public Stroke getLineStroke() {
        return contourStroke;
    }

    @Override
    public NtxElement2DPrimitive setLineStroke(Stroke contourStroke) {
        this.contourStroke = contourStroke;
        return this;
    }
}
