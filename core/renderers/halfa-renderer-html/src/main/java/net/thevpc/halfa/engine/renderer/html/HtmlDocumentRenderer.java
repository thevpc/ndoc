/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.renderer.html;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.function.Supplier;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageListImpl;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.spi.renderer.AbstractHDocumentRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NIOException;
import net.thevpc.nuts.io.NPath;

/**
 *
 * @author vpc
 */
public class HtmlDocumentRenderer extends AbstractHDocumentRenderer implements HDocumentStreamRenderer {


    public HtmlDocumentRenderer(HEngine engine, NSession session) {
        super(engine, session);
    }


    @Override
    public void render(HDocument document) {
        render(document,NPath.of("result.html",session));
    }

    @Override
    public void render(HDocument document, OutputStream os) {
        HMessageList messages2=this.messages;
        if(messages2==null){
            messages2=new HMessageListImpl(session, engine.computeSource(document.root()));
        }
        document= engine.compileDocument(document, messages2).get();
        PrintStream out = new PrintStream(os);
        out.println("<html>");
        out.println("<body>");
        render(document.root(), out);
        out.println("</body>");
        out.println("</html>");
    }

    @Override
    public void renderSupplier(Supplier<HDocument> document) {
        render(document.get());
    }

    public PrintStream psOf(OutputStream out) {
        if(out instanceof PrintStream){
            return (PrintStream) out;
        }
        return new PrintStream(out);
    }

    public void render(HNode part, OutputStream out) {
        switch (part.type()) {
            case HNodeType.PAGE_GROUP:
                break;
            case HNodeType.PAGE: {
                PrintStream o=psOf(out);
                for (HNode pp : part.children()) {
                    render(pp,o);
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

    public void render(HDocument document, NPath path) {
        try (OutputStream os = path.getOutputStream()) {
            render(document, os);
        } catch (IOException ex) {
            throw new NIOException(session, ex);
        }
    }

}
