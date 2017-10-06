package mcgill;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Discrete {
	
//	public static final String FILENAME;// = "report.txt";
	static String INPUT;
	static String TOUTPUT;
	static String OUTPUT;
	public Discrete(){
		
	}

	public void mySplitter(){
		split(INPUT, OUTPUT);
		split(INPUT, TOUTPUT);
	}
	
public static void reader() throws IOException, FileNotFoundException{
		
		// Variables help to identify the id, longitude and latitude in the cogent file
		String average = "";
		String numObs = "";
		String id = "";
		
		// helps to identify the edges
		int source = -1;
		int target = -1;
		BufferedWriter bufferedWriter;
		
		// trying to write output to a file called fogResults.txt
		try(FileWriter fw = new FileWriter(OUTPUT, false)){
			bufferedWriter = new BufferedWriter(fw);
			
			// read the contents of the cogentco file
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader(INPUT))) {
				String currentLine;
				
				
				while((currentLine = bufferedReader.readLine()) != null){
					currentLine = currentLine.trim();
					
					//Nodes
					//while reading line by line, we look out for lines with id and take the value after the space
					if (currentLine.startsWith("REPORT")){
						String [] splits = currentLine.split(" ");
//						id = Integer.parseInt(splits[1]);
						id = (splits[4]);
					
					}
					
					if (currentLine.startsWith("Utilization")){
						String [] splits = currentLine.split(" +");
//						average = Double.parseDouble(splits[0]);
						average =  (splits[3]);
					}
					if (currentLine.startsWith("Wait")){
						String [] splits = currentLine.split(" +");
//						numObs = Integer.parseInt(splits[5]);
						numObs = (splits[5]);
						
					}
					if (average != "" && numObs != "" & id != ""){
//					bufferedWriter.write(id + " ");
//					bufferedWriter.write("\n");
//					bufferedWriter.write(average + " " + (double)Integer.parseInt(numObs)/500.0);
						bufferedWriter.write(average);// + " " + (double)Integer.parseInt(numObs)/500.0);
					bufferedWriter.write("\n");
					average = ""; numObs = "";
					}
					
					
				}
				
				bufferedReader.close();	
			} 
			
			
			bufferedWriter.flush();
			bufferedWriter.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		BufferedWriter bw;
		String num1Obs = "";
		// Throuput write
		try(FileWriter fw = new FileWriter(TOUTPUT, false)){
			bw = new BufferedWriter(fw);
			
			// read the contents of the cogentco file
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader(INPUT))) {
				String currentLine;
				
				
				while((currentLine = bufferedReader.readLine()) != null){
					currentLine = currentLine.trim();
					
					//Nodes
					//while reading line by line, we look out for lines with id and take the value after the space
					if (currentLine.startsWith("REPORT")){
						String [] splits = currentLine.split(" ");
//						id = Integer.parseInt(splits[1]);
						id = (splits[4]);
					
					}
					
					if (currentLine.startsWith("Wait")){
						String [] splits = currentLine.split(" +");
//						numObs = Integer.parseInt(splits[5]);
						num1Obs = (splits[5]);
						
					}
					if ( num1Obs != "" ){
//					bufferedWriter.write(id + " ");
//					bufferedWriter.write("\n");
//					bufferedWriter.write(average + " " + (double)Integer.parseInt(numObs)/500.0);
//					bw.write(Double.parseDouble(num1Obs)/500.0);
					double val = (double)(Integer.parseInt(num1Obs)/500.0);
						bw.write("" +  val);
						
					bw.write("\n");
					 num1Obs = "";
					}
					
					
				}
				
				bufferedReader.close();	
			} 
			
			
			bw.flush();
			bw.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void split(String input, String output){
		BufferedWriter bufferedWriter;
		String average = "", id = "", numObs = "";
		
		try(FileWriter fw = new FileWriter(output, false)){
			bufferedWriter = new BufferedWriter(fw);
			
			// read the contents of the cogentco file
			try (BufferedReader bufferedReader = new BufferedReader(new FileReader(input))) {
				String currentLine;
				
				
				while((currentLine = bufferedReader.readLine()) != null){
					currentLine = currentLine.trim();
					
					//Nodes
					//while reading line by line, we look out for lines with id and take the value after the space
					if (currentLine.startsWith("REPORT")){
						String [] splits = currentLine.split(" ");
//						id = Integer.parseInt(splits[1]);
						id = (splits[4]);
					
					}
					
					if (currentLine.startsWith("Utilization")){
						String [] splits = currentLine.split(" +");
//						average = Double.parseDouble(splits[0]);
						average =  (splits[3]);
					}
					if (currentLine.startsWith("Wait")){
						String [] splits = currentLine.split(" +");
//						numObs = Integer.parseInt(splits[5]);
						numObs = (splits[5]);
						
					}
					if (average != "" && numObs != "" & id != ""){
//					bufferedWriter.write(id + " ");
//					bufferedWriter.write("\n");
					bufferedWriter.write(average + " " + (double)Integer.parseInt(numObs)/500.0);
					bufferedWriter.write("\n");
					average = ""; numObs = "";
					}
					
					
				}
				
				bufferedReader.close();	
			} 
			
			
			bufferedWriter.flush();
			bufferedWriter.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException, IOException{
		INPUT = args[0];
		OUTPUT = args[1];
		TOUTPUT = args[2];
		reader();
		System.out.print("DOne");
	}
	
}
