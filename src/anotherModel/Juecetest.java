package anotherModel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Juecetest {
	static int winSize = 10;// 窗口大小，表示一个窗口的样本数
	static int moveStep = 1;// 步长
	static int anoFreq = 6;
	static HashSet<String> wrongJudgeHealth = new HashSet<String>();

	public static void main(String[] args) throws IOException {

		long firstTime = System.currentTimeMillis();
		List<String> lisheal = new ArrayList<String>();
		List<String> lisfail = new ArrayList<String>();
		String path = "D:/smart_data/smartTestSetWithSerNO_last24h.txt";
		try {
			File termSetFile = new File(path);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = null;
			String lineArr[] = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				if (Integer.parseInt(lineArr[1]) == 1) {
					lisfail.add(curLine);
				} else {
					lisheal.add(curLine);
				}
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		double healRate[] = new double[2];
		double failRate[] = new double[2];

		int flag = 0;// 0表示健康
		healRate = computeFinalResult(lisheal, flag); // 计算健康误判率
		System.out.println("健康样本预测率为：" + (1 - healRate[0]));
		System.out.println("健康盘预测率为：" + (1 - healRate[1]));
		System.out.println("--------------------------------");

		System.out.println("健康误判为故障盘号：");
		for (String serNO : wrongJudgeHealth) {
			System.out.println(serNO);
		}

		int flag1 = 1;// 1表示故障
		failRate = computeFinalResult(lisfail, flag1); // 计算故障预测率
		System.out.println("故障样本预测率为：" + failRate[0]);
		System.out.println("故障盘预测率为：" + failRate[1]);
		long lastTime = System.currentTimeMillis();

		 System.out.println(lastTime - firstTime);
	}

	public static double[] computeFinalResult(List<String> lis, int flag)
			throws IOException {

		long firstTime = System.currentTimeMillis();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int thinkFailSample = 0;
		int SampleSum = 0; // 样本点总数
		int failDiskNum = 0; // 模型检测出的故障盘数
		int groupnum = 0;
		double rate[] = new double[2];

		String lineArr[] = null;
		for (int i = 0; i < lis.size(); i++) {
			lineArr = lis.get(i).split("\t");
			SampleSum++;
			if (!map.containsKey(lineArr[lineArr.length - 1])) {
				map.put(lineArr[lineArr.length - 1], groupnum);
				groupnum++;
			}
			if (Integer.parseInt(lineArr[0]) == 1) {
				thinkFailSample++;
			}
		}

		List<LinkedList<String>> groupList = new LinkedList<LinkedList<String>>();
		for (int i = 0; i < map.size(); i++) {
			LinkedList<String> link = new LinkedList<String>();
			groupList.add(link);
		}
		String str = null;
		for (int i = 0; i < lis.size(); i++) {
			lineArr = lis.get(i).split("\t");
			str = lineArr[2] + "\t" + lineArr[0];
			if (map.containsKey(lineArr[lineArr.length - 1])) {
				groupList.get(map.get(lineArr[lineArr.length - 1])).add(str);
			}
		}
		/* 初始化所有盘块所属的类标号 */
		int finalLabel[] = new int[map.size()];
		for (int i = 0; i < map.size(); i++) {
			finalLabel[i] = 0;
		}

		long midTime = System.currentTimeMillis();
		System.out.println(midTime - firstTime);
		String[] tempStr = null;
		int percent = 0;

		/* 采用滑动窗口机制判断盘是故障还是健康 */
		for (Map.Entry<String, Integer> m : map.entrySet()) {
			if (++percent % 100 == 0) {
				System.out.println(percent + "/" + map.size());
			}
			int[] cycleArray = new int[winSize];
			int anomlyCount = 0;
			int cyArrPoint = 0;
			int nextStepPoint = 0;
			// 参数初始化
			for (int i = 0; i < winSize; i++) {
				tempStr = ((String) groupList.get(m.getValue()).get(i))
						.split("\t");
				cycleArray[i] = Integer.parseInt(tempStr[1]);
				anomlyCount += cycleArray[i];
			}
			nextStepPoint = winSize;

			for (int i = winSize; i < groupList.get(m.getValue()).size(); i = i
					+ moveStep) {
				if (anomlyCount >= anoFreq) {
					finalLabel[m.getValue()] = 1;
					wrongJudgeHealth.add(m.getKey());
					break;
				}

				for (int j = 0; j < moveStep; j++) {
					tempStr = ((String) groupList.get(m.getValue()).get(
							nextStepPoint++)).split("\t");
					anomlyCount += Integer.parseInt(tempStr[1]);
					anomlyCount -= cycleArray[cyArrPoint % winSize];
					cycleArray[cyArrPoint++ % winSize] = Integer
							.parseInt(tempStr[1]);
					if (nextStepPoint >= groupList.get(m.getValue()).size())
						break;
				}
			}
		}

		for (int i = 0; i < map.size(); i++) {
			if (finalLabel[i] == 1) {
				failDiskNum++;
			}
		}
		rate[0] = (double) thinkFailSample / SampleSum;
		rate[1] = (double) failDiskNum / map.size();

		if (flag == 0) {
			System.out.println("健康样本数/健康判为故障样本数：" + SampleSum + "/"
					+ thinkFailSample);
			System.out.println("健康磁盘数/健康判为故障磁盘数：" + map.size() + "/"
					+ failDiskNum);
		} else {
			System.out.println("故障样本数/故障判为故障样本数：" + SampleSum + "/"
					+ thinkFailSample);
			System.out.println("故障磁盘数/故障判为故障磁盘数：" + map.size() + "/"
					+ failDiskNum);
		}
		return rate;

	}
}
