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
    
    public Road(double startX, double startY, double endX, double endY, int speedLimit, int federalDirection, double length, boolean oneway){
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.speedLimit = speedLimit;
        this.federalDirection = federalDirection;
        this.length = length * 0.000621371;
        this.oneway = oneway;
        
        System.out.println(this.length);
    }
    
    public void addLane(Lane lane){
        this.lanes.add(lane);
    }
    
    public void addVehicleToLane(Vehicle vehicle, int laneNum){ //0 is left most lane
        this.lanes.get(laneNum).vehicles.add(vehicle);
        this.lanes.get(laneNum).checkNeighbor();
    }
}
