package net.thevpc.halfa.engine.document;

import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.style.*;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.util.NOptional;

import java.util.*;
import java.util.stream.Collectors;

public class HPropCalculator {


    public NOptional<HProp> computeProperty(HNode node, String[] propertyNames) {
        return computePropertyMagnetude(node, propertyNames).map(HStyleAndMagnitude::getStyle);
    }

    private static class HStyleRuleResult2 {
        HStyleRule rule;
        HProp property;

        public HStyleRuleResult2(HStyleRule rule, HProp property) {
            this.rule = rule;
            this.property = property;
        }

        @Override
        public String toString() {
            return "HStyleRuleResult2{" +
                    "rule=" + rule +
                    ", property=" + property +
                    '}';
        }
    }

    private HStyleRuleResult2[] _HStyleRuleResult2s(HNode t, HNode p) {
        HStyleRule[] rules = p.rules();
        List<HStyleRuleResult2> rr = new ArrayList<>();
        for (HStyleRule rule : rules) {
            if (rule.accept(t)) {
                for (HProp style : rule.styles().toList()) {
                    rr.add(new HStyleRuleResult2(rule, style));
                }
            }
        }
        return rr.toArray(new HStyleRuleResult2[0]);
    }

    private HStyleRuleResult2[] _HStyleRuleResult2(HNode t, HNode p, String[] propertyNames) {
        HStyleRule[] rules = p.rules();
        List<HStyleRuleResult2> rr = new ArrayList<>();
        for (HStyleRule rule : rules) {
            if (rule.accept(t)) {
                NOptional<HProp> ok = rule.styles().get(propertyNames);
                if(ok.isPresent()){
                    rr.add(new HStyleRuleResult2(rule, ok.get()));
//                    break;
                }
            }
        }
        return rr.toArray(new HStyleRuleResult2[0]);
    }

    public NOptional<HStyleAndMagnitude> computePropertyMagnetude(HNode node, String[] propertyNames) {
        propertyNames = HUtils.uids(propertyNames);
        NOptional<HProp> u = node.getProperty(propertyNames);
        if (u.isPresent()) {
            return NOptional.of(
                    new HStyleAndMagnitude(
                            u.get(),
                            new HStyleMagnitude(0, DefaultHNodeSelector.ofImportant())
                    )
            );
        }
        HNode p = node.parent();
        int distance = 1;
        while (p != null) {
            HStyleMagnitude bestMag = null;
            HProp bestStyle = null;
            HStyleRuleResult2[] validRules = _HStyleRuleResult2(node, p, propertyNames);
            for (HStyleRuleResult2 rule : validRules) {
                HStyleMagnitude m2 = new HStyleMagnitude(distance, rule.rule.selector());
                if (bestMag == null || m2.compareTo(bestMag) <= 0) {
                    bestMag = m2;
                    bestStyle = rule.property;
                }
            }
            if (bestMag != null) {
                return NOptional.of(
                        new HStyleAndMagnitude(
                                bestStyle,
                                new HStyleMagnitude(distance, bestMag.getSelector())
                        )
                );
            }
            distance++;
            p = p.parent();
        }
        return NOptional.ofNamedEmpty("no style : " + Arrays.asList(propertyNames));
    }

    public List<HStyleAndMagnitude> computePropertiesMagnetude(HNode node) {
        Map<String, HStyleAndMagnitude> found = new LinkedHashMap<>();
        for (HProp property : node.getProperties()) {
            found.put(property.getName(),
                    new HStyleAndMagnitude(
                            property,
                            new HStyleMagnitude(0, DefaultHNodeSelector.ofImportant())
                    )
            );
        }
        HNode p = node.parent();
        int distance = 1;
        while (p != null) {
            HStyleRuleResult2[] validRules = _HStyleRuleResult2s(node, p);
            for (HStyleRuleResult2 rule : validRules) {
                HStyleAndMagnitude m2 = new HStyleAndMagnitude(rule.property, new HStyleMagnitude(distance, rule.rule.selector()));
                HStyleAndMagnitude hStyleAndMagnitude = found.get(rule.property.getName());
                if (hStyleAndMagnitude == null || m2.getMagnetude().compareTo(hStyleAndMagnitude.getMagnetude()) <= 0) {
                    found.put(rule.property.getName(), m2);
                }
            }
            distance++;
            p = p.parent();
        }
        return new ArrayList<>(found.values());
    }

    public List<HProp> computeProperties(HNode node) {
        return computePropertiesMagnetude(node).stream().map(x -> x.getStyle()).collect(Collectors.toList());
    }

    public List<HProp> computeInheritedProperties(HNode node) {
        return computePropertiesMagnetude(node).stream()
                .filter(x -> x.getMagnetude().getDistance() > 0)
                .map(x -> x.getStyle()).collect(Collectors.toList());
    }

    public <T> NOptional<T> computePropertyValue(HNode t, String ...s) {
        if (t != null) {
            return computeProperty(t, s).map(HProp::getValue).map(x -> {
                try {
                    return (T) x;
                } catch (ClassCastException e) {
                    return null;
                }
            }).filter(x -> x != null);
        }
        return NOptional.ofNamedEmpty("style " + Arrays.asList(s));
    }
}
