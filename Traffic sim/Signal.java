
public class Signal{
    int left = 0;
    int through = 0;
    //0 = red //1 = yellow //2 = green
    
    public Signal(){
        
    }
    
    public int getTurn(int i){
        if(i == 2){
            if(left == 0){
                return through;
            }
            return left;
        }
        else{
            return through;
        }
    }
}
