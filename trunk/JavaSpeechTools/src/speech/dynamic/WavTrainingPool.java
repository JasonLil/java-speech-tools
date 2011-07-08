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
	public double target[][];
	HashSet<String> filt;
	
	WavTrainingPool(File root, Config config) {
		names=new ArrayList<String>();
		
		reader = new ReadFeatureVectors(config);
		trainingData = new ArrayList<TrainingData>();
		filt=new HashSet<String>();
		
		filt.add("Red");
		filt.add("Bed");
		filt.add("silence");
		
		HashMap<String, List<File>> set = new HashMap<String, List<File>>();

		assert (root.isDirectory());
		for (File dir : root.listFiles()) {
			if (dir.getName().startsWith("."))
				continue;
			assert (dir.isDirectory());
			for (File f : dir.listFiles()) {
				if (f.getName().startsWith("."))
					continue;

				String key = removeExtention(f.getName());
				if (filt == null || (! filt.contains(key))) continue;
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
		

		 target = new double[nOut][];

		for (int i = 0; i < nOut; i++) {
			target[i] = new double[nOut];
			target[i][i] = 1.0;
		}

	}

	public int nTarget() {
		return nOut;
	}
	
	public static String removeExtention(String name) {
	   

	    // Now we know it's a file - don't need to do any special hidden
	    // checking or contains() checking because of:
	    final int lastPeriodPos = name.lastIndexOf('.');
	    if (lastPeriodPos == -1)
	    {
	        // No period after first character - return name as it was passed in
	        return name;
	    }
	    else
	    {
	        // Remove the last period and everything after it
	        return name.substring(0, lastPeriodPos);
	    }
	}


}
