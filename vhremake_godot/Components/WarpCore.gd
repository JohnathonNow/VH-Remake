/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */











/**
 *
 * @author John
 */
extends Node2D
class_name WarpCore
    static var serialVersionUID = 1350865709912274567L
    var xpoints = {0,0,0,0,0,0,0,0,0,0}, ypoints = {0,0,0,0,0,0,0,0,0,0}
    var timer = 0
    var rechargeCounter = 0
    var timeToRecharge = 60*10
    func WarpCore(double x, double y):
        this.x = x;
        this.y = y;
        mass = 0;
        name = "Warp Core";
        shape.addPoint(0,-3);
        shape.addPoint(0,3);
        shape.addPoint(-1*3,4*3);
        shape.addPoint(-3*3,3*3);
        shape.addPoint(-3*3,1*3);
        shape.addPoint(-4*3,0);
        shape.addPoint(-3*3,-1*3);
        shape.addPoint(-3*3,-3*3);
        shape.addPoint(-1*3,-4*3);
        ax = 0;
        ay = 0;
        color   = new Color(100,255,0,127);
        outline = new Color(100,255,0,255);
        rStrength = 0.1f;
        type = TYPE.WARP;
    }
        /**
     * Calls the Component obeyMaster method, as well as
     * emits the plasma stream graphic effect if the thruster is active.
     * This method also warps the ship when upon activation.
     *
     * @param game - the Game world calling the method
     */
    @Override
    func obeyMaster(Game game):
        super.obeyMaster(game);
        if (onMaster)
        {
            if (active)
            {
                game.effects.add(new PlasmaPuff(x-Math.cos(orientation)*16,y-Math.sin(orientation)*16,-Math.cos(orientation)/5,-Math.sin(orientation)/5,0.4f,0.4f,0.85f));
            }
            if (rechargeCounter>0)
            {
                rechargeCounter--;
            }
        }
    }
    func warp(Player p):
        if (rechargeCounter<=0)
        {
            rechargeCounter=timeToRecharge;
            p.getChassis().jump(orientation,250);
        }
    }
    @Override
    func draw(Graphics2D g, int vX, int vY):
        var at = g.getTransform()
        var translate = new AffineTransform()
        translate.translate(x-vX, y-vY);
        var rotate = new AffineTransform()
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
            for (var i = 0 i < xpoints.length-1; i++)
            {
                g.drawLine(xpoints[i]-vX, ypoints[i]-vY, xpoints[i+1]-vX, ypoints[i+1]-vY);
            }
        }
    }
    @Override
    func render(GL2 g, double vX, double vY):
        g.glPushMatrix();
        g.glTranslated(x-vX, (y-vY),0);
        g.glRotated(orientation*180f/Math.PI,0,0,1);
        var go = onMaster?(MASTER==null?color:MASTER.color):color
        g.glBegin( GL2.GL_POLYGON);
            g.glColor4d((go.getRed()/255f-.5f)+((1-(integrity/max_integrity)+Game.pulse*(1-(integrity/max_integrity)))/2),(go.getGreen()/255f-.5f)*integrity/max_integrity,(go.getBlue()/255f-.5f)*integrity/max_integrity,1f);
            for (var i = 0 i < shape.npoints; i++)
            {

                g.glVertex2f( shape.xpoints[i],shape.ypoints[i]);
            }
        g.glEnd();
        g.glBegin( GL2.GL_LINE_LOOP);
            g.glColor4f( go.getRed()/255f,go.getGreen()/255f,go.getBlue()/255f,1f);
            for (var i = 0 i < shape.npoints; i++)
            {
                g.glVertex2f( shape.xpoints[i],shape.ypoints[i]);
            }
        g.glEnd();
        g.glPopMatrix();
    }
}