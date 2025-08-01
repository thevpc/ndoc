/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.api.renderer;

import java.io.OutputStream;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public abstract class NDocDocumentStreamRendererBase extends NDocDocumentRendererBase implements NDocDocumentStreamRenderer {

    protected Object output;
    protected NDocDocumentStreamRendererConfig config;

    public NDocDocumentStreamRendererBase(NDocEngine engine) {
        super(engine);
    }

    @Override
    public NDocDocumentStreamRenderer setStreamRendererConfig(NDocDocumentStreamRendererConfig config) {
        this.config=config;
        return this;
    }

    @Override
    public NDocDocumentStreamRendererConfig getStreamRendererConfig() {
        return config;
    }

    @Override
    public NDocDocumentStreamRenderer setOutput(OutputStream outputStream) {
        this.output = outputStream;
        return this;
    }

    @Override
    public NDocDocumentStreamRenderer setOutput(NPath path) {
        this.output = path;
        return this;
    }
}
