/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vhremake.Components;

import java.awt.Color;
import com.jogamp.opengl.GL2;
import vhremake.Game;
/**
 *
 * @author John
 */
public class Lance extends Component{
    static final long serialVersionUID = 8655528870237956456L;
    public Lance(int x, int y)
    {
        this.x = x;
        this.y = y;
        this.ax = 1;
        this.ay = 0;
        shape.addPoint(0,-6);
        shape.addPoint(0,6);
        shape.addPoint(-3,3);
        shape.addPoint(-48,0);
        shape.addPoint(-3,-3);
        integrity = 500;
        max_integrity = integrity;
        color = new Color(200,200,200,127);
        outline = new Color(255,255,255,255);
        name = "Lance";
        type=TYPE.NONE;
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
        game.players.stream().filter(p->p.getChassis().isClose(tipX,tipY,200))
                .forEach(p->p.components.stream().filter(c->c.collisionPoint(tipX, tipY)&&c.MASTER!=MASTER)
                        .forEach(h->h.damage((int)(10))));
        }
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
        g.glPopMatrix();
    }
}
