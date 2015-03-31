package dataFilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeDynOKFail {

	public static void main(String[] args) {
		String realFailDynPath = "C:/Users/Administrator/Desktop/SMARTAnalyze/separateData/dynamicData/realFailureDynData.txt";
		String dynamicPath = "C:/Users/Administrator/Desktop/SMARTAnalyze/separateData/dynamicData/dynamicFailure.txt";
		String dynMergePath = "C:/Users/Administrator/Desktop/SMARTAnalyze/separateData/dynamicData/dynMerge.txt";
		List<String> failList = new ArrayList<String>();
		List<String> healthList = new ArrayList<String>();

		try {
			File termSetFile = new File(realFailDynPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				String tmp = "";
				for (int i = 4; i < lineArr.length; i++)
					tmp += lineArr[i] + "\t";
				failList.add(tmp);
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
				String tmp = "";
				for (int i = 4; i < lineArr.length; i++)
					tmp += lineArr[i] + "\t";
				healthList.add(tmp);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					dynMergePath), false));
			for (int i = 0; i < failList.size(); i++) {
				writer.write(healthList.get(i) + "\r\n");
				writer.write(failList.get(i) + "\r\n\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
