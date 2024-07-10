package net.thevpc.halfa.spi.renderer;

public class HDocumentStreamRendererConfig {
    private PageOrientation orientation;
    private int gridX;
    private int gridY;
    private int pageWidth;
    private int pageHeight;
    private boolean showPageNumber;
    private boolean showFileName;
    private boolean showDate;
    private int pagenumber;
    private float marginTop;
    private float marginBottom;
    private float marginLeft;
    private float marginRight;

    // getters and setters
    public float getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public float getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(float marginBottom) {
        this.marginBottom = marginBottom;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public float getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(float marginRight) {
        this.marginRight = marginRight;
    }
    public PageOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(PageOrientation orientation) {
        this.orientation = orientation;
    }

    public int getGridX() {
        return gridX;
    }

    public void setGridX(int gridX) {
        this.gridX = gridX;
    }

    public int getGridY() {
        return gridY;
    }

    public void setGridY(int gridY) {
        this.gridY = gridY;
    }

    public int getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(int pageWidth) {
        this.pageWidth = pageWidth;
    }

    public int getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(int pageHeight) {
        this.pageHeight = pageHeight;
    }

    public boolean isShowPageNumber() {
        return showPageNumber;
    }

    public void setShowPageNumber(boolean showPageNumber) {
        this.showPageNumber = showPageNumber;
    }

    public boolean isShowFileName() {
        return showFileName;
    }

    public void setShowFileName(boolean showFileName) {
        this.showFileName = showFileName;
    }

    public boolean isShowDate() {
        return showDate;
    }

    public void setShowDate(boolean showDate) {
        this.showDate = showDate;
    }

    public void setImagesPerPage(int selectedItem) {
        this.pagenumber = selectedItem;
    }
    public int getPagenumber() {
        return pagenumber;
    }

//    public void setSizePage(int i) {
//        this.sizepage = i;
//    }
//    public int getSizePage() {
//        return sizepage;
//    }
}
