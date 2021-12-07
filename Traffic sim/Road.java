import java.util.ArrayList;

public class Road{
    double startX;
    double startY;
    double endX;
    double endY;
    int speedLimit;
    int federalDirection;
    int delay = 0;
    double length;
    boolean oneway;
    ArrayList<Lane> lanes = new ArrayList<Lane>();
    double perfDelay;
    Intersection intersection;
    
    public Road(double startX, double startY, double endX, double endY, int speedLimit, int federalDirection, double length, boolean oneway){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.speedLimit = speedLimit;
        this.federalDirection = federalDirection; //1 North 3 East 5 South 7 West //mod8
        this.length = length * 0.000621371;
        this.oneway = oneway;
        this.perfDelay = Math.round(this.length/((this.speedLimit*1.0/3600)*1.0));
        
        System.out.println(this.length + " " + perfDelay);
    }
    
    public void addLane(Lane lane){
        this.lanes.add(lane);
    }
    
    public void addVehicleToLane(Vehicle vehicle, int laneNum){ //0 is left most lane
        this.lanes.get(laneNum).vehicles.add(vehicle);
        this.lanes.get(laneNum).checkNeighbor();
    }
}
