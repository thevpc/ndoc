/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.ndoc.elem.base.image;

import net.thevpc.ndoc.api.model.node.HNode;
import net.thevpc.ndoc.api.model.node.HNodeType;
import net.thevpc.ndoc.api.style.HPropName;
import net.thevpc.ndoc.api.util.HUtils;
import net.thevpc.ndoc.spi.base.parser.NDocNodeParserBase;
import net.thevpc.ndoc.spi.base.format.ToTsonHelper;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

import java.util.HashSet;
import java.util.Set;


/**
 * @author vpc
 */
public class NDocImageParser extends NDocNodeParserBase {

    public NDocImageParser() {
        super(false, HNodeType.IMAGE);
    }

    @Override
    protected boolean processArguments(ParseArgumentInfo info) {
        Set<Integer> processed = new HashSet<>();
        boolean found = false;
        for (int i = 0; i < info.arguments.length; i++) {
            TsonElement currentArg = info.arguments[i];
            switch (currentArg.type()) {
                case STRING: {
                    info.node.setProperty(HPropName.VALUE, info.context.asPathRef(currentArg));
                    processed.add(i);
                    found = true;
                    break;
                }
                case PAIR: {
                    if (currentArg.isSimplePair()) {
                        TsonPair p = currentArg.toPair();
                        String sid = net.thevpc.ndoc.api.util.HUtils.uid(p.key().stringValue());
                        switch (sid) {
                            case HPropName.VALUE:
                            case HPropName.FILE: {
                                info.node.setProperty(sid, info.context.asPathRef(p.value()));
                                processed.add(i);
                                found = true;
                                break;
                            }
                            case "content":
                            case "src": {
                                info.node.setProperty(HPropName.VALUE, info.context.asPathRef(p.value()));
                                processed.add(i);
                                found = true;
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        if (!found) {
            for (int i = 0; i < info.arguments.length; i++) {
                if (!processed.contains(i)) {
                    TsonElement currentArg = info.arguments[i];
                    switch (currentArg.type()) {
                        case NAME: {
                            info.node.setProperty(HPropName.VALUE, info.context.asPathRef(currentArg));
                            processed.add(i);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < info.arguments.length; i++) {
            if (!processed.contains(i)) {
                info.currentArg = info.arguments[i];
                info.currentArgIndex = i;
                if (!processArgument(info)) {
                    return processArgumentAsCommonStyleProperty(info);
                }
            }
        }
        return true;
    }

    @Override
    protected boolean processArgument(ParseArgumentInfo info) {
        switch (info.currentArg.type()) {
            case STRING:
            case NAME: {
                break;
            }
            case PAIR: {
                if (info.currentArg.isSimplePair()) {
                    TsonPair p = info.currentArg.toPair();
                    String sid = HUtils.uid(p.key().stringValue());
                    switch (sid) {
                        case HPropName.TRANSPARENT_COLOR: {
                            info.node.setProperty(sid, p.value());
                            return true;
                        }
                        case HPropName.VALUE:
                        case HPropName.FILE:
                        case "content":
                        case "src": {
                            return true;
                        }
                    }
                }
                break;
            }
        }
        return super.processArgument(info);
    }


    @Override
    public TsonElement toTson(HNode item) {
        return ToTsonHelper
                .of(item, engine())
                .inlineStringProp(HPropName.VALUE)
                .build();
    }
}
