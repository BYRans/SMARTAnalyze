package Golobal;

import java.io.File;
import java.io.IOException;

/** ȫ���ļ���·������ */
public final class UTILITY {

	/** EM�㷨 �����ݷ�Ϊk�� */
	public final static int K = 10;
	/** Binning����bins��Ŀ */
	public final static int Bins = 10;

	/** �������̾�̬���� �ļ�·�� */
	public final static String STATIC_HEALTH_PATH = "/home/pgxc/SMARTAnalyze/separateData/staticData/staticHealth.txt";

	/** �������̶�̬���� �ļ�·�� */
	public final static String DYNAMIC_HEALTH_PATH = "/home/pgxc/SMARTAnalyze/separateData/dynamicData/dynamicHealth.txt";

	/** ���ϴ��̾�̬���� �ļ�·�� */
	public final static String STATIC_FAILURE_PATH = "/home/pgxc/SMARTAnalyze/separateData/staticData/staticFailure.txt";

	/** ���ϴ��̾���̬���� �ļ�·�� */
	public final static String DYNAMIC_FAILURE_PATH = "/home/pgxc/SMARTAnalyze/separateData/dynamicData/dynamicFailure.txt";

	/** �������� �ļ�·�� */
	public final static String FEATURE_VECTOR_PATH = "/home/pgxc/SMARTAnalyze/featureVector/featureVector.txt";

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
