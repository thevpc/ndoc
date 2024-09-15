package net.thevpc.halfa.extension.shapes2d.arrow;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.model.node.HNode;
import net.thevpc.halfa.api.model.node.HNodeType;
import net.thevpc.halfa.api.style.HProp;
import net.thevpc.halfa.api.style.HPropName;
import net.thevpc.halfa.spi.base.format.ToTsonHelper;
import net.thevpc.halfa.spi.base.parser.HNodeParserBase;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonPair;

/**
 *
 */
public class HArrowImpl extends HNodeParserBase {

    public HArrowImpl() {
        super(false, HNodeType.ARROW);
    }

    @Override
    protected boolean processArgument(String id, TsonElement tsonElement, HNode node, TsonElement currentArg, TsonElement[] allArguments, int currentArgIndex, HDocumentFactory f, HNodeFactoryParseContext context) {
        switch (currentArg.type()) {
            case PAIR: {
                if (currentArg.isSimplePair()) {
                    TsonPair p = currentArg.toPair();
                    switch (HUtils.uid(p.key().stringValue())) {
                        case "width": {
                            node.setProperty(HPropName.WIDTH, p.value());
                            return true;
                        }
                        case "height": {
                            node.setProperty(HPropName.HEIGHT, p.value());
                            return true;
                        }
                        case "points": {
                            node.setProperty(HProp.ofObject("points", p.value()));
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
        HProp width = item.getProperty(HPropName.WIDTH).orNull();
        HProp height = item.getProperty(HPropName.HEIGHT).orNull();
        HProp points = item.getProperty(HPropName.POINTS).orNull();

        return ToTsonHelper.of(
                item,
                engine()
        ).addChildren(
                width == null ? null : Tson.ofPair("width", HUtils.toTson(width.getValue())),
                height == null ? null : Tson.ofPair("height", HUtils.toTson(height.getValue())),
                points == null ? null : Tson.ofPair("points", HUtils.toTson(points.getValue()))
        ).build();
    }

}
