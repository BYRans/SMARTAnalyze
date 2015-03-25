package Golobal;

import java.io.File;
import java.io.IOException;

/** 全局文件夹路径变量 */
public final class UTILITY {

	/** EM算法 将数据分为k类 */
	public final static int K = 10;
	/** Binning划分bins数目 */
	public final static int Bins = 10;

	/** 健康磁盘静态数据 文件路径 */
	public final static String STATIC_HEALTH_PATH = "/home/pgxc/SMARTAnalyze/separateData/staticData/staticHealth.txt";

	/** 健康磁盘动态数据 文件路径 */
	public final static String DYNAMIC_HEALTH_PATH = "/home/pgxc/SMARTAnalyze/separateData/dynamicData/dynamicHealth.txt";

	/** 故障磁盘静态数据 文件路径 */
	public final static String STATIC_FAILURE_PATH = "/home/pgxc/SMARTAnalyze/separateData/staticData/staticFailure.txt";

	/** 故障磁盘静动态数据 文件路径 */
	public final static String DYNAMIC_FAILURE_PATH = "/home/pgxc/SMARTAnalyze/separateData/dynamicData/dynamicFailure.txt";

	/** 特征向量 文件路径 */
	public final static String FEATURE_VECTOR_PATH = "/home/pgxc/SMARTAnalyze/featureVector/featureVector.txt";

	/**
	 * 初始化文件 如果文件不存在，则创建该文件
	 * 
	 * @param path
	 *            文件目录
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
