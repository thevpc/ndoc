package net.thevpc.halfa.spi.renderer;

import java.io.Serializable;

public class HDocumentStreamRendererConfig implements Serializable, Cloneable {
    private PageOrientation orientation;
    private int gridX;
    private int gridY;
    private int pageWidth;
    private int pageHeight;
    private boolean showPageNumber;
    private boolean showFileName;
    private boolean showDate;
    private float marginTop;
    private float marginBottom;
    private float marginLeft;
    private float marginRight;

    public PageOrientation getOrientation() {
        return orientation;
    }

    public HDocumentStreamRendererConfig setOrientation(PageOrientation orientation) {
        this.orientation = orientation;
        return this;
    }

    public int getGridX() {
        return gridX;
    }

    public HDocumentStreamRendererConfig setGridX(int gridX) {
        this.gridX = gridX;
        return this;
    }

    public int getGridY() {
        return gridY;
    }

    public HDocumentStreamRendererConfig setGridY(int gridY) {
        this.gridY = gridY;
        return this;
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public HDocumentStreamRendererConfig setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
        return this;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public HDocumentStreamRendererConfig setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
        return this;
    }

    public boolean isShowPageNumber() {
        return showPageNumber;
    }

    public HDocumentStreamRendererConfig setShowPageNumber(boolean showPageNumber) {
        this.showPageNumber = showPageNumber;
        return this;
    }

    public boolean isShowFileName() {
        return showFileName;
    }

    public HDocumentStreamRendererConfig setShowFileName(boolean showFileName) {
        this.showFileName = showFileName;
        return this;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public HDocumentStreamRendererConfig setShowDate(boolean showDate) {
        this.showDate = showDate;
        return this;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public HDocumentStreamRendererConfig setMarginTop(float marginTop) {
        this.marginTop = marginTop;
        return this;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public HDocumentStreamRendererConfig setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public HDocumentStreamRendererConfig setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public HDocumentStreamRendererConfig setMarginRight(float marginRight) {
        this.marginRight = marginRight;
        return this;
    }

    public HDocumentStreamRendererConfig copy() {
        try {
            return (HDocumentStreamRendererConfig) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
