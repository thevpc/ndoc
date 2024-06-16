/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.renderer.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import net.thevpc.halfa.api.HEngine;
import net.thevpc.halfa.api.document.HDocument;
import net.thevpc.halfa.api.document.HMessageList;
import net.thevpc.halfa.api.document.HMessageListImpl;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.spi.renderer.AbstractHDocumentStreamRenderer;
import net.thevpc.halfa.spi.renderer.HDocumentRendererContext;
import net.thevpc.halfa.spi.renderer.HDocumentRendererSupplier;
import net.thevpc.halfa.spi.renderer.HDocumentStreamRenderer;
import net.thevpc.halfa.spi.util.PagesHelper;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NIOException;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 * @author vpc
 */
public class PdfDocumentRenderer extends AbstractHDocumentStreamRenderer implements HDocumentStreamRenderer {

    private HDocumentRendererContext rendererContext = new HDocumentRendererContextImpl();

    public PdfDocumentRenderer(HEngine engine, NSession session) {
        super(engine, session);
    }

    @Override
    public void renderSupplier(HDocumentRendererSupplier document) {
        HDocument d = document.get(rendererContext);
        Object o = output;
        if (o == null) {
            o = NPath.of("document.pdf", session);
        }
        if (o instanceof NPath) {
            try (OutputStream os = ((NPath) o).getOutputStream()) {
                renderStream(d, os);
            } catch (IOException ex) {
                throw new NIOException(session, ex);
            }
        } else if (o instanceof OutputStream) {
            renderStream(d, (OutputStream) o);
        }
    }

    public void renderStream(HDocument document, OutputStream stream) {
        try {
            HMessageList messages2 = this.messages;
            if (messages2 == null) {
                messages2 = new HMessageListImpl(session, engine.computeSource(document.root()));
            }
            document = engine.compileDocument(document, messages2).get();
            HDocumentStreamRenderer htmlRenderer = engine.newStreamRenderer("html");
            List<Supplier<InputStream>> all = new ArrayList<>();
            for (HNode page : PagesHelper.resolvePages(document)) {
                Supplier<InputStream> y = renderPage(page, htmlRenderer);
                if (y != null) {
                    all.add(y);
                }
            }
            mergePdfFiles(all, stream);
        } catch (IOException | DocumentException ex) {
            throw new NIOException(session, ex);
        }
    }

    @Override
    public void renderNode(HNode part, OutputStream out) {
        try {
            HDocumentStreamRenderer htmlRenderer = engine.newStreamRenderer("html");
            List<Supplier<InputStream>> all = new ArrayList<>();
            for (HNode page : PagesHelper.resolvePages(part)) {
                Supplier<InputStream> y = renderPage(page, htmlRenderer);
                if (y != null) {
                    all.add(y);
                }
            }
            mergePdfFiles(all, out);
        } catch (IOException | DocumentException ex) {
            throw new NIOException(session, ex);
        }
    }

    public Supplier<InputStream> renderPage(HNode part, HDocumentStreamRenderer htmlRenderer) {
        try {
            ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(bos);
            htmlRenderer.renderNode(part, ps);
            ps.flush();
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(bos.toString(), "./base.html");
            renderer.layout();
            renderer.createPDF(bos2);
            //perhaps I need to store to disk
            return () -> new ByteArrayInputStream(bos2.toByteArray());
        } catch (DocumentException e) {
            throw new NIllegalArgumentException(session, NMsg.ofC("invalid parse %s", "file"), e);
        }
    }

    // thanks to
    // https://stackoverflow.com/questions/3585329/how-to-merge-two-pdf-files-into-one-in-java
    private void mergePdfFiles(
            List<Supplier<InputStream>> inputPdfList,
            OutputStream outputStream) throws IOException, DocumentException {
        //Create document and pdfReader objects.
        Document document = new Document();
        List<PdfReader> readers
                = new ArrayList<PdfReader>();
        int totalPages = 0;

        //Create pdf Iterator object using inputPdfList.
        Iterator<Supplier<InputStream>> pdfIterator
                = inputPdfList.iterator();

        // Create reader list for the input pdf files.
        while (pdfIterator.hasNext()) {
            try (InputStream pdf = pdfIterator.next().get()) {
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
                totalPages = totalPages + pdfReader.getNumberOfPages();
            }
        }

        // Create writer for the outputStream
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        //Open document.
        document.open();

        //Contain the pdf data.
        PdfContentByte pageContentByte = writer.getDirectContent();

        PdfImportedPage pdfImportedPage;
        int currentPdfReaderPage = 1;
        Iterator<PdfReader> iteratorPDFReader = readers.iterator();

        // Iterate and process the reader list.
        while (iteratorPDFReader.hasNext()) {
            PdfReader pdfReader = iteratorPDFReader.next();
            //Create page and add content.
            while (currentPdfReaderPage <= pdfReader.getNumberOfPages()) {
                document.newPage();
                pdfImportedPage = writer.getImportedPage(
                        pdfReader, currentPdfReaderPage);
                pageContentByte.addTemplate(pdfImportedPage, 0, 0);
                currentPdfReaderPage++;
            }
            currentPdfReaderPage = 1;
        }

        //Close document and outputStream.
        outputStream.flush();
        document.close();
    }

    public void renderPagePart(HNode part, PrintStream out) {
        switch (part.type()) {
            case HNodeType.PAGE_GROUP:
                break;
            case HNodeType.PAGE:
                break;
            default:
                throw new IllegalArgumentException("invalid type " + part);
        }
    }

    private class HDocumentRendererContextImpl implements HDocumentRendererContext {

        public HDocumentRendererContextImpl() {
        }

        @Override
        public HMessageList messages() {
            return messages;
        }
    }
}
