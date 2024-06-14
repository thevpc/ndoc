package net.thevpc.halfa.api.node;

import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.model.elem2d.HAlign;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HStyleRule;
import net.thevpc.nuts.util.NOptional;

import java.util.List;
import java.util.Set;

public interface HNode extends HItem {
    String getUuid();

    HNode setUuid(String uuid);

    String[] getAncestors();

    String[] getStyleClasses();

    HItem setAncestors(String[] parentTemplate);

    HItem setStyleClasses(String[] classNames);

    boolean append(HItem a);

    Object source();

    HNode setSource(Object source);

    boolean isTemplate();

    HNode setTemplate(boolean template);

    boolean isDisabled();

    HNode setDisabled(boolean enabled);

    HNode parent();

    String name();

    String type();

    List<HProp> props();

    NOptional<Object> getPropertyValue(String styleType);

    NOptional<HProp> getProperty(String propertyName);

    List<HProp> getProperties();


    HNode setName(String name);

    HNode setProperty(HProp s);

    HNode setProperty(String name, Object value);

    HNode setProperties(HProp... props);

    HNode addStyleClass(String className);

    HNode addStyleClasses(String... classNames);

    HNode removeClass(String className);

    boolean hasClass(String className);

    Set<String> styleClasses();

    HNode unsetProperty(String s);

    HNode setParent(HNode parent);

    HNode mergeNode(HItem other);

    HNode setPosition(HAlign align);

    HNode setPosition(Number x, Number y);

    HNode setPosition(Double2 d);

    HNode setOrigin(HAlign align);

    HNode setOrigin(Number x, Number y);

    HNode setOrigin(Double2 d);

    HNode at(HAlign align);

    HNode at(Number x, Number y);

    HNode at(Double2 d);

    HNode setSize(Number size);

    HNode setSize(Double2 size);

    HNode setSize(Number w, Number h);

    HNode setFontSize(Number w);

    HNode setFontFamily(String w);

    HNode setFontBold(Boolean w);

    HNode setFontItalic(Boolean w);

    HNode setFontUnderlined(Boolean w);

    HNode setForegroundColor(String w);

    HNode setBackgroundColor(String w);

    HNode setLineColor(String w);

    HNode setGridColor(String w);

    List<HNode> children();


    HNode add(HNode a);

    HNode addAll(HNode... a);

    HNode setChildren(HNode... a);


    HNode addRule(HStyleRule s);

    HNode removeRule(HStyleRule s);

    HNode addRules(HStyleRule... s);

    HNode clearRules();

    HStyleRule[] rules();

    HNode copy();

    String getName();

    HNode setRules(HStyleRule[] rules);

    void setChildAt(int i, HNode c);
}
