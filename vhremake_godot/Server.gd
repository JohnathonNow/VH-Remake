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
class_name Server
    var players

    func main(String args[]):
        var me = new Thread(new Server())
       // me.start();
//        System.load(Server.class.getClassLoader().getResource("jinput-dx8_64.dll").getPath());
//        System.load(Server.class.getClassLoader().getResource("jinput-raw_64.dll").getPath());
        var ca = ControllerEnvironment.getDefaultEnvironment().getControllers()

        for(var i = 0i<ca.length;i++){

            /* Get the name of the controller */
            if (ca[i].getType()==Controller.Type.STICK||ca[i].getType()==Controller.Type.GAMEPAD)
            {
                for (var b = 0 b < ca[i].getComponents().length; b++)
                {
                    print(ca[i].getComponents()[b].getName());
                }
             }
        }


    }
    var ss
    @Override
    func run():
        try {
            players = [];
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
        var listener
        func client(Socket listener):
            this.listener = listener;
            var me = new Thread(this)
            me.start();
        }
        @Override
        func run():
            while (true)
            {

            }
        }

    }
}
