/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.node.HItem;
import net.thevpc.halfa.spi.HUtils;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author vpc
 */
public class HStyle implements HItem {

    private HStyleType name;
    private Object value;

    public HStyle(HStyleType name, Object value) {
        this.name = name;
        this.value = value;
    }

    public HStyleType getName() {
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
        final HStyle other = (HStyle) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(NNameFormat.LOWER_KEBAB_CASE.format(name.name()));
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

    @Override
    public TsonElement toTson() {
        return Tson.pair(HUtils.toTson(name), HUtils.toTson(value));
    }
}
