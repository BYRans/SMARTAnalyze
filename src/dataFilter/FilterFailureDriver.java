package dataFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FilterFailureDriver {

	public static void main(String[] args) {
		String realFailDynPath = "C:/Users/Administrator/Desktop/SMARTAnalyze/separateData/dynamicData/realFailureDynData.txt";
		String dynamicPath = "C:/Users/Administrator/Desktop/SMARTAnalyze/separateData/dynamicData/dynamicFailure.txt";
		String staticPath = "C:/Users/Administrator/Desktop/SMARTAnalyze/separateData/staticData/staticFailure.txt";
		List<String> list = new ArrayList<String>();
		HashSet<String> serNumSet = new HashSet<String>();
		try {
			File termSetFile = new File(staticPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if (!"OK".equals(lineArr[lineArr.length - 1]))
					serNumSet.add(lineArr[lineArr.length - 4]);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			File termSetFile = new File(dynamicPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if (serNumSet.contains(lineArr[lineArr.length - 1]))
					list.add(curLine);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					realFailDynPath), false));
			for (String record : list) {
				writer.write(record + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		
		
		
		
		
		
		
		

	}
}
