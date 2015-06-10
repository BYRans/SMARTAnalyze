package NBEM_Model.golobal;

import java.io.File;
import java.io.IOException;

/** ȫ���ļ���·������ */
public final class UTILITY {

	/** EM�㷨 �����ݷ�Ϊk�� */
	public final static int K = 100;
	/** Binning����bins��Ŀ */
	public final static int BINS = 100;
	/** ������� */
	public final static double ERROR = 0.001;

	/** ѵ�����ݸ��� */
	public static double TRAIN_DATA_COUNT = 0;

	/** ��ʼ��ѵ�����ݸ��� */
	public static void SET_FEATURE_VECTOR_TrainDataCount(int count) {
		UTILITY.TRAIN_DATA_COUNT = count;
	}

	/** ��������ά�� D */
	public static int DIMENSION = 12;

	/** ��ʼ����������ά������D */
	public static void SET_FEATURE_VECTOR_dimension(int count) {
		UTILITY.DIMENSION = count;
	}

	/** �������̾�̬���� �ļ�·�� */
	public final static String STATIC_HEALTH_PATH = "/home/pgxc/SMARTAnalyze/separateData/staticData/staticHealth.txt";

	/** �������̶�̬����-ѵ������ �ļ�·�� */
	public final static String DYNAMIC_HEALTH_PATH = "/home/pgxc/SMARTAnalyze/separateData/dynamicData/dynamicHealth.txt";

	/** ���ϴ��̾�̬���� �ļ�·�� */
	public final static String STATIC_FAILURE_PATH = "/home/pgxc/SMARTAnalyze/separateData/staticData/staticFailure.txt";

	/** ���ϴ��̾���̬���� �ļ�·�� */
	public final static String DYNAMIC_FAILURE_PATH = "/home/pgxc/SMARTAnalyze/separateData/dynamicData/dynamicFailure.txt";

	/** �������� �ļ�·�� */
	public final static String FEATURE_VECTOR_PATH = "/home/pgxc/SMARTAnalyze/featureVector/featureVector.txt";

	/** binning�зֵ� �ļ�·�� */
	public final static String CUT_POINTS_PATH = "/home/pgxc/SMARTAnalyze/featureVector/cutPoints.txt";

	/** binning���������� �ļ�·�� */
	public final static String BINNED_FEATURE_VECTOR_PATH = "/home/pgxc/SMARTAnalyze/featureVector/binnedFeatureVector.txt";

	/** binning���������� �ļ�·�� */
	public final static String BINNED_TEST_FEATURE_VECTOR_PATH = "/home/pgxc/SMARTAnalyze/featureVector/OKTestSet.txt";

	/** Bayes��Ȼ����likelihood �洢�ļ�·�� */
	public final static String BAYES_POSTERIOR_PATH = "/home/pgxc/SMARTAnalyze/Bayes/posterior.txt";

	/** Bayes�������prior �洢�ļ�·�� */
	public final static String BAYES_PRIOR_PATH = "/home/pgxc/SMARTAnalyze/Bayes/prior.txt";

	/** Bayes count(xi=v��k) ��ά����[d][v][k] �洢�ļ�·�� */
	public final static String BAYES_COUNT_Xd_V_K = "/home/pgxc/SMARTAnalyze/Bayes/countDVK.txt";

	/** Bayes count(k) �洢�ļ�·�� */
	public final static String BAYES_COUNT_K = "/home/pgxc/SMARTAnalyze/Bayes/countK.txt";

	/** ���Լ� �洢�ļ�·�� */
	public final static String TEST_SET_PATH = "/home/pgxc/SMARTAnalyze/testSet/testSet.txt";

	/** δ�����ԭʼ�������� �洢�ļ�·�� */
	public final static String RAW_TEST_SET_PATH = "/home/pgxc/SMARTAnalyze/testSet/dynOKStateFailure.txt";
//	public final static String RAW_TEST_SET_PATH = "/home/pgxc/SMARTAnalyze/testSet/dynFailStateFailurePath.txt";

	/**
	 * ��ʼ���ļ� ����ļ������ڣ��򴴽����ļ�
	 * 
	 * @param path
	 *            �ļ�Ŀ¼
	 * @throws IOException
	 */
	public static void INIT_FILE(String path) throws IOException {
		File f = new File(path);
		File pf = f.getParentFile();
		if (!pf.exists()) {
			pf.mkdirs();
		}
		if (!f.exists()) {
			f.createNewFile();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
