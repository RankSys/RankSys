/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ranksys.metrics.curves;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.ranksys.core.util.tuples.Tuple2oo;

/**
 * Represents a curve in dimension 2.
 * @author Javier Sanz-Cruzado Puig (javier.sanz-cruzado@uam.es)
 */
public class Curve
{
    /**
     * A list of points.
     */
    protected List<Tuple2oo<Double>> points = new ArrayList<>();
    
    /**
     * Adds a point to the curve.
     * @param x the first coordinate of the new point.
     * @param y the second coordinate of the new point.
     */
    public void addPoint(double x, double y)
    {
        points.add(new Tuple2oo<>(x,y));
    }
    
    /**
     * Adds a point to the curve.
     * @param point the new point.
     */
    public void addPoint(Tuple2oo<Double> point)
    {
        points.add(new Tuple2oo<>(point));
    }
    
    /**
     * Adds a point to the curve.
     * @param pos the position in which to add the point.
     * @param x the first coordinate of the new point.
     * @param y the second coordinate of the new point.
     */
    public void addPoint(int pos, double x, double y)
    {
        points.add(pos, new Tuple2oo<>(x,y));
    }
    
    /**
     * Adds a point to the curve.
     * @param pos the position in which to add the point.
     * @param point the new point.
     */
    public void addPoint(int pos, Tuple2oo<Double> point)
    {
        points.add(pos, new Tuple2oo<>(point));
    }
    
    /**
     * Replaces a point in the curve.
     * @param pos the position of the element to replace.
     * @param x the first coordinate of the new point.
     * @param y the second coordinate of the new point.
     * @return the old value if OK, null otherwise.
     */
    public Tuple2oo<Double> replacePoint(int pos, double x, double y)
    {
        Tuple2oo<Double> point = this.getPoint(pos);
        if(point != null)
            points.set(pos, new Tuple2oo<>(x,y));
        return point;
    }
    
    /**
     * Replaces a point in the curve.
     * @param pos the position of the element to replace.
     * @param point the new point.
     * @return the old value if OK, null otherwise.
     */
    public Tuple2oo<Double> updatePoint(int pos, Tuple2oo<Double> point)
    {
        Tuple2oo<Double> p = this.getPoint(pos);
        if(p != null)
            points.set(pos, new Tuple2oo<>(point));
        return p;    
    }
    
    /**
     * Obtains a point in the curve.
     * @param pos the position of the element.
     * @return the element if exists, null otherwise.
     */
    public Tuple2oo<Double> getPoint(int pos)
    {
        return (pos >= 0 && pos < points.size()) ? points.get(pos) : null;
    }
    
    /**
     * Appends a curve
     * @param curve the curve.
     */
    public void append(Curve curve)
    {
        curve.getCurve().forEach(point -> this.addPoint(point));
    }
    
    /**
     * Obtains the curve.
     * @return a stream with the points in the curve.
     */
    public Stream<Tuple2oo<Double>> getCurve()
    {
        return this.points.stream();
    }
    
}
