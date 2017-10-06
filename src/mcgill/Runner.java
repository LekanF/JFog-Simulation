package mcgill;

import java.util.ArrayList;
import java.util.List;

import mcgill.Time.Machine;

public class Runner {
	
	static List<Machine> machines;
	
	public static void create(){
		machines = new ArrayList<Machine>();
		String name = "M";
		for (int i = 0; i < 10; i++){
			String cat = name + i;
			Machine m = new Machine(cat); 
			machines.add(m);
		}
		
	}
	
	public static void timing() throws InterruptedException{
		for (int i = 0; i < machines.size(); i++){
			//Thread.sleep(10);
			machines.get(i).sleep((long) (Math.random()*10)); //use this as delay to serve
		}
	}

	public static void main(String[] args) throws InterruptedException {
		create();
		timing();
		for (int i = 0; i < machines.size(); i++){
			System.out.println(machines.get(i).name + ": " + machines.get(i).time);
			//System.out.println(machines.get(i).t.currentThread().isAlive());
		}
	}

}
