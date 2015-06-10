package anotherModel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

public class LifeCycleJudge {
	public static SimpleDateFormat DATE_TEMPLATE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	static int winSize = 10;// 窗口大小，表示一个窗口的样本数
	static int moveStep = 1;// 步长
	static int anoFreq = 8;
	static String path = "D:/smart_data/allJudgePredictScore.txt";
	static String scoreFolderPath = "D:/smart_data/lifeCycleJudge/";

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		HashMap<String, List<String>> diskRecordMap = new HashMap<String, List<String>>();
		HashSet<String> failureDiskSet = new HashSet<String>();

		failureDiskSet.add("DAA0P820678B");
		failureDiskSet.add("DAA0P8206736");
		failureDiskSet.add("6SJ583T10000N243K7E1");
		failureDiskSet.add("3SD1V0V1000090145F8E");
		failureDiskSet.add("DAA0P8105WPE");
		failureDiskSet.add("EB00PC80CS5D");
		failureDiskSet.add("BJA0P8B053FU");
		failureDiskSet.add("DAA0P81064J7");
		failureDiskSet.add("D40XE42K");

		try {
			File termSetFile = new File(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				if (curLine.trim().equals(""))
					continue;
				curLine = curLine.replace("\"", "");
				lineArr = curLine.split("\t");
				if (diskRecordMap.containsKey(lineArr[2]))
					diskRecordMap.get(lineArr[2]).add(curLine);
				else {
					List<String> list = new ArrayList<String>();
					list.add(curLine);
					diskRecordMap.put(lineArr[2], list);
				}
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		int percent = 0;
		for (Entry<String, List<String>> kv : diskRecordMap.entrySet()) {
			System.out.println(++percent + "/" + diskRecordMap.size());
			// 按时间戳排序
			mergeSort(kv.getValue(), 0, 1);

			// 循环数组 求当前窗口异常数
			List<String> diskRecordList = kv.getValue();
			int[] cycleArray = new int[winSize];
			int anomlyCount = 0;
			int cyArrPoint = 0;
			int nextStepPoint = 0;
			// 参数初始化
			for (int i = 0; i < winSize; i++) {
				String[] tempStr = diskRecordList.get(i).split("\t");
				cycleArray[i] = Integer.parseInt(tempStr[0]);
				anomlyCount += cycleArray[i];
			}
			nextStepPoint = winSize;

			for (int i = winSize; i < diskRecordList.size(); i = i + moveStep) {
				if (anomlyCount >= anoFreq) {
					diskRecordList.set(i - 1, "! " + anomlyCount + "\t"
							+ diskRecordList.get(i - 1));
				} else {
					diskRecordList.set(i - 1, "  " + anomlyCount + "\t"
							+ diskRecordList.get(i - 1));
				}

				for (int j = 0; j < moveStep; j++) {
					String[] tempStr = diskRecordList.get(nextStepPoint++)
							.split("\t");
					anomlyCount += Integer.parseInt(tempStr[0]);
					anomlyCount -= cycleArray[cyArrPoint % winSize];
					cycleArray[cyArrPoint++ % winSize] = Integer
							.parseInt(tempStr[0]);
					if (nextStepPoint >= diskRecordList.size())
						break;
				}
			}

			diskRecordMap.put(kv.getKey(), diskRecordList);
		}

		String state = "";
		for (Entry<String, List<String>> kv : diskRecordMap.entrySet()) {

			if (failureDiskSet.contains(kv.getKey()))
				state = "failure/";
			else
				state = "health/";
			try {
				BufferedWriter writer = new BufferedWriter(
						new FileWriter(new File(scoreFolderPath + state
								+ kv.getKey() + ".txt"), false));
				writer.write(kv.getKey() + "\r\n");
				for (String str : kv.getValue()) {
					writer.write(str + "\r\n");
				}
				writer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param a
	 * @param s
	 * @param len
	 *            每次归并的有序集合的长度
	 * @throws ParseException
	 **/
	public static void mergeSort(List<String> list, int s, int len)
			throws ParseException {
		int size = list.size();
		int mid = size / (len << 1);
		int c = size & ((len << 1) - 1);
		// -------归并到只剩一个有序集合的时候结束算法-------//
		if (mid == 0)
			return;
		// ------进行一趟归并排序-------//
		for (int i = 0; i < mid; ++i) {
			s = i * 2 * len;
			merge(list, s, s + len, (len << 1) + s - 1);
		}
		// -------将剩下的数和倒数一个有序集合归并-------//
		if (c != 0)
			merge(list, size - c - 2 * len, size - c, size - 1);
		// -------递归执行下一趟归并排序------//
		mergeSort(list, 0, 2 * len);
	}

	private static void merge(List<String> list, int s, int m, int t)
			throws ParseException {
		List<String> tmpList = new ArrayList<String>();
		int i = s, j = m;
		while (i < m && j <= t) {
			Date dateI = DATE_TEMPLATE.parse(list.get(i).split("\t")[1]);
			Date dateJ = DATE_TEMPLATE.parse(list.get(j).split("\t")[1]);
			if (dateI.getTime() <= dateJ.getTime()) {
				tmpList.add(list.get(i));
				i++;
			} else {
				tmpList.add(list.get(j));
				j++;
			}
		}
		while (i < m) {
			tmpList.add(list.get(i));
			i++;
		}

		while (j <= t) {
			tmpList.add(list.get(j));
			j++;
		}
		for (int c = 0; c < tmpList.size(); c++) {
			list.set(s + c, tmpList.get(c));
		}
	}
}
