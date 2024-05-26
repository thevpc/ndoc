package net.thevpc.halfa.api.node;

import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.model.HAlign;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HStyleAndMagnitude;
import net.thevpc.nuts.util.NOptional;

import java.awt.*;
import java.util.List;
import java.util.Set;

public interface HNode extends HItem {
    String getParentTemplate();

    HItem setParentTemplate(String parentTemplate);

    boolean append(HItem a);

    Object source();

    Object computeSource();

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

    NOptional<HProp> computeProperty(String propertyName);

    NOptional<HStyleAndMagnitude> computePropertyMagnetude(String propertyName);


    HNode setName(String name);

    HNode setProperty(HProp s);

    HNode setProperty(String name, Object value);

    HNode addClass(String className);

    HNode addClasses(String... classNames);

    HNode removeClass(String className);

    boolean hasClass(String className);

    Set<String> styleClasses();

    HNode unsetProperty(String s);

    void setParent(HNode parent);

    void mergeNode(HItem other);

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

}
