import java.util.*;

public class TrafficSim{
    static int count = 0;
    public static void main(String args[]){
        generate();
        
        while(count < 100){
            tick();
        }
        System.out.println("done!");
    }
    
    public static void generate(){ //blank args for now, will read info from xlsx in future
        //temp pre created generation
        Road CedarStW = new Road(-74.00983, 40.70812, -74.01284, 40.70971, 25, 7, 311.03021, true);
        Road CedarStE = new Road(-74.00983, 40.70812, -74.01284, 40.70971, 25, 3, 311.03021, true);
        Road Broadway = new Road(-74.01052, 40.70914, -74.01284, 40.70971, 25, 1, 431.6389, true);
        
        CedarStW.addLane(new Lane(true, true, false));
        CedarStE.addLane(new Lane(true, true, true));
        Broadway.addLane(new Lane(true, true, false));
        Broadway.addLane(new Lane(false, true, true));
        
        TrafficLight BroadwayCedarSt = new TrafficLight(-74.01284, 40.70971);
        
        Intersection bc = new Intersection(BroadwayCedarSt);
        bc.inRoads.add(CedarStW);
        bc.inRoads.add(Broadway);
        bc.addToRoads();
        
        Vehicle car1 = new Vehicle("Car",15, 25, 0, 0, CedarStW, Broadway, 0);
        Vehicle car2 = new Vehicle("Motercycle",10, 0, 0, .06, CedarStW, CedarStW, 0);
        Vehicle car3 = new Vehicle("Truck",72, 0, 10, 0.2, Broadway, CedarStE, 1);
        
    }
    
    public static void tick(){
        TrafficLight.tick();
        Vehicle.tick();
        ++count;
    }
    
}