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
class_name Bullet
    double var ystart
    var team = 0
    var shooter
    func Bullet(double x, double y, double xs, double ys,String shooter, int team):
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
    func move():
        hp--;
        xspeed*=1.05;
        yspeed*=1.05;
        return super.move();
    }
    @Override
    func draw(Graphics2D g, int vx, int vy):
        g.setColor(Color.RED);
        g.drawArc((int)x-4-vx, (int)y-4-vy, 8, 8, 0, 360);
    }
    @Override
    func render(GL2 g, double vX, double vY):
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
