/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.johnwesthoff.vhremake.Components;

import java.awt.Color;
import com.jogamp.opengl.GL2;
/**
 *
 * @author John
 */
public class Armor extends Hull{
    static final long serialVersionUID = -219838555725653209L;
    public Armor(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.ax = 1;
        this.ay = 0;
        shape.addPoint(0,-height/2);
        shape.addPoint(-width/2,-height/2);
        shape.addPoint(-width,width/2-height/2);
        shape.addPoint(-width,height/2-width/2);
        shape.addPoint(-width/2,height/2);
        shape.addPoint(0,height/2);
        integrity = 1000;
        max_integrity = integrity;
        mass = height*width;
        color = new Color(200,200,200,127);
        outline = new Color(255,255,255,255);
        name = mass+"kg Armor";
        //orientation = (float) ((new Random()).nextInt(360)*Math.PI/180);
    }
    @Override
    public void render(GL2 g, double vX, double vY)
    {
        g.glPushMatrix();
        g.glTranslated(x-vX, (y-vY),0);
        g.glRotated(orientation*180f/Math.PI,0,0,1);
        Color go = color;
        g.glBegin( GL2.GL_POLYGON);
            g.glColor4f( go.getRed()/255f,(go.getGreen()/255f)*(integrity/max_integrity),(go.getBlue()/255f)*(integrity/max_integrity),1f);
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
        if (!onMaster)
        {
            g.glBegin( GL2.GL_LINE_LOOP);
            g.glColor3f(1,0,0);
                g.glVertex2f(0,0);
                g.glVertex2d(ax,ay);
            g.glEnd();
        }
        locations.stream().forEach((c) -> {
            c.render(g);
        });
        g.glPopMatrix();
    }
    
}
