package net.thevpc.ndoc.extension.plantuml;

import net.sourceforge.plantuml.SourceStringReader;
import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;


public abstract class PlantUmlBuilderBase implements NDocNodeCustomBuilder {
    private String id;
    private String mode;
    private String[] aliases;
    private NDocProperties defaultStyles = new NDocProperties();

    public PlantUmlBuilderBase(String id) {
        this("plantuml-" + id, id);
    }

    public PlantUmlBuilderBase(String id, String... aliases) {
        this(id, id, aliases);
    }

    public PlantUmlBuilderBase(String id, String mode, String[] aliases) {
        this.id = id;
        this.mode = mode;
        this.aliases = aliases;
        this.mode = id;
    }

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext.id(id)
                .alias(aliases)
                .parseParam().named(NDocPropName.VALUE).then()
                .parseParam().named(NDocPropName.FILE).set(NDocPropName.VALUE).resolvedAs((uid, value, info, buildContext) -> {
                    NPath nPath = buildContext.engine().resolvePath(value.asString().get(), info.node());
                    info.getContext().document().resources().add(nPath);
                    return NDocProp.ofString(uid, nPath.readString().trim());
                }).then()
                .parseParam().matchesStringOrName().set(NDocPropName.VALUE).then()
                .renderComponent(this::renderMain)
        ;
    }


    public void renderMain(NDocNode p, NDocNodeRendererContext ctx, NDocNodeCustomBuilderContext builderContext) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        String txt = NDocObjEx.of(p.getPropertyValue(NDocPropName.VALUE).orNull()).asStringOrName().orNull();
        if (NBlankable.isBlank(txt)) {
            return;
        }
        String mode = NDocUtils.uid(this.mode);
        NDocGraphics g = ctx.graphics();
        NDocBounds2 b = ctx.selfBounds(p);
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
            case "latex": {
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
                NDocResource src = NDocUtils.sourceOf(p);
                ctx.log().log(NMsg.ofC("Unable to evaluate UML : %s", ex).asSevere(), src);
            }
        }
        if (image != null) {

            if (!ctx.isDry()) {
                if (NDocNodeRendererUtils.applyBackgroundColor(p, g, ctx)) {
                    g.fillRect((int) x, (int) y, NDocUtils.intOf(b.getWidth()), NDocUtils.intOf(b.getHeight()));
                }

                NDocNodeRendererUtils.applyForeground(p, g, ctx, false);
                if (image != null) {
                    // would resize?
                    int w = NDocUtils.intOf(b.getWidth());
                    int h = NDocUtils.intOf(b.getHeight());
                    if (w > 0 && h > 0) {
                        BufferedImage resized = ctx.engine().tools().resizeBufferedImage(image, w, h);
                        g.drawImage(resized, (int) x, (int) y, null);
                    }
                }
            }
            NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b);
        }
    }

    private String prepare(String type, String txt, NDocBounds2 b) {
        return "@start" + type + "\n"
                + "scale " + (b.getWidth().intValue()) + "*" + (b.getHeight().intValue()) + "\n"
                + "skinparam backgroundcolor transparent\n"
//                        + "skinparam dpi 300\n"
                + txt
                + "\n@end" + type + "\n";
    }
}
