package net.thevpc.halfa.api.node;

import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.api.style.HStyleAndMagnitude;
import net.thevpc.halfa.api.style.HStyleType;
import net.thevpc.nuts.util.NOptional;

import java.util.List;
import java.util.Set;

public interface HNode extends HItem{
    String getParentTemplate() ;

    HItem setParentTemplate(String parentTemplate);

    boolean append(HItem a);

    Object getSource();

    HNode setSource(Object source);

    boolean isTemplate();

    HNode setTemplate(boolean template);

    boolean isDisabled();

    HNode setDisabled(boolean enabled);

    HNode parent();

    String name();

    HNodeType type();

    List<HStyle> styles();

    NOptional<HStyle> getStyle(HStyleType s);

    NOptional<HStyle> computeStyle(HStyleType s);

    NOptional<HStyleAndMagnitude> computeStyleMagnetude(HStyleType s);


    HNode setName(String name);

    HNode set(HStyle s);

    HNode addClass(String className);

    HNode addClasses(String... classNames);

    HNode removeClass(String className);

    boolean hasClass(String className);

    Set<String> styleClasses();

    HNode unset(HStyleType s);

    void setParent(HNode parent);

    void mergeNode(HItem other);

}
