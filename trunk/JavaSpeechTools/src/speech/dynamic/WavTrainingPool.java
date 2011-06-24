package speech.dynamic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import config.Config;

import speech.ReadFeatureVectors;

public class WavTrainingPool {
	private int nOut;
	public List<TrainingData> trainingData;
	ReadFeatureVectors reader;
	ArrayList<String> names;

	WavTrainingPool(File root, Config config) {
		names=new ArrayList<String>();
		
		reader = new ReadFeatureVectors(config.getFeatureVectorSize(),
				config.getFFTSize());
		trainingData = new ArrayList<TrainingData>();

		HashMap<String, List<File>> set = new HashMap<String, List<File>>();

		assert (root.isDirectory());
		for (File dir : root.listFiles()) {
			if (dir.getName().startsWith("."))
				continue;
			assert (dir.isDirectory());
			for (File f : dir.listFiles()) {
				if (f.getName().startsWith("."))
					continue;

				String key = f.getName();
				if (set.containsKey(key)) {
					set.get(key).add(f);
				} else {
					List<File> list = new ArrayList<File>();
					list.add(f);
					set.put(key, list);
				}
			}
		}

		nOut = 0;
		for (String key : set.keySet()) {
			List<File> list = set.get(key);
			names.add(key);
			for (File file : list) {
				try {
					System.out.println(" Loading features: " + file.getPath());
					TrainingData td = new TrainingData(file, nOut, reader);
					trainingData.add(td);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			nOut++;
		}
	}

	public int nTarget() {
		return nOut;
	}

}
