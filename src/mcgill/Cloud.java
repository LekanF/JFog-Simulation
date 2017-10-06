//package mcgill;
//
//public class Cloud {
//	double mem; double cpu;
//	
//	public Cloud(double cpu, double mem){
//		this.cpu = cpu;
//		this.mem = mem;
//	}
//	
//	
//}

package mcgill;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import umontreal.iro.lecuyer.simevents.*;
import umontreal.iro.lecuyer.simprocs.*;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.randvar.*;
import umontreal.iro.lecuyer.stat.*;

public class Cloud{
	boolean warmupDone;
	List<Fog> fogN;// = FogCreator.googleFogs;
	List<JFog> JamFogs = new ArrayList<JFog>();
	//Create fogs, devices and let devices generate tasks
	double warmupTime; // Warmup time T_0;
	double horizonTime; // Horizon Length T
	List<Device> devices;// = FogCreator.createDevicesForFogs();
	RandomStream streamArr = new MRG32k3a();
	List<JAMDevice> jDevices = new ArrayList<JAMDevice>();
//	 public Resource poolRes;
//	 Double poolCapacity = 0.0 ;
	 static Double ARRRATE;
	static Double SERRATE;
	static String FILENAME;
	double responseTime = 0, startTime = 0, finishTime = 0;
	public Accumulate util;
	BufferedWriter bufferedWriter;
	
	static List<Double> Times = new ArrayList<Double>();
	public Cloud() throws FileNotFoundException, IOException{
		
		
		
		
		devices = FogCreator.createDevicesForFogs();
//		horizonTime = 100000;
//		warmupTime = 1000;
		fogN = FogCreator.googleFogs;
//		for (Fog f : fogN){
//			poolCapacity += f.jresource.getCapacity() * 0.3;
//			System.out.println(f.jresource.getCapacity());
//			f.jresource.setCapacity((int) (f.jresource.getCapacity()*1));
//			
//		}
//		poolRes = new Resource(poolCapacity.intValue(), "Pool");
		
			for (Device d : devices){
				
//			JAMDevice temp = new JAMDevice(d, getGaussian(5, 0.5)); // device arrival rate
				JAMDevice temp = new JAMDevice(d, ARRRATE);
			jDevices.add(temp);
		}
//			System.out.println(fogN.size());
//		System.out.println(jDevices.size());
//		System.out.println(poolCapacity);

	}
	
 class JFog{
	 int capacity;
	 double longitude; double latitude;
	  Resource jfog;
	 double cpu, memory; // not in use now
	 Fog fog;

	public JFog(Fog f, int capacity){

		this.fog=f;
		this.capacity = capacity;
	}

	
	public double getCapacity()
	{
		return this.capacity;
	}
}
 
 class JAMDevice{

	 public Tally taskStatsSojourn;
	 double arrivalRate;
//	 RandomVariateGen taskArr = new ExponentialGen(new MRG32k3a(), 1); // change variables here, task generation 
//	 RandomVariateGen taskServ = new ExponentialGen(new MRG32k3a(), 5); // task delay length
	 RandomVariateGen taskServ = new ExponentialGen(new MRG32k3a(), SERRATE);
	 Device dev;
	 
//

	 public JAMDevice(Device d, double arr){
		 arrivalRate = arr;
		 this.dev = d;
		 
		 taskStatsSojourn = new Tally(d.getDevice_id().toString());	
		 
//		 if ()
	 }
	 
	 public void mapTaskToFog(SimProcess p){
		 double arrivalTime = Sim.time();
//		 double responseTime, startTime, finishTime;
		 int numWanted = (int)(Math.random() * 5 + 1);
		 startTime = 0; finishTime = 0; responseTime = 0;
		 
//		 double servTime = taskServ.nextDouble();
		 // Send to homoe fog
		 for (Fog f : fogN){
			 // if device is in fog location
			 if (dev.getDeviceLongitude().equals(f.fog.getLongitude())){
				 
//				if(f.jresource.getAvailable() >= numWanted){
				 startTime = Sim.time();
				 f.jresource.request(1);
				 p.delay (taskServ.nextDouble());
				 f.jresource.release(1);
				 finishTime = Sim.time();
				 responseTime = finishTime - startTime;
				 Times.add(responseTime);
				 util = f.jresource.statOnUtil();
				 //add response time to latencies.
//				 }
//				 else{
//				 	
//				 	poolRes.request(numWanted);
//				 	p.delay(taskServ.nextDouble());
//				 	poolRes.release(numWanted);
//				 }
			 }
		 }
//		 double roundtrip = Sim.time() - arrivalTime + (2*dev.devLatency);
//		 // Send to another fog
//		 double min = 0;
//		 for (Fog g : fogN){
//			 if (!dev.getDeviceLongitude().equals(g.fog.getLongitude())){
//				 double lat = FogCreator.Latency.DFLatency(dev, g);
//				 if (min < lat){
//					 min = lat;
//				 }
//			 }
//		 }
//		 
//		 for (Fog h : fogN){
//			 if (!dev.getDeviceLongitude().equals(h.fog.getLongitude()) && min == FogCreator.Latency.DFLatency(dev, h)){
//				 h.jresource.request(1);
//				 p.delay(taskServ.nextDouble());
//				 h.jresource.release(1);
//				 
//			 }
//			 
//		 }
//		 double poolroundtrip = Sim.time() - arrivalTime + (2 * min);
//		 double algoDelay = poolroundtrip - roundtrip;
		if (warmupDone)
			taskStatsSojourn.add(Sim.time() - arrivalTime + (2*dev.devLatency));
//			taskStatsSojourn.add(roundtrip);

	 }
 }
 class TaskProcess extends SimProcess{
	 JAMDevice t;
	 
	 TaskProcess (JAMDevice task){
		 t = task;
	 }
	 public void actions(){
		 new TaskProcess(t).schedule(ExponentialGen.nextDouble(new MRG32k3a(), ARRRATE));
			 t.mapTaskToFog(this);
//		 }
	 }
 	}
 
 Event endWarmup = new Event(){

		
		public void actions() {
			for (int i = 0; i < fogN.size(); i++){
				fogN.get(i).jresource.setStatCollecting(true);				
			}
//			poolRes.setStatCollecting(true);
			warmupDone = true;
		}
		 
	 };
	 
	 Event endOfSim = new Event(){
		public void actions(){
			Sim.stop();
		}
	 };
	 
	 public void simulateOneRun(){
		 SimProcess.init();
		 for (int i = 0; i < fogN.size(); i++){
				fogN.get(i).jresource.init();				
			}
		 for (int j = 0; j < jDevices.size(); j++){
			 jDevices.get(j).taskStatsSojourn.init();
		 }
		 endOfSim.schedule(1000);
		 endWarmup.schedule(500);
		 warmupDone = false;
//		 Generate tasks here and schedule them one by one
		 
		 for(JAMDevice d : jDevices)
		 {
			 new TaskProcess(d).schedule(ExponentialGen.nextDouble(streamArr, d.arrivalRate));
		 }
		 
		 Sim.start();
	 }
	 
	 public void printReportOneRun() throws IOException{
//		 BufferedWriter bufferedWriter;
		 
		 try(FileWriter fw = new FileWriter(FILENAME, false)){
				bufferedWriter = new BufferedWriter(fw);
				
				bufferedWriter.write("FOG RESOURCE REPORT:===========");
				for (int i = 0; i < fogN.size();i++){
					bufferedWriter.write(fogN.get(i).jresource.report());
				 }
				bufferedWriter.write("END OF FOG RESOURCE REPORT:===========");
				bufferedWriter.write("Device Task REPORT:===========");
				for (int j = 0; j < jDevices.size(); j++){
					bufferedWriter.write(jDevices.get(j).taskStatsSojourn.report());
				 }
				bufferedWriter.write("End of Device Task REPORT:===========");
				
				bufferedWriter.flush();
					bufferedWriter.close();	
		 }
		 
		 
		 catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		}
		
		 System.out.println("FOG RESOURCE REPORT:===========");
		 for (int i = 0; i < fogN.size();i++){
			 System.out.println(fogN.get(i).jresource.report());
//			 System.out.println(util.report());
		 }
//		 System.out.println("Pool RESOURCE REPORT:===========");
//		 System.out.println(poolRes.report());
		 System.out.println("END OF FOG RESOURCE REPORT:===========");
		 
		 System.out.println("Device Task REPORT:===========");
		 for (int j = 0; j < jDevices.size(); j++){
			 System.out.print(jDevices.get(j).taskStatsSojourn.report());
		 }
		 System.out.println("End of Device Task REPORT:===========");
	 }
	 
	 //Simulate many runs
	 public void manyRuns(int numRuns) throws IOException{
		 
		 for (int i = 0; i < numRuns; i++){
			 simulateOneRun();
//			 streamArr.resetStartSubstream();
			
			 printReportOneRun();
		 }
	 }
	 
	 public void send( Fog f, SimProcess p, RandomVariateGen tServ, int n){
		 f.jresource.request(n);
		 p.delay(tServ.nextDouble());
		 f.jresource.release(n);
	 }
	 
	 public static Random fRandom = new Random();

	    public static double getGaussian(double aMean, double variance) {
	        double d = 0;
	        while (d <= 0){
	        	d = aMean + fRandom.nextGaussian() * (aMean * variance);
	        }return d;
	        	
	        }
	 
	 static public void main (String[] args) throws IOException{
//		 Cloud mahes = new Cloud();
//		 mahes.simulateOneRun();
//		 mahes.printReportOneRun();
		 ARRRATE = Double.parseDouble(args[0]);
		 SERRATE = Double.parseDouble(args[1]);
		 FILENAME = args[2];
		 
		 new Cloud().manyRuns(1);
//		 for (double t : Times){
//			 System.out.println(t);
//		 }
	 }	 
}
//
//public class QueueProc1 {
//	Resource server = new Resource (2, "Server");
//	Resource pool = new Resource
//	RandomVariateGen genArr;
//	RandomVariateGen genServ;
//
//	public QueueProc1 (double lambda, double mu) {
//		genArr = new ExponentialGen (new MRG32k3a(), lambda);
//		genServ = new ExponentialGen (new MRG32k3a(), mu);
//	}
//
//	public void simulateOneRun (double timeHorizon) {
//		SimProcess.init();
//		server.setStatCollecting (true);
//		new EndOfSim().schedule (timeHorizon);
//		new Customer().schedule (genArr.nextDouble());
//		Sim.start();
//	}
//	class Customer extends SimProcess {
//		public void actions() {
/* If customer is first object, send to both pool and fog,  */
//			new Customer().schedule (genArr.nextDouble());
//			server.request (1);
//			delay (genServ.nextDouble());
//			server.release (1);
//		}
//	}
//	class EndOfSim extends Event {
//		public void actions() { Sim.stop(); }
//	}
//	public static void main (String[] args) {
//		QueueProc queue = new QueueProc (1.0, 2.0);
//		queue.simulateOneRun (1000.0);
//		System.out.println (queue.server.report());
//	}
//}

