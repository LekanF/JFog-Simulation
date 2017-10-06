package mcgill;

interface Comparable{
	boolean lessThan(Comparable y);
}

//abstract class AbstractEvent implements Comparable{
//	abstract void execute(AbstractSimulator simulator);
//}
//
//abstract class OrderedSet{
//	abstract void insert(Comparable x);
//	abstract Comparable removeFirst();
//	abstract int size();
//	abstract Comparable remove(Comparable x);
//}
//
//class AbstractSimulator{
//	OrderedSet events;
//	
//	void insert(AbstractEvent e){
//		events.insert(e);
//	}
//	
//	AbstractEvent cancel(AbstractEvent e){
//		throw new java.lang.RuntimeException("Method not implemented");
//	}
//}

