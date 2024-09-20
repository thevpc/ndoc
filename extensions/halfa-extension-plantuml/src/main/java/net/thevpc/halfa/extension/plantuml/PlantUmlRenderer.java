package net.thevpc.halfa.extension.plantuml;

import net.sourceforge.plantuml.SourceStringReader;
import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.resources.HResource;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.halfa.spi.renderer.HNodeRendererBase;
import net.thevpc.halfa.spi.util.HNodeRendererUtils;
import net.thevpc.halfa.spi.base.renderer.HImageUtils;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PlantUmlRenderer extends HNodeRendererBase {
    HProperties defaultStyles = new HProperties();

    public PlantUmlRenderer() {
        super("plantuml", "diagram");
    }

    private String prepare(String type, String txt, Bounds2 b) {
        return "@start" + type + "\n"
                + "scale " + (b.getWidth().intValue()) + "*" + (b.getHeight().intValue()) + "\n"
                + "skinparam backgroundcolor transparent\n"
//                        + "skinparam dpi 300\n"
                + txt
                + "\n@end" + type + "\n";
    }

    @Override
    public void renderMain(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        String txt = ObjEx.of(p.getPropertyValue(HPropName.VALUE).orNull()).asStringOrName().orNull();
        String mode = ObjEx.of(p.getPropertyValue(HPropName.MODE).orNull()).asStringOrName().orNull();
        mode = HUtils.uid(mode);
        if (NBlankable.isBlank(txt)) {
            return;
        }
        HGraphics g = ctx.graphics();
        Bounds2 b = selfBounds(p, ctx);
        double x = b.getX();
        double y = b.getY();
        BufferedImage image = null;
        String plantUMLText = null;
        switch (mode) {
            case "": {
                plantUMLText = txt;
                break;
            }
            case "uml":
            case "json":
            case "yaml":
            case "ebnf":
            case "regex":
            case "ditaa":
            case "gantt":
            case "chronology":
            case "mindmap":
            case "wbs":
            case "chen":
            case "math":
            case "latex":
            {
                plantUMLText = prepare(mode, txt, b);
                break;
            }
            case "nwdiag": {
                plantUMLText = prepare("uml",
                        "nwdiag {\n"
                                + txt
                                + "\n}"
                        , b);
                break;
            }
            case "salt":
            case "wireframe": {
                plantUMLText = prepare("salt", txt, b);
                break;
            }
        }
        if (plantUMLText != null) {
            SourceStringReader reader = new SourceStringReader(plantUMLText);
            // Write the first image to "png"
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                reader.outputImage(bos);
                image = ImageIO.read(new ByteArrayInputStream(bos.toByteArray()));
            } catch (Exception ex) {
                HResource src = ctx.engine().computeSource(p);
                ctx.messages().addError(NMsg.ofC("Unable to evaluate UML : %s", ex), src);
            }
        }
        if (image != null) {

            if (!ctx.isDry()) {
                if (HNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fillRect((int) x, (int) y, net.thevpc.halfa.api.util.HUtils.intOf(b.getWidth()), net.thevpc.halfa.api.util.HUtils.intOf(b.getHeight()));
                }

                HNodeRendererUtils.applyForeground(p, g, ctx, false);
                if (image != null) {
                    // would resize?
                    int w = net.thevpc.halfa.api.util.HUtils.intOf(b.getWidth());
                    int h = net.thevpc.halfa.api.util.HUtils.intOf(b.getHeight());
                    if (w > 0 && h > 0) {
                        BufferedImage resized = HImageUtils.resize(image, w, h);
                        g.drawImage(resized, (int) x, (int) y, null);
                    }
                }
            }
            HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
        }
    }
}
