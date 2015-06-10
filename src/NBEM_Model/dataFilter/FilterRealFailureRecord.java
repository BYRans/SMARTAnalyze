package NBEM_Model.dataFilter;

import NBEM_Model.golobal.UTILITY;

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

public class FilterRealFailureRecord {
	public static SimpleDateFormat DATE_TEMPLATE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) throws ParseException, IOException {
		System.out.println("Running...");
		String dynamicPath = "/home/pgxc/SMARTAnalyze/separateData/dynamicData/dynamicFailure.txt";
		String staticPath = "/home/pgxc/SMARTAnalyze/separateData/staticData/staticFailure.txt";
		String dynOKStateFailurePath = "/home/pgxc/SMARTAnalyze/testSet/dynOKStateFailure.txt";
		String dynFailStateFailurePath = "/home/pgxc/SMARTAnalyze/testSet/dynFailStateFailurePath.txt";

		HashMap<String, Date> failureMap = new HashMap<String, Date>();
		HashMap<String, Date> OKStateMap = new HashMap<String, Date>();

		// 得到状态为故障的磁盘序列号+时间 和状态为健康的磁盘突然被换掉的磁盘序列号+最后出现时间 start--
		// try {
		// File termSetFile = new File(staticPath);
		// BufferedReader br = new BufferedReader(new InputStreamReader(
		// new FileInputStream(termSetFile), "UTF-8"));
		// String curLine = null;
		// String[] lineArr = null;
		// while ((curLine = br.readLine()) != null) {
		// lineArr = curLine.split("\t");
		// if (!"OK".equals(lineArr[lineArr.length - 1])) {
		// if (failureMap.get(lineArr[lineArr.length - 4]) == null
		// || DATE_TEMPLATE.parse(lineArr[3]).getTime() > failureMap
		// .get(lineArr[lineArr.length - 4]).getTime())
		// failureMap.put(lineArr[lineArr.length - 4],
		// DATE_TEMPLATE.parse(lineArr[3]));
		// } else {
		// if (OKStateMap.get(lineArr[lineArr.length - 4]) == null
		// || DATE_TEMPLATE.parse(lineArr[3]).getTime() > OKStateMap
		// .get(lineArr[lineArr.length - 4]).getTime()) {
		// OKStateMap.put(lineArr[lineArr.length - 4],
		// DATE_TEMPLATE.parse(lineArr[3]));
		// }
		// }
		// }
		// br.close();
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }

		try {
			File termSetFile = new File(dynamicPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if (OKStateMap.get(lineArr[lineArr.length - 1]) == null
						|| DATE_TEMPLATE.parse(lineArr[3]).getTime() > OKStateMap
								.get(lineArr[lineArr.length - 1]).getTime()) {
					OKStateMap.put(lineArr[lineArr.length - 1],
							DATE_TEMPLATE.parse(lineArr[3]));
				}
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Date lastDate = DATE_TEMPLATE.parse("2015-02-09 00:00:00");
		List<String> rmKey = new ArrayList<String>();
		for (Entry<String, Date> serTime : OKStateMap.entrySet()) {
			if (serTime.getValue().getTime() > lastDate.getTime())
				rmKey.add(serTime.getKey());
		}
		for (String rm : rmKey)
			OKStateMap.remove(rm);
		// 得到状态为故障的磁盘序列号+时间 和状态为健康的磁盘突然被换掉的磁盘序列号+最后出现时间 --end

		Calendar cal = Calendar.getInstance();
		for (Entry<String, Date> kv : OKStateMap.entrySet()) {
			System.out.print(kv.getKey() + ":\t" + kv.getValue() + "\t -->  ");
			cal.setTime(kv.getValue());
			cal.add(Calendar.HOUR_OF_DAY, -6);// 从该磁盘消失前6个小时的数据，作为故障磁盘数据
			kv.setValue(cal.getTime());
			System.out.println(kv.getValue());
		}

		List<String> okFailureList = new ArrayList<String>();
		List<String> falseFailureList = new ArrayList<String>();
		try {
			File termSetFile = new File(dynamicPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if (OKStateMap.containsKey(lineArr[lineArr.length - 1])) {
					if (DATE_TEMPLATE.parse(lineArr[3]).getTime() > OKStateMap
							.get(lineArr[lineArr.length - 1]).getTime())
						okFailureList.add(curLine);
				}
				// else if (failureMap.containsKey(lineArr[lineArr.length - 1]))
				// {
				// start = DATE_TEMPLATE.parse(failureMap
				// .get(lineArr[lineArr.length - 1]));
				// cal.setTime(start);
				// cal.add(Calendar.HOUR_OF_DAY, -3);
				// start = cal.getTime();
				// cal.add(Calendar.HOUR_OF_DAY, 3);
				// Date end = cal.getTime();
				// if (DATE_TEMPLATE.parse(lineArr[3]).getTime() > start
				// .getTime()
				// && end.getTime() > DATE_TEMPLATE.parse(lineArr[3])
				// .getTime())
				// falseFailureList.add(curLine);
				// }
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// 保存dynamic故障磁盘数据
		UTILITY.INIT_FILE(dynOKStateFailurePath);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					dynOKStateFailurePath), false));
			for (String record : okFailureList) {
				writer.write(record + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		UTILITY.INIT_FILE(dynFailStateFailurePath);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					dynFailStateFailurePath), false));
			for (String record : falseFailureList) {
				writer.write(record + "\r\n");
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Completed.");

	}
}
