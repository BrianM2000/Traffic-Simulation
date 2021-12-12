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
    boolean tester = true;
    int toTurn; //6 turns right, 2 turns left, 0 through, else cannot turn
    
    public Vehicle(String type, double size, double speed, double accel, double position, Road currRoad, Road destRoad, int laneNum){  
        this.type = type;
        this.size = size * 0.000189394; 
        this.speed = speed; //mph
        this.accel = accel; //mph 
        this.position = position; 
        this.currRoad = currRoad;
        this.destRoad = destRoad;
        this.laneNum = laneNum;
        this.toTurn = Math.floorMod(this.currRoad.federalDirection - this.destRoad.federalDirection, 8);
        this.currRoad.addVehicleToLane(this, laneNum);
        
        if(vehicles.add(this)){ 
            System.out.println(this + " " + type + " added at position " + (position - this.size) + " " + toTurn);
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
        if(this.position > this.currRoad.length){
            this.currRoad.removeVehicleToLane(this, laneNum);
            this.position = this.position - this.currRoad.length;
            this.currRoad = this.destRoad;
            this.currRoad.addVehicleToLane(this, laneNum);
            this.destRoad = this.currRoad; //should eventaully be this.destRoad = this.getNextDest();
        }
        /*
        if(type == "Car"){
            System.out.println(type + " " + this.speed + " mph " + position);
        }
        */
        this.position = this.position + this.speed/3600;
        //System.out.println(this.position);
        if(0 < this.position - this.currRoad.length && tester){
            System.out.println(this + " " + type + " has exited " + this.currRoad + " at " + count + " seconds!");
            tester = false;
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
            this.accel = (neighbor.speed - this.speed)/(((neighbor.position - neighbor.size) - position) * 50 + 1);
        }
        
        else if(((this.currRoad.length - 0.00284091) - position < (speed/3600) * 3 ||
            (position + ((this.currRoad.speedLimit - this.speed)/Math.sqrt(this.currRoad.speedLimit))/3600) > this.currRoad.length - 0.00284091 - position) &&
            !this.canGoThroughLight()){// make it so vehicles can go through yellow lights only if their position in the next 3 seconds is greater than the white line
            this.accel = (-this.speed)/(((this.currRoad.length - 0.00284091) - position) * 50 + 1);
            //System.out.println(type + " is " + (this.currRoad.length - position) + " miles away from intersection");
        }
        
        else{
            this.accel = (this.currRoad.speedLimit - this.speed)/Math.sqrt(this.currRoad.speedLimit);
        }
        /*
        if(this.canGoThroughLight()){
            System.out.println(type + " is going " + toTurn + " at light " + this.currRoad.intersection.light);
        }
        */
        //System.out.println("accel " + this.accel);
        /*
        if(type == "Truck"){
            accel = 0;
        }
        */
        this.speed = Math.ceil((this.speed + this.accel)*1000.0)/1000.0;
    }
    
    public boolean canGoThroughLight(){
        boolean correctLane;
        boolean traffic = false;
        int i;
        if(this.toTurn == 0){
            correctLane = this.currRoad.lanes.get(laneNum).through;
        }
        else if(this.toTurn == 2){
            for(Road road : this.currRoad.intersection.inRoads){
                if(Math.floorMod(this.currRoad.federalDirection - road.federalDirection, 8) == 4){
                    for(i = road.lanes.size() - 1; i >= 0; --i){
                        if(road.lanes.get(i).through){
                            for(Vehicle vehicle : road.lanes.get(i).vehicles){
                                if(vehicle.position + (vehicle.speed/3600) * 3 > vehicle.currRoad.length - 0.00284091){
                                    traffic = true;
                                }
                            }
                        }
                    }
                }
            }
            correctLane = this.currRoad.lanes.get(laneNum).left && !traffic;
        }
        else if(this.toTurn == 6){
            correctLane = this.currRoad.lanes.get(laneNum).right;
        }
        else{
            correctLane = false;
        }
        return (this.currRoad.intersection.pattern[(this.currRoad.federalDirection/2)].getTurn(toTurn) == 2) && correctLane;
    }
}
