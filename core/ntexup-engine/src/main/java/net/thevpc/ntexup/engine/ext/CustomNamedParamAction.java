package net.thevpc.ntexup.engine.ext;

import net.thevpc.ntexup.api.document.style.NTxProp;
import net.thevpc.ntexup.api.engine.NDocNodeCustomBuilderContext;
import net.thevpc.ntexup.api.parser.NDocArgumentReader;
import net.thevpc.ntexup.api.util.NDocUtils;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NElementTypeGroup;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

class CustomNamedParamAction implements NDocNodeCustomBuilderContext.NamedParamAction, NDocNodeCustomBuilderContext.ProcessParamAction {
    NDocNodeCustomBuilderContextImpl base;
    Set<String> expectedNames = new HashSet<>();
    Predicate<NDocArgumentReader> matches;
    String toName;
    boolean flags;
    NDocNodeCustomBuilderContext.PropResolver propResolver;
    boolean ignoreDuplicates;
    boolean matchesLeading;

    public CustomNamedParamAction(NDocNodeCustomBuilderContextImpl base) {
        if (base.customNamedParamAction != null) {
            throw new RuntimeException("must call end");
        }
        this.base = base;
        base.customNamedParamAction = this;
    }


    @Override
    public NDocNodeCustomBuilderContext.NamedParamAction asFlags() {
        this.flags = true;
        return this;
    }

    public boolean isIgnoreDuplicates() {
        return ignoreDuplicates;
    }

    public NDocNodeCustomBuilderContext.NamedParamAction ignoreDuplicates() {
        this.ignoreDuplicates = true;
        return this;
    }

    public NDocNodeCustomBuilderContext.NamedParamAction ignoreDuplicates(boolean ignoreDuplicates) {
        this.ignoreDuplicates = ignoreDuplicates;
        return this;
    }

    public NDocNodeCustomBuilderContext.NamedParamAction matchesLeading() {
        this.matchesLeading = true;
        return this;
    }

    public NDocNodeCustomBuilderContext.NamedParamAction matchesLeading(boolean matchesLeading) {
        this.matchesLeading = matchesLeading;
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext.NamedParamAction matchesString() {
        return matches(new Predicate<NDocArgumentReader>() {
            @Override
            public boolean test(NDocArgumentReader info) {
                NElement x = info.peek();
                return x.type().typeGroup() == NElementTypeGroup.STRING;
            }

            @Override
            public String toString() {
                return "(type().typeGroup() == STRING)";
            }
        });
    }

    @Override
    public NDocNodeCustomBuilderContext.NamedParamAction matchesStringOrName() {
        return matches(new Predicate<NDocArgumentReader>() {
            @Override
            public boolean test(NDocArgumentReader info) {
                NElement x = info.peek();
                return x.type().typeGroup() == NElementTypeGroup.STRING || x.type().typeGroup() == NElementTypeGroup.NAME;
            }

            @Override
            public String toString() {
                return "(type().typeGroup() == STRING|NAME)";
            }
        });
    }

    @Override
    public NDocNodeCustomBuilderContext.NamedParamAction matchesName() {
        return matches(new Predicate<NDocArgumentReader>() {
            @Override
            public boolean test(NDocArgumentReader info) {
                NElement x = info.peek();
                return x.type().typeGroup() == NElementTypeGroup.NAME;
            }

            @Override
            public String toString() {
                return "(type().typeGroup() == NAME)";
            }
        });
    }

    @Override
    public NDocNodeCustomBuilderContext.NamedParamAction matches(Predicate<NDocArgumentReader> predicate) {
        this.matches = predicate;
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext.NamedParamAction named(String... names) {
        expectedNames.addAll(Arrays.asList(names));
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext.NamedParamAction store(String newName) {
        this.toName = newName;
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext.NamedParamAction resolvedAs(NDocNodeCustomBuilderContext.PropResolver a) {
        this.propResolver = a;
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext.NamedParamAction resolvedAsTrimmedBloc() {
        return resolvedAs((uid, value, info, buildContext) -> NTxProp.ofString(uid, buildContext.engine().tools().trimBloc(value.asStringValue().orNull())));
    }

    @Override
    public NDocNodeCustomBuilderContext.NamedParamAction resolvedAsTrimmedPathTextContent() {
        return resolvedAs(new NDocNodeCustomBuilderContext.PropResolver() {
            @Override
            public NTxProp resolve(String uid, NElement value, NDocArgumentReader info, NDocNodeCustomBuilderContext buildContext) {
                NPath nPath = buildContext.engine().resolvePath(value.asString().get(), info.node());
                info.parseContext().document().resources().add(nPath);
                return NTxProp.ofString(uid, nPath.readString().trim());
            }
        });
    }

    @Override
    public NDocNodeCustomBuilderContext then() {
        return end();
    }

    @Override
    public NDocNodeCustomBuilderContext end() {
        if (base.knownArgNames == null) {
            base.knownArgNames = new HashSet<>();
        }
        if (toName != null) {
            base.knownArgNames.add(toName);
        } else {
            base.knownArgNames.addAll(expectedNames);
        }
        if (base.processSingleArgumentList == null) {
            base.processSingleArgumentList = new ArrayList<>();
        }
        base.processSingleArgumentList.add(this);
        this.base.customNamedParamAction = null;
        return base;
    }

    public boolean processParam(NDocArgumentReader info, NDocNodeCustomBuilderContext buildContext) {
        if (matchesLeading) {
            if (info.currentIndex() != 0) {
                return false;
            }
        }
        if (expectedNames.isEmpty()) {
            NElement n = info.peek();
            if (matches.test(info)) {
                info.read();
                String uid = NDocUtils.uid(toName);
                setProp(uid, n, info, buildContext);
                return true;
            }
        } else {
            NElement n = info.peek();
            switch (n.type()) {
                case PAIR: {
                    if (n.isSimplePair()) {
                        NPairElement p = n.asPair().get();
                        String uid = NDocUtils.uid(p.key().asStringValue().get());
                        boolean u = expectedNames.contains(uid);
                        if (u) {
                            info.read();
                            setProp(uid, p.value(), info, buildContext);
                            return true;
                        }
                    }
                    break;
                }
                case NAME: {
                    if (flags) {
                        String uid = NDocUtils.uid(n.asNameValue().get());
                        boolean u = expectedNames.contains(uid);
                        if (u) {
                            info.read();
                            setProp(uid, NElement.ofTrue(), info, buildContext);
                            return true;
                        }
                    }
                    break;
                }
            }
        }
        return false;
    }

    private void setProp(String uid, NElement p, NDocArgumentReader info, NDocNodeCustomBuilderContext buildContext) {
        try {
            String okName;
            if (toName == null) {
                okName = uid;
            } else {
                okName = NDocUtils.uid(toName);
            }
            NTxProp prp = propResolver == null ?
                    NTxProp.of(okName, p)
                    : propResolver.resolve(okName, p, info, buildContext);
            String oldCDP = NDocUtils.getCompilerDeclarationPath(p);
            String newCDP = NDocUtils.getCompilerDeclarationPath(prp.getValue());
            if (newCDP == null) {
                prp = NTxProp.of(prp.getName(), NDocUtils.addCompilerDeclarationPath(prp.getValue(), oldCDP));
            }
            if (ignoreDuplicates) {
                NOptional<NTxProp> o = info.node().getProperty(prp.getName());
                if (!o.isPresent()) {
                    info.node().setProperty(prp);
                }
            } else {
                info.node().setProperty(prp);
            }
        } catch (Exception ex) {
            info.parseContext().messages().log(
                    NMsg.ofC("unable to set %s : %s", NDocUtils.snippet(p), ex).asSevere()
            );
        }
    }

}
