/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.style;

import net.thevpc.halfa.api.model.elem2d.Double2;
import net.thevpc.halfa.api.model.elem2d.HPoint2D;
import net.thevpc.halfa.api.model.elem3d.HPoint3D;
import net.thevpc.halfa.api.model.node.HItem;
import net.thevpc.halfa.api.util.HUtils;
import net.thevpc.nuts.util.NNameFormat;
import net.thevpc.tson.ToTson;
import net.thevpc.tson.Tson;
import net.thevpc.tson.TsonElement;
import net.thevpc.tson.TsonElementBase;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author vpc
 */
public class HProp implements HItem, ToTson {

    private String name;
    private TsonElement value;

    public static HProp ofDouble(String name, Double value) {
        return new HProp(name, Tson.of(value));
    }

    public static HProp ofInt(String name, Integer value) {
        return new HProp(name, Tson.of(value));
    }

    public static HProp ofBoolean(String name, Boolean value) {
        return new HProp(name, Tson.of(value));
    }

    public static HProp ofString(String name, String value) {
        return new HProp(name, Tson.of(value));
    }

    public static HProp ofObject(String name, TsonElement value) {
        return new HProp(name, value);
    }

    public static HProp ofStringArray(String name, String[] value) {
        return new HProp(name, Tson.of(value));
    }

    public static HProp ofDouble2(String name, double x, double y) {
        return new HProp(name, Tson.ofUplet(Tson.of(x), Tson.of(y)).build());
    }

    public static HProp ofHPoint2D(String name, double x, double y) {
        return new HProp(name, Tson.ofUplet(Tson.of(x), Tson.of(y)).build());
    }

    public static HProp ofDouble2(String name, Double2 d) {
        return new HProp(name, d==null?null:d.toTson());
    }

    public static HProp ofHPoint2D(String name, HPoint2D d) {
        return new HProp(name, d==null?null:d.toTson());
    }

    public static HProp ofHPoint3D(String name, HPoint3D d) {
        return new HProp(name, d==null?null:d.toTson());
    }

    public static HProp ofDouble2Array(String name, Double2... d) {
        return new HProp(name, Tson.ofArray(Arrays.stream(d).map(x->x.toTson()).toArray(TsonElementBase[]::new)).build());
    }
    public static HProp ofDoubleArray (String name, double[] d) {
        return new HProp(name, Tson.of(d));
    }

    public static HProp ofHPoint2DArray(String name, HPoint2D... d) {
        return new HProp(name, Tson.ofArray(Arrays.stream(d).map(x->x.toTson()).toArray(TsonElementBase[]::new)).build());
    }

    public static HProp ofHPoint3DArray(String name, HPoint3D... d) {
        return new HProp(name, Tson.ofArray(Arrays.stream(d).map(x->x.toTson()).toArray(TsonElementBase[]::new)).build());
    }

    public HProp(String name, TsonElement value) {
        this.name = name;
        this.value = value;
    }
    public HProp(String name, ToTson value) {
        this.name = name;
        this.value = value==null?null:value.toTson();
    }


    public String getName() {
        return name;
    }

    public TsonElement getValue() {
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
        StringBuilder sb = new StringBuilder();
        sb.append(NNameFormat.LOWER_KEBAB_CASE.format(name));
        sb.append(":");
        Object vv = value;
        if (vv != null) {
            if (vv.getClass().isArray()) {
                Class<?> ct = vv.getClass().getComponentType();
                if (ct.isPrimitive()) {
                    switch (ct.getName()) {
                        case "double": {
                            sb.append(Arrays.toString((double[]) vv));
                            break;
                        }
                        case "int": {
                            sb.append(Arrays.toString((int[]) vv));
                            break;
                        }
                        case "long": {
                            sb.append(Arrays.toString((long[]) vv));
                            break;
                        }
                        case "boolean": {
                            sb.append(Arrays.toString((boolean[]) vv));
                            break;
                        }
                        default: {
                            sb.append(vv);
                        }
                    }
                } else {
                    sb.append(Arrays.asList((Object[]) vv));
                }
            } else {
                sb.append(vv);
            }
        } else {
            sb.append(vv);
        }
        return sb.toString();
    }

    public TsonElement toTson() {
        return Tson.ofPair(net.thevpc.halfa.api.util.HUtils.toTson(name), HUtils.toTson(value));
    }
}
