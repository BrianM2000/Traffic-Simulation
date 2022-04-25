import java.util.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.time.Duration;
import java.time.Instant;
import java.io.*;    
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.xssf.usermodel.XSSFSheet;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TrafficSim{
    static int count = 0;
    static String filePath = "E:\\Java\\Traffic sim\\AlgroDelay.txt";
    public static void main(String args[]){
        Instant start = Instant.now();
        int i = 0;
        generate();
        String location = writeToFile();
        
        System.out.println("Enviorment generated successfully! Written to " + location);

        while(count < 3600){//3600 for an hour;
            tick();
            System.out.println(count);
            //System.out.println(" num vehicles: " + Vehicle.vehicles.size());
        }
        
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        
        Road.printDelay();
        saveDelay(filePath);
        
        System.out.println("done! " + count + " simulated seconds elapsed in " + timeElapsed.toMillis() + " milliseconds");

        Canvas canvas = new DelayMap();
        canvas.setSize(1536, 864);
        new DelayMap(canvas);

        boolean test = true;
        if(test){
            lightAlgor("E:\\Java\\Traffic sim\\AlgroDelay.txt");
        }
    }
    
    public static void generate(){ //blank args for now, will read info from xlsx in future
        boolean readFromFile = true;
        
        if(readFromFile){
            try{
                File file = new File(filePath);
                Scanner scnr = new Scanner(file);
                scnr.next();
                scnr.next();
                while(scnr.hasNext() && !scnr.hasNext("//Roads")){
                    String input = scnr.next();
                    String[] inputArray = input.split(",");
                    //System.out.println(inputArray[0] + " " + inputArray[1] + " " + inputArray[2]);
                    new Intersection(new Vertex(Double.parseDouble(inputArray[0]),Double.parseDouble(inputArray[1])),inputArray[2]).generatePattern();;
                }
                scnr.next();
                while(scnr.hasNext()){
                    String input = scnr.next();
                    //System.out.println(input);
                    String[] inputArray = input.split(",");
                    new Road(inputArray[0], 
                    Double.parseDouble(inputArray[1]), 
                    Double.parseDouble(inputArray[2]), 
                    Double.parseDouble(inputArray[3]),
                    Double.parseDouble(inputArray[4]), 
                    Integer.parseInt(inputArray[5]), 
                    Integer.parseInt(inputArray[6]), 
                    Double.parseDouble(inputArray[7]), 
                    Integer.parseInt(inputArray[8]), 
                    Integer.parseInt(inputArray[9]), 
                    inputArray[10]);
                }
                Intersection.addRoads();
                Intersection.addToRoads();
                Road.addLanes();
            }
            catch(Exception e){
                e.printStackTrace();
            }

        }

        if(!readFromFile){
            ArrayList<Vertex> trafficVert = new ArrayList<Vertex>();
            
            //reading traffic Light intersections
            try{
                //FileInputStream file = new FileInputStream(new File("C:\\Users\\thepa\\Desktop\\downtownManhattanLights.xlsx"));
                FileInputStream file = new FileInputStream(new File("E:\\TestRoads\\DowntownLights.xlsx"));
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
                        temp.signal = "002202t060102302t060"; //"002202t060102302t060"; //"000100200300t060"; //"002102202302t060"
                    }
                }
                wb.close();
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
                FileInputStream file = new FileInputStream(new File("E:\\TestRoads\\DowntownTraffic.xlsx"));
                XSSFWorkbook wb = new XSSFWorkbook(file);
                XSSFSheet sheet = wb.getSheetAt(0);
                
                BasicStroke bs = new BasicStroke((float)0.0005);
                Path2D path = new Path2D.Double();
                Vertex start = null;
                String id = "";
                boolean oneway = false;
                boolean lastRow = false;
                double tempX = 0;
                double tempY = 0;
                int fedDir = 0;
                int vertInd = 0;
                int AADT = 0;
                //Traffic data
                for(Row row : sheet){
                    int i = 0;
                    for(Cell cell : row){
                        switch (i) {
                            case 0: id = String.valueOf(cell.getNumericCellValue());
                                    break;
                            case 1: if(cell.getStringCellValue().contains("Y")){
                                        oneway = true;
                            }
                            else{
                                oneway = false;
                            }
                                    break;
                            case 2: fedDir = (int)cell.getNumericCellValue();
                                    break;
                            case 3: AADT = (int)cell.getNumericCellValue();
                                    break;
                            case 4: vertInd = (int)cell.getNumericCellValue();
                                    break;
                            case 5: tempX = cell.getNumericCellValue();
                                    break;
                            case 6: tempY = cell.getNumericCellValue();
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
                        new TrafficHelper(bs.createStrokedShape(path), AADT, fedDir, oneway);
                        path.closePath();
                        path = new Path2D.Double();
                        trafficVert.clear();
                    }
                    if(lastRow){
                        break;
                    }
                }
                wb.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            
            //generating objects
            
            try{
                
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
                FileInputStream file = new FileInputStream(new File("E:\\TestRoads\\SmallDowntownRoads.xlsx"));
                XSSFWorkbook wb = new XSSFWorkbook(file);
                XSSFSheet sheet = wb.getSheetAt(0);
                int i = 0;
                int j = 0;
                
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
                        if(Math.abs(inter.vertex.x - ib.vertex.x) < 0.00002 && Math.abs(inter.vertex.y - ib.vertex.y) < 0.00002){
                            already = true;
                        }
                    }
                    
                    if((ib.count > 0) && !already){
                        Intersection temp = new Intersection(ib.vertex);
                    }
                }

                for(Row row : sheet){//road generation
                    i = 0;
                    double tempX = 0;
                    double tempY = 0;
                    AADT = 0;
                    fedDir = 0;
                    oneway = false;
                    
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
                                if(Math.abs(inter.vertex.x - closestVrt.x) < 0.00002 && Math.abs(inter.vertex.y - closestVrt.y) < 0.00002
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
                                            //System.out.println(AADT + " " + fedDir);
                                            
                                            if(tf.oneway == true){
                                                oneway = true;
                                            } 
                                            
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
                wb.close();
            }
            
            catch(Exception e){
                e.printStackTrace();
            }
            
            //BroadwayFulton.signal = "002202t060102302t060"; //each 3 digits is a 'block', first for direction of road; second, light of left turn signal; third, light of through signal; t means next block represents time those lights are green for
            
            Intersection.addRoads();
            Intersection.addToRoads();
            
            //THIS ONLY WORKS FOR ONEWAY ROADS!!!!!!!
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
            
            if(false){ //incase I wanna turn on filling in empty data later
                for(Intersection inter : Intersection.intersections){
                    inter.generatePattern();
                    
                    for(Road road : inter.outRoads){
                        if(road.federalDirection == 0){
                            int dirEW = 0;
                            int dirNS = 0;
                            
                            if(road.startX - road.endX > 0){
                                dirEW = 7;
                            }
                            else{
                                dirEW = 3;
                            }
                            if(road.startY - road.endY > 0){
                                dirNS = 5;
                            }
                            else{
                                dirNS = 1;
                            }
                            Road compare;
                            if(inter.inRoads.size() > 0 && inter.inRoads.get(0) == road && inter.outRoads.size() > 1){
                                compare = inter.outRoads.get(1);
                            }
                            else{
                                compare = inter.outRoads.get(0);
                            }
                            double m1 = ((road.startY - road.endY)/(road.startX - road.endX) + .00000001);
                            double m2 = ((compare.startY - compare.endY)/(compare.startX - compare.endX) + .00000001);
                            double angle = Math.abs(Math.toDegrees(Math.atan(((m1)-(m2)))/(1+(m1)*(m2))));
                            //System.out.println(road.id + " " + compare.id + " " + angle + " " + dirNS + " " + dirEW + " " + compare.federalDirection);
                            if(angle > 130 && angle < 230){
                                if(Math.floorMod(compare.federalDirection - dirEW, 8) == 4){
                                    road.federalDirection = dirEW;
                                }
                                else if(Math.floorMod(compare.federalDirection - dirNS, 8) == 4){
                                    road.federalDirection = dirNS;
                                }
                            }
                            else{
                                if(Math.abs(compare.federalDirection - dirEW%8) == 2){
                                    road.federalDirection = dirEW;
                                }
                                else if(Math.abs(compare.federalDirection - dirNS%8) == 2){
                                    road.federalDirection = dirNS;
                                }
                            }
                        }
                    }
                    
                    //Fill in FedDir for roads missing that data in spreadsheet
                    for(Road road : inter.inRoads){
                        if(road.federalDirection == 0){
                            int dirEW = 0;
                            int dirNS = 0;
                            
                            if(road.startX - road.endX > 0){
                                dirEW = 7;
                            }
                            else{
                                dirEW = 3;
                            }
                            if(road.startY - road.endY > 0){
                                dirNS = 5;
                            }
                            else{
                                dirNS = 1;
                            }
                            Road compare;
                            if(inter.inRoads.get(0) == road && inter.inRoads.size() > 1){
                                compare = inter.inRoads.get(1);
                            }
                            else{
                                compare = inter.inRoads.get(0);
                            }
                            double m1 = ((road.startY - road.endY)/(road.startX - road.endX) + .00000001);
                            double m2 = ((compare.startY - compare.endY)/(compare.startX - compare.endX) + .00000001);
                            double angle = Math.abs(Math.toDegrees(Math.atan(((m1)-(m2)))/(1+(m1)*(m2))));
                            //System.out.println(road.id + " " + compare.id + " " + angle + " " + dirNS + " " + dirEW + " " + compare.federalDirection);
                            if(angle > 130 && angle < 230){
                                if(Math.floorMod(compare.federalDirection - dirEW, 8) == 4){
                                    road.federalDirection = dirEW;
                                }
                                else if(Math.floorMod(compare.federalDirection - dirNS, 8) == 4){
                                    road.federalDirection = dirNS;
                                }
                            }
                            else{
                                if(Math.abs(compare.federalDirection - dirEW%8) == 2){
                                    road.federalDirection = dirEW;
                                }
                                else if(Math.abs(compare.federalDirection - dirNS%8) == 2){
                                    road.federalDirection = dirNS;
                                }
                            }
                            //System.out.println(road.federalDirection);
                        }
                    }
                }
            }
            Road.addLanes();
        }
    }
    
    public static String writeToFile(){
        try{
            String location = "E:\\Java\\Traffic sim\\roads.txt";
            FileWriter roadWriter = new FileWriter(location);
            roadWriter.write(Instant.now() + "\n //Intersections \n");
            for(Intersection inter : Intersection.intersections){
                roadWriter.write(inter.vertex.x + "," + 
                                 inter.vertex.y + "," + 
                                 inter.signal + "\n");
            }
            roadWriter.write("//Roads \n");
            for(Road road : Road.roads){
                roadWriter.write(road.id + "," +
                                 road.startX + "," +
                                 road.startY + "," +
                                 road.endX + "," +
                                 road.endY + "," + 
                                 road.speedLimit + "," +
                                 road.federalDirection + "," +
                                 (road.length/0.000621371) + "," +
                                 road.AADT + "," +
                                 road.numLanes + "," +
                                 road.turnLanes + "\n");
            }
            roadWriter.close();
            return location;
        }
        catch(Exception e){
            return "Error!";
        }
    }
    
    public static void tick(){
        Intersection.tick();
        Vehicle.tick();
        ++count;
    }
    
    public static double gcDist(double lon1, double lat1, double lon2, double lat2){
        double r = 6371e3;
        double p1 = lat1 * Math.PI/180;
        double p2 = lat2 * Math.PI/180;
        double dp = (lat2 - lat1) * Math.PI/180;
        double dl = (lon2 - lon1) * Math.PI/180;
        
        double a = Math.sin(dp/2) * Math.sin(dp/2) +
                   Math.cos(p1) * Math.cos(p2) *
                   Math.sin(dl/2) * Math.sin(dl/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        //System.out.println(lon1 + " " + lat1 + " " + lon2 + " " + lat2);
        //System.out.println(r * c);
        return r * c; //should be meters
    }
    
    public static double pointLineDist(Vertex point, Vertex start, Vertex end){
        return Math.abs((end.x - start.x)*(start.y - point.y) - (start.x - point.x)*(end.y - start.y))/
        Math.sqrt(Math.pow((end.x - start.x),2) + Math.pow((end.y - start.y),2));
    }

    public static String saveDelay(String path){
        try{
            String location = path;
            FileWriter roadWriter = new FileWriter(location);
            roadWriter.write(Instant.now() + "\n //Intersections \n");
            for(Intersection inter : Intersection.intersections){
                roadWriter.write(inter.vertex.x + "," + 
                                 inter.vertex.y + "," + 
                                 inter.signal + "\n");
            }
            roadWriter.write("//Roads \n");
            for(Road road : Road.roads){
                roadWriter.write(road.id + "," +
                                 road.startX + "," +
                                 road.startY + "," +
                                 road.endX + "," +
                                 road.endY + "," + 
                                 road.speedLimit + "," +
                                 road.federalDirection + "," +
                                 (road.length/0.000621371) + "," +
                                 road.AADT + "," +
                                 road.numLanes + "," +
                                 road.turnLanes + "," + 
                                 road.totalDelay + "\n");
            }
            roadWriter.close();
            return location;
        }
        catch(Exception e){
            return "Error!";
        }
    }

    public static void lightAlgor(String path){
        for(Intersection inter : Intersection.intersections){
            if(!inter.signal.equals("0") && !inter.signal.equals("1")){
                int nsMax = 1, ewMax = 1;
                String ns, ew;
                for(Road road : inter.inRoads){
                    if((road.federalDirection == 1 || road.federalDirection == 5) && road.AADT > nsMax){
                        nsMax = road.AADT;
                    }
                    if((road.federalDirection == 3 || road.federalDirection == 7) && road.AADT > ewMax){
                        ewMax = road.AADT;
                    }
                    
                }
                    if(nsMax > ewMax){ //"002202t060102302t060";
                        ns = "002202t120";
                        int temp = (int)Math.ceil(((ewMax*1.0)/(nsMax*1.0) * 120));
                        if(temp < 30){
                            temp = 30;
                        }
                        String Stemp = String.valueOf(temp);
                        if(Stemp.length() < 3){
                            Stemp = "0".concat(Stemp);
                        }
                        ew = "102302t" + (Stemp);
                        inter.signal = ns + ew;
                    }
                    else{
                        ew = "102302t120";
                        int temp = (int)Math.ceil(((nsMax*1.0)/(ewMax*1.0) * 120));
                        if(temp < 30){
                            temp = 30;
                        }
                        String Stemp = String.valueOf(temp);
                        if(Stemp.length() < 3){
                            Stemp = "0".concat(Stemp);
                        }
                        ns = "002202t" + (Stemp);
                        inter.signal = ns + ew;

                }
            }
            
        }
        saveDelay(path);
    }

}


    