/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.johnwesthoff.vhremake;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import com.johnwesthoff.vhremake.ChooseShips.FileE;
import com.johnwesthoff.vhremake.Components.Chassis;
import com.johnwesthoff.vhremake.Components.Component;
import com.johnwesthoff.vhremake.Components.Gun;
import com.johnwesthoff.vhremake.Components.Hull;
import com.johnwesthoff.vhremake.Components.RectangularHull;
import com.johnwesthoff.vhremake.Components.Saw;
import com.johnwesthoff.vhremake.Components.Thruster;
import com.johnwesthoff.vhremake.Components.WarpCore;
import com.johnwesthoff.vhremake.Entities.Asteroid;
import com.johnwesthoff.vhremake.Entities.Bullet;
import com.johnwesthoff.vhremake.Entities.Entity;
import com.johnwesthoff.vhremake.Entities.Esplosion;
import com.johnwesthoff.vhremake.Entities.Nebula;
import com.johnwesthoff.vhremake.Entities.Star;

/**
 *
 * @author John
 */
public final class Game implements Runnable, MouseListener, MouseMotionListener, KeyListener {

    public int mX, mY;
    public final ArrayList<Component> components;
    public final ArrayList<Player> players;
    public final CopyOnWriteArrayList<Bullet> bullets;
    public final ArrayList<Asteroid> asteroids;
    public final CopyOnWriteArrayList<Entity> effects;
    public final ArrayList<Entity> stars;
    public static double pulse, counter = 0;
    public final int PLAYERS = 2;
    public static int width = 960, height = 1080;
    public static int pCount = 1;
    public static Random r = new Random();
    Component held = null;
    Component nearest = null;
    boolean keys[] = new boolean[32000];
    double offX, offY;
    double scale = 1;
    int FPS = 0, DFPS = 0;
    Polygon toDraw2Nearest = new Polygon();
    JTabbedPane tab;
    long time = 0;
    boolean paused = false;
    boolean editMode = false;
    public double deltaT = 1.0;
    public static void main(String[] args) {
        JFrame myFrame = new JFrame();
        Game theGame = new Game();
        //myFrame.add(theGame);
        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);
        theGame.tab = new JTabbedPane();
        theGame.tab.setUI(new BasicTabbedPaneUI() {
            @Override
            protected int calculateTabAreaHeight(int tab_placement, int run_count, int max_tab_height) {
                return 0;
            }
        });
        ChooseShips choo = new ChooseShips();
        JPanel s = new JPanel();
        theGame.tab.setSize(1920, 1080);
        s.setSize(1920, 1080);
        s.setLayout(null);
        s.add(glcanvas[0], BorderLayout.CENTER);
        s.add(glcanvas[1], BorderLayout.CENTER);
        s.setDoubleBuffered(false);
        glcanvas[0].setSize(width, height);
        glcanvas[1].setSize(width, height);
        glcanvas[1].setLocation(width, 0);

        theGame.tab.addTab("Chooser", choo);
        theGame.tab.addTab("Game", s);
        choo.addActionListener((ActionEvent e) -> {
            if ("go".equals(e.getActionCommand())) {
                theGame.tab.setSelectedIndex(1);
                theGame.loadShips(choo.getShips());
                theGame.goLimo();
            }
        });
        myFrame.getContentPane().add(theGame.tab);
        myFrame.setSize(1920, 1080);

        myFrame.getContentPane().setLayout(null);

        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setUndecorated(true);
        myFrame.setVisible(true);
        myFrame.addMouseMotionListener(theGame);
        myFrame.addMouseListener(theGame);
        myFrame.addKeyListener(theGame);
        myFrame.setLocation(0, 0);
        myFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        Player.initAttachables();
    }
    static GLCanvas glcanvas[];

    public void loadShips(ChooseShips.FileE ships[]) {
        if (firstRun) {
            for (int i = 0; i < PLAYERS; i++) {
                players.add(new Player(this, ships[i]));
                components.add(players.get(i).getChassis());
            }
        }
        for (int i = 0; i < PLAYERS; i++) {
            players.get(i).setShip(ships[i]);
            loadPlayerShip(players.get(i));
        }
    }
    boolean go = false;

    public Game() {
        components = new ArrayList<>();
        asteroids = new ArrayList<>();
        bullets = new CopyOnWriteArrayList<>();
        effects = new CopyOnWriteArrayList<>();
        stars = new ArrayList<>();
        players = new ArrayList<>();
        TimerTask swag = new TimerTask() {

            @Override
            public void run() {
                if (glcanvas != null && glcanvas[0] != null && glcanvas[0].getAnimator() != null) {
                    System.out.printf("~~~%s~~~\n\tLFPS: %d\n\tDFPS: %f\n\tEffects: %d\n", (new Date()).toString(), FPS, glcanvas[0].getAnimator().getTotalFPS(), effects.size());
                    glcanvas[0].getAnimator().resetFPSCounter();
                }
                FPS = 0;
                DFPS = 0;
            }

        };
        (new Timer()).schedule(swag, 0, 1000);
        for (int b = 0; b < 5; b++) {
            double cX = r.nextInt(6000) - 600, cY = r.nextInt(6000) - 600;
            for (int i = 0; i < 300; i++) {
                stars.add(new Nebula(cX, cY, 90 + 90 * r.nextFloat()));
                cX += 90 * Math.cos(r.nextFloat() * Math.PI * 2);
                cY += 90 * Math.sin(r.nextFloat() * Math.PI * 2);
            }
        }
        for (int i = 0; i < 600; i++) {
            stars.add(new Star((r.nextInt(6000)) - 600, r.nextInt(6000) - 600, 5 * r.nextFloat(), 1 + 5 * r.nextFloat()));
        }

        // player = loadShip(importants,player);
        for (int i = 0; i < 100; i++) {
            asteroids.add(new Asteroid((r.nextInt(6000)) - 600, r.nextInt(6000) - 600, 0, 0, 64));
        }
        GLProfile glprofile = GLProfile.get(GLProfile.GL2);
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        glcapabilities.setDoubleBuffered(true);
        glcanvas = new GLCanvas[]{new GLCanvas((GLCapabilities) glcapabilities), new GLCanvas((GLCapabilities) glcapabilities)};
        glcanvas[0].addGLEventListener(new GLEventListener() {

            @Override
            public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height) {
                setup(glautodrawable.getGL().getGL2(), width, height);
            }

            @Override
            public void init(GLAutoDrawable glautodrawable) {
                FPSAnimator animator = new FPSAnimator( glcanvas[0], 120);
                animator.start();
                animator.setUpdateFPSFrames(60, null);
                
            }

            @Override
            public void dispose(GLAutoDrawable glautodrawable) {
            }

            @Override
            public void display(GLAutoDrawable glautodrawable) {
                update();
                //for (int i = 0; i < 4; i++)
                    render(glautodrawable.getGL().getGL2(), 0,0,glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight(), 0);
            }
        });
        glcanvas[1].addGLEventListener(new GLEventListener() {

            @Override
            public void reshape(GLAutoDrawable glautodrawable, int x, int y, int width, int height) {
                setup(glautodrawable.getGL().getGL2(), width, height);
            }

            @Override
            public void init(GLAutoDrawable glautodrawable) {
                FPSAnimator animator = new FPSAnimator( glcanvas[1], 60);
                animator.start();
                animator.setUpdateFPSFrames(1, null);
            }

            @Override
            public void dispose(GLAutoDrawable glautodrawable) {
            }

            @Override
            public void display(GLAutoDrawable glautodrawable) {
                render(glautodrawable.getGL().getGL2(), 0,0,glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight(), 1);
            }
        });
    }

    public void saveShip(Player p, File f) {
        FileOutputStream fos;
        ObjectOutputStream oos;
        try {
            File out = f;
            fos = new FileOutputStream(out);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(p.components);
            fos.close();
            oos.close();
            Thread ugh = new Thread(() -> {
                try {
                    int person = p.me - 1;
                    Robot r1;
                    r1 = new Robot();
                    Rectangle rect = glcanvas[person].getBounds();
                    double cX = rect.getCenterX();
                    double cY = rect.getCenterY();
                    rect = new Rectangle((int) cX - 200, (int) cY - 200, 400, 400);
                    ImageIO.write(r1.createScreenCapture(rect), "png", new File(out.getAbsolutePath() + ".png"));
                } catch (Exception ex) {
                    Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            ugh.start();
            p.setShip(new FileE(out.getAbsolutePath()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads the ship described in a file.
     *
     * @param p - the player whose ship will be overridden
     * @param f - the enhanced file to load from
     * @see vhremake.ChooseShips.FileE
     */
    public void loadShip(Player p, FileE f) {
        FileInputStream fis;
        ObjectInputStream ois;
        try {
            File out = f;
            fis = new FileInputStream(out);
            ois = new ObjectInputStream(fis);
            components.removeAll(p.components);
            p.components.clear();
            p.components.addAll((ArrayList<Component>) ois.readObject());
            components.addAll(p.components);
            p.components.stream().sorted();
            components.sort(null);
            p.overwriteChassis((Chassis) p.components.get(0));
            p.init();
            fis.close();
            ois.close();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        p.components.stream().forEach((c) -> {
            c.fixIt();
        });
        p.getChassis().calculateCOM();
    }
    boolean firstRun = true;

    private void goLimo() {
        if (firstRun) {
            glcanvas[0].addKeyListener(this);
            glcanvas[0].addMouseMotionListener(this);
            glcanvas[0].addMouseListener(this);
                        glcanvas[1].addKeyListener(this);
            glcanvas[1].addMouseMotionListener(this);
            glcanvas[1].addMouseListener(this);
            Thread gameThread = new Thread(this);
            gameThread.start();
            firstRun = false;
        }
        paused = false;
//        addMouseMotionListener(this);
//        addMouseListener(this);
//        addKeyListener(this);

    }

    @Override
    public void run() {

//        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
//        while (true) {
//           // update();
//            try {
//                long b = System.nanoTime() - time;
//                //if (b<25)
//                while (b <= 16666666) {
//                    b = System.nanoTime() - time;
//                }
//                FPS++;
//                time = System.nanoTime();
//            } catch (Exception ex) {
//
//            }
//        }
    }

    private void update() {
        FPS++;
        deltaT = 60/glcanvas[0].getAnimator().getLastFPS();
        try {
            if (!paused) {

                pulse = Math.sin(counter += Math.PI / 200);
                for (int i = 0; i < effects.size(); i++) {
                    Entity e = effects.get(i);
                    e.move();
                    if (e.hp <= 0) {
                        effects.remove(i);
                    }
                }
                for (int i = 0; i < bullets.size(); i++) {
                    Bullet e = bullets.get(i);
                    if (bullets.get(i).hp <= 0) {
                        bullets.remove(i);
                        i--;
                        continue;
                    }
                    e.move();
                    for (int it = 0; it < PLAYERS; it++) {
                        if (e.team == it) {
                            continue;
                        }
                        players.get(it).components.stream().filter(d -> Math.abs(d.x - e.x) < 30 && Math.abs(d.y - e.y) < 10).filter((c) -> (c.collisionPoint(e.x, e.y))).forEach((c) -> {
                            c.damage(3);
                            bullets.remove(e);
                            e.hp = 0;
                        });
                    }
                    if (e.hp <= 0) {
                        bullets.remove(i);
                    }
                }

                for (int i = 0; i < asteroids.size(); i++) {
                    Entity e = asteroids.get(i);
                    if (e.hp <= 0) {
                        asteroids.remove(i);
                        i--;
                        continue;
                    }
                    e.move();
//                    if (e.x > vX + 600 || e.x < vX || e.y > vY + 600 || e.y < vY) {
//                        continue;
//                    }

                    Asteroid a = (Asteroid) e;
                    for (int it = 0; it < PLAYERS; it++) {
                        players.get(it).components.stream().filter(d -> Math.abs(d.x - a.x) < a.radius * 2 && Math.abs(d.y - a.y) < a.radius * 2).forEach((c) -> {
                            double dx, dy, angl, dis, rx, ry;
                            dis = Math.min(a.radius, Math.sqrt((a.x - c.x) * (a.x - c.x) + (a.y - c.y) * (a.y - c.y)));
                            angl = Math.atan2(a.y - c.y, a.x - c.x);
                            dx = a.x - Math.cos(angl) * dis;
                            dy = a.y - Math.sin(angl) * dis;
                            rx = Math.cos(angl);
                            ry = Math.sin(angl);
                            if (c.collisionPoint(dx, dy)) {
                                a.xspeed += rx / 8;
                                a.yspeed += ry / 8;
                                c.damage(3);
                                if (c.MASTER != null) {
                                    c.MASTER.xspeed -= rx / 8;
                                    c.MASTER.yspeed -= ry / 8;
                                }
                            }
                        });
//                            players.get(it).components.stream().forEach((c) -> {
//                                double dx, dy, angl, dis, rx, ry;
//                                dis = Math.min(a.radius, Math.sqrt((a.x - c.x) * (a.x - c.x) + (a.y - c.y) * (a.y - c.y)));
//                                angl = Math.atan2(a.y - c.y, a.x - c.x);
//                                dx = a.x - Math.cos(angl) * dis;
//                                dy = a.y - Math.sin(angl) * dis;
//                                rx = Math.cos(angl);
//                                ry = Math.sin(angl);
//                                if (c.collisionPoint(dx, dy)) {
//                                    a.xspeed += rx / 8;
//                                    a.yspeed += ry / 8;
//                                    c.damage(3);
//                                    if (c.MASTER != null) {
//                                        c.MASTER.xspeed -= rx / 8;
//                                        c.MASTER.yspeed -= ry / 8;
//                                    }
//                                }
//                            });
                    }
                    for (int ia = 0; ia < bullets.size(); ia++) {
                        Entity c = bullets.get(ia);
                        if (a != c && Math.abs(c.x - a.x) < a.radius && Math.abs(c.y - a.y) < a.radius) {
                            if (c.type == Entity.TYPE.BULLET) {
                                a.xspeed += c.xspeed / 500f;
                                a.yspeed += c.yspeed / 500f;
                                double a1, a2, a3, m;
                                a1 = Math.atan2(c.yspeed, c.xspeed);
                                a2 = Math.atan2(a.y - c.y, a.x - c.x);
                                a3 = Math.atan2(Math.sin(a1 - a2), Math.cos(a1 - a2));
                                m = Math.sin(a3) * Math.sqrt((c.x - a.x) * (c.x - a.x) + (c.y - a.y) * (c.y - a.y));
                                a.angularv -= m / 1000000f;
                                bullets.remove(ia);
                            }
                        }
                    }
                    asteroids.stream().map((asteroid) -> (Asteroid) asteroid).filter((c) -> (a != c && Math.abs(c.x - a.x) < a.radius + c.radius && Math.abs(c.y - a.y) < a.radius + c.radius)).filter((c) -> (c.type == Entity.TYPE.DANGER)).map((c) -> {
                        a.xspeed += (a.x - c.x) / 100;
                        return c;
                    }).forEach((c) -> {
                        a.yspeed += (a.y - c.y) / 100;
                    });
                }
                try {
                    for (int it = 0; it < PLAYERS; it++) {
                        for (Component c : players.get(it).components) {
                            players.get(it).handleComp(c);
                        }
                        players.get(it).onUpdate();
                        players.get(it).components.stream().forEach((c) -> {
                            c.fixIt();
                        });

                        for (Component c : players.get(it).components) {
                            for (int ib = it + 1; ib < PLAYERS; ib++) {
                                players.get(ib).components.stream().filter(d -> Math.abs(d.x - c.x) < 30 && Math.abs(d.y - c.y) < 30).forEach((a) -> {
                                    double dx, dy, angl, rx, ry, dis2;
                                    dis2 = c.mass / 100 + a.mass / 100;//Math.sqrt((a.x - c.x) * (a.x - c.x) + (a.y - c.y) * (a.y - c.y));
                                    angl = Math.atan2(a.y - c.y, a.x - c.x);
                                    rx = Math.cos(angl);
                                    ry = Math.sin(angl);
                                    dx = a.x - rx * dis2;
                                    dy = a.y - ry * dis2;
                                    if (c.collisionPoint(dx, dy)) {
                                        c.damage(3);
                                        a.damage(3);
                                        if (c.type == Component.TYPE.MELEE) {
                                            a.damage(10);
                                        } else {
                                            if (c.MASTER != null) {
                                                c.MASTER.xspeed -= rx / 3;
                                                c.MASTER.yspeed -= ry / 3;
                                            }
                                        }
                                        if (a.type == Component.TYPE.MELEE) {
                                            c.damage(10);
                                        } else {
                                            if (a.MASTER != null) {
                                                a.MASTER.xspeed += rx / 3;
                                                a.MASTER.yspeed += ry / 3;
                                            }
                                        }
                                    }
                                });
                            }
                            c.obeyMaster(this);

                            if (c.integrity < 0) {
                                c.disown();
                                if (c.holder != null) {
                                    c.holder.pieces.remove(c);
                                    c.mine.occupied = false;
                                    c.holder = null;

                                    modifiedShip(players.get(it).components);

                                }
                                explosion(c.x, c.y, c.xspeed, c.yspeed, c.mass / 2 + 32);
                                components.remove(c);
                            }
                        }
                    }
                } catch (ConcurrentModificationException cme) {

                }

                if (editMode) {
                    double minDis = 100;
                    double dis;
                    if (held == null) {
                        nearest = null;
                    }
                    for (int i = 0; i < components.size(); i++) {
                        Component c = components.get(i);
                        c.obeyMaster(this);
                        if (c.integrity < 0) {
                            c.disown();
                            if (c.holder != null) {
                                c.holder.pieces.remove(c);
                                c.mine.occupied = false;
                                c.holder = null;

                            }
                            explosion(c.x, c.y, c.xspeed, c.yspeed, c.mass / 2 + 32);
                            components.remove(c);
                        }
                        if (held == null) {
                            c.nearest = false;
                            dis = Math.sqrt((mX - c.x) * (mX - c.x) + (mY - c.y) * (mY - c.y));
                            if (dis < minDis && !(c instanceof Chassis)) {
                                nearest = c;
                                c.nearest = true;
                                minDis = dis;
                            }
                        }
                    }
                }
            }
            if (held != null) {
                held.x = mX + offX;
                held.y = mY + offY;
                for (Component c : components) {
                    if (c == held) {
                        continue;
                    }
                    if (c instanceof Hull && c.onMaster) {
                        Hull h = (Hull) c;
                        for (Coordinate co : h.locations) {
                            if (co.occupied) {
                                continue;
                            }
                            if (Math.abs(mX - (co.getRX(h.orientation) + h.x)) < 10) {
                                if (Math.abs(mY - (co.getRY(h.orientation) + h.y)) < 10) {
                                    held.x = h.x + co.getRX(h.orientation) + held.getAX(held.orientation - Math.PI);
                                    held.y = h.y + co.getRY(h.orientation) + held.getAY(held.orientation - Math.PI);
                                    held.orientation = co.angle + h.orientation + Math.PI;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            mX -= players.get(0).vX;
            mY -= players.get(0).vY;
            for (int i = 0; i < PLAYERS; i++) {
                players.get(i).vX = (int) (players.get(i).getChassis().x) - width / 2;
                players.get(i).vY = (int) (players.get(i).getChassis().y) - height / 2;
            }
            mX += players.get(0).vX;
            mY += players.get(0).vY;
//                glcanvas[0].repaint();
//                glcanvas[1].repaint();
//                    glcanvas[0].display();
//                glcanvas[1].display();

        } catch (Exception e) {
            //Don't let an exception ruin everything
        }
    }

    protected static void setup(GL2 gl2, int width, int height) {
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();

        // coordinate system origin at lower left with width and height same as the window
        GLU glu = new GLU();
        glu.gluOrtho2D(0.0f, width, 0.0f, height);

        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();

        gl2.glViewport(0, 0, width, height);
    }

    static int x = 0;

    protected void render(GL2 gl2, int xxx, int yyy, int width, int height, int pNum) {
        gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

        // draw a triangle filling the window
        //gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        gl2.glMatrixMode(GL2.GL_PROJECTION);

        gl2.glScaled(scale, scale, 1);

        gl2.glOrtho(xxx, // left
                xxx+width, // right
                yyy+height, // bottom
                yyy, // top
                0, // zNear
                1 // zFar
        );
        gl2.glEnable(GL2.GL_BLEND);
        gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        //gl2.glOrtho(0, width, 0, height, -1.0f, 1.0f);
        //gl2.glTranslated(50, 0, 0);

        double vX2 = players.get(pNum).vX;
        double vY2 = players.get(pNum).vY;
        gl2.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
        try {
            stars.stream().forEach((e) -> {
                try {
                    e.render(gl2, vX2, vY2);
                } catch (Exception loser) {
                    loser.printStackTrace();
                }
            });
        } catch (Exception e) {

        }
        try {
            effects.stream().filter((e) -> (e.x > vX2 - 12 && e.x < vX2 + width + 12)).filter((e) -> (e.y > vY2 - 12 && e.y < vY2 + height + 12)).forEach((e) -> {
                try {
                    e.render(gl2, vX2, vY2);
                } catch (Exception loser) {
                    loser.printStackTrace();
                }
            });
        } catch (Exception e) {

        }
        try {
            bullets.stream().filter((e) -> (e.x > vX2 - 300 && e.x < vX2 + width + 300)).filter((e) -> (e.y > vY2 - 300 && e.y < vY2 + height + 300)).forEach((e) -> {
                try {
                    e.render(gl2, vX2, vY2);
                } catch (Exception loser) {
                    loser.printStackTrace();
                }
            });
        } catch (Exception e) {

        }
        gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        try {
            asteroids.stream().filter((e) -> (e.x > vX2 - 300 && e.x < vX2 + width + 300)).filter((e) -> (e.y > vY2 - 300 && e.y < vY2 + height + 300)).forEach((e) -> {
                try {
                    e.render(gl2, vX2, vY2);
                } catch (Exception loser) {
                    loser.printStackTrace();
                }
            });
        } catch (Exception e) {

        }
        try {
            components.stream().filter((c) -> (c.x > vX2 - 300 && c.x < vX2 + width + 300) && !c.cloaked).filter((c) -> (c.y > vY2 - 300 && c.y < vY2 + height + 300)).forEach((c) -> {
                try {
                    c.render(gl2, vX2, vY2);
                } catch (Exception loser) {
                    System.err.println(loser.getMessage());
                }
            });
        } catch (Exception e) {

        }
        
        players.get(pNum).render(gl2);
        gl2.glFlush();
    }

    public boolean isSpace() {
        return this.keys[KeyEvent.VK_SPACE];
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    boolean mHeld = false;

    @Override
    public void mousePressed(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        assignMouse(e);
        switch (e.getButton()) {

            case MouseEvent.BUTTON1:
                mHeld = true;
                //players.get(0).components.stream().forEach((c) -> {c.event(mX, mY, Component.EventType.CLICK);});
                break;
            case MouseEvent.BUTTON3:
                if (nearest != null) {
                    held = nearest;
                    nearest.disown();
                    if (nearest.holder != null) {
                        nearest.holder.pieces.remove(nearest);
                        nearest.mine.occupied = false;
                        nearest.holder = null;
                        modifiedShip();
                    }
                    nearest.xspeed = 0;
                    nearest.yspeed = 0;
                    nearest.angularv = 0;
                    nearest.held = true;
                    nearest.holder = null;
                    offX = 0;//nearest.x - mX;
                    offY = 0;//nearest.y - mY;
                }
                break;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e
    ) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mHeld = false;
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            if (held != null) {
                held.held = false;
                main:
                for (Component c : components) {
                    if (c == held) {
                        continue;
                    }
                    if (c instanceof Hull && c.onMaster) {
                        Hull h = (Hull) c;
                        for (Coordinate co : h.locations) {
                            if (co.occupied) {
                                continue;
                            }
                            if (Math.abs(mX - (co.getRX(h.orientation) + h.x)) < 10) {
                                if (Math.abs(mY - (co.getRY(h.orientation) + h.y)) < 10) {
                                    attachPiece(held, h, co);
                                    break main;
                                }
                            }
                        }
                    }
                    //       
                }

            }
        }
        held = null;
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public Component attachPiece(Component a, Hull h, Coordinate co) {
        a.holder = h;
        a.mine = co;
        co.occupied = true;
        if (a.holder != null) {
            a.holder.pieces.add(a);
        }
        held = null;
        nearest = null;
        a.findMaster();
        a.fixIt();
        modifiedShip();
        a.score = getScore(a);
        players.get(0).components.stream().sorted();
        components.sort(null);
        return a;
    }

    public Component attachPiece(Component a, Hull h, Coordinate co, ArrayList<Component> al) {
        a.holder = h;
        a.mine = co;
        co.occupied = true;
        if (a.holder != null) {
            a.holder.pieces.add(a);
        }
        held = null;
        nearest = null;
        a.findMaster();
        a.fixIt();
        modifiedShip(al);
        a.score = getScore(a);
        al.stream().sorted();
        components.sort(null);
        return a;
    }

    public int getScore(Component c) {
        return (c.holder == null ? 0 : getScore(c.holder) + 1);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        assignMouse(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        assignMouse(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        assignMouse(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        assignMouse(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        if (held != null && e.getKeyCode() == KeyEvent.VK_M) {
            held.event(mX, mY, Component.EventType.MOD);
        }
        if (e.getKeyCode() == KeyEvent.VK_B) {

        }

        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void modifiedShip() {
        modifiedShip(players.get(0).components);
    }

    public void modifiedShip(ArrayList<Component> al) {
        Chassis p = (Chassis) al.get(0);
        p.getParts(al);
        p.calculateCOM();
    }

    private void assignMouse(MouseEvent e) {
        mX = (int) ((e.getX()) / scale + players.get(0).vX + (width / 2 * scale - width / 2) / scale);//+600*scale-600);
        mY = (int) ((e.getY()) / scale + players.get(0).vY + (height / 2 * scale - height / 2) / scale);//+600*scale-600);
        scale = 1;
    }

    public void explosion(double x, double y, double vx, double vy, int s) {
        s = (int) (Math.log1p(s)) + 1;
        for (int i = 0; i < s * 2; i++) {
            effects.add(new Esplosion(x + s / 2 - r.nextInt(s), y + s / 2 - r.nextInt(s), vx, vy));
        }
        for (int it = 0; it < 2; it++) {
            players.get(it).setRumble(20000);
        }
    }

    /**
     * Loads the default player into this player object
     *
     * @param p - the player to load
     */
    public void loadDefault(Player p) {
        Hull bob = p.getChassis();
        components.add(attachPiece(new WarpCore(0, 0), bob, (bob).locations.get(4), p.components));
        components.add(attachPiece(new WarpCore(0, 0), bob, (bob).locations.get(5), p.components));
        components.add(attachPiece(new WarpCore(0, 0), bob, (bob).locations.get(3), p.components));
        components.add(attachPiece(new WarpCore(0, 0), bob, (bob).locations.get(2), p.components));
        components.add(attachPiece(new WarpCore(0, 0), bob, (bob).locations.get(0), p.components));
        RectangularHull rh = new RectangularHull(0, 0, 16, 48);
        components.add(attachPiece(rh, (bob), (bob).locations.get(1), p.components));
        components.add(attachPiece(new Thruster(0, 0), rh, rh.locations.get(3), p.components));
        components.add(attachPiece(new Thruster(0, 0), rh, rh.locations.get(6), p.components));
        components.add(attachPiece(new Thruster(0, 0), rh, rh.locations.get(0), p.components));
        components.add(attachPiece(new Thruster(0, 0), rh, rh.locations.get(1), p.components));
        components.add(attachPiece(new Saw(0, 0), rh, rh.locations.get(4), p.components));
        components.add(attachPiece(new Gun(0, 0), rh, rh.locations.get(5), p.components));
        components.add(attachPiece(new Gun(0, 0), rh, rh.locations.get(2), p.components));
        p.components.stream().forEach((c) -> {
            c.fixIt();
        });
        p.getChassis().calculateCOM();
    }

    public void loadPlayerShip(Player p) {
        p.init();
        if (p.getDesign().toString().equals("CHOOSE\n")) {
            loadDefault(p);
        } else {
            this.loadShip(p, p.getDesign());
        }
    }
}
