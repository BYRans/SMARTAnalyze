package decisionTreeModel;
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

public class PreHoldoutValidation {
	
	public static void main(String[] args) throws ParseException, IOException {
		System.out.println("Running...");
		//读入总的故障磁盘数据
		String realFailureRecordPath = "D:/smart_data/smart_data_section_bad_last24h.txt";
		//按7:3比例写入trainingSet和testSet两个文件
		String failTrainSetPath = "D:/smart_data/smart_data_section_failTrainSet_last24h.txt";
		String failTestSetPath = "D:/smart_data/smart_data_section_failTestSet_last24h.txt";
		HashMap<String, List<String>> diskRecordMap = new HashMap<String, List<String>>();
		
		//读入总的故障磁盘数据
		try {
			File termSetFile = new File(realFailureRecordPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = null;
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				if (curLine.trim().equals(""))
					continue;
				lineArr = curLine.split("\t");
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

		//70%写入trainingSet
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					failTrainSetPath), false));
			for (Entry<String, List<String>> kv : diskRecordMap.entrySet()) {
				System.out.println(kv.getValue().size());
				for (int i = 0; i < (int)(kv.getValue().size() * 0.7); i++) {
					writer.write(kv.getValue().get(i) + "\r\n");
				}
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//30%写入testSet
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					failTestSetPath), false));
			for (Entry<String, List<String>> kv : diskRecordMap.entrySet()) {
				for (int i = (int)(kv.getValue().size() * 0.7); i < kv.getValue().size(); i++) {
					writer.write(kv.getValue().get(i) + "\r\n");
				}
				writer.flush();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Completed.");

	}
}
