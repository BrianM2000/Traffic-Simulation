import java.util.*;

public class TrafficLight{//should probably merge this with intersection
    double x;
    double y;
    Signal[] pattern = new Signal[4];
    int currPattern = 0;
    int counter;
    int max;
    int i;
    static ArrayList<TrafficLight> lights = new ArrayList<TrafficLight>();
    
    public TrafficLight(double x, double y){// eventually needs an ArrayList or something to store a sequence for the pattern
       this.x = x;
       this.y = y;
       lights.add(this);
       
       for(i = 0; i < 4; ++i){ //0 north, 1 east, 2 south, 3 west
        pattern[i] = new Signal();
       }
       pattern[currPattern].through = 2;
       pattern[currPattern + 2].through = 2;
       counter = 60;
    }
    
    public static void tick(){
        for(TrafficLight light : lights){
            light.next();
        }
    }
    
    public void next(){
        if(counter == 0){
            pattern[currPattern].through = 0;
            pattern[Math.floorMod(currPattern + 2, 4)].through = 0;
            currPattern = Math.floorMod(currPattern + 1, 4);
            //System.out.println(currPattern);
            pattern[currPattern].through = 2;
            pattern[Math.floorMod(currPattern + 2, 4)].through = 2;
            counter = 60;
        }
        else{
            counter--;
        }
    }
}
