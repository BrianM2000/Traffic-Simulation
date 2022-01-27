import java.util.*;

public class Intersection{
    //TrafficLight light;
    static ArrayList<Road> inRoads = new ArrayList<Road>();
    static ArrayList<Road> outRoads = new ArrayList<Road>();
    Vertex vertex;
    ArrayList<Signal> pattern = new ArrayList<Signal>();
    int timer = 0;
    int currPattern = 0;
    int counter;
    int max;
    int i;
    int j = 0;
    int k = 0;
    String signal = "0";
    static ArrayList<Intersection> intersections = new ArrayList<Intersection>();
    static Random rand = new Random(1);
    
    public Intersection(Vertex vertex){
       this.vertex = vertex;
       intersections.add(this);
    }
    
    public void generatePattern(){
        for(i = 0; i < inRoads.size(); ++i){ //0 north, 1 east, 2 south, 3 west
        pattern.add(new Signal());
       }
       
       if(signal.equals("0")){
           for(k = 0; k < inRoads.size(); ++k){
                pattern.get(k).left = 2;
                pattern.get(k).through = 2;
            }
       }
       if(signal.equals("1")){
           for(k = 0; k < inRoads.size(); ++k){
                pattern.get(k).left = 1;
                pattern.get(k).through = 1;
            }
       }
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
        if(!signal.equals("0") || !signal.equals("1")){
            if(timer == 0){
                for(k = 0; k < inRoads.size(); ++k){
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
        }
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
