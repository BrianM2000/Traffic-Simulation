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
          if(neighbor == null && vehicle.position != position && vehicle.position - position > 0){
              neighbor = vehicle;
            }
          else if(vehicle.position - position > 0 && vehicle.position - position < neighbor.position - position && vehicle.position - position != 0){
              neighbor = vehicle;
            }
      }
      
      return neighbor;
    }
    
  public void checkNeighbor(){ //NOT OPTIMAL, 
     //INSTEAD WHEN A VEHICLE LEAVES A LANE SEARCH THE VEHICLES FOR THE ONE WHO HAS IT AS IT'S NEIGHBOR AND SEE THE PREVIOUS VEHICLES NEIGHBOR TO THE LEAVING VEHICLES NEIGHBOR
     // WHEN A VEHICLE ENTERS THE LANE, ONLY UPDATE FOR VEHICLES WHOSE POSITION IS LESS THEN THE NEW VEHICLES 
     for(Vehicle vehicle : vehicles){
          vehicle.mustCheckNeighbor = true;
        }
    }
}
