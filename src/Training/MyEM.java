package Training;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import EM.DataSetReader;
import EM.EM;
import EM.Main;
import Golobal.UTILITY;

public class MyEM {
	private static ArrayList<Integer[]> binVecList = new ArrayList<Integer[]>();
	static {
		readBinVecData(UTILITY.BINNED_FEATURE_VECTOR_PATH);
	}
	private static double[][] posteriorMatrix = new double[binVecList.size()][UTILITY.K];// p(k_j|x_i)�������
	private static double[][] likelihoodMatrix = new double[binVecList.size()][UTILITY.K];// p(x_i|k_j)��Ȼ����
	private static double[][] p_xiv_k = new double[UTILITY.DIMENSION][UTILITY.K];// p_xiv_k��ʽ��3��ֵ��dάk��x=v����Ȼ����
	private static int[][] SUM_x_ki_xd = new int[UTILITY.DIMENSION][UTILITY.BINS];// I�����ݼ���xĳһά��ֵ����(v_i+1)������,v��ȡֵ��1��bins
	private static double[] priors = new double[UTILITY.K];// p(k_j)�������
	private static double[] count = new double[UTILITY.K];// count[k_i]Ϊ����x_iȡֵ��p(k_i|x_i)�ܺͣ�
	private static double error = 0.001;
	private static int step = 1;

	public static void main(String args[]) throws IOException {
		System.out.println("Running...");
		initParameters();
		iterate();
		savePosteriorMatrixAndPrior(UTILITY.BAYES_POSTERIOR_PATH,
				UTILITY.BAYES_PRIOR_PATH);
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
			priors[i] = Math.random();
		}
	}

	// /** ��ʼ����Ϊ����E-Step M-Step��׼�� */
	// public static void initParameters() {
	// System.out.println("init Parameters..");
	// // ��ʼ��likelihoodMatrix����--���� ��--���
	// for (int i = 0; i < binVecList.size(); i++) {
	// for (int j = 0; j < UTILITY.K; j++) {
	// likelihoodMatrix[i][j] = 1.0 / (double) binVecList.size();
	// }
	// }
	// // ��ʼ��priors
	// for (int i = 0; i < UTILITY.K; i++) {
	// priors[i] = 1.0 / (double) UTILITY.K;
	// }
	// }

	/** ����E-Step M-Step������ */
	public static void iterate() {
		double prevLogLikeliHood = 0;
		double logLikeliHood = 0;
		do {
			EStep();// ����posteriorMatrix��������p(w_i|x_i)
			prevLogLikeliHood = calculateLogLikelyHood();
			MStep();
			logLikeliHood = calculateLogLikelyHood();
			step++;
			System.out.print("Step " + step + "\t");
		} while (!converged(logLikeliHood, prevLogLikeliHood));
	}

	/** EM�㷨 E-Step */
	public static void EStep() {
		System.out.println("E-Steping...");
		// evidence֤�����ӣ���p(x)
		double evidence = 0.0;
		for (int i = 0; i < binVecList.size(); i++) {
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
		System.out.println("M-Steping...");
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
		double evidence = 0.0;
		for (int i = 0; i < binVecList.size(); i++) {
			for (int j = 0; j < UTILITY.K; j++) {
				double posterior = priors[j] * likelihoodMatrix[i][j];
				evidence = evidence + posterior;
			}
			result += Math.log(evidence);
		}
		return result;
	}

	/** ����Bayes��Ȼ���� */
	public static void updateLikelihood(int i, int j) {
		double possibility = 1.0;
		for (int d = 0; d < UTILITY.DIMENSION; d++) {// dά����
			double p_xi_k = 0.0;// v��p_xiv_k��
			double count_xiv_kj = 0.0;
			for (int v = 1; v <= UTILITY.BINS; v++) {// x_i = vѭ��
				for (int xi = 0; xi < binVecList.size(); xi++) {// �������е�����������x��P(k_j|x_i)
					if (binVecList.get(xi)[d] == v)// ָʾ����I����xd==vʱ�ӵ�sum��
						count_xiv_kj += posteriorMatrix[xi][j];
				}
				p_xi_k += count_xiv_kj / count[j];// v��p(xi=v|k)�ĺ;���p(xi|k)
			}
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
		System.out.println("change: "
				+ (Math.abs(logLikelihood - previousLogLikelihood)));
		if (Math.abs(logLikelihood - previousLogLikelihood) < error) {
			return true;
		}
		return false;
	}

	/**
	 * ����p(k_j|x_i)������ʺ�p(k_j)������ʣ���һ��Bayes��ʹ����Щ������
	 * 
	 * @param posteriorMatrixPath
	 *            Bayes������ʴ洢�ļ�·��
	 * @param priorPath
	 *            Bayes�������prior�洢�ļ�·��
	 * @throws IOException
	 * 
	 * */
	public static void savePosteriorMatrixAndPrior(String posteriorMatrixPath,
			String priorPath) throws IOException {
		// ������� д���ļ�
		UTILITY.INIT_FILE(UTILITY.BAYES_POSTERIOR_PATH);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					UTILITY.BAYES_POSTERIOR_PATH), false));
			for (double[] posterior : posteriorMatrix) {
				for (double item : posterior)
					writer.write(item + "\t");
				writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// ������� д���ļ�
		UTILITY.INIT_FILE(UTILITY.BAYES_PRIOR_PATH);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					UTILITY.BAYES_PRIOR_PATH), false));
			for (double prior : priors) {
				writer.write(prior + "\t");
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
