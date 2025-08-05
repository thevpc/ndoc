/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ntexup.api.renderer;

import java.io.OutputStream;

import net.thevpc.ntexup.api.engine.NTxEngine;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public abstract class NTxDocumentStreamRendererBase extends NTxDocumentRendererBase implements NTxDocumentStreamRenderer {

    protected Object output;
    protected NTxDocumentStreamRendererConfig config;

    public NTxDocumentStreamRendererBase(NTxEngine engine) {
        super(engine);
    }

    @Override
    public NTxDocumentStreamRenderer setStreamRendererConfig(NTxDocumentStreamRendererConfig config) {
        this.config=config;
        return this;
    }

    @Override
    public NTxDocumentStreamRendererConfig getStreamRendererConfig() {
        return config;
    }

    @Override
    public NTxDocumentStreamRenderer setOutput(OutputStream outputStream) {
        this.output = outputStream;
        return this;
    }

    @Override
    public NTxDocumentStreamRenderer setOutput(NPath path) {
        this.output = path;
        return this;
    }
}
