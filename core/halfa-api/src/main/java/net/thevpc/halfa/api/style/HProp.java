/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.model.Double2;
import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author vpc
 */
public class HProp implements HItem, ToTson {

    private String name;
    private Object value;

    public static HProp ofDouble(String name, Double value) {
        return new HProp(name,value);
    }

    public static HProp ofInt(String name, Integer value) {
        return new HProp(name,value);
    }

    public static HProp ofBoolean(String name, Boolean value) {
        return new HProp(name,value);
    }

    public static HProp ofString(String name, String value) {
        return new HProp(name,value);
    }

    public static HProp ofObject(String name, Object value) {
        return new HProp(name,value);
    }

    public static HProp ofStringArray(String name, String[] value) {
        return new HProp(name,value);
    }

    public static HProp ofDouble2(String name, double x, double y) {
        return new HProp(name,new Double2(x,y));
    }

    public static HProp ofDouble2(String name, Double2 d) {
        return new HProp(name,d);
    }

    public static HProp ofDouble2Array(String name, Double2 ... d) {
        return new HProp(name,d);
    }

    public HProp(String name, Object value) {
        this.name = name;
        this.value = value;
    }


    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HProp other = (HProp) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(NNameFormat.LOWER_KEBAB_CASE.format(name));
        sb.append(":");
        Object vv = value;
        if(vv!=null){
            if(vv.getClass().isArray()){
                Class<?> ct = vv.getClass().getComponentType();
                if(ct.isPrimitive()){
                    switch (ct.getName()){
                        case "double":{
                            sb.append(Arrays.toString((double[])vv));
                            break;
                        }
                        case "int":{
                            sb.append(Arrays.toString((int[])vv));
                            break;
                        }
                        case "long":{
                            sb.append(Arrays.toString((long[])vv));
                            break;
                        }
                        case "boolean":{
                            sb.append(Arrays.toString((boolean[])vv));
                            break;
                        }
                        default:{
                            sb.append(vv);
                        }
                    }
                }else{
                    sb.append(Arrays.asList((Object[])vv));
                }
            }else{
                sb.append(vv);
            }
        }else{
            sb.append(vv);
        }
        return sb.toString();
    }

    public TsonElement toTson() {
        return Tson.ofPair(HUtils.toTson(name), HUtils.toTson(value));
    }
}
