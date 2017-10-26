package mcgill;

import java.io.BufferedReader;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.sun.javafx.css.CalculatedValue;

import mcgill.Util.Compress;
import umontreal.iro.lecuyer.simprocs.*;

public class FogCreator  extends JFrame {
	public static final String FILENAME = "Cogentco.gml";
	public static Util.InputMap myMap = new Util.InputMap();;
	public static Util.SourceTarget myEdge =  new Util.SourceTarget();
	private static Point2D.Double[] points;
	
	static List<Fog> fogs = new ArrayList<Fog>(); // Fogs with cogentData only
	static List<Fog> googleFogs = new ArrayList<Fog>(); // Fogs with location Data and google capacities.
		
	//Here, not really necessary, trying to get the rectangles
	public FogCreator(){
		super("JamCloud Simulation");
		
		getContentPane().setBackground(Color.WHITE);
		setSize(1580, 1300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
	
	public static void reader() throws IOException, FileNotFoundException{
		
		// Variables help to identify the id, longitude and latitude in the cogent file
		int id = -1;
		double lng = 0;
		double lat = 0;
		
		// helps to identify the edges
		int source = -1;
		int target = -1;
		BufferedWriter bufferedWriter;
		
		// trying to write output to a file called fogResults.txt
		try(FileWriter fw = new FileWriter("fogResults.txt", false)){
			bufferedWriter = new BufferedWriter(fw);
			
			// read the contents of the cogentco file
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FILENAME))) {
				String currentLine;
				
				
				while((currentLine = bufferedReader.readLine()) != null){
					currentLine = currentLine.trim();
					
					//Nodes
					//while reading line by line, we look out for lines with id and take the value after the space
					if (currentLine.startsWith("id")){
						String [] splits = currentLine.split(" ");
						id = Integer.parseInt(splits[1]);					
					}
					if (currentLine.startsWith("Longitude")){
						String [] splits = currentLine.split(" ");
						lng = Double.parseDouble(splits[1]);
					
					}
					if (currentLine.startsWith("Latitude")){
						String [] splits = currentLine.split(" ");
						lat = Double.parseDouble(splits[1]);
					}
					
					// Here, we insert the id, long, and lat into a custom array myMap
					if (id != -1 && lng != 0 && lat != 0){
						myMap.insert(id, lat, lng);
						id = -1;
						lng = 0;
						lat = 0;						
					}
					
					// Edges
					//Checks the edges data for source and data and take their ids.
					if (currentLine.startsWith("source")){
						String [] splits = currentLine.split(" ");
						source = Integer.parseInt(splits[1]);					
					}

					if (currentLine.startsWith("target")){
						String [] splits = currentLine.split(" ");
						target = Integer.parseInt(splits[1]);					
					}
					
					if (source != -1 && target != -1 ){
						myEdge.insert(source, target);
						source = -1;
						target = -1;									
					}
				}
				bufferedReader.close();	
			} 
			
			//Here we write to file, not really necessarily but it is for bigger files.
			//Dont use myMap because of memnory constraints going forward.
			for (int i = 0; i < myMap.getSize(); i ++){		
				
				bufferedWriter.write(myMap.getId(i) + " " + myMap.getLong(i) + " " + myMap.getLat(i));
				
				bufferedWriter.write("\n");
			}
			bufferedWriter.flush();
			bufferedWriter.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
		
	public static double calEdgeLatencies() throws FileNotFoundException, IOException{
		reader();
		ArrayList<Double> numbers = new ArrayList<Double>();
		double srcLong = 0, srcLat = 0, tarLong = 0, tarLat = 0;
		Util.Latency myLat = new Util.Latency();
				
			for (int i = 0; i < myEdge.getSize(); i++){				
			
			int src = myEdge.getSource(i);	
			int dest = myEdge.getTarget(i);
		
			for (int j = 0; j < myMap.getSize(); j++){
				// Gets long and lat for edge src
				if (myMap.getId(j) == src){
					srcLong = myMap.getLong(j);
					srcLat = myMap.getLat(j);
				}				
				// Gets long and lat for edge destination
		
				if(myMap.getId(j) == dest){
					tarLong = myMap.getLong(j);
					tarLat = myMap.getLat(j);
				}
			}			
			
			numbers.add(myLat.queueLatency(srcLong, srcLat, tarLong, tarLat));
			}
			
		myLat.meanLatency(numbers);
		myLat.stdLatency(numbers);
		return myLat.deviceLatency();		
	}
	
	public static void getNodeEdge() throws FileNotFoundException, IOException{
		reader();
		int outDegree = 0, inDegree = 0;		
		
		Map<Integer, Integer> nodeDegree = new HashMap<Integer, Integer>();
						
		for (int i = 0; i < myMap.getSize(); i ++){
//			Fog tempFog = new Fog(myMap.getId(i), myMap.getLat(i), myMap.getLong(i));
//			fogs.add(tempFog);
			for (int j = 0; j < myEdge.getSize(); j++){
			
				if (myMap.getId(i) == myEdge.getSource(j)){ // Gets the outDegree - how many connections leaving the node
					outDegree += 1;
				}
				if (myMap.getId(i) == myEdge.getTarget(j)){ // Gets the inDegree - how many connections coming into the node
					inDegree += 1;
				}				
			}
			nodeDegree.put(myMap.getId(i), outDegree + inDegree);
			outDegree = 0; inDegree = 0;
//			System.out.println(myMap.getId(i) + " " + myMap.getLat(i)+ " " + myMap.getLong(i));
		}
		int co = 1;
		for (Entry<Integer, Integer> entry: nodeDegree.entrySet()){
			// Checks if the node has more than 4 connections, make it a fog
			if (entry.getValue() >= 4){
				//System.out.println(co + " " + entry.getKey()+ " " + entry.getValue());
				co++;
				for (int i = 0; i < myMap.getSize(); i++){
				if(entry.getKey() == myMap.getId(i)){//assign fogs to nodes which have more than 4 node degrees
					Fog tempFog = new Fog(myMap.getId(i), myMap.getLat(i), myMap.getLong(i));
					fogs.add(tempFog);
				}
				}
			}
		}
				
		Util.Compress Mcapacity = new Util.Compress();
		Util.Latency latent = new Util.Latency();
		
		Mcapacity.fileSorter("machine_events.csv", "machine_output1.txt", 3);

		List<Float> machineCPU = new ArrayList<Float>();
		List<Float> machineMem = new ArrayList<Float>();
		float total = 0; float totalMem = 0;
		int counter = 0;
		
		// This aggregates the CPUs
		for(int i = 0; i < 500; i++ ){ //500/20 gives 25 Fogs
			total += Mcapacity.machine.getCPU(i);
			totalMem += Mcapacity.machine.getMemory(i);
			counter++;
			
			if(counter == 20){
				machineCPU.add(total);
				machineMem.add(totalMem);
				total = 0;
				counter = 0;
			}
		}
	
		
		for(int i = 0; i < fogs.size();i++){ //  Adds data from google to cogent nodes, only 23
//		for(int i = 0; i < 10;i++){// fog.size instead of 10 
			//Simple way, just add a machine to a fog and multiply the capapcity by 10 for aggregation
//				Fog capacitatedFog = new Fog(fogs.get(i), Mcapacity.machine.getCPU(i)*20, Mcapacity.machine.getMemory(i) );
			Fog capacitatedFog = new Fog(fogs.get(i).getId(), fogs.get(i), machineCPU.get(i), machineMem.get(i) );
//			Resource r = null;
//			Fog capacitatedFog = new Fog(fogs.get(i).getId(), fogs.get(i), machineCPU.get(i), machineMem.get(i), r );
				googleFogs.add(capacitatedFog);                                             
		}		
	}
	
	public static void drawShapes(Graphics g) throws FileNotFoundException, IOException{
		reader();
		double latMin, latMax, longMin, longMax = 0;
		Graphics h;
		
		latMin = myMap.getMinLat() ;
		latMax = myMap.getMaxLat()  ;
		longMin = myMap.getMinLong() ;
		longMax = myMap.getMaxLong();
		System.out.println (longMin + " " + latMin + " " + longMax + " " + latMax );
		Graphics2D g2D = (Graphics2D) g;
//		Rectangle2D.Double rect = new Rectangle2D.Double(longMin , latMin, (longMax), latMax);
//		Rectangle2D.Double rect = new Rectangle2D.Double((longMin-longMin) , latMin, (longMax-longMin), latMax);
		Rectangle2D.Double rect = new Rectangle2D.Double(-122.67621d, 19.42847d, 36.25d, 60.16952);
		g2D.draw(rect);
//		h.f
		
//		int j = 0;
//		for (int i =0; i < myMap.getSize(); i++){
////			Point2D.Double point = new Point2D.Double(myMap.getLong(i), myMap.getLat(i));
//			points[j++] = new Point2D.Double(myMap.getLong(i), myMap.getLat(i));			
//		}
//        g.setColor(Color.RED);
//        for(final java.awt.geom.Point2D.Double p : points){
//            g.fillRect((int) p.getX(), (int) p.getY(), 2, 2);
//        }
//		
		
	}
	
	public void paint(Graphics g){
		super.paint(g);
		try {
			drawShapes(g);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<Device> createDevicesForFogs() throws FileNotFoundException, IOException{
		getNodeEdge();
		int j = 0;
		List<Device> allDevices = new ArrayList<Device>();

		// Here we create devices around all fogs but more around googlefogs.
		for (Fog fog : googleFogs) {
			int random = (int)(Math.random() * 2 + 1); // create a random number of devices up to 20
//			for(Integer i = 0; i < random; i++){
			for(Integer i = 0; i < 5; i++){ // put random instead of 1 because with 1, I am creating one device per fog
//				Device myDevice = new Device(fog.fog.getId(), fog.fog.getLongitude(), fog.fog.getLatitude(), calEdgeLatencies());
				String id = fog.fog.getId().toString() + i.toString();
				// Here we are creating devices around a fog and giving it latency to that particular fog
				Device myDevice = new Device(Integer.parseInt(id), fog.fog.getLongitude(), fog.fog.getLatitude(), calEdgeLatencies());
//				myDevice.insertLatency(calEdgeLatencies() + Latency.DFLatency(myDevice, fog));
				allDevices.add(myDevice);				
			}
			//System.out.println("Devices size per fog increasing : "+allDevices.size());
		}
		
		return allDevices;
	}
	static LinkedList<Double> waitList;
	static LinkedList<Double> servList;
	
	public static void PSserveTask(Device d, Fog f, int num, double delay) throws FileNotFoundException, IOException{
		getNodeEdge();
		waitList = new LinkedList<Double>();
		
		int capacity = f.getCapacity();
		if (num < capacity){
			capacity -= num;
			waitList.add(0.0);
			servList.add(delay);
			capacity += num;
			// after its finished add the resource back, 
			//but we need to deal with time
		}
		else
		{
			delay = delay + 2.0; // Do the maths
			servList.add(delay);
		}
		
		
	}
	
	public void fogPS(Device.Task t){
		double capacity;
		
		
		
	}
	public void fogWR(){
		
	}
	public static void mapTasksToFogs() throws FileNotFoundException, IOException{
//		reader();
		getNodeEdge();
		Util.Compress numTasks;
		
		Pool myPool;
		
		numTasks = new Util.Compress();
		numTasks.fileSorter("task_events.csv", "task_output.txt", 4);
		
//		Device myDev = new Device();
		int random;
		double remCPU = 0, remMem = 0;
		int count = 1; 
		ArrayList<Double> fogLatencyNumbers = new ArrayList<Double>();
		double res;
		Util.Latency latent = new Util.Latency();
		
		double average = 0, total = 0, sd = 0, stdev = 0;
		//Get latency with respect to 0;
		for (Fog fog : googleFogs) {
//		for ( int i = 0 ; i < googleFogs.size(); i ++){
			// this is wrong, I need to get the edges and compute the mean and std
			
//			res = latent.queueLatency(googleFogs.get(0).fog.getLongitude(), googleFogs.get(0).fog.getLatitude() ,googleFogs.get(i).fog.getLongitude(), googleFogs.get(i).fog.getLatitude());
//			Above, I mapped the first fog as a relative to other fogs.
			res = latent.queueLatency(0, 0, fog.fog.getLongitude(), fog.fog.getLatitude());
			fogLatencyNumbers.add(res);
		}
		latent.meanLatency(fogLatencyNumbers);
		latent.stdLatency(fogLatencyNumbers);
		double devlat = latent.deviceLatency();
		
		List<Device> allDevices = new ArrayList<Device>();
		
		for (Fog fog : googleFogs) {			
			
			int global = 0;
			remCPU = fog.cpu; remMem = fog.memory;
			random = (int)(Math.random() * 100 + 1); // create random number of tasks per fog up to 100.
			// Create a random number of devices per fog
			for (int j = 0; j < random; j++){
			// Create a task per fog
				Device fogDevice  = new Device(devlat, numTasks.task.getNumCPU(j), numTasks.task.getMem(j));
//				Device fogDevice  = new Device(Latency.DFLatency(fogDevice, fog), numTasks.task.getNumCPU(j), numTasks.task.getMem(j));
				allDevices.add(fogDevice);
			}
			
			for(int i = 0; i < random; i++){			
				remCPU = remCPU - numTasks.task.getNumCPU(i);			
				remMem = remMem - numTasks.task.getMem(i);	
				
				if (remCPU <= 0 || remMem <= 0){
					global = i;
					break;
				}
			}			 
			
		}
//		myPool = new Pool();
//		myPool.cPool(googleFogs);
//		System.out.println("CPU Pool : " + myPool.cpuPool);
	}
//	public static void main (String [] args) throws FileNotFoundException, IOException {

//		getNodeEdge();
//		mapTasksToFogs();
//		calEdgeLatencies();
//		Schedule mySchedule = new Schedule();
//		mySchedule.assignDevicesToFogs();
//		System.out.print("Latency " + calEdgeLatencies());
		
//		Mapper mapp  = new Mapper();
//		mapp.addPoint(-155.27, 67.3623);
		
//		SwingUtilities.invokeLater(new Runnable(){
//			@Override
//			public void run() {
//				new FogCreator().setVisible(true);
//			}
//		});
	

//		Util.Compress myFile = new Util.Compress();
//		myFile.fileSorter("task_events.csv", "task_output.txt", 4);
//		myFile.fileSorter("machine_events.csv", "machine_output.txt", 3);
//		myFile.taskSplitter();
//	}
	static class Latency {
		static double bandwidth = 10; //10Gbps
		static double requestSize = 1; // Size R comes from the task
		double delay;
		double latency;
		
		public double computeDistance(double devLong, double devLat, double fogLong, double fogLat){
			double distanceLatency;
			 
			distanceLatency = Math.sqrt((Math.pow(devLong - fogLong,2)) + (Math.pow(devLat - fogLat,2)));
			return distanceLatency;
		}
		
		public double calLatency(double bandwidth, double taskSize, double delay){
			
			latency = taskSize/bandwidth + delay;
			return latency;
		}
		
		public static double DFLatency(Device d, Fog f){
			
			double delay =  Math.sqrt((Math.pow(d.getDeviceLongitude()- f.fog.getLongitude(),2)) + 
					Math.pow(d.getDeviceLatitude() - f.fog.getLatitude(), 2));
			double latency = requestSize/bandwidth + delay;
			return latency;
//			return delay;
		}
		
		public void algorithm(){
//			 schedule next task
//			if tasks is first from device{
//			sendTasktoFog(time 0) {task uses a link to reach fog and back and also gets served}
//			sendTaskToPool(time 0)
//			get response times and calc delay delay = pool - fog 
//			}
//			
//			else 
//				send to fog at arrival time
//			if fog returns result before delay, dont send to pool
//			otherwise schedule sendtopool(arrTiem + delay)
//			subsequent tasks is sent to fog at arrival time but  to pool at arrivaltime + delay
//			get response times and calculate delay delay = delay + (p-f)
//			
//			if fog returns result before delay dont send to pool
//			keep sending to both until delay caps out
//			if fog response time > delay
//			sendnexttasktoFogOnly()
//			if fog responsetime < delay
//			repeat process and send to both 
//			if fogresponse time 
//			}
			
		}
		
		
		
	}
	
	class FogResource {
		int capacity;
		String name;
		
		public FogResource(int arg0, String arg1) {
//			super(arg0, arg1);
			this.capacity = arg0;
			this.name = arg1;
			// TODO Auto-generated constructor stub
		}
		
		public void FogProcessorSharing(){
			int total = 0;
			
			while (total < capacity){
				break;
			}
		}
	}
}
