package Training;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Golobal.UTILITY;

public class NaiveBayes {
	private static double[][] posteriorMatrix = null;// p(k_j|x_i)后验概率
	private static double[] priors = new double[UTILITY.K];// p(k_j)先验概率

	public static void main(String[] args) {

		readPosteriorMatrixAndPrior(UTILITY.BAYES_POSTERIOR_PATH,
				UTILITY.BAYES_PRIOR_PATH);
		List<Integer[]> testSetList = readTestSet(UTILITY.TEST_SET_PATH);
		// double[] predict = new double[UTILITY.K];
		for (Integer[] testRecord : testSetList) {
			naiveBayes(testRecord);
		}

	}

	public static void naiveBayes(Integer[] vector) {
		double[] prediction = new double[UTILITY.K];
		double p_xd_kj = 1.0;
		double evidence = 0.0;
		for (int j = 0; j < UTILITY.K; j++) {
			for (int d = 0; d < vector.length; d++) {
				double p_xi_k = 0.0;// v个p_xiv_k和
				double count_xiv_kj = 0.0;
				for (int xi = 0; xi < posteriorMatrix.length; xi++) {// 遍历所有的数据求所有x的P(k_j|x_i)
					if (binVecList.get(xi)[d] == v)// 指示函数I，当xd==v时加到sum里
						count_xiv_kj += posteriorMatrix[xi][j];
				}
				p_xi_k += count_xiv_kj / priors[j];
				p_xd_kj *= p_xi_k;// d个p(xd|k)相乘
			}
			double posterior = p_xd_kj * priors[j];
			evidence = evidence + posterior;
			prediction[j] = posterior;
		}
		for (int j = 0; j < UTILITY.K; j++) {
			prediction[j] = prediction[j] / evidence;
		}
		for (double posterior : prediction) {
			System.out.print(posterior + "\t");
		}
		System.out.println();
	}

	/** 初始化由EM算法得到的 p(k_j|x_i)后验概率 和 // p(k_j)先验概率 */
	public static void readPosteriorMatrixAndPrior(String posteriorMatrixPath,
			String priorPath) {
		List<double[]> posteriorList = new ArrayList<double[]>();
		// 读入PosteriorMatrix
		try {
			File file = new File(UTILITY.BAYES_POSTERIOR_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				if ("".equals(curLine.trim()))
					continue;
				lineArr = curLine.split("\t");
				double[] intArr = new double[lineArr.length];
				for (int i = 0; i < lineArr.length; i++)
					intArr[i] = Double.valueOf(lineArr[i]);
				posteriorList.add(intArr);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		posteriorMatrix = new double[posteriorList.size()][UTILITY.K];
		for (int i = 0; i < posteriorList.size(); i++) {
			posteriorMatrix[i] = posteriorList.get(i);
		}

		// 读入priors
		try {
			File file = new File(UTILITY.BAYES_PRIOR_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = curLine.trim().split("\t");
			for (int i = 0; i < lineArr.length; i++)
				priors[i] = Double.valueOf(lineArr[i]);
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
			File file = new File(UTILITY.TEST_SET_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				if ("".equals(curLine.trim()))
					continue;
				lineArr = curLine.trim().split("\t");
				Integer[] intArr = new Integer[lineArr.length];
				for (int i = 0; i < lineArr.length; i++)
					intArr[i] = Integer.valueOf(lineArr[i]);
				testSetList.add(intArr);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return testSetList;
	}
}
