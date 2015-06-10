package dataFilter;

import golobal.UTILITY;

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

public class ClassifyByVendor {

	public static void main(String[] args) throws IOException {
		String staticPath = "/home/pgxc/SMARTAnalyze/unityData/finalStatic.txt";
		String dynPath = "/home/pgxc/SMARTAnalyze/unityData/finalDynamic.txt";
		String vendorStaticPath = "/home/pgxc/SMARTAnalyze/Vendor/static_data/SEAGATEStaticData.txt";
		String vendorDynamicPath = "/home/pgxc/SMARTAnalyze/Vendor/dynamic_data/SEAGATEDynamicData.txt";

		List<String> staticData = new ArrayList<String>();
		List<String> dynData = new ArrayList<String>();
		HashSet<String> set = new HashSet<String>();
		try {
			File termSetFile = new File(staticPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if ("SEAGATE".equals(lineArr[4])) {
					staticData.add(curLine);
					set.add(lineArr[lineArr.length - 4]);
				}
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			File termSetFile = new File(dynPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if (set.contains(lineArr[lineArr.length - 1])) {
					dynData.add(curLine);
				}
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		
		UTILITY.INIT_FILE(vendorStaticPath);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					vendorStaticPath), false));
			for (int i = 0; i < staticData.size(); i++) {
				writer.write(staticData.get(i) + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		UTILITY.INIT_FILE(vendorDynamicPath);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					vendorDynamicPath), false));
			for (int i = 0; i < dynData.size(); i++) {
				writer.write(dynData.get(i) + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
