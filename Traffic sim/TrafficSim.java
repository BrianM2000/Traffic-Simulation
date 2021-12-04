import java.util.*;

public class TrafficSim{
    static int count = 0;
    public static void main(String args[]){
        generate();
        
        while(count < 40){
            tick();
        }
        System.out.println("done!");
    }
    
    public static void generate(){ //blank args for now, will read info from xlsx in future
        //temp pre created generation
        Road CedarSt = new Road(-74.00983, 40.70812, -74.01284, 40.70971, 25, 7, 311.03021, true);
        Road Broadway = new Road(-74.01052, 40.70914, -74.01328, 40.70587, 25, 1, 431.6389, true);
        
        CedarSt.addLane(new Lane(true, true, true));
        Broadway.addLane(new Lane(true, true, false));
        Broadway.addLane(new Lane(false, true, true));
        
        TrafficLight BroadwayCedarSt = new TrafficLight(-74.01082, 40.70879);
        BroadwayCedarSt.pattern.add(0);
        BroadwayCedarSt.pattern.add(60);
        BroadwayCedarSt.pattern.add(1);
        BroadwayCedarSt.pattern.add(60);
        
        Intersection bc = new Intersection(BroadwayCedarSt);
        bc.roads.add(CedarSt);
        bc.roads.add(Broadway);
        
        Vehicle car1 = new Vehicle("Car",15, 25, 0, 0, CedarSt, CedarSt, 0);
        //Vehicle car2 = new Vehicle("Car",10, 0, 0, 0, Broadway, Broadway, 1);
        Vehicle car3 = new Vehicle("Truck",72, 0, 0, 0.03, CedarSt, CedarSt, 0);
        
    }
    
    public static void tick(){
        Vehicle.tick();
        ++count;
    }
    
}