import java.util.*;

public class Vehicle{
    String type;
    double size;
    double speed; //in mph
    double accel; //in mph
    double position; //in miles
    Road currRoad;
    Road destRoad;
    int laneNum;
    static ArrayList<Vehicle> vehicles = new ArrayList<Vehicle>();
    static int count = 1;
    boolean mustCheckNeighbor = true;
    
    
    public Vehicle(String type, double size, double speed, double accel, double position, Road currRoad, Road destRoad, int laneNum){  
        this.type = type;
        this.size = size; 
        this.speed = speed; //mph
        this.accel = accel; //mph 
        this.position = position; 
        this.currRoad = currRoad;
        this.destRoad = destRoad;
        this.laneNum = laneNum;
        
        this.currRoad.addVehicleToLane(this, laneNum);
        
        if(vehicles.add(this)){ 
            System.out.println(this + " " + type + " added at position " + position);
        }
    }
    
    public static void tick(){
       
        for(Vehicle vehicle : vehicles){
            vehicle.move();
        }
        ++count;
    }
    
    public void move(){
        this.accelerate();
        if(type == "Car"){
            System.out.println(type + " " + this.speed + " mph");
        }
        this.position = this.position + this.speed/3600;
        //System.out.println(this.position);
        if(0 < this.position - this.currRoad.length){
            System.out.println(this + " " + type + " has exited " + this.currRoad + " at " + count + " seconds!");
        }
    }
    
    public void accelerate(){
        Vehicle neighbor = null;
        if(mustCheckNeighbor){
            neighbor = this.currRoad.lanes.get(laneNum).getNeighbor(position);
            mustCheckNeighbor = false;
        }
        if(neighbor != null && neighbor.position - position < (Math.abs(neighbor.speed - speed)/3600) * 5 && neighbor.position - position > 0 && speed > 0){
            System.out.println(type + "  " + position + " is behind " + neighbor.type + " " + neighbor.position);
            this.accel = (neighbor.speed - this.speed)/((neighbor.position - position) * 100);
        }
        
        else{
            this.accel = (this.currRoad.speedLimit - this.speed)/Math.sqrt(this.currRoad.speedLimit);
        }
        
        //System.out.println("accel " + this.accel);
        
        this.speed = Math.round((this.speed + this.accel)*1000.0)/1000.0;
    }
}
