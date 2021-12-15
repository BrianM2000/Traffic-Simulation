import java.util.ArrayList;

public class Road{
    double startX;
    double startY;
    double endX;
    double endY;
    int speedLimit;
    int federalDirection;
    int delay = 0;
    static int totalSystemDelay = 0;
    double length;
    ArrayList<Lane> lanes = new ArrayList<Lane>();
    double perfDelay;
    Intersection intersection;
    static ArrayList<Road> roads = new ArrayList<Road>();
    double totalDelay = 0;
    
    public Road(double startX, double startY, double endX, double endY, int speedLimit, int federalDirection, double length){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.speedLimit = speedLimit;
        this.federalDirection = federalDirection; //1 North 3 East 5 South 7 West //mod8
        this.length = length * 0.000621371;
        this.perfDelay = Math.round(this.length/((this.speedLimit*1.0/3600)*1.0));
        
        roads.add(this);
        
        System.out.println(this.length + " " + perfDelay);
    }
    
    public void addLane(Lane lane){
        this.lanes.add(lane);
    }
    
    public void addVehicleToLane(Vehicle vehicle, int laneNum){ //0 is left most lane
        this.lanes.get(laneNum).vehicles.add(vehicle);
        this.lanes.get(laneNum).checkNeighbor();
    }
    
    public void removeVehicleToLane(Vehicle vehicle, int laneNum){ //0 is left most lane
        this.lanes.get(laneNum).vehicles.remove(vehicle);
        this.lanes.get(laneNum).checkNeighbor();
    }
    
    public static void addToIntersection(Intersection intersection){
        for(Road road : roads){
            if(road.startX == intersection.x && road.startY == intersection.y){
                intersection.outRoads.add(road);
            }
            else if(road.endX == intersection.x && road.endY == intersection.y){
                intersection.inRoads.add(road);
            }
        }
    }
    
    public static void addLanes(){
        for(Road road: roads){
            road.lanes.add(new Lane(true, true, true));
        }
    }
    
    public static void printDelay(){
        for(Road road: roads){
            System.out.println(road + " had a delay of " + road.totalDelay + " seconds");
            totalSystemDelay = totalSystemDelay + (int) road.totalDelay;
        }
        System.out.println("Total system delay " + totalSystemDelay);
    }
}
