package net.thevpc.ndoc.api.document.node;

import net.thevpc.ndoc.api.document.elem2d.NDocDouble2;
import net.thevpc.ndoc.api.document.elem2d.NDocAlign;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocStyleRule;
import net.thevpc.ndoc.api.eval.NDocFunction;
import net.thevpc.ndoc.api.parser.NDocResource;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NToElement;
import net.thevpc.nuts.util.NOptional;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface NDocNode extends NDocItem {
    String getUuid();

    NDocNode setUuid(String uuid);


    String[] getStyleClasses();


    NDocItem setStyleClasses(String[] classNames);

    boolean append(NDocItem a);

    NDocResource source();

    NDocNode setSource(NDocResource source);

    boolean isDisabled();

    NDocNode setDisabled(boolean enabled);

    String name();

    String type();

    List<NDocProp> props();

    NOptional<NElement> getVar(String property);

    Map<String, NElement> getVars();

    NDocNode setVar(String name, NElement value);

    NOptional<NElement> getPropertyValue(String... propertyNames);

    NOptional<NDocProp> getProperty(String... propertyNames);

    List<NDocProp> getProperties();


    NDocNode setName(String name);

    NDocNode setProperty(NDocProp s);

    NDocNode setProperty(String name, NElement value);

    NDocNode setProperty(String name, NToElement value);

    NDocNode setProperties(NDocProp... props);

    NDocNode addStyleClass(String className);

    NDocNode addStyleClasses(String... classNames);

    NDocNode removeClass(String className);

    boolean hasClass(String className);

    Set<String> styleClasses();

    NDocNode unsetProperty(String s);

    NDocNode setParent(NDocItem parent);

    NDocNode mergeNode(NDocItem other);

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


    NDocNode addRule(NDocStyleRule s);

    NDocNode removeRule(NDocStyleRule s);

    NDocNode addRules(NDocStyleRule... s);

    NDocNode clearRules();

    NDocStyleRule[] rules();

    NDocNode copyTo(NDocNode o);

    NDocNode copyFrom(NDocNode o);

    NDocNode copy();

    String getName();

    NDocNode setRules(NDocStyleRule[] rules);

    void setChildAt(int i, NDocNode c);

    NDocNodeDef[] nodeDefinitions();

    NDocNode addNodeDefinition(NDocNodeDef s);

    NDocNode addNodeDefinitions(NDocNodeDef... definitions);

    NDocNode removeNodeDefinition(NDocNodeDef s);

    NDocFunction[] nodeFunctions();

    NDocNode addNodeFunction(NDocFunction s);

    NDocNode addNodeFunctions(NDocFunction... definitions);

    NDocNode removeNodeFunction(String name);

    NDocNode reset();
}
