package EM;
import java.util.ArrayList;
import java.util.Random;

public class ModifiedEM {

	ArrayList<Double> clusterData = new ArrayList<Double>();
	String fileName = null;

	private static double means[] = null;
	private static double prior[] = null;// p(w_i|x)
	private static double varianceMatrix[] = null;
	private static double nK[] = null;
	private static double[][] weightMatrix = null;
	private static int clusters = 0; // 数据划分为几类
	private static boolean converged = false;// 是否已收敛
	private static int step = 1;
	private static double covariance = 0.0;// 协方差
	private static double mean = 0.0;
	private static boolean variance = false;

	ModifiedEM(String fileName) {
		this.fileName = fileName;
	}

	DataSetReader dsr = new DataSetReader();

	public void loadDataFromTheFile(String fileName) {
		clusterData = dsr.readDataFromTheFile(fileName);
	}

	public void setNumOfClusters(int cluster) {
		this.clusters = cluster;
		means = new double[cluster];
		prior = new double[cluster];
		varianceMatrix = new double[cluster];
		weightMatrix = new double[clusterData.size()][cluster];
	}

	public void init() {

		double prevLogLikeliHood = 0;
		double logLikeliHood = 0;

		do {
			EStep();
			prevLogLikeliHood = calculateLogLikelyHood();
			MStep();
			logLikeliHood = calculateLogLikelyHood();
			step++;
		} while (!converged(logLikeliHood, prevLogLikeliHood));
		printData("Modified");
	}

	public boolean converged(double logliklyhood, double previousLogLiklyHood) {
		double diff = 0.0;
		if (logliklyhood - previousLogLiklyHood < 0.001) {
			return true;
		}

		return false;
	}

	public void MStep() {
		nK = new double[clusters];
		for (int j = 0; j < clusters; j++) {
			for (int i = 0; i < clusterData.size(); i++) {
				nK[j] += weightMatrix[i][j];
			}
		}
		for (int j = 0; j < clusters; j++) {
			prior[j] = nK[j] / (double) clusterData.size();
		}

		for (int j = 0; j < clusters; j++) {
			double sum = 0.0;
			for (int i = 0; i < clusterData.size(); i++) {
				sum += clusterData.get(i) * weightMatrix[i][j];
			}
			means[j] = (sum / nK[j]);
		}

		for (int j = 0; j < clusters; j++) {
			double sum = 0.0;
			for (int i = 0; i < clusterData.size(); i++) {
				sum += weightMatrix[i][j] * (clusterData.get(i) - means[j])
						* (clusterData.get(i) - means[j]);
			}
			varianceMatrix[j] = (sum / nK[j]);
		}
	}

	private void EStep() {
		for (int i = 0; i < clusterData.size(); i++) {
			double denom = 0.0;
			for (int j = 0; j < clusters; j++) {
				double weight = prior[j]
						* gaussian(clusterData.get(i), means[j],
								varianceMatrix[j]);
				denom = denom + weight;
				weightMatrix[i][j] = weight;
			}
			for (int j = 0; j < clusters; j++) {
				weightMatrix[i][j] = weightMatrix[i][j] / denom;
			}
		}
	}

	public double calculateLogLikelyHood() {
		double result = 0.0;
		for (int i = 0; i < clusterData.size(); i++) {
			double sum = 0.0;
			for (int j = 0; j < clusters; j++) {
				sum += prior[j]
						* gaussian(clusterData.get(i), means[j],
								varianceMatrix[j]);
			}
			result += Math.log(sum);
		}
		return result;
	}

	private double gaussian(double xi, double mean, double var) {
		double gaussian = 0.0;
		gaussian = Math.exp(-((xi - mean) * (xi - mean)) / (2 * var));
		gaussian = gaussian
				/ (Math.sqrt(2 * Math.PI) * Math.sqrt(Math.abs(var)));
		return gaussian;
	}

	public void initParameters(boolean variance) {
		
		
		
		
		
		
		
		
		
		
		
		
		for (int i = 0; i < clusters; i++) {
			means[i] = clusterData
					.get(new Random().nextInt(clusterData.size()));
			prior[i] = 1.0 / (double) clusters;
		}
		for (int i = 0; i < clusterData.size(); i++) {
			mean += clusterData.get(i);
		}
		mean = mean / (double) clusterData.size();
		for (int i = 0; i < clusterData.size(); i++) {
			covariance += (clusterData.get(i) - mean)
					* (clusterData.get(i) - mean);
		}
		covariance = covariance / (double) clusterData.size();
		if (variance) {
			for (int i = 0; i < clusters; i++) {
				varianceMatrix[i] = 1.0;
			}
		} else {
			for (int i = 0; i < clusters; i++) {
				varianceMatrix[i] = covariance * (1.0 + Math.random());
			}
		}
		printData("Initial");
		init();
	}

	public void printData(String type) {
		System.out.println();
		System.out.println(type + " Mean Matrix");
		for (int i = 0; i < clusters; i++) {
			System.out.println(means[i]);
		}
		System.out.println();
		System.out.println(type + " Initial Varience Matrix");
		for (int i = 0; i < clusters; i++) {
			System.out.println(varianceMatrix[i]);
		}
		System.out.println();
		System.out.println(type + " Probability Totals Per Cluster:");
		for (int i = 0; i < clusters; i++) {
			System.out.println(prior[i]);
		}
	}

}
