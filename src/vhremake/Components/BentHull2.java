/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vhremake.Components;

import java.awt.Color;
import vhremake.Coordinate;
/**
 *
 * @author John
 */
public class BentHull2 extends Hull{
    public double angle;
    static final long serialVersionUID = -5357716973078613272L;
    public BentHull2(int x, int y, double angle)
    {
        this.angle = angle;
        integrity = max_integrity=100;
        this.x = x;
        this.y = y;
        this.ax = 16;
        this.ay = 0;
        shape.addPoint(0, 8);
        double xx = 0;
        double yy = 8;
        xx-=Math.cos(angle)*16;
        yy+=Math.sin(angle)*16;
        shape.addPoint((int)xx, (int)yy);
        xx-=Math.cos(angle-Math.PI/2)*16;
        yy+=Math.sin(angle-Math.PI/2)*16;
        shape.addPoint((int)xx, (int)yy);
        xx-=Math.cos(angle-Math.PI)*16;
        yy+=Math.sin(angle-Math.PI)*16;
        shape.addPoint((int)xx, (int)yy);
        shape.addPoint(0, -8);
        shape.addPoint(16, -8);
        shape.addPoint(16, 8);
        constructPoints();
        mass = 16*32/10;
        color = new Color(0,0,255,127);
        outline = new Color(0,0,255,255);
        name = mass+"kg Hull Piece";
        //orientation = (float) ((new Random()).nextInt(360)*Math.PI/180);
    }
    @Override
    public final void constructPoints()
    {
        locations.add(new Coordinate(8,-8,-Math.PI/2,this));
        locations.add(new Coordinate(-(int)(Math.cos(angle)*8),8+(int)(Math.sin(angle)*8),Math.PI/2-angle,this));
        
        locations.add(new Coordinate(-(int)(Math.cos(angle)*16+Math.cos(angle-Math.PI/2)*8),8+(int)(Math.sin(angle)*16+Math.sin(angle-Math.PI/2)*8),Math.PI/2-angle+Math.PI/2,this));
        
        locations.add(new Coordinate(-(int)(Math.cos(angle)*8+Math.cos(angle-Math.PI/2)*16),8+(int)(Math.sin(angle)*8+Math.sin(angle-Math.PI/2)*16),Math.PI/2-angle+Math.PI,this));
        
        locations.add(new Coordinate(shape.xpoints[3]/2,(shape.ypoints[3]-8)/2,(Math.PI-angle)/2+Math.PI,this));
        locations.add(new Coordinate(8,8,Math.PI/2,this));
    }
    @Override
    public Object clone() throws CloneNotSupportedException {

    BentHull2 cloned = new BentHull2((int)x,(int)y,angle);
    
    return cloned;
  }
}
