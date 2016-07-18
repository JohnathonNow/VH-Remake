/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vhremake;

import java.awt.Graphics2D;
import java.io.Serializable;
import com.jogamp.opengl.GL2;
import vhremake.Components.Component;

/**
 *
 * @author John
 */
public class Coordinate implements Serializable, Comparable{
    static final long serialVersionUID = 1L;
    public double x, y, angle, r, a;
    public boolean occupied = false;
    protected boolean isSelected = false;
    protected Component master;
    private static int count = 0;
    private final int myid = count++;
    public Coordinate(double x, double y, double angle, Component master)
    {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.r = Math.sqrt(x*x+y*y);
        this.a = Math.atan2(y, x);
        this.master = master;
    }
    public double getRX(double otherAngle)
    {
        return r*Math.cos(otherAngle+a);
    }
    public double getRY(double otherAngle)
    {
        return r*Math.sin(otherAngle+a);
    }
    public void draw(Graphics2D g)
    {
        g.drawArc((int)x-1,(int)y-1, 2, 2, 0, 360);
    }
    public void render(GL2 g)
    {
        if (isSelected)
        {
            bigRender(g);
        }
        else
        {
            g.glBegin( GL2.GL_LINE_LOOP);
                g.glColor3f(0,1,0);
                g.glVertex2d(x,y-1);
                g.glVertex2d(x+1,y);
                g.glVertex2d(x,y+1);
                g.glVertex2d(x-1,y);
            g.glEnd();
        }
    }
    public void bigRender(GL2 g)
    {
        g.glBegin( GL2.GL_LINE_LOOP);
            g.glColor3f(1.0f,0.5f,0.5f);
            g.glVertex2d(x,y-4);
            g.glVertex2d(x+4,y);
            g.glVertex2d(x,y+4);
            g.glVertex2d(x-4,y);
        g.glEnd();
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Coordinate)
        {
            Coordinate c = (Coordinate)o;
//            return c.myid-myid;
            if (master==null)
            {
                return 0;
            }
            if (master.MASTER==null)
            {
                return 0;
            }
            return (master.MASTER.angleTo(c.x, c.y)>master.MASTER.angleTo(x, y))?1:-1;     
        }
        return 0;
    }
}