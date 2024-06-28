package net.thevpc.halfa.engine.renderer.screen.renderers.images;


import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class GifResizer {



    public static byte[] transform(byte[] source, Function<BufferedImage,BufferedImage> transform) {

        List<BufferedImage> images = new ArrayList<>(25);
        List<Integer> delays = new ArrayList<>(25);
        int delay = 0;

        ImageOutputStream output = null;
        GifSequenceWriter writer = null;

        try {

            String[] imageatt = new String[]{
                    "imageLeftPosition",
                    "imageTopPosition",
                    "imageWidth",
                    "imageHeight"
            };

            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            ImageInputStream ciis = ImageIO.createImageInputStream(new ByteArrayInputStream(source));
            reader.setInput(ciis, false);
            int noi = reader.getNumImages(true);
            BufferedImage master = null;

            for (int i = 0; i < noi; i++) {

                BufferedImage image = reader.read(i);
                IIOMetadata metadata = reader.getImageMetadata(i);

                Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
                NodeList children = tree.getChildNodes();
                for (int j = 0; j < children.getLength(); j++) {
                    Node nodeItem = children.item(j);
                    System.out.println(nodeItem.getNodeName());
                    if (nodeItem.getNodeName().equals("ImageDescriptor")) {
                        Map<String, Integer> imageAttr = new HashMap<String, Integer>();
                        NamedNodeMap attr = nodeItem.getAttributes();
//                        for (int index = 0; index < attr.getLength(); index++) {
//                            Node node = attr.item(index);
//                            System.out.println("----> " + node.getNodeName() + "=" + node.getNodeValue());
//                        }
                        for (int k = 0; k < imageatt.length; k++) {
                            Node attnode = attr.getNamedItem(imageatt[k]);
                            imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
                        }

                        if (master == null) {
                            master = new BufferedImage(imageAttr.get("imageWidth"), imageAttr.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
                        }

                        Graphics2D g2d = master.createGraphics();
                        g2d.drawImage(image, imageAttr.get("imageLeftPosition"), imageAttr.get("imageTopPosition"), null);
                        g2d.dispose();

//                        BufferedImage frame = mirror(copyImage(master));
                        BufferedImage frame = copyImage(master);
                        frame=transform.apply(frame);
                        //ImageIO.write(frame, "png", new File("img" + i + ".png"));
                        images.add(frame);

                    } else if (nodeItem.getNodeName().equals("GraphicControlExtension")) {
                        NamedNodeMap attr = nodeItem.getAttributes();
                        Node delayNode = attr.getNamedItem("delayTime");
                        if (delayNode != null) {
                            delay = Math.max(delay, Integer.valueOf(delayNode.getNodeValue()));
                            delays.add(delay);
                        }
                    }
                }

            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            output = new MemoryCacheImageOutputStream(bos);
            writer = new GifSequenceWriter(output, images.get(0).getType(), delay * 10, true);

            for (int i = 0; i < images.size(); i++) {
                BufferedImage nextImage = images.get(i);
                writer.writeToSequence(nextImage);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new IllegalArgumentException(e);
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            }
            try {
                output.close();
            } catch (Exception e) {
            }
        }
    }

    public static BufferedImage transform(BufferedImage img) {

        BufferedImage mirror = createCompatibleImage(img);
        Graphics2D g2d = mirror.createGraphics();
        AffineTransform at = new AffineTransform();
        at.setToScale(1, -1);
        at.translate(0, -img.getHeight());
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return mirror;

    }

    public static BufferedImage copyImage(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        BufferedImage newImage = createCompatibleImage(img);
        Graphics graphics = newImage.createGraphics();

        int x = 0;//(width - img.getWidth()) / 2;
        int y = 0;//(height - img.getHeight()) / 2;

        graphics.drawImage(img, x, y, img.getWidth(), img.getHeight(), null);
        graphics.dispose();

        return newImage;
    }

    public static BufferedImage createCompatibleImage(BufferedImage image) {
        return getGraphicsConfiguration().createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());
    }

    public static GraphicsConfiguration getGraphicsConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }

    public static class GifSequenceWriter {

        protected ImageWriter gifWriter;
        protected ImageWriteParam imageWriteParam;
        protected IIOMetadata imageMetaData;

        /**
         * Creates a new GifSequenceWriter
         *
         * @param outputStream        the ImageOutputStream to be written to
         * @param imageType           one of the imageTypes specified in BufferedImage
         * @param timeBetweenFramesMS the time between frames in miliseconds
         * @param loopContinuously    wether the gif should loop repeatedly
         * @throws IIOException if no gif ImageWriters are found
         * @author Elliot Kroo (elliot[at]kroo[dot]net)
         */
        public GifSequenceWriter(
                ImageOutputStream outputStream,
                int imageType,
                int timeBetweenFramesMS,
                boolean loopContinuously) throws IIOException, IOException {
            // my method to create a writer
            gifWriter = getWriter();
            imageWriteParam = gifWriter.getDefaultWriteParam();
            ImageTypeSpecifier imageTypeSpecifier
                    = ImageTypeSpecifier.createFromBufferedImageType(imageType);

            imageMetaData
                    = gifWriter.getDefaultImageMetadata(imageTypeSpecifier,
                    imageWriteParam);

            String metaFormatName = imageMetaData.getNativeMetadataFormatName();

            IIOMetadataNode root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

            IIOMetadataNode graphicsControlExtensionNode = getNode(
                    root,
                    "GraphicControlExtension");

            //restoreToBackgroundColor
            //restoreToPrevious
            graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
            graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
            graphicsControlExtensionNode.setAttribute(
                    "transparentColorFlag",
                    "FALSE");
            graphicsControlExtensionNode.setAttribute(
                    "delayTime",
                    Integer.toString(timeBetweenFramesMS / 10));
            graphicsControlExtensionNode.setAttribute(
                    "transparentColorIndex",
                    "0");

            IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
            commentsNode.setAttribute("CommentExtension", "Created by MAH");

            IIOMetadataNode appEntensionsNode = getNode(
                    root,
                    "ApplicationExtensions");

            IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

            child.setAttribute("applicationID", "NETSCAPE");
            child.setAttribute("authenticationCode", "2.0");

            int loop = loopContinuously ? 0 : 1;

            child.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)});
            appEntensionsNode.appendChild(child);

            imageMetaData.setFromTree(metaFormatName, root);

            gifWriter.setOutput(outputStream);

            gifWriter.prepareWriteSequence(null);
        }

        public void writeToSequence(RenderedImage img) throws IOException {
            gifWriter.writeToSequence(
                    new IIOImage(
                            img,
                            null,
                            imageMetaData),
                    imageWriteParam);
        }

        /**
         * Close this GifSequenceWriter object. This does not close the
         * underlying stream, just finishes off the GIF.
         */
        public void close() throws IOException {
            gifWriter.endWriteSequence();
        }

        /**
         * Returns the first available GIF ImageWriter using
         * ImageIO.getImageWritersBySuffix("gif").
         *
         * @return a GIF ImageWriter object
         * @throws IIOException if no GIF image writers are returned
         */
        private static ImageWriter getWriter() throws IIOException {
            Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
            if (!iter.hasNext()) {
                throw new IIOException("No GIF Image Writers Exist");
            } else {
                return iter.next();
            }
        }

        /**
         * Returns an existing child node, or creates and returns a new child
         * node (if the requested node does not exist).
         *
         * @param rootNode the <tt>IIOMetadataNode</tt> to search for the child
         *                 node.
         * @param nodeName the name of the child node.
         * @return the child node, if found or a new node created with the given
         * name.
         */
        private static IIOMetadataNode getNode(
                IIOMetadataNode rootNode,
                String nodeName) {
            int nNodes = rootNode.getLength();
            for (int i = 0; i < nNodes; i++) {
                if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)
                        == 0) {
                    return ((IIOMetadataNode) rootNode.item(i));
                }
            }
            IIOMetadataNode node = new IIOMetadataNode(nodeName);
            rootNode.appendChild(node);
            return (node);
        }
    }
}