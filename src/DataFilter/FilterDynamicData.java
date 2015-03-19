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
import java.util.regex.Pattern;

public class FilterDynamicData {

	public static void main(String[] args) {
		System.out.println("FilterDynamicData Running...");
		String rawDataPath = "/home/pgxc/smart_dynamic_data1.csv";
		String standerStaPath = "/home/pgxc/SMARTAnalyze/filterd_smart_data/dynamic_data/stander_smart_dynamic_info.txt";
		String wrongStaPath = "/home/pgxc/SMARTAnalyze/filterd_smart_data/dynamic_data/wrong_smart_dynamic_info.txt";

		List<String> standerData = new ArrayList<String>();
		List<String> wrongData = new ArrayList<String>();
		try {
			File termSetFile = new File(rawDataPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if (lineArr.length == 36)
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

		System.out.println("FilterDynamicData Completed.");
	}
}
