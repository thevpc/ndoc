package net.thevpc.ndoc.engine.eval;

import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElementType;
import net.thevpc.nuts.elem.NUpletElement;
import net.thevpc.nuts.reflect.NReflectUtils;

import java.math.MathContext;

public class NDocCompilerNumberUtils {
    public static NElement simplifyPars(NElement e) {
        if (e.isUplet()) {
            NUpletElement u = e.asUplet().get();
            if (u.params().size() == 1) {
                return simplifyPars(u.params().get(0));
            }
        }
        return e;
    }

    public static NElement substruct(NElement a, NElement b) {
        NElement aa = simplifyPars(a);
        NElement bb = simplifyPars(b);
        if (aa.isNumber() && bb.isNumber()) {
            Number na = aa.asNumberValue().get();
            Number nb = bb.asNumberValue().get();
            return NElement.ofNumber(NReflectUtils.substructNumbers(na, nb));
        }
        return NElement.ofUplet(NElement.ofOp(NElementType.OP_MINUS, aa, bb));
    }

    public static NElement remainder(NElement a, NElement b) {
        NElement aa = simplifyPars(a);
        NElement bb = simplifyPars(b);
        if (aa.isNumber() && bb.isNumber()) {
            Number na = aa.asNumberValue().get();
            Number nb = bb.asNumberValue().get();
            return NElement.ofNumber(NReflectUtils.reminderNumbers(na, nb));
        }
        return NElement.ofUplet(NElement.ofOp(NElementType.OP_REM, aa, bb));
    }

    public static NElement negate(NElement a) {
        NElement aa = simplifyPars(a);
        if (aa.isNumber()) {
            Number na = aa.asNumberValue().get();
            return NElement.ofNumber(NReflectUtils.negateNumber(na));
        }
        return NElement.ofUplet(NElement.ofOp(NElementType.OP_MINUS, aa));
    }

    public static NElement inv(NElement a, MathContext mc) {
        NElement aa = simplifyPars(a);
        if (aa.isNumber()) {
            Number na = aa.asNumberValue().get();
            return NElement.ofNumber(NReflectUtils.invNumber(na, mc));
        }
        return NElement.ofUplet(NElement.ofOp(NElementType.OP_DIV, aa));
    }

    public static NElement add(NElement a, NElement b) {
        NElement aa = simplifyPars(a);
        NElement bb = simplifyPars(b);
        if (aa.isNumber() && bb.isNumber()) {
            Number na = aa.asNumberValue().get();
            Number nb = bb.asNumberValue().get();
            return NElement.ofNumber(NReflectUtils.addNumbers(na, nb));
        }
        return NElement.ofUplet(NElement.ofOp(NElementType.OP_PLUS, aa, bb));
    }

    public static NElement div(NElement a, NElement b, MathContext mc) {
        NElement aa = simplifyPars(a);
        NElement bb = simplifyPars(b);
        if (aa.isNumber() && bb.isNumber()) {
            Number na = aa.asNumberValue().get();
            Number nb = bb.asNumberValue().get();
            return NElement.ofNumber(NReflectUtils.divideNumbers(na, nb, mc));
        }
        return NElement.ofUplet(NElement.ofOp(NElementType.OP_DIV, aa, bb));
    }

    public static NElement mul(NElement a, NElement b, MathContext mc) {
        NElement aa = simplifyPars(a);
        NElement bb = simplifyPars(b);
        if (aa.isNumber() && bb.isNumber()) {
            Number na = aa.asNumberValue().get();
            Number nb = bb.asNumberValue().get();
            return NElement.ofNumber(NReflectUtils.multiplyNumbers(na, nb, mc));
        }
        return NElement.ofUplet(NElement.ofOp(NElementType.OP_MUL, aa, bb));
    }
}
