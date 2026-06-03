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
class_name Laser
    static var serialVersionUID = -1068066043467617631L
    func Laser(double x, double y):
        super(x,y);
        this.x = x;
        this.y = y;
        mass = 0;
        name = "Laser";
        this.type=TYPE.GUN;
        shape.reset();
        shape.addPoint(-12, 0);
        shape.addPoint(-4, -2);
        shape.addPoint(0, -4);
        shape.addPoint(2, -2);
        shape.addPoint(2, 2);
        shape.addPoint(0, 4);
        shape.addPoint(-4, 2);
        ax = 2;
        ay = 0;
        color   = new Color(255,255,0,127);
        outline = new Color(255,255,0,255);
        this.cooldown=90;
    }
    var hitR
    var discharge = 0
    @Override
    func shoot(Game game,int team):
        if (charge<=0)
        {
            charge = cooldown;
            discharge = 30;
            fire = false;
        }
    }
    @Override
    func obeyMaster(Game game):
        super.obeyMaster(game);
        if (discharge>0)
        {
            discharge--;
            var hit = null
            hitR = 500;
            var hitRa = 500, hitRb = 500
            var func for(Component c:game.components):
                curDis = c.rayTrace(x, y, orientation+Math.PI, 500);
                if (curDis>=20&&curDis<=hitR)
                {
                    hitRa = curDis;
                    hit = c;
                }
            }
            var aHit = func for(Asteroid d:game.asteroids):
                curDis = d.rayTrace(x, y, orientation+Math.PI, 500);
                if (curDis>=20&&curDis<=hitR)
                {
                    hitRb = curDis;
                    aHit = d;
                }
            }
            if (hitRa<hitRb)
            {
                if (hit!=null&&hit.MASTER!=MASTER)
                {
                    hit.damage(3);
                }
            }
            else
            {
                if (aHit!=null)
                {
                    aHit.xspeed-=(x-aHit.x)/10000;
                    aHit.yspeed-=(y-aHit.y)/10000;
                }
            }
            hitR = Math.min(hitRa,hitRb);
            /*for (var i = 20 i var 500 i +=12)
            {
                hitR = i;
                var xx = x-i*Math.cos(orientation)
                var yy = y-i*Math.sin(orientation)

                var may = game.components.stream().filter(c->c.MASTER!=MASTER).limit(100).filter(d->Math.abs(d.x-xx)<30&&Math.abs(d.y-yy)<30).filter((c)->c.collisionPoint(xx,yy)).findFirst()
                if (may.isPresent())
                {
                    may.get().damage(3);
                    break;
                }
                 var ast = game.asteroids.stream().filter(d->Math.abs(d.x-xx)<30&&Math.abs(d.y-yy)<30).filter((c)->c.collisionPoint(xx,yy)).findFirst()
                if (ast.isPresent())
                {
                    ast.get().xspeed+=(xx-x)/i/100;
                    ast.get().yspeed+=(yy-y)/i/100;
                    break;
                }
            }*/
        }
        if (charge>=0)
        {
            charge--;
        }
    }
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
        if (discharge>0)
        {
            g.glBegin( GL2.GL_LINE_LOOP);
                g.glColor4f(1f,0,0,1f);
                g.glVertex2f(0,0);
                g.glVertex2d(-hitR,0);
            g.glEnd();
        }
        g.glPopMatrix();
    }
}