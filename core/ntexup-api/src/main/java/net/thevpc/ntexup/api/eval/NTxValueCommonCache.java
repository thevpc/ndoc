package net.thevpc.ntexup.api.eval;

import net.thevpc.ntexup.api.document.elem2d.*;
import net.thevpc.ntexup.api.util.NTxSizeRef;
import net.thevpc.ntexup.api.util.NtxFontInfo;
import net.thevpc.nuts.elem.NElement;

import java.awt.*;

public class NTxValueCommonCache {
    public Font font;
    public NTxSize fontSize;
    public double fontXSize;
    public double fontYSize;
    public boolean fontItalic;
    public boolean fontStrike;
    public boolean fontUnderline;
    public boolean fontBold;
    public String fontFamily;
//    public boolean visible;
    public NElement stroke;
    public NTxElemNumber2 position;
    public NTxElemNumber2 origin;
    public boolean preserveRatio;
    public NtxFontInfo fontInfo;
    public Paint foregroundColor;
    public Paint backgroundColor;
    public NTxDouble2 componentSize;
    public NTxMargin margin;
    public boolean drawContour;
    public boolean fillBackground;
    public int debugLevel;
    public Color debugColor;
    public NTxSizeRef parentWithMarginRef;
    public NTxBounds2 parentBoundsWithMargin;
}
