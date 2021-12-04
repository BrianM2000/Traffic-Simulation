import java.util.*;

public class TrafficLight{
    double x;
    double y;
    int light;
    Queue<Integer> pattern = new LinkedList<>();
    int counter = 0;
    int max;
    static ArrayList<TrafficLight> lights = new ArrayList<TrafficLight>();
    
    public TrafficLight(double x, double y){
       this.x = x;
       this.y = y;
       lights.add(this);
    }

    /* Light Pattern chart
     * 0 = green north/south
     * 1 = green east/west
     * nums < 45 are how long a light stays
     */
    
    public static void tick(){
        
    }
    
}
