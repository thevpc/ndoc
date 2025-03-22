package net.thevpc.ndoc.api.style;

import java.util.Set;

public interface HStyleRuleResult /*extends Comparable<HStyleRuleResult>*/ {
    boolean isValid();

    HStyleMagnitude magnitude();

    Set<HProp> value();

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
