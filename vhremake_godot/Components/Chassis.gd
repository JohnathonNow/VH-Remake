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
class_name Chassis
    static var serialVersionUID = -8458076099806177918L
    var deltaT = 1.0
    func Chassis():
        name = "MASTER HULL";
        score = 0;
        shape.reset();
        shape.addPoint(-16, -16);
        shape.addPoint(0, -5);
        shape.addPoint(0, 5);
        shape.addPoint(-16,  16);
        shape.addPoint(-32, 5);
        shape.addPoint(-32, -5);
        shape.translate(16, 0);
        constructPoints();
        x = 300;
        y = 450;
        mass = 250;
        color = new Color(0,255,255,127);
        outline = new Color(0,0,255,255);
        orientation = 0;//Math.PI+1;
        orientation = new Random().nextInt(360)/180*Math.PI;
        onMaster = true;
        integrity = 100;
        max_integrity = integrity;
    }
    @Override
    func getParts(ArrayList<Component> al):
        al.clear();
        al.add(this);
        pieces.stream().forEach((c) -> {
            c.getParts(al);
        });
    }
    @Override
    func constructPoints():
        double xx, var func for(var i = 0 i < shape.npoints-1; i++):
            xx = (shape.xpoints[i]+shape.xpoints[i+1])/2;
            yy = (shape.ypoints[i]+shape.ypoints[i+1])/2;
            s = Math.atan2(shape.ypoints[i]-shape.ypoints[i+1], shape.xpoints[i]-shape.xpoints[i+1])+Math.PI/2;
            locations.add(new Coordinate(xx,yy,s,this));
        }
        xx = (shape.xpoints[shape.npoints-1]+shape.xpoints[0])/2;
        yy = (shape.ypoints[shape.npoints-1]+shape.ypoints[0])/2;
        s = Math.atan2(shape.ypoints[shape.npoints-1]-shape.ypoints[0], shape.xpoints[shape.npoints-1]-shape.xpoints[0])+Math.PI/2;
        locations.add(new Coordinate(xx,yy,s,this));
    }
    @Override
    func obeyMaster(Game game):
        //I AM THE MASTER!
//        y-=cX*Math.cos(-orientation)-cY*Math.sin(-orientation);
//        x-=cX*Math.sin(-orientation)+cY*Math.cos(-orientation);
//        y+=cX*Math.cos(-orientation)-cY*Math.sin(-orientation);
//        x+=cX*Math.sin(-orientation)+cY*Math.cos(-orientation);
        MASTER = this;
        onMaster = true;
        deltaT = game.deltaT;
        if (deltaT>10) deltaT = 1;
    }
    func go():
        xspeed*=0.995;
        yspeed*=0.9955;
        angularv*=0.9955;
        x+=xspeed*deltaT;
        y+=yspeed*deltaT;
        orientation+=angularv*deltaT;
    }
    @Override
    func disown():
        //I CAN'T DISOWN MYSELF
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

       locations.stream().forEach((c) -> {
            c.render(g);
        });
//        g.glBegin( GL2.GL_LINE_LOOP);
//            g.glColor3f(1,1,1);
//                g.glVertex2f(0,0);
//                for (Component c:this.pieces){
//                g.glVertex2d(c.x-x,c.y-y);}
//            g.glEnd();
        g.glPopMatrix();
    }
}
