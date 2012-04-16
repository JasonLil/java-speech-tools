package speech.dynamic;


import java.io.File;

import config.Config;

public class CreateSerializeDataMain {

	/*
	 * Trains a neural network for a given set of audio data acquired from
	 * a variety of sound sources
	 */

	

		//
		// public static double alpha = 3e6;
		// public static double beta = 1e-9;

		public static void main(String args[]) throws Exception {

			Config config = Config.current();
			File rootSrc = new File("../JavaSpeechToolData/wavfiles/Dynamic");
			File rootDir = new File("/tmp/SPEECH");
			
			rootDir.mkdir();
			
			
			
			
			String words[]={"Bed_","Red_","Bad_","Call_","Did_","Father_","Leg_","Tea_","Wet_"};
			
			double fractTrain=0.7;
			
			WavTrainingPoolToDirectory pool = 
				new WavTrainingPoolToDirectory(rootSrc,rootDir,fractTrain, config,words);
			

		}
			
}
