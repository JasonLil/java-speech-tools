package speech.dynamic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import speech.FeatureVectorReader;
import uk.ac.bath.ai.util.Data;
import uk.ac.bath.ai.util.MyDimension;

import config.Config;

//import speech.ReadFeatureVectors;

public class WavTrainingPoolToDirectory {

	public List<TrainingData> trainingData;
	FeatureVectorReader reader;
	// ArrayList<String> names;

	HashSet<String> filt;

	WavTrainingPoolToDirectory(File rootSrc, File rootDst, double fractTrain,
			Config config, String words[]) throws IOException {
		// names=new ArrayList<String>();

		File trainDir = new File("/tmp/SPEECH/train");
		File testDir  = new File("/tmp/SPEECH/test");
		trainDir.mkdir();
		testDir.mkdir();

		reader = new FeatureVectorReader(config);
		trainingData = new ArrayList<TrainingData>();
		filt = new HashSet<String>();

		for (String s : words) {
			filt.add(s);
		}

		HashMap<String, List<File>> set = new HashMap<String, List<File>>();

		assert (rootSrc.isDirectory());

		// Create a map of the files against the key==WORD

		for (File dir : rootSrc.listFiles()) {

			// For each subdirectory
			if (dir.getName().startsWith("."))
				continue;
			assert (dir.isDirectory());
			for (File f : dir.listFiles()) {
				if (f.getName().startsWith("."))
					continue;

				String key = removeExtention(f.getName());
				if (filt == null || (!filt.contains(key)))
					continue;
				if (set.containsKey(key)) {
					set.get(key).add(f);
				} else {
					List<File> list = new ArrayList<File>();
					list.add(f);
					set.put(key, list);
				}
			}
		}

		int idTrain = 0;
		int idTest = 0;
		int outCount = 0;
		int nOutTot = set.keySet().size();

		MyDimension inDimension = null;
		MyDimension outDimension = new MyDimension(nOutTot, 1);

		for (String key : set.keySet()) {

			
			// For each name
			float[] target = new float[nOutTot];

			target[outCount] = 1.0f;
			int nFeature = 0;

			List<File> list = set.get(key);
			
			int nTrain=(int) (list.size()*fractTrain);
			int nTest=list.size()-nTrain;
			assert(nTrain > 0  && nTest> 0);
			
			int cntF=0;
			
			// names.add(key);
			for (File file : list) {
				// try {
				System.out.println(" Loading features: " + file.getPath());
				ArrayList<double[]> featSeq = reader.readVectors(file);
				Data data = new Data();

				// Copy features into array
				// pack as sequence of features
				int nslice = featSeq.size();
				if (nFeature == 0) {
					nFeature = nslice;
				} else {
					assert (nFeature == nslice);
				}
				int imageSize = config.getFeatureVectorSize() * nslice;

				if (inDimension == null) {
					inDimension = new MyDimension(
							config.getFeatureVectorSize(), nslice);
				}

				float feat[] = new float[imageSize];

				int cnt = 0;
				for (double[] fa : featSeq) {
					for (double f : fa) {
						feat[cnt++] = (float) f;
					}
				}
				assert (cnt == imageSize);

				data.image = feat;
				data.label = target;

				
				File fOut;
				
				if (cntF++ < nTrain){
					fOut = new File(trainDir, "train_" + (idTrain++)+".data");
				} else {
					fOut = new File(testDir, "test_" + (idTest++)+".data");
				}
				
				FileOutputStream fos = new FileOutputStream(fOut);
				ObjectOutputStream out = new ObjectOutputStream(fos);
				out.writeObject(data);
				out.close();
			}

			outCount++;
		}

		File filename = new File(trainDir, "dimensionIn.data");
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(inDimension);
		out.close();

		filename = new File(trainDir, "dimensionOut.data");
		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(outDimension);
		out.close();

		filename = new File(testDir, "dimensionIn.data");
		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(inDimension);
		out.close();

		filename = new File(testDir, "dimensionOut.data");
		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(outDimension);
		out.close();
		
	}

	public static String removeExtention(String name) {

		// Now we know it's a file - don't need to do any special hidden
		// checking or contains() checking because of:
		final int lastPeriodPos = name.lastIndexOf('.');
		if (lastPeriodPos == -1) {
			// No period after first character - return name as it was passed in
			return name;
		} else {
			// Remove the last period and everything after it
			return name.substring(0, lastPeriodPos);
		}
	}

}
