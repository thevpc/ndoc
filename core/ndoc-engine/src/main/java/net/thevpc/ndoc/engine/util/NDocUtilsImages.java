package net.thevpc.ndoc.engine.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class NDocUtilsImages {
    public static BufferedImage toBufferedImage(Image originalImage) {
        if (originalImage instanceof BufferedImage) {
            return (BufferedImage) originalImage;
        }
        BufferedImage bimage = new BufferedImage(originalImage.getWidth(null), originalImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(originalImage, 0, 0, null);
        bGr.dispose();
        return bimage;
    }

    public static Image resizeImage(Image originalImage, int targetWidth, int targetHeight) {
        return originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);

//        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
//        Graphics2D graphics2D = resizedImage.createGraphics();
//        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
//        graphics2D.dispose();
//        return resizedImage;
    }

    public static BufferedImage resize(BufferedImage img, Dimension size) {
        if(size==null){
            return img;
        }
        int newW=size.width;
        int newH=size.height;
        int width = img.getWidth();
        int height = img.getHeight();
        if(newH==width && height==newH){
            return img;
//            BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
//            Graphics2D g2d = dimg.createGraphics();
//            g2d.drawImage(img, 0, 0, null);
//            g2d.dispose();
//            return dimg;
        }
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int width = img.getWidth();
        int height = img.getHeight();
        if(newH==width && height==newH){
            BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = dimg.createGraphics();
            g2d.drawImage(img, 0, 0, null);
            g2d.dispose();
            return dimg;
        }
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
}
