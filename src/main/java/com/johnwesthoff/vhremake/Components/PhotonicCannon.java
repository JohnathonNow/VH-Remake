/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.johnwesthoff.vhremake.Components;

import java.awt.Color;
import com.jogamp.opengl.GL2;
import com.johnwesthoff.vhremake.Entities.Asteroid;
import com.johnwesthoff.vhremake.Game;

/**
 *
 * @author John
 */
public class PhotonicCannon extends Gun{
    static final long serialVersionUID = -1887359681631943982L;
    private final double[][] lightningPoints;
    public PhotonicCannon(double x, double y)
    {
        super(x,y);
        this.x = x;
        this.y = y;
        mass = 0;
        name = "Photonic Cannon";
        this.type=TYPE.GUN;
        shape.reset();
        shape.addPoint(-24, 0);
        shape.addPoint(-4, -2);
        shape.addPoint(0, -4);
        shape.addPoint(2, -2);
        shape.addPoint(2, 2);
        shape.addPoint(0, 4);
        shape.addPoint(-4, 2);
        lightningPoints = new double[2][30];
        ax = 2;
        ay = 0;
        color   = new Color(255,255,0,127);
        outline = new Color(255,255,0,255);
        this.cooldown=300;
    }
    public double hitR;
    public int discharge = 0;
    @Override
    public void shoot(Game game,int team)
    {
        if (charge<=0)
        {
            charge = cooldown;
            discharge = 60;
            fire = false;
        }
    }
    @Override
    public void obeyMaster(Game game)
    {
        super.obeyMaster(game);
      
        if (discharge>0)
        {
            discharge--;
//            Component hit = null;
//            hitR = 501;
//            double curDis;
//            for (Component c:game.components)
//            {
//                curDis = c.rayTrace(x, y, Math.PI+orientation, 500);
//                if (curDis>=30&&curDis<hitR&&c.MASTER!=MASTER)
//                {
//                    hitR = curDis;
//                    hit = c;
//                }
//            }
//            
//            if (hit!=null)
//            {
//                hit.damage(30);
//            }
            Component hit = null;
            hitR = 300;
            double hitRa = 500, hitRb = 500;
            double curDis;
            for (Component c:game.components)
            {
                curDis = c.rayTrace(x, y, orientation+Math.PI, 500);
                if (curDis>=20&&curDis<=hitR)
                {
                    hitRa = curDis;
                    hit = c;
                }
            }
            Asteroid aHit = null;
            for (Asteroid d:game.asteroids)
            {
                curDis = d.rayTrace(x, y, orientation+Math.PI, 500);
                if (curDis>=20&&curDis<=hitR)
                {
                    hitRb = curDis;
                    aHit = d;
                }
            }
            if (hitRa<hitRb)
            {
                if (hit!=null&&hit.MASTER!=MASTER)
                {
                    hit.damage(6);
                }
            }
            else
            {
                if (aHit!=null)
                {
                    aHit.xspeed-=(x-aHit.x)/10000;
                    aHit.yspeed-=(y-aHit.y)/10000;
                }
            }
            hitR = Math.min(hitRa,hitRb);
//            for (int i = 30; i < 500; i +=12)
//            {
//                hitR = i;
//                double xx = x-i*Math.cos(orientation);
//                double yy = y-i*Math.sin(orientation);
//
//                Optional<Component> may = game.components.stream().filter(c->c.MASTER!=MASTER).limit(100).filter(d->Math.abs(d.x-xx)<30&&Math.abs(d.y-yy)<30).filter((c)->c.collisionPoint(xx,yy)).findFirst();
//                if (may.isPresent())
//                {
//                    may.get().damage(30);
//                    break;
//                }
//                Optional<Asteroid> ast = game.asteroids.stream().filter(d->Math.abs(d.x-xx)<30&&Math.abs(d.y-yy)<30).filter((c)->c.collisionPoint(xx,yy)).findFirst();
//                 if (ast.isPresent())
//                {
//                    ast.get().xspeed+=(xx-x)/i/100;
//                    ast.get().yspeed+=(yy-y)/i/100;
//                    break;
//                }
//            }
            double dr = hitR/30, a;
            for (int i = 0; i < 30; i++)
            {
                a = (2-r.nextDouble()*4)/50;
                lightningPoints[0][i]=-i*dr*Math.cos(a);
                lightningPoints[1][i]=-i*dr*Math.sin(a);
            }
        }
        if (charge>=0)
        {
            charge--;
        }
    }
    @Override
    public void event(int eX, int eY, EventType eventType)
    {
        if (eventType==EventType.HOLD&&onMaster)
        {
            angle = Math.atan2(eY-y, eX-x);
            double difference = Math.abs(angleDif(angle,orientation+Math.PI));
            if (difference<FoV)
            {
                fire = true;
            }
        }
    }
    @Override
    public void render(GL2 g, double vX, double vY)
    {
        g.glPushMatrix();
        g.glTranslated(x-vX, (y-vY),0);
        g.glRotated(orientation*180f/Math.PI,0,0,1);
        Color go = onMaster?(MASTER==null?color:MASTER.color):color;
        g.glBegin( GL2.GL_POLYGON);
            g.glColor4d((go.getRed()/255f-.5f)+((1-(integrity/max_integrity)+Game.pulse*(1-(integrity/max_integrity)))/2),(go.getGreen()/255f-.5f)*integrity/max_integrity,(go.getBlue()/255f-.5f)*integrity/max_integrity,1f);
            for (int i = 0; i < shape.npoints; i++)
            {

                g.glVertex2f( shape.xpoints[i],shape.ypoints[i]);
            }
        g.glEnd();
        g.glBegin( GL2.GL_LINE_LOOP);
            g.glColor4f( go.getRed()/255f,go.getGreen()/255f,go.getBlue()/255f,1f);
            for (int i = 0; i < shape.npoints; i++)
            {
                g.glVertex2f( shape.xpoints[i],shape.ypoints[i]);
            }
        g.glEnd();
        if (discharge>0)
        {
             g.glBegin( GL2.GL_LINE_STRIP);
             g.glColor4f(1f,1f,0,1f);
             g.glVertex2d(0,0);
            for (int i = 0; i < 30; i++)
            {
                g.glVertex2d(lightningPoints[0][i],lightningPoints[1][i]);
            }
            g.glEnd();
        }
        g.glPopMatrix();
    }
}