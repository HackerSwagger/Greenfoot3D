import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Vector;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Rover extends Actor
{
    
    public int ind = 0;
    int turn = 0;
    /**
     * Diese Funktion wird jedes mal ausgeführt wenn eine der Steuerungstasten betätigt wird. Das
     * löst dann eine erneute berechnung von allem auf dem Bildschirm aus, sowie auch eine neue Abfahrt
     * des Bildschirms durch den Rover.
     */
    
    public synchronized void buttonPress(ArrayList<Painter.triangle> tris,boolean drawTris,double[][] xRotation,double[][] zRotation,double[][] projectionMatrix,double rotX,double rotZ,double dist, String info2) {
        
        //Liste der Dreiecke die auf die Koordinaten projiziert wurden
        ArrayList<Painter.triangle> projTris = new ArrayList<>();
        //Alte Liste zur Speicherung der Punkte, nurnoch für Kompatibilität
        //der Füllfunktion vorhanden.
        ArrayList<Painter.Vec3> triPs = new ArrayList<>();
        Painter.projectTriangles(tris,projTris,zRotation,xRotation,projectionMatrix,rotX,rotZ,dist,info2);
        
        /** Ein 2D Array aus Integern der in den Dimensionen der Höhe bzw. Breite des
        * Bildschirms entspricht. Wenn eine Marke auf einer Koordinate gesetzt werden
        * muss, so beträgt der entsprechende Wert im Array 1. So ist eine wesentlich
        * schnellere und effizientere Abrage des Bildschirms möglich als zuvor, da einfach
        * die x-/y-Position überprüft werden kann. Ich frage mich nur weshalb ich nicht
        * früher auf diese eigentlich recht naheliegende Lösung gekommen bin.
        */
        int[][] points = Painter.points(projTris, drawTris);
        
        for (int i = 0; i < points.length; i++) {
 
            // Loop through all elements of current row
            for (int j = 0; j < points[i].length; j++) {
                //System.out.print(points[i][j] + " ");
            }
        }
       
        turn=0;
        //Rover durchfährt jede Reihe-
        for(int i=0;i<Planet.ScreenH-1;i+=1) {
            //-sowie jede Spalte des Feldes
            for(int j=0;j<(Planet.ScreenW/Planet.roverAnzahl)-2;j+=1) {
                //Marken werden immer entfernt, sodass keine Spur alter "Frames"
                //zurückbleibt.
                try {
                    entferneMarke();
                } catch (Exception e) {
                    //e.printStackTrace();
                } 
                if(!drawTris) {
                    //Durchläuft die Liste der Eckpunkte des Objektes, nur relevant wenn
                    //Dreiecke nicht gezeichnet werden und kostet nur sehr wenig Performance.
                    //Wenn ein Punkt mit der Position übereinstimmt wird eine Marke gesetzt
                    for(int d=0;d<projTris.size();d+=1) {
                        if(getX() == (int)projTris.get(d).c1.xp &&
                            getY() == (int)projTris.get(d).c1.yp &&
                            !markeVorhanden() ||
                            getX() == (int)projTris.get(d).c2.xp &&
                            getY() == (int)projTris.get(d).c2.yp &&
                            !markeVorhanden() ||
                            getX() == (int)projTris.get(d).c3.xp &&
                            getY() == (int)projTris.get(d).c3.yp && !markeVorhanden()) {
                            setzeMarke();
                            }
                    }
                }
                //Überprüfung ob der Wert der Position positiv ist
                if(points[getX()][getY()] == 1) {
                    setzeMarke();
                }
                fahre();
            }
            //Am Ende einer Reihe wendet der Rover
            if(turn==0) {
                drehe("rechts");
                fahre();
                drehe("rechts");
                turn+=1;
            } else {
                drehe("links");
                fahre();
                drehe("links");
                turn-=1;
            }
        }
        //Der Rover kehrt zur Ausgangsposition zurück und ist
        //für den nächsten durchlauf bereit
        for(int i=0;i<Planet.ScreenW/Planet.roverAnzahl-2;i+=1) {
            fahre();
        }
        drehe("rechts");
        for(int i=0;i<Planet.ScreenH-1;i+=1) {
            fahre();
        }
        drehe("rechts");
    }
    
    //String fName ,double dist,double rotSpeed, double maxIterations, boolean drawTris
    int ind2 = 0;
    /**
     * Dies ist das Hauptprogramm von welchem alle anderen Funktionen ausgehen. Es kann einfach
     * durch klicken von "Play" gestartet und dann nach Belieben gesteuert werden.
     */
    double rotX = 1;
    public synchronized void acting() 
    {
        //Für den Fall mehrerer Ausführungen werden die Werte angafnags
        //zurückgesetzt.
        double iter = 0;
        int xp = 0;
        int yp = 0;
        int cubeWidth = 20;
        //double rotX = 1;
        double rotZ = 1;
        
        //Nötige Liste der 3D-Dreiecke, entweder manuell definiert oder aus Datei importiert.
        ArrayList<Painter.triangle> tris = new ArrayList<>();
        
        //Weitere notwendige Werte zur Ausführung die ursprünglich beim Ausführen eingestellt werden konnten.
        //Mit der implementierung von Tastenkontrollen wurde dies jedoch überflüssig und act() erlaubt auch keine Argumente
        String fName = new String();double dist = 3.5;double rotSpeed = .1; double maxIterations = 1000; boolean drawTris = false;String info2 = new String();boolean repeat = true;
        
        //Durch die Konsole werden nötige Werte wie der Dateiname und
        //die Info ob gezeichnet werden soll eingeholt. Dreiecke nicht
        //zu zeichnen ist wesentlich effizienter und sehr schnell,
        //sieht jedoch um einiges weniger befriedigend aus.
        Scanner in = new Scanner(System.in);
        
        /**Hier muss User-Input eingefügt werden**/
        //System.out.println(".obj Dateinamen eingeben:");
        fName = "Cube.obj";//in.nextLine();
        
        
        /**Hier muss User-Input eingefügt werden**/
        //System.out.println("Sollen Dreiecke gezeichnet werden(y/n)?");
        String info = "y";//in.nextLine();
        
        //Übername der Werte, dür die Dateinamen wird entweder
        //die zugehörige Datei importiert oder bei der Angabe "normal"
        //der manuell definierte Testwürfel.
        if(info.equals("y")) {
            drawTris = true;
        } else {
            System.out.println("Sollen Normale ignoriert werden(y/n)?");
            info2 = in.nextLine();
            drawTris = false;
        }
        
        tris = Painter.meshTris(fName);
        
        //Matrix zur Anpassung in 3 dimensionalen Raum
        double[][] projectionMatrix = new double[4][4];
        //Matrix zur Speicherung der Rotation auf der x-Achse
        double[][] xRotation = new double[4][4];
        //Matrix zur Speicherung der Rotation auf der z-Achse
        double[][] zRotation = new double[4][4];
        
        //nötige Variablen für die Projektionsmatrix, z.B. Entfernung zum Bildschirm, Tiefe, Bildschirmgröße etc.
        double near = .1;
        double far = 2000;
        double fov = 90; //Sichtfeld in °
        double fovRad = 1.0/Math.tan(Math.toRadians(fov/2));
        double aR = (double)200/(double)300;
        
        //Werte in die Matrix einfügen, alle nicht benötigten sind gleich 0
        //Natürlich habe ich mir diese nicht selbst ausgedacht, das haben
        //netterweise schlauere Menschen schon früher übernommen. Die Art wie
        //sie berechnet wird ist jedoch sehr interessant und ein weiterer guter
        //Grund das oben genannte Video anzuschauen.
        projectionMatrix[0][0] = aR*fovRad;
        projectionMatrix[0][1] = 0;
        projectionMatrix[0][2] = 0;
        projectionMatrix[0][3] = 0;
        projectionMatrix[1][0] = 0;
        projectionMatrix[1][1] = fovRad;
        projectionMatrix[1][2] = 0;
        projectionMatrix[1][3] = 0;
        projectionMatrix[2][0] = 0;
        projectionMatrix[2][1] = 0;
        projectionMatrix[2][2] = far/(far-near);
        projectionMatrix[2][3] = 1.0;
        projectionMatrix[3][0] = 0;
        projectionMatrix[3][1] = 0;
        projectionMatrix[3][2] = (-far*near)/(far-near);
        projectionMatrix[3][3] = 0;
        
        
        //Quasi der "Main-Loop", zu Anfang durchlief das Programm immer eine bestimmte
        //Menge "Frames" bzw. einen Abfahrtszyklus des Rovers. Das war mir jedoch zu wenig
        //interaktiv, weswegen man jetzt mit den Pfeiltasten die x bzw. z Rotation manipulieren
        //kann. So muss der Rover auch immer nur dann den Bildschirm neu abfahren wenn eine Taste
        //betätigt wird. Mit "W" und "S" kann man sich auch dem Objekt nähern, bzw. sich entfernen.
        //while(iter < maxIterations) {
            //if(Greenfoot.isKeyDown("up")) {
                
                //Werte werden je nach Knopfdruck angepasst
                iter+=1;
                rotX-=rotSpeed;
                
                buttonPress(tris,drawTris,xRotation,zRotation,projectionMatrix,rotX,rotZ,dist,info2);
            //}
            
            /*if(Greenfoot.isKeyDown("down")) {
                iter+=1;
                rotX+=rotSpeed;
                buttonPress(tris,drawTris,xRotation,zRotation,projectionMatrix,rotX,rotZ,dist,info2);
            }
            
            if(Greenfoot.isKeyDown("left")) {
                iter+=1;
                rotZ-=rotSpeed;
                buttonPress(tris,drawTris,xRotation,zRotation,projectionMatrix,rotX,rotZ,dist,info2);
            } 
            
            if(Greenfoot.isKeyDown("right")) {
                iter+=1;
                rotZ+=rotSpeed;
                buttonPress(tris,drawTris,xRotation,zRotation,projectionMatrix,rotX,rotZ,dist,info2);
            } 
            
            if(Greenfoot.isKeyDown("w")) {
                iter+=1;
                dist-=rotSpeed;
                buttonPress(tris,drawTris,xRotation,zRotation,projectionMatrix,rotX,rotZ,dist,info2);
            } 
            
            if(Greenfoot.isKeyDown("s")) {
                iter+=1;
                dist+=rotSpeed;
                buttonPress(tris,drawTris,xRotation,zRotation,projectionMatrix,rotX,rotZ,dist,info2);
            }
            if(Greenfoot.isKeyDown("escape")) {
                iter = maxIterations;
                Greenfoot.stop();
                break;
            }*/
        //}
    }


    /**
     * Der Rover bewegt sich ein Feld in Fahrtrichtung weiter.
     * Sollte sich in Fahrtrichtung ein Objekt der Klasse Huegel befinden oder er sich an der Grenze der Welt befinden,
     * dann erscheint eine entsprechende Meldung auf dem Display.
     */
    public void fahre()
    {
        int posX = getX();
        int posY = getY();

        if(huegelVorhanden("vorne"))
        {
            nachricht("Zu steil!");
        }
        else if(getRotation()==270 && getY()==1)
        {
            nachricht("Ich kann mich nicht bewegen");
        }
        else
        {
            move(1);
            Greenfoot.delay(1);
        }

        if(posX==getX()&&posY==getY()&&!huegelVorhanden("vorne"))
        {
            nachricht("Ich kann mich nicht bewegen");
        }
    }

    /**
     * Der Rover dreht sich um 90 Grad in die Richtung, die mit richtung („links“ oder „rechts“) übergeben wurde.
     * Sollte ein anderer Text (String) als "rechts" oder "links" übergeben werden, dann erscheint eine entsprechende Meldung auf dem Display.
     */
    public void drehe(String richtung)
    {
        if(richtung=="rechts")
        {
            setRotation(getRotation()+90);
        }
        else if (richtung=="links")
        {
            setRotation(getRotation()-90);
        }
        else
        {
            nachricht("Befehl nicht korrekt!");
        }
    }

    /**
     * Der Rover gibt durch einen Wahrheitswert (true oder false )zurück, ob sich auf seiner Position ein Objekt der Klasse Gestein befindet.
     * Eine entsprechende Meldung erscheint auch auf dem Display.
     */
    public boolean gesteinVorhanden()
    {
        if(getOneIntersectingObject(Gestein.class)!=null)
        {
            nachricht("Gestein gefunden!");
            return true;

        }

        return false;
    }

    /**
     * Der Rover überprüft, ob sich in richtung ("rechts", "links", oder "vorne") ein Objekt der Klasse Huegel befindet.
     * Das Ergebnis wird auf dem Display angezeigt.
     * Sollte ein anderer Text (String) als "rechts", "links" oder "vorne" übergeben werden, dann erscheint eine entsprechende Meldung auf dem Display.
     */
    public boolean huegelVorhanden(String richtung)
    {
        int rot = getRotation();

        if (richtung=="vorne" && rot==0 || richtung=="rechts" && rot==270 || richtung=="links" && rot==90)
        {
            if(getOneObjectAtOffset(1,0,Huegel.class)!=null && ((Huegel)getOneObjectAtOffset(1,0,Huegel.class)).getSteigung() >30)
            {
                return true;
            }
        }

        if (richtung=="vorne" && rot==180 || richtung=="rechts" && rot==90 || richtung=="links" && rot==270)
        {
            if(getOneObjectAtOffset(-1,0,Huegel.class)!=null && ((Huegel)getOneObjectAtOffset(-1,0,Huegel.class)).getSteigung() >30)
            {
                return true;
            }
        }

        if (richtung=="vorne" && rot==90 || richtung=="rechts" && rot==0 || richtung=="links" && rot==180)
        {
            if(getOneObjectAtOffset(0,1,Huegel.class)!=null && ((Huegel)getOneObjectAtOffset(0,1,Huegel.class)).getSteigung() >30)
            {
                return true;
            }

        }

        if (richtung=="vorne" && rot==270 || richtung=="rechts" && rot==180 || richtung=="links" && rot==0)
        {
            if(getOneObjectAtOffset(0,-1,Huegel.class)!=null && ((Huegel)getOneObjectAtOffset(0,-1,Huegel.class)).getSteigung() >30)
            {
                return true;
            }

        }

        if(richtung!="vorne" && richtung!="links" && richtung!="rechts")
        {
            nachricht("Befehl nicht korrekt!");
        }

        return false;
    }

    /**
     * Der Rover ermittelt den Wassergehalt des Gesteins auf seiner Position und gibt diesen auf dem Display aus.
     * Sollte kein Objekt der Klasse Gestein vorhanden sein, dann erscheint eine entsprechende Meldung auf dem Display.
     */
    public void analysiereGestein()
    {
        if(gesteinVorhanden())
        {
            nachricht("Gestein untersucht! Wassergehalt ist " + ((Gestein)getOneIntersectingObject(Gestein.class)).getWassergehalt()+"%.");
            Greenfoot.delay(1);
            removeTouching(Gestein.class);
        }
        else 
        {
            nachricht("Hier ist kein Gestein");
        }
    }

    /**
     * Der Rover erzeugt ein Objekt der Klasse „Markierung“ auf seiner Position.
     */
    public void setzeMarke()
    {
        getWorld().addObject(new Marke(), getX(), getY());
    }

    /**
     * *Der Rover gibt durch einen Wahrheitswert (true oder false )zurück, ob sich auf seiner Position ein Objekt der Marke befindet.
     * Eine entsprechende Meldung erscheint auch auf dem Display.
     */
    public boolean markeVorhanden()
    {
        if(getOneIntersectingObject(Marke.class)!=null)
        {
            return true;
        }

        return false;
    }

    public synchronized void entferneMarke()
    {
        if(markeVorhanden())
        {
            removeTouching(Marke.class);
        }
    }
    
    Display anzeige;

    private void nachricht(String pText)
    {
        if(anzeige!=null)
        {
            anzeige.anzeigen(pText);
            Greenfoot.delay(1);
            anzeige.loeschen();
        }
    }

    private void displayAusschalten()
    {
        getWorld().removeObject(anzeige);

    }

    protected void addedToWorld(World world)
    {

        setImage("images/rover5.png");
        world = getWorld();
        //anzeige = new Display();
        //anzeige.setImage("images/nachricht.png");
        //world.addObject(anzeige, 7, 0);
        if(getY()==0)
        {
            setLocation(getX(),1);
        }
        //anzeige.anzeigen("Ich bin bereit");

    }

    class Display extends Actor
    {
        GreenfootImage bild; 

        public Display()
        {
          bild = getImage();
        }

        public void act() 
        {

        }  

        public void anzeigen(String pText)
        {
           loeschen();
           getImage().drawImage(new GreenfootImage(pText, 25, Color.BLACK, new Color(0, 0, 0, 0)),10,10);

        }

        public void loeschen()
        {
            getImage().clear();
            setImage("images/nachricht.png");
        }

    }
    public void NotizenStorage() {
        //Sortierung der Punkte zur Ermöglichung effizienterer Suche (momentan ausgesetzt, da mir was besseres eingefallen ist)
        /*while (repeat) {
            repeat=false;
            for(int i=0;i<triPs.size()-1;i++) {
                if(triPs.get(i).xp > triPs.get(i+1).xp) {
                    repeat=true;
                    Vec3 thisV = triPs.get(i);
                    Vec3 nextV = triPs.get(i+1);
                    triPs.set(i,nextV);
                    triPs.set(i+1,thisV);
                }
            }
        }
        repeat=true;
        while (repeat) {
            repeat=false;
            for(int i=0;i<triPs.size()-1;i++) {
                if(triPs.get(i).yp > triPs.get(i+1).yp && (int)triPs.get(i).xp == (int)triPs.get(i+1).xp) {
                    repeat=true;
                    Vec3 thisV = triPs.get(i);
                    Vec3 nextV = triPs.get(i+1);
                    triPs.set(i,nextV);
                    triPs.set(i+1,thisV);
                }
            }
        }
        repeat=true;*/
    }
}
