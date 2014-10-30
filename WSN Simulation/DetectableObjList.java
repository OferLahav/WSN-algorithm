
import java.util.concurrent.CopyOnWriteArrayList;

public class DetectableObjList
{
	static CopyOnWriteArrayList  <DetectableObject> objectList = new CopyOnWriteArrayList <DetectableObject>();
	static int objectsDeployed= 0;
	
	
	public int getObjectsDeployed(){
		return objectsDeployed;
	}
	
	
	public DetectableObjList(){
		
	}
	
	public void initDetectableObjList(){
		int counter = 1;
		while(counter <= 10){
			objectList.add(new DetectableObject(100));
			counter++;
		}
		objectsDeployed += --counter;
		//System.out.println(objectsDeployed+" total objects deployed");
	}
	
	public CopyOnWriteArrayList  <DetectableObject> getDetectableObjList(){
		return objectList;
	}
	
	
	public void moveObjects(){
		for (DetectableObject obj : objectList)
			obj.move();
	} 	
	
}