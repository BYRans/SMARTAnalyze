package NBEM_Model.training;

import NBEM_Model.golobal.UTILITY;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class EqualWidthBinning {

	public static void main(String[] args) throws IOException {
		System.out.println("Running...");
		List<String[]> vectorList = readVector(UTILITY.FEATURE_VECTOR_PATH);
		List<Integer[]> binVecList = new ArrayList<Integer[]>();
		List<double[]> cutPointsList = new ArrayList<double[]>();// ������binning�зֵ�
		int totalDyHealSnapNum = vectorList.size();

		int HealCount7 = 0;
		// int totalDynamicHealDataNum=0;
		int actTrainHealCount = 200000;

		Set<Integer> set = new TreeSet<Integer>();
		// HashMap <Integer,String[]> hash=new HashMap <Integer,String[]>();

		if (vectorList.size() == 0) {
			System.out.println("Feature vector dataset exception!");
			return;
		}
		for (int i = 0; i < vectorList.get(0).length; i++) {
			List<Long> dataList = new ArrayList<Long>();// ��Long�����Խ������
			for (int j = 0; j < vectorList.size(); j++) {
				dataList.add(Long.valueOf(vectorList.get(j)[i]));
			}
			double[] cutPoints = calculateCutPointsByEqualWidthBinning(dataList);
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

		/*
		 * for(int i=0;i<vectorList.size();i++){ hash.put(i, vectorList.get(i));
		 * }
		 */

		HealCount7 = (int) ((totalDyHealSnapNum / 10) * 7); // ��ѵ�����ݷֳ�7:3
		Random rdm = new Random(System.currentTimeMillis());

		while (set.size() != actTrainHealCount) { // ��[0,HealCount7)��Χ������actTrainHealCount�����ظ��������
			set.add(rdm.nextInt(HealCount7));
		}

		java.util.Iterator<Integer> it = set.iterator();

		int[] rand = new int[set.size()];
		int i = 0;
		while (it.hasNext()) {
			rand[i] = it.next();
			i++;
		}

		System.out.println("�������ݵ�������" + vectorList.size() + " ");
		System.out.println("7�ݽ������ݵĸ�����" + HealCount7 + " ");

		// binning���Vectorд���ļ�����ǰ7�������ȡѵ�����ݼ����ݣ�
		UTILITY.INIT_FILE(UTILITY.BINNED_FEATURE_VECTOR_PATH);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					UTILITY.BINNED_FEATURE_VECTOR_PATH), false));
			for (int j = 0; j < set.size(); j++) {
				for (Integer item : binVecList.get(rand[j]))
					writer.write(item + "\t");
				writer.newLine();
				writer.flush();
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// binning���Vectorд���ļ�(��������������������)
		UTILITY.INIT_FILE(UTILITY.BINNED_TEST_FEATURE_VECTOR_PATH);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					UTILITY.BINNED_TEST_FEATURE_VECTOR_PATH), false));
			for (int k = HealCount7; k < totalDyHealSnapNum; k++) {
				for (Integer item : binVecList.get(k))
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
	protected static double[] calculateCutPointsByEqualWidthBinning(
			List<Long> dataList) {
		// Scan for max and min values
		double max = 0, min = 1, currentVal;
		for (int i = 0; i < dataList.size(); i++) {
			currentVal = dataList.get(i);
			if (max < min) {
				max = min = currentVal;
			}
			if (currentVal > max) {
				max = currentVal;
			}
			if (currentVal < min) {
				min = currentVal;
			}
		}
		double binWidth = (max - min) / UTILITY.BINS;
		double[] cutPoints = null;
		if ((UTILITY.BINS > 1) && (binWidth > 0)) {
			cutPoints = new double[UTILITY.BINS];
			for (int i = 1; i < UTILITY.BINS; i++) {
				cutPoints[i - 1] = min + binWidth * i;
			}
		}
		cutPoints[UTILITY.BINS - 1] = max + 1;
		return cutPoints;
	}
}
