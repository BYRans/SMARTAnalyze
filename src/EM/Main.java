package EM;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import Golobal.UTILITY;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class Main {

	public static void main(String args[]) throws Exception {
		String fileName = "D:/workspace/EMAlgorithm/em_data.txt";
		Main m = new Main();
		ModifiedEM em = new ModifiedEM(fileName);
		em.loadDataFromTheFile(fileName);
		em.setNumOfClusters(UTILITY.K);
		em.initParameters(false);
	}

}
