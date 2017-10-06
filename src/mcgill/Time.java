package mcgill;

public class Time {

	public static class Machine implements Runnable {
	    String name;
	    Double time;
	    static Thread t;
	    //constructor with name
	    Machine(String tName){
	        name = tName;
	        t = new Thread(this, name);
	        t.start();
	        time = 0.0;
	    }
	    @Override
	    public void run() {
	        //System.out.println("I am here" + t.getName());
	    }
	    
	    public void setTime(Double time){
	        this.time = time;
	    }
	    
	    public void increaseTime(Double incr){
	        time = time + incr;
	    }
	    
	    //return current machine time
	    public Double getTime(){
	        return time;
	    }
	    
	    public void sleep(long d) throws InterruptedException{
	    	
	    	Thread.sleep(d);
	    }
	    
	    public void now(){
	    	
	    }
	}
}
