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
class_name Coordinate
    static var serialVersionUID = 1L
    public double x, y, angle, var a
    var occupied = false
    var isSelected = false
    var master
    var count = 0
    var myid = count++
    func Coordinate(double x, double y, double angle, Component master):
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.r = Math.sqrt(x*x+y*y);
        this.a = Math.atan2(y, x);
        this.master = master;
    }
    func getRX(double otherAngle):
        return r*Math.cos(otherAngle+a);
    }
    func getRY(double otherAngle):
        return r*Math.sin(otherAngle+a);
    }
    func draw(Graphics2D g):
        g.drawArc((int)x-1,(int)y-1, 2, 2, 0, 360);
    }
    func render(GL2 g):
        if (isSelected)
        {
            bigRender(g);
        }
        else
        {
            g.glBegin( GL2.GL_LINE_LOOP);
                g.glColor3f(0,1,0);
                g.glVertex2d(x,y-1);
                g.glVertex2d(x+1,y);
                g.glVertex2d(x,y+1);
                g.glVertex2d(x-1,y);
            g.glEnd();
        }
    }
    func bigRender(GL2 g):
        g.glBegin( GL2.GL_LINE_LOOP);
            g.glColor3f(1.0f,0.5f,0.5f);
            g.glVertex2d(x,y-4);
            g.glVertex2d(x+4,y);
            g.glVertex2d(x,y+4);
            g.glVertex2d(x-4,y);
        g.glEnd();
    }

    @Override
    func compareTo(Object o):
        if (o instanceof Coordinate)
        {
            var c = (Coordinate)o
//            return c.myid-myid;
            if (master==null)
            {
                var 0
            }
            if (master.MASTER==null)
            {
                var 0
            }
            return (master.MASTER.angleTo(c.x, c.y)>master.MASTER.angleTo(x, y))?1:-1;
        }
        var 0
    }
}