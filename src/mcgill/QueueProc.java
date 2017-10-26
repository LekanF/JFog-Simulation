package mcgill;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import umontreal.iro.lecuyer.simevents.*;
import umontreal.iro.lecuyer.simprocs.*;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.randvar.*;
import umontreal.iro.lecuyer.stat.*;

//public class QueueProc{
//	boolean warmupDone;
//	List<Fog> fogN;// = FogCreator.googleFogs;
//	List<JFog> JamFogs = new ArrayList<JFog>();
//	//Create fogs, devices and let devices generate tasks
//	double warmupTime; // Warmup time T_0;
//	double horizonTime; // Horizon Length T
//	List<Device> devices;// = FogCreator.createDevicesForFogs();
//	RandomStream streamArr = new MRG32k3a();
//	List<JAMDevice> jDevices = new ArrayList<JAMDevice>();
//	
//	public QueueProc() throws FileNotFoundException, IOException{
//		
//		
//		fogN = FogCreator.googleFogs;
//
//
//		devices = FogCreator.createDevicesForFogs();
//		horizonTime = 100000;
//		warmupTime = 1000;
//		
//		
//		
//			for (Device d : devices){
//			JAMDevice temp = new JAMDevice(d, 1);
//			jDevices.add(temp);
//		}
//			System.out.println(fogN.size());
//		System.out.println(jDevices.size());
//
//	}
//	
// class JFog{
//	 int capacity;
//	 double longitude; double latitude;
//	  Resource jfog;
//	 double cpu, memory; // not in use now
//	 Fog fog;
//
//	public JFog(Fog f, int capacity){
//
//		this.fog=f;
//		this.capacity = capacity;
//	}
//
//	
//	public double getCapacity()
//	{
//		return this.capacity;
//	}
//}
// 
// class JAMDevice{
//
//	 public Tally taskStatsSojourn;
//	 double arrivalRate;
//	 RandomVariateGen taskArr = new ExponentialGen(new MRG32k3a(), 1);
//	 RandomVariateGen taskServ = new ExponentialGen(new MRG32k3a(), 1);
//	 Device dev;
//
//	 public JAMDevice(Device d, double arr){
//		 arrivalRate = arr;
//		 this.dev = d;
//		 
//		 taskStatsSojourn = new Tally(d.getDevice_id().toString());		 
//	 }
//	 
//	 public void mapTaskToFog(SimProcess p){
//		 double arrivalTime = Sim.time();
//
//
//		 for (Fog f : fogN){
//			 // if device is in fog location
//			 if (dev.getDeviceLongitude().equals(f.fog.getLongitude())){
//				 
////				 f.jresource.setStatCollecting(true);
//				 f.jresource.request(1);
//				 p.delay (taskServ.nextDouble());
//				 f.jresource.release(1);
//			 }
//		 }
//		if (warmupDone)
//			taskStatsSojourn.add(Sim.time() - arrivalTime);
//
//	 }
// }
// class TaskProcess extends SimProcess{
//	 JAMDevice t;
//	 
//	 TaskProcess (JAMDevice task){
//		 t = task;
//	 }
//	 public void actions(){
//		 new TaskProcess(t).schedule(ExponentialGen.nextDouble(new MRG32k3a(), 1));
//			 t.mapTaskToFog(this);
////		 }
//	 }
// 	}
// 
// Event endWarmup = new Event(){
//
//		
//		public void actions() {
//			for (int i = 0; i < fogN.size(); i++){
//				fogN.get(i).jresource.setStatCollecting(true);				
//			}
//			warmupDone = true;
//		}
//		 
//	 };
//	 
//	 Event endOfSim = new Event(){
//		public void actions(){
//			Sim.stop();
//		}
//	 };
//	 
//	 public void simulateOneRun(){
//		 SimProcess.init();
//		 endOfSim.schedule(1000);
//		 endWarmup.schedule(500);
//		 warmupDone = false;
////		 Generate tasks here and schedule them one by one
//		 
//		 for(JAMDevice d : jDevices)
//		 {
//			 new TaskProcess(d).schedule(ExponentialGen.nextDouble(streamArr, d.arrivalRate));
//		 }
//		 
//		 Sim.start();
//	 }
//	 
//	 public void printReportOneRun(){
//		 System.out.println("FOG RESOURCE REPORT:===========");
//		 for (int i = 0; i < fogN.size();i++){
//			 System.out.println(fogN.get(i).jresource.report());
//		 }
//		 System.out.println("END OF FOG RESOURCE REPORT:===========");
//		 
//		 System.out.println("Device Task REPORT:===========");
//		 for (int j = 0; j < jDevices.size(); j++){
//			 System.out.print(jDevices.get(j).taskStatsSojourn.report());
//		 }
//		 System.out.println("End of Device Task REPORT:===========");
//	 }
//	 
//	 //Simulate many runs
//	 public void manyRuns(int numRuns){
//		 
//		 for (int i = 0; i < numRuns; i++){
//			 simulateOneRun();
//			 streamArr.resetStartSubstream();
//			
//			 printReportOneRun();
//		 }
//	 }
//	 
//	 static public void main (String[] args) throws IOException{
//		 QueueProc mahes = new QueueProc();
//		 mahes.simulateOneRun();
//		 mahes.printReportOneRun();
//		 
////		 new QueueProc().manyRuns(1);
//	 }	 
//}

public class QueueProc {
	Resource server = new Resource (1, "Server");
//	Resource pool = new Resource
	RandomVariateGen genArr;
	RandomVariateGen genServ;

	public QueueProc (double lambda, double mu) {
		genArr = new ExponentialGen (new MRG32k3a(), lambda);
		genServ = new ExponentialGen (new MRG32k3a(), mu);
	}

	public void simulateOneRun (double timeHorizon) {
		SimProcess.init();
		server.setStatCollecting (true);
		new EndOfSim().schedule (timeHorizon);
		new Customer().schedule (genArr.nextDouble());
		Sim.start();
	}
	class Customer extends SimProcess {
		public void actions() {
/* If customer is first object, send to both pool and fog,  */
			new Customer().schedule (genArr.nextDouble());
			server.request (1);
			delay (genServ.nextDouble());
			server.release (1);
		}
	}
	class EndOfSim extends Event {
		public void actions() { Sim.stop(); }
	}
	public static void main (String[] args) {
		QueueProc queue = new QueueProc (1.0, 2.0);
		queue.simulateOneRun (1000.0);
		System.out.println (queue.server.report());
	}
}

