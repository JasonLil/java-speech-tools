package speech.dynamic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import speech.ReadFeatureVectors;

import config.Config;

//import speech.ReadFeatureVectors;

public class WavTrainingPoolToDirectory {
	private int nOut;
	public List<TrainingData> trainingData;
	ReadFeatureVectors reader;
	ArrayList<String> names;
	public double target[];
	HashSet<String> filt;
	
	WavTrainingPoolToDirectory(File root, Config config,String words[]) {
		names=new ArrayList<String>();
		
		reader = new ReadFeatureVectors(config);
		trainingData = new ArrayList<TrainingData>();
		filt=new HashSet<String>();
		
		for (String s:words){
			filt.add(s);
		}
		
		HashMap<String, List<File>> set = new HashMap<String, List<File>>();

		assert (root.isDirectory());
		
		// Create a map of the files against the key==WORD
		
		for (File dir : root.listFiles()) {
			
			// For each subdirectory
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
		int nOutTot=set.keySet().size();
		
		for (String key : set.keySet()) {
			
			// For each name
			target = new double[nOutTot];

			
			target[nOut] = 1.0;
			
			
			List<File> list = set.get(key);
			names.add(key);
			for (File file : list) {
				try {
					System.out.println(" Loading features: " + file.getPath());
					ArrayList<double[]> featSeq= reader.readVectors(file);
						
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
