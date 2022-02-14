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
    static ArrayList<Vehicle> toRemove = new ArrayList<Vehicle>();
    static int count = 1;
    boolean mustCheckNeighbor = true;
    Vehicle neighbor = null;
    boolean toClose;
    boolean arrived = false;
    boolean safe;
    boolean tester = true;
    int toTurn; //6 turns right, 2 turns left, 0 through, else cannot turn
    int delay;
    
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
            //System.out.println(this + " " + type + " added at position " + (position - this.size) + " " + toTurn);
        }
    }
    
    public static void tick(){
       
        for(Vehicle vehicle : vehicles){
            vehicle.move();
            if(vehicle.arrived){
                toRemove.add(vehicle);
                //System.out.println("removing " + vehicle);
            }
        }
        vehicles.removeAll(toRemove);
        //System.out.println(vehicles.size());
        ++count;
    }
    
    public void move(){
        ++delay;
        int toChange = this.rightLane();
        safe = true;
        if(toChange != 0){ 
            this.changeLane(toChange); 
        }
        this.accelerate();
        if(this.speed/3600 < 0){
            this.speed = 0;
        }
        if(this.position >= this.currRoad.length){//moves to destRoad
            toDestination();
        }
        
        if(type == "Car"){
            //System.out.println(type + " " + this.speed + " mph " + position);
        }
        
        this.position = this.position + this.speed/3600;
        //System.out.println(this.position);
        if(0 < this.position - this.currRoad.length && tester){
            //System.out.println(this + " " + type + " has exited " + this.currRoad + " at " + count + " seconds!");
            tester = false;
        }
    }
    
    public void accelerate(){
        
        if(mustCheckNeighbor){//find neighbor
            neighbor = this.currRoad.lanes.get(laneNum).getNeighbor(position);
            mustCheckNeighbor = false;
        }
        
        if(neighbor != null){//stay safe distance from neighbor; needs to check for cars in next lane 
            //System.out.println(neighbor.size);
            toClose = (neighbor.position - neighbor.size) - position < (Math.abs(neighbor.speed - speed)/3600) * 3 || 
                      (position + ((this.currRoad.speedLimit - this.speed)/Math.sqrt(this.currRoad.speedLimit))/3600) > neighbor.position - neighbor.size - position;
        }
        /*
        else if(neighbor == null && (this.position + this.speed/3600) > this.currRoad.length){
            //keep vehicle a safe distance from vehicles in destRoad; need to change how vehicles are kept track of first, to a more optimal way
        }
        */
        if(neighbor != null && toClose && speed >= 0){//slow down relative to neighbor
            this.accel = (neighbor.speed - this.speed)/(((neighbor.position - neighbor.size) - position) * 50 + 1);
        }
        
        else if(((this.currRoad.length - 0.00284091) - position < (speed/3600) * 3 ||
            (position + ((this.currRoad.speedLimit - this.speed)/Math.sqrt(this.currRoad.speedLimit))/3600) > this.currRoad.length - 0.00284091 - position) &&
            this.currRoad.intersection != null && !this.canGoThroughLight()){//slowdown if not allowed to go through intersection
            this.accel = (-this.speed)/(((this.currRoad.length - 0.00284091) - position) * 50 + 1);
            //System.out.println(type + " is " + (this.currRoad.length - position) + " miles away from intersection");
        }
        else if(!safe){//slow down if vehicle can't change lanes as desired
            this.accel = (this.currRoad.speedLimit/2 - this.speed)/Math.sqrt(this.currRoad.speedLimit/2) ;
        }
        
        else{//accelerate towards speed limit
            this.accel = (this.currRoad.speedLimit - this.speed)/Math.sqrt(this.currRoad.speedLimit);
        }
        /*
        if(this.canGoThroughLight()){
            System.out.println(type + " is going " + toTurn + " at light " + this.currRoad.intersection);
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
    
    public void toDestination(){
        int i;
        int j;
        int diff;
        int firstThrough = 0;
        int numThrough = 0;
        if(this.currRoad == this.destRoad){
            arrived = true;
        }
        this.currRoad.totalDelay = this.currRoad.totalDelay + ((this.delay) - (int) this.currRoad.perfDelay);
        this.currRoad.removeVehicleToLane(this, laneNum);
        this.position = this.position - this.currRoad.length;
            
        if(toTurn == 0){
            for(i = 0; i < currRoad.lanes.size(); i++){
                if(this.currRoad.lanes.get(i).through){
                    firstThrough = i;
                    break;
                }
            }
            diff = laneNum - firstThrough;
            this.currRoad = this.destRoad;
            for(i = 0; i < currRoad.lanes.size(); i++){
                if(this.currRoad.lanes.get(i).through){
                    firstThrough = i;
                    break;
                }
            }
            this.currRoad.addVehicleToLane(this, firstThrough + diff);
        }
        if(toTurn == 2){
            this.currRoad = this.destRoad;
            this.currRoad.addVehicleToLane(this, laneNum);
        }
        if(toTurn == 6){
            diff = this.currRoad.lanes.size() - 1 - laneNum;
            this.currRoad = this.destRoad;
            this.currRoad.addVehicleToLane(this, this.currRoad.lanes.size() - 1 - diff);
        }
            
        //System.out.println(this.type + " moving to " + this.currRoad + " to lane " + this.laneNum);
            
        this.destRoad = this.currRoad; //should eventaully be this.destRoad = this.getNextDest();
        this.toTurn = Math.floorMod(this.currRoad.federalDirection - this.destRoad.federalDirection, 8);
        this.delay = 0;
            
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
        return ((this.currRoad.intersection.pattern.get(this.currRoad.federalDirection/2).getTurn(toTurn) == 2) && correctLane) ||
        ((this.currRoad.intersection.pattern.get(this.currRoad.federalDirection/2).getTurn(toTurn) == 2)&& correctLane && this.canGoThruYellow());
    }
    
    public void changeLane(int toChange){// need to make vehicles spread out, rn they just stop at the first acceptable lane
        safe = true;
        Vehicle close = null;
        int pos;
        try{//gotta check to make sure no ones in the way
            for(Vehicle vehicle : this.currRoad.lanes.get(laneNum + toChange).vehicles){
                if(this.position <= vehicle.position){//unhappy with this, but it works ok; needs to account for speed
                    if(Math.abs(this.position - vehicle.position) < vehicle.size * 2){
                        safe = false;
                        //System.out.println(vehicle.type + " is too close to infront of " + this.type);
                    }
                }
                else{
                    if(Math.abs(this.position - vehicle.position) < this.size * 3 || 
                      (Math.abs(this.speed - vehicle.speed) * vehicle.position + (vehicle.speed/3600) * 3 < this.position)){
                        //System.out.println(vehicle.type + " is too close behind " + this.type);
                        safe = false;
                    }
                }
                if(neighbor == null && vehicle.position != position && vehicle.position - position > 0){
                  close = vehicle;
                }
                else if(vehicle.position - position > 0 && vehicle.position - position < neighbor.position - position && vehicle.position - position != 0){
                  close = vehicle;
                }
            }
            if(safe){
                currRoad.removeVehicleToLane(this, laneNum);
                laneNum = laneNum + toChange;
                currRoad.addVehicleToLane(this, laneNum);
                //this.neighbor = close.neighbor;
                //close.neighbor = this;
                //System.out.println(this.position + " " + close.position);
                //System.out.println(this.type + " moved to lane " + laneNum);
            }
        }
        catch(Exception e){
            throw e;
        }
    }
    
    public int rightLane(){
        int move = 0;
        //System.out.println(this.currRoad.id + " "+ this.currRoad.federalDirection + " " + this.destRoad.id + " " + this.destRoad.federalDirection);
        if(toTurn == 0){//straight
            if(this.currRoad.lanes.get(laneNum).left && !this.currRoad.lanes.get(laneNum).through){
                move = 1;
            }
            if(this.currRoad.lanes.get(laneNum).right && !this.currRoad.lanes.get(laneNum).through){
                move = -1;
            }
        }
        if(toTurn == 2){//left
            if((this.currRoad.lanes.get(laneNum).through || this.currRoad.lanes.get(laneNum).right) && !this.currRoad.lanes.get(laneNum).left){
                move = -1;
            }
        }
        if(toTurn == 6){//right
            if((this.currRoad.lanes.get(laneNum).through || this.currRoad.lanes.get(laneNum).left)&& !this.currRoad.lanes.get(laneNum).right){
                move = 1;
            }
        }
        if(toTurn == 4){//backwards, final destination
            move = 0;
        }
        return move;
    }
    
    public boolean canGoThruYellow(){
        int i = 0;
        for(Road road : this.currRoad.intersection.inRoads){
                if(this.currRoad.federalDirection - road.federalDirection != 0){
                    for(i = road.lanes.size() - 1; i >= 0; --i){
                        if(road.lanes.get(i).through){
                            for(Vehicle vehicle : road.lanes.get(i).vehicles){
                                if(vehicle.position + (vehicle.speed/3600) * 3 > vehicle.currRoad.length - 0.00284091){
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        return true;
    }
}
