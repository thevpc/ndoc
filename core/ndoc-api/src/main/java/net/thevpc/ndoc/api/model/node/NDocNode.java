package net.thevpc.ndoc.api.model.node;

import net.thevpc.ndoc.api.model.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.model.elem2d.NDocAlign;
import net.thevpc.ndoc.api.resources.NDocResource;
import net.thevpc.ndoc.api.style.HProp;
import net.thevpc.ndoc.api.style.HStyleRule;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;
import net.thevpc.nuts.util.NOptional;

import java.util.List;
import java.util.Set;

public interface NDocNode extends HItem {
    String getUuid();

    NDocNode setUuid(String uuid);

    String[] getAncestors();

    String[] getStyleClasses();

    HItem setAncestors(String[] parentTemplate);

    HItem setStyleClasses(String[] classNames);

    boolean append(HItem a);

    NDocResource source();

    NDocNode setSource(NDocResource source);

    boolean isTemplate();

    NDocNode setTemplate(boolean template);

    boolean isDisabled();

    NDocNode setDisabled(boolean enabled);

    NDocNode parent();

    String name();

    String type();

    List<HProp> props();

    NOptional<NElement> getVar(String property);

    NDocNode setVar(String name, NElement value);

    NOptional<NElement> getPropertyValue(String... propertyNames);

    NOptional<HProp> getProperty(String... propertyNames);

    List<HProp> getProperties();


    NDocNode setName(String name);

    NDocNode setProperty(HProp s);

    NDocNode setProperty(String name, NElement value);

    NDocNode setProperty(String name, NToElement value);

    NDocNode setProperties(HProp... props);

    NDocNode addStyleClass(String className);

    NDocNode addStyleClasses(String... classNames);

    NDocNode removeClass(String className);

    boolean hasClass(String className);

    Set<String> styleClasses();

    NDocNode unsetProperty(String s);

    NDocNode setParent(NDocNode parent);

    NDocNode mergeNode(HItem other);

    NDocNode setPosition(NDocAlign align);

    NDocNode setPosition(Number x, Number y);

    NDocNode setPosition(NDocDouble2 d);

    NDocNode setOrigin(NDocAlign align);

    NDocNode setOrigin(Number x, Number y);

    NDocNode setOrigin(NDocDouble2 d);

    NDocNode at(NDocAlign align);

    NDocNode at(Number x, Number y);

    NDocNode at(NDocDouble2 d);

    NDocNode setSize(Number size);

    NDocNode setSize(NDocDouble2 size);

    NDocNode setSize(Number w, Number h);

    NDocNode setFontSize(Number w);

    NDocNode setFontFamily(String w);

    NDocNode setFontBold(Boolean w);

    NDocNode setFontItalic(Boolean w);

    NDocNode setFontUnderlined(Boolean w);

    NDocNode setFontStrike(Boolean w);

    NDocNode setForegroundColor(String w);

    NDocNode setBackgroundColor(String w);

//    NDocNode setLineColor(String w);

    NDocNode setGridColor(String w);

    List<NDocNode> children();


    NDocNode add(NDocNode a);

    NDocNode addAll(NDocNode... a);

    NDocNode setChildren(NDocNode... a);


    NDocNode addRule(HStyleRule s);

    NDocNode removeRule(HStyleRule s);

    NDocNode addRules(HStyleRule... s);

    NDocNode clearRules();

    HStyleRule[] rules();

    NDocNode copy();

    String getName();

    NDocNode setRules(HStyleRule[] rules);

    void setChildAt(int i, NDocNode c);

    NDocNodeDef[] definitions();

    NDocNode addDefinition(NDocNodeDef s) ;

    NDocNode removeDefinition(NDocNodeDef s) ;

    NDocNode addDefinitions(NDocNodeDef... definitions);
}
