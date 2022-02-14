import java.util.*;
import java.awt.*;
import java.awt.geom.Path2D;
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
        int i = 0;
        generate();
        
        
        while(count < 3600){//3600 for an hour; esitmate 5 minutes for 1 hour worth of simulated time for all 2,820 intersections in Manhattan
            tick();
            System.out.println(count);
        }
        
       
        for(Road road : Road.roads){
            if(road.AADT == 0){
               ++i;
               System.out.println(road.id);
            }
        }
        System.out.println(Road.roads.size() + " roads");
        System.out.println(Intersection.intersections.size() + " intersections");
        
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        
        Road.printDelay();
        
        System.out.println("done! " + count + " simulated seconds elapsed in " + timeElapsed.toMillis() + " milliseconds");
    }
    
    public static void generate(){ //blank args for now, will read info from xlsx in future
        
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        ArrayList<Vertex> trafficVert = new ArrayList<Vertex>();
        ArrayList<TrafficHelper> trafficHelper = new ArrayList<TrafficHelper>();
        
        //reading traffic Light intersections
        try{
            //FileInputStream file = new FileInputStream(new File("C:\\Users\\thepa\\Desktop\\downtownManhattanLights.xlsx"));
            FileInputStream file = new FileInputStream(new File("E:\\TestRoads\\testLights.xlsx"));
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
                    Intersection temp = new Intersection(new Vertex(tempX, tempY));
                    temp.signal = "002202t060102302t060";
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        
        //get traffic info
        //first, create Path2D an feed it vertices of a road
        //then BasicStroke bs = new BasicStroke(float width);
        //then bs.createStrokedShape(Path2D)
        //then right before roads are declared, find shapes which contain the start and end points of roads
        //shape.contains(start.x, start.y) && shape.contains(end.x, end.y);
        
        try{
            //FileInputStream file = new FileInputStream(new File("C:\\Users\\thepa\\Desktop\\downtownManhattanTraffic.xlsx"));
            FileInputStream file = new FileInputStream(new File("E:\\TestRoads\\testTraffic.xlsx"));
            XSSFWorkbook wb = new XSSFWorkbook(file);
            XSSFSheet sheet = wb.getSheetAt(0);
            
            BasicStroke bs = new BasicStroke((float)0.0003);
            Path2D path = new Path2D.Double();
            Vertex start = null;
            String id = "";
            boolean lastRow = false;
            double tempX = 0;
            double tempY = 0;
            int fedDir = 0;
            int vertInd = 0;
            int AADT = 0;
            
            for(Row row : sheet){
                int i = 0;
                for(Cell cell : row){
                    switch (i) {
                        case 0: id = String.valueOf(cell.getNumericCellValue());
                                break;
                        case 1: fedDir = (int)cell.getNumericCellValue();
                                break;
                        case 2: AADT = (int)cell.getNumericCellValue();
                                break;
                        case 3: vertInd = (int)cell.getNumericCellValue();
                                break;
                        case 4: tempX = cell.getNumericCellValue();
                                break;
                        case 5: tempY = cell.getNumericCellValue();
                                break;
                        default: break;
                            }
                    ++i;
                }

                if(vertInd == 0){
                       start = new Vertex(tempX, tempY);
                       path.moveTo(start.x, start.y);
                   }
                   
                try{
                    double tryTest = (sheet.getRow(row.getRowNum() + 1).getCell(0).getNumericCellValue());
                    lastRow = false;
                }
                catch(Exception e){
                    lastRow = true;
                }
                path.lineTo(tempX, tempY);
                if(lastRow || !String.valueOf(sheet.getRow(row.getRowNum() + 1).getCell(0).getNumericCellValue()).equals(id)){
                    new TrafficHelper(bs.createStrokedShape(path), AADT, fedDir);
                    path.closePath();
                    path = new Path2D.Double();
                    trafficVert.clear();
                }
                if(lastRow){
                    break;
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        //generating objects
         
        try{
            
            boolean split = false;
            boolean lastRow = false;
            boolean oneway = false;
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
            int AADT = 0;
            int fedDir = 0;
            int lanes = 1;
            String turns = "";
            int dirNS;
            int dirEW;
            
            //FileInputStream file = new FileInputStream(new File("C:\\Users\\thepa\\Desktop\\downtownManhattanTest.xlsx"));
            FileInputStream file = new FileInputStream(new File("E:\\TestRoads\\testRoads.xlsx"));
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
                        case 8: tempX = cell.getNumericCellValue();
                                break;
                        case 9: tempY = cell.getNumericCellValue();
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
                AADT = 0;
                fedDir = 0;
                
                for(Cell cell : row){
                    switch (i) {
                        case 0: id = cell.getStringCellValue();
                                //System.out.println(cell);
                                break;
                        case 1: limit = Integer.parseInt(cell.getStringCellValue().substring(0, cell.getStringCellValue().indexOf(" ")));
                                break;
                        case 2: name = cell.getStringCellValue();
                                break;
                        case 3: if(cell.getStringCellValue().equals("yes")){
                                    oneway = true;
                                }
                                break;
                        case 4: start.x = cell.getNumericCellValue();
                                break;
                        case 5: start.y = cell.getNumericCellValue();
                                break;
                        case 6: lanes = ((int)cell.getNumericCellValue());
                                break;
                        case 7: turns = cell.getStringCellValue();
                                break;
                        case 8: tempX = cell.getNumericCellValue();
                                break;
                        case 9: tempY = cell.getNumericCellValue();
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
                            if(Math.abs(inter.vertex.x - closestVrt.x) < 0.000005 && Math.abs(inter.vertex.y - closestVrt.y) < 0.000005
                            && (start.x != inter.vertex.x && start.y != inter.vertex.y)){
                                if(start.x - inter.vertex.x > 0){
                                    dirEW = 7;
                                }
                                else{
                                    dirEW = 3;
                                }
                                if(start.y - inter.vertex.y > 0){
                                    dirNS = 5;
                                }
                                else{
                                    dirNS = 1;
                                }
                                for(TrafficHelper tf : TrafficHelper.trafficHelpers){
                                    if(tf.shape.contains(start.x, start.y) && tf.shape.contains(inter.vertex.x, inter.vertex.y) &&
                                      (dirEW == tf.fedDir || dirNS == tf.fedDir)){
                                        AADT = tf.AADT;
                                        fedDir = tf.fedDir;
                                        System.out.println(AADT + " " + fedDir);
                                    }
                                }
                                dirNS = (dirNS + 4)%8;
                                dirEW = (dirEW + 4)%8;
                                new Road(id, start.x, start.y, inter.vertex.x, inter.vertex.y, limit, fedDir, length, AADT, lanes, turns);
                                if(!oneway){
                                    for(TrafficHelper tf : TrafficHelper.trafficHelpers){
                                        if(tf.shape.contains(start.x, start.y) && tf.shape.contains(inter.vertex.x, inter.vertex.y) &&
                                          (dirEW == tf.fedDir || dirNS == tf.fedDir)){
                                            AADT = tf.AADT;
                                            fedDir = tf.fedDir;
                                        }
                                    }
                                    new Road(id, inter.vertex.x, inter.vertex.y, start.x, start.y, limit, fedDir, length, AADT, lanes, turns);
                                }
                                //System.out.println("break " + inter + " " + inter.vertex.x + " " + inter.vertex.y);
                                length = 0;
                                newRoad = false;
                                start.x = inter.vertex.x;
                                start.y = inter.vertex.y;
                                break;
                            }
                        }
                        //System.out.println("dist " + dist + " " + vertex.x + " " + vertex.y + " " + closestVrt.x + " " + closestVrt.y);
                        
                        roadVertices.remove(vertex);
                        vertex = closestVrt;
                    }
                    if(start.x - vertex.x > 0){
                        dirEW = 7;
                    }
                    else{
                        dirEW = 3;
                    }
                    if(start.y - vertex.y > 0){
                        dirNS = 5;
                    }
                    else{
                        dirNS = 1;
                    }
                    for(TrafficHelper tf : TrafficHelper.trafficHelpers){
                        if(tf.shape.contains(start.x, start.y) && tf.shape.contains(vertex.x, vertex.y) &&
                        (dirEW == tf.fedDir || dirNS == tf.fedDir)){
                            AADT = tf.AADT;
                            fedDir = tf.fedDir;
                        }
                    }
                    dirNS = (dirNS + 4)%8;
                    dirEW = (dirEW + 4)%8;
                    if(newRoad){
                        new Road(id, start.x, start.y, vertex.x, vertex.y, limit, fedDir, length, AADT, lanes, turns);
                        if(!oneway){
                            for(TrafficHelper tf : TrafficHelper.trafficHelpers){
                                if(tf.shape.contains(start.x, start.y) && tf.shape.contains(vertex.x, vertex.y) &&
                                (dirEW == tf.fedDir || dirNS == tf.fedDir)){
                                    AADT = tf.AADT;
                                    fedDir = tf.fedDir;
                                }
                            }
                            new Road(id, vertex.x, vertex.y, start.x, start.y, limit, fedDir, length, AADT, lanes, turns);
                        }
                    }
                    //System.out.println(length);
                    roadVertices.clear();
                    oneway = false;
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
    