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
    Vehicle neighbor = null;
    boolean toClose;
    
    public Vehicle(String type, double size, double speed, double accel, double position, Road currRoad, Road destRoad, int laneNum){  
        this.type = type;
        this.size = size * 0.000189394; 
        this.speed = speed; //mph
        this.accel = accel; //mph 
        this.position = position; 
        this.currRoad = currRoad;
        this.destRoad = destRoad;
        this.laneNum = laneNum;
        
        this.currRoad.addVehicleToLane(this, laneNum);
        
        if(vehicles.add(this)){ 
            System.out.println(this + " " + type + " added at position " + (position - this.size));
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
        if(this.speed/3600 < 0){
            this.speed = 0;
        }
        if(type == "Motercycle"){
            System.out.println(type + " " + this.speed + " mph " + position);
        }
        
        this.position = this.position + this.speed/3600;
        //System.out.println(this.position);
        if(0 < this.position - this.currRoad.length){
            System.out.println(this + " " + type + " has exited " + this.currRoad + " at " + count + " seconds!");
        }
    }
    
    public void accelerate(){
        
        if(mustCheckNeighbor){
            neighbor = this.currRoad.lanes.get(laneNum).getNeighbor(position);
            mustCheckNeighbor = false;
        }
        if(neighbor != null){
            //System.out.println(neighbor.size);
            toClose = (neighbor.position - neighbor.size) - position < (Math.abs(neighbor.speed - speed)/3600) * 3 || 
                      (position + ((this.currRoad.speedLimit - this.speed)/Math.sqrt(this.currRoad.speedLimit))/3600) > neighbor.position - neighbor.size - position;
        }
        if(neighbor != null && toClose && speed >= 0){
            //System.out.println((neighbor.position - neighbor.size) - position + 1);
            this.accel = (neighbor.speed - this.speed)/(((neighbor.position - neighbor.size) - position) * 400 + 1);
        }
        
        else if((this.currRoad.length - 0.00284091) < (position + (speed/3600) * 3) ||
            (position + ((this.currRoad.speedLimit - this.speed)/Math.sqrt(this.currRoad.speedLimit))/3600) > this.currRoad.length - 0.00284091 - position &&
            this.currRoad.intersection.light.currPattern == 0){
            this.accel = -this.speed/(((this.currRoad.length - 0.00284091) - position) * 400 + 1);
            //System.out.println(type + " is " + (this.currRoad.length - position) + " miles away from intersection");
        }
        
        else{
            this.accel = (this.currRoad.speedLimit - this.speed)/Math.sqrt(this.currRoad.speedLimit);
        }
        
        //System.out.println("accel " + this.accel);
        /*
        if(type == "Truck"){
            accel = 0;
        }
        */
        this.speed = Math.round((this.speed + this.accel)*1000.0)/1000.0;
    }
}
