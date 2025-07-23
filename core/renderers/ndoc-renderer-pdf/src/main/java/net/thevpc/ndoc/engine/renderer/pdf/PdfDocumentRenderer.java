/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.engine.renderer.pdf;

import com.itextpdf.text.Rectangle;
import com.lowagie.text.*;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.*;

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

import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.document.NDocument;
import net.thevpc.ndoc.api.engine.NDocLogger;
import net.thevpc.ndoc.api.engine.DefaultNDocLogger;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.base.renderer.HSpiUtils;
import net.thevpc.ndoc.api.renderer.*;
import net.thevpc.ndoc.api.util.PagesHelper;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.io.NIOException;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import org.xhtmlrenderer.pdf.ITextRenderer;

/**
 * @author vpc
 */
public class PdfDocumentRenderer extends AbstractNDocDocumentStreamRenderer implements NDocDocumentStreamRenderer {

    private NDocDocumentRendererContext rendererContext = new NDocDocumentRendererContextImpl();


    public PdfDocumentRenderer(NDocEngine engine, NDocDocumentStreamRendererConfig config) {
        super(engine);
        this.config = config;

    }

    @Override
    public void renderSupplier(NDocDocumentRendererSupplier documentSupplier) {
        NDocument document = documentSupplier.get(rendererContext);
        NDocument compiledDocument = engine.compileDocument(document.copy()).get();
        Object outputTarget = output;
        if (outputTarget == null) {
            outputTarget = NPath.of("document.pdf");
        }
        if (outputTarget instanceof NPath) {
            try (OutputStream os = ((NPath) outputTarget).getOutputStream()) {
                renderStream(compiledDocument, os);
            } catch (IOException ex) {
                throw new NIOException(ex);
            }
        } else if (outputTarget instanceof OutputStream) {
            renderStream(compiledDocument, (OutputStream) outputTarget);
        }
    }


    public void renderStream(NDocument document, OutputStream stream) {
        NDocDocumentStreamRendererConfig config = HSpiUtils.validateConfig(this.config);
        Document pdfDocument = new Document();
        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, stream);
            applyConfigSettings(pdfDocument, pdfWriter);
            pdfDocument.open();
            addContent(pdfDocument);

            NDocLogger messages = this.messages != null ? this.messages : new DefaultNDocLogger(engine.computeSource(document.root()));

            int imagesPerRow = config.getGridX();
            int imagesPerColumn = config.getGridY();
            int imagesPerPage = imagesPerRow * imagesPerColumn;
            List<NDocNode> pages = document.pages();
            int imageCount = 0;
            float margin = 10f;
            float marginLeft = config.getMarginLeft() >= 0 ? config.getMarginLeft() : 0;
            float marginRight = config.getMarginRight() >= 0 ? config.getMarginRight() : 0;
            float marginTop = config.getMarginTop() >= 0 ? config.getMarginTop() : 0;
            float marginBottom = config.getMarginBottom() >= 0 ? config.getMarginBottom() : 0;

            float usableWidth;
            float usableHeight;

            if (config.getOrientation() == NDocPageOrientation.LANDSCAPE) {
                usableWidth = PageSize.A4.getHeight() - marginLeft - marginRight - 10f;
                usableHeight = PageSize.A4.getWidth() - marginTop - marginBottom - 10f;
            } else {
                usableWidth = PageSize.A4.getWidth() - marginLeft - marginRight - 10f;
                usableHeight = PageSize.A4.getHeight() - marginTop - marginBottom - 10f;
            }

            float totalMarginWidth = (imagesPerRow - 1) * margin;
            float totalMarginHeight = (imagesPerColumn - 1) * margin;

            float cellWidth = (usableWidth - totalMarginWidth) / imagesPerRow;
            float cellHeight = (usableHeight - totalMarginHeight) / imagesPerColumn;

            PdfPTable table = null;

            for (NDocNode page : pages) {
                if (imageCount % imagesPerPage == 0) {
                    if (table != null) {
                        pdfDocument.add(table);
                        pdfDocument.newPage();
                    }
                    table = new PdfPTable(imagesPerRow);
                    table.setWidthPercentage(100);
                    table.setTotalWidth(usableWidth);
                    table.setLockedWidth(true);
                }
                Image img = Image.getInstance(engine.renderManager().renderImageBytes(
                        page,
                        new NDocNodeRendererConfig((int) cellWidth, (int) cellHeight)
                                .withAnimate(false)
                                .withPrint(true)
                                .setMessages(messages)
                ));
                img.scaleToFit(cellWidth, cellHeight);

                PdfPCell cell = new PdfPCell(img, true);
                cell.setFixedHeight(cellHeight);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);

                // Adding margins between cells
                if (imageCount % imagesPerRow != 0) {
                    cell.setPaddingLeft(margin / 2);
                }
                if ((imageCount + 1) % imagesPerRow != 0) {
                    cell.setPaddingRight(margin / 2);
                }
                if (imageCount >= imagesPerRow) {
                    cell.setPaddingTop(margin / 2);
                }
                if (imageCount < imagesPerPage - imagesPerRow) {
                    cell.setPaddingBottom(margin / 2);
                }

                table.addCell(cell);
                imageCount++;
            }

            if (table != null) {
                while (imageCount % imagesPerPage != 0) {
                    PdfPCell emptyCell = new PdfPCell();
                    emptyCell.setBorder(Rectangle.NO_BORDER);
                    emptyCell.setFixedHeight(cellHeight);
                    table.addCell(emptyCell);

                    imageCount++;
                }
                pdfDocument.add(table);
            }

            if (config.isShowDate() || config.isShowFileName()) {
                pdfDocument.newPage();
                PdfPTable footerTable = new PdfPTable(1);
                footerTable.setWidthPercentage(100);
                footerTable.setSpacingBefore(10);

                if (config.isShowFileName()) {
                    String fileName = "MyDocument.pdf";
                    PdfPCell fileNameCell = new PdfPCell(new Phrase("File: " + fileName));
                    fileNameCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    fileNameCell.setBorder(Rectangle.NO_BORDER);
                    footerTable.addCell(fileNameCell);
                }

                if (config.isShowDate()) {
                    java.util.Date date = new java.util.Date();
                    PdfPCell dateCell = new PdfPCell(new Phrase("Date: " + date.toString()));
                    dateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    dateCell.setBorder(Rectangle.NO_BORDER);
                    footerTable.addCell(dateCell);
                }

                pdfDocument.add(footerTable);
            }

        } catch (Exception ex) {
            throw new NIOException(ex);
        } finally {
            if (pdfDocument.isOpen()) {
                pdfDocument.close();
            }
        }
    }

    private void applyConfigSettings(Document document, PdfWriter pdfWriter) throws DocumentException {
        NDocDocumentStreamRendererConfig config = HSpiUtils.validateConfig(this.config);
        if (config.getOrientation() == NDocPageOrientation.LANDSCAPE) {
            document.setPageSize(PageSize.A4.rotate());
        } else {
            document.setPageSize(PageSize.A4);
        }
        document.setMargins(config.getMarginLeft(), config.getMarginRight(), config.getMarginTop(), config.getMarginBottom());
        if (config.isShowPageNumber()) {
            PageNumberEvent pageNumberEvent = new PageNumberEvent();
            pdfWriter.setPageEvent(pageNumberEvent);
        }
    }


    private void addContent(Document document) throws DocumentException {
        NDocDocumentStreamRendererConfig config = HSpiUtils.validateConfig(this.config);
        if (config.isShowFileName()) {
            String fileName = "my-document.pdf";
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
//    private byte[] createPageImage(int sizeWidth, int sizeHeight, NDocNode page, NDocMessageList messages2) {
//        BufferedImage newImage = new BufferedImage(
//                sizeWidth, sizeHeight, BufferedImage.TYPE_INT_ARGB);
//
//        Graphics2D g = newImage.createGraphics();
//        NDocGraphics gh = engine.createGraphics(g);
//        NDocNodeRenderer renderer = engine.renderManager().getRenderer(page.type()).get();
//        renderer.render(page, new PdfNDocNodeRendererContext(engine, gh, new Dimension(sizeWidth, sizeHeight), session, messages2));
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        try {
//            ImageIO.write(newImage, "png", bos);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return bos.toByteArray();
//    }


//    public void renderStream(NDocDocument document, OutputStream stream) {
//        try {
//            NDocMessageList messages2 = this.messages;
//            if (messages2 == null) {
//                messages2 = new NDocMessageListImpl(session, engine.computeSource(document.root()));
//            }
//            document = engine.compileDocument(document, messages2).get();
//            NDocDocumentStreamRenderer htmlRenderer = engine.newStreamRenderer("html");
//            List<Supplier<InputStream>> all = new ArrayList<>();
//            for (NDocNode page : document.pages()) {
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
    public NDocDocumentStreamRenderer renderNode(NDocNode part, OutputStream out) {
        try {
            NDocDocumentStreamRenderer htmlRenderer = engine.newHtmlRenderer().get();
            List<Supplier<InputStream>> all = new ArrayList<>();
            for (NDocNode page : PagesHelper.resolvePages(part)) {
                Supplier<InputStream> y = renderPage(page, htmlRenderer);
                if (y != null) {
                    all.add(y);
                }
            }
            mergePdfFiles(all, out);
        } catch (IOException | DocumentException ex) {
            throw new NIOException(ex);
        }
        return this;
    }

    public Supplier<InputStream> renderPage(NDocNode part, NDocDocumentStreamRenderer htmlRenderer) {
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
            throw new NIllegalArgumentException(NMsg.ofC("invalid parse %s", "file"), e);
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

    public void renderPagePart(NDocNode part, PrintStream out) {
        switch (part.type()) {
            case NDocNodeType.PAGE_GROUP:
                break;
            case NDocNodeType.PAGE:
                break;
            default:
                throw new IllegalArgumentException("invalid type " + part);
        }
    }

    private class NDocDocumentRendererContextImpl implements NDocDocumentRendererContext {

        public NDocDocumentRendererContextImpl() {
        }

        @Override
        public NDocLogger messages() {
            return messages;
        }
    }
}
