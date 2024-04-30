/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.thevpc.halfa.api.model;

import java.util.Objects;

/**
 *
 * @author vpc
 */
public class HStyle {

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
    
}
