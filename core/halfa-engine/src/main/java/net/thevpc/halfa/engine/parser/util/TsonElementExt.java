package net.thevpc.halfa.engine.parser.util;

import net.thevpc.tson.*;

import java.util.ArrayList;
import java.util.List;

public class TsonElementExt {
    private TsonElement e;
    private String name;
    private List<TsonElement> args=new ArrayList<>();
    private List<TsonElement> children=new ArrayList<>();

    public TsonElementExt(TsonElement e) {
        this.e = e;
        switch (e.type()){
            case FUNCTION:{
                TsonFunction item = e.toFunction();
                name= HParseHelper.uid(item.name());;
                args.addAll(item.all());
                break;
            }
            case OBJECT:{
                TsonObject item = e.toObject();
                TsonElementHeader header = item.getHeader();
                if(header!=null) {
                    name = header.name();
                    args.addAll(header.all());
                }
                children.addAll(item.all());
                break;
            }
            case ARRAY:{
                TsonArray item = e.toArray();
                TsonElementHeader header = item.getHeader();
                if(header!=null) {
                    name = header.name();
                    args.addAll(header.all());
                }
                children.addAll(item.all());
                break;
            }
        }
    }

    public TsonElement elem() {
        return e;
    }

    public String name() {
        return name;
    }

    public List<TsonElement> args() {
        return args;
    }

    public List<TsonElement> children() {
        return children;
    }
}
