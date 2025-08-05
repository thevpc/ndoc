package net.thevpc.ntexup.api.renderer.text;

import net.thevpc.ntexup.api.document.elem2d.NTxPoint2D;
import net.thevpc.nuts.util.NBlankable;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

public class NTxTextOptions implements Cloneable, NBlankable {
    public Paint backgroundColor;
    public Integer foregroundColorIndex;
    public Integer backgroundColorIndex;
    public Paint foregroundColor;
    public Boolean bold;
    public Boolean italic;
    public Float fontSize;
    public String fontFamily;
    public Boolean underlined;
    public Boolean strikeThrough;
    public Font font;
    public Paint shadowColor;
    public NTxPoint2D shadowTranslation;
    public Stroke stroke;

    @Override
    public boolean isBlank() {
        if (backgroundColor != null) {
            return false;
        }
        if (foregroundColorIndex != null) {
            return false;
        }
        if (backgroundColorIndex != null) {
            return false;
        }
        if (bold != null) {
            return false;
        }
        if (italic != null) {
            return false;
        }
        if (fontSize != null) {
            return false;
        }
        if (fontFamily != null) {
            return false;
        }
        if (underlined != null) {
            return false;
        }
        if (strikeThrough != null) {
            return false;
        }
        if (font != null) {
            return false;
        }
        if (shadowColor != null) {
            return false;
        }
        if (shadowTranslation != null) {
            return false;
        }
        if (stroke != null) {
            return false;
        }

        return true;
    }

    public Integer getBackgroundColorIndex() {
        return backgroundColorIndex;
    }

    public NTxTextOptions setBackgroundColorIndex(Integer backgroundColorIndex) {
        this.backgroundColorIndex = backgroundColorIndex;
        return this;
    }

    public Integer getForegroundColorIndex() {
        return foregroundColorIndex;
    }

    public NTxTextOptions setForegroundColorIndex(Integer foregroundColorIndex) {
        this.foregroundColorIndex = foregroundColorIndex;
        return this;
    }

    public Boolean getBold() {
        return bold;
    }

    public NTxTextOptions setBold(Boolean bold) {
        this.bold = bold;
        return this;
    }

    public Boolean getItalic() {
        return italic;
    }

    public NTxTextOptions setItalic(Boolean italic) {
        this.italic = italic;
        return this;
    }

    public Float getFontSize() {
        return fontSize;
    }

    public NTxTextOptions setFontSize(Float fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public NTxTextOptions setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    public Boolean getUnderlined() {
        return underlined;
    }

    public NTxTextOptions setUnderlined(Boolean underlined) {
        this.underlined = underlined;
        return this;
    }

    public Boolean getStrikeThrough() {
        return strikeThrough;
    }

    public NTxTextOptions setStrikeThrough(Boolean strikeThrough) {
        this.strikeThrough = strikeThrough;
        return this;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public NTxTextOptions setStroke(Stroke stroke) {
        this.stroke = stroke;
        return this;
    }

    public Paint getBackgroundColor() {
        return backgroundColor;
    }

    public NTxTextOptions setBackgroundColor(Paint backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Paint getForegroundColor() {
        return foregroundColor;
    }

    public NTxTextOptions setForegroundColor(Paint foregroundColor) {
        this.foregroundColor = foregroundColor;
        return this;
    }


    public Font getFont() {
        return font;
    }

    public NTxTextOptions setFont(Font font) {
        this.font = font;
        return this;
    }

    public Paint getShadowColor() {
        return shadowColor;
    }

    public NTxTextOptions setShadowColor(Paint shadowColor) {
        this.shadowColor = shadowColor;
        return this;
    }

    public NTxPoint2D getShadowTranslation() {
        return shadowTranslation;
    }

    public NTxTextOptions setShadowTranslation(NTxPoint2D shadowTranslation) {
        this.shadowTranslation = shadowTranslation;
        return this;
    }

    public boolean isStyled() {
        if (underlined != null) {
            return true;
        }
        if (strikeThrough != null) {
            return true;
        }
        if (foregroundColorIndex != null) {
            return true;
        }
        if (backgroundColorIndex != null) {
            return true;
        }
        if (foregroundColor != null) {
            return true;
        }
        if (bold != null) {
            return true;
        }
        if (italic != null) {
            return true;
        }
        if (fontSize != null) {
            return true;
        }
        if (fontFamily != null) {
            return true;
        }
        if (backgroundColor != null) {
            return true;
        }
        return false;
    }

    public AttributedString createAttributedString(String text, Graphics2D g) {
        AttributedString attributedString = new AttributedString(text);
        int strLen = text.length();
        if (underlined != null && underlined) {
            attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0, strLen);
        }
        if (strikeThrough != null && strikeThrough) {
            attributedString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 0, strLen);
        }
        if (foregroundColor != null) {
            attributedString.addAttribute(TextAttribute.FOREGROUND, foregroundColor, 0, strLen);
        }
        if (backgroundColor != null) {
            attributedString.addAttribute(TextAttribute.BACKGROUND, backgroundColor, 0, strLen);
        }
        applyFont(attributedString, g);
        return attributedString;
    }

    private void applyFont(AttributedString attributedString, Graphics2D g) {
        Font f = font;
        boolean someChanges = font != null;
        if (f == null) {
            f = g.getFont();
        }
        if (fontFamily == null || fontSize != null || bold != null || italic != null) {
            int oldStyle = f.getStyle();
            if (bold != null) {
                if (bold) {
                    oldStyle |= Font.BOLD;
                } else {
                    oldStyle &= ~Font.BOLD;
                }
            }
            if (italic != null) {
                if (italic) {
                    oldStyle |= Font.ITALIC;
                } else {
                    oldStyle &= ~Font.ITALIC;
                }
            }
            if (NBlankable.isBlank(fontFamily)) {
                f = new Font(fontFamily, oldStyle, fontSize == null ? f.getSize() : fontSize.intValue());
            } else {
                f = f.deriveFont(oldStyle, fontSize == null ? f.getSize() : fontSize);
            }
            someChanges = true;
        }
        if (someChanges) {
            attributedString.addAttribute(TextAttribute.FONT, f);
        }
    }

    public AttributedString createShadowAttributedString(String text, Graphics2D g) {
        AttributedString attributedString = new AttributedString(text);
        int strLen = text.length();
        if (underlined != null && underlined) {
            attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 0, strLen);
        }
        if (strikeThrough != null && strikeThrough) {
            attributedString.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 0, strLen);
        }
        if (shadowColor != null) {
            attributedString.addAttribute(TextAttribute.FOREGROUND, shadowColor, 0, strLen);
        }
        if (backgroundColor != null) {
            attributedString.addAttribute(TextAttribute.BACKGROUND, backgroundColor, 0, strLen);
        }
        applyFont(attributedString, g);
        return attributedString;
    }

    public NTxTextOptions copyNonNullFrom(NTxTextOptions other) {
        if (other != null) {
            if (other.backgroundColor != null) {
                this.backgroundColor = other.backgroundColor;
            }
            if (other.foregroundColor != null) {
                this.foregroundColor = other.foregroundColor;
            }
            if (other.underlined != null) {
                this.underlined = other.underlined;
            }
            if (other.italic != null) {
                this.italic = other.italic;
            }
            if (other.bold != null) {
                this.bold = other.bold;
            }
            if (other.fontSize != null) {
                this.fontSize = other.fontSize;
            }
            if (other.fontFamily != null) {
                this.fontFamily = other.fontFamily;
            }
            if (other.strikeThrough != null) {
                this.strikeThrough = other.strikeThrough;
            }
            if (other.font != null) {
                this.font = other.font;
            }
            if (other.shadowColor != null) {
                this.shadowColor = other.shadowColor;
            }
            if (other.shadowTranslation != null) {
                this.shadowTranslation = other.shadowTranslation;
            }
        }
        return this;
    }

    public NTxTextOptions copy() {
        return clone();
    }

    @Override
    protected NTxTextOptions clone() {
        try {
            return (NTxTextOptions) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
