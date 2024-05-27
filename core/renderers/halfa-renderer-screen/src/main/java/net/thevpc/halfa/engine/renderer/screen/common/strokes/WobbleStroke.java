package net.thevpc.halfa.engine.renderer.screen.common.strokes;

import net.thevpc.halfa.spi.util.HUtils;
import net.thevpc.halfa.spi.util.ObjEx;
import net.thevpc.tson.TsonElement;

import java.awt.*;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.Map;

/**
 * http://www.jhlabs.com/java/java2d/strokes/WobbleStroke.java
 */
public class WobbleStroke implements Stroke {
    private float detail = 2;
    private float amplitude = 2;
    private float flatness = 1;

    public static Stroke of(TsonElement e) {
        ObjEx o = ObjEx.of(e);
        double detail = 2;
        double amplitude = 2;
        double flatness = 1;
        for (Map.Entry<String, ObjEx> ke : o.argsMap().entrySet()) {
            switch (HUtils.uid(ke.getKey())) {
                case "details": {
                    detail = ke.getValue().asDouble().orElse(detail);
                    break;
                }
                case "amplitude": {
                    amplitude = ke.getValue().asDouble().orElse(amplitude);
                    break;
                }
                case "flatness": {
                    flatness = ke.getValue().asDouble().orElse(flatness);
                    break;
                }
            }
        }
        return new WobbleStroke(
                (float) detail,
                (float) amplitude,
                (float) flatness
        );
    }
    public WobbleStroke( float detail, float amplitude ,float flatness) {
        this.detail	= detail;
        this.amplitude	= amplitude;
        this.flatness	= flatness;
    }

    public Shape createStrokedShape(Shape shape ) {
        GeneralPath result = new GeneralPath();
        shape = new BasicStroke( 10 ).createStrokedShape( shape );
        PathIterator it = new FlatteningPathIterator( shape.getPathIterator( null ), flatness);
        float points[] = new float[6];
        float moveX = 0, moveY = 0;
        float lastX = 0, lastY = 0;
        float thisX = 0, thisY = 0;
        int type = 0;
        boolean first = false;
        float next = 0;

        while ( !it.isDone() ) {
            type = it.currentSegment( points );
            switch( type ){
                case PathIterator.SEG_MOVETO:
                    moveX = lastX = randomize( points[0] );
                    moveY = lastY = randomize( points[1] );
                    result.moveTo( moveX, moveY );
                    first = true;
                    next = 0;
                    break;

                case PathIterator.SEG_CLOSE:
                    points[0] = moveX;
                    points[1] = moveY;
                    // Fall into....

                case PathIterator.SEG_LINETO:
                    thisX = randomize( points[0] );
                    thisY = randomize( points[1] );
                    float dx = thisX-lastX;
                    float dy = thisY-lastY;
                    float distance = (float)Math.sqrt( dx*dx + dy*dy );
                    if ( distance >= next ) {
                        float r = 1.0f/distance;
                        float angle = (float)Math.atan2( dy, dx );
                        while ( distance >= next ) {
                            float x = lastX + next*dx*r;
                            float y = lastY + next*dy*r;
                            result.lineTo( randomize( x ), randomize( y ) );
                            next += detail;
                        }
                    }
                    next -= distance;
                    first = false;
                    lastX = thisX;
                    lastY = thisY;
                    break;
            }
            it.next();
        }

        return result;
    }

    private float randomize( float x ) {
        return x+(float)Math.random()*amplitude*2-1;
    }

}