import javax.swing.*;
import java.awt.*;

public class DelayMap extends Canvas{
    
    public DelayMap(){}

    public DelayMap(Canvas canvas){ //1536, 864
        JFrame frame = new JFrame();
        frame.add(canvas);
        frame.getContentPane().setBackground(Color.gray);
        frame.pack();
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public void paint(Graphics g){
        double zeroX = 0;
        double zeroY = 0;
        double maxX = -1536;
        double maxY = 864;
        int maxDelay = 0;
        for(Road road : Road.roads){

            if(road.totalDelay > maxDelay){
                maxDelay = (int)road.totalDelay;
            }
            //find top left
            if(road.startX < zeroX){
                zeroX = road.startX;
            }
            if(road.endX < zeroX){
                zeroX = road.endX;
            }
            if(road.startY > zeroY){
                zeroY = road.startY;
            }
            if(road.endY > zeroY){
                zeroY = road.endY;
            }

            //find bottom right
            if(road.startX > maxX){
                maxX = road.startX;
            }
            if(road.endX > maxX){
                maxX = road.endX;
            }
            if(road.startY < maxY){
                maxY = road.startY;
            }
            if(road.endY < maxY){
                maxY = road.endY;
            }
            
        }
        //System.out.println(maxDelay);

        for(Road road : Road.roads){
            int startX, startY, endX, endY;

            startX = (int)Math.round((road.startX - zeroX) * 40000) + 400;
            startY = (int)-Math.round((road.startY - zeroY) * 40000);
            endX = (int)Math.round((road.endX - zeroX) * 40000) + 400;
            endY = (int)-Math.round((road.endY - zeroY) * 40000);

            
            if(road.totalDelay <= 0){
                g.setColor(Color.green);
            }
            else{
                int delayColor = (int)((255-50) - (Math.log(road.totalDelay)/Math.log(maxDelay) * (255-50)));
                
                g.setColor(new Color(255, delayColor, delayColor));
            }
        
            if(road.federalDirection == 0){
                g.setColor(Color.blue);
            }
            g.drawLine(startX, startY, endX, endY);
        }

        g.setColor(Color.black);

        for(Intersection inter : Intersection.intersections){
            if(!inter.signal.equals("0") && !inter.signal.equals("1") && inter.inRoads.size() > 0){ //&& inter.inRoads.size() > 0
                int x, y;
                x = (int)Math.round((inter.vertex.x - zeroX) * 40000) + 400;
                y = (int)-Math.round((inter.vertex.y - zeroY) * 40000);

                g.fillOval(x-2,y-2,4,4);
            }
        }

    }

}
