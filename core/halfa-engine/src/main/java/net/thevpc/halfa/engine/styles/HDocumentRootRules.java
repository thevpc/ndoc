package net.thevpc.halfa.engine.styles;

import net.thevpc.halfa.api.node.HNodeType;
import net.thevpc.halfa.api.style.HStyles;
import net.thevpc.halfa.api.style.DefaultHStyleRule;
import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.api.style.HStyleRule;

import java.awt.*;

public class HDocumentRootRules {
    public static final HStyleRule[] DEFAULT = {
            DefaultHStyleRule.ofClass("ul-bullet",
                    HStyles.origin(HAlign.CENTER),
                    HStyles.position(HAlign.CENTER),
                    HStyles.backgroundColor(new Color(0x0b3123)),
                    HStyles.lineColor(new Color(0x0b3123).darker())

            ),
            DefaultHStyleRule.ofClass("ul-item",
                    HStyles.origin(HAlign.LEFT),
                    HStyles.position(HAlign.LEFT)
            ),
            DefaultHStyleRule.ofClass("ol-bullet",
                    HStyles.origin(HAlign.CENTER),
                    HStyles.position(HAlign.CENTER),
                    HStyles.backgroundColor(new Color(0x0b3123)),
                    HStyles.lineColor(new Color(0x0b3123).darker())

            ),
            DefaultHStyleRule.ofClass("ol-item",
                    HStyles.origin(HAlign.LEFT),
                    HStyles.position(HAlign.LEFT)
            ),

            DefaultHStyleRule.ofType(HNodeType.ELLIPSE,
                    HStyles.position(HAlign.CENTER),
                    HStyles.origin(HAlign.CENTER)
            ),
            DefaultHStyleRule.ofType(HNodeType.ARC,
                    HStyles.position(HAlign.CENTER),
                    HStyles.origin(HAlign.CENTER)
            ),
            DefaultHStyleRule.ofAny(
                    HStyles.fontFamily("Verdana"),
                    HStyles.fontSize(40),
                    HStyles.size(100, 100),
                    HStyles.origin(HAlign.TOP_LEFT),
                    HStyles.position(HAlign.TOP_LEFT),
                    HStyles.gridColor(Color.lightGray)
            ),
    };
}
