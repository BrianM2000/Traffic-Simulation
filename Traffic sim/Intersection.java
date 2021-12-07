import java.util.*;

public class Intersection{
    TrafficLight light;
    ArrayList<Road> roads = new ArrayList<Road>();
    
    public Intersection(TrafficLight light){
        this.light = light;
    }

    public void addToRoads(){
        for(Road road : roads){
            road.intersection = this;
        }
    }
}
