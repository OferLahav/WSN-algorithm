import java.util.Random;


public class DetectableObject{

	int Ox, Oy;
	boolean detected;
	
	public DetectableObject(int LENGTH){
		Ox = randInt(0, LENGTH-1);
		Oy = randInt(0, LENGTH-1);
		detected = false;
	}
	
	void setDetected(boolean det){
		detected = det;
	}
	
	boolean getDetected(){
		return detected;
	}
	
	int getOx(){
		return Ox;
	}
	
	int getOy(){
		return Oy;
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
	
	public void move(){
		Ox += randInt(-1,1); 
		Oy += randInt(-1,1); 
	}
}