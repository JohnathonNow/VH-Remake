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
public class RectangularHull extends Hull{
    static final long serialVersionUID = -7228373935924493628L;
    public RectangularHull(int x, int y, int width, int height)
    {
        integrity = max_integrity=100;
        this.x = x;
        this.y = y;
        this.ax = (width/2);
        this.ay = 0;
        shape.addPoint(-width/2, -height/2);
        shape.addPoint(width/2, -height/2);
        shape.addPoint(width/2, height/2);
        shape.addPoint(-width/2, height/2);
        constructPoints();
        mass = height*width/10;
        color = new Color(0,0,255,127);
        outline = new Color(0,0,255,255);
        name = mass+"kg Hull Piece";
        //orientation = (float) ((new Random()).nextInt(360)*Math.PI/180);
    }
    @Override
    public final void constructPoints()
    {
        for (int i = shape.xpoints[0]+8; i <= shape.xpoints[1]-8; i+=16)
        {
            locations.add(new Coordinate(i,shape.ypoints[0],3*Math.PI/2,this));
            locations.add(new Coordinate(i,shape.ypoints[2],Math.PI/2,this));
        }
        for (int i = shape.ypoints[0]+8; i <= shape.ypoints[2]-8; i+=16)
        {
            locations.add(new Coordinate(shape.xpoints[0],i,Math.PI,this));
            locations.add(new Coordinate(shape.xpoints[1],i,0,this));
        }
        for (int i = 0; i < locations.size(); i ++)
        {
            Coordinate c = locations.get(i);
            if (c.x==ax&&c.y==ay)
            {
                locations.remove(i);
                break;
            }
        }
    }
    @Override
    public Object clone() throws CloneNotSupportedException {

    RectangularHull cloned = new RectangularHull((int)x,(int)y,shape.xpoints[1]*2,shape.ypoints[2]*2);
    
    return cloned;
  }
}
