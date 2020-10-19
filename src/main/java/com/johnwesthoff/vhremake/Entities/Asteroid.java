/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.johnwesthoff.vhremake.Entities;

import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import com.jogamp.opengl.GL2;
/**
 *
 * @author John
 */
public class Asteroid extends Entity{
    Polygon shape;
    public double radius = 5;
    public Asteroid(double x, double y, double vx, double vy, double radius) {
        this.x = x;
        this.y = y;
        xspeed = vx;
        yspeed = vy;
        this.type = TYPE.DANGER;
        this.radius = radius;
        shape = new Polygon();
        int numSides = random.nextInt(5)+5;
        double degChange = Math.PI*2/numSides;
        double deg = 0;
        for (int i = 0; i <numSides; i++)
        {
            double size = radius*(50+random.nextInt(50))/90;
            shape.addPoint((int)(Math.cos(deg)*size),(int)(Math.sin(deg)*size));
            deg+=degChange;
        }
    }
    public boolean collisionPoint(double cx, double cy)
    {
        if (Math.abs(x-cx)>50||Math.abs(y-cy)>50) return false;
        Area area = new Area(shape);
        AffineTransform translate = new AffineTransform(), rotate = new AffineTransform();
        translate.translate(x, y);
        rotate.rotate(orientation);
        translate.concatenate(rotate);
        area.transform(translate);
        //return area.getBounds().contains(cx,cy);
        return (area.contains(cx, cy));
    }
    @Override
    public void render(GL2 g, double vX, double vY)
    {
        g.glPushMatrix();
        g.glTranslated(x-vX, (y-vY),0);
        g.glRotated(orientation*180f/Math.PI,0,0,1);
        g.glBegin( GL2.GL_POLYGON);
            g.glColor4f(.2f,.2f,.2f,1f);
            for (int i = 0; i < shape.npoints; i++)
            {
                g.glVertex2f( shape.xpoints[i],shape.ypoints[i]);
            }
        g.glEnd();
         g.glBegin( GL2.GL_POLYGON);
            g.glColor4f(.3f,.3f,.3f,1f);
            for (int i = 0; i < shape.npoints; i++)
            {
                g.glVertex2f( shape.xpoints[i]*(2f/3f),shape.ypoints[i]*(2f/3f));
            }
        g.glEnd();
        g.glBegin( GL2.GL_LINE_LOOP);
            g.glColor4f(.3f,.3f,.3f,1f);
            for (int i = 0; i < shape.npoints; i++)
            {
                g.glVertex2f( shape.xpoints[i],shape.ypoints[i]);
            }
        g.glEnd();
        g.glPopMatrix();
    }
        /**
     * Returns whether or not a vector at (x,y) in direction direction and magnitude magnitude intersects polygon a.
     * 
     * @param xx - x start of vector
     * @param yy - y start of vector
     * @param direction - direction of vector (in radians)
     * @param magnitude - maximum magnitude of vector
     * @return -1 if no intersection, else the minimum distance to a collision with a along the vector
     */
    public double rayTrace(double xx,double yy,final double direction,final double magnitude)
    {
        AffineTransform att = new AffineTransform();
        //att.translate(x, y);
        xx-=x;
        yy-=y;
        AffineTransform atr = new AffineTransform();
        atr.rotate(orientation);
        att.concatenate(atr);
        PathIterator pi = shape.getPathIterator(att);
        double coords[] = new double[6];
        double dbx = Math.cos(direction)*magnitude, dax;
        double dby = Math.sin(direction)*magnitude, day;
        pi.currentSegment(coords);
        double pX = coords[0], pY = coords[1], minDistance = 2;
        pi.next();
        while (!pi.isDone())
        {
            pi.currentSegment(coords);
            day = (coords[1]-pY);
            dax = (coords[0]-pX);
            double s = -(xx - pX + (dax*(pY - yy))/day)/(dbx - (dax*dby)/day);
            double t = (yy+s*dby-pY)/day;
            if (s>=0&&s<=minDistance&&Double.isFinite(s)&&t>=-1&&t<=1&&Double.isFinite(t))
            {
                minDistance=s;
            }
            pX = coords[0];
            pY = coords[1];
            pi.next();
        }
        if (minDistance<=1)
            return minDistance*magnitude;
        else
            return -1;
    }
}
