import java.util.*;
import java.time.Duration;
import java.time.Instant;

public class TrafficSim{
    static int count = 0;
    public static void main(String args[]){
        generate();
        
        Instant start = Instant.now();
        
        while(count < 3600){
            tick();
        }
        
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        
        Road.printDelay();
        
        System.out.println("done! " + count + " simulated seconds elapsed in " + timeElapsed.toMillis() + " milliseconds");
    }
    
    public static void generate(){ //blank args for now, will read info from xlsx in future
        //temp pre created generation
        Road CedarStW = new Road(-74.00983, 40.70812, -74.01284, 40.70971, 25, 7, 311.03021);
        Road CedarStE = new Road(-74.01284, 40.70971, -74.00983, 40.70812, 25, 3, 311.03021);
        Road SouthBroadwayN = new Road(-74.00953, 40.71032, -74.00906, 40.71088, 25, 1, 74.23706);
        Road SouthBroadwayS = new Road(-74.00906, 40.71088, -74.00953, 40.71032, 25, 5, 74.23706);
        Road NorthBroadwayN = new Road(-74.00906, 40.71088, -74.00865, 40.71138, 25, 1, 65.46808);
        Road NorthBroadwayS = new Road(-74.00865, 40.71138, -74.00906, 40.71088, 25, 5, 65.46808);
        Road WestFultonE = new Road(-74.01043, 40.71152, -74.00906, 40.71088, 25, 3, 135.86778);
        Road WestFultonW = new Road(-74.00906, 40.71088, -74.01043, 40.71152, 25, 7, 135.86778);
        Road EastFultonE = new Road(-74.00906, 40.71088, -74.00775, 40.71022, 25, 3, 133.22134);
        Road EastFultonW = new Road(-74.00775, 40.71022, -74.00906, 40.71088, 25, 3, 133.22134);
     
        Road.addLanes(); //temp method, just adds one lane for now 
        
        Intersection BroadwayFulton = new Intersection(-74.00906, 40.71088);
        
        Intersection.addRoads();
        Intersection.addToRoads();
        
        //TrafficLight BroadwayCedarSt = new TrafficLight(-74.01284, 40.70971);
        
        //Intersection bc = new Intersection(BroadwayCedarSt);
        //bc.inRoads.add(CedarStW);
        //bc.inRoads.add(Broadway);
        
        //bc.outRoads.add(CedarStE);
        
        //bc.addToRoads();
        
        SouthBroadwayN.addLane(new Lane(false, false, true));
        
        Vehicle v1 = new Vehicle("CarLeft", 15, 25, 0, 0, SouthBroadwayN, WestFultonW, 0);
        //Vehicle v4 = new Vehicle("CarBlocking", 30, 25, 0, .00284091, SouthBroadwayN, WestFultonW, 0);
        Vehicle v3 = new Vehicle("Car", 15, 0, 0, .0028409*4, SouthBroadwayN, NorthBroadwayN, 1);
        Vehicle v2 = new Vehicle("CarThrough", 15, 25, 0, 0, NorthBroadwayS, SouthBroadwayS, 0);
        
        /*
        Vehicle car1 = new Vehicle("Car",15, 25, 0, 0, CedarStW, Broadway, 0);
        Vehicle car2 = new Vehicle("Motorcycle",10, 0, 0, .06, CedarStW, CedarStW, 0);
        Vehicle car3 = new Vehicle("Truck",72, 0, 10, 0.2, Broadway, CedarStE, 1);
        */
        
    }
    
    public static void tick(){
        Intersection.tick();
        Vehicle.tick();
        ++count;
    }
    
}