/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.johnwesthoff.vhremake.Components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import com.jogamp.opengl.GL2;
import com.johnwesthoff.vhremake.Coordinate;
import com.johnwesthoff.vhremake.Game;

/**
 *
 * @author John
 */
public abstract class Component implements Serializable, Comparable, Cloneable{
    public byte firingGroup = 0x1;
    static final long serialVersionUID = 1L;
    public int mass = 0;
    public int score = Integer.MAX_VALUE;
    public boolean nearest = false;
    public float integrity = 25;
    public float max_integrity = integrity;
    public Color paintjob = Color.BLUE;
    public float rStrength = 1;
    public double orientation;
    public double x, y, ax, ay, cr, ca, cX, cY;
    public double comR, comA, MoI;
    public Color color, outline;
    public double xspeed, yspeed, angularv;
    public Polygon shape = (new Polygon());
    public String name;
    public Component holder, MASTER;
    public ArrayList<Component> pieces = new ArrayList<>();
    public Coordinate mine;
    public boolean held = false;
    public boolean onMaster = false; 
    public boolean active = false;
    public boolean cloaked = false;
    /**
     * Deals damage to this component
     * 
     * @param d - the damage dealt to the component
     */
    public void damage(int d)
    {
        integrity-=d;
    }
    /**
     * Jumps along a given vector
     * 
     * @param dir - the direction to jump
     * @param mag - how far to jump
     */
    public void jump(double dir, double mag)
    {
        x+=Math.cos(dir)*mag;
        y+=Math.sin(dir)*mag;
    }
    @Override
    public int compareTo(Object o) {
        int toRet;
        if (o instanceof Component)
        {
            Component c = ((Component)o);
            toRet = score-c.score;
            if (toRet==0)
            {
                if (c.type==TYPE.MELEE)
                {
                    toRet = -1;
                }
                if (type==TYPE.MELEE)
                {
                    toRet = 1;
                }
            }
        }
        else
        {
            toRet = 0;
        }
        return toRet;
    }
    public enum EventType{CLICK,HOLD,MOD};
    public enum TYPE{NONE,THRUSTER,HULL,GUN,MOTOR,CLOAK,MELEE,WARP};
    public TYPE type = TYPE.HULL;
    /**
     * Draw graphics on AWT
     * 
     * @deprecated Use render instead
     * @param g - the graphics to draw with
     * @param vX - the horizontal view offset
     * @param vY - the vertical view offset
     */
    public void draw(Graphics2D g, int vX, int vY)
    {
        
    }
    public void render(GL2 g, double vX, double vY)
    {
        
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
    public Component getOwner()
    {
        if (holder!=null)
        {
            return holder.getOwner();
        }
        else
        {
            return this;
        }
    }
    public void getParts(final ArrayList<Component> al)
    {
        al.add(this);
        pieces.stream().filter(c->c!=this).forEach((c) -> {
            c.getParts(al);
        });
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
    static Random r = new Random();
    public void disown()
    {
        pieces.stream().forEach((c) -> {
            System.out.println("Detatching "+c.name);
            //c.xspeed=5;//-r.nextDouble()*10;
            //c.yspeed=5;//-r.nextDouble()*10;
            c.disown();
        });
        if (MASTER!=null)
        {
            MASTER.mass -= mass;
            MASTER.calculateCOM();
        }
        MASTER = null;
        onMaster = false;
    }
    public void findMaster()
    {
        pieces.stream().map((c) -> {
            System.out.println("Retatching "+c.name);
            return c;
        }).filter(c->c!=this).forEach((c) -> {
            c.findMaster();
        });
        MASTER = getOwner();
        System.out.println(name+" on " +MASTER);
        onMaster = false;
        if (MASTER!=null)
        {
            MASTER.mass+=mass;
            MASTER.calculateCOM();
            if (MASTER instanceof Chassis)
            {
                onMaster = true;
            }
        }
    }
    public void obeyMaster(Game game)
    {
        if (onMaster)
        {
            xspeed = MASTER.xspeed;
            yspeed = MASTER.yspeed;
            angularv = MASTER.angularv;
        }
        x+=xspeed;
        y+=yspeed;
        orientation+=angularv;
        fixIt();
    }
        public void fixIt()
    {
        if (holder!=null)
        {
            orientation = mine.angle+holder.orientation+Math.PI;
            x = holder.x+mine.getRX(holder.orientation)+getAX(orientation-Math.PI);
            y = holder.y+mine.getRY(holder.orientation)+getAY(orientation-Math.PI);
        }
    }
    public double getAX(double oa)
    {
        double rr = Math.sqrt(ax*ax+ay*ay);
        double a = Math.atan2(ay, ax);
        return rr*Math.cos(a+oa);
    }
    public double getAY(double oa)
    {
        double rr = Math.sqrt(ax*ax+ay*ay);
        double a = Math.atan2(ay, ax);
        return rr*Math.sin(a+oa);
    }
    /**
     * Tests if (tX,tY) is within r of the center of this component.
     * 
     * @param tX - the x coordinate to test
     * @param tY - the y coordinate to test
     * @param r  - the radius to check within
     * @return whether or not the point is within r
     */
    public boolean isClose(int tX, int tY, int r)
    {
        return r>=Math.sqrt((x-tX)*(x-tX)+(y-tY)*(y-tY));
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
    /**
     * Calculates the difference between the velocities of two components
     * 
     * @param c - component to test velocity against
     * @return whether or not the point is within r
     */
    public double velocityDif(Component c)
    {
        return Math.sqrt(((c.xspeed-xspeed)*(c.xspeed-xspeed))+((c.yspeed-yspeed)*(c.yspeed-yspeed)));
    }
    public void calculateCOM()
    {
        ArrayList<Component> allMyFriends = new ArrayList<>();
        getParts(allMyFriends);
        MoI = 0;
        double xtotal = 0, ytotal = 0, mtotal = mass;
        for (Component c:allMyFriends)
        {
            if (c.holder!=null)
            {
                xtotal+=(c.x-x)*c.mass;
                ytotal+=(c.y-y)*c.mass;
                MoI += c.disTo(x, y)*c.mass;
            }
        }
        double comX, comY;
        comX = (xtotal/mtotal);
        comY = (ytotal/mtotal);
        comR = Math.sqrt(comX*comX+comY*comY);
        comA = Math.atan2(comY, comX)-orientation;
        cX = comX;
        cY = comY;
        cX = comR*Math.cos(comA);
        cY = comR*Math.sin(comA);
        
        
    }
    public double angleDif(Component c)
    {
        return (Math.atan2(Math.sin(c.orientation-orientation),Math.cos(c.orientation-orientation)));
    }
    public double angleDif(double a1, double a2)
    {
        return (Math.atan2(Math.sin(a1-a2),Math.cos(a1-a2)));
    }
    public double angleTo(double a1, double a2)
    {
        return (Math.atan2(y-a2,x-a1));
    }
    public double torque(Component c)
    {
        double comX, comY;
        comX = x+comR*Math.cos(orientation+comA);
        comY = y+comR*Math.sin(orientation+comA);
        double a1, a2, a, m;
        a1 = c.orientation;
        a2 = Math.atan2(comY-c.y, comX-c.x);
        a = Math.atan2(Math.sin(a1-a2), Math.cos(a1-a2));
        m = Math.sin(a)*Math.sqrt((c.x-comX)*(c.x-comX)+(c.y-comY)*(c.y-comY));
        return - m/mass/1000;
    }
    public void event(int eX, int eY, EventType eventType)
    {
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {

    Component cloned=(Component)super.clone();

    return cloned;
  }
}
