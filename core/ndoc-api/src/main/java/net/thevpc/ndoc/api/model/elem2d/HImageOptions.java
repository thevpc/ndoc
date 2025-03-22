package net.thevpc.ndoc.api.model.elem2d;

import java.awt.*;
import java.awt.image.ImageObserver;

public class HImageOptions {
    private Color transparentColor;
    private boolean disableAnimation;
    private ImageObserver imageObserver;
    private Runnable asyncLoad;
    private Dimension size;

    public Dimension getSize() {
        return size;
    }

    public HImageOptions setSize(Dimension size) {
        this.size = size;
        return this;
    }

    public Runnable getAsyncLoad() {
        return asyncLoad;
    }

    public HImageOptions setAsyncLoad(Runnable asyncLoad) {
        this.asyncLoad = asyncLoad;
        return this;
    }

    public ImageObserver getImageObserver() {
        return imageObserver;
    }

    public HImageOptions setImageObserver(ImageObserver imageObserver) {
        this.imageObserver = imageObserver;
        return this;
    }

    public Color getTransparentColor() {
        return transparentColor;
    }

    public HImageOptions setTransparentColor(Color transparentColor) {
        this.transparentColor = transparentColor;
        return this;
    }

    public boolean isDisableAnimation() {
        return disableAnimation;
    }

    public HImageOptions setDisableAnimation(boolean disableAnimation) {
        this.disableAnimation = disableAnimation;
        return this;
    }
}
