/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.engine.renderer.screen.common;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author vpc
 */
public class ImageUtils {

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
}
