import java.awt.*;
import java.util.ArrayList;

public class TrafficHelper{
    Shape shape;
    int AADT;
    int fedDir;
    static ArrayList<TrafficHelper> trafficHelpers = new ArrayList<TrafficHelper>();
    
    public TrafficHelper(Shape shape, int AADT, int fedDir){
      this.shape = shape;
      this.AADT = AADT;
      this.fedDir = fedDir;
      
      trafficHelpers.add(this);
    }

}
