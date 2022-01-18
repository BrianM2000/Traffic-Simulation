import java.util.*;
import java.time.Duration;
import java.time.Instant;

public class TrafficSim{
    static int count = 0;
    public static void main(String args[]){
        generate();
        
        Instant start = Instant.now();
        
        while(count < 3600){//3600 for an hour; esitmate 5 minutes for 1 hour worth of simulated time for all 2,820 intersections in Manhattan
            tick();
        }
        
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        
        Road.printDelay();
        
        System.out.println("done! " + count + " simulated seconds elapsed in " + timeElapsed.toMillis() + " milliseconds");
    }
    
    public static void generate(){ //blank args for now, will read info from xlsx in future
        //temp hard coded generation
        //Road CedarStW = new Road(-74.00983, 40.70812, -74.01284, 40.70971, 25, 7, 311.03021, 3, "");
        //Road CedarStE = new Road(-74.01284, 40.70971, -74.00983, 40.70812, 25, 3, 311.03021, 3,"left|through|right");
        Road SouthBroadwayN = new Road(-74.00953, 40.71032, -74.00906, 40.71088, 25, 1, 74.23706, 3264, 3,"left|through|right");
        Road SouthBroadwayS = new Road(-74.00906, 40.71088, -74.00953, 40.71032, 25, 5, 74.23706, 3264, 3,"left|through|right");
        Road NorthBroadwayN = new Road(-74.00906, 40.71088, -74.00865, 40.71138, 25, 1, 65.46808, 3264, 3,"left|through|right");
        Road NorthBroadwayS = new Road(-74.00865, 40.71138, -74.00906, 40.71088, 25, 5, 65.46808, 3264, 3,"left|through|right");
        Road WestFultonE = new Road(-74.01043, 40.71152, -74.00906, 40.71088, 25, 3, 135.86778, 1779, 3,"left|through|right");
        Road WestFultonW = new Road(-74.00906, 40.71088, -74.01043, 40.71152, 25, 7, 135.86778, 1779, 3,"left|through|right");
        Road EastFultonE = new Road(-74.00906, 40.71088, -74.00775, 40.71022, 25, 3, 133.22134, 4767, 3,"left|through|right");
        Road EastFultonW = new Road(-74.00775, 40.71022, -74.00906, 40.71088, 25, 3, 133.22134, 4767, 3,"left|through|right");
     
        Road.addLanes();
        
        Intersection BroadwayFulton = new Intersection(-74.00906, 40.71088);
        BroadwayFulton.signal = "002202t060102302t060"; //each 3 digits is a 'block', first for direction of road; second, light of left turn signal; third, light of through signal; t means next block represents time those lights are green for
        
        Intersection.addRoads();
        Intersection.addToRoads();
        
        //TrafficLight BroadwayCedarSt = new TrafficLight(-74.01284, 40.70971);
        
        //Intersection bc = new Intersection(BroadwayCedarSt);
        //bc.inRoads.add(CedarStW);
        //bc.inRoads.add(Broadway);
        
        //bc.outRoads.add(CedarStE);
        
        //bc.addToRoads();
        
        /*
        Vehicle v1 = new Vehicle("CarLeft", 15, 25, 0, 0, SouthBroadwayN, WestFultonW, 0);
        Vehicle v4 = new Vehicle("CarBlocking", 30, 25, 0, .00284091, SouthBroadwayN, WestFultonW, 0);
        Vehicle v3 = new Vehicle("CarT", 15, 0, 0, .0028409*4, SouthBroadwayN, NorthBroadwayN, 1);
        Vehicle v2 = new Vehicle("CarThrough", 15, 25, 0, 0, NorthBroadwayS, SouthBroadwayS, 0);
        
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