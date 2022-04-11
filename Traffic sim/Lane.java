import java.util.*;

public class Lane{
  boolean left;
  boolean through;
  boolean right;
  ArrayList<Vehicle> vehicles = new ArrayList<>();
  
  public Lane(boolean left, boolean through, boolean right){
      this.left = left;
      this.through = through;
      this.right = right;
  }
  
  public Vehicle getNeighbor(Vehicle neighborless){
      Vehicle neighbor = null;
      
      for(Vehicle vehicle : vehicles){
          if(neighbor == null && vehicle.position != neighborless.position && vehicle.position - neighborless.position > 0){
              neighbor = vehicle;
            }
          else if(vehicle.position - neighborless.position > 0 && vehicle.position - neighborless.position < neighbor.position - neighborless.position && vehicle.position - neighborless.position != 0){
              neighbor = vehicle;
            }
      }
      
      /* should be faster, needs along with faster lane changing; but for some reason the two give weird results ¯\_(ツ)_/¯
      if(this.vehicles.indexOf(neighborless) == 0){
        neighbor = null;
      }
      else{
        neighbor = this.vehicles.get(this.vehicles.indexOf(neighborless) - 1);
      }
      */
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
