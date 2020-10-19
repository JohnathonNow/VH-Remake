package com.johnwesthoff.vhremake;

import ch.aplu.xboxcontroller.XboxController;
import ch.aplu.xboxcontroller.XboxControllerListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import com.johnwesthoff.utils.JInputInvoker;
import com.johnwesthoff.vhremake.ChooseShips.FileE;
import com.johnwesthoff.vhremake.Components.*;

/**
 * The Player object class, which handles their ship and the corresponding
 * controls. Built to make code more modular and to prevent an array of
 * Component ArrayLists.
 *
 * @version 0.01, 02/20/15
 * @author John
 */
public class Player implements XboxControllerListener {

    private static final ArrayList<Component> attachables = new ArrayList<>();
    private static final boolean attachablesInitialized = false;
    private static int pNum = 1, xNum = 1, cNum = 1;
    final int me;
    private Chassis chassis;
    private final Game world;
    //private int cloaks = 0;
    protected int holdTime, holdMax = 30;
    public ArrayList<Component> components;
    XboxController controller;
    private boolean shootyShootShoot = false;
    private double thrustDir, thrustMag, turnDir, turnMag, cursX, cursY;
    protected double vX, vY;
    private boolean mLeft, mRight, isListening, isWarping, kill;
    private FileE shipDesign;
    private int rumble = 0, chassisDesign = 0;
    private byte firingGroup = 0x1;
    public Coordinate selected;
    private int partSel = 0;
    private ArrayList<Coordinate> hardPoints = new ArrayList<>();

    /**
     * Player Constructor - assigns controller, name, and location init must be
     * called before the controller works, and the ship must be populated with
     * the Game object's method loadShips.
     *
     * @param world - the Game object that the player is attached to
     * @param shipDesign - the file containing this design
     * @see vhremake.Game#loadShips(ChooseShips.FileE ships[])
     * @see #init()
     */
    public Player(Game world, ChooseShips.FileE shipDesign) {
        me = pNum++;
        this.world = world;
        this.shipDesign = shipDesign;
        chassis = new Chassis();
        components = new ArrayList<>();
        components.add(chassis);

    }

    protected void init() {
        if (!isListening) {
            controller = new XboxController(xNum);
            if (controller.isConnected()) {
                controller.addXboxControllerListener(this);
                xNum++;
            } else {
                if (cNum <= JInputInvoker.conNum()) {
                    JInputInvoker ok = new JInputInvoker();
                    ok.setController(cNum);
                    ok.addListener(this);
                    Thread me = new Thread(ok);
                    me.start();
                    cNum++;
                }
            }
            isListening = true;
        }
        chassis.name = "Player " + me;
        chassis.orientation = Math.PI / 2;
        chassis.x = 900 * me;
        chassis.y = 900 * me;
    }

    /**
     * A getter for the Chassis
     *
     * @return The player's chassis
     */
    public Chassis getChassis() {
        return chassis;
    }

    /**
     * A setter for the Chassis
     *
     * @param c - the Chassis to replace the player's current one
     */
    protected void overwriteChassis(Chassis c) {
        chassis = c;
    }

    @Override
    public void buttonA(boolean bln) {
        if (world.editMode && bln) {
            if (selected != null) {
                try {
                    Component adding = (Component) attachables.get(partSel).clone();
                    adding.firingGroup = firingGroup;
                    world.components.add(world.attachPiece(adding, (Hull) selected.master, selected, components));

                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
                }
                selected.isSelected = false;
            }
            hardPoints = chassis.getHardpoints();
        }
        if (world.paused && bln) {
            JFileChooser shipSaver = new JFileChooser(System.getProperty("user.dir"));
            shipSaver.setFileFilter(new FileFilter() {

                @Override
                public boolean accept(File pathname) {
                    return (pathname.getAbsolutePath().toLowerCase().endsWith(".shp"));
                }

                @Override
                public String getDescription() {
                    return "Ship Files";
                }
            });
            if (JFileChooser.APPROVE_OPTION == shipSaver.showSaveDialog(world.tab.getTabComponentAt(0))) {
                String swag = shipSaver.getSelectedFile().getAbsolutePath();
                if (!swag.toLowerCase().endsWith(".shp")) {
                    swag += ".shp";
                }
                world.saveShip(this, new File(swag));
                world.paused = false;
            }
        }
    }

    @Override
    public void buttonB(boolean bln) {
        if (bln) {
            world.editMode = !world.editMode;
            chassis.xspeed = 0;
            chassis.yspeed = 0;
            chassis.orientation = 0;
            chassis.angularv = 0;
            cursX = 0;
            cursY = 0;
            hardPoints = chassis.getHardpoints();
        }
    }

    @Override
    public void buttonX(boolean bln) {
        kill = bln;
        if (world.paused && bln) {
            world.tab.setSelectedIndex(0);
        }
    }

    @Override
    public void buttonY(boolean bln) {
        isWarping = bln;
    }

    @Override
    public void back(boolean bln) {
        if (bln) {
            if (world.editMode) {
                components.stream().filter(c -> c != chassis).forEach((c) -> {
                    c.integrity = -100;
                });
                if (++chassisDesign >= 2) {
                    chassisDesign = 0;
                }
                switch (chassisDesign) {
                    case 0:
                        chassis.locations.clear();
                        chassis.shape.reset();
                        chassis.shape.addPoint(-16, -16);
                        chassis.shape.addPoint(0, -5);
                        chassis.shape.addPoint(0, 5);
                        chassis.shape.addPoint(-16,  16);
                        chassis.shape.addPoint(-32, 5);
                        chassis.shape.addPoint(-32, -5);
                        chassis.shape.translate(16, 0);
                        chassis.constructPoints();
                        break;
                    case 1:
                        chassis.shape.reset();
                        chassis.shape.addPoint(-16, -16);
                        chassis.shape.addPoint(16, -16);
                        chassis.shape.addPoint(16, 16);
                        chassis.shape.addPoint(-16, 16);
                        chassis.locations.clear();
                        for (int i = chassis.shape.xpoints[0] + 8; i <= chassis.shape.xpoints[1] - 8; i += 16) {
                            chassis.locations.add(new Coordinate(i, chassis.shape.ypoints[0], 3 * Math.PI / 2, chassis));
                            chassis.locations.add(new Coordinate(i, chassis.shape.ypoints[2], Math.PI / 2, chassis));
                        }
                        for (int i = chassis.shape.ypoints[0] + 8; i <= chassis.shape.ypoints[2] - 8; i += 16) {
                            chassis.locations.add(new Coordinate(chassis.shape.xpoints[0], i, Math.PI, chassis));
                            chassis.locations.add(new Coordinate(chassis.shape.xpoints[1], i, 0, chassis));
                        }
                        break;
                }
            } else {
                chassis.integrity = -1;
            }
        }

    }

    @Override
    public void start(boolean bln) {
        if (bln) {
            world.paused = !world.paused;
        }
    }

    @Override
    public void leftShoulder(boolean bln) {
        mLeft = bln;
    }

    @Override
    public void rightShoulder(boolean bln) {
        mRight = bln;
    }

    @Override
    public void leftThumb(boolean bln) {
    }

    @Override
    public void rightThumb(boolean bln) {
    }

    @Override
    public void dpad(int i, boolean bln) {
        firingGroup = (byte)((1<<((i-(i&1))/2))|(1<<((i+(i&1))/2)) % 15);
    }

    @Override
    public void leftTrigger(double d) {
    }

    @Override
    public void rightTrigger(double d) {
        if (controller != null) {
            controller.vibrate(0, ((d == 1) ? 20000 : 0));
        }
        shootyShootShoot = (d == 1);
    }

    @Override
    public void leftThumbMagnitude(double d) {
        thrustMag = d;
        if (world.editMode) {
            hardPoints = chassis.getHardpoints();
        }
    }

    @Override
    public void leftThumbDirection(double d) {
        thrustDir = d * Math.PI / 180;
    }

    @Override
    public void rightThumbMagnitude(double d) {
        turnMag = d;
    }

    @Override
    public void rightThumbDirection(double d) {
        turnDir = d * Math.PI / 180;
    }

    @Override
    public void isConnected(boolean bln) {
    }

    /**
     * Get the design file of this ship
     *
     * @return the design file
     */
    public FileE getDesign() {
        return shipDesign;
    }

    /**
     * Handles the controls and state of the player ship. To be called once per
     * game loop.
     */
    public void onUpdate() {
        //    chassis.color = new java.awt.Color(Game.r.nextFloat(),Game.r.nextFloat(),Game.r.nextFloat());
//        if (rumble>0)
//        {
//            controller.vibrate(rumble, (shootyShootShoot?20000:0));
//            rumble -= 200;
//        }
        chassis.go();
        if (world.editMode) {
            components.stream().filter(c -> c.integrity < c.max_integrity).forEach((c) -> {
                c.integrity++;
            });
            if (Math.abs(turnMag * Math.cos(turnDir)) > 0.25) {
                if (holdTime++ > holdMax) {
                    partSel = partSel - (int) Math.signum(Math.cos(turnDir));
                    holdTime = 0;
                    if (holdMax > 10) {
                        holdMax -= 10;
                    }
                    if (partSel >= attachables.size()) {
                        partSel = 0;
                    }
                    if (partSel < 0) {
                        partSel = attachables.size() - 1;
                    }
                }
            } else {
                holdMax = 60;
                holdTime = holdMax;
            }
            if (thrustMag > 0.2) {
                cursX += thrustMag * Math.sin(thrustDir) * 2;
                cursY -= thrustMag * Math.cos(thrustDir) * 2;
            }
            double minD = 99999;
            double xxx = cursX + chassis.x;
            double yyy = cursY + chassis.y;
            for (Coordinate C : hardPoints) {
                C.isSelected = false;
                double xx = C.master.x + C.getRX(C.master.orientation);
                double yy = C.master.y + C.getRY(C.master.orientation);
                if ((xx - (xxx)) * (xx - (xxx)) + (yy - (yyy)) * (yy - (yyy)) < minD) {
                    minD = (xx - (xxx)) * (xx - (xxx)) + (yy - (yyy)) * (yy - (yyy));
                    selected = C;
                }
            }
            if (selected != null) {
                selected.isSelected = true;
            }
            if (kill) {
                minD = 99999;
                Component mustDie = null;
                for (Component c : components) {
                    if (c.disTo(xxx, yyy) < minD) {
                        minD = c.disTo(xxx, yyy);
                        mustDie = c;
                    }
                }
                if (mustDie != null) {
                    mustDie.integrity -= 5;
                }
            }
        } else {
            if (chassis.integrity <= 0) {
                components.stream().forEach((c) -> {
                    world.explosion(c.x, c.y, c.xspeed, c.yspeed, c.mass / 2 + 32);
                    if (c instanceof Hull && c != chassis) {
                        Hull h = (Hull) c;
                        h.locations.clear();
                    }
                });
                chassis.integrity = chassis.max_integrity;
                chassis.mass = 250;
                world.components.removeAll(components);
                components.clear();
                chassis.pieces.clear();
                components.add(chassis);
                world.components.add(chassis);
                world.loadPlayerShip(this);
                world.modifiedShip(components);
            }

        }

    }

    public final void handleComp(Component c) {
        if (world.editMode) {
            return;
        }
        switch (c.type) {
            case WARP:
                if (isWarping) {
                    ((WarpCore) c).warp(this);
                }
            case THRUSTER:
                final double turnP = turnMag * Math.sin(turnDir);
                c.active = false;
                double twist = c.MASTER.torque(c) * turnP * c.rStrength;
                double rAngle = Math.abs(c.angleDif(c.MASTER.angleDif(c), thrustDir));
                if (c.rStrength == 0) {
                    c.rStrength = 1;
                }
                if (thrustMag > .2 && rAngle < Math.PI / 3) {
                    c.active = thrustMag > .2;
                    c.MASTER.angularv += twist / (5 * c.MASTER.mass);
                    c.MASTER.xspeed += c.rStrength * thrustMag * 2 * Math.cos(c.orientation) / (c.MASTER.mass);
                    c.MASTER.yspeed += c.rStrength * thrustMag * 2 * Math.sin(c.orientation) / (c.MASTER.mass);
                }
                if (twist > 0 && turnMag>0.2) {
                    c.active = Math.abs(turnP) > .2;
                    c.MASTER.angularv += 2 * twist * Math.signum(Math.sin(turnDir)) / (1 + (c.MASTER.MoI / 3000));
                    c.MASTER.xspeed += thrustMag * 2 * Math.cos(c.orientation) / (c.MASTER.mass);
                    c.MASTER.yspeed += thrustMag * 2 * Math.sin(c.orientation) / (c.MASTER.mass);
                }
                break;
            case GUN:
                if (shootyShootShoot) {
                    Gun g = ((Gun) c);
                    c.active = false;
                    if ((g.firingGroup&firingGroup)>0||g.firingGroup==0)
                    {
                        g.shoot(world, me - 1);
                    }
                }
                break;
            case MOTOR:
                c.active = false;
                Motor e = (Motor) c;
                if (((e.firingGroup&firingGroup)>0||e.firingGroup==0))
                {
                    if (mLeft) {
                        e.myRotation += .02 * e.rev;
                    }
                    if (mRight) {
                        e.myRotation -= .02 * e.rev;
                    }
                    if (mLeft && mRight) {
                        e.myRotation /= 1.25;
                    }
                }
                break;
        }

    }

    /**
     * Draw HUD for Build Mode
     *
     * @param g - the OpenGL object to draw with
     */
    public void render(GL2 g) {
        if (world.editMode) {
            attachables.stream().forEach((c) -> {
                c.render(g, 0, 0);
            });
            g.glBegin(GL.GL_LINE_LOOP);
            g.glColor3f(1, 0, 0);
            double ydy = attachables.get(partSel).y;
            g.glVertex2d(80, ydy - 8);
            g.glVertex2d(64, ydy);
            g.glVertex2d(80, 8 + ydy);
            g.glEnd();
            g.glBegin(GL.GL_LINE_LOOP);
            g.glColor3f(1, 0, 0);
            g.glVertex2d(cursX + chassis.x - vX - 10, cursY + chassis.y - vY);
            g.glVertex2d(cursX + chassis.x - vX, cursY - 10 + chassis.y - vY);
            g.glVertex2d(cursX + 10 + chassis.x - vX, cursY + chassis.y - vY);
            g.glVertex2d(cursX + chassis.x - vX, cursY + 10 + chassis.y - vY);
            g.glEnd();
        }
    }

    public static void initAttachables() {
        if (attachablesInitialized) {
            return;
        }
        int yy = 16;
        Armor a = new Armor(32, yy, 8, 32);
        yy += 32;
        a.orientation = Math.PI / 2;
        attachables.add(a);
        Cloak c = new Cloak(32, yy);
        yy += 32;
        attachables.add(c);
        Gun g = new Gun(32, yy);
        yy += 32;
        attachables.add(g);
        Lance l = new Lance(64, yy);
        yy += 32;
        attachables.add(l);
        Laser la = new Laser(32, yy);
        yy += 32;
        attachables.add(la);
        Motor m = new Motor(32, yy, 16, 16, 1);
        yy += 32;
        attachables.add(m);
        Motor m2 = new Motor(32, yy, 16, 16, -1);
        yy += 32;
        attachables.add(m2);
        PhotonicCannon p = new PhotonicCannon(32, yy);
        yy += 32;
        attachables.add(p);
        PlasmaThruster pt = new PlasmaThruster(32, yy);
        yy += 32;
        attachables.add(pt);
        RealPhotonicCannon r = new RealPhotonicCannon(32, yy);
        yy += 32;
        attachables.add(r);
        Thruster th = new Thruster(32, yy);
        yy += 32;
        attachables.add(th);
        WarpCore wc = new WarpCore(32, yy);
        yy += 32;
        attachables.add(wc);
        RectangularHull rh = new RectangularHull(32, yy, 32, 16);
        yy += 48;
        attachables.add(rh);
        RectangularHull rh2 = new RectangularHull(32, yy, 48, 16);
        yy += 48;
        attachables.add(rh2);
        RectangularHull rh3 = new RectangularHull(32, yy, 16, 32);
        yy += 64;
        attachables.add(rh3);
        RectangularHull rh4 = new RectangularHull(32, yy, 16, 48);
        yy += 64;
        attachables.add(rh4);
        RectangularHull rh5 = new RectangularHull(32, yy, 16, 16);
        yy += 64;
        attachables.add(rh5);
        TriangularHull t = new TriangularHull(32, yy, 16);
        yy += 64;
        attachables.add(t);
        Saw s = new Saw(32, yy);
        yy += 64;
        attachables.add(s);
        BentHull bh = new BentHull(32, yy, 45d / 180d * Math.PI);
        yy += 48;
        attachables.add(bh);
        BentHull2 bh2 = new BentHull2(32, yy, 45d / 180d * Math.PI);
        yy += 48;
        attachables.add(bh2);
        bh = new BentHull(32, yy, 30d / 180d * Math.PI);
        yy += 48;
        attachables.add(bh);
        bh2 = new BentHull2(32, yy, 30d / 180d * Math.PI);
        yy += 48;
        attachables.add(bh2);
        TractorBeam tb = new TractorBeam(32, yy);
        yy += 48;
        attachables.add(tb);
    }

    public void setShip(FileE shipDesign) {
        this.shipDesign = shipDesign;
    }

    public void setRumble(int rumble) {
        this.rumble = rumble;
    }
}
