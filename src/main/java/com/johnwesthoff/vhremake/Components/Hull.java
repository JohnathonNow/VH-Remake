/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.johnwesthoff.vhremake.Components;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import com.jogamp.opengl.GL2;
import com.johnwesthoff.vhremake.Coordinate;
import com.johnwesthoff.vhremake.Game;

/**
 *
 * @author John
 */
public class Hull extends Component{
    public ArrayList<Coordinate> locations = new ArrayList<>();
    static final long serialVersionUID = -3572277392867535870L;
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
        g.setColor(Color.GREEN);
        locations.stream().forEach((c) -> {
            c.draw(g);
        });
        g.setColor(Color.RED);
        g.drawLine((int)ax*3/4,(int)ay*3/4,(int)ax*5/4,(int)ay*5/4);
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
        if (!onMaster)
        {
            g.glBegin( GL2.GL_LINE_LOOP);
            g.glColor3f(1,0,0);
                g.glVertex2f(0,0);
                g.glVertex2d(ax,ay);
            g.glEnd();
        }
        g.glBegin( GL2.GL_LINE_LOOP);
            g.glColor3f(1,0,0);
                g.glVertex2f(0,0);
                g.glVertex2d(comR*Math.cos(comA),comR*Math.sin(comA));
            g.glEnd();
        
        locations.stream().forEach((c) -> {
            c.render(g);
        });
        g.glPopMatrix();
    }
    @Override
    public String toString()
    {
        return this.getClass().getSimpleName()+": "+name;
    }
    public ArrayList<Coordinate> getHardpoints()
    {
        ArrayList<Coordinate> _co = new ArrayList<>();
        locations.stream().filter((c) -> (!c.occupied)).forEach((c) -> {
            _co.add(c);
        });
        pieces.stream().filter(c->c instanceof Hull).forEach(c->_co.addAll(((Hull)c).getHardpoints()));
        return _co;
    }
    public void constructPoints()
    {
        
    }
    @Override
    public Object clone() throws CloneNotSupportedException {

    Hull cloned=(Hull)super.clone();
    cloned.locations = new ArrayList<>(cloned.locations);
    cloned.constructPoints();
    return cloned;
  }
}
