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

public class EM {
	private static ArrayList<Integer[]> binVecList = new ArrayList<Integer[]>();
	static {
		readBinVecData(UTILITY.BINNED_FEATURE_VECTOR_PATH);
		UTILITY.SET_FEATURE_VECTOR_TrainDataCount(binVecList.size());
	}

	private static double[][] posteriorMatrix = new double[binVecList.size()][UTILITY.K];// p(k_j|x_i)后验概率
	private static double[][] likelihoodMatrix = new double[binVecList.size()][UTILITY.K];// p(x_i|k_j)似然函数
	private static double[][][] COUNT_XdV_K = new double[UTILITY.DIMENSION][UTILITY.BINS][UTILITY.K];// count_xdv_ki,公式3的分子
	private static double[] priors = new double[UTILITY.K];// p(k_j)先验概率
	private static double[] count = new double[UTILITY.K];// count[k_i]为所有x_i取值的p(k_i|x_i)总和，

	public static void main(String args[]) throws IOException {
		System.out.println("Running...");
		initParameters();
		iterate();
		savePosteriorMatrixAndPrior(UTILITY.BAYES_COUNT_Xd_V_K,
				UTILITY.BAYES_COUNT_K);
		System.out.println("Finished.");
	}

	/** 初始化，为进行E-Step M-Step做准备 */
	public static void initParameters() {// 疑问：初始化需要初始化哪些值？？likelihood和prior吗？？？？
		System.out.println("init Parameters..");
		// 初始化likelihoodMatrix，行--样本 列--类别。
		for (int i = 0; i < binVecList.size(); i++) {
			for (int j = 0; j < UTILITY.K; j++) {
				likelihoodMatrix[i][j] = Math.random();// 疑问：是用随机值来初始化吗？？不用保证其和为1？？？？？
			}
		}
		// 初始化priors
		for (int i = 0; i < UTILITY.K; i++) {
			// priors[i] = Math.random();//随机初始化先验概率
			priors[i] = 1.0 / UTILITY.K;// 平均初始化先验概率
		}
	}

	/** 迭代E-Step M-Step至收敛 */
	public static void iterate() {
		int step = 0;
		double prevLogLikeliHood = 0;
		double logLikeliHood = 0;
		do {
			long start = System.currentTimeMillis();
			System.out.println("Step " + ++step + "\t");
			System.out.print("priors: ");
			for (double prior : priors)
				System.out.print(prior + "\t");
			EStep();// 更新posteriorMatrix，即更新p(w_i|x_i)
			prevLogLikeliHood = calculateLogLikelyHood();
			MStep();
			logLikeliHood = calculateLogLikelyHood();
			System.out.println("using " + (System.currentTimeMillis() - start)
					/ 1000 + "S");
		} while (!converged(logLikeliHood, prevLogLikeliHood));
	}

	/** EM算法 E-Step */
	public static void EStep() {
		System.out.println("\nE-Stepping...");
		// evidence证据因子，即p(x)
		for (int i = 0; i < binVecList.size(); i++) {
			double evidence = 0.0;
			for (int j = 0; j < UTILITY.K; j++) {
				double posterior = priors[j] * likelihoodMatrix[i][j];
				evidence = evidence + posterior;
				posteriorMatrix[i][j] = posterior;
			}
			for (int j = 0; j < UTILITY.K; j++) {
				posteriorMatrix[i][j] = posteriorMatrix[i][j] / evidence;
			}
		}
	}

	/** EM算法 M-Step */
	public static void MStep() {
		System.out.println("M-Stepping...");
		// 求count_k。count_k is the number of data points in the training dataset
		// which satisfy the predicate k.
		System.out.println("updating count_k...");
		for (int j = 0; j < UTILITY.K; j++) {
			double count_k = 0.0;
			for (int i = 0; i < binVecList.size(); i++) {
				count_k += posteriorMatrix[i][j];
			}
			count[j] = count_k;
		}

		// 更新p(k_j)
		System.out.println("updating p(k_j)...");
		double totalCount_k = 0.0;
		for (int j = 0; j < UTILITY.K; j++) {
			totalCount_k += count[j];
		}
		for (int j = 0; j < UTILITY.K; j++) {
			priors[j] = count[j] / totalCount_k;
		}

		/*
		 * // 更新COUNT_XdV_K,公式3的分子
		 * System.out.println("updating COUNT_XdV_K[][][]..."); double
		 * count_xdv_kj = 0.0; for (int j = 0; j < UTILITY.K; j++) {// k for
		 * (int d = 0; d < UTILITY.DIMENSION; d++) {// d维向量 for (int v = 1; v <=
		 * UTILITY.BINS; v++) {// x_i = v循环 count_xdv_kj = 0.0; for (int xi = 0;
		 * xi < binVecList.size(); xi++) {// 遍历所有的数据求所有x的P(k_j|x_i) if
		 * (binVecList.get(xi)[d] == v)// 指示函数I，当xd==v时加到sum里 count_xdv_kj +=
		 * posteriorMatrix[xi][j]; } COUNT_XdV_K[d][v - 1][j] = count_xdv_kj; }
		 * } }
		 */
		/** 2015/6/3 丁煜修改 降低时间复杂度 */
		// 更新COUNT_XdV_K,公式3的分子
		System.out.println("updating COUNT_XdV_K[][][]...");
		for (int j = 0; j < UTILITY.K; j++) {// k
			for (int d = 0; d < UTILITY.DIMENSION; d++) {// d维向量
				// count_xdv_kj是一个数组，存储B个count_xdv_k,这样只需要遍历一遍binVecList，这段看不懂可参考上面注释掉的代码。
				double[] count_xdv_kj = new double[UTILITY.BINS];
				for (int xi = 0; xi < binVecList.size(); xi++) {// 遍历所有的数据求所有x的P(k_j|x_i)
					// posteriorMatrix值分别按B分别求和，存储到count_xdv_kj数组中。binVecList中每一维值都在B以内。在这里是在同时计算论文中sum(P(k|x)*I(x_i=v))
					count_xdv_kj[binVecList.get(xi)[d] - 1] += posteriorMatrix[xi][j];
				}
				for (int v = 0; v < UTILITY.BINS; v++)
					COUNT_XdV_K[d][v][j] = count_xdv_kj[v];// count_xdv_kj数组中就是B个count_xdv_k
			}
		}

		// 更新p(x_i|k_j),p(x_i|k_j)=p(x_di=v|k_j)。这里的x_i是d维的，p(x_i|k_j)是d个p(x_i_d|k_j)的乘积
		System.out.println("updating p(x_i|k_j)...");
		for (int i = 0; i < binVecList.size(); i++) {
			for (int j = 0; j < UTILITY.K; j++) {
				updateLikelihood(i, j);
			}
		}
	}

	/** EM算法 计算logLikelyhood -- sum(logp(x)) */
	public static double calculateLogLikelyHood() {
		double result = 0.0;
		// evidence证据因子，即p(x)
		for (int i = 0; i < binVecList.size(); i++) {
			double evidence = 0.0;
			for (int j = 0; j < UTILITY.K; j++) {
				double posterior = priors[j] * likelihoodMatrix[i][j];
				evidence = evidence + posterior;
			}
			result += Math.log(evidence);
		}
		return result;
	}

	/**
	 * 更新Bayes似然函数
	 * */
	public static void updateLikelihood(int i, int j) {
		double possibility = 1.0;
		for (int d = 0; d < UTILITY.DIMENSION; d++) {// d维向量
			double p_xi_k = 0.0;// v个p_xiv_k和
			p_xi_k = (COUNT_XdV_K[d][binVecList.get(i)[d] - 1][j] + 1 / UTILITY.TRAIN_DATA_COUNT)
					/ (count[j] + UTILITY.BINS / UTILITY.TRAIN_DATA_COUNT);// p(xi=v|k)的和就是p(xi|k)
			possibility *= p_xi_k;// d个p(xd|k)相乘
		}
		likelihoodMatrix[i][j] = possibility;
	}

	/**
	 * 判断是否收敛
	 * 
	 * @param logLikelihood
	 *            E-Step结束后的logLikelihood
	 * @param previousLogLikelihood
	 *            M-Step结束后的logLikelihood
	 * @return boolean true已收敛 false未收敛
	 * */
	public static boolean converged(double logLikelihood,
			double previousLogLikelihood) {
		System.out.println("change: " + (logLikelihood - previousLogLikelihood)
				+ "\n");
		if (Math.abs(logLikelihood - previousLogLikelihood) < UTILITY.ERROR) {
			return true;
		}
		return false;
	}

	/**
	 * 保存count(xi=v∧k)（三维数组[d][v][k]） 和 count(k)，下一步Bayes将使用这些参数。
	 * 
	 * @param countDVKPath
	 *            count(xi=v∧k) 三维数组[d][v][k] 存储文件路径
	 * @param countKPath
	 *            count(k) 存储文件路径
	 * @throws IOException
	 * 
	 * */
	public static void savePosteriorMatrixAndPrior(String countDVKPath,
			String countKPath) throws IOException {
		// count(xi=v∧k) 三维数组[d][v][k]
		UTILITY.INIT_FILE(countDVKPath);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					countDVKPath), false));
			for (int d = 0; d < UTILITY.DIMENSION; d++) {// 迭代d
				for (int v = 0; v < UTILITY.BINS; v++) {// 迭代v
					for (int k = 0; k < UTILITY.K; k++) {// 迭代k
						writer.write(COUNT_XdV_K[d][v][k] + "\t");// d块：v行 k列
					}
					writer.newLine();
					writer.flush();
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// count(k) 写入文件
		UTILITY.INIT_FILE(countKPath);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					countKPath), false));
			for (double countK : count) {
				writer.write(countK + "\t");
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读训练数据
	 * 
	 * @param path
	 *            训练数据文件路径
	 * */
	public static void readBinVecData(String path) {
		try {
			File file = new File(UTILITY.BINNED_FEATURE_VECTOR_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				if ("".equals(curLine.trim()))
					continue;
				lineArr = curLine.split("\t");
				Integer[] intArr = new Integer[lineArr.length];
				for (int i = 0; i < lineArr.length; i++)
					intArr[i] = Integer.valueOf(lineArr[i]);
				binVecList.add(intArr);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
