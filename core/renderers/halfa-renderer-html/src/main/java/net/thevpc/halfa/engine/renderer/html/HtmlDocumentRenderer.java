/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.renderer.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageListImpl;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.renderer.*;
import net.thevpc.nuts.io.NIOException;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public class HtmlDocumentRenderer extends AbstractHDocumentStreamRenderer implements HDocumentStreamRenderer {

    private HDocumentRendererContext rendererContext = new HDocumentRendererContextImpl();

    public HtmlDocumentRenderer(HEngine engine) {
        super(engine);
    }

    protected void renderStream(HDocument document, OutputStream os) {
        HMessageList messages2 = this.messages;
        if (messages2 == null) {
            messages2 = new HMessageListImpl(engine.computeSource(document.root()));
        }
        document = engine.compileDocument(document, messages2).get();
        PrintStream out = new PrintStream(os);
        out.println("<html>");
        out.println("<body>");
        renderNode(document.root(), out);
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    public void renderSupplier(HDocumentRendererSupplier document) {
        HDocument d = document.get(rendererContext);
        Object o = output;
        if (o == null) {
            o = NPath.of("document.pdf");
        }
        if (o instanceof NPath) {
            try (OutputStream os = ((NPath) o).getOutputStream()) {
                renderStream(d, os);
            } catch (IOException ex) {
                throw new NIOException(ex);
            }
        } else if (o instanceof OutputStream) {
            renderStream(d, (OutputStream) o);
        }
    }

    public PrintStream psOf(OutputStream out) {
        if (out instanceof PrintStream) {
            return (PrintStream) out;
        }
        return new PrintStream(out);
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
    public void renderNode(HNode part, OutputStream out) {
        switch (part.type()) {
            case HNodeType.PAGE_GROUP:
                break;
            case HNodeType.PAGE: {
                PrintStream o = psOf(out);
                for (HNode pp : part.children()) {
                    renderNode(pp, o);
                }
                o.flush();
                break;
            }
            default:
                throw new IllegalArgumentException("invalid type " + part);
        }
    }

//    public void render(HDocumentPart part, PrintStream out) {
//        switch (part.type()) {
//            case PAGE_GROUP:
//                break;
//            case PAGE:
//                break;
//            case PARAGRAPH:
//                break;
//            case PHRASE:
//                break;
//            default:
//                throw new IllegalArgumentException("invalid type " + part);
//        }
//    }

    private class HDocumentRendererContextImpl implements HDocumentRendererContext {

        public HDocumentRendererContextImpl() {
        }

        @Override
        public HMessageList messages() {
            return messages;
        }
    }

}