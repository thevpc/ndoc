/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.renderer.pdf;

import com.itextpdf.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;

import java.awt.*;
import java.awt.image.BufferedImage;
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
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.base.renderer.HNodeRendererContextBase;
import net.thevpc.halfa.spi.HNodeRenderer;
import net.thevpc.halfa.spi.renderer.*;
import net.thevpc.halfa.spi.util.PagesHelper;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NIOException;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.imageio.ImageIO;

/**
 * @author vpc
 */
public class PdfDocumentRenderer extends AbstractHDocumentStreamRenderer implements HDocumentStreamRenderer {

    private HDocumentRendererContext rendererContext = new HDocumentRendererContextImpl();


    public PdfDocumentRenderer(HEngine engine, NSession session, HDocumentStreamRendererConfig config) {
        super(engine, session);
        this.config = config;

    }
    @Override
    public void renderSupplier(HDocumentRendererSupplier documentSupplier) {
        HDocument document = documentSupplier.get(rendererContext);
        Object outputTarget = output;
        if (outputTarget == null) {
            outputTarget = NPath.of("document.pdf", session);
        }
        if (outputTarget instanceof NPath) {
            try (OutputStream os = ((NPath) outputTarget).getOutputStream()) {
                renderStream(document, os, config);
            } catch (IOException ex) {
                throw new NIOException(session, ex);
            }
        } else if (outputTarget instanceof OutputStream) {
            renderStream(document, (OutputStream) outputTarget, config);
        }
    }

    public void renderStream(HDocument document, OutputStream stream, HDocumentStreamRendererConfig config) {
        Document pdfDocument = new Document();
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, stream);
            applyConfigSettings(pdfDocument, pdfWriter, config);
            pdfDocument.open();
            addContent(pdfDocument, config);

            HMessageList messages = this.messages != null ?
                    this.messages : new HMessageListImpl(session, engine.computeSource(document.root()));

            int imagesPerPage = config.getPagenumber();
            int imagesPerRow = (int) Math.ceil(Math.sqrt(imagesPerPage));
//            int totalCells = imagesPerPage;

            List<HNode> pages = PagesHelper.resolvePages(document);
            int imageCount = 0;

            float usableWidth = pdfDocument.getPageSize().getWidth() - pdfDocument.leftMargin() - pdfDocument.rightMargin();
            float usableHeight = pdfDocument.getPageSize().getHeight() - pdfDocument.topMargin() - pdfDocument.bottomMargin();
            float cellWidth = usableWidth / imagesPerRow;
            float cellHeight = usableHeight / (float) Math.ceil((double) imagesPerPage / imagesPerRow);

            PdfPTable table = null;

            for (HNode page : pages) {
                if (imageCount % imagesPerPage == 0) {
                    if (table != null) {
                        pdfDocument.add(table);
                        pdfDocument.newPage();
                    }
                    table = new PdfPTable(imagesPerRow);
                    table.setWidthPercentage(95);
                    table.setSpacingBefore(8);
                }

                byte[] imageData = createPageImage((int) cellWidth, (int) cellHeight, page, messages);
                Image img = Image.getInstance(imageData);
                img.scaleToFit(cellWidth, cellHeight);

                PdfPCell cell = new PdfPCell(img, true);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setPadding(10f);

                table.addCell(cell);
                imageCount++;
            }

            if (table != null) {
                while (imageCount % imagesPerPage != 0) {
                    PdfPCell emptyCell = new PdfPCell();
                    emptyCell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(emptyCell);
                    emptyCell.setPadding(10f);

                    imageCount++;
                }
                pdfDocument.add(table);
            }

        } catch (Exception ex) {
            throw new NIOException(session, ex);
        } finally {
            if (pdfDocument.isOpen()) {
                pdfDocument.close();
            }
        }
    }

    private byte[] createPageImage(int sizeWidth, int sizeHeight, HNode page, HMessageList messages) {
        BufferedImage newImage = new BufferedImage(sizeWidth, sizeHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        HGraphics hg = engine.createGraphics(g);
        HNodeRenderer renderer = engine.renderManager().getRenderer(page.type()).get();
        renderer.render(page, new PdfHNodeRendererContext(engine, hg, new Dimension(sizeWidth, sizeHeight), session, messages));

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ImageIO.write(newImage, "png", bos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            g.dispose();
        }

        return bos.toByteArray();
    }

    private void applyConfigSettings(Document document, PdfWriter pdfWriter, HDocumentStreamRendererConfig config) throws DocumentException {
        if (config != null) {
            if (config.getOrientation() == PageOrientation.LANDSCAPE) {
                document.setPageSize(PageSize.A4.rotate());
            } else {
                document.setPageSize(PageSize.A4);
            }
            document.setMargins(0f, 0f, 0f, 0f);

            if (config.isShowPageNumber()) {
                PageNumberEvent pageNumberEvent = new PageNumberEvent();
                pdfWriter.setPageEvent(pageNumberEvent);
            }
        }
    }

    private void addContent(Document document, HDocumentStreamRendererConfig config) throws DocumentException {
        if (config != null) {
            if (config.isShowFileName()) {
                String fileName = "MyDocument.pdf";
                PdfPTable table = new PdfPTable(1);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10);
                PdfPCell cell = new PdfPCell(new Phrase("File: " + fileName));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                document.add(table);
            }

            if (config.isShowDate()) {
                java.util.Date date = new java.util.Date();
                PdfPTable table = new PdfPTable(1);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10);
                PdfPCell cell = new PdfPCell(new Phrase("Date: " + date.toString()));
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
                document.add(table);
            }
        }
    }

    private class PageNumberEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase("Page " + writer.getPageNumber());
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, header, document.right() - 50, document.top() + 10, 0);
        }
    }



//}
//
//
//    private byte[] createPageImage(int sizeWidth, int sizeHeight, HNode page, HMessageList messages2) {
//        BufferedImage newImage = new BufferedImage(
//                sizeWidth, sizeHeight, BufferedImage.TYPE_INT_ARGB);
//
//        Graphics2D g = newImage.createGraphics();
//        HGraphics gh = engine.createGraphics(g);
//        HNodeRenderer renderer = engine.renderManager().getRenderer(page.type()).get();
//        renderer.render(page, new PdfHNodeRendererContext(engine, gh, new Dimension(sizeWidth, sizeHeight), session, messages2));
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try {
//            ImageIO.write(newImage, "png", bos);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return bos.toByteArray();
//    }


//    public void renderStream(HDocument document, OutputStream stream) {
//        try {
//            HMessageList messages2 = this.messages;
//            if (messages2 == null) {
//                messages2 = new HMessageListImpl(session, engine.computeSource(document.root()));
//            }
//            document = engine.compileDocument(document, messages2).get();
//            HDocumentStreamRenderer htmlRenderer = engine.newStreamRenderer("html");
//            List<Supplier<InputStream>> all = new ArrayList<>();
//            for (HNode page : PagesHelper.resolvePages(document)) {
//                Supplier<InputStream> y = renderPage(page, htmlRenderer);
//                if (y != null) {
//                    all.add(y);
//                }
//            }
//            mergePdfFiles(all, stream);
//        } catch (IOException | DocumentException ex) {
//            throw new NIOException(session, ex);
//        }
//    }

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

    private static class PdfHNodeRendererContext extends HNodeRendererContextBase {

        public PdfHNodeRendererContext(HEngine engine, HGraphics g, Dimension size, NSession session, HMessageList messages) {
            super(engine, g, size, session, messages);
            setCapability("print", true);
            setCapability("animated", false);
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
