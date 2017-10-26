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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.scenario.effect.impl.PoolFilterable;

import umontreal.iro.lecuyer.simevents.*;
import umontreal.iro.lecuyer.simprocs.*;
import umontreal.iro.lecuyer.rng.*;
import umontreal.iro.lecuyer.randvar.*;
import umontreal.iro.lecuyer.stat.*;
import umontreal.iro.lecuyer.simprocs.DSOLProcessSimulator;

public class Cloud {
	boolean warmupDone;
	static List<Fog> fogN;// = FogCreator.googleFogs;
	List<JFog> JamFogs = new ArrayList<JFog>();
	//Create fogs, devices and let devices generate tasks
	double warmupTime; // Warmup time T_0;
	double horizonTime; // Horizon Length T
	static List<Device> devices;// = FogCreator.createDevicesForFogs();
	RandomStream streamArr = new MRG32k3a();
	static List<JAMDevice> jDevices = new ArrayList<JAMDevice>();
//	 public Resource poolRes;
//	 Double poolCapacity = 0.0 ;
	static double prevHLAT = 0;
	static double currHLAT = 0;
	static double prevDelay = 0;
	static double currDelay = 0;
	static double totalDelay = 0;
	static double prevPLAT = 0;
	static int counter = 0;
	static int totalTasks = 0;
	static int taskDevice = 0;
//static //	 Double poolCapacity = 0.0 ;
static double currPLAT;
	 static Fog temp = null;
	
	public static final int WR = 1;
	public static final int PS = 2;
	
	public static final int HOMEFOG = 1;
	public static final int POOL = 2;
	public static final int PO2 = 3;
	public static final int MODPOOL = 4;
	public static final int MODPO2 = 5;
	
	private ProcessSimulator sim;
	private int policy = WR;
//	static int converger = 0;
	
	static Double ARRRATE;
	static Double SERRATE;
	static String FILENAME, UTILIZATION, WAIT, SERV, THROUGHPUT, SOJ, RESPTIME;
	double responseTime = 0, startTime = 0, finishTime = 0;
	public Accumulate util;
	public Tally waitTimeAvg, servTimeAvg, sojTimeAvg, devRespTime;
	BufferedWriter bufferedWriter;
	static SimProcess proc;
	
	static List<Double> Times = new ArrayList<Double>();
	public Cloud() throws FileNotFoundException, IOException{
		
		devices = FogCreator.createDevicesForFogs();

		fogN = FogCreator.googleFogs;

			for (Device d : devices){
				
//			JAMDevice temp = new JAMDevice(d, getGaussian(5, 0.5)); // device arrival rate
				JAMDevice temp = new JAMDevice(d, ARRRATE);
			jDevices.add(temp);

		}
			System.out.println(fogN.size());
		System.out.println(jDevices.size());
		


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
	 public TallyStore sojournObs;
	 double arrivalRate;
//	 RandomVariateGen taskArr = new ExponentialGen(new MRG32k3a(), 1); // change variables here, task generation 
//	 RandomVariateGen taskServ = new ExponentialGen(new MRG32k3a(), 5); // task delay length
	 RandomVariateGen taskServ = new ExponentialGen(new MRG32k3a(), SERRATE);
	 Device dev;
	 double latency;
	 private int convergerDev = 0;
	 public int conv = 0;
	 public int totalReq = 0;
	 
	 
	 public int getConverger(){
		 return convergerDev;
	 }
	 
	 public void setConveger(int val){
		 convergerDev = val;
	 }
//

	 public JAMDevice(Device d, double arr){
		 arrivalRate = arr;
		 this.dev = d;
		 
		 taskStatsSojourn = new Tally(d.getDevice_id().toString());	
		 sojournObs = new TallyStore(d.getDevice_id().toString());
		 		 
	 }
	 
	 public double mapTaskToFog(SimProcess p, int policy, Fog f, double time) {
		 
		 double arrivalTime = Sim.time();
		 latency = dev.getDevLatatency() + FogCreator.Latency.DFLatency(dev, f);
		 
//		 int numWanted = (int)(Math.random() * 2 + 1);
		 startTime = 0; finishTime = 0; responseTime = 0;
		 
		 switch (policy) {
		 case WR : 
		
		 
//		 double servTime = taskServ.nextDouble();
		 // Send to homoe fog
//		 for (Fog f : fogN){
			 // if device is in fog location
//			 if (dev.getDeviceLongitude().equals(f.fog.getLongitude())){
				 
//				if(f.jresource.getAvailable() >= numWanted){
				 startTime = Sim.time();
				 //Before you request, make  a delay 
//				 p.delay(time); // This is wrong, dont implment the delay at this resource, instead, it should be at the device
				 f.jresource.request(1);
				 // Delay for service
				 p.delay (taskServ.nextDouble());
				 f.jresource.release(1);
//				 if(f.jresource.release(numWanted) != null)
				 if (warmupDone){
					 double res = (double) ((double)(f.jresource.getCapacity()- (double)f.jresource.getAvailable())/(double)f.jresource.getCapacity());
					 f.utilise.update(res, Sim.time());
				 
				 }
				 finishTime = Sim.time();
				 responseTime = finishTime - startTime;
				 Times.add(responseTime);
//				 util = f.jresource.statOnUtil();
				 util = f.jresource.servList().statSize();
				 
				 //add response time to latencies.
				 
				 //PS from here
//				 FogCreator myPS = new FogCreator();
//				 FogCreator.PSserveTask(dev, f, numWanted, taskServ.nextDouble());
//				 }
//				 else{
//					 System.out.println("Cannot accept");
////				 	
////				 	poolRes.request(numWanted);
////				 	p.delay(taskServ.nextDouble());
////				 	poolRes.release(numWanted);
//					 double min = 0;
//					 for (Fog g : fogN){
//						 if (!dev.getDeviceLongitude().equals(g.fog.getLongitude())){
//							 double lat = FogCreator.Latency.DFLatency(dev, g);
//							 if (min < lat){
//								 min = lat;
//							 }
//						 }
//					 }
//					 
//					 for (Fog h : fogN){
//						 if (!dev.getDeviceLongitude().equals(h.fog.getLongitude()) && min == FogCreator.Latency.DFLatency(dev, h)){
//							 h.jresource.request(numWanted);
//							 p.delay(taskServ.nextDouble());
//							 h.jresource.release(numWanted);
//							 
//						 }
//						 
//					 }
//				 }
//			 }
//		 }
		 ; break;
		 case PS :
			 // Use time sharing here
			 System.out.println("PS Not yet implemented exception");
			 break;
		 }
		 
		if (warmupDone){
//			taskStatsSojourn.add(Sim.time() - arrivalTime + (2*(dev.devLatency + FogCreator.Latency.DFLatency(dev, f))));
			taskStatsSojourn.add(Sim.time() - arrivalTime + (2*latency));
			sojournObs.add(Sim.time() - arrivalTime);// + (2*dev.devLatency));
		}
//			taskStatsSojourn.add(roundtrip);
//		return (Sim.time() - arrivalTime + (2*(dev.devLatency + FogCreator.Latency.DFLatency(dev, f))));
		return (Sim.time() - arrivalTime + (2*latency));
		
	 }
	 
 }
 
 static class TaskProcess extends SimProcess{
	 JAMDevice t;
	 double homeLatency, poolLtency, pLatency;
	 ArrayList<Fog> pool = new ArrayList<Fog>();
	 boolean control  = false;
	 static double delay = 0;
	 SimProcess proc1;
	 SimProcess proc2;
	 static int  num = 0;
	 static double homefoglat = 0, poollat = 0, prevhomefog = 0, prevpool = 0, curdelay = 0, totalDelay = 0;
	 static Fog temp;
	 int choice;
	 Accumulate queueSize;
	 static int delaycounter = 0;
	 
	 TaskProcess (JAMDevice task, int cho, double fogtime, double pooltime){
		 t = task; 
		 choice = cho;
		 homefoglat = fogtime;
		 poollat = pooltime;
	 }
	 public void actions(){
		 double min = 100000000000.0;
		 
		 new TaskProcess(t, choice, homefoglat, poollat).schedule(ExponentialGen.nextDouble(new MRG32k3a(), ARRRATE) );
//		 counter++;
		 
		 // For po2
		 switch (choice) {
			 
		 case PO2 :
			 
		 List<Fog> po2 = PO2(fogN); // return two fogs
		
//		 FogCreator.Latency.DFLatency(t.dev, po2.)
		 int first = po2.get(0).jresource.waitList().size(); // check number on the queue
		 int second = po2.get(1).jresource.waitList().size();
		 
		 if (first < second){
			 t.mapTaskToFog(this, WR, po2.get(0), 0);
		 }
		 else if (first > second)
		 {
			 t.mapTaskToFog(this, WR, po2.get(1), 0);
		 } 
		 else if (first == second){ // break arbitrary ties
			 int random = (int)(Math.random() * 1 + 1);
			 t.mapTaskToFog(this, WR, po2.get(random), 0);
		 }
		 
//		 if equal, break ties arbitrarily by sending to any
		 
//		 for (Fog g : po2){			 
//			 t.mapTaskToFog(this, WR, g, 0);
//			 g.jresource.statOnSojourn().numberObs();
//		 }
		 break;
		 
		 case HOMEFOG:
		 		
		 // For home fog only
		 for (Fog f : fogN){
			 
			 if (t.dev.getDeviceLongitude().equals(f.fog.getLongitude())){
				 homefoglat = t.mapTaskToFog(this, WR, f, 0);
			}
		 }
		 break;
		 
		 case POOL:
		t.totalReq++;
		// For the first instance, we send to both pool and fog
		 List<Fog> pool =  poolFogs(t.dev, fogN, 3);
		if (homefoglat <= poollat || totalDelay < homefoglat){
			for (Fog f : fogN){
				 
				 if (t.dev.getDeviceLongitude().equals(f.fog.getLongitude())){
					 homefoglat = t.mapTaskToFog(this, WR, f, 0);
				}	
				 
			 }
			for (Fog p : pool){
				  
					if (totalDelay == 0) totalDelay = 0.001;
					this.delay(totalDelay);
				  poollat  = t.mapTaskToFog(this,WR, p, totalDelay);
				  
				  if (poollat < min){
					  min = poollat;
				  		temp = p; // So, we take the minimum poollatency and send it to that fog.
				  }
			  
			  }
			poollat = min;
			curdelay = Math.abs(poollat - homefoglat); // Instead you could use if cur < 0, delay fog, else delay pool
			if (totalDelay < homefoglat){
				totalDelay += curdelay;
			}
//			else{
////					totalDelay = homefoglat; // this is the benchmark for the latencies. we want to use home fog as much as possible
//					totalDelay += curdelay; 
//				}
//			delaycounter++;
			t.conv++;
		}
		
		 //For subsequent instances, we check if 
		else if (homefoglat  < poollat && totalDelay >= homefoglat){
			for (Fog f : fogN){
				 
				 if (t.dev.getDeviceLongitude().equals(f.fog.getLongitude())){
					 homefoglat = t.mapTaskToFog(this, WR, f, 0);
				}	
				 
			 }
		}
		
//		else if (homefoglat < poollat && totalDelay < homefoglat){
//			for (Fog f : fogN){
//				 
//				 if (t.dev.getDeviceLongitude().equals(f.fog.getLongitude())){
//					 homefoglat = t.mapTaskToFog(this, WR, f, 0);
//				}	
//				 
//			 }
//			for (Fog p : pool){
//				  
//				  poollat  = t.mapTaskToFog(this,WR, p, totalDelay);
//				  
//				  if (poollat < min){
//					  min = poollat;
//				  		temp = p; // So, we take the minimum poollatency and send it to that fog.
//				  }
//			  
//			  }
//			poollat = min;
//			curdelay = Math.abs(poollat - homefoglat); // Instead you could use if cur < 0, delay fog, else delay pool
//			if (totalDelay < homefoglat){
//				totalDelay += curdelay;
//			}
//			else
//				totalDelay = homefoglat; // this is the benchmark for the latencies. we want to use home fog as much as possible
//		
//		}
		
		else if (homefoglat > poollat){
			for (Fog p : pool){
				if (temp.equals(p)){
					poollat = t.mapTaskToFog(this,WR, temp, totalDelay);
				}
//					else
//				{
//					poollat = t.mapTaskToFog(this,WR, p, totalDelay); // this actually never happens
//				}
				// How do we return back to fog, it returns based on previous value
			}
		}
//		t.setConveger(delaycounter);
		
		break;
//End of VPool
		
		 case MODPOOL : 
			 List<Fog> modPool = poolFogs(t.dev, fogN, 1);
			 
			 for (Fog f : fogN){
				 
				 if (t.dev.getDeviceLongitude().equals(f.fog.getLongitude())){
					 homefoglat = t.mapTaskToFog(this, WR, f, 0);
				}	
				 
			 }
			 prevhomefog = homefoglat;
			 
			 for (Fog g : modPool){
				 poollat = t.mapTaskToFog(this, WR, g, totalDelay);
			 }
			 
			 break;
			 
		 case MODPO2 :
			 	//Create zones around each location
			 List<Fog> zones = poolFogs(t.dev, fogN, 5);
			 
			 List<Fog> modPo2 = PO2(zones);
			 
			 
//			 
			 int first1 = modPo2.get(0).jresource.waitList().size(); // check number on the queue
			 int second1 = modPo2.get(1).jresource.waitList().size();
			 
			 if (first1 < second1){
				 t.mapTaskToFog(this, WR, modPo2.get(0), 0);
			 }
			 else if (first1 > second1)
			 {
				 t.mapTaskToFog(this, WR, modPo2.get(1), 0);
			 } 
			 else if (first1 == second1){ // break arbitrary ties
				 int random = (int)(Math.random() * 1 + 1);
				 t.mapTaskToFog(this, WR, modPo2.get(random), 0);
			 }
			 break;
		
		 } 
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
		 endWarmup.schedule(200);
		 warmupDone = false;
//		 Generate tasks here and schedule them one by one
		 List<Fog> pool = null;
		 double hLat, pLat;
		 
		 for(JAMDevice d : jDevices)
		 {
			 new TaskProcess(d, POOL, 0, 0).schedule(ExponentialGen.nextDouble(streamArr, d.arrivalRate));	
			 counter++;
		 }		 
		 
		 Sim.start();
	 }
	 
	 public void printReportOneRun() throws IOException{
		 BufferedWriter bufferedWriter;
		 double average = 0, sum = 0;
		 FileWriter utilization = new FileWriter(UTILIZATION, false);
		 FileWriter wait = new FileWriter (WAIT, false);
		 FileWriter serv = new FileWriter (SERV, false);
		 FileWriter soj = new FileWriter (SOJ, false);
		 FileWriter throughput = new FileWriter (THROUGHPUT, false);
		 FileWriter respTime =  new FileWriter (RESPTIME, false);
		 
		 try(FileWriter fw = new FileWriter(FILENAME, false)){
				bufferedWriter = new BufferedWriter(fw);
				
				bufferedWriter.write("FOG RESOURCE REPORT:===========");
				for (int i = 0; i < fogN.size();i++){
					sum = 0;
					bufferedWriter.write(fogN.get(i).jresource.report());
					bufferedWriter.write("\n");
					for (int j = 0; j < fogN.get(i).utilise.getSize(); j++){
//						 System.out.println(fogN.get(i).utilise.getTime(j) + "\t" + fogN.get(i).utilise.getResource(j));
						sum += fogN.get(i).utilise.getResource(j);
						bufferedWriter.write(String.format("%.2f", fogN.get(i).utilise.getTime(j)) + "\t" + fogN.get(i).utilise.getResource(j));
						
						 bufferedWriter.write("\n");
				 }
					average = sum / fogN.get(i).utilise.getSize();
					totalTasks +=  fogN.get(i).utilise.getSize();
					bufferedWriter.write("Sum: " + sum + " Size " + fogN.get(i).utilise.getSize() + " Average : " + average);
					bufferedWriter.write("\n");
				}
				bufferedWriter.write("END OF FOG RESOURCE REPORT:===========");
				bufferedWriter.write("Device Task REPORT:===========");
				for (int j = 0; j < jDevices.size(); j++){
					bufferedWriter.write(jDevices.get(j).taskStatsSojourn.report());
					bufferedWriter.write("\n");
//					bufferedWriter.write(jDevices.get(j).sojournObs.getDoubleArrayList().toString());
					bufferedWriter.write("\n");
				 }
				bufferedWriter.write("End of Device Task REPORT:===========");
				
				bufferedWriter.flush();
					bufferedWriter.close();	
		 }
//		 
//		 
		 catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
		}
		
		 System.out.println("FOG RESOURCE REPORT:===========");
//		 double average = 0, sum = 0;
		 for (int i = 0; i < fogN.size();i++){
			 sum = 0;
			 System.out.println(fogN.get(i).jresource.report());

			 // Waiting and sojourn times
			 waitTimeAvg = fogN.get(i).jresource.waitList().statSojourn();
			 servTimeAvg = fogN.get(i).jresource.servList().statSojourn();
			 sojTimeAvg = fogN.get(i).jresource.statOnSojourn();
			 
			 wait.write(waitTimeAvg.average() + "");
			 wait.write("\n");
			 serv.write(servTimeAvg.average() + "");
			 serv.write("\n");
			 soj.write(sojTimeAvg.average() + "");
			 soj.write("\n");
			 
			 for (int j = 0; j < fogN.get(i).utilise.getSize(); j++){
//				 System.out.println(fogN.get(i).utilise.getTime(j) + "\t" + fogN.get(i).utilise.getResource(j));
//				 System.out.printf("%.2f \t %.2f ",fogN.get(i).utilise.getTime(j), fogN.get(i).utilise.getResource(j));
				 sum += fogN.get(i).utilise.getResource(j);
//				 System.out.println();
			 }
			 
			 average = sum / fogN.get(i).utilise.getSize();
			 System.out.println("Sum: " + sum + " Size " + fogN.get(i).utilise.getSize() + " Average : " + average);
			 // Writing avergae utilization and number of tasks sent to fog to file
			 utilization.write(average + "" );
			 utilization.write("\n");
			 throughput.write(fogN.get(i).utilise.getSize()+ "");
			 throughput.write("\n");
//			 System.out.println(util.report() );
//			 break;

		 }
		
//		 System.out.println("Pool RESOURCE REPORT:===========");
//		 System.out.println(poolRes.report());
		 System.out.println("END OF FOG RESOURCE REPORT:===========");
		 
		 System.out.println("Device Task REPORT:===========");
		 
		 
		 for (int j = 0; j < jDevices.size(); j++){
			 System.out.print(jDevices.get(j).taskStatsSojourn.report());
			 taskDevice += jDevices.get(j).taskStatsSojourn.numberObs();
			 respTime.write(jDevices.get(j).taskStatsSojourn.average() +"\n");
//			 respTime.write("\n");
			 
//			 System.out.println(jDevices.get(j).sojournObs.getDoubleArrayList().toString());
		 }
		 System.out.println("End of Device Task REPORT:===========");
		 utilization.write("Total tasks" + taskDevice);
		 utilization.close();
		 wait.close(); serv.close(); throughput.close(); soj.close(); respTime.close();
	 }
	 
	 //Simulate many runs
	 public void manyRuns(int numRuns) throws IOException{
		 
		 for (int i = 0; i < numRuns; i++){
			 simulateOneRun();
//			 streamArr.resetStartSubstream();
			
			 printReportOneRun();
		 }
	 }
	 	
	
	 
	 public static List<Fog> PO2 (List<Fog> Fogs){
		 
		 List<Fog> po2 = new ArrayList<Fog>();
		 
//		 int random = returnRandom();
		 int random  = (int)(Math.random() * Fogs.size() );

		 po2.add(Fogs.get(random));
		 
		 
//		 int anotherRandom = returnRandom();
		 int anotherRandom = (int)(Math.random() * Fogs.size() );

		 while (random == anotherRandom){
			 anotherRandom = (int)(Math.random() * Fogs.size() );
		 }
		 po2.add(Fogs.get(anotherRandom));		 
		 
		 return po2;
	 }
	 
	 public static int returnRandom(){
		int random  = (int)(Math.random() * fogN.size() );
		while (random ==  fogN.size()){
			random  = (int)(Math.random() * fogN.size() );
		}
		return random;
	 }
	 public static List<Fog> poolFogs(Device dev, List<Fog> fog, int choice){
		// return the 3 closest fogs
		 List<Fog> myPool3;// = new ArrayList<Fog>();
		 
		 HashMap<Fog, Double> myFogMap = new HashMap<Fog, Double>();
		 
		 //Lets randomly pick 3 fogs
		 for (Fog g : fog){
			 if (!dev.getDeviceLongitude().equals(g.fog.getLongitude()))
			 {
				 double lat = FogCreator.Latency.DFLatency(dev, g);
//				 dev.insertLatency(lat+dev.getDevLatatency());
//				 dev.setLatency(lat);
//				 myFogMap.put(g, lat);
				 // devLatency is always the latency to its own closest fog, to get to another fog
				 // we need to add the latencies together
				 myFogMap.put(g, dev.getDevLatatency() + lat);
			 }
		 }
		 
		 // For ascending order of latencies
		 Stream<Map.Entry<Fog, Double>> sorted = myFogMap.entrySet().stream().sorted(Map.Entry.comparingByValue());
		 // Should you want descending order, use below
//		 Stream<Map.Entry<Fog, Double>> revervesorted = myFogMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
		 
		 
		 // To take the top 3 fogs for a pool
		 Map<Fog, Double> top3 = myFogMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).limit(choice).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); 
		 
		 myPool3 = new ArrayList<>(top3.keySet());

		 return myPool3;		
	 }
	 
	 public static Random fRandom = new Random();

	    public static double getGaussian(double aMean, double variance) {
	        double d = 0;
	        while (d <= 0){
	        	d = aMean + fRandom.nextGaussian() * (aMean * variance);
	        }return d;
	        	
	        }
	    
	    public void returnLats(){
	    	for (Device dev : devices){
	    		System.out.println(dev.getDevLatatency());
	    	}
	    }
	 
	 static public void main (String[] args) throws IOException{

		 ARRRATE = Double.parseDouble(args[0]);
		 SERRATE = Double.parseDouble(args[1]);
		 FILENAME = args[2];
		 UTILIZATION = args[3];
		 THROUGHPUT = args[4];
		 WAIT = args[5];
		 SERV = args[6];
		 SOJ = args [7];
		 RESPTIME = args[8];
		 
		 ArrayList<Integer> numbers = new ArrayList<Integer>();
		 new Cloud().manyRuns(1);

		 System.out.println("Total Fog tasks : " + totalTasks);
		 System.out.println("Total Device tasks : " + taskDevice);
		 int sum = 0;
		 System.out.println("Number of tasks before converging");
		 for (JAMDevice d : jDevices){
//			 System.out.println(d.getConverger());
			 System.out.println("Before converging : " + d.conv + " Total number of requests : " + d.totalReq);
			 numbers.add(d.conv);
			 sum += d.totalReq;
		 }
		 Collections.sort(numbers);
		 System.out.println("Min = " + numbers.get(0));
		 System.out.println("Max = " + numbers.get(numbers.size()-1));
		 System.out.println("Total Requests " + sum);
	 }	 
}

