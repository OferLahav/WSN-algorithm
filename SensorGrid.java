
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CopyOnWriteArrayList;
    	

public class SensorGrid
{
	static int LENGTH = 100;
	int counter = 0;
	int squareSize = 5;
	
	
	static Sensor border = new Sensor(999,999); // Used to distinguish the ends of the grid
	static Sensor [][] sensorGrid = new Sensor [LENGTH][LENGTH];
	
	static long networkLifeSpan;
	

	//static int counter = 0;
	
	/*************Visual********/
	static JFrame frame = new JFrame();
	JPanel panel = new MyRectangleJPanel(); // changed this line
	Scanner input = new Scanner(System.in);
		
	
	public SensorGrid(){
	
		for (int i = 0; i < sensorGrid.length; i++){
			for (int j = 0; j < sensorGrid.length; j++){
				sensorGrid[j][i] = new Sensor(j,i);
			}
		}
		
		System.out.println("\n");
		
		for (int i = sensorGrid.length - 1; i >= 0; i--){
			for (int j = sensorGrid.length - 1; j >= 0; j--){
				init(sensorGrid[j][i]);
			}
		}
		
		initFrame();
		networkLifeSpan = System.currentTimeMillis();
		sensorGrid[0][0].setHead(true);
		sensorGrid[0][0].activate(false);
	}
	
	public Sensor getSensor(int x, int y){
		return sensorGrid[x][y];
	}
	
	public void initFrame(){	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setSize(620, 640);
	}	

	public void init(Sensor s){
		/****** Check and set the corners  *****/
		//System.out.println(" sensorNet.length: "+sensorNet.length+"    x : "+x+" y :"+y +"");
		int x = s.getX();
		int y = s.getY();
		if (x == 999) {s.setNeighbors(border,border,border,border);} // border points to itself.
		
		
		//setNeighbors(Sensor N, Sensor E, Sensor S, Sensor W){
		Sensor South,North,East,West;
		/*the walls*/
				
			if ( (y - 1) >= 0 )					{North = sensorGrid[x][y-1];}
			else{ North = sensorGrid[x][y+1];}	
			
			if( x + 1 < sensorGrid.length )		{East = sensorGrid[x+1][y];}
			else{East = border;}	
			
			if( y + 1 < sensorGrid.length )		{South = sensorGrid[x][y+1];}
			else{ South = sensorGrid[x][y-1];}
			
			if( (x - 1) >= 0 )					{West = sensorGrid[x-1][y];}
			else{West =  border;}	
		
		s.setNeighbors(North, East, South, West);
	}
	
	public static int randInt(int min, int max) {

    // NOTE: Usually this should be a field rather than a method
    // variable so that it is not re-seeded every call.
    Random rand = new Random();

    // nextInt is normally exclusive of the top value,
    // so add 1 to make it inclusive
    int randomNum = rand.nextInt((max - min) + 1) + min;

    return randomNum;
}
	
	public void printAll(){
		counter++;

		
		if (counter >= 20000)// just to stop it at any point
			System.exit(0);
			
        
		frame.remove(panel);
        frame.add(panel);
		
		panel.revalidate();
		panel.repaint();

        Graphics g = panel.getGraphics();
			
		
        frame.validate(); // because you added panel after setVisible was called
        frame.repaint(); // because you added panel after setVisible was called	
		
	}
	
	class MyRectangleJPanel extends JPanel {
		
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLUE);
			int xc = 1;
			int yc = 1;
			
			DetectableObjList obj1 = new DetectableObjList();
			CopyOnWriteArrayList <DetectableObject> objectList =  obj1.getDetectableObjList();
			
			
			for (int i = 0; i < sensorGrid.length; i++){
				for (int j = 0; j < sensorGrid.length; j++){
				
					for (DetectableObject obj : objectList){
						if (j == obj.getOx() && i == obj.getOy()){
							//obj.setDetected(true);
							g.setColor(Color.ORANGE);
							xc = i*squareSize;
							yc = j*squareSize;
							g.fillRect(yc, xc, squareSize, squareSize);
						}
					}
					if (sensorGrid[j][i].getDead() == true){
						g.setColor(Color.BLACK);
						xc = i*squareSize;
						yc = j*squareSize;
						g.fillRect(yc, xc, squareSize, squareSize);
					}
					
					if (sensorGrid[j][i].getState() == true){
						if(sensorGrid[j][i].getHead() == true)
							g.setColor(Color.RED);
						else
							g.setColor(Color.BLUE);
						xc = i*squareSize;
						yc = j*squareSize;
						g.fillRect(yc, xc, squareSize, squareSize);
						}
				}
			}
			

		}
	}	
	
	public int getObjectsFound(){
		Sensor s = new Sensor(-1,-1);
		return s.getObjectsFound();
	}
	
	public long getNetworkLifeSpan(){
		Sensor s = new Sensor(-1,-1);
		return s.getNetworkLifeSpan();
	}
	
	}

