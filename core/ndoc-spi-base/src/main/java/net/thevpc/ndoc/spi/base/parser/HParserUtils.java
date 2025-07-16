package net.thevpc.ndoc.spi.base.parser;

import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.elem.NElement;

public class HParserUtils {
    public static boolean isIntOrExprNonCommon(NElement currentArg){
        switch (currentArg.type().typeGroup()) {
            case OPERATOR:{
                return true;
            }
        }
        switch (currentArg.type()) {
            case BYTE:
            case SHORT:
            case INTEGER:
            case LONG:
            {
                return true;
            }
            case DOUBLE_QUOTED_STRING:
            case SINGLE_QUOTED_STRING:
            case ANTI_QUOTED_STRING:
            case TRIPLE_DOUBLE_QUOTED_STRING:
            case TRIPLE_SINGLE_QUOTED_STRING:
            case TRIPLE_ANTI_QUOTED_STRING:
            case LINE_STRING:
            case NAME:
            {
                if (!isCommonStyleProperty(currentArg.asStringValue().get())) {
                    return true;
                }
                break;
            }

            case UPLET:
            case NAMED_UPLET:

            case NULL:
                return true;
        }
        return false;
    }

    public static boolean isCommonStyleProperty(String s) {
        if (NBlankable.isBlank(s)) {
            return false;
        }
        s = net.thevpc.ndoc.api.util.HUtils.uid(s);
        return HStyleParser.COMMON_STYLE_PROPS.contains(s);
    }
}
