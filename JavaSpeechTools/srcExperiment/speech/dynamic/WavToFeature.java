package speech.dynamic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import speech.FeatureVectorReader;
import config.Config;

public class WavToFeature {

	
	private Config config;
	private FeatureVectorReader reader;

	public WavToFeature(Config config){
		this.config=config;
		reader = new FeatureVectorReader(config);
	}
	
	
	public float[] readFile(File file) throws Exception {
		
		ArrayList<double[]> featSeq = reader.readVectors(file);
		
		int size=featSeq.size()*featSeq.get(0).length;
		
		float feat[] = new float[size];

		int cnt = 0;
		for (double[] fa : featSeq) {
			for (double f : fa) {
				feat[cnt++] = (float) f;
			}
		}
		assert (cnt == size);
		return feat;
		
	}
}
