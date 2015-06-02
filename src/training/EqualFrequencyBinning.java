package training;

import golobal.UTILITY;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EqualFrequencyBinning {

	public static void main(String[] args) throws IOException {
		System.out.println("Running...");
		List<String[]> vectorList = readVector(UTILITY.FEATURE_VECTOR_PATH);
		List<Integer[]> binVecList = new ArrayList<Integer[]>();
		List<double[]> cutPointsList = new ArrayList<double[]>();// ������binning�зֵ�
		if (vectorList.size() == 0) {
			System.out.println("Feature vector dataset exception!");
			return;
		}
		for (int i = 0; i < vectorList.get(0).length; i++) {
			List<Long> dataList = new ArrayList<Long>();// ��Long�����Խ������
			for (int j = 0; j < vectorList.size(); j++) {
				dataList.add(Long.valueOf(vectorList.get(j)[i]));
			}
			double[] cutPoints = calculateCutPointsByEqualFrequencyBinning(dataList);
			cutPointsList.add(cutPoints);
		}

		// �洢cutPointsList
		saveCutPoints(cutPointsList);

		// ��cutPointsList binningÿ������
		for (String[] vector : vectorList) {
			Integer[] binVector = new Integer[vector.length];
			for (int i = 0; i < vector.length; i++) {
				for (int j = 0; j < cutPointsList.get(i).length; j++) {
					if (Integer.valueOf(vector[i]) < cutPointsList.get(i)[j]) {
						binVector[i] = j + 1;
						break;
					}
				}
			}
			binVecList.add(binVector);
		}

		// binning���Vectorд���ļ����ӵ�101����ʼ��ǰ10000�����������ݣ�
		UTILITY.INIT_FILE(UTILITY.BINNED_FEATURE_VECTOR_PATH);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					UTILITY.BINNED_FEATURE_VECTOR_PATH), false));
			for (int i = 10000; i < binVecList.size(); i++) {
				for (Integer item : binVecList.get(i))
					writer.write(item + "\t");
				writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// binning���Vectorд���ļ�(ǰ10000��,��������������)
		UTILITY.INIT_FILE(UTILITY.BINNED_TEST_FEATURE_VECTOR_PATH);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					UTILITY.BINNED_TEST_FEATURE_VECTOR_PATH), false));
			for (int i = 0; i < 10000; i++) {
				for (Integer item : binVecList.get(i))
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

	/**
	 * �Ѹ����������зֵ�д���ļ�
	 * 
	 * @param cutPointsList
	 *            �зֵ�����List
	 * @throws IOException
	 * */
	public static void saveCutPoints(List<double[]> cutPointsList)
			throws IOException {
		UTILITY.INIT_FILE(UTILITY.CUT_POINTS_PATH);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					UTILITY.CUT_POINTS_PATH), false));
			for (double[] cutPoints : cutPointsList) {
				for (double item : cutPoints)
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
	 * ��feature Vector
	 * 
	 * @return List<String[]>
	 * */
	public static List<String[]> readVector(String path) {
		List<String[]> list = new ArrayList<String[]>();
		try {
			File file = new File(UTILITY.FEATURE_VECTOR_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				if ("".equals(curLine.trim()))
					continue;
				lineArr = curLine.split("\t");
				list.add(lineArr);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return list;
	}

	/**
	 * ����һ���������зֵ�
	 *
	 * @param dataList
	 *            ������Ҫ�и������
	 * @return �зֵ�����
	 */
	protected static double[] calculateCutPointsByEqualFrequencyBinning(
			List<Long> dataList) {
		// �����з�����
		Collections.sort(dataList);
		// �����и������ָ���
		Integer dataCount = dataList.size();
		double freq = dataCount / UTILITY.BINS;
		double[] cutPoints = new double[UTILITY.BINS - 1];
		for (int i = 0; i < cutPoints.length; i++) {
			cutPoints[i] = Double.NEGATIVE_INFINITY;
		}
		// �����зֵ�
		double counter = 0, last = 0;
		int cpindex = 0, lastIndex = -1;
		for (int i = 0; i < dataList.size() - 1; i++) {
			counter++;
			dataCount--;
			// �Ƿ񵽴�Ǳ���зֵ�
			if (dataList.get(i) < dataList.get(i + 1)) {
				// �Ƿ�ﵽ����ָ���
				if (counter >= freq) {
					// ����Ǳ���зֵ��Ƿ����һ��Ǳ���зֵ����
					if (((freq - last) < (counter - freq)) && (lastIndex != -1)) {
						cutPoints[cpindex] = (dataList.get(lastIndex) + dataList
								.get(lastIndex + 1)) / 2;
						counter -= last;
						last = counter;
						lastIndex = i;
					} else {
						cutPoints[cpindex] = (dataList.get(i) + dataList
								.get(i + 1)) / 2;
						counter = 0;
						last = 0;
						lastIndex = -1;
					}
					cpindex++;
					freq = (dataCount + counter)
							/ ((cutPoints.length + 1) - cpindex);
				} else {
					lastIndex = i;
					last = counter;
				}
			}
		}
		// ����Ƿ�����һ���зֵ�
		if ((cpindex < cutPoints.length - 1) && (lastIndex != -1)) {
			cutPoints[cpindex] = (dataList.get(lastIndex) + dataList
					.get(lastIndex + 1)) / 2;
			cpindex++;
		}
		// �����зֵ����飬��������ֻ�����ҵ��ĵ� + �Ͻ��
		int arrLenth = 0;
		for (int i = 0; i < cutPoints.length; i++) {
			if (cutPoints[i] == Double.NEGATIVE_INFINITY)
				break;
			arrLenth++;
		}
		double[] finalCutPoints = new double[arrLenth + 1];
		for (int i = 0; i < cutPoints.length; i++) {
			if (cutPoints[i] == Double.NEGATIVE_INFINITY)
				break;
			finalCutPoints[i] = cutPoints[i];
		}
		finalCutPoints[arrLenth] = dataList.get(dataList.size() - 1) + 1;
		return finalCutPoints;
	}
}
