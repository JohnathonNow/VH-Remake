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
public class TriangularHull extends Hull{
    static final long serialVersionUID = 981289526117837655L;
    public TriangularHull(int x, int y, int size)
    {
        this.x = x;
        this.y = y;
        shape.addPoint(0,-size/2);
        shape.addPoint(-(int) (size*Math.sqrt(3)/2f),0);
        shape.addPoint(0,size/2);
//        for (int i = -width/2+8; i <= width/2-8; i+=16)
//        {
//            locations.add(new Coordinate(i,-height/2,3*Math.PI/2));
//            locations.add(new Coordinate(i,height/2,Math.PI/2));
//        }
        integrity = max_integrity=100;
        locations.add(new Coordinate(-(int) (size*Math.sqrt(3)/4f),(-size/2)*Math.sin(Math.toRadians(135)),Math.toRadians(240),this));
        locations.add(new Coordinate(-(int) (size*Math.sqrt(3)/4f),(size/2)*Math.sin(Math.toRadians(135)),Math.toRadians(120),this));
        mass = size*size/10;
        color = new Color(0,0,255,127);
        outline = new Color(0,0,255,255);
        name = mass+"kg Hull Piece";
        //orientation = (float) ((new Random()).nextInt(360)*Math.PI/180);
        
    }
    @Override
    public Object clone() throws CloneNotSupportedException {

    TriangularHull cloned = new TriangularHull((int)x,(int)y,shape.ypoints[2]*2);
    
    return cloned;
  }
}
