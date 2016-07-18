/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utils;

import ch.aplu.xboxcontroller.XboxControllerListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.Component;
/**
 *
 * @author John
 */
public class JInputInvoker implements Runnable{
    XboxControllerListener xcl;
    int it;
    public static int conNum()
    {
        Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
        int n = 0;
        for (Controller ca1 : ca) {
            if (ca1.getType() == Controller.Type.STICK) {
                n++;
            }
        }
        return n;
    }
    @Override
    public void run() {
        Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();
        int a = it;
        int real = 0;
        for (int i = 0; i < ca.length; i ++)
        {
            if (ca[i].getType()==Controller.Type.STICK)
            {
                if ((--a)<=0)
                {
                    real = i;
                    break;
                }
            }
        }
        while (true)
        {
            if (real<0||real>=ca.length)
                continue;
            ca[real].poll();
            Event e = new Event();
            
            while (ca[real].getEventQueue().getNextEvent(e))
            {
                if (e.getComponent().getIdentifier()==Component.Identifier.Axis.X||e.getComponent().getIdentifier()==Component.Identifier.Axis.Y)
                {
                    xcl.leftThumbMagnitude(Math.sqrt(Math.pow(ca[real].getComponent(Component.Identifier.Axis.X).getPollData(),2)+Math.pow(ca[real].getComponent(Component.Identifier.Axis.Y).getPollData(),2)));
                    xcl.leftThumbDirection(90+180f/Math.PI*Math.atan2(ca[real].getComponent(Component.Identifier.Axis.Y).getPollData(), ca[real].getComponent(Component.Identifier.Axis.X).getPollData()));
                }
                if (e.getComponent().getIdentifier()==Component.Identifier.Axis.RX||e.getComponent().getIdentifier()==Component.Identifier.Axis.RY)
                {
                    xcl.rightThumbMagnitude(Math.sqrt(Math.pow(ca[real].getComponent(Component.Identifier.Axis.RX).getPollData(),2)+Math.pow(ca[real].getComponent(Component.Identifier.Axis.RY).getPollData(),2)));
                    xcl.rightThumbDirection(90+180f/Math.PI*Math.atan2(ca[real].getComponent(Component.Identifier.Axis.RY).getPollData(), ca[real].getComponent(Component.Identifier.Axis.RX).getPollData()));
                }
                if (e.getComponent().getIdentifier()==Component.Identifier.Axis.Z)
                {
                  if (e.getValue()>0)
                  {
                      xcl.leftTrigger(Math.min(e.getValue()*1.2,1));
                  }
                  else
                  {
                      xcl.rightTrigger(Math.abs(Math.max(e.getValue()*1.2,-1)));
                  }
                }
                if (e.getComponent().getIdentifier()==Component.Identifier.Button._0)
                {
                    xcl.buttonA(e.getValue()==1.0f);
                }
                if (e.getComponent().getIdentifier()==Component.Identifier.Button._1)
                {
                    xcl.buttonB(e.getValue()==1.0f);
                }
                if (e.getComponent().getIdentifier()==Component.Identifier.Button._2)
                {
                    xcl.buttonX(e.getValue()==1.0f);
                }
                if (e.getComponent().getIdentifier()==Component.Identifier.Button._3)
                {
                    xcl.buttonY(e.getValue()==1.0f);
                }
                if (e.getComponent().getIdentifier()==Component.Identifier.Button._7)
                {
                    xcl.start(e.getValue()==1.0f);
                }
                if (e.getComponent().getIdentifier()==Component.Identifier.Button._6)
                {
                    xcl.back(e.getValue()==1.0f);
                }
                if (e.getComponent().getIdentifier()==Component.Identifier.Button._4)
                {
                    xcl.leftShoulder(e.getValue()==1.0f);
                }
                if (e.getComponent().getIdentifier()==Component.Identifier.Button._5)
                {
                    xcl.rightShoulder(e.getValue()==1.0f);
                }
                if (e.getComponent().getIdentifier()==Component.Identifier.Axis.POV)
                {
                    System.out.println(e.getValue());
                    xcl.dpad((int)(e.getValue()*8), e.getValue()!=0);
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                Logger.getLogger(JInputInvoker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    public void setController(int i)
    {
        it = i;
    }
    public void addListener(XboxControllerListener xcl)
    {
        this.xcl = xcl;
    }
}
