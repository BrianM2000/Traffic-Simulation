import java.util.ArrayList;
import java.util.Random;

public class Road{
    String id;
    double startX;
    double startY;
    double endX;
    double endY;
    int speedLimit;
    int federalDirection;
    int delay = 0;
    static int totalSystemDelay = 0;
    double length;
    int AADT;
    int numLanes;
    String turnLanes;
    ArrayList<Lane> lanes = new ArrayList<Lane>();
    double perfDelay;
    Intersection intersection;
    static ArrayList<Road> roads = new ArrayList<Road>();
    double totalDelay = 0;
    static Random rand = new Random(1);
    static int numCars = 0;
    
    public Road(String id, double startX, double startY, double endX, double endY, int speedLimit, int federalDirection, double length, int AADT, int numLanes, String turnLanes){
        this.id = id;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.speedLimit = speedLimit;
        this.federalDirection = federalDirection; //1 North 3 East 5 South 7 West //mod8
        this.length = length * 0.000621371;
        this.AADT = AADT;
        this.numLanes = numLanes;
        this.turnLanes = turnLanes;
        this.perfDelay = Math.round(this.length/((this.speedLimit*1.0/3600)*1.0));
        
        //System.out.println("adding road " + id);
        roads.add(this);
        
        //System.out.println(this.length + " " + perfDelay);
    }
    
    public void addLane(Lane lane){
        this.lanes.add(lane);
    }
    
    public void addVehicleToLane(Vehicle vehicle, int laneNum){ //0 is left most lane
        try{
        this.lanes.get(laneNum).vehicles.add(vehicle);
        }
        catch(Exception e){
            System.out.println(vehicle.toTurn + " " + vehicle.currRoad.id);
        }
        vehicle.laneNum = laneNum;
        this.lanes.get(laneNum).checkNeighbor();
    }
    
    public void removeVehicleToLane(Vehicle vehicle, int laneNum){ //0 is left most lane
        this.lanes.get(laneNum).vehicles.remove(vehicle);
        this.lanes.get(laneNum).checkNeighbor();
    }
    
    public static void addToIntersection(Intersection intersection){
        for(Road road : roads){
            if(Math.abs(road.startX - intersection.vertex.x) < 0.000001 && Math.abs(road.startY - intersection.vertex.y) < 0.000001){
                intersection.outRoads.add(road);
            }
            else if(Math.abs(road.endX - intersection.vertex.x) < 0.000001 && Math.abs(road.endY - intersection.vertex.y) < 0.000001){
                intersection.inRoads.add(road);
            }
        }
    }
    
    public static void addLanes(){ // should probably check to make sure turns are legal, i.e. not turning onto a one-way street; Might not be needed if cars check instead
    
        for(Road road: roads){
            int i = 0;
            int j = 0;
            String curLane;

            if(road.turnLanes.equals("none")){
                
                for(i = 0; i < road.numLanes; ++i){
                    road.lanes.add(new Lane(false, true, false));
                }
                road.lanes.get(0).left = true;
                road.lanes.get(road.numLanes - 1).right = true;
            }
            else{
                for(i = 0; i < road.numLanes; ++i){
                    road.lanes.add(new Lane(false, false, false));
                }
                i = 0;
                while(j < road.numLanes){
                    try{
                        curLane = road.turnLanes.substring(i, road.turnLanes.indexOf("|", i));
                        
                        i = i + curLane.length() + 1;
                    }
                    catch(Exception e){
                        curLane = road.turnLanes.substring(i, road.turnLanes.length());
                        i = i + curLane.length() - 1;
                    }

                    if(curLane.contains("left") || curLane.contains("none") || (curLane.equals("") && j == 0)){
                        road.lanes.get(j).left = true;
                    }
                    if(curLane.contains("through") || curLane.contains("none") || curLane.equals("")){
                        road.lanes.get(j).through = true;
                    }
                    if(curLane.contains("right") || curLane.contains("none") || (curLane.equals("") && j == road.numLanes - 1)){
                        road.lanes.get(j).right = true;
                    }
                    //System.out.println(j);
                    ++j;
                }
            }
            /*
            System.out.println(road.id);
            for(i = 0; i < road.numLanes; ++i){
                System.out.println(road.lanes.get(i).left + " " + road.lanes.get(i).through + " " + road.lanes.get(i).right);
            }
            */
        }
        
    }
    
    public void newVehicle(){ //should eventually pick from different types i.e. cars, motorcycle, trucks
        Road destRoad;
        boolean possible;
        do{
            possible = false;
            int i = this.intersection.outRoads.size();
            if(i == 0){
                destRoad = this;
                break;
            }
            else{
                destRoad = this.intersection.outRoads.get(rand.nextInt(i));
                /*
                if((Math.floorMod(this.federalDirection - destRoad.federalDirection, 8) == 4) && i == 1){
                    destRoad = this;
                    break;
                }
                */
                for(Road road : this.intersection.outRoads){
                    if((Math.floorMod(this.federalDirection - road.federalDirection, 8) != 4)){
                        possible = true;
                    }
                    
                }

            }
        }while(possible && Math.floorMod(this.federalDirection - destRoad.federalDirection, 8) == 4);
        int laneNum = rand.nextInt(this.numLanes);
        if(this.lanes.get(laneNum).vehicles.size() == 0 || this.lanes.get(laneNum).vehicles.get(this.lanes.get(laneNum).vehicles.size() - 1).position > 0.00284091){
            new Vehicle(Integer.toString(numCars), //name
                        rand.nextInt(10) + 10, //size
                        this.speedLimit, //speed
                        0, //accel
                        0, //position
                        this, //curRoad
                        destRoad, //destination, can select itself
                        laneNum//lane
                        );
            numCars++;
        }
    }
    
    public static void printDelay(){
        for(Road road: roads){
            if(road.federalDirection != 0){
                /*//this scares me
                for(Lane lane : road.lanes){
                    for(Vehicle vehicle : lane.vehicles){
                        if(vehicle.delay > road.perfDelay){
                            road.totalDelay = road.totalDelay + vehicle.delay - (int)(road.perfDelay);
                        }
                    }
                }
                //*/
                System.out.print(road.id + " " + road.federalDirection + " had a delay of " + road.totalDelay + " seconds");
                for(Lane lane : road.lanes){
                    System.out.print(" " + lane.vehicles.size());
                }
                System.out.println("");
                totalSystemDelay = totalSystemDelay + (int) road.totalDelay;
            }
        }
        System.out.println("Total system delay " + totalSystemDelay + " for " + numCars + " vehicles!");
        System.out.println("Approx. " + totalSystemDelay/numCars + " wasted seconds per car");
    }
    
    public void addDirection(int dir){
        this.federalDirection = dir;
    }
}
