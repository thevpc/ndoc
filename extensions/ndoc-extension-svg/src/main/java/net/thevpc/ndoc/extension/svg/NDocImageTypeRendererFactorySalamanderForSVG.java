package net.thevpc.ndoc.extension.svg;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;
import com.kitfox.svg.app.beans.SVGIcon;
import net.thevpc.ndoc.api.model.elem2d.HImageOptions;
import net.thevpc.ndoc.spi.NDocImageTypeRendererFactory;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocGraphicsImageDrawer;
import net.thevpc.nuts.NCallableSupport;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class NDocImageTypeRendererFactorySalamanderForSVG implements NDocImageTypeRendererFactory {
    @Override
    public NCallableSupport<NDocGraphicsImageDrawer> resolveRenderer(NPath path, HImageOptions options, NDocGraphics graphics) {
        if (path.getName().toLowerCase().endsWith(".svg")) {
            return NCallableSupport.of(10,
                    () -> {
                        return new SvgNDocImageByTypeRenderer(path);
                    }
            );
        }
        return NCallableSupport.invalid(() -> NMsg.ofC("not supported %s", path));
    }

    private static SVGIcon createSVGIconFromBytes(byte[] svgBytes) {
        // Load SVG from byte array
        SVGUniverse svgUniverse = new SVGUniverse();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(svgBytes);
        URI uri = null;
        try {
            uri = svgUniverse.loadSVG(byteArrayInputStream, "svgFromBytes");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SVGDiagram diagram = svgUniverse.getDiagram(uri);

        // Create SVGIcon
        SVGIcon icon = new SVGIcon();
        icon.setSvgURI(uri);
        icon.setSvgUniverse(svgUniverse);
        return icon;
    }



    private static class SvgNDocImageByTypeRenderer implements NDocGraphicsImageDrawer {
        private final NPath path;

        public SvgNDocImageByTypeRenderer(NPath path) {
            this.path = path;
        }

        @Override
        public void drawImage(double x, double y, HImageOptions options, NDocGraphics g) {
            if(!path.exists()){
                throw new IllegalArgumentException("file not found "+path);
            }
            URL u = path.toURL().orNull();
            SVGDiagram diagram;
            URI uri = null;
            SVGUniverse svgUniverse = new SVGUniverse();
            if(u!=null){
                uri = svgUniverse.loadSVG(u);
                diagram = svgUniverse.getDiagram(uri);
            }else {
                // Load SVG from byte array
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(path.readBytes());
                try {
                    uri = svgUniverse.loadSVG(byteArrayInputStream, "svgFromBytes");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                diagram = svgUniverse.getDiagram(uri);
            }

            // Create SVGIcon
            SVGIcon icon = new SVGIcon();
            icon.setSvgURI(uri);
            icon.setScaleToFit(true);
            icon.setSvgUniverse(svgUniverse);
            icon.setAntiAlias(true);
            icon.setPreferredSize(options.getSize());
            icon.paintIcon(null, g.graphics2D(), (int) x, (int) y);
        }
    }
}
