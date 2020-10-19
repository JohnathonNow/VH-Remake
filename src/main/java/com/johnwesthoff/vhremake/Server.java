/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.johnwesthoff.vhremake;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;

/**
 *
 * @author John
 */
public class Server implements Runnable{
    ArrayList<client> players;
    
    public static void main(String args[])
    {
        Thread me = new Thread(new Server());
       // me.start();
//        System.load(Server.class.getClassLoader().getResource("jinput-dx8_64.dll").getPath());
//        System.load(Server.class.getClassLoader().getResource("jinput-raw_64.dll").getPath());
        Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for(int i =0;i<ca.length;i++){

            /* Get the name of the controller */
            if (ca[i].getType()==Controller.Type.STICK||ca[i].getType()==Controller.Type.GAMEPAD)
            {
                for (int b = 0; b < ca[i].getComponents().length; b++)
                {
                    System.out.println(ca[i].getComponents()[b].getName());
                }
             }
        }
     
        
    }
    ServerSocket ss;
    @Override
    public void run() {
        try {
            players = new ArrayList<>();
            ss = new ServerSocket(19254);
            while (true)
            {
                players.add(new client(ss.accept()));
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private class client implements Runnable
    {
        Socket listener;
        public client(Socket listener)
        {
            this.listener = listener;
            Thread me = new Thread(this);
            me.start();
        }
        @Override
        public void run() {
            while (true)
            {
                
            }
        }
        
    }
}
