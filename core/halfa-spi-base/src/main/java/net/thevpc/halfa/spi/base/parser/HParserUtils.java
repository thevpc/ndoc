package net.thevpc.halfa.spi.base.parser;

import net.thevpc.nuts.util.NBlankable;
import net.thevpc.tson.TsonElement;

public class HParserUtils {
    public static boolean isIntOrExprNonCommon(TsonElement currentArg){
        switch (currentArg.type()) {
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            {
                return true;
            }
            case STRING:
            case NAME:
            {
                if (!isCommonStyleProperty(currentArg.stringValue())) {
                    return true;
                }
                break;
            }
            case OP:
            case UPLET:
            case NULL:
                return true;
        }
        return false;
    }

    public static boolean isCommonStyleProperty(String s) {
        if (NBlankable.isBlank(s)) {
            return false;
        }
        s = net.thevpc.halfa.api.util.HUtils.uid(s);
        return HStyleParser.COMMON_STYLE_PROPS.contains(s);
    }
}
