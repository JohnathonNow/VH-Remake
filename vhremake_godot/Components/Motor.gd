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
class_name Motor
    static var serialVersionUID = 7044319729499612621L
    var myRotation = 0
    var rev = 1
    func Motor(int x, int y, int width, int height,float rev):
        this.x = x;
        this.y = y;
        this.ax = (width/2);
        this.ay = 0;
        this.rev = rev;
        locations.add(new Coordinate(0,0,Math.PI,this));
        shape.addPoint(0, -height/2);
        shape.addPoint(0, height/2);
        shape.addPoint(width, height/2);
        shape.addPoint(width, -height/2);
        mass = 1;
        color = new Color(0,255,255,127);
        outline = new Color(0,255,255,255);
        name = "Motor";
        type = TYPE.MOTOR;

        //orientation = (float) ((new Random()).nextInt(360)*Math.PI/180);
    }
    @Override
    func fixIt():
        if (holder!=null)
        {
            orientation = mine.angle+holder.orientation+Math.PI+myRotation;
            x = holder.x+mine.getRX(holder.orientation)+getAX(orientation-Math.PI);
            y = holder.y+mine.getRY(holder.orientation)+getAY(orientation-Math.PI);
        }
    }
    @Override
    public Object clone() throws CloneNotSupportedException {

    var cloned = new Motor((int)x,(int)y,shape.xpoints[2],shape.ypoints[2]*2,rev)

    var cloned
  }
    @Override
    func event(int ex, int ey,EventType t):
        if (t==EventType.MOD)
        {
            rev = -rev;
            print("REV");
        }
    }

}
