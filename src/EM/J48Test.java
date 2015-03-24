package EM;  
  
/** 
 * desc:试试Weka的决策树类 
 * <code>J48Test</code> 
 * @version 1.0 2011/12/13 
 * @author chenwq 
 * 
 */  
import java.io.BufferedReader;
import java.io.File;  
import java.io.FileInputStream;
import java.io.IOException;  
  
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import weka.classifiers.Classifier;  
import weka.classifiers.trees.J48;  
import weka.core.Instances;  
import weka.core.converters.ArffLoader;  
  
public class J48Test {  
  
	/**
	 * Set cutpoints for a single attribute.
	 *
	 * @param index
	 *            the index of the attribute to set cutpoints for
	 */
	protected void calculateCutPointsByEqualFrequencyBinning(int index) {
		// Copy data so that it can be sorted
		List<Integer> data = new ArrayList<Integer>();
		List<String> rawData = new ArrayList<String>();
		
		
		String realFailDynPath = "C:/Users/Administrator/Desktop/SMARTAnalyze/separateData/dynamicData/realFailureDynData.txt";
		try {
			File termSetFile = new File(realFailDynPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(termSetFile), "UTF-8"));
			String curLine = br.readLine();
			String[] lineArr = null;
			while ((curLine = br.readLine()) != null) {
				lineArr = curLine.split("\t");
				String tmp = "";
				for (int i = 4; i < lineArr.length; i++)
					tmp += lineArr[i] + "\t";
				failList.add(tmp);
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		
		// Sort input data
		Collections.sort(data);
		// Compute weight of instances without missing values
		double sumOfWeights = 0;
		for (int i = 0; i < data.numInstances(); i++) {
			if (data.instance(i).isMissing(index)) {
				break;
			} else {
				sumOfWeights += data.instance(i).weight();
			}
		}
		double freq;
		double[] cutPoints = new double[m_NumBins - 1];
		if (getDesiredWeightOfInstancesPerInterval() > 0) {
			freq = getDesiredWeightOfInstancesPerInterval();
			cutPoints = new double[(int) (sumOfWeights / freq)];
		} else {
			freq = sumOfWeights / m_NumBins;
			cutPoints = new double[m_NumBins - 1];
		}
		// Compute break points
		double counter = 0, last = 0;
		int cpindex = 0, lastIndex = -1;
		for (int i = 0; i < data.numInstances() - 1; i++) {
			// Stop if value missing
			if (data.instance(i).isMissing(index)) {
				break;
			}
			counter += data.instance(i).weight();
			sumOfWeights -= data.instance(i).weight();
			// Do we have a potential breakpoint?
			if (data.instance(i).value(index) < data.instance(i + 1).value(
					index)) {
				// Have we passed the ideal size?
				if (counter >= freq) {
					// Is this break point worse than the last one?
					if (((freq - last) < (counter - freq)) && (lastIndex != -1)) {
						cutPoints[cpindex] = (data.instance(lastIndex).value(
								index) + data.instance(lastIndex + 1).value(
								index)) / 2;
						counter -= last;
						last = counter;
						lastIndex = i;
					} else {
						cutPoints[cpindex] = (data.instance(i).value(index) + data
								.instance(i + 1).value(index)) / 2;
						counter = 0;
						last = 0;
						lastIndex = -1;
					}
					cpindex++;
					freq = (sumOfWeights + counter)
							/ ((cutPoints.length + 1) - cpindex);
				} else {
					lastIndex = i;
					last = counter;
				}
			}
		}
		// Check whether there was another possibility for a cut point
		if ((cpindex < cutPoints.length) && (lastIndex != -1)) {
			cutPoints[cpindex] = (data.instance(lastIndex).value(index) + data
					.instance(lastIndex + 1).value(index)) / 2;
			cpindex++;
		}
		// Did we find any cutpoints?
		if (cpindex == 0) {
			m_CutPoints[index] = null;
		} else {
			double[] cp = new double[cpindex];
			for (int i = 0; i < cpindex; i++) {
				cp[i] = cutPoints[i];
			}
			m_CutPoints[index] = cp;
		}
	}
}  