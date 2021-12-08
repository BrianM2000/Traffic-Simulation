import java.util.*;

public class Intersection{
    TrafficLight light;
    ArrayList<Road> inRoads = new ArrayList<Road>();
    ArrayList<Road> outRoads = new ArrayList<Road>();
    
    public Intersection(TrafficLight light){
        this.light = light;
    }

    public void addToRoads(){
        for(Road road : inRoads){
            road.intersection = this;
        }
    }
}
