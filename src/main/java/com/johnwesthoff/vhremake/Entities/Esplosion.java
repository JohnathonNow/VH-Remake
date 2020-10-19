/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.johnwesthoff.vhremake.Entities;
import com.jogamp.opengl.GL2;
import java.awt.Graphics2D;

/**
 *
 * @author John
 */
public class Esplosion extends Entity{
    public Esplosion(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        xspeed = vx;
        yspeed = vy;
        this.type = TYPE.EFFECT;
    }
    @Override
    public void draw(Graphics2D g, int vx, int vy)
    {

    }
    @Override
    public boolean move()
    {
        hp-=.05;
        return super.move();
    }
    @Override
    public void render(GL2 g, double vX, double vY)
    {
        
        g.glPushMatrix();
        g.glTranslated(x-vX, (y-vY),0);
        g.glBegin( GL2.GL_POLYGON);
        double big = 5*(12-hp);
            g.glColor4d(1f,.1f,0f,hp/10f);
            g.glVertex2d(0,-big);
            g.glVertex2d(big,0);
            g.glVertex2d(0,big);
            g.glVertex2d(-big,0);
        g.glEnd();
        g.glPopMatrix();
        //g.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    }
}
