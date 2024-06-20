package net.thevpc.halfa.api.model.elem2d;

import java.awt.Paint;
import java.util.Objects;

public class Shadow{

    private HPoint2D translation;
    private Paint color;
    private HPoint2D shear;

    public void setColor(Paint color) {
        this.color = color;
    }

    public Paint getColor() {
        return color;
    }

    public HPoint2D getTranslation() {
        return translation;
    }

    public void setTranslation(HPoint2D translation) {
        this.translation = translation;
    }

    public HPoint2D getShear() {return shear;}

    public void setShear (HPoint2D shear) {this.shear = shear;}

    @Override
    public String toString() {
        return "Shadow{" + "translation=" + translation + ", color=" + color +", shear=" + shear + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.translation);
        hash = 29 * hash + Objects.hashCode(this.color);
        hash = 29 * hash + Objects.hashCode(this.shear);
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
        final Shadow other = (Shadow) obj;
        if (!Objects.equals(this.translation, other.translation)) {
            return false;
        }
        if (!Objects.equals(this.color, other.color)) {
            return false;
        }
        return Objects.equals(this.shear, other.shear);
    }

    

}
