package net.thevpc.halfa.engine.renderer.screen.renderers.containers;

import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.api.node.container.HContainer;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.halfa.engine.renderer.screen.common.AbstractHPartRenderer;
import net.thevpc.halfa.engine.renderer.screen.HPartRendererContext;
import net.thevpc.halfa.engine.renderer.screen.common.HPartRendererContextDelegate;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class HGridContainerRenderer extends AbstractHPartRenderer {


    public Rectangle2D.Double paintPagePart(HNode p, HPartRendererContext ctx) {
        Rectangle2D.Double expectedBounds = bounds(p, ctx);
        HContainer t = (HContainer) p;
        Graphics2D g = ctx.getGraphics();
        Rectangle2D.Double a = expectedBounds;

        paintBackground(t, ctx, g, a);
        List<HNode> children = t.children();
        List<HPagePartExtInfo> effPositions = new ArrayList<>();
        int minColumns = 0;
        int minRows = 0;
        for (int i = 0; i < children.size(); i++) {
            HNode cc = children.get(i);
            HPagePartExtInfo e = new HPagePartExtInfo();
            e.part = cc;
            e.colspan = colspan(cc, ctx);
            e.rowspan = rowspan(cc, ctx);
            if (e.colspan > minColumns) {
                minColumns = e.colspan;
            }
            if (e.rowspan > minRows) {
                minRows = e.rowspan;
            }
            e.sizeRequirements = computeSizeRequirements(p, ctx);
            e.index = i;
            effPositions.add(e);
        }

        Integer cols = (Integer) ctx.getStyle(t, HStyleType.COLUMNS).orElse(HStyles.columns(-1)).getValue();
        Integer rows = (Integer) ctx.getStyle(t, HStyleType.ROWS).orElse(HStyles.rows(-1)).getValue();
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
        Rectangle2D.Double b = ctx.getBounds();
        double childrenWidth = expectedBounds.getWidth();
        double childrenHeight = expectedBounds.getHeight();
        double xOffset = b.getX();
        double yOffset = b.getY();

        WeightInfo wi = loadWeightInfo(cols, rows, t, ctx);
        for (HPagePartExtInfo ee : effPositions) {
            Point ff = flagmap.firstFree(ee.colspan, ee.rowspan);
            if (ff != null) {
                ee.colRow = ff;
                ee.width = wi.width(ee.colRow.x, ee.colspan);
                ee.height = wi.height(ee.colRow.y, ee.rowspan);
                ee.ww = childrenWidth * (ee.width / 100);
                ee.hh = childrenHeight * (ee.height / 100);
                ee.jx = (wi.colStart[ee.colRow.x] / 100) * childrenHeight + xOffset;
                ee.jy = (wi.rowStart[ee.colRow.y] / 100) * childrenHeight + yOffset;
                HPartRendererContext ctx3 = new HPartRendererContextDelegate(
                        t,
                        ctx,
                        new Rectangle2D.Double(
                                ee.jx,
                                ee.jy,
                                ee.ww,
                                ee.hh
                        )
                );
                ee.sizeRequirements = ctx3.computeSizeRequirements(ee.part);
            }
        }

        //re-evaluate paintable zone
        HAlign posAnchor = (HAlign) ctx.getStyle(t, HStyleType.ORIGIN).orElse(HStyles.origin(HAlign.NONE)).getValue();
        if (posAnchor == null) {
            posAnchor = HAlign.NONE;
        }

        if (posAnchor != HAlign.NONE) {
            double[] preferredRowsWeight = new double[effPositions.size()];
            double minPref = 0;
            double maxPref = 0;
            for (HPagePartExtInfo ee : effPositions) {
                if (ee.colRow != null) {
                    double newY = ee.sizeRequirements.preferredY;
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
                switch (posAnchor) {
                    case CENTER:
                    case LEFT:
                    case RIGHT: {
                        yOffset += extraOffset / 2;
                        break;
                    }
                    case BOTTOM:
                    case BOTTOM_LEFT:
                    case BOTTOM_RIGHT: {
                        yOffset += extraOffset;
                        break;
                    }
                    case TOP:
                    case TOP_LEFT:
                    case TOP_RIGHT: {
                        //do nothing
                        break;
                    }
                }
            }
        }
        for (HPagePartExtInfo ee : effPositions) {
            if (ee.colRow != null) {
                ee.ww = childrenWidth * (ee.width / 100);
                ee.hh = childrenHeight * (ee.height / 100);
                ee.jx = (wi.colStart[ee.colRow.x] / 100) * childrenWidth + xOffset;
                ee.jy = (wi.rowStart[ee.colRow.y] / 100) * childrenHeight + yOffset;
                HPartRendererContext ctx3 = new HPartRendererContextDelegate(
                        t,
                        ctx,
                        new Rectangle2D.Double(
                                ee.jx,
                                ee.jy,
                                ee.ww,
                                ee.hh
                        )
                );
                Rectangle2D.Double r1 = ctx3.paintPagePart(ee.part);
                a = expand(r1, a);
            }
        }

        if (this.requireDrawContour(p, g, ctx)) {
            if (this.applyGridColor(p, g, ctx)) {
                for (int i = 0; i < wi.colsWeight.length; i++) {
                    g.drawLine(
                            (int) (wi.colStart[i] / 100 * childrenWidth + xOffset),
                            (int) yOffset,
                            (int) (wi.colStart[i] / 100 * childrenWidth + xOffset),
                            (int) (yOffset + childrenHeight)
                    );
                }
                g.drawLine(
                        (int) (childrenWidth + xOffset),
                        (int) yOffset,
                        (int) (childrenWidth + xOffset),
                        (int) (yOffset + childrenHeight)
                );
                for (int i = 0; i < wi.rowsWeight.length; i++) {
                    g.drawLine(
                            (int) xOffset,
                            (int) (wi.rowStart[i] / 100 * childrenHeight + yOffset),
                            (int) (xOffset + childrenWidth),
                            (int) (wi.rowStart[i] / 100 * childrenHeight + yOffset)
                    );
                }
                g.drawLine(
                        (int) xOffset,
                        (int) (childrenHeight + yOffset),
                        (int) (xOffset + childrenWidth),
                        (int) (childrenHeight + yOffset)
                );
            }
        }

        paintBorderLine(t, ctx, g, a);
        return a;
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

    private WeightInfo loadWeightInfo(int cols, int rows, HContainer t, HPartRendererContext ctx) {
        WeightInfo ii = new WeightInfo();
        ii.colsWeight = (double[]) ctx.getStyle(t, HStyleType.COLUMNS_WEIGHT).orElse(HStyles.columnsWeight()).getValue();
        ii.rowsWeight = (double[]) ctx.getStyle(t, HStyleType.ROWS_WEIGHT).orElse(HStyles.rowsWeight()).getValue();
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
        HNode part;
        HSizeRequirements sizeRequirements;
        int colspan;
        int rowspan;
        int index;

        double ww;
        double hh;
        double jx;
        double jy;
    }
}
