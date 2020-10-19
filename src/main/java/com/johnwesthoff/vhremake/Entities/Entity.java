/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.johnwesthoff.vhremake.Entities;

import com.jogamp.opengl.GL2;
import java.awt.Graphics2D;
import java.util.Random;

/**
 *
 * @author John
 */
public class Entity {
    public double x, y, xspeed, yspeed,angularv,orientation,depth;
    public enum TYPE{BULLET,EFFECT,DANGER};
    public TYPE type;
    public double hp = 10;
    public static Random random = new Random();
    public boolean move()
    {
        x+=xspeed;
        y+=yspeed;
        orientation+=angularv;
        xspeed*=0.995;
        yspeed*=0.995;
        angularv*=0.995;
        return hp>=0;
    }
    public void draw(Graphics2D g, int vx, int vy)
    {
        
    }
    public void render(GL2 g, double vX, double vY)
    {
        
    }
    /**
     * Finds the distance to (tX,tY)
     * 
     * @param tX - the x coordinate to test
     * @param tY - the y coordinate to test
     * @return distance to (tX,tY)
     */
    public double disTo(double tX, double tY)
    {
        return Math.sqrt((x-tX)*(x-tX)+(y-tY)*(y-tY));
    }
}
