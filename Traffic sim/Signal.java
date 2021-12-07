
public class Signal{
    int left = 0;
    int through = 0;
    //0 = red //1 = yellow //2 = green
    
    public Signal(){
        
    }
    
    public boolean getTurn(int i){
        if(i == 2){
            return left == 2;
        }
        else{
            return through == 2;
        }
    }
}
