import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import java.util.Vector;
import java.util.ArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Painter  
{
    //Anzahl der Umdrehungen die der Rover bereits geleistet hat
    static int turn = 0;
    
    //Klasse zur einfachen bestimmung von Vektoren bzw. Punkten im 3D-Raum
    //Hält x,y und z Koordinaten die zur Positionsbestimmung verwendet werden
    public static class Vec3 {
        //Müssen zur Verwendung in der Vektor-Klasse bestimmt werden
        public double xp;
        public double yp;
        public double zp;
        Vec3(double x, double y, double z) {
            this.xp = x;
            this.yp = y;
            this.zp = z;
        }
    }
    
    //Klasse zur bestimmung der Dreiecke aus denen die 3D-Formen gebildet werden
    //Wird aus 3 Positionsvektoren gebildet
    public static class triangle {
        //Müssen zur Verwendung in der Dreieck-Klasse bestimmt werden
        public Vec3 c1,c2,c3;
        public triangle(Vec3 p1,Vec3 p2, Vec3 p3) {
            this.c1 = p1;
            this.c2 = p2;
            this.c3 = p3;
        }
    }
    
    /**
     * Diese Funktion dient zum importieren von Dateien im .obj Format mithilfe der Scanner-Klasse.
     * Die .obj Dateien enthalten Eckdaten von 3D-Formen die im Format wie die Koordinaten der
     * Vektor-Klasse aufgebaut sind, also in x,y und z. So kann ich diese Daten problemlos
     * in mein vorhandenes Programm integrieren und so jedes belibiege 3D-Modell mit diesem
     * Format anzeigen lassen. Die entnommenen Daten werden erst einzeln in einer Liste Punkte 
     * gespeichert und dann in eine Liste Dreiecke übertragen.
     */
    public static void fileImpTest(String filename, ArrayList<Vec3> verts, ArrayList<triangle> tris) {
        try {
            
          File myObj = new File(filename);
          Scanner myReader = new Scanner(myObj);
          
          while (myReader.hasNextLine()) {
              
            String data = myReader.nextLine();
            
            if(data.split(" ")[0].equals("v")) {
                
                try {
                    double vert1 = Double.parseDouble(data.split(" ")[1]);
                    double vert2 = Double.parseDouble(data.split(" ")[2]);
                    double vert3 = Double.parseDouble(data.split(" ")[3]);
                    if(vert1 >= -1 && vert1 <= 1) {
                        verts.add(new Vec3(vert1,vert2,vert3));
                    }
                } catch(NumberFormatException e) {
                    System.out.println("Not a Number");
                    //break;
                }
            }
            if(data.split(" ")[0].equals("f")) {
                
                try {
                    double f1 = Double.parseDouble(data.split(" ")[1]);
                    double f2 = Double.parseDouble(data.split(" ")[2]);
                    double f3 = Double.parseDouble(data.split(" ")[3]);
                    if(f1 >= 1) {
                        tris.add(new triangle(verts.get((int)(f1-1)),verts.get((int)(f2-1)),verts.get((int)(f3-1))));
                    }
                } catch(NumberFormatException e) {
                    System.out.println("Not a Number");
                }
            }
          }
          myReader.close();
        } catch (FileNotFoundException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
    }
    
    public static ArrayList<triangle> meshTris(String fName) {
        ArrayList<triangle> tris = new ArrayList<>();
        
        if(fName.equals("normal")) {
            //Vorne
            triangle vorne1 = new triangle(new Painter.Vec3(0,0,0),new Vec3(0,1,0),new Vec3(1,1,0));
            tris.add(vorne1);
            triangle vorne2 = new triangle(new Vec3(0,0,0),new Vec3(1,1,0),new Vec3(1,0,0));
            tris.add(vorne2);
            
            //Rechts
            triangle rechts1 = new triangle(new Vec3(1,0,0),new Vec3(1,1,0),new Vec3(1,1,1));
            tris.add(rechts1);
            triangle rechts2 = new triangle(new Vec3(1,0,0),new Vec3(1,1,1),new Vec3(1,0,1));
            tris.add(rechts2);
            
            //Hinten
            triangle hinten1 = new triangle(new Vec3(1,0,1),new Vec3(1,1,1),new Vec3(0,1,1));
            tris.add(hinten1);
            triangle hinten2 = new triangle(new Vec3(1,0,1),new Vec3(0,1,1),new Vec3(0,0,1));
            tris.add(hinten2);
            
            //Links
            triangle links1 = new triangle(new Vec3(0,0,1),new Vec3(0,1,1),new Vec3(0,1,0));
            tris.add(links1);
            triangle links2 = new triangle(new Vec3(0,0,1),new Vec3(0,1,0),new Vec3(0,0,0));
            tris.add(links2);
            
            //Oben
            triangle oben1 = new triangle(new Vec3(0,1,0),new Vec3(0,1,1),new Vec3(1,1,1));
            tris.add(oben1);
            triangle oben2 = new triangle(new Vec3(0,1,0),new Vec3(1,1,1),new Vec3(1,1,0));
            tris.add(oben2);
            
            //Unten
            triangle unten1 = new triangle(new Vec3(1,0,1),new Vec3(0,0,1),new Vec3(0,0,0));
            tris.add(unten1);
            triangle unten2 = new triangle(new Vec3(1,0,1),new Vec3(0,0,0),new Vec3(1,0,0));
            tris.add(unten2);
        } else {
            ArrayList<Vec3> vertices = new ArrayList<>();
            fileImpTest(fName,vertices, tris);
        }
        return tris;
    }
    
    
    
    int c = 0;
    /**
     * Diese Funktion ahmt im Prinzip nur eine Algorithmus zum Zeichnen von Linien mit Pixeln, also eine eigentlich 
     * sehr grundlegende Systemfunktion. Hier berechne ich mithilfe einer erweiterten und abgewandelten
     * Version des einfachsten Linienzeichnungsalgorithmus, wie er hier beschrieben ist: https://en.wikipedia.org/wiki/Line_drawing_algorithm
     * die Punkte der Linie und füge sie einer Liste für alle Punkte zu.
     */
    public synchronized static void drawLine(Vec3 p1, Vec3 p2, int[][] points) {
        
        //Geht verschiedene Möglichkeiten und Spezialfälle durch (z.B. negative
        //Abstände etc.) und zeichnet dann mithilfe der fallspezifisch notwendigen
        //Abwandlung des Algorithmus die Punkte der Linien.
        double dx = p2.xp-p1.xp;
        double dy = p2.yp-p1.yp;
        double mult;
        
        if(dx > 0 && dy > 0) {
            if(dy > dx) {
                mult = dx/dy;
            } else {
                mult = dy/dx;
            }
            if(mult > 0 && mult < 0.001) {
                mult = 0.001;
            } else if(mult < 0 && mult > -0.001) {
                mult = -0.001;
            }
            if(mult > 0) {
                for(double x = p1.xp;x<p2.xp;x+=mult) {
                    double dx2 = x-p1.xp;
                    double y = p1.yp + dy * dx2/dx;
                    if(x < Planet.ScreenW && y < Planet.ScreenH && x > 0 && y > 0) {
                        points[(int)x][(int)y]=1;
                    }
                }
            } else {
                for(double x = p1.xp;x<p2.xp;x-=mult) {
                    double dx2 = x-p1.xp;
                    double y = p1.yp + dy * dx2/dx;
                    if(x < Planet.ScreenW && y < Planet.ScreenH && x > 0 && y > 0) {
                        points[(int)x][(int)y]=1;
                    }
                }
            }
        } else if(dx < 0 && dy < 0) {
            if(dy < dx) {
                mult = dx/dy;
            } else {
                mult = dy/dx;
            }
            if(mult > 0 && mult < 0.001) {
                mult = 0.001;
            } else if(mult < 0 && mult > -0.001) {
                mult = -0.001;
            }
            if(mult > 0) {
                for(double x = p1.xp;x>p2.xp;x-=mult) {
                    double dx2 = x-p1.xp;
                    double y = p1.yp + dy * dx2/dx;
                    if(x < Planet.ScreenW && y < Planet.ScreenH && x > 0 && y > 0) {
                        points[(int)x][(int)y]=1;
                    }
                }
            } else {
                for(double x = p1.xp;x>p2.xp;x+=mult) {
                    double dx2 = x-p1.xp;
                    double y = p1.yp + dy * dx2/dx;
                    if(x < Planet.ScreenW && y < Planet.ScreenH && x > 0 && y > 0) {
                        points[(int)x][(int)y]=1;
                    }
                }
            }
        } else if(dx < 0 && dy > 0) {
            if(dy > dx*-1) {
                mult = dx/dy;
            } else {
                mult = dy/dx;
            }
            if(mult > 0 && mult < 0.001) {
                mult = 0.001;
            } else if(mult < 0 && mult > -0.001) {
                mult = -0.001;
            }
            if(mult > 0) {
                for(double x = p1.xp;x>p2.xp;x-=mult) {
                    double dx2 = x-p1.xp;
                    double y = p1.yp + dy * dx2/dx;
                    if(x < Planet.ScreenW && y < Planet.ScreenH && x > 0 && y > 0) {
                        points[(int)x][(int)y]=1;
                    }
                }
            } else {
                for(double x = p1.xp;x>p2.xp;x+=mult) {
                    double dx2 = x-p1.xp;
                    double y = p1.yp + dy * dx2/dx;
                    if(x < Planet.ScreenW && y < Planet.ScreenH && x > 0 && y > 0) {
                        points[(int)x][(int)y]=1;
                    }
                }
            }
        } else if(dx > 0 && dy < 0) {
            if(dy*-1 > dx) {
                mult = dx/dy;
            } else {
                mult = dy/dx;
            }
            if(mult > 0 && mult < 0.001) {
                mult = 0.001;
            } else if(mult < 0 && mult > -0.001) {
                mult = -0.001;
            }
            if(mult > 0) {
                for(double x = p1.xp;x<p2.xp;x+=mult) {
                    double dx2 = x-p1.xp;
                    double y = p1.yp + dy * dx2/dx;
                    if(x < Planet.ScreenW && y < Planet.ScreenH && x > 0 && y > 0) {
                        points[(int)x][(int)y]=1;
                    }
                }
            } else {
                for(double x = p1.xp;x<p2.xp;x-=mult) {
                    double dx2 = x-p1.xp;
                    double y = p1.yp + dy * dx2/dx;
                    if(x < Planet.ScreenW && y < Planet.ScreenH && x > 0 && y > 0) {
                        points[(int)x][(int)y]=1;
                    }
                }
            }
        }
        //Für gerade Linien
        if(dx == 0) {
            if(p1.yp < p2.yp) {
                for(int i=(int)p1.yp;i<(int)p2.yp;i++) {
                    if(p1.xp < Planet.ScreenW && i < Planet.ScreenH && p1.xp > 0 && i > 0) {
                        points[(int)p1.xp][i]=1;
                    }
                }
            } else {
                for(int i=(int)p2.yp;i>(int)p1.yp;i--) {
                    if(p1.xp < Planet.ScreenW && i < Planet.ScreenH && p1.xp > 0 && i > 0) {
                        points[(int)p1.xp][i]=1;
                    }
                }
            }
        } else if(dy == 0) {
            if(p1.xp < p2.xp) {
                for(int i=(int)p1.xp;i<(int)p2.xp;i++) {
                    if(i < Planet.ScreenW && p1.yp < Planet.ScreenH && p1.xp > 0 && i > 0) {
                        points[i][(int)p1.yp]=1;
                    }
                }
            } else {
                for(int i=(int)p2.xp;i>(int)p1.xp;i--) {
                    if(i < Planet.ScreenW && p1.yp < Planet.ScreenH && p1.xp > 0 && i > 0) {
                        points[i][(int)p1.yp]=1;
                    }
                }
            }
        }
    }
    /**
     * Die Linienfunktion wird für die Punktkombinationen eines Dreiecks angewendet
     */
    public synchronized static void drawTri(Vec3 p1, Vec3 p2, Vec3 p3, int[][] points) {
        //Dieser Vorgang wiederholt sich für alle drei Punkt-Kombinationen
        drawLine(p1,p2,points);
        drawLine(p2,p3,points);
        drawLine(p3,p1,points);
    }
    
    /**
     * Die folgenden Funktionen sind zum füllen der gezeichneten Dreiecke gedacht, funktionieren
     * mit dem vorhandenen System jedoch nicht so gut, dass man sie in ihrer jetzigen Form
     * nutzen könnte. Außerdem fehlt zur Unterscheidung der Seiten dann noch ein notwendiges
     * System für Beleuchtung bzw. variierende Seitenfarben. Die Algorithmen sind Abwandlungen der
     * hier beschrieben Füllfunktionen: http://www.sunshine2k.de/coding/java/TriangleRasterization/TriangleRasterization.html
     */
    public static void fillTopFlat(Vec3 p1, Vec3 p2, Vec3 botP, int[][] points) {
        double steigung1 = (botP.xp - p1.xp) / (botP.yp - p1.yp);
        double steigung2 = (botP.xp - p2.xp) / (botP.yp - p2.yp);
        
        double curx1 = botP.xp;
        double curx2 = botP.xp;
        
        for (int scanlineY = (int)botP.yp; scanlineY > p1.yp; scanlineY-=1) {
            for(int i=(int)curx1;i<curx2;i+=1) {
                points[i][scanlineY] = 1;
            }
            curx1 -= steigung1;
            curx2 -= steigung2;
        }
    }
    
    public static void fillBotFlat(Vec3 topP, Vec3 p2, Vec3 p3, int[][] points) {
        double steigung1 = (p2.xp - topP.xp) / (p2.yp - topP.yp);
        double steigung2 = (p3.xp - topP.xp) / (p3.yp - topP.yp);
        
        double curx1 = topP.xp;
        double curx2 = topP.xp;
        
        for (int scanlineY = (int)topP.yp; scanlineY <= p2.yp; scanlineY+=1) {
            for(int i=(int)curx2;i>curx1;i-=1) {
                points[i][scanlineY] = 1;
            }
            curx1 += steigung1;
            curx2 += steigung2;
        }
    }
    
    public static void fillTri(Vec3 p1, Vec3 p2, Vec3 p3, int[][] points) {
        Vec3 topPoint  = p1;
        Vec3 bottomPoint  = p1;
        Vec3 middlePoint  = p1;
        double minY = p1.yp,maxY = p1.yp,midY = p1.yp;
        
        if(p2.yp < minY) {
            minY = p2.yp;
            bottomPoint = p2;
        } else if(p2.yp > maxY) {
            maxY = p2.yp;
            topPoint = p2;
        } else {
            midY = p2.yp;
            middlePoint = p2;
        }
        
        if(p3.yp < minY) {
            minY = p3.yp;
            bottomPoint = p3;
        } else if(p3.yp > maxY) {
            maxY = p3.yp;
            topPoint = p3;
        } else {
            midY = p3.yp;
            middlePoint = p3;
        }
        
        if ((int)midY == (int)maxY) {
            fillBotFlat(topPoint, middlePoint, bottomPoint,points);
        } else if ((int)minY == (int)midY) {
            fillTopFlat(topPoint, middlePoint, bottomPoint,points);
        } else {
            Vec3 p4 = new Vec3(bottomPoint.xp + ((middlePoint.yp - bottomPoint.yp) / (topPoint.yp - bottomPoint.yp)) * (topPoint.xp - bottomPoint.xp), middlePoint.yp,0);
            
            fillBotFlat(bottomPoint, p4, middlePoint, points);
            fillTopFlat(p4, middlePoint, topPoint,points);
            
            p4 = new Vec3(topPoint.xp + ((middlePoint.yp - topPoint.yp) / (bottomPoint.yp - topPoint.yp)) * (bottomPoint.xp - topPoint.xp), middlePoint.yp,0);
            fillBotFlat(bottomPoint,middlePoint,p4,points);
            fillTopFlat(middlePoint,p4,topPoint,points);
            
          }
    }
    
    /**
     * Funktion zum multiplizieren von Matrixen (Matritzen?), lediglich
     * zur Vereinfachung gedacht, da diese Operation mehrmals nötig ist.
     */
    public static void MatrixVectorMal(Vec3 vector, Vec3 vectorOut, double[][] matrix) {
        vectorOut.xp = vector.xp*matrix[0][0] + vector.yp*matrix[1][0] + vector.zp*matrix[2][0] + matrix[3][0];
        vectorOut.yp = vector.xp*matrix[0][1] + vector.yp*matrix[1][1] + vector.zp*matrix[2][1] + matrix[3][1];
        vectorOut.zp = vector.xp*matrix[0][2] + vector.yp*matrix[1][2] + vector.zp*matrix[2][2] + matrix[3][2];
        double w = vector.xp*matrix[0][3] + vector.yp*matrix[1][3] + vector.zp*matrix[2][3] + matrix[3][3];
        
        if(w!=0) {
            vectorOut.xp/=w; vectorOut.yp/=w; vectorOut.zp/=w;
        }
    }
    
    /**
     * Diese Funktion ist der eigentliche Kern des Programms, da sie dafür verantwortlich ist die
     * 3-dimensionalen Koordinaten in 2-dimensionalen Raum zu projizieren. Ich versuche die einzelnen
     * Schritte nach Möglichkeit so genau es geht in den Kommentaren zu beschreiben, es ist aber sicher
     * Ratsam sich dieses Video dazu anzusehen: https://www.youtube.com/watch?v=ih20l3pJoeU
     * Dort werden die Konzepte und genrellen mathematischen Vorgänge genau beschrieben und anschaulich/bildlich
     * erklärt. 
     * 
     * Während diese Funktion in vielen Teilen der Programmunterschiede wegen abweichen muss gibt das Video einen
     * sehr guten Eindruck wie das Programm sich aufbaut und ich hätte es nicht geschafft ohne es dieses Projekt
     * ansatzweise so schnell so weit zu bringen.
     */
    public static void projectTriangles(ArrayList<triangle> tris, ArrayList<triangle> projTris, double[][] zRotation, double[][] xRotation, double[][] projectionMatrix, double rotationX, double rotationZ,double dist,String inf) {
        
        //Berechnet die Rotationsposition Z basierend auf der angegeben Rotation in °
        zRotation[0][0] = Math.cos(rotationZ);
        zRotation[0][1] = Math.sin(rotationZ);
        zRotation[1][0] = -Math.sin(rotationZ);
        zRotation[1][1] = Math.cos(rotationZ);
        zRotation[2][2] = 1;
        zRotation[3][3] = 1;
    
        //Berechnet die Rotationsposition X basierend auf der angegeben Rotation in °
        xRotation[0][0] = 1;
        xRotation[1][1] = Math.cos(rotationX * 0.5f);
        xRotation[1][2] = Math.sin(rotationX * 0.5f);
        xRotation[2][1] = -Math.sin(rotationX * 0.5f);
        xRotation[2][2] = Math.cos(rotationX * 0.5f);
        xRotation[3][3] = 1;
    
        //Geht alle zu projizierenden Dreiecke durch und berechnet ihre Position im 2D-Raum
        for(int d=0;d<tris.size();d+=1) {
            //Temporäre Bestimmung einer Kamera zu berechnung der Perspektive
            Vec3 camera = new Vec3(0,0,0);
            
            //Platzhalter für die Zwischenschritte
            triangle triProj = new triangle(new Vec3(0,0,0),new Vec3(0,0,0),new Vec3(0,0,0)),
            triRotZ = new triangle(new Vec3(0,0,0),new Vec3(0,0,0),new Vec3(0,0,0)),
            triRotX = new triangle(new Vec3(0,0,0),new Vec3(0,0,0),new Vec3(0,0,0)),
            triRotXZ = new triangle(new Vec3(0,0,0),new Vec3(0,0,0),new Vec3(0,0,0));
            
            //Vektormultiplizierung mit der Rotationsmatrix Y, Rotiert den Würfel auf der y-Achse
            MatrixVectorMal(tris.get(d).c1,triRotZ.c1,zRotation);
            MatrixVectorMal(tris.get(d).c2,triRotZ.c2,zRotation);
            MatrixVectorMal(tris.get(d).c3,triRotZ.c3,zRotation);
            
            //Vektormultiplizierung mit der Rotationsmatrix X, Rotiert den Würfel auf der x-Achse
            MatrixVectorMal(triRotZ.c1,triRotXZ.c1,xRotation);
            MatrixVectorMal(triRotZ.c2,triRotXZ.c2,xRotation);
            MatrixVectorMal(triRotZ.c3,triRotXZ.c3,xRotation);
            
            //Übertragung des rotierten Dreiecks auf das finale Dreieck
            triangle triTrans = triRotXZ;
            
            //z-Achsen-Verschiebung nach hinten
            triTrans.c1.zp = triRotXZ.c1.zp + dist;
            triTrans.c2.zp = triRotXZ.c2.zp + dist;
            triTrans.c3.zp = triRotXZ.c3.zp + dist;
            
            //Berechnung der Normale für jedes einzelne Dreieck. Falls Unklarheiten bestehen
            //kann ich hier nur das zweite Video der oben verlinkten Videoreihe empfehlen, es
            //erklärt sehr gut das Konzept der Sache. Kurz dienen sie zur Bestimmung der Perspektive
            //von der Kamera aus und anhand von ihnen lässt sich bestimmen ob ein Dreieck sichtbar sein sollte.
            Vec3 normal = new Vec3(0,0,0),line1 = new Vec3(0,0,0),line2 = new Vec3(0,0,0);
            
            //Berechnung zweier abgehender Linien von denen aus
            //die aufrecht positionierte Normale berechnet werden kann.
            line1.xp = triTrans.c2.xp - triTrans.c1.xp;
            line1.yp = triTrans.c2.yp - triTrans.c1.yp;
            line1.zp = triTrans.c2.zp - triTrans.c1.zp;
            
            line2.xp = triTrans.c3.xp - triTrans.c1.xp;
            line2.yp = triTrans.c3.yp - triTrans.c1.yp;
            line2.zp = triTrans.c3.zp - triTrans.c1.zp;
            
            //Mithilfe des Kreuzproduktes die Normale berechnen (https://de.wikipedia.org/wiki/Kreuzprodukt)
            normal.xp = line1.yp * line2.zp - line1.zp * line2.yp;
            normal.yp = line1.zp * line2.xp - line1.xp * line2.zp;
            normal.zp = line1.xp * line2.yp - line1.yp * line2.xp;
            
            //Länge der Normale berechnen und sie übertragen
            double l = Math.sqrt(normal.xp*normal.xp + normal.yp*normal.yp + normal.zp*normal.zp);
            normal.xp /= l; normal.yp /= l; normal.zp /= l;
            if(inf.equals("y")) {
                //Projektion erfolgt mithilfe der manuell definierten Projektionsmatrix,
                //mehr im Haptprogramm
                MatrixVectorMal(triTrans.c1,triProj.c1,projectionMatrix);
                MatrixVectorMal(triTrans.c2,triProj.c2,projectionMatrix);
                MatrixVectorMal(triTrans.c3,triProj.c3,projectionMatrix);
                
                triProj.c1.xp+=1;triProj.c1.yp+=1;
                triProj.c2.xp+=1;triProj.c2.yp+=1;
                triProj.c3.xp+=1;triProj.c3.yp+=1;
                
                triProj.c1.xp*=0.5*Planet.ScreenW;triProj.c1.yp*=.5*Planet.ScreenH;
                triProj.c2.xp*=0.5*Planet.ScreenW;triProj.c2.yp*=.5*Planet.ScreenH;
                triProj.c3.xp*=0.5*Planet.ScreenW;triProj.c3.yp*=.5*Planet.ScreenH;
                
                projTris.add(triProj);
            } else {
                //Nur wenn die Normale sich von der Kamera aus im sichtbaren Bereich befindet
                //muss das Dreieck auch projiziert und in die Liste projizierter Dreiecke aufgenommen
                //werden.
                if(normal.xp * (triTrans.c1.xp - camera.xp) + 
                   normal.yp * (triTrans.c1.yp - camera.yp) +
                   normal.zp * (triTrans.c1.zp - camera.zp) < 0) {
                    
                    //Projektion erfolgt mithilfe der manuell definierten Projektionsmatrix,
                    //mehr im Haptprogramm
                    MatrixVectorMal(triTrans.c1,triProj.c1,projectionMatrix);
                    MatrixVectorMal(triTrans.c2,triProj.c2,projectionMatrix);
                    MatrixVectorMal(triTrans.c3,triProj.c3,projectionMatrix);
                    
                    triProj.c1.xp+=1;triProj.c1.yp+=1;
                    triProj.c2.xp+=1;triProj.c2.yp+=1;
                    triProj.c3.xp+=1;triProj.c3.yp+=1;
                    
                    triProj.c1.xp*=0.5*Planet.ScreenW;triProj.c1.yp*=.5*Planet.ScreenH;
                    triProj.c2.xp*=0.5*Planet.ScreenW;triProj.c2.yp*=.5*Planet.ScreenH;
                    triProj.c3.xp*=0.5*Planet.ScreenW;triProj.c3.yp*=.5*Planet.ScreenH;
                    
                    projTris.add(triProj);
                }
            }
        }
    }
    public static synchronized int[][] points(ArrayList<triangle> projTris, boolean drawTris) {
        int[][] points = new int[Planet.ScreenW][Planet.ScreenH];
        for(int p=0;p<projTris.size();p+=1) {
            if(drawTris) {
                drawTri(new Vec3(projTris.get(p).c1.xp,projTris.get(p).c1.yp,0),
                        new Vec3(projTris.get(p).c2.xp,projTris.get(p).c2.yp,0), 
                        new Vec3(projTris.get(p).c3.xp,projTris.get(p).c3.yp,0),points);
                /*fillTri(new Vec3(projTris.get(p).c1.xp,projTris.get(p).c1.yp,0),
                        new Vec3(projTris.get(p).c2.xp,projTris.get(p).c2.yp,0), 
                        new Vec3(projTris.get(p).c3.xp,projTris.get(p).c3.yp,0),points);*/
            }
        }
        return points;
    }
}
