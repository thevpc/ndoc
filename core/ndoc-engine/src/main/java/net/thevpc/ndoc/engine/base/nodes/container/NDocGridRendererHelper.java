package net.thevpc.ndoc.elem.base.container;

import net.thevpc.ndoc.api.document.elem2d.NDocBounds2;
import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.elem2d.SizeD;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.document.NDocSizeRequirements;
import net.thevpc.ndoc.api.eval.NDocValueByType;
import net.thevpc.ndoc.api.renderer.NDocGraphics;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.eval.NDocValue;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class NDocGridRendererHelper {
    boolean drawBackground = true;
    boolean drawGrid = true;
    boolean NEWIMPL = false;
    java.util.List<NDocNode> children;

    public NDocGridRendererHelper(List<NDocNode> children) {
        this.children = children;
    }

    public NDocBounds2 computeBound(NDocNode p, NDocNodeRendererContext ctx, NDocBounds2 expectedBounds) {
        if (NEWIMPL) {
            for (NDocNodeGridBagLayout.NDocNodeGridNode ee : computePositions2(p, expectedBounds, ctx)) {
                if (ee.visible) {
                    expectedBounds = ee.bounds.expand(expectedBounds);
                }
            }
            return expectedBounds;
        } else {
            NDocGridRendererHelper.ComputePositionsResult r = computePositions(p, expectedBounds, ctx);
            for (HPagePartExtInfo ee : r.effPositions) {
                if (ee.colRow != null) {
                    expectedBounds = ee.bounds.expand(expectedBounds);
                }
            }
            return expectedBounds;
        }
    }

    public static class ComputePositionsResult {
        List<HPagePartExtInfo> effPositions;
        double xOffset;
        double yOffset;
        double childrenWidth;
        double childrenHeight;
        WeightInfo wi;
    }

    public void render(NDocNode p, NDocNodeRendererContext ctx, NDocBounds2 expectedBounds) {
        if (ctx.isDry()) {
            return;
        }
        NDocGraphics g = ctx.graphics();

        if (drawBackground) {
            if (ctx.applyBackgroundColor(p)) {
                g.fillRect(expectedBounds);
            }
        }

        if (NEWIMPL) {
            NDocNodeGridBagLayout.NDocNodeGridNode[] nodeGridNodes = computePositions2(p, expectedBounds, ctx);
            for (NDocNodeGridBagLayout.NDocNodeGridNode ee : nodeGridNodes) {
                if (ee.node != null && ee.visible) {
                    NDocNodeRendererContext ctx3 = ctx.withBounds(p, ee.bounds);
                    if (!ctx.isDry()) {
                        if (ctx.isDebug(p)) {
                            g.setColor(ctx.getDebugColor(p));
                            g.setFont(new Font("Verdana", Font.PLAIN, 8));
                            g.drawString(String.valueOf(ee.index), ee.bounds.getCenterX(), ee.bounds.getCenterY());
                        }
                    }
                    ctx3.render((NDocNode) ee.node);
                }
            }

        } else {

            NDocGridRendererHelper.ComputePositionsResult r = computePositions(p, expectedBounds, ctx);
            for (NDocGridRendererHelper.HPagePartExtInfo ee : r.effPositions) {
                if (ee.colRow != null) {
                    NDocNodeRendererContext ctx3 = ctx.withBounds(p, ee.bounds);
                    if (!ctx.isDry()) {
                        if (ctx.isDebug(p)) {
                            g.setColor(ctx.getDebugColor(p));
                            g.setFont(new Font("Verdana", Font.PLAIN, 8));
                            g.drawString(String.valueOf(ee.index), ee.bounds.getCenterX(), ee.bounds.getCenterY());
                        }
                    }
                    ctx3.render(ee.node);
                }
            }
            drawGrid(p, r, ctx);
        }
        ctx.paintBorderLine(p, expectedBounds);
    }

    private int[] resolveColsAndRows(NDocNode t, NDocNodeRendererContext ctx) {
        int minColumns = 0;
        int minRows = 0;
        for (int i = 0; i < children.size(); i++) {
            NDocNode cc = children.get(i);
            int colspan = ctx.getColSpan(cc);
            int rowspan = ctx.getRowSpan(cc);
            if (colspan > minColumns) {
                minColumns = colspan;
            }
            if (rowspan > minRows) {
                minRows = rowspan;
            }
        }
        int cols = ctx.getColumns(t);
        int rows = ctx.getRows(t);
        if (cols < 0) {
            cols = -1;
        }
        if (rows < 0) {
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
            for (int i = 0; i < children.size(); i++) {
                NDocNode cc = children.get(i);
                int colspan = ctx.getColSpan(cc);
                int rowspan = ctx.getRowSpan(cc);
                flagmap.firstFreeAddRow(colspan, rowspan);
            }
            rows = flagmap.map.length;
        }

        return new int[]{cols, rows};
    }

    private NDocNodeGridBagLayout.NDocNodeGridNode[] computePositions2(NDocNode p, NDocBounds2 expectedBounds, NDocNodeRendererContext ctx) {
        java.util.List<NDocNodeGridBagLayout.NDocNodeGridNode> childrenNodes = new ArrayList<>();
        int[] colsAndRows = resolveColsAndRows(p, ctx);
        // try initial flagMap and add required rows
        int columns = colsAndRows[0];
        int rows = colsAndRows[1];
        FlagMMap flagmap = new FlagMMap(rows, columns);

        for (int i = 0; i < children.size(); i++) {
            NDocNode cc = children.get(i);
            NDocNodeGridBagLayout.NDocNodeGridNode n = new NDocNodeGridBagLayout.NDocNodeGridNode(cc);
            n.index = i;
            NDocSizeRequirements NDocSizeRequirements = ctx.manager().getRenderer(cc.type()).get().sizeRequirements(cc, ctx);
            n.setMinimumSize(new SizeD(NDocSizeRequirements.minX, NDocSizeRequirements.minY));
            n.setPreferredSize(new SizeD(NDocSizeRequirements.preferredX,  NDocSizeRequirements.preferredY));
            int colSpan = ctx.getColSpan(cc);
            int rowSpan = ctx.getRowSpan(cc);
            n.setGridwidth(colSpan);
            n.setGridheight(rowSpan);
            Point pos = flagmap.firstFreeAddRow(colSpan, rowSpan);
            if (pos == null) {
                throw new IllegalArgumentException("unexpected null");
            }
            n.setGridx(pos.x);
            n.setGridy(pos.y);
            double xw = 0;
            double yw = 0;
            WeightInfo wi = loadWeightInfo(columns, rows, p, ctx);
            for (int ix = pos.x; ix < pos.x + colSpan; ix++) {
                for (int iy = pos.y; iy < pos.y + rowSpan; iy++) {
                    xw += wi.colsWeight[ix];
                    yw += wi.rowsWeight[iy];
                }
            }
            n.setWeightx(xw);
            n.setWeighty(yw);
            n.setFill(NDocNodeGridBagLayout.Fill.BOTH);
            n.setAnchor(NDocNodeGridBagLayout.Anchor.NORTHWEST);
            childrenNodes.add(n);
        }
//        childrenNodes.add(
//                new NDocNodeGridBagLayout.NDocNodeGridNode(null)
//                        .setMinimumSize(new Dimension(20,10))
//                        .setPreferredSize(new Dimension(10,200))
//                        .setWeighty(10)
//                        .setGridy(10)
//                        .setFill(NDocNodeGridBagLayout.Fill.VERTICAL)
//                        .setGridx(0)
//                        .setGridy(rows+1)
//        );
//        childrenNodes.add(
//                new NDocNodeGridBagLayout.NDocNodeGridNode(null)
//                        .setFill(NDocNodeGridBagLayout.Fill.HORIZONTAL)
//                        .setWeighty(Double.MAX_VALUE)
//                        .setGridx(columns)
//                        .setGridy(rows)
//        );
        NDocNodeGridBagLayout.NDocNodeGridNode[] childrenNodesArray = childrenNodes.toArray(new NDocNodeGridBagLayout.NDocNodeGridNode[0]);
        NDocNodeGridBagLayout ll = new NDocNodeGridBagLayout(
                new Insets(0, 0, 0, 0),
                false,
                expectedBounds,
                childrenNodesArray
        );
        ll.doLayout();
        return childrenNodesArray;
    }

    private ComputePositionsResult computePositions(NDocNode t, NDocBounds2 expectedBounds, NDocNodeRendererContext ctx) {
        List<HPagePartExtInfo> effPositions = new ArrayList<>();
        int minColumns = 0;
        int minRows = 0;
        for (int i = 0; i < children.size(); i++) {
            NDocNode cc = children.get(i);
            HPagePartExtInfo e = new HPagePartExtInfo();
            e.node = cc;
            e.colspan = ctx.getColSpan(cc);
            e.rowspan = ctx.getRowSpan(cc);
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

        int cols = ctx.getColumns(t);
        int rows = ctx.getRows(t);
        if (cols < 0) {
            cols = -1;
        }
        if (rows < 0) {
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
                ee.bounds = new NDocBounds2(jx, jy, ww, hh);
                NDocNodeRendererContext ctx2 = ctx.withBounds(ee.node, ee.bounds);
                ee.sizeRequirements = ctx2.manager().getRenderer(ee.node.type()).get().sizeRequirements(ee.node, ctx2);
            }
        }

        //re-evaluate paintable zone
        NDocDouble2 posAnchor = NDocValue.of(ctx.computePropertyValue(t, NDocPropName.ORIGIN).orNull()).asDouble2OrHAlign().orElse(null);

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
                ee.bounds = new NDocBounds2(jx, jy, ww, hh);
            }
        }

        return r;
    }


    public static class HPagePartExtInfo {
        Point colRow;
        double width;
        double height;
        NDocNode node;
        NDocSizeRequirements sizeRequirements;
        int colspan;
        int rowspan;
        int index;

        NDocBounds2 bounds;
    }

    public static class WeightInfo {
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

    private static double[] revalidateWeights(int cols, double[] colsWeight) {
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


    private void drawGrid(NDocNode p, NDocGridRendererHelper.ComputePositionsResult r, NDocNodeRendererContext ctx) {
        if (ctx.isDry()) {
            return;
        }
        NDocGraphics g = ctx.graphics();
        if (ctx.requireDrawGrid(p)) {
            if (ctx.applyGridColor(p, true)) {
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

    private NDocGridRendererHelper.WeightInfo revalidateWeightInfo(int cols, int rows, NDocGridRendererHelper.WeightInfo ii) {
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

    private NDocGridRendererHelper.WeightInfo loadWeightInfo(int cols, int rows, NDocNode t, NDocNodeRendererContext ctx) {
        NDocGridRendererHelper.WeightInfo ii = new NDocGridRendererHelper.WeightInfo();
        ii.colsWeight = NDocValueByType.getDoubleArray(t, ctx, NDocPropName.COLUMNS_WEIGHT).orNull();
        ii.rowsWeight = NDocValueByType.getDoubleArray(t, ctx, NDocPropName.ROWS_WEIGHT).orNull();
        revalidateWeightInfo(cols, rows, ii);
        return ii;
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


}
