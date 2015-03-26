package Training;

import java.util.ArrayList;
import java.util.Random;

import EM.DataSetReader;
import Golobal.UTILITY;

public class MyEM {

	private static ArrayList<Integer[]> binVecList = new ArrayList<Integer[]>();
	String fileName = null;

	private static double[][] posteriorMatrix = new double[binVecList.size()][UTILITY.K];// p(k_j|x_i)�������
	private static double[][] likelihoodMatrix = new double[binVecList.size()][UTILITY.K];// p(x_i|k_j)��Ȼ����
	private static double[] priors = new double[UTILITY.K];// p(k_j)�������
	double[] count = new double[UTILITY.K];// count[k]Ϊ����x��p(k|x)�ܺͣ�
	private static double means[] = null;
	private static double varianceMatrix[] = null;
	private static double nK[] = null;
	private static boolean converged = false;// �Ƿ�������
	private static int step = 1;

	/** ��ʼ����Ϊ����E-Step M-Step��׼�� */
	public void initParameters(boolean variance) {

		// ��ʼ��likelihoodMatrix����--���� ��--���
		for (int i = 0; i < binVecList.size(); i++) {
			for (int j = 0; j < UTILITY.K; j++) {
				likelihoodMatrix[i][j] = 1.0 / (double) binVecList.size();
			}
		}
		// ��ʼ��priors
		for (int i = 0; i < UTILITY.K; i++) {
			priors[i] = 1.0 / (double) UTILITY.K;
		}
		iterate();
	}

	/** ����E-Step M-Step������ */
	public void iterate() {
		double prevLogLikeliHood = 0;
		double logLikeliHood = 0;
		do {
			EStep();// ����posteriorMatrix��������p(w_i|x_i)
			prevLogLikeliHood = calculateLogLikelyHood();
			MStep();
			logLikeliHood = calculateLogLikelyHood();
			step++;
		} while (!converged(logLikeliHood, prevLogLikeliHood));
		printData("Modified");
	}

	private void EStep() {
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

	public void MStep() {
		// ��count_k��count_k is the number of data points in the training dataset
		// which satisfy the predicate k.
		for (int j = 0; j < UTILITY.K; j++) {
			double count_k = 0.0;
			for (int i = 0; i < binVecList.size(); i++) {
				count_k += posteriorMatrix[i][j];
			}
			count[j] = count_k;
		}

		// ����p(k_j)
		double totalCount_k = 0.0;
		for (int j = 0; j < UTILITY.K; j++) {
			totalCount_k += priors[j];
		}
		for (int j = 0; j < UTILITY.K; j++) {
			priors[j] = priors[j] / totalCount_k;
		}

		// ����p(x_i|k_j),p(x_i|k_j)=p(x_di=v|k_j)�������x_i��dά�ģ�p(x_i|k_j)��d��p(x_i_d|k_j)�ĳ˻�

		for (int i = 0; i < binVecList.size(); i++) {
			for (int j = 0; j < UTILITY.K; j++) {
				updateLikelihood(i, j);
			}
		}
	}

	public void updateLikelihood(int i, int j) {
		double possibility = 1.0;
		for (int d = 0; d < UTILITY.DIMENSION; d++) {// dά����
			double count_x_v = 0.0;
			double count_k_x = 0.0;
			double count_xiv_k = 0.0;

			for (int v = 1; v <= UTILITY.Bins; v++) {// x_i = vѭ��
				int vCountI = 0;
				for (int xi = 0; xi < binVecList.size(); xi++) {// �������е�������ÿ��x_i=v����������sum(1{x_i=v})
					if (binVecList.get(xi)[d] == v)
						vCountI++;
				}
				for (int xi = 0; xi < binVecList.size(); xi++) {// �������е�������ÿ��x_i=v����������sum(1{x_i=v})
					count_k_x += posteriorMatrix[xi][j];
				}
				count_xiv_k += count_k_x * vCountI / count[j];
			}
			possibility *= count_xiv_k;
		}
		likelihoodMatrix[i][j] = possibility;
	}

	MyEM(String fileName) {
		this.fileName = fileName;
	}

	DataSetReader dsr = new DataSetReader();

	public void loadDataFromTheFile(String fileName) {
		binVecList = dsr.readDataFromTheFile(fileName);
	}

	public boolean converged(double logliklyhood, double previousLogLiklyHood) {
		double diff = 0.0;
		if (logliklyhood - previousLogLiklyHood < 0.001) {
			return true;
		}

		return false;
	}

	public double calculateLogLikelyHood() {
		double result = 0.0;
		for (int i = 0; i < binVecList.size(); i++) {
			double sum = 0.0;
			for (int j = 0; j < UTILITY.K; j++) {
				sum += priors[j]
						* gaussian(binVecList.get(i)[0], means[j],
								varianceMatrix[j]);
			}
			result += Math.log(sum);
		}
		return result;
	}

	private double gaussian(Integer xi, double mean, double var) {
		double gaussian = 0.0;
		gaussian = Math.exp(-((xi - mean) * (xi - mean)) / (2 * var));
		gaussian = gaussian
				/ (Math.sqrt(2 * Math.PI) * Math.sqrt(Math.abs(var)));
		return gaussian;
	}

	public void printData(String type) {
		System.out.println();
		System.out.println(type + " Mean Matrix");
		for (int i = 0; i < UTILITY.K; i++) {
			System.out.println(means[i]);
		}
		System.out.println();
		System.out.println(type + " Initial Varience Matrix");
		for (int i = 0; i < UTILITY.K; i++) {
			System.out.println(varianceMatrix[i]);
		}
		System.out.println();
		System.out.println(type + " Probability Totals Per Cluster:");
		for (int i = 0; i < UTILITY.K; i++) {
			System.out.println(priors[i]);
		}
	}

}
