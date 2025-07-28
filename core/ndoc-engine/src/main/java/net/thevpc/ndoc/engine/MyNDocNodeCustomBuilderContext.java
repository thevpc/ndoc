package net.thevpc.ndoc.engine;

import net.thevpc.ndoc.api.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.api.document.node.NDocNode;
import net.thevpc.ndoc.api.document.style.NDocPropName;
import net.thevpc.ndoc.api.engine.NDocEngine;
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
    private List<ProcessArgAction> processArgument;
    private Set<String> knownArgNames;
    private ProcessArgAction processArguments;
    private boolean compiled;
    private NDocEngine engine;

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
    public NDocNodeCustomBuilderContext withId(String id) {
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
    public NDocNodeCustomBuilderContext withRender(RenderAction e) {
        requireNonCompiled();
        return this;
    }

    public NDocNodeCustomBuilderContext withDefaultArg() {
        return withProcessArg(new ProcessArgAction() {
            @Override
            public boolean processArgument(ParseArgumentInfo info, NDocNodeCustomBuilderContext buildContext) {
                createParser();
                return nDocNodeParserBase.defaultProcessArgument(info);
            }
        });
    }

    public NDocNodeCustomBuilderContext withArgNames(String... names) {
        if (knownArgNames == null) {
            knownArgNames=new HashSet<>();
            knownArgNames.addAll(Arrays.asList(names));
        }
        return withProcessArg(new ProcessArgAction() {
            Set<String> acceptable = new HashSet<>();

            {
                for (String name : names) {
                    if (!NBlankable.isBlank(name)) {
                        acceptable.add(NDocUtils.uid(name));
                    }
                }
            }

            @Override
            public boolean processArgument(ParseArgumentInfo info, NDocNodeCustomBuilderContext buildContext) {
                switch (info.currentArg.type()) {
                    case PAIR: {
                        if (info.currentArg.isSimplePair()) {
                            NPairElement p = info.currentArg.asPair().get();
                            String uid = NDocUtils.uid(p.key().asStringValue().get());
                            if (acceptable.contains(uid)) {
                                info.node.setProperty(uid, p.value());
                                return true;
                            }
                        }
                        break;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public NDocNodeCustomBuilderContext withProcessArg(ProcessArgAction e) {
        requireNonCompiled();
        if (e != null) {
            if (processArgument == null) {
                this.processArgument = new ArrayList<>();
                this.processArgument.add(e);
            }
        }
        return this;
    }

    @Override
    public NDocNodeCustomBuilderContext withProcessArgs(ProcessArgAction e) {
        requireNonCompiled();
        this.processArguments = e;
        return this;
    }

    @Override
    public NDocEngine engine() {
        return engine;
    }

    public void compile() {
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
                    if (processArgument != null) {
                        for (ProcessArgAction a : processArgument) {
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
                    if (processArguments != null) {
                        return processArguments.processArgument(info, MyNDocNodeCustomBuilderContext.this);
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
}
