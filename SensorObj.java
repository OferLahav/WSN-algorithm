public class SensorObj implements Runnable
{
	static int LENGTH = 6;
	long lifeSpan= 10000; // 10 seconds battery life span of each sensor
	int threadSleep=20; 
	
	int x, y;
	boolean DirectionFlag = true;	// South\ North
	boolean state = false;	// on\off
	Sensor South,North,East,West;
	//double energy = 0.8;
	long durationOn=0;
	static long networkLifeSpan;
	static boolean endofitall = false;
	public boolean isHead = false;
	static int previousX;
	public boolean isDead = false;
	
	private Thread t;
	
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
	
	public SensorObj(int x, int y){ // constructor
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
	
	public void activate(boolean direction){
		
		if( (state == false || DirectionFlag != direction) && (x != 999 && y!= 999))// if off & not a border or dead
		{
			
			state = true; //on
			
			durationOn = System.currentTimeMillis();				
			
			//printAll();
			
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
	
	public void run() throws OutOfMemoryError{
		 try {
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
					//System.out.println(" manually deactivating : ( "+x+" "+y+" )");
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
			durationOn = System.currentTimeMillis() - durationOn;
			lifeSpan -= durationOn;

			
			if(isHead == true){
				designateHead(direction);
			}
			
			state = false; // off	
			
		}
		if (lifeSpan<=0){
			state = false;
			isDead = true;
			x=999;
			y=999;
			if(!endofitall){
				networkLifeSpan = (System.currentTimeMillis() - networkLifeSpan)/1000;
				System.out.println("The network lasted for : "+networkLifeSpan+" seconds .");
				endofitall = true;
				System.exit(0);
			}	
		}
	}
	
	public void passBack(boolean direction, String origin){
		lifeSpan -= (threadSleep);
		if (x != 999 && y != 999)
		{
			if (direction == true){ //going down
				if (origin == "South"){
						//system.out.println("trying to deactivate :"+ west.getx()+", "+west.gety()+"");
						West.deactivate(direction);
					}
				if (origin == "East"){	
						//system.out.println("trying to deactivate :"+ north.getx()+", "+north.gety()+"");
						North.deactivate(direction);
					}
			}
			else{ //going up
				if (origin == "North"){
						//system.out.println("trying to deactivate :"+ west.getx()+", "+west.gety()+"");
						West.deactivate(direction);
					}
				if (origin == "East"){	
						//system.out.println("trying to deactivate :"+ north.getx()+", "+north.gety()+"");
						North.deactivate(direction);
					}
			
			
			}	
		}
	}
	
	public void passForward(boolean direction, String origin){
		lifeSpan -= (threadSleep);
		if (x != 999 && y != 999)
		{
			if (direction == true){ //going down
				if (origin == "North"){
						//system.out.println("trying to deactivate :"+ west.getx()+", "+west.gety()+"");
						East.activate(direction);
					}
				if (origin == "West"){	
						//system.out.println("trying to deactivate :"+ north.getx()+", "+north.gety()+"");
						South.activate(direction);
					}
			}
			else{ //going up
				if (origin == "South"){
						//system.out.println("trying to deactivate :"+ west.getx()+", "+west.gety()+"");
						East.activate(direction);
					}
				if (origin == "West"){	
						//system.out.println("trying to deactivate :"+ north.getx()+", "+north.gety()+"");
						North.activate(direction);
					}
			
			
			}	
		}
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