package net.thevpc.ndoc.engine;

import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocProp;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.engine.NDocEngine;
import net.thevpc.ndoc.api.eval.NDocObjEx;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilder;
import net.thevpc.ndoc.api.extension.NDocNodeCustomBuilderContext;
import net.thevpc.ndoc.api.parser.NDocNodeParser;
import net.thevpc.ndoc.api.parser.ParseArgumentInfo;
import net.thevpc.ndoc.api.renderer.NDocNodeRenderer;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererBase;
import net.thevpc.ndoc.api.renderer.NDocNodeRendererContext;
import net.thevpc.ndoc.api.util.NDocUtils;
import net.thevpc.ndoc.api.util.ToElementHelper;
import net.thevpc.nuts.NIllegalArgumentException;
import net.thevpc.nuts.elem.NElement;
import net.thevpc.nuts.elem.NPairElement;
import net.thevpc.nuts.util.NAssert;
import net.thevpc.nuts.util.NBlankable;
import net.thevpc.nuts.util.NMsg;

import java.util.*;

public class MyNDocNodeCustomBuilderContext implements NDocNodeCustomBuilderContext {
    private NDocNodeCustomBuilder builder;
    private RenderAction renderMain;
    private String id;
    private String[] aliases;
    private ToElemAction toElem;
    private NDocNodeRendererBase nDocNodeRendererBase;
    private NDocNodeParserBase nDocNodeParserBase;
    private List<ProcessArgAction> processSingleArgumentList;
    private ProcessArgAction processAllArguments;
    private boolean compiled;
    private NDocEngine engine;
    Map<String, Doer> acceptable;
    Set<String> knownArgNames;

    public MyNDocNodeCustomBuilderContext(NDocNodeCustomBuilder builder, NDocEngine engine) {
        this.builder = builder;
        this.engine = engine;
    }

    private void requireNonCompiled() {
        if (compiled) {
            throw new NIllegalArgumentException(NMsg.ofC("context is readonly"));
        }
    }

    private void requireCompiled() {
        if (!compiled) {
            throw new NIllegalArgumentException(NMsg.ofC("context is still in building process"));
        }
    }

    @Override
    public NDocNodeCustomBuilderContext id(String id) {
        requireNonCompiled();
        this.id = id;
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext withAliases(String... aliases) {
        requireNonCompiled();
        this.aliases = aliases;
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext withToElem(ToElemAction e) {
        requireNonCompiled();
        this.toElem = e;
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext withToElem(String... propNames) {
        return withToElem((item, context) -> ToElementHelper.of(
                        item,
                        context.engine()
                )
                .addChildrenByName(propNames)
                .build());
    }

    @Override
    public NDocNodeCustomBuilderContext render(RenderAction e) {
        requireNonCompiled();
        this.renderMain = e;
        return this;
    }

    public NDocNodeCustomBuilderContext parseDefaultParamNames() {
        return withProcessArg(new ProcessArgAction() {
            @Override
            public boolean processArgument(ParseArgumentInfo info, NDocNodeCustomBuilderContext buildContext) {
                createParser();
                return nDocNodeParserBase.defaultProcessArgument(info);
            }
        });
    }

    public NDocNodeCustomBuilderContext parseParamNames(String... names) {
        return parseParamNames(names, new Doer() {
            @Override
            public void doit(String uid, NElement value, ParseArgumentInfo info, NDocNodeCustomBuilderContext buildContext) {
                info.node.setProperty(uid, value);
            }
        });
    }

    public NDocNodeCustomBuilderContext parseAsDouble(String... names) {
        return parseParamNames(names, new Doer() {
            @Override
            public void doit(String uid, NElement value, ParseArgumentInfo info, NDocNodeCustomBuilderContext buildContext) {
                info.node.setProperty(NDocProp.ofDouble(uid, NDocObjEx.of(value).asDouble().get()));
            }
        });
    }

    public NDocNodeCustomBuilderContext parseAsDoubleArray(String... names) {
        return parseParamNames(names, new Doer() {
            @Override
            public void doit(String uid, NElement value, ParseArgumentInfo info, NDocNodeCustomBuilderContext buildContext) {
                info.node.setProperty(NDocProp.ofDoubleArray(uid, NDocObjEx.of(value).asDoubleArray().get()));
            }
        });
    }

    @Override
    public NDocNodeCustomBuilderContext parseAsStringArray(String... names) {
        return parseParamNames(names, new Doer() {
            @Override
            public void doit(String uid, NElement value, ParseArgumentInfo info, NDocNodeCustomBuilderContext buildContext) {
                info.node.setProperty(NDocProp.ofStringArray(uid, NDocObjEx.of(value).asStringArray().get()));
            }
        });
    }

    @Override
    public NDocNodeCustomBuilderContext parseAsInt(String... names) {
        return parseParamNames(names, new Doer() {
            @Override
            public void doit(String uid, NElement value, ParseArgumentInfo info, NDocNodeCustomBuilderContext buildContext) {
                info.node.setProperty(NDocProp.ofInt(uid, NDocObjEx.of(value).asInt().get()));
            }
        });
    }

    @Override
    public NDocNodeCustomBuilderContext parseParamNamesAsIntArray(String... names) {
        return parseParamNames(names, new Doer() {
            @Override
            public void doit(String uid, NElement value, ParseArgumentInfo info, NDocNodeCustomBuilderContext buildContext) {
                info.node.setProperty(NDocProp.ofIntArray(uid, NDocObjEx.of(value).asIntArray().get()));
            }
        });
    }

    private NDocNodeCustomBuilderContext parseParamNames(String[] names, Doer doer) {
        if (acceptable == null) {
            acceptable = new HashMap<>();
        }
        if (knownArgNames == null) {
            knownArgNames = new HashSet<>();
        }
        for (String name : names) {
            if (!NBlankable.isBlank(name)) {
                String uid = NDocUtils.uid(name);
                acceptable.put(uid, doer);
                knownArgNames.add(uid);
            }
        }
        return this;
    }

    private interface Doer {
        void doit(String uid, NElement value, ParseArgumentInfo info, NDocNodeCustomBuilderContext buildContext);
    }

    @Override
    public NDocNodeCustomBuilderContext withProcessArg(ProcessArgAction e) {
        requireNonCompiled();
        if (e != null) {
            if (processSingleArgumentList == null) {
                this.processSingleArgumentList = new ArrayList<>();
            }
            if (acceptable != null) {
                this.processSingleArgumentList.add(new MyProcessArgAction(acceptable));
                this.acceptable = null;
            }
            this.processSingleArgumentList.add(e);
        }
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext withProcessArgs(ProcessArgAction e) {
        requireNonCompiled();
        this.processAllArguments = e;
        return this;
    }

    @Override
    public NDocEngine engine() {
        return engine;
    }

    public void compile() {
        if (acceptable != null) {
            this.processSingleArgumentList.add(new MyProcessArgAction(acceptable));
            this.acceptable = null;
        }
        if (!compiled) {
            NAssert.requireNonBlank(id, "id");
            this.compiled = true;
        }
    }

    public NDocNodeParser createParser() {
        requireCompiled();
        if (nDocNodeParserBase == null) {
            nDocNodeParserBase = new NDocNodeParserBase(true, id, aliases) {
                @Override
                public NElement toElem(NDocNode item) {
                    if (toElem != null) {
                        NElement u = toElem.toElem(item, MyNDocNodeCustomBuilderContext.this);
                        if (u != null) {
                            return u;
                        }
                    }
                    if (knownArgNames != null) {
                        return ToElementHelper.of(
                                        item,
                                        engine()
                                )
                                .addChildrenByName(knownArgNames.toArray(new String[0]))
                                .build();
                    }
                    return super.toElem(item);
                }

                @Override
                protected boolean processArgument(ParseArgumentInfo info) {
                    if (processSingleArgumentList != null) {
                        for (ProcessArgAction a : processSingleArgumentList) {
                            if (a.processArgument(info, MyNDocNodeCustomBuilderContext.this)) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return super.processArgument(info);
                }

                @Override
                protected boolean processArguments(ParseArgumentInfo info) {
                    if (processAllArguments != null) {
                        return processAllArguments.processArgument(info, MyNDocNodeCustomBuilderContext.this);
                    }
                    return super.processArguments(info);
                }

            };
        }
        return nDocNodeParserBase;
    }

    public NDocNodeRenderer createRenderer() {
        requireCompiled();
        if (nDocNodeRendererBase == null) {
            nDocNodeRendererBase = new NDocNodeRendererBase(id) {
                @Override
                public void renderMain(NDocNode p, NDocNodeRendererContext ctx) {
                    if (renderMain != null) {
                        renderMain.renderMain(p, ctx, MyNDocNodeCustomBuilderContext.this);
                    }
                }
            };
        }
        return nDocNodeRendererBase;
    }

    private static class MyProcessArgAction implements ProcessArgAction {
        private Map<String, Doer> acceptable;

        public MyProcessArgAction(Map<String, Doer> acceptable) {
            this.acceptable = acceptable;
        }

        @Override
        public boolean processArgument(ParseArgumentInfo info, NDocNodeCustomBuilderContext buildContext) {
            switch (info.currentArg.type()) {
                case PAIR: {
                    if (info.currentArg.isSimplePair()) {
                        NPairElement p = info.currentArg.asPair().get();
                        String uid = NDocUtils.uid(p.key().asStringValue().get());
                        Doer u = acceptable.get(uid);
                        if (u != null) {
                            u.doit(uid, p.value(), info, buildContext);
                            return true;
                        }
                    }
                    break;
                }
            }
            return false;
        }
    }
}
