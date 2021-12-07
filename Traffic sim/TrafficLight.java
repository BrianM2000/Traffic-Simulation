import java.util.*;

public class TrafficLight{
    double x;
    double y;
    int light;
    Queue<Integer> pattern = new LinkedList<>();
    int currPattern;
    int counter = 0;
    int max;
    static ArrayList<TrafficLight> lights = new ArrayList<TrafficLight>();
    
    public TrafficLight(double x, double y){
       this.x = x;
       this.y = y;
       lights.add(this);
       
       //temp
       currPattern = 0;
    }

    /* Light Pattern chart
     * 0 = all red
     * 
     * 
     */
    
    public static void tick(){
        
    }
    
}
