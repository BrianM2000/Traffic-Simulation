import java.util.*;

public class Lane{
  boolean left;
  boolean through;
  boolean right;
  Queue<Vehicle> vehicles = new LinkedList<>();
  
  public Lane(boolean left, boolean through, boolean right){
      this.left = left;
      this.through = through;
      this.right = right;
  }
  
  public Vehicle getNeighbor(double position){
      Vehicle neighbor = null;
      for(Vehicle vehicle : vehicles){
          if(neighbor == null && vehicle.position != position){
              neighbor = vehicle;
            }
          else if(vehicle.position - position > 0 && vehicle.position - position < neighbor.position - position && vehicle.position - position != 0){
              neighbor = vehicle;
            }
      }
      
      return neighbor;
    }
    
  public void checkNeighbor(){
      for(Vehicle vehicle : vehicles){
          vehicle.mustCheckNeighbor = true;
        }
    }
}
