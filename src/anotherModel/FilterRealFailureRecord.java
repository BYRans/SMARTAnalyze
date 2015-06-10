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

public class FilterRealFailureRecord {
	public static SimpleDateFormat DATE_TEMPLATE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) throws ParseException, IOException {
		System.out.println("Running...");
		int lastHours = 24;// 取last_update_time前多少个小时数据
		String infomationPath = "C:/Users/Administrator/Desktop/diskInfomation.txt";
		String failurePath = "D:/smart_data/smart_data_toshiba_bad_mbf2300r.txt";
		String realFailureRecordPath = "D:/smart_data/smart_data_toshiba_bad_mbf2300r_last24h.txt";

		HashMap<String, Date> diskLastTimeMap = new HashMap<String, Date>();
		HashMap<String, Date> failureDiskLastTimeMap = new HashMap<String, Date>();

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
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Calendar cal = Calendar.getInstance();
		for (Entry<String, Date> kv : failureDiskLastTimeMap.entrySet()) {
			System.out.print(kv.getKey() + ":\t" + kv.getValue() + "\t -->  ");
			cal.setTime(kv.getValue());
			cal.add(Calendar.HOUR_OF_DAY, -lastHours);// 从该磁盘消失前6个小时的数据，作为故障磁盘数据
			kv.setValue(cal.getTime());
			System.out.println(kv.getValue());
		}

		List<String> realFailureRecordList = new ArrayList<String>();
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
					realFailureRecordList.add(curLine);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					realFailureRecordPath), false));
			for (String record : realFailureRecordList) {
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
