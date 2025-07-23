package net.thevpc.ndoc.api.renderer;

import java.io.Serializable;

public class NDocDocumentStreamRendererConfig implements Serializable, Cloneable {
    private NDocPageOrientation orientation;
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

    public NDocPageOrientation getOrientation() {
        return orientation;
    }

    public NDocDocumentStreamRendererConfig setOrientation(NDocPageOrientation orientation) {
        this.orientation = orientation;
        return this;
    }

    public int getGridX() {
        return gridX;
    }

    public NDocDocumentStreamRendererConfig setGridX(int gridX) {
        this.gridX = gridX;
        return this;
    }

    public int getGridY() {
        return gridY;
    }

    public NDocDocumentStreamRendererConfig setGridY(int gridY) {
        this.gridY = gridY;
        return this;
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public NDocDocumentStreamRendererConfig setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
        return this;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public NDocDocumentStreamRendererConfig setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
        return this;
    }

    public boolean isShowPageNumber() {
        return showPageNumber;
    }

    public NDocDocumentStreamRendererConfig setShowPageNumber(boolean showPageNumber) {
        this.showPageNumber = showPageNumber;
        return this;
    }

    public boolean isShowFileName() {
        return showFileName;
    }

    public NDocDocumentStreamRendererConfig setShowFileName(boolean showFileName) {
        this.showFileName = showFileName;
        return this;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public NDocDocumentStreamRendererConfig setShowDate(boolean showDate) {
        this.showDate = showDate;
        return this;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public NDocDocumentStreamRendererConfig setMarginTop(float marginTop) {
        this.marginTop = marginTop;
        return this;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public NDocDocumentStreamRendererConfig setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
        return this;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public NDocDocumentStreamRendererConfig setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
        return this;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public NDocDocumentStreamRendererConfig setMarginRight(float marginRight) {
        this.marginRight = marginRight;
        return this;
    }

    public NDocDocumentStreamRendererConfig copy() {
        try {
            return (NDocDocumentStreamRendererConfig) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
