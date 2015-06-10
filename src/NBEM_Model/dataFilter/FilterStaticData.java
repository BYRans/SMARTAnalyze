package NBEM_Model.dataFilter;

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

public class FilterStaticData {

	public static void main(String[] args) {
		System.out.println("FilterStaticData Running...");
		String rawDataPath = "C:/Users/Administrator/Desktop/SMART project/data set/smart data/smart_static_info.csv";
		String standerStaPath = "C:/Users/Administrator/Desktop/SMARTAnalyze/filterd smart data/static data/stander_smart_static_info.txt";
		String wrongStaPath = "C:/Users/Administrator/Desktop/SMARTAnalyze/filterd smart data/static data/wrong_smart_static_info.txt";

		List<String> standerData = new ArrayList<String>();
		List<String> wrongData = new ArrayList<String>();

		try {
			File termSetFile = new File(rawDataPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if (lineArr.length == 13)
					standerData.add(curLine);
				else
					wrongData.add(curLine);

			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					standerStaPath), false));
			for (String record : standerData) {
				writer.write(record + "\r\n");
				writer.flush();
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					wrongStaPath), false));
			for (String record : wrongData) {
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
