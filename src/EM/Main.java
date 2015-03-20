package EM;

public class Main {
	
	
	public static void main(String args[]) {
		
		String fileName = "D:/workspace/EMAlgorithm/em_data.txt";
		Main m = new Main();
		EM em = new EM(fileName);
		
		
		em.loadDataFromTheFile(fileName);
		em.setNumOfClusters(5);
		em.initParameters(false);
		
	}
}
