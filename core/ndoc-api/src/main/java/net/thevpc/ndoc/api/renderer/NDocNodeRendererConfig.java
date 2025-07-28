package net.thevpc.ndoc.api.renderer;

import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.nuts.util.NAssert;

import java.util.HashMap;
import java.util.Map;

public class NDocNodeRendererConfig {
    private int width;
    private int height;
    private Map<String, Object> capabilities;

    public NDocNodeRendererConfig() {
    }
    public NDocNodeRendererConfig(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public NDocNodeRendererConfig setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public NDocNodeRendererConfig setHeight(int height) {
        this.height = height;
        return this;
    }

    public Map<String, Object> getCapabilities() {
        return capabilities;
    }

    public NDocNodeRendererConfig setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
        return this;
    }

    public NDocNodeRendererConfig setCapability(String name, Object value) {
        NAssert.requireNonBlank(name, "name");
        if (this.capabilities == null) {
            this.capabilities = new HashMap<>();
        }
        if (value == null) {
            this.capabilities.remove(name);
        } else {
            this.capabilities.put(name, value);
        }
        return this;
    }

    public NDocNodeRendererConfig withAnimate(boolean value) {
        return setCapability(NDocNodeRendererContext.CAPABILITY_ANIMATE, value);
    }

    public NDocNodeRendererConfig withPrint(boolean value) {
        return setCapability(NDocNodeRendererContext.CAPABILITY_PRINT, value);
    }
}
