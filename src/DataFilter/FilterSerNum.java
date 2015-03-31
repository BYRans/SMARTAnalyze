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
import java.util.regex.Pattern;

public class FilterSerNum {

	public static void main(String[] args) {
		System.out.println("SeparateData Running...");
		HashSet<String> serNumSet = new HashSet<String>();
		HashSet<String> dynamicSet = new HashSet<String>();
		HashSet<String> staticSet = new HashSet<String>();
		// String path =
		// "/home/pgxc/SMARTAnalyze/filterd_smart_data/static_data/stander_smart_static_info.txt";
		// String path =
		// "/home/pgxc/SMARTAnalyze/filterd_smart_data/dynamic_data/stander_smart_static_info.txt";
		String staticPath = "/home/pgxc/SMARTAnalyze/filterd_smart_data/static_data/stander_smart_static_info.txt";
		String dynamicPath = "/home/pgxc/SMARTAnalyze/filterd_smart_data/dynamic_data/stander_smart_dynamic_info.txt";
		String reportPath = "/home/pgxc/SMARTAnalyze/report.txt";
		String serNumPath = "/home/pgxc/SMARTAnalyze/SerialNumber.txt";
		String finalStaPath = "/home/pgxc/SMARTAnalyze/finalStatic.txt";
		String finalDynPath = "/home/pgxc/SMARTAnalyze/finalDynamic.txt";

		try {
			File termSetFile = new File(staticPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				serNumSet.add(lineArr[lineArr.length - 4]);
				staticSet.add(lineArr[lineArr.length - 4]);
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
				dynamicSet.add(lineArr[lineArr.length - 1]);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					reportPath), false));
			writer.write("static盘数: " + serNumSet.size() + "\r\n");
			writer.write("dynamic盘数: " + dynamicSet.size() + "\r\n");

			writer.write("\r\n static中dynamic没有的盘号: \r\n");
			staticSet.removeAll(dynamicSet);
			for (String serNum : staticSet) {
				writer.write(serNum + "\r\n");
				writer.flush();
			}

			writer.write("\r\n dynamic中static没有的盘号: \r\n");
			dynamicSet.removeAll(serNumSet);
			for (String serNum : dynamicSet) {
				writer.write(serNum + "\r\n");
				writer.flush();
			}

			serNumSet.removeAll(staticSet);
			writer.write("\r\n 交集盘号(" + serNumSet.size() + "块): \r\n");
			for (String serNum : serNumSet) {
				writer.write(serNum + "\r\n");
				writer.flush();
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					serNumPath), false));
			for (String serNum : serNumSet) {
				writer.write(serNum + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<String> staticData = new ArrayList<String>();
		List<String> dynamicData = new ArrayList<String>();
		try {
			File termSetFile = new File(staticPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if (serNumSet.contains(lineArr[9]))
					staticData.add(curLine);
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
					dynamicData.add(curLine);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		
		


		
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					finalStaPath), false));
			for (String record : staticData) {
				writer.write(record + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					finalDynPath), false));
			for (String record : dynamicData) {
				writer.write(record + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("SeparateData Completed.");
	}
}
