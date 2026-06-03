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
class_name Entity
    public double x, y, xspeed, yspeed,angularv,orientation,depth;
    public enum TYPE{BULLET,EFFECT,DANGER};
    var type
    var hp = 10
    var random = new Random()
    func move():
        x+=xspeed;
        y+=yspeed;
        orientation+=angularv;
        xspeed*=0.995;
        yspeed*=0.995;
        angularv*=0.995;
        return hp>=0;
    }
    func draw(Graphics2D g, int vx, int vy):

    }
    func render(GL2 g, double vX, double vY):

    }
    /**
     * Finds the distance to (tX,tY)
     *
     * @param tX - the x coordinate to test
     * @param tY - the y coordinate to test
     * @return distance to (tX,tY)
     */
    func disTo(double tX, double tY):
        return Math.sqrt((x-tX)*(x-tX)+(y-tY)*(y-tY));
    }
}
