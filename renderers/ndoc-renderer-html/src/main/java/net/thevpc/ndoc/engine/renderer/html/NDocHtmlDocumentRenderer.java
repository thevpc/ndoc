/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.renderer.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRendererBase;
import net.thevpc.ndoc.api.renderer.NDocDocumentRendererContext;
import net.thevpc.ndoc.api.renderer.NDocDocumentRendererSupplier;
import net.thevpc.ndoc.api.renderer.NDocDocumentStreamRenderer;
import net.thevpc.nuts.io.NIOException;
import net.thevpc.nuts.io.NPath;

/**
 * @author vpc
 */
public class NDocHtmlDocumentRenderer extends NDocDocumentStreamRendererBase implements NDocDocumentStreamRenderer {

    private NDocDocumentRendererContext rendererContext = new NDocDocumentRendererContextImpl();

    public NDocHtmlDocumentRenderer(NDocEngine engine) {
        super(engine);
    }

    protected void renderStream(NDocument document, OutputStream os) {
        document = engine.compileDocument(document).get();
        PrintStream out = new PrintStream(os);
        out.println("<html>");
        out.println("<body>");
        renderNode(document.root(), out);
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    public void renderSupplier(NDocDocumentRendererSupplier document) {
        NDocument d = document.get(rendererContext);
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
    public NDocDocumentStreamRenderer renderNode(NDocNode part, OutputStream out) {
        switch (part.type()) {
            case NDocNodeType.PAGE_GROUP:
                break;
            case NDocNodeType.PAGE: {
                PrintStream o = psOf(out);
                for (NDocNode pp : part.children()) {
                    renderNode(pp, o);
                }
                o.flush();
                break;
            }
            default:
                throw new IllegalArgumentException("invalid type " + part);
        }
        return this;
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

    private class NDocDocumentRendererContextImpl implements NDocDocumentRendererContext {

        public NDocDocumentRendererContextImpl() {
        }
    }

}
