/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.renderer.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import net.thevpc.halfa.api.HalfaEngine;
import net.thevpc.halfa.api.model.HDocument;
import net.thevpc.halfa.api.model.HDocumentPart;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NIOException;
import net.thevpc.nuts.io.NPath;

/**
 *
 * @author vpc
 */
public class HtmlDocumentRenderer implements HDocumentStreamRenderer {

    private NSession session;
    private HalfaEngine halfaEngine;

    public HtmlDocumentRenderer(HalfaEngine halfaEngine,NSession session) {
        this.halfaEngine = halfaEngine;
        this.session = session;
    }

    @Override
    public void render(HDocument document, OutputStream os) {
        PrintStream out = new PrintStream(os);
        out.println("<html>");
        out.println("<body>");
        for (HDocumentPart dp : document.getDocumentParts()) {
            render(dp, out);
        }
        out.println("</body>");
        out.println("</html>");
    }

    public void render(HDocumentPart part, OutputStream out) {
        switch (part.type()) {
            case PAGE_GROUP:
                break;
            case PAGE:
                break;
            case PARAGRAPH:
                break;
            case PHRASE:
                break;
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

    public void render(HDocument document, NPath path) {
        try (OutputStream os = path.getOutputStream()) {
            render(document, os);
        } catch (IOException ex) {
            throw new NIOException(session, ex);
        }
    }

}
