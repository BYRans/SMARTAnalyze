package Training;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Golobal.UTILITY;

public class FeatureSeletion {

	public static void main(String[] args) throws IOException {
		System.out.println("Running...");
		List<String[]> vectorList = new ArrayList<String[]>();
		try {
			File file = new File(UTILITY.DYNAMIC_HEALTH_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				if ("".equals(curLine.trim()))
					continue;
				lineArr = curLine.split("\t");
				// 选取哪些列作为特征
				String[] vector = new String[12];
				vector[0] = lineArr[4];
				vector[1] = lineArr[9];
				vector[2] = lineArr[10];
				vector[3] = lineArr[21];
				vector[4] = lineArr[22];
				vector[5] = lineArr[23];
				vector[6] = lineArr[24];
				vector[7] = lineArr[25];
				vector[8] = lineArr[26];
				vector[9] = lineArr[30];
				vector[10] = lineArr[32];
				vector[11] = lineArr[33];
				vectorList.add(vector);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// 保存特征向量
		UTILITY.INIT_FILE(UTILITY.FEATURE_VECTOR_PATH);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					UTILITY.FEATURE_VECTOR_PATH), false));
			for (String[] vectorArr : vectorList) {
				for (String item : vectorArr)
					writer.write(item + "\t");
				writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Finished...");
	}

}
