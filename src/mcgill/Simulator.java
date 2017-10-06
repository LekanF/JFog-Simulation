package mcgill;


/* Events are ordered by time which allows us to create
 * create concrete classes that  implement Event and flesh out the 
 * Simulator Class*/

//abstract class Event extends AbstractEvent{
//	double time;
//	public boolean lessThan(Comparable y){
//		Event e = (Event) y; // Will throw an exception if y is not an event
//		return this.time < e.time;
//	}
//}
//
//class Simulator extends AbstractSimulator {
//	double time;
//	double now(){
//		return time;
//	}
//	void doAllEvents(){
//		Event e;
//		while((e = (Event) events.removeFirst()) != null ){
//			time = e.time;
//			e.execute(this);
//		}
//	}
//}
//
//
//

public class Simulator{}