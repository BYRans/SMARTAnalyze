package NBEM_Model.training;

import NBEM_Model.golobal.UTILITY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NaiveBayes {
	private static double[] priors = new double[UTILITY.K];// p(k_j)先验概率
	private static double[][][] COUNT_XdV_K = new double[UTILITY.DIMENSION][UTILITY.BINS][UTILITY.K];// count_xdv_ki,公式3的分子
	private static double[] count = new double[UTILITY.K];// count[k_i]为所有x_i取值的p(k_i|x_i)总和
	private static double totalCount_k = 0.0;// count[k]总和
	static {
		int trainDataCount = 0;
		try {
			File file = new File(UTILITY.BINNED_FEATURE_VECTOR_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = null;
			while ((curLine = br.readLine()) != null) {
				trainDataCount++;
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		UTILITY.SET_FEATURE_VECTOR_TrainDataCount(trainDataCount);
	}

	public static void main(String[] args) {
		List<double[]> predictList = new ArrayList<double[]>();
		readPosteriorMatrixAndPrior(UTILITY.BAYES_COUNT_Xd_V_K,
				UTILITY.BAYES_COUNT_K);
		String testSetPath = UTILITY.TEST_SET_PATH;
		if (args != null && args.length == 1)
			testSetPath = args[0];
		List<Integer[]> testSetList = readTestSet(testSetPath);
		initPriorsAndTotalCountK();
		System.out.println(testSetList.size());
		for (Integer[] testRecord : testSetList) {
			double[] prediction = naiveBayes(testRecord);
			double[] result = new double[2];
			for (int i = 0; i < prediction.length; i++) {
				if (result[1] < prediction[i]) {
					result[0] = i;
					result[1] = prediction[i];
				}
			}
			predictList.add(result);
		}
		for (double[] record : predictList)
			System.out.println(((int) record[0]) + "\t" + record[1]);

	}

	/**
	 * 朴素贝叶斯 计算输入向量的各类别后验概率
	 * 
	 * @param vector
	 *            特征向量
	 * @return double[] prediction,即P(k|x)
	 * */
	public static double[] naiveBayes(Integer[] vector) {
		double[] prediction = new double[UTILITY.K];
		double evidence = 0.0;
		for (int j = 0; j < UTILITY.K; j++) {
			double p_x_k = 1.0;
			for (int d = 0; d < vector.length; d++) {// d维p(xi=v|k)成绩得到P(x|k)
				p_x_k *= (COUNT_XdV_K[d][vector[d] - 1][j] + 1 / UTILITY.TRAIN_DATA_COUNT)
						/ (count[j] + UTILITY.BINS / UTILITY.TRAIN_DATA_COUNT);// [vector[d]-1]是特征向量Xd处的值-1，v=Bin-1
			}
			double posterior = p_x_k * priors[j];
			evidence = evidence + posterior;
			prediction[j] = posterior;
		}
		for (int j = 0; j < UTILITY.K; j++) {
			prediction[j] = prediction[j] / evidence;
		}
		return prediction;
	}

	/**
	 * 计算Bayes先验概率priors[] 和 count(k)的和totalCount_k
	 */
	public static void initPriorsAndTotalCountK() {
		for (int j = 0; j < UTILITY.K; j++) {
			totalCount_k += count[j];
		}
		for (int j = 0; j < UTILITY.K; j++) {
			priors[j] = count[j] / totalCount_k;
		}

	}

	/**
	 * 初始化由EM算法得到的count_xdv_ki(公式3的分子) 和 p(k_j)先验概率
	 * 
	 * @param countDVKPath
	 *            count_xdv_ki(公式3的分子)存储文件路径
	 * @param countKPath
	 *            p(k_j)先验概率存储文件路径
	 * */
	public static void readPosteriorMatrixAndPrior(String countDVKPath,
			String countKPath) {
		List<double[]> countDVKList = new ArrayList<double[]>();
		// 读入PosteriorMatrix
		try {
			File file = new File(countDVKPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				if ("".equals(curLine.trim()))
					continue;
				lineArr = curLine.split("\t");
				double[] doubleArr = new double[lineArr.length];
				for (int i = 0; i < lineArr.length; i++)
					doubleArr[i] = Double.valueOf(lineArr[i]);
				countDVKList.add(doubleArr);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (int d = 0; d < UTILITY.DIMENSION; d++) {
			for (int v = 0; v < UTILITY.BINS; v++) {
				COUNT_XdV_K[d][v] = countDVKList.get(d * UTILITY.BINS + v);
			}
		}

		// 读入count(k)
		try {
			File file = new File(countKPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = curLine.trim().split("\t");
			for (int i = 0; i < lineArr.length; i++)
				count[i] = Double.valueOf(lineArr[i]);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 读入测试集
	 * 
	 * @param testSetPath
	 *            测试数据集路径
	 * @return 测试数据集List
	 */
	public static List<Integer[]> readTestSet(String testSetPath) {
		List<Integer[]> testSetList = new ArrayList<Integer[]>();
		// 读入PosteriorMatrix
		try {
			File file = new File(testSetPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			int count = 0;
			while ((curLine = br.readLine()) != null) {
				if ("".equals(curLine.trim()))
					continue;
				lineArr = curLine.trim().split("\t");
				Integer[] intArr = new Integer[lineArr.length];
				for (int i = 0; i < lineArr.length; i++) {
					intArr[i] = Integer.valueOf(lineArr[i]);
				}
				testSetList.add(intArr);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return testSetList;
	}
}
