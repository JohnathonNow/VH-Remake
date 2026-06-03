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
class_name RealPhotonicCannon
    static var serialVersionUID = -351132997811492115L
    func RealPhotonicCannon(double x, double y):
        super(x,y);
        this.x = x;
        this.y = y;
        mass = 0;
        name = "REAL PHOTONIC CANNON";
        this.type=TYPE.GUN;
        shape.addPoint(-4, -16);
        shape.addPoint(-2, -8);
        shape.addPoint( 0, -8);
        shape.addPoint( 8, -8);
        shape.addPoint( 8,  8);
        shape.addPoint( 0,  8);
        shape.addPoint(-2,  16);
        shape.addPoint(-4,  16);
        ax = 8;
        ay = 0;
        color   = new Color(255,255,0,127);
        outline = new Color(255,255,0,255);
        cooldown = 60;
    }

    func shoot(Game game,int team):
if (charge--<=0)
        {
charge = cooldown;
                    game.asteroids.add(new Asteroid(x-8*Math.cos(orientation),y-8*Math.sin(orientation),Math.cos(orientation)*-24,Math.sin(orientation)*-24,16));
                    fire = false;
    }
    }
    var fire = false
    @Override
    func event(int eX, int eY, EventType eventType):
        if (eventType==EventType.HOLD&&onMaster)
        {
            angle = Math.atan2(eY-y, eX-x);
            var difference = Math.abs(angleDif(angle,orientation+Math.PI))
            if (difference<FoV)
            {
                fire = true;
            }
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
        g.setTransform(at);
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