import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Vector;
import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Die einzigen aktiven Akteure in der Roboterwelt sind die Roboter.
 * Die Welt besteht aus 14 * 10 Feldern.
 */

public class Planet extends World
{
    private static int zellenGroesse = 5;
    public static int roverAnzahl = 2;
    
    //Länge und breite des "Bildschirms" aus Feldern
    public static int ScreenH = 200;
    public static int ScreenW = 300;
    
    ArrayList<Rover> rovers = new ArrayList<>();
    ArrayList<Thread> roverThreads = new ArrayList<>();
    
    public static boolean[] finished = new boolean[roverAnzahl];

    /**
     * Erschaffe eine Welt mit 15 * 12 Zellen.
     */
    public Planet()
    {
        super(ScreenW, ScreenH, zellenGroesse);
        setBackground("images/boden5.png");
        setPaintOrder(String.class, Rover.class, Marke.class, Gestein.class, Huegel.class);
        Greenfoot.setSpeed(2000000); 
        prepare();
        finished[0] = false;
        
        for(int i=0;i<finished.length;i++) {
            finished[i] = true;
        }
        
        for(int i = 0;i<roverAnzahl;i++) {
            rovers.add(new Rover());
            addObject(rovers.get(rovers.size()-1),ScreenW/roverAnzahl*i,0);
            
            roverThreads.add(new Thread(new RoverController(rovers.get(rovers.size()-1),i)));
            roverThreads.get(roverThreads.size()-1).start();
        }
        /*
        Thread roverThread1 = new Thread();
        Thread roverThread2 = new Thread();
        
        roverThread1.start();
        roverThread2.start();*/
    }

    
    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare()
    {
    }
}