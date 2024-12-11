/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.spi.renderer;

import java.io.OutputStream;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public abstract class AbstractHDocumentStreamRenderer extends AbstractHDocumentRenderer implements HDocumentStreamRenderer {

    protected Object output;
    protected HDocumentStreamRendererConfig config;

    public AbstractHDocumentStreamRenderer(HEngine engine) {
        super(engine);
    }

    @Override
    public void setStreamRendererConfig(HDocumentStreamRendererConfig config) {
        this.config=config;
    }

    @Override
    public HDocumentStreamRendererConfig getStreamRendererConfig() {
        return config;
    }

    @Override
    public void setOutput(OutputStream outputStream) {
        this.output = outputStream;
    }

    @Override
    public void setOutput(NPath path) {
        this.output = path;
    }
}
