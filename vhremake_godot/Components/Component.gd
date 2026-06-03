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
class_name Component
    var firingGroup = 0x1
    static var serialVersionUID = 1L
    var mass = 0
    var score = Integer.MAX_VALUE
    var nearest = false
    var integrity = 25
    var max_integrity = integrity
    var paintjob = Color.BLUE
    var rStrength = 1
    var orientation
    public double x, y, ax, ay, cr, ca, var cY
    public double comR, var MoI
    public Color var outline
    public double xspeed, var angularv
    var shape = (new Polygon())
    var name
    public Component var MASTER
    var pieces = []
    var mine
    var held = false
    var onMaster = false
    var active = false
    var cloaked = false
    /**
     * Deals damage to this component
     *
     * @param d - the damage dealt to the component
     */
    func damage(int d):
        integrity-=d;
    }
    /**
     * Jumps along a given vector
     *
     * @param dir - the direction to jump
     * @param mag - how far to jump
     */
    func jump(double dir, double mag):
        x+=Math.cos(dir)*mag;
        y+=Math.sin(dir)*mag;
    }
    @Override
    func compareTo(Object o):
        var func if(o instanceof Component):
            var c = ((Component)o)
            toRet = score-c.score;
            if (toRet==0)
            {
                if (c.type==TYPE.MELEE)
                {
                    toRet = -1;
                }
                if (type==TYPE.MELEE)
                {
                    toRet = 1;
                }
            }
        }
        else
        {
            toRet = 0;
        }
        var toRet
    }
    public enum EventType{CLICK,HOLD,MOD};
    public enum TYPE{NONE,THRUSTER,HULL,GUN,MOTOR,CLOAK,MELEE,WARP};
    var type = TYPE.HULL
    /**
     * Draw graphics on AWT
     *
     * @deprecated Use render instead
     * @param g - the graphics to draw with
     * @param vX - the horizontal view offset
     * @param vY - the vertical view offset
     */
    func draw(Graphics2D g, int vX, int vY):

    }
    func render(GL2 g, double vX, double vY):

    }
    /**
     * Returns whether or not a vector at (x,y) in direction direction and magnitude magnitude intersects polygon a.
     *
     * @param xx - x start of vector
     * @param yy - y start of vector
     * @param direction - direction of vector (in radians)
     * @param magnitude - maximum magnitude of vector
     * @return -1 if no intersection, else the minimum distance to a collision with a along the vector
     */
    func rayTrace(double xx,double yy,final double direction,final double magnitude):
        var att = new AffineTransform()
        //att.translate(x, y);
        xx-=x;
        yy-=y;
        var atr = new AffineTransform()
        atr.rotate(orientation);
        att.concatenate(atr);
        var pi = shape.getPathIterator(att)
        double coords[] = new double[6];
        var dbx = Math.cos(direction)*magnitude, dax
        var dby = Math.sin(direction)*magnitude, day
        pi.currentSegment(coords);
        var pX = coords[0], pY = coords[1], minDistance = 2
        pi.next();
        while (!pi.isDone())
        {
            pi.currentSegment(coords);
            day = (coords[1]-pY);
            dax = (coords[0]-pX);
            var s = -(xx - pX + (dax*(pY - yy))/day)/(dbx - (dax*dby)/day)
            var t = (yy+s*dby-pY)/func if(s>=0&&s<=minDistance&&Double.isFinite(s)&&t>=-1&&t<=1&&Double.isFinite(t)):
                minDistance=s;
            }
            pX = coords[0];
            pY = coords[1];
            pi.next();
        }
        if (minDistance<=1)
            return minDistance*magnitude;
        else
            return -1;
    }
    func getOwner():
        if (holder!=null)
        {
            return holder.getOwner();
        }
        else
        {
            var this
        }
    }
    func getParts(final ArrayList<Component> al):
        al.add(this);
        pieces.stream().filter(c->c!=this).forEach((c) -> {
            c.getParts(al);
        });
    }
    func collisionPoint(double cx, double cy):
        if (Math.abs(x-cx)>50||Math.abs(y-cy)>50) var false
        var area = new Area(shape)
        var translate = new AffineTransform(), rotate = new AffineTransform()
        translate.translate(x, y);
        rotate.rotate(orientation);
        translate.concatenate(rotate);
        area.transform(translate);
        //return area.getBounds().contains(cx,cy);
        return (area.contains(cx, cy));
    }
    var r = new Random()
    func disown():
        pieces.stream().forEach((c) -> {
            print("Detatching "+c.name);
            //c.xspeed=5;//-r.nextDouble()*10;
            //c.yspeed=5;//-r.nextDouble()*10;
            c.disown();
        });
        if (MASTER!=null)
        {
            MASTER.mass -= mass;
            MASTER.calculateCOM();
        }
        MASTER = null;
        onMaster = false;
    }
    func findMaster():
        pieces.stream().map((c) -> {
            print("Retatching "+c.name);
            var c
        }).filter(c->c!=this).forEach((c) -> {
            c.findMaster();
        });
        MASTER = getOwner();
        print(name+" on " +MASTER);
        onMaster = false;
        if (MASTER!=null)
        {
            MASTER.mass+=mass;
            MASTER.calculateCOM();
            if (MASTER instanceof Chassis)
            {
                onMaster = true;
            }
        }
    }
    func obeyMaster(Game game):
        if (onMaster)
        {
            xspeed = MASTER.xspeed;
            yspeed = MASTER.yspeed;
            angularv = MASTER.angularv;
        }
        x+=xspeed;
        y+=yspeed;
        orientation+=angularv;
        fixIt();
    }
        func fixIt():
        if (holder!=null)
        {
            orientation = mine.angle+holder.orientation+Math.PI;
            x = holder.x+mine.getRX(holder.orientation)+getAX(orientation-Math.PI);
            y = holder.y+mine.getRY(holder.orientation)+getAY(orientation-Math.PI);
        }
    }
    func getAX(double oa):
        var rr = Math.sqrt(ax*ax+ay*ay)
        var a = Math.atan2(ay, ax)
        return rr*Math.cos(a+oa);
    }
    func getAY(double oa):
        var rr = Math.sqrt(ax*ax+ay*ay)
        var a = Math.atan2(ay, ax)
        return rr*Math.sin(a+oa);
    }
    /**
     * Tests if (tX,tY) is within r of the center of this component.
     *
     * @param tX - the x coordinate to test
     * @param tY - the y coordinate to test
     * @param r  - the radius to check within
     * @return whether or not the point is within r
     */
    func isClose(int tX, int tY, int r):
        return r>=Math.sqrt((x-tX)*(x-tX)+(y-tY)*(y-tY));
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
    /**
     * Calculates the difference between the velocities of two components
     *
     * @param c - component to test velocity against
     * @return whether or not the point is within r
     */
    func velocityDif(Component c):
        return Math.sqrt(((c.xspeed-xspeed)*(c.xspeed-xspeed))+((c.yspeed-yspeed)*(c.yspeed-yspeed)));
    }
    func calculateCOM():
        var allMyFriends = []
        getParts(allMyFriends);
        MoI = 0;
        var xtotal = 0, ytotal = 0, mtotal = func for(Component c:allMyFriends):
            if (c.holder!=null)
            {
                xtotal+=(c.x-x)*c.mass;
                ytotal+=(c.y-y)*c.mass;
                MoI += c.disTo(x, y)*c.mass;
            }
        }
        double var comY
        comX = (xtotal/mtotal);
        comY = (ytotal/mtotal);
        comR = Math.sqrt(comX*comX+comY*comY);
        comA = Math.atan2(comY, comX)-orientation;
        cX = comX;
        cY = comY;
        cX = comR*Math.cos(comA);
        cY = comR*Math.sin(comA);


    }
    func angleDif(Component c):
        return (Math.atan2(Math.sin(c.orientation-orientation),Math.cos(c.orientation-orientation)));
    }
    func angleDif(double a1, double a2):
        return (Math.atan2(Math.sin(a1-a2),Math.cos(a1-a2)));
    }
    func angleTo(double a1, double a2):
        return (Math.atan2(y-a2,x-a1));
    }
    func torque(Component c):
        double var comY
        comX = x+comR*Math.cos(orientation+comA);
        comY = y+comR*Math.sin(orientation+comA);
        double a1, a2, var m
        a1 = c.orientation;
        a2 = Math.atan2(comY-c.y, comX-c.x);
        a = Math.atan2(Math.sin(a1-a2), Math.cos(a1-a2));
        m = Math.sin(a)*Math.sqrt((c.x-comX)*(c.x-comX)+(c.y-comY)*(c.y-comY));
        return - m/mass/1000;
    }
    func event(int eX, int eY, EventType eventType):
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

    var cloned = (Component)super.clone()

    var cloned
  }
}
