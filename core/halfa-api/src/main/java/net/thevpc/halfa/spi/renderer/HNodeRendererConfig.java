package net.thevpc.halfa.spi.renderer;

import net.thevpc.halfa.api.document.HLogger;
import net.thevpc.nuts.util.NAssert;

import java.util.HashMap;
import java.util.Map;

public class HNodeRendererConfig {
    private int width;
    private int height;
    private Map<String, Object> capabilities;
    private HLogger messages;

    public HNodeRendererConfig() {
    }
    public HNodeRendererConfig(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public HNodeRendererConfig setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public HNodeRendererConfig setHeight(int height) {
        this.height = height;
        return this;
    }

    public Map<String, Object> getCapabilities() {
        return capabilities;
    }

    public HNodeRendererConfig setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
        return this;
    }

    public HLogger getMessages() {
        return messages;
    }

    public HNodeRendererConfig setMessages(HLogger messages) {
        this.messages = messages;
        return this;
    }

    public HNodeRendererConfig setCapability(String name, Object value) {
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

    public HNodeRendererConfig withAnimate(boolean value) {
        return setCapability(HNodeRendererContext.CAPABILITY_ANIMATE, value);
    }

    public HNodeRendererConfig withPrint(boolean value) {
        return setCapability(HNodeRendererContext.CAPABILITY_PRINT, value);
    }
}
