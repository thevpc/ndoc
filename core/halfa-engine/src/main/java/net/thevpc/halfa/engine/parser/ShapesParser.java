package net.thevpc.halfa.engine.parser;

import net.thevpc.halfa.HDocumentFactory;
import net.thevpc.halfa.api.node.HLine;
import net.thevpc.halfa.api.node.HNode;
import net.thevpc.halfa.api.node.HPolygon;
import net.thevpc.halfa.api.node.HPolyline;
import net.thevpc.halfa.api.style.HStyle;
import net.thevpc.halfa.spi.nodes.HNodeFactoryParseContext;
import net.thevpc.nuts.util.NOptional;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonElementHeader;
import net.thevpc.tson.TsonObject;

import java.awt.geom.Point2D;

public class ShapesParser {
    public static NOptional<HNode> parseShape(String type, TsonObject ff, HDocumentFactory f, HNodeFactoryParseContext context) {
        HNode p = null;
        switch (type) {
            case "rectangle": {
                p = f.rectangle();
                break;
            }
            case "square": {
                p = f.rectangle();
                break;
            }
            case "circle": {
                p = f.circle();
                break;
            }
            case "ellipse":
            case "oval": {
                p = f.ellipse();
                break;
            }
            case "glue": {
                p = f.glue();
                break;
            }
            case "hglue":
            case "h-glue":
            case "glue-h": {
                p = f.hglue();
                break;
            }
            case "vglue":
            case "v-glue":
            case "glue-v": {
                p = f.vglue();
                break;
            }
            case "polygon": {
                p = f.polygon();
                break;
            }
            case "polyline": {
                p = f.polyline();
                break;
            }
            case "line": {
                p = f.line();
                break;
            }
            default:{
                return NOptional.ofNamedError("invalid type: " + type);
            }
        }
        switch (ff.type()) {
            case FUNCTION: {
                for (TsonElement e : ff.toFunction().all()) {
                    if(!processItem(p, e,f, context)){
                        return NOptional.ofNamedError("invalid " + e+" for ");
                    }
                }
            }
            case OBJECT: {
                TsonObject o = ff.toObject();
                TsonElementHeader h = o.getHeader();
                boolean some=false;
                for (TsonElement e : h.all()) {
                    if(!processItem(p, e,f, context)){
                        return NOptional.ofNamedError("invalid " + e+" for ");
                    }
                    some=true;
                }
                if(!some) {
                    for (TsonElement e : ff.toFunction().all()) {
                        if(!processItem(p, e,f, context)){
                            return NOptional.ofNamedError("invalid " + e+" for ");
                        }
                    }
                }
                break;
            }
        }
        return NOptional.of(p);
    }

    private static boolean processItem(HNode p,TsonElement e, HDocumentFactory f, HNodeFactoryParseContext context){
        NOptional<HStyle> u = HStyleParser.parseStyle(e, f, context);
        if (u.isPresent()) {
            p.set(u.get());
            return true;
        } else {
            switch (p.type()){
                case POLYGON:{
                    switch (e.type()) {
                        case PAIR: {
                            TsonElementParseHelper h = new TsonElementParseHelper(e.toPair().getKey());
                            NOptional<String> uu = h.asStringOrName();
                            if (uu.isPresent()) {
                                String uid = HParseHelper.uid(uu.get());
                                switch (uid){
                                    case "point":{
                                        NOptional<Point2D.Double> p2d = new TsonElementParseHelper(e.toPair().getValue()).asPoint2D();
                                        if(p2d.isPresent()){
                                            ((HPolygon)p).add(p2d.get());
                                        }else{
                                            return false;
                                        }
                                        break;
                                    }
                                    default:{
                                        return false;
                                    }
                                }
                            }else{
                                return false;
                            }
                            break;
                        }
                        case UPLET:{
                            NOptional<Point2D.Double> p2d = new TsonElementParseHelper(e.toPair().getValue()).asPoint2D();
                            if(p2d.isPresent()){
                                ((HPolygon)p).add(p2d.get());
                            }else{
                                return false;
                            }
                            break;
                        }
                        default:{
                            return false;
                        }
                    }
                    break;
                }
                case POLYLINE:{
                    switch (e.type()) {
                        case PAIR: {
                            TsonElementParseHelper h = new TsonElementParseHelper(e.toPair().getKey());
                            NOptional<String> uu = h.asStringOrName();
                            if (uu.isPresent()) {
                                String uid = HParseHelper.uid(uu.get());
                                switch (uid){
                                    case "point":{
                                        NOptional<Point2D.Double> p2d = new TsonElementParseHelper(e.toPair().getValue()).asPoint2D();
                                        if(p2d.isPresent()){
                                            ((HPolyline)p).add(p2d.get());
                                        }else{
                                            return false;
                                        }
                                        break;
                                    }
                                    default:{
                                        return false;
                                    }
                                }
                            }else{
                                return false;
                            }
                            break;
                        }
                        case UPLET:{
                            NOptional<Point2D.Double> p2d = new TsonElementParseHelper(e.toPair().getValue()).asPoint2D();
                            if(p2d.isPresent()){
                                ((HPolyline)p).add(p2d.get());
                            }else{
                                return false;
                            }
                            break;
                        }
                        default:{
                            return false;
                        }
                    }
                    break;
                }
                case LINE:{
                    switch (e.type()) {
                        case PAIR: {
                            TsonElementParseHelper h = new TsonElementParseHelper(e.toPair().getKey());
                            NOptional<String> uu = h.asStringOrName();
                            if (uu.isPresent()) {
                                String uid = HParseHelper.uid(uu.get());
                                switch (uid){
                                    case "from":{
                                        NOptional<Point2D.Double> p2d = new TsonElementParseHelper(e.toPair().getValue()).asPoint2D();
                                        if(p2d.isPresent()){
                                            ((HLine)p).setFrom(p2d.get());
                                        }else{
                                            return false;
                                        }
                                        break;
                                    }
                                    case "to":{
                                        NOptional<Point2D.Double> p2d = new TsonElementParseHelper(e.toPair().getValue()).asPoint2D();
                                        if(p2d.isPresent()){
                                            ((HLine)p).setTo(p2d.get());
                                        }else{
                                            return false;
                                        }
                                        break;
                                    }
                                    case "point":{
                                        NOptional<Point2D.Double> p2d = new TsonElementParseHelper(e.toPair().getValue()).asPoint2D();
                                        if(p2d.isPresent()){
                                            addLinePoint(((HLine)p),p2d.get());
                                        }else{
                                            return false;
                                        }
                                        break;
                                    }
                                    default:{
                                        return false;
                                    }
                                }
                            }else{
                                return false;
                            }
                            break;
                        }
                        case UPLET:{
                            NOptional<Point2D.Double> p2d = new TsonElementParseHelper(e.toPair().getValue()).asPoint2D();
                            if(p2d.isPresent()){
                                addLinePoint(((HLine)p),p2d.get());
                            }else{
                                return false;
                            }
                            break;
                        }
                        default:{
                            return false;
                        }
                    }
                    break;
                }
            }
            return false;
        }
    }

    private static boolean addLinePoint(HLine line,Point2D.Double point){
        if(point!=null) {
            if (line.from() == null) {
                line.setFrom(point);
                return true;
            }else if(line.to() == null) {
                line.setTo(point);
                return true;
            }
        }
        return false;
    }
}
