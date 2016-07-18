/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import java.awt.Polygon;
import java.util.ArrayList;

/**
 *
 * @author John
 */
public class TextRenderer {
    static ArrayList<Polygon> font = new ArrayList<>();
    public static void init()
    {
        Polygon shape = new Polygon();
        shape.addPoint(0,-25);
        shape.addPoint(0,-25);
        shape.addPoint(-17,14);
        shape.addPoint(-12,14);
        shape.addPoint(0,-16);
        shape.addPoint(5,0);
        shape.addPoint(-6,0);
        shape.addPoint(-7,3);
        shape.addPoint(6,3);
        shape.addPoint(9,14);
        shape.addPoint(14,14);
        font.add(shape);
        shape = new Polygon();
        shape.addPoint(-20,-23);
        shape.addPoint(13,-23);
        shape.addPoint(15,-20);
        shape.addPoint(15,-17);
        shape.addPoint(14,-15);
        shape.addPoint(-14,-15);
        shape.addPoint(-14,-4);
        shape.addPoint(14,-4);
        shape.addPoint(16,-1);
        shape.addPoint(16,2);
        shape.addPoint(15,4);
        shape.addPoint(-14,4);
        shape.addPoint(-14,12);
        shape.addPoint(-14,13);
        shape.addPoint(14,13);
        shape.addPoint(16,16);
        shape.addPoint(16,17);
        shape.addPoint(16,18);
        shape.addPoint(16,19);
        shape.addPoint(15,20);
        shape.addPoint(-20,20);

    }
}
