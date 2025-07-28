package net.thevpc.ndoc.extension.shapes2d.donut;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import  net.thevpc.ndoc.api.document.style.NDocPropName;
import  net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocValueByName;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocNodeRendererUtils;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class NDocDonutOrPieRenderer extends NDocNodeRendererBase {
    NDocProperties defaultStyles = new NDocProperties();

    public NDocDonutOrPieRenderer(String type) {
        super(type);
    }

    @Override
    public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);

        NDocBounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        double width = b.getWidth();
        double height = b.getHeight();

        double outerRadius = Math.min(width, height) / 2;
        double innerRadius=0;
        if(types()[0].equals(NDocNodeType.DONUT)) {
            innerRadius = NDocObjEx.of(p.getPropertyValue(NDocPropName.INNER_RADIUS)).asDouble().orElse(0.0);
            if (innerRadius <= 0) {
                innerRadius = 50;
            } else if (innerRadius >= 100) {
                innerRadius = 0;
            }
            innerRadius = innerRadius / 100 * outerRadius;
        }

        double startAngle = NDocObjEx.of(p.getPropertyValue(NDocPropName.START_ANGLE)).asDouble().orElse(0.0);
        double extentAngle = NDocObjEx.of(p.getPropertyValue(NDocPropName.EXTENT_ANGLE)).asDouble().orElse(360.0);
        double dash = NDocObjEx.of(p.getPropertyValue(NDocPropName.DASH)).asDouble().orElse(0.0);
        NDocGraphics g = ctx.graphics();

        String[] colors = NDocObjEx.of(p.getPropertyValue(NDocPropName.COLORS)).asStringArray().orElse(null);
        g.setColor(Color.black);
        if (!ctx.isDry()) {
            double finalInnerRadius = innerRadius;
            NDocNodeRendererUtils.withStroke(p, g, ctx, () -> {
                int sliceCount = NDocObjEx.of(p.getPropertyValue(NDocPropName.SLICE_COUNT)).asInt().orElse(-1);
                //equal slices
                if (sliceCount > 0) {
                    double sliceAngle = (extentAngle - sliceCount * dash) / sliceCount;
                    double currentAngle = startAngle;

                    for (int i = 0; i < sliceCount; i++) {
                        Color color = getColor(i, colors);

                        Area outerCircle = new Area(new Arc2D.Double(x + (width / 2 - outerRadius), y + (height / 2 - outerRadius), outerRadius * 2, outerRadius * 2, currentAngle, sliceAngle, Arc2D.PIE));
                        if (finalInnerRadius > 0) {
                            Area innerCircle = new Area(new Arc2D.Double(x + (width / 2 - finalInnerRadius), y + (height / 2 - finalInnerRadius), finalInnerRadius * 2, finalInnerRadius * 2, currentAngle, sliceAngle, Arc2D.PIE));
                            outerCircle.subtract(innerCircle);
                        }

                        g.setColor(color);
                        g.fill(outerCircle);

                        currentAngle += sliceAngle + dash;
                    }
                }
                //diff sizes
                else {

                    double[] slicePercentage = NDocObjEx.of(p.getPropertyValue(NDocPropName.SLICES)).asDoubleArray().orElse(getSlicePercentage(p));
                    slicePercentage = Arrays.stream(slicePercentage).filter(xx -> xx <= 0 || Double.isInfinite(xx) || Double.isNaN(xx)).toArray();
                    if (slicePercentage.length == 0) {
                        slicePercentage = new double[]{1, 1, 1};
                    }
                    double sum = Arrays.stream(slicePercentage).sum();
                    slicePercentage = Arrays.stream(slicePercentage).map(xx -> xx / (sum == 0 ? 1 : sum) * 100.0).toArray();
                    int percentageSliceCount = slicePercentage.length;
                    double totalDash = dash * percentageSliceCount;
                    double[] sliceAngles = new double[percentageSliceCount];

                    for (int i = 0; i < percentageSliceCount; i++) {
                        sliceAngles[i] = (extentAngle - totalDash) * slicePercentage[i] / 100.0;
                    }

                    double currentAngle = startAngle;

                    for (int i = 0; i < percentageSliceCount; i++) {
                        Color color = getColor(i, colors);

                        double sliceAngle = sliceAngles[i];

                        Area outerCircle = new Area(new Arc2D.Double(x + (width / 2 - outerRadius), y + (height / 2 - outerRadius), outerRadius * 2, outerRadius * 2, currentAngle, sliceAngle, Arc2D.PIE));
                        if (finalInnerRadius > 0) {
                            Area innerCircle = new Area(new Arc2D.Double(x + (width / 2 - finalInnerRadius), y + (height / 2 - finalInnerRadius), finalInnerRadius * 2, finalInnerRadius * 2, currentAngle, sliceAngle, Arc2D.PIE));
                            outerCircle.subtract(innerCircle);
                        }
                        g.setColor(color);
                        g.fill(outerCircle);

                        currentAngle += sliceAngle + dash;
                    }
                }
            });


//            if (NDocNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
//                NDocNodeRendererUtils.applyStroke(p, g, ctx);
//
//                double centerX = x + width / 2;
//                double centerY = y + height / 2;
//
//                g.drawOval((int) (centerX - outerRadius), (int) (centerY - outerRadius), (int) (outerRadius * 2), (int) (outerRadius * 2));
//                g.drawOval((int) (centerX - innerRadius), (int) (centerY - innerRadius), (int) (innerRadius * 2), (int) (innerRadius * 2));
//            }

            NDocNodeRendererUtils.paintDebugBox(p, ctx, g, b);
        }
    }

    private Color getColor(int sliceIndex, String[] colors) {
        String[] defaultColors = {
                "#402E7A",
                "#4C3BCF",
                "#4B70F5",
                "#3DC2EC",
        };

        String colorStr = (colors != null && colors.length > 0)
                ? colors[sliceIndex % colors.length]
                : defaultColors[sliceIndex % defaultColors.length];

        return parseColor(colorStr);
    }

    private Color parseColor(String colorStr) {
        if (colorStr.startsWith("#")) {
            return Color.decode(colorStr);
        } else {
            try {
                Field field = Color.class.getField(colorStr.toLowerCase());
                return (Color) field.get(null);
            } catch (Exception e) {
                throw new IllegalArgumentException("Unvalid color: " + colorStr);
            }
        }
    }

    private double[] getSlicePercentage(NDocNode node) {
        return new double[]{};
    }
}
