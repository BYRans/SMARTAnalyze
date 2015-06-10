package verification;

import golobal.UTILITY;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TestDataProcess {
	public static List<double[]> cutPointsList = new ArrayList<double[]>();// 各特征binning切分点

	public static void main(String[] args) throws IOException {

		List<String[]> vectorList = new ArrayList<String[]>();
		try {
			File file = new File(UTILITY.RAW_TEST_SET_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = null;
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
		readCutPointsList(UTILITY.CUT_POINTS_PATH);
		List<Integer[]> binVecList = new ArrayList<Integer[]>();
		// 由cutPointsList binning每个特征
		for (String[] vector : vectorList) {
			Integer[] binVector = new Integer[vector.length];
			for (int i = 0; i < vector.length; i++) {
				boolean binned = false;
				for (int j = 0; j < cutPointsList.get(i).length; j++) {
					if (Integer.valueOf(vector[i]) < cutPointsList.get(i)[j]) {
						binVector[i] = j + 1;
						binned = true;
						break;
					}
				}
				if (!binned) {
					binVector[i] = UTILITY.BINS;// 由于这是故障数据，由健康数据得到的切分点如果过小，即故障数据超过了最大的切分点，如何处理更好？？？？
				}
			}
			binVecList.add(binVector);
		}
		
		//将binning后的测试数据写入测试文件
		UTILITY.INIT_FILE(UTILITY.TEST_SET_PATH);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					UTILITY.TEST_SET_PATH), false));
			for (Integer[] binVectorArr : binVecList) {
				for (Integer item : binVectorArr)
					writer.write(item + "\t");
				writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读cutPointsList文件
	 * 
	 * @param path
	 *            cutPointsList存储文件
	 */
	public static void readCutPointsList(String path) {

		try {
			File file = new File(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				if ("".equals(curLine.trim()))
					continue;
				lineArr = curLine.split("\t");
				double[] cutPoints = new double[lineArr.length];
				for (int i = 0; i < lineArr.length; i++) {
					cutPoints[i] = Double.parseDouble(lineArr[i]);
				}
				cutPointsList.add(cutPoints);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
}
