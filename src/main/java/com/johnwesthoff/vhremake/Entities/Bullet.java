/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.johnwesthoff.vhremake.Entities;

import java.awt.Color;
import java.awt.Graphics2D;
import com.jogamp.opengl.GL2;
/**
 *
 * @author John
 */
public class Bullet extends Entity{
    double xstart, ystart;
    public int team = 0;
    public String shooter;
    public Bullet(double x, double y, double xs, double ys,String shooter, int team) {
        this.x = (xstart = x);
        this.y = (ystart = y);
        this.xspeed = xs/2;
        this.yspeed = ys/2;
        this.shooter = shooter;
        this.team = team;
        this.type = TYPE.BULLET;
        hp = 75;
    }
    @Override
    public boolean move()
    {
        hp--;
        xspeed*=1.05;
        yspeed*=1.05;
        return super.move();
    }
    @Override
    public void draw(Graphics2D g, int vx, int vy)
    {
        g.setColor(Color.RED);
        g.drawArc((int)x-4-vx, (int)y-4-vy, 8, 8, 0, 360);
    }
    @Override
    public void render(GL2 g, double vX, double vY)
    {
       // g.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        g.glPushMatrix();
        g.glTranslated(x-vX, (y-vY),0);
        g.glBegin( GL2.GL_LINE_STRIP);
            g.glColor3f(0,0,0);
            g.glVertex2d(-xspeed*7,-yspeed*7);
            g.glColor3f(1,1,0);
            g.glVertex2d(0,0);
        g.glEnd();
        g.glPopMatrix();
        //g.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    }
}
