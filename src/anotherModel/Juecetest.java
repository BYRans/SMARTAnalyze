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
	static int winSize = 10;// ���ڴ�С����ʾһ�����ڵ�������
	static int moveStep = 1;// ����
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

		int flag = 0;// 0��ʾ����
		healRate = computeFinalResult(lisheal, flag); // ���㽡��������
		System.out.println("��������Ԥ����Ϊ��" + (1 - healRate[0]));
		System.out.println("������Ԥ����Ϊ��" + (1 - healRate[1]));
		System.out.println("--------------------------------");

		System.out.println("��������Ϊ�����̺ţ�");
		for (String serNO : wrongJudgeHealth) {
			System.out.println(serNO);
		}

		int flag1 = 1;// 1��ʾ����
		failRate = computeFinalResult(lisfail, flag1); // �������Ԥ����
		System.out.println("��������Ԥ����Ϊ��" + failRate[0]);
		System.out.println("������Ԥ����Ϊ��" + failRate[1]);
		long lastTime = System.currentTimeMillis();

		 System.out.println(lastTime - firstTime);
	}

	public static double[] computeFinalResult(List<String> lis, int flag)
			throws IOException {

		long firstTime = System.currentTimeMillis();
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int thinkFailSample = 0;
		int SampleSum = 0; // ����������
		int failDiskNum = 0; // ģ�ͼ����Ĺ�������
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
		/* ��ʼ�������̿����������� */
		int finalLabel[] = new int[map.size()];
		for (int i = 0; i < map.size(); i++) {
			finalLabel[i] = 0;
		}

		long midTime = System.currentTimeMillis();
		System.out.println(midTime - firstTime);
		String[] tempStr = null;
		int percent = 0;

		/* ���û������ڻ����ж����ǹ��ϻ��ǽ��� */
		for (Map.Entry<String, Integer> m : map.entrySet()) {
			if (++percent % 100 == 0) {
				System.out.println(percent + "/" + map.size());
			}
			int[] cycleArray = new int[winSize];
			int anomlyCount = 0;
			int cyArrPoint = 0;
			int nextStepPoint = 0;
			// ������ʼ��
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
			System.out.println("����������/������Ϊ������������" + SampleSum + "/"
					+ thinkFailSample);
			System.out.println("����������/������Ϊ���ϴ�������" + map.size() + "/"
					+ failDiskNum);
		} else {
			System.out.println("����������/������Ϊ������������" + SampleSum + "/"
					+ thinkFailSample);
			System.out.println("���ϴ�����/������Ϊ���ϴ�������" + map.size() + "/"
					+ failDiskNum);
		}
		return rate;

	}
}
