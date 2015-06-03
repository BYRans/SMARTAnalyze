import golobal.UTILITY;

import java.io.IOException;
import training.EqualFrequencyBinning;
import training.EM;
import training.NaiveBayes;
import verification.TestDataProcess;

public class OneKeyRunning {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		EqualFrequencyBinning.main(null);
		EM.main(null);
		TestDataProcess.main(null);
		System.out.println("===========================");
		System.out.println("failure test set");
		NaiveBayes.main(null);
		System.out.println("\n===========================");
		System.out.println("health test set");
		String[] path = { UTILITY.BINNED_TEST_FEATURE_VECTOR_PATH };
		NaiveBayes.main(path);
		System.out.println("Completed." + "\n" + "process time:"
				+ (System.currentTimeMillis() - startTime) / 1000 + "S\n\n");
	}

}
