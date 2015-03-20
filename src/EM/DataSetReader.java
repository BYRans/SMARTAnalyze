package EM;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataSetReader {

	String fileName = null;
	
	DataSetReader() {
		
	}
	
	DataSetReader(String fileName) {
		this.fileName = fileName;
	}
	
	// Scan through the 1 dimensional data stored in the file
	public ArrayList readDataFromTheFile(String fileName){

		ArrayList<Double> data = new ArrayList<Double>();

		String line = null;

		try {
			FileReader file = new FileReader(fileName);
			BufferedReader br = new BufferedReader(file);
			
			if (br != null) {
				while ((line = br.readLine()) != null) {
					data.add(Double.parseDouble(line));
				}
			}
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return data;
	}
}
