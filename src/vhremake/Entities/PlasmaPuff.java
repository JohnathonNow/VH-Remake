/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vhremake.Entities;

import java.awt.Graphics2D;
import com.jogamp.opengl.GL2;
/**
 *
 * @author John
 */
public class PlasmaPuff extends Entity{
    float rc,gc,bc;
    public PlasmaPuff(double x, double y, double vx, double vy, float r, float g, float b) {
        this.x = x;
        this.y = y;
        this.rc=r;
        this.gc=g;
        this.bc=b;
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
      //  g.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        g.glPushMatrix();
        g.glTranslated(x-vX, (y-vY),0);
        g.glBegin( GL2.GL_POLYGON);
        double big = (10-hp);
            g.glColor4d(rc,gc,bc,hp/20f);
            g.glVertex2d(0,-big);
            g.glVertex2d(big,0);
            g.glVertex2d(0,big);
            g.glVertex2d(-big,0);
        g.glEnd();
        g.glPopMatrix();
       // g.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    }
}
