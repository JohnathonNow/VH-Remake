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
class_name Nebula
    var big
     var graphic
    func Nebula(double x, double y, float big):
        this.x = x;
        this.big = big;
        this.y = y;
        this.type = Entity.TYPE.EFFECT;

        float[]b = new float[]{
                               .5f,1,.5f, .1f,  1,1,1,   0,-big/2,0,
                               .5f,1,.5f, .1f,  1,1,1,   big/2,0,0,
                               .5f,1,.5f, .1f  ,1,1,1,   0,big/2,0,
                               .5f,1,.5f, .1f,  1,1,1,   -big/2,0,0,


                               .6f,1,.6f, .1f,  1,1,1,   0,-big,0,
                               .6f,1,.6f, .1f,  1,1,1,   big,0,0,
                               .6f,1,.6f, .1f  ,1,1,1,   0,big,0,
                               .6f,1,.6f, .1f,  1,1,1,   -big,0,0};
        graphic = Buffers.newDirectFloatBuffer(b);
    }
    @Override
    func draw(Graphics2D g, int vx, int vy):

    }
    @Override
    func move():
        var true
        //Stars don't move!
    }
    @Override
    func render(GL2 g, double vX, double vY):
        if (x-vX>=-100&&x-vX<10200&&y-vY>-1000&&y-vY<1090)
        {
       // g.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        g.glPushMatrix();
        g.glTranslated(x-vX, (y-vY),0);
        g.glInterleavedArrays(GL2.GL_C4F_N3F_V3F, 0, graphic);
           //     g.glBegin( GL2.GL_POLYGON);
               g.glDrawArrays(GL2.GL_QUADS, 0, 8);
//        g.glBegin( GL2.GL_POLYGON);
//            g.glColor4d(0.6f,1f,.6f,0.1f);
//            g.glVertex2d(0,-big);
//            g.glVertex2d(big,0);
//            g.glVertex2d(0,big);
//            g.glVertex2d(-big,0);
//        g.glEnd();
//        g.glBegin( GL2.GL_POLYGON);
//            g.glColor4d(.5f,1f,.5f,0.1f);
//            g.glVertex2d(0,-big/2);
//            g.glVertex2d(big/2,0);
//            g.glVertex2d(0,big/2);
//            g.glVertex2d(-big/2,0);
//        g.glEnd();
        g.glPopMatrix();
        }
        //g.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
    }
}
