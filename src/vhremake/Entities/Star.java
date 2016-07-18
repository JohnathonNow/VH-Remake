/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vhremake.Entities;

import com.jogamp.opengl.GL2;
import com.sun.prism.impl.BufferUtil;
import java.awt.Graphics2D;
import java.nio.FloatBuffer;


/**
 *
 * @author John
 */
public class Star extends Entity{
    private final float big;
    private final FloatBuffer graphic;
    public Star(double x, double y, float big, float depth) {
        this.x = x;
        this.big = big;
        this.y = y;
        this.type = Entity.TYPE.EFFECT;
        this.depth = depth;
        graphic = BufferUtil.newFloatBuffer(56+24);
        float[]b = new float[]{
                               1,1,1, 1,  1,1,1,  0,-big/2,0,
                               1,1,1, 1,  1,1,1,  big/2,0,0,
                               1,1,1, 1,  1,1,1,  0,big/2,0,
                               1,1,1, 1,  1,1,1,  -big/2,0,0,
        
        
                               .6f,.6f,1, .5f,  1,1,1,  0,-big,0,
                               .6f,.6f,1, .5f,  1,1,1,  big,0,0,
                               .6f,.6f,1, .5f,  1,1,1,  0,big,0,
                               .6f,.6f,1, .5f,  1,1,1, -big,0,0};
        graphic.put(b);
        graphic.rewind();
    }
    @Override
    public void draw(Graphics2D g, int vx, int vy)
    {

    }
    @Override
    public boolean move()
    {
        return true;
        //Stars don't move!
    }
    @Override
    public void render(GL2 g, double vX, double vY)
    {
        if (x-vX/depth>=-3&&x-vX/depth<1000&&y-vY/depth>-3&&y-vY/depth<1000)
        {
            //g.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
            g.glPushMatrix();
            g.glTranslated(x-vX/depth, (y-vY/depth),0);
            
            //g.glBegin( GL2.GL_POLYGON);
                g.glInterleavedArrays(GL2.GL_C4F_N3F_V3F, 0, graphic);
           //     g.glBegin( GL2.GL_POLYGON);
               g.glDrawArrays(GL2.GL_QUADS, 0, 8);
               
          
           //g.glEnd();
     /*       g.glBegin( GL2.GL_POLYGON);
                g.glColor4d(1f,1f,1f,1f);
                g.glVertex2d(0,-big/2);
                g.glVertex2d(big/2,0);
                g.glVertex2d(0,big/2);
                g.glVertex2d(-big/2,0);
            g.glEnd();*/
            g.glPopMatrix();
           // g.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        }
    }
}
