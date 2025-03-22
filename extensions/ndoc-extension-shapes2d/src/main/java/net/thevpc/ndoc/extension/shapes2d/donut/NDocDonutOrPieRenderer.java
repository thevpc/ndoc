package net.thevpc.ndoc.extension.shapes2d.donut;

import net.thevpc.ndoc.api.model.elem2d.Bounds2;
import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.style.HProperties;
import net.thevpc.ndoc.spi.eval.NDocValueByName;
import net.thevpc.ndoc.spi.eval.NDocObjEx;
import net.thevpc.ndoc.spi.renderer.NDocGraphics;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.spi.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.spi.util.HNodeRendererUtils;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class NDocDonutOrPieRenderer extends NDocNodeRendererBase {
    HProperties defaultStyles = new HProperties();

    public NDocDonutOrPieRenderer(String type) {
        super(type);
    }

    @Override
    public void renderMain(HNode p, NDocNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);

        Bounds2 b = NDocValueByName.selfBounds(p, null, null, ctx);
        double x = b.getX();
        double y = b.getY();
        double width = b.getWidth();
        double height = b.getHeight();

        double outerRadius = Math.min(width, height) / 2;
        double innerRadius=0;
        if(types()[0].equals(HNodeType.DONUT)) {
            innerRadius = NDocObjEx.of(p.getPropertyValue(HPropName.INNER_RADIUS)).asDouble().orElse(0.0);
            if (innerRadius <= 0) {
                innerRadius = 50;
            } else if (innerRadius >= 100) {
                innerRadius = 0;
            }
            innerRadius = innerRadius / 100 * outerRadius;
        }

        double startAngle = NDocObjEx.of(p.getPropertyValue(HPropName.START_ANGLE)).asDouble().orElse(0.0);
        double extentAngle = NDocObjEx.of(p.getPropertyValue(HPropName.EXTENT_ANGLE)).asDouble().orElse(360.0);
        double dash = NDocObjEx.of(p.getPropertyValue(HPropName.DASH)).asDouble().orElse(0.0);
        NDocGraphics g = ctx.graphics();

        String[] colors = NDocObjEx.of(p.getPropertyValue(HPropName.COLORS)).asStringArray().orElse(null);
        g.setColor(Color.black);
        if (!ctx.isDry()) {
            double finalInnerRadius = innerRadius;
            HNodeRendererUtils.withStroke(p, g, ctx, () -> {
                int sliceCount = NDocObjEx.of(p.getPropertyValue(HPropName.SLICE_COUNT)).asInt().orElse(-1);
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

                    double[] slicePercentage = NDocObjEx.of(p.getPropertyValue(HPropName.SLICES)).asDoubleArray().orElse(getSlicePercentage(p));
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


//            if (HNodeRendererUtils.applyForeground(p, g, ctx, !someBG)) {
//                HNodeRendererUtils.applyStroke(p, g, ctx);
//
//                double centerX = x + width / 2;
//                double centerY = y + height / 2;
//
//                g.drawOval((int) (centerX - outerRadius), (int) (centerY - outerRadius), (int) (outerRadius * 2), (int) (outerRadius * 2));
//                g.drawOval((int) (centerX - innerRadius), (int) (centerY - innerRadius), (int) (innerRadius * 2), (int) (innerRadius * 2));
//            }

            HNodeRendererUtils.paintDebugBox(p, ctx, g, b);
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

    private double[] getSlicePercentage(HNode node) {
        return new double[]{};
    }
}