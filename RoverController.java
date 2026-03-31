import java.util.Arrays;


/**
 * Write a description of class RoverController here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class RoverController implements Runnable
{
    // instance variables - replace the example below with your own
    private Rover rover;
    private int threadID;
    /**
     * Constructor for objects of class RoverController
     */
    public RoverController(Rover rover, int threadID)
    {
        this.rover = rover;
        this.threadID = threadID;
    }
    
    public boolean allFinished() {
        for(boolean i : Planet.finished) {
            if(!i) {
                return false;
            }
        }
        return true;
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public synchronized void run() {
        System.out.println("Thread " + threadID + " has been started");
        while(true) {
            if(allFinished()) {
                try{
                    Thread.sleep(10);
                } catch(Exception e) {
                    break;
                }
                Planet.finished[threadID] = false;
                rover.acting();
                
                Planet.finished[threadID] = true;
            }
        }
    }
}
