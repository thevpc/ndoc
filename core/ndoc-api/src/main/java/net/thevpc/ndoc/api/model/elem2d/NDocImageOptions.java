package net.thevpc.ndoc.api.model.elem2d;

import java.awt.*;
import java.awt.image.ImageObserver;

public class NDocImageOptions {
    private Color transparentColor;
    private boolean disableAnimation;
    private ImageObserver imageObserver;
    private Runnable asyncLoad;
    private Dimension size;

    public Dimension getSize() {
        return size;
    }

    public NDocImageOptions setSize(Dimension size) {
        this.size = size;
        return this;
    }

    public Runnable getAsyncLoad() {
        return asyncLoad;
    }

    public NDocImageOptions setAsyncLoad(Runnable asyncLoad) {
        this.asyncLoad = asyncLoad;
        return this;
    }

    public ImageObserver getImageObserver() {
        return imageObserver;
    }

    public NDocImageOptions setImageObserver(ImageObserver imageObserver) {
        this.imageObserver = imageObserver;
        return this;
    }

    public Color getTransparentColor() {
        return transparentColor;
    }

    public NDocImageOptions setTransparentColor(Color transparentColor) {
        this.transparentColor = transparentColor;
        return this;
    }

    public boolean isDisableAnimation() {
        return disableAnimation;
    }

    public NDocImageOptions setDisableAnimation(boolean disableAnimation) {
        this.disableAnimation = disableAnimation;
        return this;
    }
}
