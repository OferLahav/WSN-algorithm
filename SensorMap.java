//import sens.Sensor;
	import java.util.Timer;
	import java.util.TimerTask;
	import java.util.ArrayList;



public class SensorMap
{
	
	static int objectsFoundSoFar = 0;
	public static void main(String[] args)
	{

		SensorGrid s = new SensorGrid();
		DetectableObjList objectList = new DetectableObjList();
		
		//PRINT OUT
		Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
			
				int objFound = s.getObjectsFound() ;
				int objDeployed = objectList.getObjectsDeployed();
				long netLifeSpan = s.getNetworkLifeSpan();
				double percentFound;
				if(objDeployed != 0)
					percentFound = ((double)objFound/(double)objDeployed)*100;
				else 
					percentFound = 0;
				System.out.println((objFound - objectsFoundSoFar)+" new objects detected");
				System.out.println(objFound +" / "+objDeployed+ 
				" total objects detected after "+ netLifeSpan+" seconds : "+percentFound+"%");
			   
				if ((objFound - objectsFoundSoFar) == 0 && objDeployed != 0)
					System.exit(0);
				objectsFoundSoFar = objFound;	
            }
        }, 0, 10000);  
		
		
		//for (int i = 0; i < 500 ; i++)	// 500 deployments of objects
			//new ObjectTimer(i); 		// every 1 second
		
		
		
		timer.schedule(new TimerTask() {
            @Override
            public void run() {
				s.printAll();
		    }
        }, 0, 20); 
		
		timer.schedule(new TimerTask() {
			@Override	
			public void run(){
				objectList.initDetectableObjList();
			}
		},0,1000);
		
		// MOVE OBJECTS
		timer.schedule(new TimerTask() {

            @Override
            public void run() {
				objectList.moveObjects();
            }
        }, 0, 2000);  

		
		
	}
}

