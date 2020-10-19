/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.johnwesthoff.vhremake.Components;

import java.awt.Color;
import com.jogamp.opengl.GL2;
import com.johnwesthoff.vhremake.Game;
/**
 *
 * @author John
 */
public class Saw extends Component{
    static final long serialVersionUID = -6744553048762788525L;
    private float spin = 0;
    public Saw(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.ax = 1;
        this.ay = 0;
        shape.addPoint(0,-29);
        shape.addPoint(-2,-23);
        shape.addPoint(-2,-23);
        shape.addPoint(-10,-26);
        shape.addPoint(-6,-20);
        shape.addPoint(-19,-22);
        shape.addPoint(-14,-16);
        shape.addPoint(-27,-14);
        shape.addPoint(-20,-6);
        shape.addPoint(-26,-1);
        shape.addPoint(-19,2);
        shape.addPoint(-27,10);
        shape.addPoint(-18,10);
        shape.addPoint(-21,28);
        shape.addPoint(-13,19);
        shape.addPoint(-9,27);
        shape.addPoint(-3,18);
        shape.addPoint(3,27);
        shape.addPoint(3,15);
        shape.addPoint(15,26);
        shape.addPoint(13,12);
        shape.addPoint(26,16);
        shape.addPoint(17,4);
        shape.addPoint(24,0);
        shape.addPoint(14,-3);
        shape.addPoint(26,-13);
        shape.addPoint(14,-10);
        shape.addPoint(22,-24);
        shape.addPoint(10,-18);
        shape.addPoint(10,-29);
        shape.addPoint(3,-23);
        integrity = 500;
        max_integrity = integrity;
        color = new Color(200,200,200,127);
        outline = new Color(255,255,255,255);
        name = "Saw";
        type=TYPE.MELEE;
        mass = 50;
        //orientation = (float) ((new Random()).nextInt(360)*Math.PI/180);
    }
    /**
     * Stab things
     * 
     * @param game - the game containing pieces to stab 
     */
    @Override
    public void obeyMaster(Game game)
    {
        super.obeyMaster(game);
        if (onMaster)
        {
        int tipX = (int)(-Math.cos(orientation)*48+x);
        int tipY = (int)(-Math.sin(orientation)*48+y);
        spin+=Math.PI/10;
        game.players.stream().filter(p->p.getChassis().isClose(tipX,tipY,200))
                .forEach(p->p.components.stream().filter(c->c.collisionPoint(tipX, tipY))
                        .forEach(h->h.damage((int)(10*h.MASTER.velocityDif(this.MASTER)))));
        }
    }
    @Override
    public void render(GL2 g, double vX, double vY)
    {
        g.glPushMatrix();
        g.glTranslated(x-vX, (y-vY),0);
        g.glRotated((spin+orientation)*180f/Math.PI,0,0,1);
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
        g.glPopMatrix();
    }
}
