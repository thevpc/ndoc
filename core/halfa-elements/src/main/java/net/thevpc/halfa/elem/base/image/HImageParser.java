/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.elem.base.image;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.base.format.ToTsonHelper;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

import java.util.HashSet;
import java.util.Set;


/**
 * @author vpc
 */
public class HImageParser extends HNodeParserBase {

    public HImageParser() {
        super(false, HNodeType.IMAGE);
    }

    @Override
    protected boolean processArguments(String id, TsonElement tsonElement, HNode node, TsonElement[] arguments, HDocumentFactory f, HNodeFactoryParseContext context) {
        Set<Integer> processed = new HashSet<>();
        boolean found = false;
        for (int i = 0; i < arguments.length; i++) {
            TsonElement currentArg = arguments[i];
            switch (currentArg.type()) {
                case STRING: {
                    node.setProperty(HPropName.VALUE, context.asPathRef(currentArg));
                    processed.add(i);
                    found = true;
                    break;
                }
                case PAIR: {
                    if (currentArg.isSimplePair()) {
                        TsonPair p = currentArg.toPair();
                        String sid = HUtils.uid(p.key().stringValue());
                        switch (sid) {
                            case HPropName.VALUE:
                            case HPropName.FILE: {
                                node.setProperty(sid, context.asPathRef(p.value()));
                                processed.add(i);
                                found = true;
                                break;
                            }
                            case "content":
                            case "src": {
                                node.setProperty(HPropName.VALUE, context.asPathRef(p.value()));
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
            for (int i = 0; i < arguments.length; i++) {
                if (!processed.contains(i)) {
                    TsonElement currentArg = arguments[i];
                    switch (currentArg.type()) {
                        case NAME: {
                            node.setProperty(HPropName.VALUE, context.asPathRef(currentArg));
                            processed.add(i);
                            found = true;
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < arguments.length; i++) {
            if (!processed.contains(i)) {
                TsonElement currentArg = arguments[i];
                if (!processArgument(id, tsonElement, node, currentArg, arguments, i, f, context)) {
                    return processArgumentAsCommonStyleProperty(id, tsonElement, node, currentArg, arguments, i, f, context);
                }
            }
        }
        return true;
    }

    @Override
    protected boolean processArgument(String id, TsonElement tsonElement, HNode node, TsonElement currentArg, TsonElement[] allArguments, int currentArgIndex, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (currentArg.type()) {
            case STRING:
            case NAME: {
                break;
            }
            case PAIR: {
                if (currentArg.isSimplePair()) {
                    TsonPair p = currentArg.toPair();
                    String sid = HUtils.uid(p.key().stringValue());
                    switch (sid) {
                        case HPropName.TRANSPARENT_COLOR: {
                            node.setProperty(sid, p.value());
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
        return super.processArgument(id, tsonElement, node, currentArg, allArguments, currentArgIndex, f, context);
    }


    @Override
    public TsonElement toTson(HNode item) {
        return ToTsonHelper
                .of(item, engine())
                .inlineStringProp(HPropName.VALUE)
                .build();
    }
}
