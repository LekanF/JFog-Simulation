package mcgill;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mcgill.Util.SourceTarget;

import org.jfree.ui.RefineryUtilities;

public class Schedule implements Runnable {
 // Send tasks to either fog, pool or cloud.
	static FogCreator myFogs;
	static List<Device> myDevices;
	static Util.SourceTarget dataStructure = new Util.SourceTarget();
	static BufferedWriter bWriter;
	
	public Schedule(){
		
	}
	
	public static Integer[][] assignDevicesToFogs() throws FileNotFoundException, IOException{
		
		myDevices = FogCreator.createDevicesForFogs();
		
		List<Fog> FogNodes = FogCreator.googleFogs;
		System.out.println("Im here 2");
		Integer [][] data = new Integer [2][FogNodes.size()];
		
//		for (Device device : myDevices){
//			Device.Task tasks = new Device.Task();
//			tasks = device.generateTasksRequest();
//			for(Fog homeFog : FogNodes){
//				if (device.getDevice_id()== homeFog.getId()){
//					Latency(device, homeFog);
//					schedule(task,homefog,simtime+lat);
//				}
//				
//			}
//		}
		
		for (Fog fog : FogNodes) {
			for (Device device : myDevices){
				if (device.getDevice_id() == fog.getId()){
//				if(device.getDeviceLongitude() == fog.fog.getLongitude()){ // If devices are in same location as fog
				
				scheduleRequest(device, fog);
//				}
				}
			}
		}
		
//		int j = 0;
////		while (j<5){
//		for (int i = 0; i < FogNodes.size(); i++){
//			scheduleRequest(myDevices.get(i), FogNodes.get(i));
////			bWriter.write("Iteration: " + j);
//		}
//			j++;
//		}
		
		Pool p = new Pool();
		p.cPool(FogNodes);
		for (Fog fog : FogNodes) {
			for (Device device : myDevices){
				if (device.getDevice_id() == fog.getId()){						
					useFogPool(device, fog, p);
				}
			}
		}
		
		for (int i=0; i<dataStructure.getSize(); i++){
			System.out.println("Computed: " + dataStructure.getSource(i) + "Remaining: " + dataStructure.getTarget(i));
			data[0][i] = dataStructure.getSource(i);
			data[1][i] = dataStructure.getTarget(i);
		}
		
		
////		for(int i = 0; i< 2; i++){
//			for(int j = 0; j < FogNodes.size(); j++){
////				if(i==0){
//					data[0][j] = dataStructure.getSource(j);
////				}
////				 if(i==1){
//					data[1][j] = dataStructure.getTarget(j);
////				}
////				System.out.println("Data is "+data[i][j]);
//			}
////		}
//		for(int i = 0; i<2;i++){
//			for(int j=0;j<FogNodes.size();j++){
//				System.out.println("Data is "+data[i][j]);
//			}
//		}
		return data;
	}
	
	public static void scheduleRequest(Device d, Fog f) throws FileNotFoundException, IOException{
//		BufferedWriter bWriter ;//= new BufferedWriter(arg0);
		
		try(FileWriter fw = new FileWriter("dataResults.txt", true)){
			bWriter = new BufferedWriter(fw);
			
			Device.Task numberOfTasks = new Device.Task();
			double remCPU = f.cpu; 
			double remMem = f.memory;
			int i = 0; int remTasks = 0;
			numberOfTasks = d.generateTasksRequest();
			double holder = 0; int count = 0;
			
			
			
			for ( i = 0; i < numberOfTasks.getSize(); i++){
				holder += numberOfTasks.getCPU(i);
				
				if(remCPU / holder <= 1){
					System.out.println("Fog is Overload");
					remTasks = (numberOfTasks.getSize()-1) - i;

					System.out.println("Total tasks: " + numberOfTasks.getSize() + " Remaining Tasks: " + remTasks);					
					bWriter.write(f.getId() + " " + i +" " + remTasks); // id, computed, remaining tasks. 
					bWriter.write("\n");
					dataStructure.insert(i, remTasks);// tasks computed and remaining tasks
					break;
					
				}
				
				else if(i == numberOfTasks.getSize()-1 && remCPU/holder > 1) {
					remTasks = (numberOfTasks.getSize()-1) - i;
					bWriter.write(f.getId() + " " + i +" " + remTasks); // id, computed, remaining tasks. 
					bWriter.write("\n");
					dataStructure.insert(i, remTasks);
					System.out.println("It is not overloaded yet");
				
				}

			}
			bWriter.flush();
			bWriter.close();
			}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	public static void useFogPool(Device d, Fog f, Pool p)
	{
		try(FileWriter fw = new FileWriter("dataResults.txt", true)){
			bWriter = new BufferedWriter(fw);
			
			Device.Task numberOfTasks = new Device.Task();
			double remCPU = f.cpu; 
			double remMem = f.memory;
			int i = 0; int remTasks = 0;
			numberOfTasks = d.generateTasksRequest();
			double holder = 0; int count = 0;
			
			for ( i = 0; i < numberOfTasks.getSize(); i++){
				holder += numberOfTasks.getCPU(i);
				
				if(remCPU / holder <= 1){
					System.out.println("Fog is Overload");
					remTasks = (numberOfTasks.getSize()-1) - i;

					System.out.println("Total tasks: " + numberOfTasks.getSize() + " Remaining Tasks: " + remTasks);					
					bWriter.write(f.getId() + " " + i +" " + remTasks); // id, computed, remaining tasks. 
					bWriter.write("\n");
					dataStructure.insert(i, remTasks);// tasks computed and remaining tasks
					break;
					
				}
				
				else if(i == numberOfTasks.getSize()-1 && remCPU/holder > 1) {
					remTasks = (numberOfTasks.getSize()-1) - i;
					bWriter.write(f.getId() + " " + i +" " + remTasks); // id, computed, remaining tasks. 
					bWriter.write("\n");
					dataStructure.insert(i, remTasks);
					System.out.println("It is not overloaded yet");
				
				}

			}
			bWriter.flush();
			bWriter.close();
			}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
//	public static void main ( String [] args) throws FileNotFoundException, IOException {
//		assignDevicesToFogs();
////		System.out.println("Im here after running the assign method");
//		
//		final Figures myFig = new Figures("Bar Chart");
//		myFig.pack();
//		RefineryUtilities.centerFrameOnScreen(myFig);
//		myFig.setVisible(true);
//		
////		System.out.println("This is currently running on the main thread, " + "the id is: "
////		+ Thread.currentThread().getId());
////		Schedule schedule = new Schedule();
////		Thread t  = new Thread(schedule);
////		t.start();
//	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.print("This is the currently running thread on a seperate thread, " +
		"the id is: " + Thread.currentThread().getId());
	}
	
}
