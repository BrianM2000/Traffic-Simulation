import java.util.*;
import java.time.Duration;
import java.time.Instant;
import java.io.File;  
import java.io.FileInputStream;  
import java.util.Iterator;  
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.xssf.usermodel.XSSFSheet;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TrafficSim{
    static int count = 0;
    public static void main(String args[]){
        Instant start = Instant.now();
       
        generate();
        /*
        while(count < 3600){//3600 for an hour; esitmate 5 minutes for 1 hour worth of simulated time for all 2,820 intersections in Manhattan
            tick();
        }
        */

        System.out.println(Road.roads.size() + " roads");
        System.out.println(Intersection.intersections.size() + " intersections");
       
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        
        //Road.printDelay();
        
        System.out.println("done! " + count + " simulated seconds elapsed in " + timeElapsed.toMillis() + " milliseconds");
    }
    
    public static void generate(){ //blank args for now, will read info from xlsx in future
        //temp hard coded generation
        //Road CedarStW = new Road(-74.00983, 40.70812, -74.01284, 40.70971, 25, 7, 311.03021, 3, "");
        //Road CedarStE = new Road(-74.01284, 40.70971, -74.00983, 40.70812, 25, 3, 311.03021, 3,"left|through|right");
        /*
        Road SouthBroadwayN = new Road(-74.00953, 40.71032, -74.00906, 40.71088, 25, 1, 74.23706, 3264, 3,"left||right");
        Road SouthBroadwayS = new Road(-74.00906, 40.71088, -74.00953, 40.71032, 25, 5, 74.23706, 3264, 3,"left|through|right");
        Road NorthBroadwayN = new Road(-74.00906, 40.71088, -74.00865, 40.71138, 25, 1, 65.46808, 3264, 3,"left|through|right");
        Road NorthBroadwayS = new Road(-74.00865, 40.71138, -74.00906, 40.71088, 25, 5, 65.46808, 3264, 3,"left|through|right");
        Road WestFultonE = new Road(-74.01043, 40.71152, -74.00906, 40.71088, 25, 3, 135.86778, 1779, 3,"left|through|right");
        Road WestFultonW = new Road(-74.00906, 40.71088, -74.01043, 40.71152, 25, 7, 135.86778, 1779, 3,"left|through|right");
        Road EastFultonE = new Road(-74.00906, 40.71088, -74.00775, 40.71022, 25, 3, 133.22134, 4767, 3,"left|through|right");
        Road EastFultonW = new Road(-74.00775, 40.71022, -74.00906, 40.71088, 25, 3, 133.22134, 4767, 3,"left|through|right");
        */
        
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        ArrayList<Vertex> toIntersection = new ArrayList<Vertex>();
        
        //reading traffic Light intersections
        try{
            FileInputStream file = new FileInputStream(new File("C:\\Users\\thepa\\Desktop\\downtownManhattanLights.xlsx"));
            XSSFWorkbook wb = new XSSFWorkbook(file);
            XSSFSheet sheet = wb.getSheetAt(0);
            int i;
            for(Row row : sheet){
                i = 0;
                double tempX = 0;
                double tempY = 0;
                              
                for(Cell cell : row){
                    switch (i) {
                        case 0: tempX = cell.getNumericCellValue();
                                break;
                        case 1: tempY = cell.getNumericCellValue();
                                break;
                            }
                    ++i;
                }
                if(tempX != 0.0 && tempY != 0.0){
                    new Intersection(new Vertex(tempX, tempY));
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        /*
        Vehicle v1 = new Vehicle("CarLeft", 15, 25, 0, 0, SouthBroadwayN, WestFultonW, 0);
        Vehicle v4 = new Vehicle("CarBlocking", 30, 25, 0, .00284091, SouthBroadwayN, WestFultonW, 0);
        Vehicle v3 = new Vehicle("CarT", 15, 0, 0, .0028409*4, SouthBroadwayN, NorthBroadwayN, 1);
        Vehicle v2 = new Vehicle("CarThrough", 15, 25, 0, 0, NorthBroadwayS, SouthBroadwayS, 0);
        
        Vehicle car1 = new Vehicle("Car",15, 25, 0, 0, CedarStW, Broadway, 0);
        Vehicle car2 = new Vehicle("Motorcycle",10, 0, 0, .06, CedarStW, CedarStW, 0);
        Vehicle car3 = new Vehicle("Truck",72, 0, 10, 0.2, Broadway, CedarStE, 1);
        */
       
        //generating objects
        boolean split = false;
        boolean lastRow = false;
        double length = 0;
        double dist = 10000000;
        double between = 0;
        
        ArrayList<Vertex> roadVertices = new ArrayList<Vertex>();
        Vertex closestVrt = null;
        Vertex start = new Vertex(0,0);
        Vertex vertex = new Vertex(0,0);
        String id = "";
        String name = "";
        int limit = 0;
        int direction = 0;
        int lanes = 1;
        String turns = ""; 
        try{
            FileInputStream file = new FileInputStream(new File("C:\\Users\\thepa\\Desktop\\downtownManhattanTest.xlsx"));
            XSSFWorkbook wb = new XSSFWorkbook(file);
            XSSFSheet sheet = wb.getSheetAt(0);
            int i = 0;
            int j = 0;
            int k = 0;
            
            for(Row row : sheet){//non-light intersections generation
                i = 0;
                double tempX = 0;
                double tempY = 0;
                for(Cell cell : row){
                    switch (i) {
                        case 0: id = cell.getStringCellValue();
                                break;
                        case 7: tempX = cell.getNumericCellValue();
                                break;
                        case 8: tempY = cell.getNumericCellValue();
                                break;
                        default: break;
                            }
                    ++i;
                }
                IntersectionBuilder.build(new Vertex(tempX, tempY, id));
            }

            for(IntersectionBuilder ib : IntersectionBuilder.IB){
                boolean already = false;
                
                for(Intersection inter : Intersection.intersections){
                    if(Math.abs(inter.vertex.x - ib.vertex.x) < 0.000005 && Math.abs(inter.vertex.y - ib.vertex.y) < 0.000005){
                        already = true;
                    }
                }
                
                if((ib.count > 0) && !already){//NOT THE RIGHT PLACE To ASSING SIGNAL, MUST COME AFTER I KNOW WHAT ROADS ARE A PART OF INTERSECTION
                    Intersection temp = new Intersection(ib.vertex);
                }
            }

            for(Row row : sheet){//road generation
                i = 0;
                double tempX = 0;
                double tempY = 0;
                
                for(Cell cell : row){
                    switch (i) {
                        case 0: id = cell.getStringCellValue();
                                //System.out.println(cell);
                                break;
                        case 1: limit = Integer.parseInt(cell.getStringCellValue().substring(0, cell.getStringCellValue().indexOf(" ")));
                                break;
                        case 2: name = cell.getStringCellValue();
                                break;
                        case 3: start.x = cell.getNumericCellValue();
                                break;
                        case 4: start.y = cell.getNumericCellValue();
                                break;
                        case 5: lanes = ((int)cell.getNumericCellValue());
                                break;
                        case 6: turns = cell.getStringCellValue();
                                break;
                        case 7: tempX = cell.getNumericCellValue();
                                break;
                        case 8: tempY = cell.getNumericCellValue();
                                break;

                        default: break;
                    }
                    ++i;
                    
                    vertex = start;
                }
                
                try{
                    String tryTest = (sheet.getRow(row.getRowNum() + 1).getCell(0).getStringCellValue());
                    lastRow = false;
                }
                catch(Exception e){
                    lastRow = true;
                }
                roadVertices.add(new Vertex(tempX, tempY, id));
                if(lastRow || sheet.getRow(row.getRowNum() + 1).getCell(0).getStringCellValue() != id){
                    //System.out.println(id);
                    length = 0;
                    boolean newRoad = true;
                    int numVertices = roadVertices.size();
                    Intersection toRemove = null;
                    closestVrt = null;
                    for(j = 0; j < numVertices; ++j){
                        newRoad = true;
                        dist = 100000;
                        for(Vertex nextVrt : roadVertices){
                            between = gcDist(vertex.x, vertex.y, nextVrt.x, nextVrt.y);
                            //System.out.println(between + " " + dist);
                            if(between < dist && vertex != nextVrt){
                                //System.out.println(between);
                                dist = between;
                                closestVrt = nextVrt;
                            }
                        }
                        length = length + dist;
                        //System.out.println(length);
                        for(Intersection inter : Intersection.intersections){
                            if(Math.abs(inter.vertex.x - closestVrt.x) < 0.000005 && Math.abs(inter.vertex.y - closestVrt.y) < 0.000005){
                                new Road(id, start.x, start.y, inter.vertex.x, inter.vertex.y, limit, 0, length, 0, lanes, turns);
                                //System.out.println("break " + inter + " " + inter.vertex.x + " " + inter.vertex.y);
                                length = 0;
                                newRoad = false;
                                break;
                            }
                        }
                        //System.out.println("dist " + dist + " " + vertex.x + " " + vertex.y + " " + closestVrt.x + " " + closestVrt.y);
                        
                        roadVertices.remove(vertex);
                        vertex = closestVrt;
                    }
                    if(newRoad){
                        new Road(id, start.x, start.y, vertex.x, vertex.y, limit, 0, length, 0, lanes, turns);
                    }
                    //System.out.println(length);
                    roadVertices.clear();
                }
                if(lastRow){
                    break;
                }
            }
        }
        
        catch(Exception e){
             e.printStackTrace();
        }
        
        Road.addLanes();
        
        //Intersection BroadwayFulton = new Intersection(new Vertex(-74.00906, 40.71088));
        //BroadwayFulton.signal = "002202t060102302t060"; //each 3 digits is a 'block', first for direction of road; second, light of left turn signal; third, light of through signal; t means next block represents time those lights are green for
        
        Intersection.addRoads();
        Intersection.addToRoads();
        
        for(Intersection inter : Intersection.intersections){
            if(inter.signal.equals("0")){
                if(inter.inRoads.size() == 1){
                    
                }
                else if(inter.inRoads.size() > 1){
                    inter.signal = "1";
                }
            }
            inter.generatePattern();
        }
        
        for(Intersection inter : Intersection.intersections){
            inter.generatePattern();
        }
        
    }
    
    public static void tick(){
        Intersection.tick();
        Vehicle.tick();
        ++count;
    }
    
    public static double gcDist(double lon1, double lat1, double lon2, double lat2){
        double r = 6371e3;
        double φ1 = lat1 * Math.PI/180;
        double φ2 = lat2 * Math.PI/180;
        double Δφ = (lat2 - lat1) * Math.PI/180;
        double Δλ = (lon2 - lon1) * Math.PI/180;
        
        double a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                   Math.cos(φ1) * Math.cos(φ2) *
                   Math.sin(Δλ/2) * Math.sin(Δλ/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        //System.out.println(lon1 + " " + lat1 + " " + lon2 + " " + lat2);
        //System.out.println(r * c);
        return r * c; //should be meters
    }
    
    public static double pointLineDist(Vertex point, Vertex start, Vertex end){
        return Math.abs((end.x - start.x)*(start.y - point.y) - (start.x - point.x)*(end.y - start.y))/
        Math.sqrt(Math.pow((end.x - start.x),2) + Math.pow((end.y - start.y),2));
    }
}
    