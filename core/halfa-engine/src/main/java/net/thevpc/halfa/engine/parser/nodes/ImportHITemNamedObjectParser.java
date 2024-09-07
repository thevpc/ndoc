package net.thevpc.halfa.engine.parser.nodes;

import net.thevpc.halfa.api.model.node.HItemList;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.spi.eval.ObjEx;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.NExecCmd;
import net.thevpc.nuts.NSession;
import net.thevpc.nuts.io.NIO;
import net.thevpc.nuts.io.NPath;
import net.thevpc.nuts.util.NMsg;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.nuts.util.NRef;
import net.thevpc.tson.TsonElement;

import java.util.*;

public class ImportHITemNamedObjectParser extends AbstractHITemNamedObjectParser {
    public ImportHITemNamedObjectParser() {
        super("import", "include");
    }

    @Override
    public boolean accept(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        return true;
    }

    @Override
    public NOptional<HItem> parseItem(String id, TsonElement tsonElement, HNodeFactoryParseContext context) {
        switch (tsonElement.type()) {
            case FUNCTION: {
                List<TsonElement> u = tsonElement.toFunction().all();
                if (u.isEmpty()) {
                    context.messages().addError(NMsg.ofC("missing path argument : %s", tsonElement), context.source());
                    return NOptional.ofError(s -> NMsg.ofC("missing path argument : %s", tsonElement));
                }
                HNode putInto = context.node();
                List<HItem> loaded = new ArrayList<>();
                NRef<Boolean> someLoaded = NRef.of(false);
                for (TsonElement ee : u) {
                    ObjEx t = ObjEx.of(ee);
                    NOptional<String[]> p = t.asStringArrayOrString();
                    if (p.isPresent()) {
                        for (String sp : p.get()) {
                            NOptional<HItem> a = importOne(sp, loaded, putInto, context);
                            if (a != null) {
                                return a;
                            } else {
                                someLoaded.set(true);
                            }
                        }
                    }
                }
                if (someLoaded.get()) {
                    return NOptional.of(new HItemList().addAll(loaded));
                }
                break;
            }
        }
        context.messages().addError(NMsg.ofC("missing include elements from %s", tsonElement), context.source());
        return NOptional.ofNamedEmpty("include elements");
    }

    public NOptional<HItem> importOne(String sp, List<HItem> loaded, HNode putInto, HNodeFactoryParseContext context) {
        if (sp.startsWith("github://")) {
            return importOneGitHub(sp, loaded, putInto, context);
        } else {
            return importOneLocalFile(context.resolvePath(sp), loaded, putInto, context);
        }
    }

    public NOptional<HItem> importOneGitHub(String sp, List<HItem> loaded, HNode putInto, HNodeFactoryParseContext context) {
        NSession session = context.session();
        NPath userConfHome = NPath.ofUserHome(session).resolve(".config/halfa/github");
        String part0 = sp.substring("github://".length());
        int i1 = part0.indexOf('/');
        if (i1 <= 0) {
            context.messages().addError(NMsg.ofC("invalid include. error loading : %s", sp), context.source());
            return NOptional.ofError(s -> NMsg.ofC("invalid include. error loading : %s", sp));
        }
        int i2 = part0.indexOf('/', i1+1);
        if (i2 <= 0) {
            context.messages().addError(NMsg.ofC("invalid include. error loading : %s", sp), context.source());
            return NOptional.ofError(s -> NMsg.ofC("invalid include. error loading : %s", sp));
        }
        String user = part0.substring(0, i1);
        String repoName = part0.substring(i1 + 1, i2);
        String path = part0.substring(i2 + 1);
        NPath localRepo = userConfHome.resolve(user + "/" + repoName + "/" + path);userConfHome.resolve(user).mkdirs()
        if (localRepo.isDirectory()) {
            NExecCmd.of(session)
                    .addCommand("git", "pull")
                    .setDirectory(userConfHome.resolve(user + "/" + repoName))
                    .failFast()
                    .run();
        } else {
            NExecCmd.of(session)
                    .addCommand("git", "clone", "git@github.com:" + user + "/" + repoName + ".git")
                    .setDirectory(userConfHome.resolve(user))
                    .failFast()
                    .run();
        }
        return importOneLocalFile(localRepo, loaded, putInto, context);
    }

    public NOptional<HItem> importOneLocalFile(NPath spp, List<HItem> loaded, HNode putInto, HNodeFactoryParseContext context) {
        context.document().resources().add(spp);
        List<NPath> list = spp.walkGlob().toList();
        list.sort(Comparator.comparing(NPath::toString));
        for (NPath nPath : list) {
            if (nPath.isRegularFile()) {
                NOptional<HItem> se = context.engine().loadNode(putInto, nPath, context.document(), context.messages());
                if (se.isPresent()) {
                    loaded.add(se.get());
                } else {
                    context.messages().addError(NMsg.ofC("invalid include. error loading : %s", nPath), context.source());
                    return NOptional.ofError(s -> NMsg.ofC("invalid include. error loading : %s", nPath));
                }
            }
        }
        return null;
    }
}

