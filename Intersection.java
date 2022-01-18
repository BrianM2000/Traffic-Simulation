import java.util.*;

public class Intersection{
    //TrafficLight light;
    static ArrayList<Road> inRoads = new ArrayList<Road>();
    static ArrayList<Road> outRoads = new ArrayList<Road>();
    double x;
    double y;
    ArrayList<Signal> pattern = new ArrayList<Signal>();
    int timer = 0;
    int currPattern = 0;
    int counter;
    int max;
    int i;
    int j = 0;
    int k = 0;
    String signal;
    static ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    static Random rand = new Random(1);
    
    public Intersection(double x, double y){// eventually needs an ArrayList or something to store a sequence for the pattern
       this.x = x;
       this.y = y;
       intersections.add(this);
       
       for(i = 0; i < 4; ++i){ //0 north, 1 east, 2 south, 3 west
        pattern.add(new Signal());
       }
       
    }
    
    public void addPattern(String signal){
        
    }
    
    public static void tick(){
        for(Intersection light : intersections){
            light.next();
            
            for(Road road : inRoads){
                double chance = rand.nextInt(1000) + 1;
                //System.out.println(chance + " " + (road.AADT/86400) * 1000);
                if(chance <= ((road.AADT * 1.0)/86400.0) * 1000){
                    road.newVehicle();
                    //System.out.println("adding new car to " + road);
                }
            }
        }
    }
    
    public void next(){
        //example code "002202t060102302t060";
        //each 3 digits is a 'block', first for direction of road; second, light of left turn signal; third, light of through signal; t means next block represents time those lights are green for
        if(timer == 0){
            for(k = 0; k < 4; ++k){
                pattern.get(k).left = 0;
                pattern.get(k).through = 0;
            }
        }
        
        if(j >= signal.length()){ 
            j = 0;
        }
        
        while(timer == -3){
            if(signal.charAt(j) == 't'){
                timer = (Integer.parseInt(String.valueOf("" + signal.charAt(j + 1) + signal.charAt(j + 2) + signal.charAt(j + 3))));
                j = j + 4;
            }
            else{
                currPattern = Character.getNumericValue(signal.charAt(j));
                pattern.get(currPattern).left = Character.getNumericValue(signal.charAt(j + 1));
                pattern.get(currPattern).through = Character.getNumericValue(signal.charAt(j + 2));
                j = j + 3;
            }
        }
        
        --timer;
        /*
        if(counter == 0){
            pattern.get(currPattern).through = 0;
            pattern.get(Math.floorMod(currPattern + 2, 4)).through = 0;
            currPattern = Math.floorMod(currPattern + 1, 4);
            //System.out.println(currPattern);
            pattern.get(currPattern).through = 2;
            pattern.get(Math.floorMod(currPattern + 2, 4)).through = 2;
            counter = 60;
        }
        else{
            counter--;
        }
        */
    }

    public static void addRoads(){
        for(Intersection intersection : intersections){
            Road.addToIntersection(intersection);
        }
    }
    
    public static void addToRoads(){
        for(Intersection intersection : intersections){
            for(Road road : inRoads){
                road.intersection = intersection;
            }
            /*
            for(Road road : outRoads){
                road.intersection = intersection;
            }
            */
        }
    }
}
