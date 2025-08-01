package net.thevpc.ndoc.extension.shapes2d;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.node.NDocNodeType;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.style.NDocProperties;
import net.thevpc.ndoc.api.eval.NDocValue;
import net.thevpc.ndoc.api.extension.NDocNodeBuilder;
import net.thevpc.ndoc.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.lang.reflect.Field;
import java.util.Arrays;


/**
 *
 */
public class NDocDonutBuilder implements NDocNodeBuilder {
    NDocProperties defaultStyles = new NDocProperties();

    @Override
    public void build(NDocNodeCustomBuilderContext builderContext) {
        builderContext
                .id(NDocNodeType.DONUT)
                .parseParam().named(NDocPropName.INNER_RADIUS,NDocPropName.START_ANGLE,NDocPropName.EXTENT_ANGLE,NDocPropName.DASH).then()
                .parseParam().named(NDocPropName.SLICE_COUNT).then()
                .parseParam().named(NDocPropName.SLICES).then()
                .parseParam().named(NDocPropName.COLORS).then()
                .renderComponent(this::render)
                ;
    }


    private void render(NDocNode p, NDocNodeRendererContext renderContext,NDocNodeCustomBuilderContext builderContext) {
        renderDonutOrPie(p, renderContext, builderContext, defaultStyles, true);
    }

    public static void renderDonutOrPie(NDocNode p, NDocNodeRendererContext renderContext,
                                  NDocNodeCustomBuilderContext builderContext,NDocProperties defaultStyles,
                                  boolean isDonut
                                  ) {
        renderContext = renderContext.withDefaultStyles(p, defaultStyles);

        NDocBounds2 b = renderContext.selfBounds(p, null, null);
        double x = b.getX();
        double y = b.getY();
        double width = b.getWidth();
        double height = b.getHeight();

        double outerRadius = Math.min(width, height) / 2;
        double innerRadius=0;
        if(isDonut) {
            innerRadius = NDocValue.of(p.getPropertyValue(NDocPropName.INNER_RADIUS)).asDouble().orElse(0.0);
            if (innerRadius <= 0) {
                innerRadius = 50;
            } else if (innerRadius >= 100) {
                innerRadius = 0;
            }
            innerRadius = innerRadius / 100 * outerRadius;
        }

        double startAngle = NDocValue.of(p.getPropertyValue(NDocPropName.START_ANGLE)).asDouble().orElse(0.0);
        double extentAngle = NDocValue.of(p.getPropertyValue(NDocPropName.EXTENT_ANGLE)).asDouble().orElse(360.0);
        double dash = NDocValue.of(p.getPropertyValue(NDocPropName.DASH)).asDouble().orElse(0.0);
        NDocGraphics g = renderContext.graphics();

        String[] colors = NDocValue.of(p.getPropertyValue(NDocPropName.COLORS)).asStringArray().orElse(null);
        g.setColor(Color.black);
        if (!renderContext.isDry()) {
            double finalInnerRadius = innerRadius;
            renderContext.withStroke(p, () -> {
                int sliceCount = NDocValue.of(p.getPropertyValue(NDocPropName.SLICE_COUNT)).asInt().orElse(-1);
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

                    double[] slicePercentage = NDocValue.of(p.getPropertyValue(NDocPropName.SLICES)).asDoubleArray().orElse(getSlicePercentage(p));
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


//            if (renderContext.applyForeground(p, !someBG)) {
//                renderContext.applyStroke(p);
//
//                double centerX = x + width / 2;
//                double centerY = y + height / 2;
//
//                g.drawOval((int) (centerX - outerRadius), (int) (centerY - outerRadius), (int) (outerRadius * 2), (int) (outerRadius * 2));
//                g.drawOval((int) (centerX - innerRadius), (int) (centerY - innerRadius), (int) (innerRadius * 2), (int) (innerRadius * 2));
//            }

            renderContext.paintDebugBox(p, b);
        }
    }

    private static Color getColor(int sliceIndex, String[] colors) {
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

    private static Color parseColor(String colorStr) {
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

    private static double[] getSlicePercentage(NDocNode node) {
        return new double[]{};
    }


}
