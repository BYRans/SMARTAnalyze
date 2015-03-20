package DataFilter;

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

public class SeparateData {

	public static void main(String[] args) {
		System.out.println("FilterStaticData Running...");
		String staDataPath = "/home/pgxc/SMARTAnalyze/finalStatic.txt";
		String dynDataPath = "/home/pgxc/SMARTAnalyze/finalDynamic.txt";
		String sepStaDataPath = "/home/pgxc/SMARTAnalyze/separateData/staticData/";
		String sepDynDataPath = "/home/pgxc/SMARTAnalyze/separateData/dynamicData/";

		HashSet<String> failureSet = new HashSet<String>();
		HashSet<String> healthSet = new HashSet<String>();
		HashSet<String> lastOKSet = new HashSet<String>();

		List<String> healthData = new ArrayList<String>();
		List<String> failureData = new ArrayList<String>();

		// 读static信息数据，获取健康磁盘序列号
		try {
			File termSetFile = new File(staDataPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if ("OK".equals(lineArr[12])
						&& "2015-02-10".equals(lineArr[3].split(" ")[0]))
					lastOKSet.add(lineArr[9]);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// 读static数据，过滤static数据
		try {
			File termSetFile = new File(staDataPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if (lastOKSet.contains(lineArr[9]))
					healthData.add(curLine);
				else
					failureData.add(curLine);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// 保存static健康磁盘数据
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					sepStaDataPath + "staticHealth.txt"), false));
			for (String record : healthData) {
				writer.write(record + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 保存static故障磁盘数据
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					sepStaDataPath + "staticFailure.txt"), false));
			for (String record : failureData) {
				writer.write(record + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		healthData = new ArrayList<String>();
		failureData = new ArrayList<String>();
		// 读dynamic数据，过滤dynamic数据
		try {
			File termSetFile = new File(dynDataPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if (lastOKSet.contains(lineArr[lineArr.length - 1]))
					healthData.add(curLine);
				else
					failureData.add(curLine);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// 保存dynamic健康磁盘数据
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					sepDynDataPath + "dynamicHealth.txt"), false));
			for (String record : healthData) {
				writer.write(record + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 保存dynamic故障磁盘数据
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					sepDynDataPath + "dynamicFailure.txt"), false));
			for (String record : failureData) {
				writer.write(record + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("FilterStaticData Completed.");
	}
}
