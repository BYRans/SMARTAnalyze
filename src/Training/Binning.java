package Training;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Golobal.UTILITY;

public class Binning {

	public static void main(String[] args) {
		System.out.println("Running...");
		List<String[]> vectorList = readVector(UTILITY.FEATURE_VECTOR_PATH);
		List<double[]> cutPointsList = new ArrayList<double[]>();// 各特征binning切分点
		if (vectorList.size() == 0) {
			System.out.println("Feature vector dataset exception!");
			return;
		}
		for (int i = 0; i < vectorList.get(0).length; i++) {
			List<Integer> dataList = new ArrayList<Integer>();
			for (int j = 0; j < vectorList.size(); j++) {
				dataList.add(Integer.valueOf(vectorList.get(j)[i]));
			}
			double[] cutPoints = calculateCutPointsByEqualFrequencyBinning(dataList);
			cutPointsList.add(cutPoints);
		}

	}

	/**
	 * 读feature Vector
	 * 
	 * @return List<String[]>
	 * */
	public static List<String[]> readVector(String path) {
		List<String[]> list = new ArrayList<String[]>();
		try {
			File file = new File(UTILITY.FEATURE_VECTOR_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = br.readLine();
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
	 * 计算一个特征的切割点
	 *
	 * @param dataList
	 *            输入需要切割的数据
	 * @return 切割点数组
	 */
	protected static double[] calculateCutPointsByEqualFrequencyBinning(
			List<Integer> dataList) {
		// 排序切分数据
		Collections.sort(dataList);
		// 计算切割的理想分割数
		double dataCount = dataList.size();
		double freq = dataCount / UTILITY.Bins;
		double[] cutPoints = new double[UTILITY.Bins - 1];
		// 计算分割点
		double counter = 0, last = 0;
		int cpindex = 0, lastIndex = -1;
		for (int i = 0; i < dataList.size() - 1; i++) {
			counter++;
			dataCount--;
			// 是否到达潜在分割点
			if (dataList.get(i) < dataList.get(i + 1)) {
				// 是否达到理想分割数
				if (counter >= freq) {
					// 当下潜在切割点是否比上一个潜在切割点更差
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
		// 检查是否还有另一个切割点
		if ((cpindex < cutPoints.length) && (lastIndex != -1)) {
			cutPoints[cpindex] = (dataList.get(lastIndex) + dataList
					.get(lastIndex + 1)) / 2;
			cpindex++;
		}

		// 如果一个分割点都没找到
		if (cpindex == 0) {
			cutPoints[0] = dataList.get(dataList.size() - 1) + 1;
		}
		return cutPoints;
	}
}
