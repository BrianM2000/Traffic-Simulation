import java.util.ArrayList;

public class IntersectionBuilder{
    int count = 0;
    Vertex vertex;
    static ArrayList<IntersectionBuilder> IB = new ArrayList<IntersectionBuilder>();
    public IntersectionBuilder(Vertex vertex){
        this.vertex = vertex;
        IB.add(this);
    }

    public static void build(Vertex vrt){
        boolean overlap = false;
        for(IntersectionBuilder ib : IB){
            if(vrt.x == ib.vertex.x && vrt.y == ib.vertex.y){
                ++ib.count;
                overlap = true;
            }
        }
        if(!overlap){
            new IntersectionBuilder(vrt);
        }
    }
}
