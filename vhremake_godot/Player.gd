















/**
 * The Player object class, which handles their ship and the corresponding
 * controls. Built to make code more modular and to prevent an array of
 * Component ArrayLists.
 *
 * @version 0.01, 02/20/15
 * @author John
 */
extends Node2D
class_name Player

    private static var attachables = []
    private static var attachablesInitialized = false
    var pNum = 1, xNum = 1, cNum = 1
    var me
    var chassis
    var world
    //var cloaks = 0
    protected int var holdMax = 30
    var components
    var controller
    var shootyShootShoot = false
    private double thrustDir, thrustMag, turnDir, turnMag, var cursY
    protected double var vY
    private boolean mLeft, mRight, isListening, var kill
    var shipDesign
    var rumble = 0, chassisDesign = 0
    var firingGroup = 0x1
    var selected
    var partSel = 0
    var hardPoints = []

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
    func Player(Game world, ChooseShips.FileE shipDesign):
        me = pNum++;
        this.world = world;
        this.shipDesign = shipDesign;
        chassis = new Chassis();
        components = [];
        components.add(chassis);

    }

    func init():
        if (!isListening) {
            controller = new XboxController(xNum);
            if (controller.isConnected()) {
                controller.addXboxControllerListener(this);
                xNum++;
            } else {
                if (cNum <= JInputInvoker.conNum()) {
                    var ok = new JInputInvoker()
                    ok.setController(cNum);
                    ok.addListener(this);
                    var me = new Thread(ok)
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
    func getChassis():
        var chassis
    }

    /**
     * A setter for the Chassis
     *
     * @param c - the Chassis to replace the player's current one
     */
    func overwriteChassis(Chassis c):
        chassis = c;
    }

    @Override
    func buttonA(boolean bln):
        if (world.editMode && bln) {
            if (selected != null) {
                try {
                    var adding = (Component) attachables.get(partSel).clone()
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
            var shipSaver = new JFileChooser(System.getProperty("user.dir"))
            shipSaver.setFileFilter(func FileFilter():

                @Override
                func accept(File pathname):
                    return (pathname.getAbsolutePath().toLowerCase().endsWith(".shp"));
                }

                @Override
                func getDescription():
                    return "Ship Files";
                }
            });
            if (JFileChooser.APPROVE_OPTION == shipSaver.showSaveDialog(world.tab.getTabComponentAt(0))) {
                var swag = shipSaver.getSelectedFile().getAbsolutePath()
                if (!swag.toLowerCase().endsWith(".shp")) {
                    swag += ".shp";
                }
                world.saveShip(this, new File(swag));
                world.paused = false;
            }
        }
    }

    @Override
    func buttonB(boolean bln):
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
    func buttonX(boolean bln):
        kill = bln;
        if (world.paused && bln) {
            world.tab.setSelectedIndex(0);
        }
    }

    @Override
    func buttonY(boolean bln):
        isWarping = bln;
    }

    @Override
    func back(boolean bln):
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
                        for (var i = chassis.shape.xpoints[0] + 8 i <= chassis.shape.xpoints[1] - 8; i += 16) {
                            chassis.locations.add(new Coordinate(i, chassis.shape.ypoints[0], 3 * Math.PI / 2, chassis));
                            chassis.locations.add(new Coordinate(i, chassis.shape.ypoints[2], Math.PI / 2, chassis));
                        }
                        for (var i = chassis.shape.ypoints[0] + 8 i <= chassis.shape.ypoints[2] - 8; i += 16) {
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
    func start(boolean bln):
        if (bln) {
            world.paused = !world.paused;
        }
    }

    @Override
    func leftShoulder(boolean bln):
        mLeft = bln;
    }

    @Override
    func rightShoulder(boolean bln):
        mRight = bln;
    }

    @Override
    func leftThumb(boolean bln):
    }

    @Override
    func rightThumb(boolean bln):
    }

    @Override
    func dpad(int i, boolean bln):
        firingGroup = (byte)((1<<((i-(i&1))/2))|(1<<((i+(i&1))/2)) % 15);
    }

    @Override
    func leftTrigger(double d):
    }

    @Override
    func rightTrigger(double d):
        if (controller != null) {
            controller.vibrate(0, ((d == 1) ? 20000 : 0));
        }
        shootyShootShoot = (d == 1);
    }

    @Override
    func leftThumbMagnitude(double d):
        thrustMag = d;
        if (world.editMode) {
            hardPoints = chassis.getHardpoints();
        }
    }

    @Override
    func leftThumbDirection(double d):
        thrustDir = d * Math.PI / 180;
    }

    @Override
    func rightThumbMagnitude(double d):
        turnMag = d;
    }

    @Override
    func rightThumbDirection(double d):
        turnDir = d * Math.PI / 180;
    }

    @Override
    func isConnected(boolean bln):
    }

    /**
     * Get the design file of this ship
     *
     * @return the design file
     */
    func getDesign():
        var shipDesign
    }

    /**
     * Handles the controls and state of the player ship. To be called once per
     * game loop.
     */
    func onUpdate():
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
            var minD = 99999
            var xxx = cursX + chassis.x
            var yyy = cursY + chassis.func for(Coordinate C : hardPoints):
                C.isSelected = false;
                var xx = C.master.x + C.getRX(C.master.orientation)
                var yy = C.master.y + C.getRY(C.master.orientation)
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
                var mustDie = func for(Component c : components):
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
                        var h = (Hull) c
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

    public final func handleComp(Component c):
        if (world.editMode) {
            return;
        }
        switch (c.type) {
            case WARP:
                if (isWarping) {
                    ((WarpCore) c).warp(this);
                }
            case THRUSTER:
                var turnP = turnMag * Math.sin(turnDir)
                c.active = false;
                var twist = c.MASTER.torque(c) * turnP * c.rStrength
                var rAngle = Math.abs(c.angleDif(c.MASTER.angleDif(c), thrustDir))
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
                    var g = ((Gun) c)
                    c.active = false;
                    if ((g.firingGroup&firingGroup)>0||g.firingGroup==0)
                    {
                        g.shoot(world, me - 1);
                    }
                }
                break;
            case MOTOR:
                c.active = false;
                var e = (Motor) func if(((e.firingGroup&firingGroup)>0||e.firingGroup==0)):
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
    func render(GL2 g):
        if (world.editMode) {
            attachables.stream().forEach((c) -> {
                c.render(g, 0, 0);
            });
            g.glBegin(GL.GL_LINE_LOOP);
            g.glColor3f(1, 0, 0);
            var ydy = attachables.get(partSel).y
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

    func initAttachables():
        if (attachablesInitialized) {
            return;
        }
        var yy = 16
        var a = new Armor(32, yy, 8, 32)
        yy += 32;
        a.orientation = Math.PI / 2;
        attachables.add(a);
        var c = new Cloak(32, yy)
        yy += 32;
        attachables.add(c);
        var g = new Gun(32, yy)
        yy += 32;
        attachables.add(g);
        var l = new Lance(64, yy)
        yy += 32;
        attachables.add(l);
        var la = new Laser(32, yy)
        yy += 32;
        attachables.add(la);
        var m = new Motor(32, yy, 16, 16, 1)
        yy += 32;
        attachables.add(m);
        var m2 = new Motor(32, yy, 16, 16, -1)
        yy += 32;
        attachables.add(m2);
        var p = new PhotonicCannon(32, yy)
        yy += 32;
        attachables.add(p);
        var pt = new PlasmaThruster(32, yy)
        yy += 32;
        attachables.add(pt);
        var r = new RealPhotonicCannon(32, yy)
        yy += 32;
        attachables.add(r);
        var th = new Thruster(32, yy)
        yy += 32;
        attachables.add(th);
        var wc = new WarpCore(32, yy)
        yy += 32;
        attachables.add(wc);
        var rh = new RectangularHull(32, yy, 32, 16)
        yy += 48;
        attachables.add(rh);
        var rh2 = new RectangularHull(32, yy, 48, 16)
        yy += 48;
        attachables.add(rh2);
        var rh3 = new RectangularHull(32, yy, 16, 32)
        yy += 64;
        attachables.add(rh3);
        var rh4 = new RectangularHull(32, yy, 16, 48)
        yy += 64;
        attachables.add(rh4);
        var rh5 = new RectangularHull(32, yy, 16, 16)
        yy += 64;
        attachables.add(rh5);
        var t = new TriangularHull(32, yy, 16)
        yy += 64;
        attachables.add(t);
        var s = new Saw(32, yy)
        yy += 64;
        attachables.add(s);
        var bh = new BentHull(32, yy, 45d / 180d * Math.PI)
        yy += 48;
        attachables.add(bh);
        var bh2 = new BentHull2(32, yy, 45d / 180d * Math.PI)
        yy += 48;
        attachables.add(bh2);
        bh = new BentHull(32, yy, 30d / 180d * Math.PI);
        yy += 48;
        attachables.add(bh);
        bh2 = new BentHull2(32, yy, 30d / 180d * Math.PI);
        yy += 48;
        attachables.add(bh2);
        var tb = new TractorBeam(32, yy)
        yy += 48;
        attachables.add(tb);
    }

    func setShip(FileE shipDesign):
        this.shipDesign = shipDesign;
    }

    func setRumble(int rumble):
        this.rumble = rumble;
    }
}
