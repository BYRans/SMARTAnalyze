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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

public class New_FilterRealFailureRecord {
	public static SimpleDateFormat DATE_TEMPLATE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) throws ParseException, IOException {
		System.out.println("Running...");
		int lastHours = 24;// ȡlast_update_timeǰ���ٸ�Сʱ����
		String infomationPath = "C:/Users/Administrator/Desktop/diskInfomation.txt";
		String failurePath = "D:/smart_data/smart_data_section_bad.txt";
		String realFailureRecordPath = "D:/smart_data/smart_data_section_bad_last24h.txt";

		HashMap<String, Date> diskLastTimeMap = new HashMap<String, Date>();
		HashMap<String, Date> failureDiskLastTimeMap = new HashMap<String, Date>();
		HashMap<String, List<String>> diskRecordMap = new HashMap<String, List<String>>();
		HashSet<String> realFailureRecordSet = new HashSet<String>();

		try {
			File termSetFile = new File(infomationPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				diskLastTimeMap.put(lineArr[0],
						DATE_TEMPLATE.parse(lineArr[lineArr.length - 1]));
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			File termSetFile = new File(failurePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				failureDiskLastTimeMap.put(lineArr[1],
						diskLastTimeMap.get(lineArr[1]));

				if (diskRecordMap.containsKey(lineArr[1]))
					diskRecordMap.get(lineArr[1]).add(curLine);
				else {
					List<String> list = new ArrayList<String>();
					list.add(curLine);
					diskRecordMap.put(lineArr[1], list);
				}

			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Calendar cal = Calendar.getInstance();
		for (Entry<String, Date> kv : failureDiskLastTimeMap.entrySet()) {
			System.out.print(kv.getKey() + ":\t" + kv.getValue() + "\t -->  ");
			cal.setTime(kv.getValue());
			cal.add(Calendar.HOUR_OF_DAY, -lastHours);// �Ӹô�����ʧǰ6��Сʱ�����ݣ���Ϊ���ϴ�������
			kv.setValue(cal.getTime());
			System.out.println(kv.getValue());
		}

		try {
			File termSetFile = new File(failurePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if (DATE_TEMPLATE.parse(lineArr[2]).getTime() > failureDiskLastTimeMap
						.get(lineArr[1]).getTime())
					realFailureRecordSet.add(curLine.trim());
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (Entry<String, List<String>> kv : diskRecordMap.entrySet()) {
			mergeSort(kv.getValue(), 0, 1);
			for (int i = kv.getValue().size(); i > kv.getValue().size()
					- lastHours * 2; i--) {
				realFailureRecordSet.add(kv.getValue().get(i - 1));
			}
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					realFailureRecordPath), false));
			for (String record : realFailureRecordSet) {
				writer.write(record + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Completed.");

	}

	/**
	 * @param a
	 * @param s
	 * @param len
	 *            ÿ�ι鲢�����򼯺ϵĳ���
	 * @throws ParseException
	 **/
	public static void mergeSort(List<String> list, int s, int len)
			throws ParseException {
		int size = list.size();
		int mid = size / (len << 1);
		int c = size & ((len << 1) - 1);
		// -------�鲢��ֻʣһ�����򼯺ϵ�ʱ������㷨-------//
		if (mid == 0)
			return;
		// ------����һ�˹鲢����-------//
		for (int i = 0; i < mid; ++i) {
			s = i * 2 * len;
			merge(list, s, s + len, (len << 1) + s - 1);
		}
		// -------��ʣ�µ����͵���һ�����򼯺Ϲ鲢-------//
		if (c != 0)
			merge(list, size - c - 2 * len, size - c, size - 1);
		// -------�ݹ�ִ����һ�˹鲢����------//
		mergeSort(list, 0, 2 * len);
	}

	private static void merge(List<String> list, int s, int m, int t)
			throws ParseException {
		List<String> tmpList = new ArrayList<String>();
		int i = s, j = m;
		while (i < m && j <= t) {
			Date dateI = DATE_TEMPLATE.parse(list.get(i).split("\t")[2]);
			Date dateJ = DATE_TEMPLATE.parse(list.get(j).split("\t")[2]);
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
