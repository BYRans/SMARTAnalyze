package EM;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class Main {

	public static void main(String args[]) throws Exception {

		// String fileName = "D:/workspace/EMAlgorithm/em_data.txt";
		// Main m = new Main();
		// ModifiedEM em = new ModifiedEM(fileName);
		//
		//
		// em.loadDataFromTheFile(fileName);
		// em.setNumOfClusters(5);
		// em.initParameters(false);

		String realFailDynPath = "C:/Users/Administrator/Desktop/SMARTAnalyze/separateData/dynamicData/realFailureDynData.txt";

		Instances instances = getFileInstances(realFailDynPath);
		// 把数据集全部输入出
		System.out.println(instances);
	}

	public static Instances getFileInstances(String fileName) throws Exception {
		FileReader frData = new FileReader(fileName);
		Instances data = new Instances(frData);
		return data;
	}
}
