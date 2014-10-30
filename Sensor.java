import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Sensor implements Runnable
{
	static int ObjectCounter = 0;
	static int LENGTH = 100;
	
	/*
	*	Calculation of the life span of a sensor based on 0.8 Joules
	*/
	double probON = 1/LENGTH;							//Probability of a sensor being in the ON state
	double probPASS = 1.5/LENGTH;						//Probability of a sensor being in the PASSING state
	double probOFF = (LENGTH-(probON+probPASS))/LENGTH;	//Probability of a sensor being in the OFF state
	double tim = (.8/((probON*.0165)+(probPASS*.0165)+(probOFF*.00006)))*1000;
	long lifeSpan = (long)tim;
	//end of life span calculation.
	
	int threadSleep=2; 									// Time between threads (in miliseconds)
	static int headCounter = 0;							// Used to break up the algorithm into parts
	long durationOn=0;									// measure of the ON state
	
	int x, y;											// Coordinates of the sensor
	boolean DirectionFlag = true;						// South/North of the WAVE
	boolean state = false;								// ON/OFF
	Sensor South,North,East,West;						// Pointers for the neighbouring sensors
	
	static long networkLifeSpan;						
	static boolean endofitall = false;
	public boolean isHead = false;
	static int previousX;
	public boolean isDead = false;
	
	DetectableObjList obj1 = new DetectableObjList();
	CopyOnWriteArrayList <DetectableObject> objectList =  obj1.getDetectableObjList();
	
	private Thread t;
	
	public long getNetworkLifeSpan(){
		return (System.currentTimeMillis() - networkLifeSpan)/1000;
	}
	
	public int getObjectsFound(){
		return ObjectCounter;
	}
	
	public void setNeighbors(Sensor N, Sensor E, Sensor S, Sensor W){
		North = N;
		East = E;
		South = S;
		West = W;
		
		networkLifeSpan = System.currentTimeMillis();
	}
	
	public boolean getDead(){
		return isDead;
	}
	
	public void setLifeSpan(int t){
		lifeSpan = t;
	}
	
	public boolean getHead(){
		return isHead;
	}
	
	public void setHead(boolean b){
		this.isHead = b;
	}
	
	public Sensor(int x, int y){ // constructor
		this.x = x;
		this.y = y;	
	}
	
	public int getX(){
		return x;
	}

	public int getY(){
		return y;
	}

	public boolean getState(){
		return state;
	}
	
	public void setState(boolean s){
		state = s;
	}
	
	public void activate(boolean direction){
		
		if (isHead == true){
			headCounter++;
			if (headCounter%((LENGTH-1)*2) == 0){// Pause between sweeps
					South.setState(false);
					East.setState(false);
				try {
					Thread.sleep(1000);                 //1000 milliseconds is one second.
				} catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			
			}
		}
		
		
		if( (state == false || DirectionFlag != direction) && (x != 999 && y!= 999))// if OFF & not a border or dead
		{
			state = true; //on
			
			durationOn = System.currentTimeMillis();				
			
			for (DetectableObject obj : objectList){
				if( x == obj.getOx() && y == obj.getOy() //||
					// x-1 == obj.getOx() && y-1 == obj.getOy() ||
					// x-1 == obj.getOx() && y == obj.getOy() ||
					// x-1 == obj.getOx() && y+1 == obj.getOy() ||
					// x == obj.getOx() && y-1 == obj.getOy() ||
					// x == obj.getOx() && y+1 == obj.getOy() ||
					// x+1 == obj.getOx() && y-1 == obj.getOy() ||
					// x+1 == obj.getOx() && y == obj.getOy() ||
					// x+1 == obj.getOx() && y+1 == obj.getOy() 
					){
					if(obj.getDetected() == false){
						obj.setDetected(true);
						ObjectCounter++;
					}
				}			
			}
			
			t = new Thread (this, "something");
			DirectionFlag = direction;
			t.start ();

		}
		else if (x == 999 && y== 999){
			if (direction == true){ //going down
					West.passBack(direction, "East");
					North.passBack(direction, "South");
				}
				else {					//going up
					West.passBack(direction, "East");
					South.passBack(direction, "North");
				}
		}		
	}
	
	public void run() throws OutOfMemoryError {
		 try {
			for (DetectableObject obj : objectList){
				if( x == obj.getOx() && y == obj.getOy() //||
					// x == obj.getOx()-1  && y == obj.getOy()-1 ||
					// x == obj.getOx()-1 && y == obj.getOy() ||
					// x == obj.getOx()-1 && y == obj.getOy()+1 ||
					// x == obj.getOx() && y == obj.getOy()-1 ||
					// x == obj.getOx() && y == obj.getOy()+1 ||
					// x == obj.getOx()+1 && y == obj.getOy()-1 ||
					// x == obj.getOx()+1 && y == obj.getOy() ||
					// x == obj.getOx()+1 && y == obj.getOy()+1 
					){
					if(obj.getDetected() == false){
						obj.setDetected(true);
						ObjectCounter++;
					}
				}			
			}
			
			Thread.sleep(threadSleep);
			boolean direction = DirectionFlag;
			if (direction == true){ //going down
					West.passBack(direction, "East");
					North.passBack(direction, "South");
				}
				else {					//going up
					West.passBack(direction, "East");
					South.passBack(direction, "North");
				}
				
				// if top or bottom wall
				if (North.getY() == South.getY()){
					if (direction == true)	{ direction = false;}	
					else					{ direction = true;}
				}
				
				if(East.getX() == 999){  // if tail
					state = false;	// shut off
					
				}
				else{					// if not tail
					if (direction == true){ //going down
						East.passForward(direction, "West");
						South.passForward(direction, "North");
					}
					else {					//going up
						East.passForward(direction, "West");
						North.passForward(direction, "South");
					}
				}
		}catch (InterruptedException e) {
			System.out.println("Thread interrupted.");
		}catch(OutOfMemoryError e){
			System.out.println("OUT OF MEMORY");
		}
		
		for (DetectableObject obj : objectList){
				if( x == obj.getOx() && y == obj.getOy() //||
					// x == obj.getOx()-1  && y == obj.getOy()-1 ||
					// x == obj.getOx()-1 && y == obj.getOy() ||
					// x == obj.getOx()-1 && y == obj.getOy()+1 ||
					// x == obj.getOx() && y == obj.getOy()-1 ||
					// x == obj.getOx() && y == obj.getOy()+1 ||
					// x == obj.getOx()+1 && y == obj.getOy()-1 ||
					// x == obj.getOx()+1 && y == obj.getOy() ||
					// x == obj.getOx()+1 && y == obj.getOy()+1 
					){
					if(obj.getDetected() == false){
						obj.setDetected(true);
						ObjectCounter++;
					}
				}			
			}
	}

    public void start(){
		
      String threadName =  "bob";
	  System.out.println("Starting " +  threadName );
      if (t == null)
      {
         t = new Thread (this, threadName);
         t.start ();
      }
   }	
	
	public void deactivate(boolean direction){
		if (state == true && (x != 999 && y!=999))
		{	
			for (DetectableObject obj : objectList){
				if( x == obj.getOx() && y == obj.getOy()// ||
					// x == obj.getOx()-1  && y == obj.getOy()-1 ||
					// x == obj.getOx()-1 && y == obj.getOy() ||
					// x == obj.getOx()-1 && y == obj.getOy()+1 ||
					// x == obj.getOx() && y == obj.getOy()-1 ||
					// x == obj.getOx() && y == obj.getOy()+1 ||
					// x == obj.getOx()+1 && y == obj.getOy()-1 ||
					// x == obj.getOx()+1 && y == obj.getOy() ||
					// x == obj.getOx()+1 && y == obj.getOy()+1 
					){
					if(obj.getDetected() == false){
						obj.setDetected(true);
						ObjectCounter++;
					}
				}			
			}
			if(x == 1)
				lifeSpan +=10000;
			

			
			
			
			if(isHead == true){
				designateHead(direction);
			}
			
			durationOn = System.currentTimeMillis() - durationOn;
			lifeSpan -= durationOn;
			
			state = false; // off	
			
		}
		if (lifeSpan<=0 && isDead == false){
			state = false;
			isDead = true;
			x=999;
			y=999;
		}
	}
	
	public void passBack(boolean direction, String origin){
		long passDurationOn = System.currentTimeMillis();
		if (x != 999 && y != 999)
		{
			if (direction == true){ //going down
				if (origin == "South"){
						West.deactivate(direction);
					}
				if (origin == "East"){	
						North.deactivate(direction);
					}
			}
			else{ //going up
				if (origin == "North"){
						West.deactivate(direction);
					}
				if (origin == "East"){	
						North.deactivate(direction);
					}
			
			
			}	
		}
		passDurationOn = System.currentTimeMillis() - passDurationOn;
		lifeSpan -= passDurationOn;
	}
	
	public void passForward(boolean direction, String origin){
		long passDurationOn = System.currentTimeMillis();
		if (x != 999 && y != 999)
		{
			if (direction == true){ //going down
				if (origin == "North"){
						East.activate(direction);
					}
				if (origin == "West"){	
						South.activate(direction);
					}
			}
			else{ //going up
				if (origin == "South"){
						East.activate(direction);
					}
				if (origin == "West"){	
						North.activate(direction);
					}
			}	
		}
		passDurationOn = System.currentTimeMillis() - passDurationOn;
		lifeSpan -= passDurationOn;
	}
	
	public void designateHead(boolean direction){
		if(West.getX()!= 999 && West.getX() != previousX){
			West.setHead(true);
			West.activate(direction);
		}	
		else if (DirectionFlag == true && South.getX() != 999 ){
			South.setHead(true);
			South.activate(direction);
		}
		else if (DirectionFlag == false && North.getX() != 999){
			North.setHead(true);
			North.activate(direction);
		}	
		else if (East.getX() != 999){
			East.setHead(true);
			East.activate(direction);
		}	
		else{
			if (DirectionFlag == true){
				DirectionFlag = false;
				North.setHead(true);
				North.activate(direction);
				}
			else	
				DirectionFlag = true;
				South.setHead(true);
				South.activate(direction);
			}
		previousX = this.x;	
		isHead = false;	
	}
	
}