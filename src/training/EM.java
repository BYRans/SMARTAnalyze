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

	private static double[][] posteriorMatrix = new double[binVecList.size()][UTILITY.K];// p(k_j|x_i)�������
	private static double[][] likelihoodMatrix = new double[binVecList.size()][UTILITY.K];// p(x_i|k_j)��Ȼ����
	private static double[][][] COUNT_XdV_K = new double[UTILITY.DIMENSION][UTILITY.BINS][UTILITY.K];// count_xdv_ki,��ʽ3�ķ���
	private static double[] priors = new double[UTILITY.K];// p(k_j)�������
	private static double[] count = new double[UTILITY.K];// count[k_i]Ϊ����x_iȡֵ��p(k_i|x_i)�ܺͣ�

	public static void main(String args[]) throws IOException {
		System.out.println("Running...");
		initParameters();
		iterate();
		savePosteriorMatrixAndPrior(UTILITY.BAYES_COUNT_Xd_V_K,
				UTILITY.BAYES_COUNT_K);
		System.out.println("Finished.");
	}

	/** ��ʼ����Ϊ����E-Step M-Step��׼�� */
	public static void initParameters() {// ���ʣ���ʼ����Ҫ��ʼ����Щֵ����likelihood��prior�𣿣�����
		System.out.println("init Parameters..");
		// ��ʼ��likelihoodMatrix����--���� ��--���
		for (int i = 0; i < binVecList.size(); i++) {
			for (int j = 0; j < UTILITY.K; j++) {
				likelihoodMatrix[i][j] = Math.random();// ���ʣ��������ֵ����ʼ���𣿣����ñ�֤���Ϊ1����������
			}
		}
		// ��ʼ��priors
		for (int i = 0; i < UTILITY.K; i++) {
			// priors[i] = Math.random();//�����ʼ���������
			priors[i] = 1.0 / UTILITY.K;// ƽ����ʼ���������
		}
	}

	/** ����E-Step M-Step������ */
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
			EStep();// ����posteriorMatrix��������p(w_i|x_i)
			prevLogLikeliHood = calculateLogLikelyHood();
			MStep();
			logLikeliHood = calculateLogLikelyHood();
			System.out.println("using " + (System.currentTimeMillis() - start)
					/ 1000 + "S");
		} while (!converged(logLikeliHood, prevLogLikeliHood));
	}

	/** EM�㷨 E-Step */
	public static void EStep() {
		System.out.println("\nE-Stepping...");
		// evidence֤�����ӣ���p(x)
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

	/** EM�㷨 M-Step */
	public static void MStep() {
		System.out.println("M-Stepping...");
		// ��count_k��count_k is the number of data points in the training dataset
		// which satisfy the predicate k.
		System.out.println("updating count_k...");
		for (int j = 0; j < UTILITY.K; j++) {
			double count_k = 0.0;
			for (int i = 0; i < binVecList.size(); i++) {
				count_k += posteriorMatrix[i][j];
			}
			count[j] = count_k;
		}

		// ����p(k_j)
		System.out.println("updating p(k_j)...");
		double totalCount_k = 0.0;
		for (int j = 0; j < UTILITY.K; j++) {
			totalCount_k += count[j];
		}
		for (int j = 0; j < UTILITY.K; j++) {
			priors[j] = count[j] / totalCount_k;
		}

		/*
		 * // ����COUNT_XdV_K,��ʽ3�ķ���
		 * System.out.println("updating COUNT_XdV_K[][][]..."); double
		 * count_xdv_kj = 0.0; for (int j = 0; j < UTILITY.K; j++) {// k for
		 * (int d = 0; d < UTILITY.DIMENSION; d++) {// dά���� for (int v = 1; v <=
		 * UTILITY.BINS; v++) {// x_i = vѭ�� count_xdv_kj = 0.0; for (int xi = 0;
		 * xi < binVecList.size(); xi++) {// �������е�����������x��P(k_j|x_i) if
		 * (binVecList.get(xi)[d] == v)// ָʾ����I����xd==vʱ�ӵ�sum�� count_xdv_kj +=
		 * posteriorMatrix[xi][j]; } COUNT_XdV_K[d][v - 1][j] = count_xdv_kj; }
		 * } }
		 */
		/** 2015/6/3 �����޸� ����ʱ�临�Ӷ� */
		// ����COUNT_XdV_K,��ʽ3�ķ���
		System.out.println("updating COUNT_XdV_K[][][]...");
		for (int j = 0; j < UTILITY.K; j++) {// k
			for (int d = 0; d < UTILITY.DIMENSION; d++) {// dά����
				// count_xdv_kj��һ�����飬�洢B��count_xdv_k,����ֻ��Ҫ����һ��binVecList����ο������ɲο�����ע�͵��Ĵ��롣
				double[] count_xdv_kj = new double[UTILITY.BINS];
				for (int xi = 0; xi < binVecList.size(); xi++) {// �������е�����������x��P(k_j|x_i)
					// posteriorMatrixֵ�ֱ�B�ֱ���ͣ��洢��count_xdv_kj�����С�binVecList��ÿһάֵ����B���ڡ�����������ͬʱ����������sum(P(k|x)*I(x_i=v))
					count_xdv_kj[binVecList.get(xi)[d] - 1] += posteriorMatrix[xi][j];
				}
				for (int v = 0; v < UTILITY.BINS; v++)
					COUNT_XdV_K[d][v][j] = count_xdv_kj[v];// count_xdv_kj�����о���B��count_xdv_k
			}
		}

		// ����p(x_i|k_j),p(x_i|k_j)=p(x_di=v|k_j)�������x_i��dά�ģ�p(x_i|k_j)��d��p(x_i_d|k_j)�ĳ˻�
		System.out.println("updating p(x_i|k_j)...");
		for (int i = 0; i < binVecList.size(); i++) {
			for (int j = 0; j < UTILITY.K; j++) {
				updateLikelihood(i, j);
			}
		}
	}

	/** EM�㷨 ����logLikelyhood -- sum(logp(x)) */
	public static double calculateLogLikelyHood() {
		double result = 0.0;
		// evidence֤�����ӣ���p(x)
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
	 * ����Bayes��Ȼ����
	 * */
	public static void updateLikelihood(int i, int j) {
		double possibility = 1.0;
		for (int d = 0; d < UTILITY.DIMENSION; d++) {// dά����
			double p_xi_k = 0.0;// v��p_xiv_k��
			p_xi_k = (COUNT_XdV_K[d][binVecList.get(i)[d] - 1][j] + 1 / UTILITY.TRAIN_DATA_COUNT)
					/ (count[j] + UTILITY.BINS / UTILITY.TRAIN_DATA_COUNT);// p(xi=v|k)�ĺ;���p(xi|k)
			possibility *= p_xi_k;// d��p(xd|k)���
		}
		likelihoodMatrix[i][j] = possibility;
	}

	/**
	 * �ж��Ƿ�����
	 * 
	 * @param logLikelihood
	 *            E-Step�������logLikelihood
	 * @param previousLogLikelihood
	 *            M-Step�������logLikelihood
	 * @return boolean true������ falseδ����
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
	 * ����count(xi=v��k)����ά����[d][v][k]�� �� count(k)����һ��Bayes��ʹ����Щ������
	 * 
	 * @param countDVKPath
	 *            count(xi=v��k) ��ά����[d][v][k] �洢�ļ�·��
	 * @param countKPath
	 *            count(k) �洢�ļ�·��
	 * @throws IOException
	 * 
	 * */
	public static void savePosteriorMatrixAndPrior(String countDVKPath,
			String countKPath) throws IOException {
		// count(xi=v��k) ��ά����[d][v][k]
		UTILITY.INIT_FILE(countDVKPath);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					countDVKPath), false));
			for (int d = 0; d < UTILITY.DIMENSION; d++) {// ����d
				for (int v = 0; v < UTILITY.BINS; v++) {// ����v
					for (int k = 0; k < UTILITY.K; k++) {// ����k
						writer.write(COUNT_XdV_K[d][v][k] + "\t");// d�飺v�� k��
					}
					writer.newLine();
					writer.flush();
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// count(k) д���ļ�
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
	 * ��ѵ������
	 * 
	 * @param path
	 *            ѵ�������ļ�·��
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
