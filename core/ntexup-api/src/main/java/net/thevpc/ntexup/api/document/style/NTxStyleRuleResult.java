package net.thevpc.ntexup.api.document.style;

import java.util.Set;

public interface NTxStyleRuleResult /*extends Comparable<HStyleRuleResult>*/ {
    boolean isValid();

    NTxStyleMagnitude magnitude();

    Set<NTxProp> value();

//    @Override
//    default int compareTo(HStyleRuleResult o) {
//        if (this.isValid() && o.isValid()) {
//            HStyleMagnitude m1 = this.magnitude();
//            HStyleMagnitude m2 = o.magnitude();
//            return m1.compareTo(m2);
//        } else if (!this.isValid() && !o.isValid()) {
//            return 0;
//        } else if (this.isValid()) {
//            return -1;
//        } else if (o.isValid()) {
//            return 1;
//        }
//        return 0;
//    }
}
