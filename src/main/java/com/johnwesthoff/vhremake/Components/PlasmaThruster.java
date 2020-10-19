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
import com.johnwesthoff.vhremake.Entities.PlasmaPuff;
import com.johnwesthoff.vhremake.Game;

/**
 *
 * @author John
 */
public class PlasmaThruster extends Thruster{
    static final long serialVersionUID = 1534759109871151660L;
    int[] xpoints = {0,0,0,0,0,0,0,0,0,0}, ypoints = {0,0,0,0,0,0,0,0,0,0};
    byte timer = 0;
    public PlasmaThruster(double x, double y)
    {
        super(x,y);
        this.x = x;
        this.y = y;
        mass = 0;
        name = "Volatile Plasma Thruster";
        int s = 3;
        shape.reset();
        shape.addPoint(0,-1*s);
        shape.addPoint(0,1*s);
        shape.addPoint(-1*s,1*s);
        shape.addPoint(-2*s,2*s);
        shape.addPoint(-3*s,1*s);
        shape.addPoint(-2*s,0);
        shape.addPoint(-3*s,-1*s);
        shape.addPoint(-2*s,-2*s);
        shape.addPoint(-1*s,-1*s);
        ax = 0;
        ay = 0;
        color   = new Color(100,255,0,127);
        outline = new Color(100,255,0,255);
        type = TYPE.THRUSTER;
        rStrength = 2;
    }
    /**
     * Deals damage to this component. Totals component if active.
     * 
     * @param d - the damage dealt to the component
     */
    @Override
    public void damage(int d)
    {
        super.damage(3);
        if (active)
        {
            integrity = -1;
        }
    }
    /**
     * Calls the Component obeyMaster method, as well as 
     * emits the plasma stream graphic effect if the thruster is active.
     * 
     * @param game - the Game world calling the method
     */
    @Override
    public void obeyMaster(Game game)
    {
        super.obeyMaster(game);
        if (onMaster)
        {
            if (active)
            {
                game.effects.add(new PlasmaPuff(x-Math.cos(orientation)*16,y-Math.sin(orientation)*16,-Math.cos(orientation)/5,-Math.sin(orientation)/5,0.75f,0.5f,0.5f));
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
        g.setColor(Color.RED);
        g.drawLine((int)ax*3/4,(int)ay*3/4,(int)ax*5/4,(int)ay*5/4);
        g.setTransform(at);
        if (active)
        {
            for (int i = 0; i < xpoints.length-1; i++)
            {
                g.drawLine(xpoints[i]-vX, ypoints[i]-vY, xpoints[i+1]-vX, ypoints[i+1]-vY);
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
        g.glPopMatrix();
    }
}