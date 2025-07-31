//package net.thevpc.ndoc.elem.base.image;
//
//import net.thevpc.ndoc.api.document.elem2d.NDocImageOptions;
//import net.thevpc.ndoc.api.renderer.NDocGraphics;
//import net.thevpc.ndoc.api.renderer.NDocGraphicsImageDrawer;
//
//import java.awt.image.BufferedImage;
//
//class BufferedImageDrawer implements NDocGraphicsImageDrawer {
//    private final BufferedImage img;
//
//    public BufferedImageDrawer(BufferedImage img) {
//        this.img = img;
//    }
//
//    @Override
//    public void drawImage(double x, double y, NDocImageOptions options, NDocGraphics g) {
//        BufferedImage image = HImageUtils.resize(img, options.getSize());
//        g.drawImage(image, (int) x, (int) y, options.getImageObserver());
//    }
//}
