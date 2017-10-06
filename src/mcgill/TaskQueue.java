package mcgill;
//
//
///* Implement the OrderedSet. We use the Java Vector Data Structure because it 
// * does a linear search when it inserts an item*/
//
//class TaskQueue extends OrderedSet{
//	@SuppressWarnings("rawtypes")
//	java.util.Vector elements = new java.util.Vector();
//	
//	@SuppressWarnings("unchecked")
//	void insert (Comparable x){
//		int i = 0;
//		while(i < elements.size() && ((Comparable)elements.elementAt(i)).lessThan(x)){
//			i++;
//		}
//		elements.insertElementAt(x, i);
//	}
//	
//	Comparable removeFirst(){
//		if (elements.size() == 0) return null;
//		Comparable x = (Comparable)elements.firstElement(); // Assign first element to x
//		elements.removeElementAt(0);
//		return x;
//	}
//	
//	Comparable remove(Comparable x){
//		for (int i = 0; i < elements.size(); i++){
//			if (elements.elementAt(i).equals(x)){
//				Comparable y = (Comparable) elements.elementAt(i);
//				elements.removeElementAt(i);
//				return y;
//			}
//		}
//		return null;
//	}
//	
//	public int size(){
//		return elements.size();
//	}
//}
//
//
public class TaskQueue{}