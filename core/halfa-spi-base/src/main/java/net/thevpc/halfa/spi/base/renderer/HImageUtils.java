package net.thevpc.halfa.spi.base.renderer;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HImageUtils {
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
