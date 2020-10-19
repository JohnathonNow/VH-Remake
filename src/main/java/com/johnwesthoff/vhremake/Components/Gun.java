/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.johnwesthoff.vhremake.Components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import com.jogamp.opengl.GL2;
import com.johnwesthoff.vhremake.Entities.Bullet;
import com.johnwesthoff.vhremake.Game;

/**
 *
 * @author John
 */
public class Gun extends Component{
    static final long serialVersionUID = -8590585349657954760L;
    byte timer = 0;
    double FoV = Math.PI*22.5d/180d;
    double angle = 0;
    short cooldown = 10;
    short charge = 0;
    
    public Gun(double x, double y)
    {
        this.x = x;
        this.y = y;
        mass = 0;
        name = "GUN";
        this.type=TYPE.GUN;
        shape.addPoint(-4, -4);
        shape.addPoint(-2, -4);
        shape.addPoint( 0, -2);
        shape.addPoint( 8, -2);
        shape.addPoint( 8,  2);
        shape.addPoint( 0,  2);
        shape.addPoint(-2,  4);
        shape.addPoint(-4,  4);
        ax = 8;
        ay = 0;
        color   = new Color(255,255,0,127);
        outline = new Color(255,255,0,255);
    }

    public void shoot(Game game,int team)
    {
if (charge--<=0)
        {
charge = cooldown;
                    game.bullets.add(new Bullet(x,y,MASTER.xspeed+Math.cos(orientation)*-8,MASTER.yspeed+Math.sin(orientation)*-8,this.MASTER.name,team));
                    fire = false;
    }
    }
    boolean fire = false;
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
    public void draw(Graphics2D g, int vX, int vY)
    {
        AffineTransform at = g.getTransform();
        AffineTransform translate = new AffineTransform();
        translate.translate(x-vX, y-vY);
        AffineTransform rotate = new AffineTransform();
        rotate.rotate(orientation);
        translate.concatenate(rotate);
        g.setTransform(translate);
        g.setColor(onMaster?(MASTER==null?color:MASTER.color):color);
        g.fillPolygon(shape);
        g.setColor(onMaster?(MASTER==null?outline:MASTER.outline):outline);
        g.drawPolygon(shape);
        g.setTransform(at);
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
        g.glPopMatrix();
    }
}