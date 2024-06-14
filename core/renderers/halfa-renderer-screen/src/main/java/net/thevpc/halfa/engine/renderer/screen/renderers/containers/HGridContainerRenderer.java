package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.api.model.elem2d.Bounds2;
import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HProperties;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.model.HSizeRequirements;
import net.thevpc.halfa.spi.renderer.HGraphics;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHNodeRenderer;
import net.thevpc.halfa.spi.renderer.HNodeRendererContext;
import net.thevpc.halfa.spi.util.ObjEx;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HGridContainerRenderer extends AbstractHNodeRenderer {

    HProperties defaultStyles = new HProperties();

    public HGridContainerRenderer() {
        super(HNodeType.GRID);
    }

    private static class ComputePositionsResult {
        List<HPagePartExtInfo> effPositions;
        double xOffset;
        double yOffset;
        double childrenWidth;
        double childrenHeight;
        WeightInfo wi;
    }

    private ComputePositionsResult computePositions(HNode t, Bounds2 expectedBounds, HNodeRendererContext ctx) {
        java.util.List<HNode> children = t.children();
        List<HPagePartExtInfo> effPositions = new ArrayList<>();
        int minColumns = 0;
        int minRows = 0;
        for (int i = 0; i < children.size(); i++) {
            HNode cc = children.get(i);
            HPagePartExtInfo e = new HPagePartExtInfo();
            e.node = cc;
            e.colspan = colspan(cc, ctx);
            e.rowspan = rowspan(cc, ctx);
            if (e.colspan > minColumns) {
                minColumns = e.colspan;
            }
            if (e.rowspan > minRows) {
                minRows = e.rowspan;
            }
            e.sizeRequirements = ctx.manager().getRenderer(cc.type()).get().sizeRequirements(cc, ctx);
            e.index = i;
            effPositions.add(e);
        }

        boolean xCompact = (Boolean) ctx.computePropertyValue(t, HPropName.XCOMPACT).orElse(false);
        boolean yCompact = (Boolean) ctx.computePropertyValue(t, HPropName.YCOMPACT).orElse(false);
        Integer cols = (Integer) ctx.computePropertyValue(t, HPropName.COLUMNS).orElse(-1);
        Integer rows = (Integer) ctx.computePropertyValue(t, HPropName.ROWS).orElse(-1);
        if (cols == null) {
            cols = -1;
        }
        if (rows == null) {
            rows = -1;
        }
        if (cols <= 0 && rows <= 0) {
            int cc = (int) Math.floor(Math.sqrt(children.size()));
            cols = cc;
            rows = cc;
        } else if (cols < 0 && rows > 0) {
            cols = (int) Math.floor(children.size() / rows);
        } else if (cols > 0 && rows < 0) {
            rows = (int) Math.floor(children.size() / cols);
        } else {

        }
        if (cols < minColumns) {
            cols = minColumns;
        }
        if (rows < minRows) {
            rows = minRows;
        }


        {
            // try initial flagMap and add required rows
            FlagMMap flagmap = new FlagMMap(rows, cols);
            for (HPagePartExtInfo ee : effPositions) {
                flagmap.firstFreeAddRow(ee.colspan, ee.rowspan);
            }
            rows = flagmap.map.length;
        }


        FlagMMap flagmap = new FlagMMap(rows, cols);
        Bounds2 b = ctx.getBounds();
        double childrenWidth = expectedBounds.getWidth();
        double childrenHeight = expectedBounds.getHeight();
        double xOffset = expectedBounds.getX();
        double yOffset = expectedBounds.getY();

        WeightInfo wi = loadWeightInfo(cols, rows, t, ctx);
        for (HPagePartExtInfo ee : effPositions) {
            Point ff = flagmap.firstFree(ee.colspan, ee.rowspan);
            if (ff != null) {
                ee.colRow = ff;
                ee.width = wi.width(ee.colRow.x, ee.colspan);
                ee.height = wi.height(ee.colRow.y, ee.rowspan);

                double ww = childrenWidth * (ee.width / 100);
                double hh = childrenHeight * (ee.height / 100);
                double jx = (wi.colStart[ee.colRow.x] / 100) * childrenHeight + xOffset;
                double jy = (wi.rowStart[ee.colRow.y] / 100) * childrenHeight + yOffset;
                ee.bounds = new Bounds2(jx, jy, ww, hh);
                HNodeRendererContext ctx2 = ctx.withBounds(ee.node, ee.bounds);
                ee.sizeRequirements = ctx2.manager().getRenderer(ee.node.type()).get().sizeRequirements(ee.node, ctx2);
            }
        }

        //re-evaluate paintable zone
        Double2 posAnchor = ObjEx.of(ctx.computePropertyValue(t, HPropName.ORIGIN).orNull()).asDouble2OrHAlign().orElse(null);

        if (posAnchor != null) {
            double[] preferredRowsWeight = new double[effPositions.size()];
            double minPref = 0;
            double maxPref = 0;
            for (HPagePartExtInfo ee : effPositions) {
                if (ee.colRow != null) {
                    double newY = ee.sizeRequirements.minY;
                    if (newY > 0) {
                        preferredRowsWeight[ee.colRow.y] = newY;
                        if (minPref == 0 || minPref > newY) {
                            minPref = newY;
                        }
                        if (maxPref == 0 || maxPref < newY) {
                            maxPref = newY;
                        }
                    }
                }
            }
            if (minPref > 0) {
                double sumY = 0;
                for (HPagePartExtInfo ee : effPositions) {
                    if (ee.colRow != null) {
                        if (preferredRowsWeight[ee.colRow.y] == 0) {
                            preferredRowsWeight[ee.colRow.y] = minPref;
                        }
                        sumY += preferredRowsWeight[ee.colRow.y];
                    }
                }

                WeightInfo wi2 = new WeightInfo();
                wi2.colsWeight = wi.colsWeight;
                wi2.rowsWeight = preferredRowsWeight;
                revalidateWeightInfo(cols, rows, wi2);
                wi = wi2;
                double extraOffset = childrenHeight - sumY;
                childrenHeight = sumY;
                xOffset += posAnchor.getX() / 100.0 * extraOffset;
                yOffset += posAnchor.getY() / 100.0 * extraOffset;
//                switch (posAnchor) {
//                    case CENTER:
//                    case LEFT:
//                    case RIGHT: {
//                        yOffset += extraOffset / 2;
//                        break;
//                    }
//                    case BOTTOM:
//                    case BOTTOM_LEFT:
//                    case BOTTOM_RIGHT: {
//                        yOffset += extraOffset;
//                        break;
//                    }
//                    case TOP:
//                    case TOP_LEFT:
//                    case TOP_RIGHT: {
//                        //do nothing
//                        break;
//                    }
//                }
            }
        }
        ComputePositionsResult r = new ComputePositionsResult();
        r.effPositions = effPositions;
        r.xOffset = xOffset;
        r.yOffset = yOffset;
        r.childrenWidth = childrenWidth;
        r.childrenHeight = childrenHeight;
        r.wi = wi;

        for (HPagePartExtInfo ee : r.effPositions) {
            if (ee.colRow != null) {
                double ww = r.childrenWidth * (ee.width / 100);
                double hh = r.childrenHeight * (ee.height / 100);
                double jx = (r.wi.colStart[ee.colRow.x] / 100) * r.childrenWidth + r.xOffset;
                double jy = (r.wi.rowStart[ee.colRow.y] / 100) * r.childrenHeight + r.yOffset;
                ee.bounds = new Bounds2(jx, jy, ww, hh);
            }
        }

        return r;
    }

    @Override
    public Bounds2 selfBounds(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 expectedBounds = super.selfBounds(p, ctx);
//        HGraphics g = ctx.graphics();
//        g.setColor(Color.RED);
//        g.drawRect(expectedBounds);
        Bounds2 a = expectedBounds;
        ComputePositionsResult r = computePositions(p, expectedBounds, ctx);
        for (HPagePartExtInfo ee : r.effPositions) {
            if (ee.colRow != null) {
                a = expand(ee.bounds, a);
            }
        }
        return a;
    }

    public void render0(HNode p, HNodeRendererContext ctx) {
        ctx = ctx.withDefaultStyles(p, defaultStyles);
        Bounds2 expectedBounds = selfBounds(p, ctx);
        HGraphics g = ctx.graphics();

        if (!ctx.isDry()) {
            paintBackground(p, ctx, g, expectedBounds);
        }
        ComputePositionsResult r = computePositions(p, expectedBounds, ctx);
        for (HPagePartExtInfo ee : r.effPositions) {
            if (ee.colRow != null) {
                HNodeRendererContext ctx3 = ctx.withBounds(p, ee.bounds);
                if (!ctx.isDry()) {
                    if (getDebugLevel(p, ctx) > 0) {
                        g.setColor(getDebugColor(p, ctx));
                        g.setFont(new Font("Verdana", Font.PLAIN, 8));
                        g.drawString(String.valueOf(ee.index), ee.bounds.getCenterX(), ee.bounds.getCenterY());
                    }
                }
                ctx3.render(ee.node);
            }
        }

        drawGrid(p, r, ctx);
        paintBorderLine(p, ctx, g, expectedBounds);
    }


    private void drawGrid(HNode p, ComputePositionsResult r, HNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return;
        }
        HGraphics g = ctx.graphics();
        if (this.requireDrawGrid(p, g, ctx)) {
            if (this.applyGridColor(p, g, ctx, true)) {
                for (int i = 0; i < r.wi.colsWeight.length; i++) {
                    g.drawLine(
                            (int) (r.wi.colStart[i] / 100 * r.childrenWidth + r.xOffset),
                            (int) r.yOffset,
                            (int) (r.wi.colStart[i] / 100 * r.childrenWidth + r.xOffset),
                            (int) (r.yOffset + r.childrenHeight)
                    );
                }
                g.drawLine(
                        (int) (r.childrenWidth + r.xOffset),
                        (int) r.yOffset,
                        (int) (r.childrenWidth + r.xOffset),
                        (int) (r.yOffset + r.childrenHeight)
                );
                for (int i = 0; i < r.wi.rowsWeight.length; i++) {
                    g.drawLine(
                            (int) r.xOffset,
                            (int) (r.wi.rowStart[i] / 100 * r.childrenHeight + r.yOffset),
                            (int) (r.xOffset + r.childrenWidth),
                            (int) (r.wi.rowStart[i] / 100 * r.childrenHeight + r.yOffset)
                    );
                }
                g.drawLine(
                        (int) r.xOffset,
                        (int) (r.childrenHeight + r.yOffset),
                        (int) (r.xOffset + r.childrenWidth),
                        (int) (r.childrenHeight + r.yOffset)
                );
            }
        }
    }

    private WeightInfo revalidateWeightInfo(int cols, int rows, WeightInfo ii) {
        if (ii.colsWeight == null) {
            ii.colsWeight = new double[0];
        }
        if (ii.rowsWeight == null) {
            ii.rowsWeight = new double[0];
        }
        ii.colsWeight = revalidateWeights(cols, ii.colsWeight);
        ii.colStart = revalidateWeightStart(ii.colsWeight);
        ii.rowsWeight = revalidateWeights(rows, ii.rowsWeight);
        ii.rowStart = revalidateWeightStart(ii.rowsWeight);
        return ii;
    }

    private WeightInfo loadWeightInfo(int cols, int rows, HNode t, HNodeRendererContext ctx) {
        WeightInfo ii = new WeightInfo();
        ii.colsWeight = (double[]) ctx.computePropertyValue(t, HPropName.COLUMNS_WEIGHT).orElse(null);
        ii.rowsWeight = (double[]) ctx.computePropertyValue(t, HPropName.ROWS_WEIGHT).orElse(null);
        revalidateWeightInfo(cols, rows, ii);
        return ii;
    }

    private static class WeightInfo {
        double[] colsWeight;
        double[] colStart;
        double[] rowsWeight;
        double[] rowStart;

        public double width(int colIndex, int colspan) {
            double width = 0;
            for (int i = 0; i < colspan; i++) {
                width += colsWeight[colIndex + i];
            }
            return width;
        }

        public double height(int rowIndex, int rowspan) {
            double height = 0;
            for (int i = 0; i < rowspan; i++) {
                height += rowsWeight[rowIndex + i];
            }
            return height;
        }
    }

    private double[] revalidateWeightStart(double[] colsWeight) {
        double[] colsWeight2 = new double[colsWeight.length];
        for (int i = 0; i < colsWeight.length; i++) {
            if (i == 0) {
                colsWeight2[i] = 0;
            } else {
                colsWeight2[i] = colsWeight[i - 1] + colsWeight2[i - 1];
            }
        }
        return colsWeight2;
    }

    private double[] revalidateWeights(int cols, double[] colsWeight) {
        double[] colsWeight2 = new double[cols];

        double cwSum = 0;
        int validColumns = 0;
        for (int i = 0; i < colsWeight2.length; i++) {
            int ii = colsWeight.length == 0 ? 0 : i % colsWeight.length;
            if (ii < colsWeight.length) {
                if (colsWeight[ii] > 0 && Double.isFinite(colsWeight[ii])) {
                    colsWeight2[i] = colsWeight[ii];
                    cwSum += colsWeight2[i];
                    validColumns++;
                }
            }
        }
        if (validColumns < colsWeight2.length) {
            double r = validColumns == 0 ? (100 / colsWeight2.length) : cwSum / validColumns;
            cwSum = 0;
            for (int i = 0; i < colsWeight2.length; i++) {
                if (colsWeight2[i] == 0) {
                    colsWeight2[i] = r;
                }
                cwSum += colsWeight2[i];
            }
        }
        if (cwSum > 0) {
            for (int i = 0; i < colsWeight2.length; i++) {
                colsWeight2[i] = colsWeight2[i] * 100 / cwSum;
            }
        }
        return colsWeight2;
    }

    private static class FlagMMap {
        boolean[][] map;

        public FlagMMap(int rows, int cols) {
            this.map = new boolean[rows][cols];
        }

        public Point firstFreeAddRow(int colspan, int rowspan) {
            for (int r = 0; r < rowspan + 1; r++) {
                for (int i = 0; i < map.length; i++) {
                    for (int j = 0; j < map[i].length; j++) {
                        if (setIfFree(j, i, colspan, rowspan)) {
                            return new Point(j, i);
                        }
                    }
                }
                boolean[][] map2 = new boolean[map.length + 1][];
                for (int i = 0; i < map.length; i++) {
                    map2[i] = map[i];
                }
                map2[map.length] = new boolean[map[0].length];
                map = map2;
            }
            return null;
        }

        public Point firstFree(int colspan, int rowspan) {
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    if (setIfFree(j, i, colspan, rowspan)) {
                        return new Point(j, i);
                    }
                }
            }
            return null;
        }

        public boolean isFree(int col, int row, int colspan, int rowspan) {
            for (int kr = 0; kr < rowspan; kr++) {
                for (int kc = 0; kc < colspan; kc++) {
                    if (!map[kr + row][kc + col]) {
                        return false;
                    }
                }
            }
            return true;
        }

        public boolean setIfFree(int col, int row, int colspan, int rowspan) {
            for (int kr = 0; kr < rowspan; kr++) {
                for (int kc = 0; kc < colspan; kc++) {
                    int rr = kr + row;
                    if (rr >= map.length) {
                        return false;
                    }
                    int cc = kc + col;
                    if (cc >= map[rr].length) {
                        return false;
                    }
                    if (map[rr][cc]) {
                        return false;
                    }

                }
            }
            for (int kr = 0; kr < rowspan; kr++) {
                for (int kc = 0; kc < colspan; kc++) {
                    map[kr + row][kc + col] = true;
                }
            }
            return true;
        }
    }


    protected class HPagePartExtInfo {
        Point colRow;
        double width;
        double height;
        HNode node;
        HSizeRequirements sizeRequirements;
        int colspan;
        int rowspan;
        int index;

        Bounds2 bounds;
    }
}
