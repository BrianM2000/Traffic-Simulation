import java.awt.*;
import java.util.ArrayList;

public class TrafficHelper{
    Shape shape;
    int AADT;
    int fedDir;
    boolean oneway;
    static ArrayList<TrafficHelper> trafficHelpers = new ArrayList<TrafficHelper>();
    
    public TrafficHelper(Shape shape, int AADT, int fedDir, boolean oneway){
      this.shape = shape;
      this.AADT = AADT;
      this.fedDir = fedDir;
      this.oneway = oneway;
      
      trafficHelpers.add(this);
    }

}
