import java.util.*;

public class Intersection{
    //TrafficLight light;
    static ArrayList<Road> inRoads = new ArrayList<Road>();
    static ArrayList<Road> outRoads = new ArrayList<Road>();
    double x;
    double y;
    Signal[] pattern = new Signal[4];
    int currPattern = 0;
    int counter;
    int max;
    int i;
    static ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    
    public Intersection(double x, double y){// eventually needs an ArrayList or something to store a sequence for the pattern
       this.x = x;
       this.y = y;
       intersections.add(this);
       
       for(i = 0; i < 4; ++i){ //0 north, 1 east, 2 south, 3 west
        pattern[i] = new Signal();
       }
       pattern[currPattern].through = 2;
       pattern[currPattern + 2].through = 2;
       counter = 60;
    }
    
    public static void tick(){
        for(Intersection light : intersections){
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

    public static void addRoads(){
        for(Intersection intersection : intersections){
            Road.addToIntersection(intersection);
        }
    }
    
    public static void addToRoads(){
        for(Intersection intersection : intersections){
            for(Road road : inRoads){
                road.intersection = intersection;
            }
            for(Road road : outRoads){
                road.intersection = intersection;
            }
        }
    }
}
